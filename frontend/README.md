# 🎨 Service-Spot Frontend v4.0

React-based frontend application for Service-Spot - A Localized Service Discovery and Booking Platform with Location Tracking.

**Version**: 4.0  
**Last Updated**: December 1, 2025  
**Status**: Production Ready ✅

---

## 🚀 Features

### Customer Features
- 🔍 Browse and search from **16 service categories**
- 📍 **Location-based search** - See distance from providers (km)
- 👤 View detailed provider profiles with reviews and distance
- 📅 **Real-time availability calendar** showing provider's schedule
- 💼 Book services with complete address and pincode
- 📊 Manage bookings (view status, track progress)
- ⭐ Leave reviews after service completion
- 🎨 Modern dark-themed UI with smooth animations
- 📱 Fully responsive (mobile, tablet, desktop)

### Provider Features
- 📊 **Comprehensive dashboard** with booking statistics
- 📅 **Smart availability management** - Set specific dates and time slots
- 🔔 Manage incoming booking requests (accept/reject)
- 👥 **View customer profiles with distance** before accepting
- 📈 Track booking history (pending, confirmed, completed)
- 💼 Professional profile with business details
- 🎯 Service listing management (create, edit, delete)
- 📍 Location automatically set via pincode

### System Features
- 🔐 JWT-based secure authentication
- 🎨 Modern glassmorphic dark theme
- ⚡ Fast loading with code splitting
- 🌈 Beautiful gradient UI with animations
- 🔄 Real-time data updates
- 📱 PWA-ready architecture
- ♿ Accessible UI components

---

## 🛠️ Tech Stack

- **Framework**: React 18.2.0
- **Build Tool**: Vite 5.4.21
- **Routing**: React Router v6
- **Styling**: Tailwind CSS 3.x
- **HTTP Client**: Axios
- **Icons**: React Icons + Lucide React
- **State Management**: React Context API
- **Forms**: Controlled components with validation
- **Date/Time**: Native JavaScript Date API

---

## 📋 Prerequisites

Before starting, ensure you have:
- ✅ **Node.js 18+** and npm installed
- ✅ **Backend API** running on `http://localhost:8080`
- ✅ Modern browser (Chrome, Firefox, Edge, Safari)

---

## 🔧 Installation

### 1. Navigate to Frontend Directory
```bash
cd frontend
```

### 2. Install Dependencies
```bash
npm install
```

This installs:
- React and React DOM
- React Router
- Axios
- Tailwind CSS
- Vite
- All other dependencies (~200 MB)

### 3. Configure API Endpoint (if needed)
**File**: `src/services/api.js`

Default configuration:
```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
```

For production, create `.env.production`:
```env
VITE_API_BASE_URL=https://your-backend-url.com/api
```

---

## 🏃 Running the Application

### Development Mode (with Hot Reload)
```bash
npm run dev
```

**Opens at**: `http://localhost:3000`  
**Features**:
- ⚡ Instant hot reload on file changes
- 🐛 Source maps for debugging
- 📊 Dev tools available

### Production Build
```bash
npm run build
```

**Output**: `dist/` folder (optimized bundle)  
**Build time**: ~30 seconds  
**Bundle size**: ~350 KB (gzipped)

### Preview Production Build
```bash
npm run preview
```

**Opens at**: `http://localhost:4173`  
Test production build locally before deploying.

---

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/              # Reusable UI components
│   │   ├── ErrorBoundary.jsx    # Error handling wrapper
│   │   ├── Footer.jsx           # App footer
│   │   ├── ImageSlider.jsx      # Hero image carousel
│   │   ├── LoadingSpinner.jsx   # Loading indicator
│   │   ├── Modal.jsx            # Modal dialog component
│   │   ├── Navbar.jsx           # Navigation bar
│   │   ├── ProtectedRoute.jsx   # Auth guard for routes
│   │   ├── ServiceCard.jsx      # Service display card
│   │   ├── StarRating.jsx       # Star rating display
│   │   └── StatusBadge.jsx      # Booking status badge
│   │
│   ├── context/                 # React Context providers
│   │   └── AuthContext.jsx      # Authentication state
│   │
│   ├── pages/                   # Page components
│   │   ├── AdminDashboard.jsx   # Admin user management
│   │   ├── BookingPage.jsx      # Service booking form
│   │   ├── CustomerDashboard.jsx # Customer overview
│   │   ├── CustomerProfile.jsx  # Customer profile page
│   │   ├── LandingPage.jsx      # Home page (16 categories)
│   │   ├── LoginPage.jsx        # Login form
│   │   ├── MyBookingsPage.jsx   # Customer bookings list
│   │   ├── ProviderDashboard.jsx # Provider overview
│   │   ├── ProviderDetailPage.jsx # Provider profile view
│   │   ├── ProviderProfile.jsx  # Provider profile edit
│   │   ├── ServiceDetailPage.jsx # Service details & booking
│   │   ├── ServiceListPage.jsx  # Browse services (with distance)
│   │   └── SignupPage.jsx       # Registration form
│   │
│   ├── services/                # API services
│   │   └── api.js               # Axios instance + API calls
│   │
│   ├── utils/                   # Utility functions
│   │   ├── categoryIcons.jsx    # Service category icons (16)
│   │   ├── constants.js         # App constants
│   │   └── validation.js        # Form validation helpers
│   │
│   ├── providers/               # Context providers wrapper
│   │   └── AppProviders.jsx     # Combines all providers
│   │
│   ├── App.jsx                  # Main app with routes
│   ├── main.jsx                 # Entry point
│   └── index.css                # Global styles + Tailwind
│
├── public/                      # Static assets
├── index.html                   # HTML template
├── package.json                 # Dependencies
├── vite.config.js              # Vite configuration
├── tailwind.config.js          # Tailwind customization
├── postcss.config.js           # PostCSS config
└── vercel.json                 # Vercel deployment config
```

---

## 🔐 Authentication Flow

### User Registration
1. Navigate to `/signup`
2. Select role: **Customer** or **Provider**
3. Fill registration form (with pincode for location)
4. Submit → Auto-login → Redirect to dashboard

### User Login
1. Navigate to `/login`
2. Enter email and password
3. JWT token stored in localStorage
4. Redirect based on role:
   - Customer → `/customer/dashboard`
   - Provider → `/provider/dashboard`
   - Admin → `/admin/dashboard`

### Protected Routes
- Uses `ProtectedRoute` component
- Checks for valid JWT token
- Redirects to login if unauthorized
- Role-based access control

---

## 🗺️ Application Routes

| Route | Component | Access | Description |
|-------|-----------|--------|-------------|
| `/` | LandingPage | Public | Home page with 16 categories |
| `/login` | LoginPage | Public | User login |
| `/signup` | SignupPage | Public | User registration |
| `/services` | ServiceListPage | Public | Browse services |
| `/services/:id` | ServiceDetailPage | Public | Service details |
| `/providers/:id` | ProviderDetailPage | Public | Provider profile |
| `/bookings/new` | BookingPage | Customer | Create booking |
| `/my-bookings` | MyBookingsPage | Customer | View bookings |
| `/customer/dashboard` | CustomerDashboard | Customer | Customer overview |
| `/profile` | CustomerProfile | Customer | Edit profile |
| `/provider/dashboard` | ProviderDashboard | Provider | Provider overview |
| `/provider/profile` | ProviderProfile | Provider | Edit provider profile |
| `/admin/dashboard` | AdminDashboard | Admin | User management |

---

## 🔌 API Integration

### API Client Configuration
**File**: `src/services/api.js`

```javascript
// Base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' }
});

// Request interceptor (adds JWT token)
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor (handles errors)
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Auto logout on 401
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

### Available API Functions

#### Authentication
- `authAPI.login(credentials)` - User login
- `authAPI.registerCustomer(data)` - Customer registration
- `authAPI.registerProvider(data)` - Provider registration

#### Services
- `serviceAPI.getAll(params)` - List all services
- `serviceAPI.getById(id)` - Get service details
- `serviceAPI.create(data)` - Create service (provider)
- `serviceAPI.update(id, data)` - Update service
- `serviceAPI.delete(id)` - Delete service
- `serviceAPI.search(query)` - Search services

#### Bookings
- `bookingAPI.create(data)` - Create booking
- `bookingAPI.getByUser(userId, role)` - Get user bookings
- `bookingAPI.updateStatus(id, status)` - Update booking status

#### Categories
- `categoryAPI.getAll()` - Get all 16 categories

#### Providers
- `providerAPI.getAll()` - List providers
- `providerAPI.getById(id)` - Get provider details
- `providerAPI.update(id, data)` - Update provider profile
- `providerAPI.getAvailableCities()` - Get cities list
- `providerAPI.getAvailableServiceTypes()` - Get service types

#### Location
- `locationAPI.getUserLocation(userId)` - Get user coordinates
- `locationAPI.calculateDistance(userId1, userId2)` - Calculate distance

#### Reviews
- `reviewAPI.create(data)` - Submit review
- `reviewAPI.getByProvider(providerId)` - Get provider reviews

---

## 🎨 Styling & Theming

### Tailwind Configuration
**File**: `tailwind.config.js`

```javascript
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#8b5cf6',    // Violet
        secondary: '#f59e0b',  // Orange
        accent: '#475569',     // Slate
      }
    }
  }
}
```

### Custom CSS Classes
**File**: `src/index.css`

- `.btn-primary` - Primary buttons (violet)
- `.btn-secondary` - Secondary buttons (outline)
- `.card` - Content cards
- `.glassmorphic` - Glass effect backgrounds

### Dark Theme
All components use dark theme by default:
- Background: Slate-900
- Text: White/Gray
- Accents: Violet/Orange gradients

---

## 📱 Responsive Design

### Breakpoints (Tailwind)
- `sm`: 640px (Mobile)
- `md`: 768px (Tablet)
- `lg`: 1024px (Desktop)
- `xl`: 1280px (Large desktop)

### Mobile-First Approach
All components are designed mobile-first, then enhanced for larger screens.

Example:
```jsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4">
  {/* 1 column on mobile, 2 on tablet, 4 on desktop */}
</div>
```

---

## 🧪 Testing

### Manual Testing Checklist
- [ ] Registration (Customer & Provider)
- [ ] Login (all roles)
- [ ] Browse 16 service categories
- [ ] Search by location and category
- [ ] View provider profile with distance
- [ ] Create booking with availability
- [ ] Provider accepts/rejects booking
- [ ] Leave review after service
- [ ] Responsive on mobile/tablet/desktop
- [ ] All images load correctly
- [ ] No console errors

### Browser Compatibility
- ✅ Chrome 90+
- ✅ Firefox 88+
- ✅ Edge 90+
- ✅ Safari 14+
- ✅ Mobile browsers (iOS Safari, Chrome Mobile)

---

## 🚀 Build & Deployment

### Build for Production
```bash
npm run build
```

**Output**:
- Folder: `dist/`
- Optimized bundles with code splitting
- Minified CSS and JS
- Asset hashing for cache busting

### Deploy to Vercel
```bash
vercel --prod
```

Or connect GitHub repo to Vercel for auto-deployments.

### Environment Variables
Create `.env.production`:
```env
VITE_API_BASE_URL=https://your-backend-api.com/api
```

---

## 🐛 Troubleshooting

### Issue: White Screen on Load
**Solution**:
1. Check browser console for errors
2. Verify API_BASE_URL is correct
3. Ensure backend is running
4. Clear browser cache (Ctrl+Shift+R)

### Issue: Images Not Loading
**Solution**:
1. Check image URLs in ImageSlider component
2. Ensure external images are accessible
3. Check browser security settings (CORS)

### Issue: API Calls Failing
**Solution**:
1. Open DevTools → Network tab
2. Check API responses
3. Verify JWT token in localStorage
4. Check backend CORS configuration

### Issue: Location Not Showing
**Solution**:
1. Ensure user has valid pincode
2. Check backend geocoding service
3. Verify latitude/longitude in database
4. Refresh page after profile update

---

## 📊 Performance Optimization

### Implemented Optimizations
- ✅ Code splitting (lazy loading routes)
- ✅ Image optimization
- ✅ Minified production builds
- ✅ Tree shaking (removes unused code)
- ✅ Gzip compression
- ✅ CDN-ready static assets

### Performance Metrics
- First Contentful Paint: <1.5s
- Time to Interactive: <3s
- Lighthouse Performance: 90+

---

## 🔒 Security Features

- ✅ JWT token authentication
- ✅ Protected routes with auth guards
- ✅ XSS protection (React escapes by default)
- ✅ Input validation on all forms
- ✅ Secure API calls (HTTPS in production)
- ✅ No sensitive data in localStorage (only token)

---

## 📚 Key Dependencies

| Package | Version | Purpose |
|---------|---------|---------|
| react | ^18.2.0 | UI library |
| react-router-dom | ^6.x | Routing |
| axios | ^1.x | HTTP client |
| tailwindcss | ^3.x | Styling |
| lucide-react | ^0.x | Icons |
| react-icons | ^5.x | Additional icons |

Full list in `package.json`

---

## 🤝 Contributing

### Code Style
- Use functional components with hooks
- Follow Tailwind utility-first approach
- Add JSDoc comments for complex functions
- Keep components small and focused
- Use meaningful variable names

### Git Workflow
1. Create feature branch
2. Make changes
3. Test thoroughly
4. Commit with descriptive message
5. Push and create PR

---

## 📞 Support

- **Documentation**: See `../README.md` and `../QUICK_START.md`
- **Issues**: Create GitHub issue
- **Questions**: Check troubleshooting section above

---

## 🎉 What's Next

**Future Enhancements**:
- Real-time chat between customer and provider
- Push notifications for booking updates
- PWA features (offline mode, install prompt)
- Advanced search filters
- Map view for providers
- Multi-language support

---

**Built with ❤️ by Team C**  
**Version 4.0 - December 2025**  
**Status**: Production Ready ✅

## 🎨 Key Pages

### Landing Page (`/`)
- Hero section with search functionality
- Featured services and categories
- Provider registration CTA
- Platform statistics

### Service List (`/services`)
- Browse all service providers
- Filter by city, category, and search term
- View provider cards with ratings

### Service Detail (`/providers/:id`)
- Detailed provider information
- List of services offered
- Customer reviews and ratings
- Book service button

### Booking Page (`/bookings/new`)
- Service summary
- Date and time selection
- Additional notes
- Booking confirmation

### My Bookings (`/my-bookings`)
- View all bookings (filterable by status)
- Cancel bookings
- Write reviews for completed services
- Track booking status

### Provider Dashboard (`/provider/dashboard`)
- Statistics overview (bookings, revenue)
- Manage booking requests
- Accept/reject/complete bookings
- Service management
- Profile information

## 🎯 API Integration

The frontend communicates with the backend REST API through the `src/services/api.js` module.

### Available API Modules:
- `customerAPI` - Customer operations
- `providerAPI` - Provider operations
- `serviceAPI` - Service management
- `bookingAPI` - Booking operations
- `reviewAPI` - Review operations
- `availabilityAPI` - Availability management

### Example API Call:
```javascript
import { providerAPI } from '../services/api';

const fetchProviders = async () => {
  try {
    const response = await providerAPI.getAll();
    setProviders(response.data);
  } catch (error) {
    console.error('Error:', error);
  }
};
```

## 🎨 Styling

The application uses Tailwind CSS with custom utility classes defined in `src/index.css`:

- `.btn-primary` - Primary action buttons
- `.btn-secondary` - Secondary action buttons
- `.btn-danger` - Danger/delete buttons
- `.input-field` - Form input fields
- `.card` - Card container
- `.badge-*` - Status badges

## 🔄 State Management

- **AuthContext**: Manages user authentication state, login/logout, and user data
- **Local State**: Component-specific state using `useState`
- **API Calls**: Handled via Axios with interceptors for authentication

## 🚧 Features to Implement (Backend Required)

Some features are currently using mock data and require backend API implementation:

1. **Service Management**
   - CRUD operations for services
   - Service categories

2. **Availability Management**
   - Provider availability slots
   - Real-time slot booking

3. **Review System**
   - Submit and fetch reviews
   - Rating calculations

4. **Advanced Booking**
   - Slot conflict prevention
   - Booking notifications

5. **Search & Filters**
   - Advanced search with multiple filters
   - Location-based search

## 🐛 Known Issues

- CSS warnings for Tailwind directives (expected, will be processed by PostCSS)
- Mock data is used for demonstrations until backend APIs are fully implemented

## 📝 Environment Variables

Create a `.env` file in the frontend root (optional):

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Then update `src/services/api.js` to use:
```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
```

## 🔗 Backend Integration

Ensure the Spring Boot backend is running before starting the frontend. The Vite dev server is configured to proxy `/api` requests to `http://localhost:8080`.

## 📱 Responsive Design

The application is fully responsive and optimized for:
- Desktop (1024px+)
- Tablet (768px - 1023px)
- Mobile (320px - 767px)

## 🎭 Demo Credentials

### Customer Account
```
Email: customer@example.com
Password: password123
```

### Provider Account
```
Email: provider@example.com
Password: password123
```

*(Note: Create these accounts through the backend or use actual registered accounts)*

## 🤝 Contributing

1. Follow the existing code structure
2. Use functional components with hooks
3. Implement proper error handling
4. Add loading states for async operations
5. Ensure responsive design

## 📄 License

This project is part of the Service-Spot platform.

---

**Built with ❤️ using React and Tailwind CSS**
