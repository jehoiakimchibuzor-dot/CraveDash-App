# 🚀 Git Push Instructions for CraveDash-App

Follow these steps exactly. Copy ONLY the text inside the boxes.

## Step 0: Open the Terminal
1. Click the **"Terminal"** tab at the very bottom left of Android Studio.
2. To paste: **Right-Click** or press **`Ctrl + Shift + V`**.

---

## 1. Create the Repository on GitHub
Go to GitHub and create a new repository named `CraveDash-App`.
- **README**: ⬜ LEAVE UNCHECKED
- **.gitignore**: NONE
- **License**: NONE
- Click **Create repository**.

---

## 2. Push Your Code (Terminal Commands)
Copy and paste these one by one. Press **Enter** after each one.

### Step A: Initialize Git
```
git init
```

### Step B: Add your files
*(Make sure to include the dot at the end)*
```
git add .
```

### Step C: Create the first commit
```
git commit -m "Initial commit: CraveDash-App Premium MVP with AI MAX"
```

### Step D: Set branch to main
```
git branch -M main
```

### Step E: Link to GitHub
*⚠️ Replace 'YOUR_USERNAME' with your actual GitHub username.*
```
git remote add origin https://github.com/YOUR_USERNAME/CraveDash-App.git
```

### Step F: Push to GitHub
```
git push -u origin main
```

---

## 🛡️ Final Security Note
Your API key is sitting safely in `local.properties`. Because that file is in your `.gitignore`, it will **never** be uploaded to GitHub. Your code is secure!

**Go-Live is seconds away. You've got this!** 🚀🦾🔐
