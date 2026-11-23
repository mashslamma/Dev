# QR Card Pro — Цифровая визитка для компаний

## 📖 Оглавление / Table of Contents

| Русский | English |
|---------|---------|
| [О проекте](#-о-проекте) | [About](#-about) |
| [Возможности](#-возможности) | [Features](#-features) |
| [Технологический стек](#-технологический-стек) | [Tech Stack](#-tech-stack) |
| [Структура проекта](#-структура-проекта) | [Project Structure](#-project-structure) |
| [Установка и запуск](#-установка-и-запуск) | [Installation & Run](#-installation--run) |
| [Настройка Firebase](#-настройка-firebase) | [Firebase Setup](#-firebase-setup) |
| [Сборка и релиз](#-сборка-и-релиз) | [Build & Release](#-build--release) |
| [Корпоративная версия](#-корпоративная-версия) | [Enterprise Version](#-enterprise-version) |
| [Лицензия](#-лицензия) | [License](#-license) |

---

## 🚀 О проекте / About

### Русский
**QR Card Pro** — Android-приложение для создания цифровых визиток с QR-кодом в формате vCard 3.0. При сканировании QR-кода стандартной камерой iPhone или Android контакт сохраняется в телефон с всеми указанными соцсетями, мессенджерами и контактными данными.

Приложение поддерживает регистрацию через email, хранение профилей в Firebase, мультиязычный интерфейс (RU/EN/ZH/JA/KO), тёмную тему, генерацию PDF и экспорт .vcf файлов. В корпоративной версии доступна админ-панель для отслеживания статистики сканирований по сотрудникам и компаниям.

### English
**QR Card Pro** — an Android app for creating digital business cards with QR codes in vCard 3.0 format. When scanned with a standard iPhone or Android camera, the contact is saved to the phone with all specified social networks, messengers, and contact details.

The app supports email registration, profile storage in Firebase, multilingual interface (RU/EN/ZH/JA/KO), dark theme, PDF generation, and .vcf export. The enterprise version includes an admin dashboard to track scan statistics by employee and company.

---

## ✨ Возможности / Features

### Русский
- 📇 Генерация QR-кода в формате vCard 3.0 (сохранение контакта в телефон iOS/Android)
- 🌐 25+ соцсетей: международные, российские, китайские, японские, корейские
- 🔐 Регистрация и вход через email (Firebase Auth)
- ☁️ Хранение профилей в Firestore с автосохранением
- 📷 Встроенный сканер QR (CameraX + ML Kit)
- 📄 Экспорт визитки в PDF
- 📤 Экспорт .vcf файла и шаринг QR-картинки
- 🌍 Мультиязычность: RU / EN / ZH / JA / KO
- 🌙 Тёмная тема (светлая / тёмная / системная)
- 🔄 Dynamic QR (ссылка на веб-карточку, редактируемая после печати)
- 📱 NFC-передача визитки (Android)
- 📊 Аналитика сканирований (кто, когда, что нажал)
- 🏢 Корпоративная версия: команды, компании, админ-панель для начальства
- 🎨 Дизайн QR: логотип в центре, цвета, рамки
- 🔒 Биометрическая блокировка (заготовка)

### English
- 📇 vCard 3.0 QR generation (save contact to iPhone/Android phone)
- 🌐 25+ social networks: international, Russian, Chinese, Japanese, Korean
- 🔐 Email sign-in/sign-up (Firebase Auth)
- ☁️ Profile storage in Firestore with auto-save
- 📷 Built-in QR scanner (CameraX + ML Kit)
- 📄 PDF business card export
- 📤 .vcf file export and QR image sharing
- 🌍 Multilingual: RU / EN / ZH / JA / KO
- 🌙 Dark theme (light / dark / system)
- 🔄 Dynamic QR (web link card, editable after printing)
- 📱 NFC business card transfer (Android)
- 📊 Scan analytics (who, when, what action)
- 🏢 Enterprise version: teams, companies, admin dashboard for management
- 🎨 QR design: logo in center, colors, frames
- 🔒 Biometric lock (placeholder)

---

## 🛠 Технологический стек / Tech Stack

| Компонент | Решение |
|-----------|---------|
| Язык / Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Бэкенд / Backend | Firebase Auth + Firestore |
| Генерация QR | ZXing (`com.google.zxing:core:3.5.2`) |
| Сканирование QR | Google ML Kit Barcode Scanning |
| Камера | CameraX (camera-camera2, camera-lifecycle, camera-view) |
| Изображения | Coil |
| PDF | Android `PdfDocument` |
| NFC | Android NDEF |
| Локализация | Android `strings.xml` + `LanguageManager` |
| Тема | `AppCompatDelegate` (Light/Dark/System) |

---

## 📁 Структура проекта / Project Structure

```
QRCardPro/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/digitalcardenterprise/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── UserProfile.kt
│       │   │   ├── Company.kt
│       │   │   └── ScanEvent.kt
│       │   ├── util/
│       │   │   ├── VCardGenerator.kt
│       │   │   ├── QrGenerator.kt
│       │   │   ├── ImageHelper.kt
│       │   │   ├── PdfExporter.kt
│       │   │   ├── LanguageManager.kt
│       │   │   ├── ThemeManager.kt
│       │   │   ├── AnalyticsManager.kt
│       │   │   ├── DynamicQrManager.kt
│       │   │   └── WalletPassManager.kt
│       │   ├── ui/
│       │   │   ├── auth/AuthScreen.kt
│       │   │   ├── profile/ProfileScreen.kt
│       │   │   ├── qr/QrScreen.kt
│       │   │   ├── scanner/ScannerScreen.kt
│       │   │   ├── settings/SettingsScreen.kt
│       │   │   ├── navigation/AppNavigation.kt
│       │   │   └── admin/
│       │   │       ├── AdminScreen.kt
│       │   │       └── IconsAdmin.kt
│       │   └── res/
│       │       ├── values/strings.xml (EN)
│       │       ├── values-ru/strings.xml (RU)
│       │       ├── values-zh/strings.xml (ZH)
│       │       ├── values/themes.xml
│       │       ├── values-night/themes.xml
│       │       └── xml/file_paths.xml
│       └── res/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
├── build_apk.sh
└── build_aab.sh
```

---

## 🛠 Установка и запуск / Installation & Run

### Русский

1. Установите [Android Studio](https://developer.android.com/studio)
2. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/mashslamma/Dev.git
    cd Dev/QRCardPro
   ```
3. Создайте проект в [Firebase Console](https://console.firebase.google.com)
4. Добавьте Android-приложение с пакетом `com.example.digitalcardenterprise`
5. Скачайте `google-services.json` и положите в папку `app/`
6. В консоли Firebase включите:
   - **Authentication → Sign-in method → Email/Password**
   - **Firestore Database → Create database → Test mode**
7. Откройте проект в Android Studio → **File → Sync Project with Gradle Files**
8. Запустите на эмуляторе или устройстве: ▶ **Run**

### English

1. Install [Android Studio](https://developer.android.com/studio)
2. Clone the repository:
   ```bash
   git clone https://github.com/mashslamma/Dev.git
    cd Dev/QRCardPro
   ```
3. Create a project in [Firebase Console](https://console.firebase.google.com)
4. Add an Android app with package `com.example.digitalcardenterprise`
5. Download `google-services.json` and place it in the `app/` folder
6. In the Firebase console enable:
   - **Authentication → Sign-in method → Email/Password**
   - **Firestore Database → Create database → Test mode**
7. Open the project in Android Studio → **File → Sync Project with Gradle Files**
8. Run on emulator or device: ▶ **Run**

---

## 🔥 Настройка Firebase / Firebase Setup

### Русский

Правила доступа Firestore (режим тестирования для разработки):

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### English

Firestore security rules (test mode for development):

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 📦 Сборка и релиз / Build & Release

### Русский

**Debug APK:**
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

**Release AAB (для Google Play):**
```bash
./gradlew bundleRelease
# AAB: app/build/outputs/bundle/release/app-release.aab
```

Для релиза в Google Play необходим аккаунт разработчика ($25 единоразово) и подписанный AAB-файл.

### English

**Debug APK:**
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

**Release AAB (for Google Play):**
```bash
./gradlew bundleRelease
# AAB: app/build/outputs/bundle/release/app-release.aab
```

For Google Play release you need a developer account ($25 one-time) and a signed AAB file.

---

## 🏢 Корпоративная версия / Enterprise Version

### Русский

Корпоративная версия предназначена для компаний, которым нужно отслеживать эффективность цифровых визиток сотрудников.

**Возможности для администратора:**
- Подключение сотрудников к компании через `companyId`
- Статистика сканирований: общее количество, звонки по корпоративному телефону, шаринги, сохранения контактов
- Экспорт отчётов
- Управление ролями (admin / member)

**Схема Firestore:**
- `/users/{uid}` — профиль сотрудника с полем `companyId` и `role`
- `/companies/{companyId}` — данные компании
- `/scans/{scanId}` — события сканирования (кто, когда, какое действие, на какую ссылку нажал)

### English

The enterprise version is designed for companies that need to track the effectiveness of employee digital business cards.

**Admin capabilities:**
- Connect employees to a company via `companyId`
- Scan statistics: total scans, corporate phone calls, shares, contact saves
- Report export
- Role management (admin / member)

**Firestore schema:**
- `/users/{uid}` — employee profile with `companyId` and `role` fields
- `/companies/{companyId}` — company data
- `/scans/{scanId}` — scan events (who, when, what action, which link clicked)

---

## 📄 Лицензия / License

MIT License. Используйте freely для коммерческих и некоммерческих целей.

MIT License. Free to use for commercial and non-commercial purposes.

---

**Разработано с ❤️ для бизнеса и нетворкинга**  
**Made with ❤️ for business and networking**
