# GitHub Release APK Guide for Olajide2004/Olajide-Sherif

This guide explains how to generate, release, and download your **Blogger Auto Typer** release APK on repository **[Olajide2004/Olajide-Sherif](https://github.com/Olajide2004/Olajide-Sherif)**.

---

## 🚀 Option 1: Automatic Release via GitHub Tags / Releases (Recommended)

The automated GitHub Action workflow (`.github/workflows/release.yml`) builds and publishes the APK whenever you create a release.

### Method A: Through GitHub Web Interface
1. Open your repository: **https://github.com/Olajide2004/Olajide-Sherif**
2. On the right side, click **Releases** (or navigate to `https://github.com/Olajide2004/Olajide-Sherif/releases/new`).
3. Click **Draft a new release**.
4. Click **Choose a tag**, enter a version tag such as `v1.0.0`, and click **Create new tag: v1.0.0 on publish**.
5. Set Release title (e.g. `Release v1.0.0 - Blogger Auto Typer`).
6. Click **Publish release**.
7. GitHub Actions will start automatically in the **Actions** tab. Once completed (approx. 2–3 minutes), `BloggerAutoTyper-release.apk` will appear under the **Assets** section of your new release for anyone to download!

---

### Method B: Through Git CLI
Pushing any tag beginning with `v` triggers the release build:
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

### Method C: One-Click Manual Trigger (Workflow Dispatch)
1. Go to **https://github.com/Olajide2004/Olajide-Sherif/actions**.
2. Select the **Build & Publish GitHub Release APK** workflow on the left sidebar.
3. Click the **Run workflow** dropdown button on the right and select the `main` branch.
4. Click **Run workflow**.
5. After the run completes, scroll down to the **Artifacts** section at the bottom of the page and click **BloggerAutoTyper-release-apk** to download the APK.

---

## 🔑 Custom Keystore Secrets (Optional)

If you have a dedicated release keystore you would like to sign with:
1. Base64-encode your `.jks` file:
   ```bash
   base64 -w 0 my-upload-key.jks > keystore_base64.txt
   ```
2. In GitHub (**Settings > Secrets and variables > Actions**), add:
   - `KEYSTORE_BASE64`: Paste the content of `keystore_base64.txt`
   - `STORE_PASSWORD`: Your keystore password
   - `KEY_PASSWORD`: Your key password

If these secrets are not configured, the workflow automatically and seamlessly handles signing using the repository fallback so builds succeed out of the box!

---

## 📲 Option 2: Generate APK via AI Studio

1. In the AI Studio top navigation bar, open the **Settings** or **Export** dropdown.
2. Select **Generate APK / AAB** or **Push to GitHub**.
