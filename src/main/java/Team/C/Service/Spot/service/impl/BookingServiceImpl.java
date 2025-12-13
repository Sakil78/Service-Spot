package Team.C.Service.Spot.service.impl;

import Team.C.Service.Spot.dto.request.CreateBookingRequest;
import Team.C.Service.Spot.dto.request.UpdateBookingStatusRequest;
import Team.C.Service.Spot.dto.response.BookingResponse;
import Team.C.Service.Spot.mapper.BookingMapper;
import Team.C.Service.Spot.model.Booking;
import Team.C.Service.Spot.model.ServiceListing;
import Team.C.Service.Spot.model.User;
import Team.C.Service.Spot.model.enums.BookingStatus;
import Team.C.Service.Spot.repository.BookingRepository;
import Team.C.Service.Spot.repository.ServiceListingRepository;
import Team.C.Service.Spot.repository.UserRepository;
import Team.C.Service.Spot.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of BookingService interface.
 * Handles all business logic for booking management.
 *
 * @author Team C
 * @version 3.0
 * @since 2025-11-29
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final BookingMapper bookingMapper;
    private final Team.C.Service.Spot.repository.SpecificAvailabilityRepository availabilityRepository;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking for customer ID: {} and service ID: {}",
                request.getCustomerId(), request.getServiceListingId());

        // Validate that booking datetime is at least 30 minutes in the future
        LocalDateTime bookingDateTime = LocalDateTime.of(request.getBookingDate(), request.getBookingTime());
        LocalDateTime minimumBookingTime = LocalDateTime.now().plusMinutes(30);

        if (bookingDateTime.isBefore(minimumBookingTime)) {
            String errorMsg = String.format(
                "Booking time must be at least 30 minutes in the future. Selected: %s, Minimum required: %s",
                bookingDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                minimumBookingTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
            );
            log.warn("Booking rejected - past or too soon: {}", errorMsg);
            throw new IllegalArgumentException("Please choose a future date/time. Bookings must be made at least 30 minutes in advance.");
        }

        // Fetch customer
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + request.getCustomerId()));

        // Fetch service listing
        ServiceListing serviceListing = serviceListingRepository.findById(request.getServiceListingId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service listing not found with ID: " + request.getServiceListingId()));

        // Get provider from service listing
        User provider = serviceListing.getProvider();

        // Create booking entity
        Booking booking = Booking.builder()
                .customer(customer)
                .provider(provider)
                .serviceListing(serviceListing)
                .bookingDate(request.getBookingDate())
                .bookingTime(request.getBookingTime())
                .durationMinutes(request.getDurationMinutes())
                .serviceDoorNo(request.getServiceDoorNo())
                .serviceAddressLine(request.getServiceAddressLine())
                .serviceCity(request.getServiceCity())
                .serviceState(request.getServiceState())
                .servicePincode(request.getServicePincode())
                .totalAmount(serviceListing.getPrice())
                .currency("INR")
                .paymentStatus("Pending")
                .paymentMethod(request.getPaymentMethod())
                .customerNotes(request.getCustomerNotes())
                .status(BookingStatus.PENDING)
                .build();

        // Generate booking reference
        booking.setBookingReference(generateBookingReference());

        // Save booking
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Successfully created booking with ID: {} and reference: {}",
                savedBooking.getId(), savedBooking.getBookingReference());

        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking by ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings for admin view");
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(Long userId, String role) {
        log.info("Fetching bookings for user ID: {} with role: {}", userId, role);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Booking> bookings;
        if ("PROVIDER".equalsIgnoreCase(role)) {
            bookings = bookingRepository.findByProvider(user);
        } else {
            bookings = bookingRepository.findByCustomer(user);
        }

        return bookings.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomer(Long customerId) {
        log.info("Fetching bookings for customer ID: {}", customerId);
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        List<Booking> bookings = bookingRepository.findByCustomer(customer);
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByProvider(Long providerId) {
        log.info("Fetching bookings for provider ID: {}", providerId);
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));

        List<Booking> bookings = bookingRepository.findByProvider(provider);
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByServiceListing(Long serviceListingId) {
        log.info("Fetching bookings for service listing ID: {}", serviceListingId);
        ServiceListing serviceListing = serviceListingRepository.findById(serviceListingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service listing not found with ID: " + serviceListingId));

        List<Booking> bookings = bookingRepository.findByServiceListing(serviceListing);
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByStatus(BookingStatus status) {
        log.info("Fetching bookings with status: {}", status);
        List<Booking> bookings = bookingRepository.findByStatus(status);
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBookingStatus(Long id, UpdateBookingStatusRequest request) {
        try {
            log.info("🔵 Updating booking status for ID: {} to {}", id, request.getStatus());
            log.info("📦 Request details: providerNotes={}, cancellationReason={}, cancelledBy={}",
                request.getProviderNotes(), request.getCancellationReason(), request.getCancelledBy());

            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));

            log.info("📋 Found booking: ID={}, Date={}, Time={}, Status={}",
                booking.getId(), booking.getBookingDate(), booking.getBookingTime(), booking.getStatus());

            // Check if booking date is in the past
            // Add null safety checks
            if (booking.getBookingDate() == null) {
                log.error("❌ Booking date is NULL for booking ID: {}", id);
                throw new IllegalArgumentException("Booking date cannot be null");
            }

            if (booking.getBookingTime() == null) {
                log.error("❌ Booking time is NULL for booking ID: {}", id);
                throw new IllegalArgumentException("Booking time cannot be null");
            }

            LocalDateTime bookingDateTime = LocalDateTime.of(booking.getBookingDate(), booking.getBookingTime());
            LocalDateTime now = LocalDateTime.now();
            boolean isBookingInPast = bookingDateTime.isBefore(now);

            log.info("⏰ Booking DateTime: {}, Now: {}, IsInPast: {}", bookingDateTime, now, isBookingInPast);

            // Handle past bookings differently
            if (isBookingInPast) {
                log.warn("⚠️ Attempting to update past booking (ID: {}, Date: {})", id, booking.getBookingDate());

                // For past bookings, only allow certain status changes
                if (booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.CONFIRMED) {
                    // Auto-expire pending/confirmed bookings that are in the past
                    if (request.getStatus() == BookingStatus.CANCELLED) {
                        log.info("✅ Allowing cancellation of past booking");
                        booking.setStatus(BookingStatus.CANCELLED);
                        booking.setCancelledAt(now);
                        booking.setCancellationReason(request.getCancellationReason() != null ?
                            request.getCancellationReason() : "Cancelled after booking date passed");
                        booking.setCancelledBy(request.getCancelledBy());
                    } else if (request.getStatus() == BookingStatus.REJECTED) {
                        log.info("✅ Allowing rejection of past booking by provider");
                        booking.setStatus(BookingStatus.REJECTED);
                        booking.setCancelledAt(now); // Use cancelled_at for rejected bookings too
                        booking.setCancellationReason(request.getCancellationReason() != null ?
                            request.getCancellationReason() : "Rejected after booking date passed");
                        booking.setCancelledBy(request.getCancelledBy() != null ?
                            request.getCancelledBy() : "PROVIDER");
                    } else if (request.getStatus() == BookingStatus.COMPLETED) {
                        log.info("✅ Marking past booking as completed");
                        booking.setStatus(BookingStatus.COMPLETED);
                        booking.setCompletedAt(now);
                        // Try cleanup but don't fail if it errors
                        cleanupAvailabilityForCompletedBooking(booking);
                    } else {
                        log.error("❌ Invalid status {} for past booking", request.getStatus());
                        throw new IllegalArgumentException(
                            "Cannot change status of past booking to " + request.getStatus() +
                            ". Only CANCELLED, REJECTED, or COMPLETED status allowed for past bookings.");
                    }
                } else {
                    log.error("❌ Cannot modify past booking with status {}", booking.getStatus());
                    throw new IllegalArgumentException(
                        "Booking is in the past with status " + booking.getStatus() +
                        " and cannot be modified further.");
                }
            } else {
                // Normal flow for future bookings
                log.info("📅 Processing future booking");
                BookingStatus oldStatus = booking.getStatus();
                booking.setStatus(request.getStatus());
                booking.setProviderNotes(request.getProviderNotes());

                // Update timestamps based on status
                switch (request.getStatus()) {
                    case CONFIRMED:
                        booking.setConfirmedAt(now);
                        break;
                    case COMPLETED:
                        booking.setCompletedAt(now);
                        // Auto-cleanup: Delete the specific availability slot for this booking
                        log.info("🗑️ Auto-cleanup: Booking completed, removing availability slot for date: {}", booking.getBookingDate());
                        cleanupAvailabilityForCompletedBooking(booking);
                        break;
                    case CANCELLED:
                        booking.setCancelledAt(now);
                        booking.setCancellationReason(request.getCancellationReason());
                        booking.setCancelledBy(request.getCancelledBy());
                        break;
                    case REJECTED:
                        booking.setCancelledAt(now); // Use cancelled_at for rejected bookings
                        booking.setCancellationReason(request.getCancellationReason() != null ?
                            request.getCancellationReason() : "Booking rejected by provider");
                        booking.setCancelledBy(request.getCancelledBy() != null ?
                            request.getCancelledBy() : "PROVIDER");
                        break;
                    default:
                        break;
                }
                log.info("Successfully updated booking status from {} to {}", oldStatus, request.getStatus());
            }

            log.info("💾 Saving booking to database...");
            Booking updatedBooking = bookingRepository.save(booking);
            log.info("✅ Booking saved successfully with status: {}", updatedBooking.getStatus());

            BookingResponse response = bookingMapper.toResponse(updatedBooking);
            log.info("✅ Returning response");
            return response;

        } catch (Exception e) {
            log.error("💥 ERROR in updateBookingStatus: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BookingResponse cancelBooking(Long id, String cancelledBy, String reason) {
        log.info("Cancelling booking ID: {} by {}", id, cancelledBy);

        UpdateBookingStatusRequest request = UpdateBookingStatusRequest.builder()
                .status(BookingStatus.CANCELLED)
                .cancelledBy(cancelledBy)
                .cancellationReason(reason)
                .build();

        return updateBookingStatus(id, request);
    }

    @Override
    public void deleteBooking(Long id) {
        log.info("Deleting booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));

        bookingRepository.delete(booking);
        log.info("Successfully deleted booking with ID: {}", id);
    }

    /**
     * Generate a unique booking reference number
     * Format: BK-YYYY-NNNNNN
     */
    private String generateBookingReference() {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long count = bookingRepository.count() + 1;
        return String.format("BK-%s-%06d", year, count);
    }

    /**
     * Clean up availability slot when booking is completed
     * This prevents the same slot from appearing as available after service is done
     * Enhanced with better null safety and error handling
     */
    private void cleanupAvailabilityForCompletedBooking(Booking booking) {
        if (booking == null || booking.getProvider() == null || booking.getBookingDate() == null) {
            log.warn("⚠️ Cannot cleanup availability: booking or required fields are null");
            return;
        }

        try {
            log.info("🔍 Searching for availability slots to cleanup for provider {} on date {}",
                    booking.getProvider().getId(), booking.getBookingDate());

            // Find and delete the specific availability for this booking's date and provider
            var allAvailabilities = availabilityRepository.findAll();
            log.info("📊 Total availability slots in database: {}", allAvailabilities.size());

            var availabilities = allAvailabilities.stream()
                    .filter(a -> a.getProvider() != null &&
                               a.getProvider().getId() != null &&
                               a.getProvider().getId().equals(booking.getProvider().getId()))
                    .filter(a -> a.getAvailableDate() != null &&
                               a.getAvailableDate().equals(booking.getBookingDate()))
                    .filter(a -> {
                        // Match the time slot with null safety
                        if (a.getStartTime() == null || booking.getBookingTime() == null) {
                            return false;
                        }
                        var bookingTime = booking.getBookingTime();
                        boolean matches = a.getStartTime().equals(bookingTime);

                        // Also check if booking time falls within slot range
                        if (!matches && a.getEndTime() != null) {
                            matches = bookingTime.isAfter(a.getStartTime()) &&
                                    bookingTime.isBefore(a.getEndTime());
                        }
                        return matches;
                    })
                    .toList();

            if (!availabilities.isEmpty()) {
                log.info("🗑️ Found {} availability slot(s) to delete", availabilities.size());
                availabilityRepository.deleteAll(availabilities);
                log.info("✅ Successfully deleted {} availability slot(s) for completed booking on {}",
                        availabilities.size(), booking.getBookingDate());
            } else {
                log.info("ℹ️ No availability slots found to clean up for date: {} (slot may have been already deleted)",
                        booking.getBookingDate());
            }
        } catch (Exception e) {
            log.error("❌ Error cleaning up availability for completed booking: {}", e.getMessage(), e);
            // Don't fail the booking update if cleanup fails - this is a non-critical operation
        }
    }
}

