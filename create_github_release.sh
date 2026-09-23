#!/usr/bin/env bash
# Script to create a GitHub Release and upload APK using GitHub CLI (gh)
# Target Repository: Olajide2004/Olajide-Sherif

set -e

REPO="Olajide2004/Olajide-Sherif"
TAG="${1:-v1.0.0}"
TITLE="${2:-Release $TAG - Blogger Auto Typer}"
NOTES="${3:-Blogger Auto Typer & Post Workbench Android APK Release. Built with custom typewriter engine, comfortable writing palettes, and integrated tools.}"
APK_PATH="release/BloggerAutoTyper-v1.0.0.apk"

# Fallback paths for APK if not found in release/
if [ ! -f "$APK_PATH" ]; then
  if [ -f ".build-outputs/app-debug.apk" ]; then
    mkdir -p release
    cp .build-outputs/app-debug.apk "$APK_PATH"
  elif [ -f "public/BloggerAutoTyper-v1.0.0.apk" ]; then
    mkdir -p release
    cp public/BloggerAutoTyper-v1.0.0.apk "$APK_PATH"
  else
    echo "APK not found. Building APK with Gradle..."
    gradle assembleDebug || ./gradlew assembleDebug
    mkdir -p release
    FOUND_APK=$(find app/build -name "*.apk" | head -n 1)
    if [ -n "$FOUND_APK" ]; then
      cp "$FOUND_APK" "$APK_PATH"
    else
      echo "Error: Could not locate built APK."
      exit 1
    fi
  fi
fi

echo "=========================================================="
echo " Creating GitHub Release via GitHub CLI (gh)"
echo " Repository: $REPO"
echo " Tag:        $TAG"
echo " Title:      $TITLE"
echo " Asset:      $APK_PATH ($(du -h "$APK_PATH" | cut -f1))"
echo "=========================================================="

# Check if gh is installed
if ! command -v gh &> /dev/null; then
  echo "Installing GitHub CLI (gh)..."
  if command -v apt-get &> /dev/null; then
    apt-get update -qq && apt-get install -y -qq gh
  else
    echo "Error: 'gh' CLI is not installed. Please install GitHub CLI."
    exit 1
  fi
fi

# Check authentication
if ! gh auth status &> /dev/null && [ -z "$GH_TOKEN" ] && [ -z "$GITHUB_TOKEN" ]; then
  echo ""
  echo "⚠️ GitHub CLI is not authenticated yet."
  echo "To authenticate, you have two simple options:"
  echo "  Option A: Run 'gh auth login' in your terminal."
  echo "  Option B: Export your GitHub Personal Access Token (with repo scope):"
  echo "            export GH_TOKEN=\"ghp_yourPersonalAccessTokenHere\""
  echo "            ./create_github_release.sh"
  echo ""
  echo "Attempting interactive login now..."
  gh auth login --hostname github.com
fi

echo "Creating release and uploading APK to https://github.com/$REPO/releases/tag/$TAG ..."
gh release create "$TAG" "$APK_PATH" \
  --repo "$REPO" \
  --title "$TITLE" \
  --notes "$NOTES"

echo ""
echo "✅ Successfully created release and uploaded APK to https://github.com/$REPO/releases/tag/$TAG!"
