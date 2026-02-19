# Vercel Deployment

To deploy the frontend to Vercel:

1. Install Vercel CLI: `npm i -g vercel`
2. Login: `vercel login`
3. Deploy: `vercel`

Or link your GitHub repository to Vercel.

**Configuration:**

- **Root Directory**: `healthcare-bbc/frontend`
- **Build Command**: `npm run build`
- **Output Directory**: `dist`
- **Environment Variables**:
  - `VITE_API_BASE_URL`: Your backend URL (e.g., `https://your-backend.up.railway.app`)
