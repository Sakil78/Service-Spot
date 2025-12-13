# 🚀 DEPLOY NOW - Service Spot v4.0

**Copy and paste these commands to deploy your app in 20 minutes**  
**Last Updated**: December 1, 2025  
**Cost**: $0/month (Free tier)

---

## ⚡ Prerequisites

Before starting, ensure:
- ✅ App runs successfully on localhost
- ✅ Backend builds without errors (`mvn clean install`)
- ✅ Frontend builds without errors (`cd frontend && npm run build`)
- ✅ All features tested locally
- ✅ Git installed on your system

---

## 🔐 Step 1: Create Accounts (5 min)

Create free accounts on these platforms:

1. **GitHub**: https://github.com/signup
   - If you already have one, just login
   
2. **Railway**: https://railway.app
   - Click "Login with GitHub"
   - Authorize Railway
   
3. **Vercel**: https://vercel.com/signup
   - Click "Continue with GitHub"
   - Authorize Vercel

**Why these?**
- Railway: Hosts backend + MySQL (free 500 hours/month)
- Vercel: Hosts React frontend (free unlimited)
- GitHub: Stores your code

---

## 🔑 Step 2: Generate JWT Secret (1 min)

### Windows PowerShell:
```powershell
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

### Mac/Linux Terminal:
```bash
openssl rand -base64 64 | tr -d '\n'
```

**📋 Copy the output!** You'll need it in Step 3.

Example output: `xK9pL2mN4oP6qR8sT0uV1wX3yZ5aB7cD9eF1gH3iJ5kL7mN9oP1qR3sT5uV7wX9yZ1`

---

## 📦 Step 3: Push Code to GitHub (5 min)

### 3.1 Initialize Git (if not already)
```bash
cd "C:\Users\Ahmed\OneDrive\Desktop\service-spotV4(location tracking, no payment)"
git init
git add .
git commit -m "Initial commit - Service Spot v4.0 Production Ready"
```

### 3.2 Create GitHub Repository
1. Go to https://github.com/new
2. Repository name: `service-spot-v4`
3. Keep it **Public** or **Private** (your choice)
4. Don't initialize with README (we already have one)
5. Click **Create repository**

### 3.3 Push to GitHub
```bash
git remote add origin https://github.com/YOUR-USERNAME/service-spot-v4.git
git branch -M main
git push -u origin main
```

**Replace `YOUR-USERNAME` with your GitHub username!**

---

## 🚂 Step 4: Deploy Backend to Railway (10 min)

### 4.1 Create New Project
1. Go to https://railway.app/dashboard
2. Click **+ New Project**
3. Select **Deploy from GitHub repo**
4. Choose **service-spot-v4**
5. Click **Deploy Now**

### 4.2 Add MySQL Database
1. In same project, click **+ New**
2. Select **Database** → **Add MySQL**
3. Railway automatically provisions database
4. Wait 30 seconds for database to be ready

### 4.3 Configure Environment Variables
1. Click on your backend service (service-spot-v4)
2. Go to **Variables** tab
3. Click **+ New Variable**
4. Add these variables one by one:

```env
PORT=8080
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<paste-your-generated-secret-from-step-2>
FRONTEND_URL=https://localhost:3000
```

### 4.4 Connect MySQL to Backend
1. Still in Variables tab
2. Click **+ Reference Variables**
3. Select your MySQL database
4. Check **DATABASE_URL**
5. Click **Add**

### 4.5 Wait for Deployment
- Watch the build logs (click **Deployments** tab)
- Wait for: ✅ **SUCCESS** (usually 2-5 minutes)
- **Copy your Railway URL**: Settings → Domains → Generate Domain
- Example: `https://service-spot-v4-production.up.railway.app`

---

## 🎨 Step 5: Deploy Frontend to Vercel (5 min)

### 5.1 Create Environment File
1. Go to `frontend` folder
2. Create file: `.env.production`
3. Add this line (replace with YOUR Railway URL):
```env
VITE_API_BASE_URL=https://your-railway-url.up.railway.app
```

### 5.2 Commit Changes
```bash
cd frontend
git add .
git commit -m "Add production environment config"
git push
```

### 5.3 Deploy to Vercel
1. Go to https://vercel.com/dashboard
2. Click **Add New** → **Project**
3. Click **Import** next to your `service-spot-v4` repo
4. **Framework Preset**: Vite
5. **Root Directory**: `frontend`
6. Click **Environment Variables** (expand)
7. Add:
   - Name: `VITE_API_BASE_URL`
   - Value: `https://your-railway-url.up.railway.app` (your Railway URL)
8. Click **Deploy**
9. Wait 2-3 minutes for deployment
10. **Copy your Vercel URL**: Example `https://service-spot-v4.vercel.app`

---

## 🔗 Step 6: Connect Frontend & Backend (2 min)

### 6.1 Update CORS in Railway
1. Go back to Railway dashboard
2. Click your backend service
3. Go to **Variables** tab
4. Find `FRONTEND_URL` variable
5. Click **Edit**
6. Change from `https://localhost:3000` to your **Vercel URL**
   - Example: `https://service-spot-v4.vercel.app`
   - **Important**: NO trailing slash!
7. Click **Save**
8. Railway will auto-redeploy (wait 2 min)

---

## ✅ Step 7: Test Your Live App! (5 min)

### 7.1 Open Your Vercel URL
Go to your Vercel URL in browser (e.g., `https://service-spot-v4.vercel.app`)

### 7.2 Test Complete Flow
- [ ] Landing page loads with 16 service categories
- [ ] Click **Sign Up** → Register new customer (use any Indian pincode)
- [ ] **Login** with your new account
- [ ] Browse services (should see distance if providers exist)
- [ ] Try creating a provider account
- [ ] Provider adds a service with availability
- [ ] Customer books the service
- [ ] Provider accepts booking
- [ ] Customer leaves review

### 7.3 Test Admin Access
- Go to: `https://your-vercel-url.vercel.app/login?type=admin`
- Email: `admin@servicespot.com`
- Password: `Admin@123`
- Should see admin dashboard with user management

---

## 🎉 SUCCESS!

If all tests pass:
- ✅ **Your app is LIVE on the internet!**
- ✅ **Anyone can access it via your Vercel URL**
- ✅ **Backend + Database hosted on Railway**
- ✅ **Frontend hosted on Vercel**
- ✅ **Total Cost: $0/month**

---

## 📊 Your Deployment Summary

| Component | Platform | URL | Status |
|-----------|----------|-----|--------|
| Backend API | Railway | https://your-railway-url.up.railway.app | 🟢 Live |
| Frontend | Vercel | https://your-vercel-url.vercel.app | 🟢 Live |
| Database | Railway MySQL | Internal | 🟢 Running |
| Source Code | GitHub | https://github.com/YOUR-USERNAME/service-spot-v4 | 🟢 Stored |

---

## 🐛 Troubleshooting

### Issue 1: CORS Error in Browser
**Symptom**: Frontend shows "Network Error" or CORS blocked

**Solution**:
1. Go to Railway → Backend Service → Variables
2. Check `FRONTEND_URL` matches your Vercel URL exactly
3. No trailing slash: ❌ `https://app.vercel.app/` ✅ `https://app.vercel.app`
4. Save and wait for redeploy (2 min)

### Issue 2: Backend Build Failed
**Symptom**: Railway shows build error

**Solution**:
1. Click on the failed deployment
2. Read error logs
3. Common fix: Ensure `pom.xml` has Java 21 configured
4. Check all files are pushed to GitHub
5. Redeploy: Settings → Redeploy

### Issue 3: Frontend Shows White Screen
**Symptom**: Blank page or loading forever

**Solution**:
1. Open browser DevTools (F12) → Console
2. Check for errors
3. Verify `VITE_API_BASE_URL` in Vercel dashboard
4. Redeploy frontend: Vercel → Deployments → ⋯ → Redeploy

### Issue 4: Database Connection Failed
**Symptom**: Backend logs show "Can't connect to MySQL"

**Solution**:
1. Railway → MySQL service → Check status (should be green)
2. Backend service → Variables → Verify DATABASE_URL exists
3. Try: Delete MySQL, add new MySQL, reconnect

---

## 💰 Free Tier Limits

### Railway (Backend + MySQL)
- ✅ 500 execution hours/month
- ✅ 512 MB RAM
- ✅ 1 GB Storage
- ⚠️ App sleeps after 15 min inactivity (wakes on request in 10-30 sec)

### Vercel (Frontend)
- ✅ Unlimited bandwidth
- ✅ 100 GB bandwidth/month
- ✅ Auto SSL certificate
- ✅ Global CDN
- ✅ No sleep/wake delays

---

## 🚀 Next Steps

### Share Your App
- Copy your Vercel URL
- Share with friends, family, or on social media
- Add to your portfolio/resume

### Custom Domain (Optional)
1. Buy domain from Namecheap/GoDaddy (~$10/year)
2. Vercel: Settings → Domains → Add
3. Railway: Settings → Domains → Custom Domain

### Monitor Your App
- Railway: View logs, metrics, usage
- Vercel: Analytics, deployment history
- Set up error tracking (Sentry - free tier available)

---

## 📚 Additional Resources

- **Full Deployment Guide**: `DEPLOYMENT_GUIDE_FREE.md`
- **Production Status**: `PRODUCTION_STATUS.md`
- **Quick Start**: `QUICK_START.md`
- **Main README**: `README.md`

---

## 🆘 Need Help?

- Check Railway logs: Dashboard → Service → Logs
- Check Vercel logs: Dashboard → Deployments → Click deployment → Build Logs
- GitHub Issues: Create issue in your repo
- Railway Discord: https://discord.gg/railway
- Vercel Support: https://vercel.com/support

---

## ✨ Congratulations!

You've successfully deployed a full-stack application with:
- ✅ Java Spring Boot backend
- ✅ React frontend
- ✅ MySQL database
- ✅ JWT authentication
- ✅ Location tracking
- ✅ 16 service categories
- ✅ Complete booking system

**Total Time**: 20-25 minutes  
**Total Cost**: $0/month  
**Your App**: Live on the internet! 🎉

---

**Share your deployed URL! Tag us if you post online! 🚀**

**Built with ❤️ by Team C**  
**Version 4.0 - December 2025**

