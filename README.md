# 🛠️ Service-Spot v4.1.1

> A Localized Service Discovery and Booking Platform connecting customers with trusted service providers.

**Latest Updates (v4.1.1 - December 26, 2025):**
- 🔧 **FIXED**: Service deletion now works perfectly with cascade delete
  - Providers can delete services without foreign key errors
  - Automatically removes related bookings, reviews, and availability slots
  - Clean database cleanup with proper cascade relationships
- ✅ **ENHANCED**: Database integrity with proper entity relationships

**Previous Updates (v4.1.0 - December 20, 2025):**
- 📧 **NEW**: Email Service Integration (Gmail SMTP - FREE!)
  - Password reset with 6-digit verification codes
  - Professional HTML email templates
  - Email notifications for bookings
  - 100% FREE (500 emails/day limit)
  - Works with ANY email provider for recipients (Gmail, Yahoo, Outlook, etc.)
- 🔐 **ENHANCED**: Forgot Password feature with email delivery
- ✅ **IMPROVED**: Better error handling for email authentication
- 📚 **DOCUMENTED**: Complete email setup guide included

**Previous Updates (v4.0.3 - December 15, 2025):**
- 🔧 **FIXED**: Popular Businesses now shows ALL unique categories from database
- ✨ **ENHANCED**: Multi-category providers display correctly (e.g., Beauty + Gardening both show)
- 🎨 **MAINTAINED**: Category card design with icons (no detail overload)
- 🚀 **DATABASE-DRIVEN**: Categories extracted from actual services, not hardcoded

**Previous Updates (v4.0.2 - December 12, 2025):**
- 🔧 **FIXED**: Past time slots are now hidden when booking for today (30-min buffer)
- 🔧 **FIXED**: Time display shows correctly (3:00 PM displays as 3:00 PM, not 9:00 PM)
- 🔧 **FIXED**: Providers can now set availability for today (not just future dates)
- ✅ **IMPROVED**: Zero console warnings - React 19 & Router v7 ready
- 📝 **ENHANCED**: Better error messages and validation
- 🎨 **UPDATED**: All defaultProps removed, modern JavaScript patterns

**Previous Updates (v4.0.1 - December 9, 2025):**
- 🔧 Fixed past bookings cancellation/completion
- ⚠️ Enhanced error handling for expired bookings
- 📝 Improved booking status validation logic

**Previous Updates (v4.0):**
- ✨ Enhanced booking page with colorful gradient UI
- 📍 Location tracking with pincode-based distance calculation
- 🎨 Glassmorphism design throughout

---

## 📖 Table of Contents

- [About](#about)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Data Storage](#data-storage)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)

---

## 🎯 About

**Service-Spot** is a full-stack web application that enables:
- **Customers**: Browse, search, and book local services
- **Service Providers**: List services, manage bookings, and build their reputation
- **Categories**: Education, Plumbing, Electrical, Cleaning, Beauty, IT Support, and more

---

## ✨ Features

### For Customers
- 🔍 Search services by location and category
- 📅 Interactive calendar with color-coded availability
- ⏰ **NEW!** Smart time slot filtering (only shows future slots for today)
- 💳 Secure booking system with complete address collection
- ⭐ Review and rate providers
- 📱 Responsive mobile-friendly UI
- 🎨 Beautiful gradient UI with smooth animations
- 📍 Location tracking for service location collection
- ✅ **NEW!** 30-minute booking buffer for realistic scheduling

### For Service Providers
- 📝 Create and manage service listings
- 🗑️ **Delete services easily** (no foreign key errors!)
- 📊 Dashboard with booking overview
- 💼 Profile management
- 📍 Location-based service radius
- 🔔 Booking notifications
- ⏰ Set availability schedules (including today!)
- 📍 Track customer locations
- 🕐 **NEW!** Accurate time display in all views
- ✅ **NEW!** Can set same-day availability

### System Features
- 🔐 JWT-based authentication with secure token management
- 👥 Role-based access control (Customer/Provider/Admin)
- 🎨 Modern professional-themed UI with glassmorphism effects
- 📍 **Pincode-based Location Services** with real-time distance calculation
- 🗺️ Haversine formula for accurate distance measurement
- 🌐 RESTful API architecture with proper error handling
- 📅 Provider-controlled availability scheduling (specific dates/times)
- 🌈 Color-coded UI with smooth animations and transitions
- 🔒 Secure password hashing and validation
- 📊 Admin dashboard for user management
- ♻️ Automatic geocoding for Indian pincodes
- ⚡ **NEW!** React 19 compatible - zero console warnings
- 🚀 **NEW!** React Router v7 ready with future flags
- 🎯 **NEW!** Enhanced validation with clear error messages
- 📧 **NEW!** Email Service (Gmail SMTP - FREE!)
  - Password reset with 6-digit codes
  - Professional HTML email templates
  - 500 emails/day limit (FREE forever)
  - Works with ANY email provider for recipients

### 16 Service Categories
1. **Education** - Tutoring and educational services
2. **Plumbing** - Pipe repairs and installations
3. **Electrical** - Wiring, repairs, and installations
4. **Cleaning** - Home and office cleaning services
5. **Beauty** - Salon and wellness services
6. **IT Support** - Computer and tech support
7. **Home Repair** - General home maintenance
8. **Health** - Medical and healthcare services
9. **Carpentry** - Furniture and woodwork
10. **Painting** - Interior and exterior painting
11. **Appliance Repair** - Fix household appliances
12. **Pest Control** - Pest management services
13. **Moving & Delivery** - Transportation services
14. **Gardening** - Lawn care and landscaping
15. **HVAC** - Heating and air conditioning
16. **Automotive** - Car repair and maintenance

---

## 🚀 Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Database**: MySQL 8.x
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security (JWT)
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **HTTP Client**: Axios
- **State Management**: React Context API
- **Routing**: React Router v6

---

## ⚡ Quick Start

### Prerequisites

- Java 21 JDK
- Node.js 18+ and npm
- MySQL 8.0+
- Git

### 1️⃣ Clone Repository

```bash
git clone <your-repo-url>
cd service-spotV4
```

### 2️⃣ Setup Database

1. Start MySQL Server
2. Create database:
   ```sql
   CREATE DATABASE service_spot;
   ```

### 3️⃣ Configure Database & Email Credentials

**IMPORTANT: Never commit credentials to Git!**

1. **Edit `src/main/resources/application.properties`:**
   ```properties
   # Database Configuration
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   
   # Email Configuration (for password reset)
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-16-char-app-password
   email.from.address=your-email@gmail.com
   ```

2. **Get Gmail App Password** (for email features):
   - Go to: https://myaccount.google.com/security
   - Enable 2-Factor Authentication
   - Go to: https://myaccount.google.com/apppasswords
   - Create app password for "ServiceSpot"
   - Copy 16-character password (remove spaces)
   - Add to `application.properties`

**Why application.properties?**
- ✅ Simple and standard Spring Boot approach
- ✅ Works out-of-the-box without extra configuration
- ✅ Protected by `.gitignore` (won't be committed)
- ✅ Easy to configure per environment

⚠️ **Note:** `application.properties` is in `.gitignore` to protect your credentials.
Users cloning the repo should copy `application.properties.example` to `application.properties` and fill in their own credentials.

📚 **Full security guide:** See `SECURITY.md`

### 4️⃣ Start Backend

**Option A: Using provided script**
```bash
./start-backend.bat
```

**Option B: Manual**
```bash
mvn clean install
mvn spring-boot:run
```

Backend runs at: `http://localhost:8080`

### 5️⃣ Start Frontend

**Option A: Using provided script**
```bash
./start-frontend.bat
```

**Option B: Manual**
```bash
cd frontend
npm install
npm run dev
```

Frontend runs at: `http://localhost:3000`

### 6️⃣ Access Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **API Docs**: http://localhost:8080/swagger-ui.html (if enabled)

### 7️⃣ Login Credentials

**Default Admin Account:**
- **Email**: `admin@servicespot.com`
- **Password**: `Admin@123` (development only - change for production!)
- **Login URL**: `http://localhost:3000/login?type=admin`

**Important Security Notes:**
- ⚠️ Admin account is pre-created but **requires manual login**
- ⚠️ Admin cannot register via signup page (security measure)
- ⚠️ Use Incognito mode for testing to avoid cached sessions
- ⚠️ Always logout after admin tasks on shared computers
- ⚠️ JWT tokens expire after 24 hours for security

**Test Accounts:**
- Customers and service providers can be created through the signup page
- Only customers and providers can self-register
- Admin accounts cannot be created via signup (security measure)

📖 **For more details, see:** `ADMIN_CREDENTIALS.md` and `ADMIN_AUTO_LOGIN_ANALYSIS.md`

---

## 📁 Project Structure

```
service-spotV4/
├── frontend/                   # React frontend application
│   ├── src/
│   │   ├── components/        # Reusable UI components
│   │   ├── pages/            # Page components (incl. enhanced BookingPage)
│   │   ├── context/          # React Context (Auth)
│   │   ├── services/         # API service layer
│   │   └── utils/            # Utility functions
│   └── package.json
│
├── src/main/java/Team/C/Service/Spot/
│   ├── config/               # Spring configurations
│   ├── controller/           # REST API controllers
│   ├── dto/                  # Data Transfer Objects
│   │   ├── request/         # API request DTOs
│   │   └── response/        # API response DTOs
│   ├── mapper/              # Entity-DTO mappers
│   ├── model/               # JPA entities
│   ├── repository/          # Data access layer
│   └── service/             # Business logic layer
│
├── src/main/resources/
│   ├── application.properties  # App configuration
│   └── data.sql               # Initial data (categories)
│
├── start-backend.bat          # Backend startup script
├── start-frontend.bat         # Frontend startup script
├── DEPLOYMENT_GUIDE.md        # Detailed deployment guide
└── pom.xml                    # Maven dependencies
```

---

## 💾 Data Storage

### Understanding Data Persistence

```
┌──────────────────┐
│ MySQL Workbench  │  ← GUI Tool (just for viewing)
└────────┬─────────┘
         │ connects to
┌────────▼─────────┐
│  MySQL Server    │  ← Actual database engine
│  localhost:3306  │     (stores your data)
└──────────────────┘
```

**Important**: MySQL Workbench is just a viewer. Your data is stored in the MySQL Server.

### Initial Data

When you start the backend:

1. **Schema Creation**: Hibernate creates tables from `@Entity` classes
2. **Data Initialization**: `data.sql` inserts default categories
3. **User Data**: Accumulated through API endpoints

### Production Data

When deployed:
- Use cloud MySQL (Railway, AWS RDS, PlanetScale, etc.)
- Same data structure, different location
- See `DEPLOYMENT_GUIDE.md` for details

---

## 🌐 Deployment

### Quick Deploy with Railway (Recommended)

1. Push code to GitHub
2. Sign up at https://railway.app
3. New Project → Deploy from GitHub
4. Add MySQL from marketplace
5. Deploy! (Railway auto-configures)

### Other Options

- **Heroku** - Backend + JawsDB MySQL
- **AWS** - Elastic Beanstalk + RDS
- **DigitalOcean** - App Platform + Managed Database
- **Vercel/Netlify** - Frontend only

📖 **Full deployment guide**: See `DEPLOYMENT_GUIDE.md`

---

## 🔑 API Endpoints

### Authentication
- `POST /api/auth/register/customer` - Register customer
- `POST /api/auth/register/provider` - Register provider
- `POST /api/auth/login` - Login
- `POST /api/auth/forgot-password` - Request password reset (sends email)
- `POST /api/auth/verify-reset-token` - Verify reset code
- `POST /api/auth/reset-password` - Reset password with code

### Services
- `GET /api/services` - List all services
- `GET /api/services/{id}` - Get service details
- `POST /api/services` - Create service (provider)
- `PUT /api/services/{id}` - Update service (provider)
- `GET /api/services/search` - Search services

### Categories
- `GET /api/categories` - List all categories
- `GET /api/categories/{id}` - Get category details

### Bookings
- `POST /api/bookings` - Create booking (customer)
- `GET /api/bookings/user/{userId}` - Get user bookings
- `PATCH /api/bookings/{id}/status` - Update booking status

### Providers
- `GET /api/providers` - List providers
- `GET /api/providers/{id}` - Get provider details
- `PUT /api/providers/{id}` - Update provider profile

---

## 🐛 Troubleshooting

### Backend Issues

**Issue**: `Field 'created_at' doesn't have a default value`  
**Solution**: ✅ Fixed in latest version with proper @CreatedDate annotations

**Issue**: Cannot connect to database  
**Solution**: 
1. Ensure MySQL Server is running (not just MySQL Workbench)
2. Verify credentials in `application.properties`
3. Check database `service_spot` exists: `CREATE DATABASE service_spot;`
4. Test connection: `mysql -u root -p`

**Issue**: Location tracking not working  
**Solution**:
1. Ensure user has valid pincode in profile
2. Check geocoding service is accessible
3. Verify latitude/longitude columns exist in users table
4. Run: `ALTER TABLE users ADD COLUMN latitude DOUBLE, ADD COLUMN longitude DOUBLE;`

**Issue**: Provider can't set availability for today  
**Solution**: ✅ Fixed in v4.0.2 - Providers can now set same-day availability

**Issue**: Booking time shows incorrectly (e.g., 3 PM shows as 9 PM)  
**Solution**: ✅ Fixed in v4.0.2 - Time display now accurate across all views

### Frontend Issues

**Issue**: Network Error when calling API  
**Solution**: 
1. Backend must be running on port 8080
2. Check CORS configuration in SecurityConfig.java
3. Verify API base URL: `http://localhost:8080/api`
4. Clear browser cache and hard refresh (Ctrl+Shift+R)

**Issue**: Categories not showing (less than 16)  
**Solution**: 
1. Check `data.sql` has all 16 categories
2. Restart backend: `mvn spring-boot:run`
3. Verify API: `http://localhost:8080/api/categories`
4. Clear browser cache

**Issue**: Provider availability not updating  
**Solution**: 
1. Check specific_availability table exists
2. Provider must set availability dates through dashboard
3. Hard refresh booking page after setting availability

**Issue**: Can select past time slots when booking  
**Solution**: ✅ Fixed in v4.0.2 - Past slots automatically hidden with 30-min buffer

**Issue**: Console shows React Router or defaultProps warnings  
**Solution**: ✅ Fixed in v4.0.2 - All warnings eliminated, React 19 ready

---

## 📚 Documentation Files

- `PRODUCTION_STATUS.md` - Production readiness checklist & recent fixes
- `DEPLOYMENT_GUIDE_FREE.md` - Free deployment options
- `QUICK_START.md` - Quick setup guide
- `PAST_TIME_SLOT_FIX.md` - Past time slot booking fix details
- `TIME_DISPLAY_BUG_FIXED.md` - Time display correction details
- `ALL_FIXES_COMPLETED.md` - Comprehensive fix summary
- `OPTIONAL_IMPROVEMENTS_COMPLETED.md` - Code quality improvements
- `TEST_PAST_TIME_SLOT_FIX.md` - Testing guide for recent fixes
- `frontend/SETUP.md` - Frontend setup details

---

## 🧪 Testing

### Backend Tests
```bash
mvn clean test
```

### Frontend Development
```bash
cd frontend
npm run dev  # Development server with hot reload
npm run build  # Production build
npm run preview  # Preview production build
```

### Manual Testing Checklist
- [ ] Customer registration and login
- [ ] Provider registration and login
- [ ] Admin login with default credentials
- [ ] Browse 16 service categories
- [ ] Search by location (pincode) and category
- [ ] Provider creates service listing
- [ ] Provider sets availability schedule
- [ ] Customer views provider profile with distance
- [ ] Customer books service
- [ ] Provider views and accepts booking
- [ ] Customer leaves review after service
- [ ] Location distance calculation accuracy

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

**Code Standards:**
- Follow Java naming conventions for backend
- Use functional components and hooks for React
- Add JSDoc comments for complex functions
- Write meaningful commit messages
- Test before submitting PR

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Team

**Team C** - Software Engineering Project  
**Version**: 4.0.3  
**Last Updated**: December 15, 2025  
**Status**: Production Ready ✅

---

## ✨ Recent Improvements (v4.0.3)

### Latest Fixes (v4.0.3 - December 15)
- ✅ **Popular Businesses Enhancement**: Shows ALL unique categories from database
- ✅ **Multi-Category Display**: Providers with multiple category services show all categories
- ✅ **Database-Driven**: Categories extracted from actual service listings
- ✅ **Original Design**: Maintains category card design (icon + name)

### Previous Fixes (v4.0.2 - December 12)

### Bug Fixes
- ✅ **Past Time Slots**: Can't select past times when booking for today
- ✅ **Time Display**: Shows correct time (3 PM displays as 3 PM, not 9 PM)
- ✅ **Same-Day Availability**: Providers can set today's availability
- ✅ **Booking Buffer**: 30-minute minimum advance booking enforced

### Code Quality
- ✅ **Zero Warnings**: Clean console output for better DX
- ✅ **React 19 Ready**: All defaultProps removed
- ✅ **Router v7 Ready**: Future flags added
- ✅ **Modern Patterns**: JavaScript default parameters
- ✅ **Enhanced Validation**: Clear error messages

### Documentation
- ✅ 6 new comprehensive guides added
- ✅ Testing procedures documented
- ✅ Troubleshooting section expanded
- ✅ Production checklist updated

---

## 🎉 Acknowledgments

- Spring Boot & Spring Security communities
- React & Vite teams
- Tailwind CSS
- MySQL Community
- OpenStreetMap Nominatim (geocoding)
- Indian Postal Service API
- All open-source contributors

---

## 🚀 What's Next?

**Planned Features (Future Versions):**
- Payment gateway integration
- Real-time chat between customers and providers
- Push notifications
- Service provider verification system
- Multi-language support
- Mobile apps (iOS/Android)
- Advanced analytics dashboard
- AI-powered service recommendations

---

**Built with ❤️ by Team C | Production Ready v4.0.3** 🚀

## 📞 Support

If you encounter issues:

1. Check `TROUBLESHOOTING.md`
2. Review backend logs
3. Check browser console for frontend errors
4. Verify database connection
5. Ensure all dependencies are installed

---

**Built with ❤️ by Team C**

🚀 Happy Coding!

