#!/bin/bash
echo "рџ”Ё Building Release AAB..."
./gradlew bundleRelease
echo ""
echo "вњ… AAB: app/build/outputs/bundle/release/app-release.aab"
echo ""
echo "рџљЂ Upload AAB to Google Play Console"
