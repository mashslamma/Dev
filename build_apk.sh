#!/bin/bash
echo "рџ”Ё Building Debug APK..."
./gradlew assembleDebug
echo ""
echo "вњ… APK: app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "рџ“± To install on device: adb install app/build/outputs/apk/debug/app-debug.apk"
