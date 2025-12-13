# 🚀 Frontend Setup Guide - Service Spot v4.0

**Complete setup instructions for the React frontend**  
**Last Updated**: December 1, 2025  
**Time Required**: 5 minutes

---

## 📋 Prerequisites

Before starting, ensure you have:
- ✅ **Node.js 18+** installed (check: `node --version`)
- ✅ **npm** installed (check: `npm --version`)
- ✅ **Backend running** on http://localhost:8080
- ✅ Terminal/Command Prompt access

---

## ⚡ Quick Setup (3 Steps)

### Step 1: Navigate to Frontend Directory
```bash
cd frontend
```

### Step 2: Install Dependencies
```bash
npm install
```

**Wait time**: 1-2 minutes  
**Downloads**: ~200 MB of packages

### Step 3: Start Development Server
```bash
npm run dev
```

**Frontend runs at**: http://localhost:3000 ✅

---

## 🎯 What's Included in v4.0

### ✅ Complete Page Set (15 Pages)
1. **LandingPage** - Home with 16 service categories
2. **LoginPage** - User authentication
3. **SignupPage** - Registration (Customer/Provider)
4. **ServiceListPage** - Browse services with distance
5. **ServiceDetailPage** - Service details + booking
6. **ProviderDetailPage** - Provider profile view
7. **BookingPage** - Create booking with availability
8. **MyBookingsPage** - Customer booking history
9. **CustomerDashboard** - Customer overview
10. **CustomerProfile** - Edit customer profile
11. **ProviderDashboard** - Provider overview + stats
12. **ProviderProfile** - Edit provider profile
13. **AdminDashboard** - User management
14. **NearbyServicesSearch** - Location-based search
15. **404 Page** - Not found handler

### ✅ Core Features
- 🔐 **JWT Authentication** - Secure login/logout
- 👥 **Role-Based Access** - Customer/Provider/Admin
- 📍 **Location Tracking** - Distance calculation (km)
- 📅 **Smart Booking** - Provider availability calendar
- ⭐ **Review System** - Post-service ratings
- 🔍 **Search & Filter** - By city, category, distance
- 📊 **Dashboards** - Role-specific overviews
- 🎨 **Dark Theme** - Modern glassmorphic UI
- 📱 **Responsive** - Mobile, tablet, desktop
- ⚡ **Fast Loading** - Code splitting + lazy loading

### ✅ UI Components (14 Components)
- **Navbar** - Navigation with auth state
- **Footer** - App footer
- **ProtectedRoute** - Auth guard wrapper
- **LoadingSpinner** - Loading indicators
- **Modal** - Dialog modals
- **StarRating** - Star display component
- **StatusBadge** - Booking status badges
- **ServiceCard** - Service display cards
- **ImageSlider** - Hero carousel
- **ErrorBoundary** - Error handling wrapper
- **EmptyState** - No data placeholders
- **RealTimeLocationTracker** - Location display
- **Sidebar** - Navigation sidebar
- **SurfaceLayout** - Layout wrapper

### ✅ Context & State
- **AuthContext** - Authentication state
- **AppProviders** - Context wrapper
- JWT token management
- User role handling
- Persistent login state

### ✅ API Integration
50+ API endpoints connected:
- Authentication (login, register)
- Services (CRUD operations)
- Bookings (create, update, list)
- Reviews (submit, fetch)
- Providers (profile, services)
- Categories (16 types)
- Location (distance calculation)
- Admin (user management)

---

## 📝 Testing the Application

### Test as Customer
1. ✅ Open http://localhost:3000
2. ✅ Click **Sign Up** → Choose "I'm a Customer"
3. ✅ Fill form with valid Indian pincode (e.g., 110001)
4. ✅ Submit → Auto-login → Redirects to dashboard
5. ✅ Click **Browse Services** → See 16 categories
6. ✅ Select a service → View provider distance
7. ✅ Click **Book Now** → Select date/time
8. ✅ Fill booking form → Submit
9. ✅ Go to **My Bookings** → See booking status
10. ✅ After service → Leave review

### Test as Provider
1. ✅ Logout (if logged in)
2. ✅ Click **Sign Up** → Choose "I'm a Service Provider"
3. ✅ Fill business details with pincode
4. ✅ Submit → Auto-login → Provider dashboard
5. ✅ Click **Add Service** → Create service listing
6. ✅ Set **Availability** → Choose dates and times
7. ✅ Go to **Manage Bookings** → See requests
8. ✅ Click customer name → View customer profile with distance
9. ✅ Accept/Reject booking
10. ✅ Mark as completed when done

### Test as Admin
1. ✅ Logout (if logged in)
2. ✅ Go to http://localhost:3000/login?type=admin
3. ✅ Email: `admin@servicespot.com`
4. ✅ Password: `Admin@123`
5. ✅ View all users (Customers & Providers)
6. ✅ Delete user (if needed)
7. ✅ See user statistics

---

## 🔌 Backend Connection

### API Configuration
**File**: `src/services/api.js`

```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
```

### How It Works
1. Frontend makes API call: `axios.get('/api/services')`
2. Axios sends request to: `http://localhost:8080/api/services`
3. Backend processes request
4. Response sent back to frontend
5. React updates UI

### API Endpoints Used
- `POST /api/auth/login` - User login
- `POST /api/auth/register/customer` - Customer signup
- `POST /api/auth/register/provider` - Provider signup
- `GET /api/services` - List services
- `GET /api/services/{id}` - Service details
- `POST /api/bookings` - Create booking
- `GET /api/categories` - Get 16 categories
- `GET /api/location/user/{id}` - Get location
- `GET /api/providers/{id}` - Provider details
- `POST /api/reviews` - Submit review
- ...and 40+ more

---

## 🎨 Key Technologies

### Core Framework
- **React 18.2.0** - Latest React with hooks
- **Vite 5.4.21** - Lightning-fast build tool
- **React Router v6** - Client-side routing

### Styling
- **Tailwind CSS 3.x** - Utility-first CSS
- **PostCSS** - CSS processing
- Custom dark theme with gradients

### HTTP & API
- **Axios** - Promise-based HTTP client
- Request/response interceptors
- Auto JWT token attachment

### Icons
- **React Icons** - 1000+ icons
- **Lucide React** - Modern icon set

### State Management
- **React Context API** - Global state
- **localStorage** - JWT persistence

---

## 🛠️ Available npm Scripts

```bash
# Development
npm run dev          # Start dev server (http://localhost:3000)

# Production
npm run build        # Create optimized build (dist/ folder)
npm run preview      # Preview production build locally

# Code Quality
npm run lint         # Run ESLint for code quality
npm run format       # Format code (if configured)
```

### Script Details

**`npm run dev`**
- Starts Vite dev server
- Hot Module Replacement (HMR)
- Fast refresh on file changes
- Source maps for debugging
- Opens at http://localhost:3000

**`npm run build`**
- Creates production bundle
- Minifies JS and CSS
- Tree shaking (removes unused code)
- Code splitting
- Asset optimization
- Output: `dist/` folder

**`npm run preview`**
- Serves production build locally
- Test before deployment
- Opens at http://localhost:4173

---

## 📱 Responsive Design

### Breakpoints (Tailwind)
```css
/* Mobile First */
.class { } /* All devices */

@media (min-width: 640px) { } /* sm: Mobile landscape & up */
@media (min-width: 768px) { } /* md: Tablet & up */
@media (min-width: 1024px) { } /* lg: Desktop & up */
@media (min-width: 1280px) { } /* xl: Large desktop & up */
```

### Testing Responsive
1. Open DevTools (F12)
2. Toggle device toolbar (Ctrl+Shift+M)
3. Test on:
   - Mobile (375px) - iPhone SE
   - Tablet (768px) - iPad
   - Desktop (1440px) - MacBook

---

## 🐛 Troubleshooting

### Issue 1: npm install fails
**Error**: `ENOENT` or `permission denied`

**Solution**:
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

### Issue 2: Port 3000 already in use
**Error**: `Port 3000 is already in use`

**Solution**:
```bash
# Windows
netstat -ano | findstr :3000
taskkill /F /PID <PID>

# Mac/Linux
lsof -ti:3000 | xargs kill -9

# Or use different port
npm run dev -- --port 3001
```

### Issue 3: White screen on load
**Error**: Blank page or infinite loading

**Solution**:
1. Check browser console (F12) for errors
2. Verify backend is running: http://localhost:8080/api/categories
3. Clear browser cache (Ctrl+Shift+R)
4. Check `src/services/api.js` for correct API_BASE_URL

### Issue 4: API calls fail (Network Error)
**Error**: Axios throws network error

**Solution**:
1. **Backend not running**: Start backend with `mvn spring-boot:run`
2. **CORS issue**: Check backend SecurityConfig CORS settings
3. **Wrong URL**: Verify API_BASE_URL in api.js
4. **Firewall**: Allow port 8080 in firewall

### Issue 5: Images not loading
**Error**: Broken image icons

**Solution**:
1. Check image URLs in `ImageSlider` component
2. Ensure external image URLs are accessible
3. Check browser security settings (mixed content)
4. Use HTTPS URLs for production

---

## 🔒 Security Notes

### JWT Token Storage
- Stored in `localStorage` (key: `token`)
- Auto-attached to API requests via Axios interceptor
- Expires after 24 hours
- Cleared on logout or 401 response

### Protected Routes
All routes requiring authentication use `ProtectedRoute` wrapper:
```jsx
<Route 
  path="/dashboard" 
  element={
    <ProtectedRoute allowedRoles={['CUSTOMER']}>
      <CustomerDashboard />
    </ProtectedRoute>
  } 
/>
```

### Best Practices
- ✅ Never store passwords in frontend
- ✅ Always use HTTPS in production
- ✅ Validate user input before API calls
- ✅ Handle errors gracefully
- ✅ Clear tokens on logout

---

## 📊 Performance Tips

### Optimizations Implemented
- ✅ Lazy loading routes (React.lazy)
- ✅ Code splitting
- ✅ Minified production build
- ✅ Tree shaking
- ✅ Image optimization
- ✅ Axios request caching
- ✅ Debounced search inputs

### Lighthouse Scores (Target)
- Performance: 90+
- Accessibility: 95+
- Best Practices: 95+
- SEO: 90+

---

## 🚀 Deployment

### Build for Production
```bash
cd frontend
npm run build
```

**Output**: `dist/` folder ready for deployment

### Deploy to Vercel (Recommended)
```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel --prod
```

### Environment Variables for Production
Create `.env.production`:
```env
VITE_API_BASE_URL=https://your-backend-url.com/api
```

**Note**: Restart dev server after changing `.env` files!

---

## 📚 Project Structure Deep Dive

```
frontend/
├── src/
│   ├── components/       # 14 reusable UI components
│   ├── context/          # AuthContext for global state
│   ├── pages/            # 15 page components
│   ├── providers/        # Context providers wrapper
│   ├── services/         # API integration (api.js)
│   ├── utils/            # Helpers, constants, icons
│   ├── App.jsx           # Root component with routes
│   ├── main.jsx          # React entry point
│   └── index.css         # Global styles + Tailwind
│
├── public/               # Static assets
├── node_modules/         # Dependencies (auto-generated)
├── dist/                 # Production build (after npm run build)
│
├── index.html            # HTML template
├── package.json          # Dependencies and scripts
├── vite.config.js        # Vite configuration
├── tailwind.config.js    # Tailwind customization
├── postcss.config.js     # PostCSS config
├── jsconfig.json         # JS config for IDE
└── vercel.json           # Vercel deployment config
```

---

## 🎓 Learning Resources

### React
- Official Docs: https://react.dev
- React Router: https://reactrouter.com

### Tailwind CSS
- Official Docs: https://tailwindcss.com/docs
- Cheat Sheet: https://nerdcave.com/tailwind-cheat-sheet

### Vite
- Official Docs: https://vitejs.dev

---

## ✅ Setup Complete Checklist

After setup, verify:
- [ ] `npm install` completed without errors
- [ ] `npm run dev` starts server on port 3000
- [ ] Landing page shows 16 service categories
- [ ] Can navigate between pages
- [ ] Backend connection works (check Network tab)
- [ ] Can register new user
- [ ] Can login successfully
- [ ] Protected routes redirect properly
- [ ] No console errors (F12)

---

## 🎉 You're All Set!

Your Service Spot frontend is now ready for development!

**Quick Links**:
- Frontend: http://localhost:3000
- Backend: http://localhost:8080/api
- Main README: ../README.md
- Deployment: ../DEPLOY_NOW.md

**Happy Coding! 🚀**

---

**Built with ❤️ by Team C**  
**Version 4.0 - December 2025**
- Desktop: ≥ 1024px

## 🎯 Next Steps

1. **Start backend server** (Spring Boot on port 8080)
2. **Install frontend dependencies**: `npm install`
3. **Start frontend**: `npm run dev`
4. **Open browser**: http://localhost:3000
5. **Create test accounts** and explore the features!

## ⚠️ Important Notes

- Some features use mock data until backend APIs are implemented
- The CSS warnings for Tailwind directives are normal (processed by PostCSS)
- Ensure both frontend and backend servers are running simultaneously

## 🐛 Troubleshooting

### Port already in use?
```powershell
# Kill process on port 3000
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

### Dependencies not installing?
```powershell
# Clear npm cache and reinstall
npm cache clean --force
rm -r node_modules
rm package-lock.json
npm install
```

### Backend not connecting?
- Verify backend is running on http://localhost:8080
- Check CORS configuration in Spring Boot
- Verify API endpoints match the frontend calls

---

**🎉 Your frontend is ready to go! Happy coding!**
