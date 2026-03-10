# 🛠️ Fix: Repository Not Found Error

The terminal said "Repository not found" because the GitHub link we used has a typo (probably that dot at the end of the username).

Follow these 3 steps to fix it and go live:

### Step 1: Open GitHub in your Browser
1. Go to your GitHub profile.
2. Open your repository named **CraveDash-App**.
3. Look for the **green button** that says **"Code"** or look at the **"Quick setup"** section.
4. **Copy the HTTPS link** (it looks like `https://github.com/YourName/CraveDash-App.git`).

---

### Step 2: Run these commands in the Terminal
Paste these one by one and press **Enter**.

#### A. Remove the broken link:
```sh
git remote remove origin
```

#### B. Add the CORRECT link:
*⚠️ PASTE the link you copied from your browser instead of the one below if it looks different!*
```sh
git remote add origin https://github.com/jehoiakimchibuzor/CraveDash-App.git
```

#### C. Push to GitHub:
```sh
git push -u origin main
```

---

## 🏁 How to know it worked:
The terminal will show:
`Writing objects: 100% (79/79), 1.2 MiB | ...`
`Branch 'main' set up to track remote branch 'main' from 'origin'.`

**You are almost there! Fix the link and you are live!** 🚀🦾🔐
