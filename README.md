# Layer News & Sports App 📱

<div align="center">

[📖 Leia em Português](#versão-em-português)

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange.svg)
![Target SDK](https://img.shields.io/badge/Target%20SDK-36-orange.svg)

A modern Android news application with multi-flavor support, featuring Firebase integration, push notifications, and a clean architecture approach.

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Product Flavors](#-product-flavors)
- [Firebase Setup](#-firebase-setup)
- [Build & Run](#-build--run)
- [Push Notifications](#-push-notifications)
- [Known Issues](#-known-issues)
- [Contributing](#-contributing)

---

## 🌟 Overview

Layer App is a news aggregator Android application built with modern Android development practices. It features two product flavors (News and Sports) with complete Firebase integration, including authentication, push notifications, analytics, and Firestore database.

The app fetches news from external APIs, caches them locally using Room database with pagination support, and provides real-time push notifications with expiration management.

---

## ✨ Features

- **📰 News Feed**: Browse top headlines with pagination
- **🔍 Search**: Search news articles in real-time with debounce
- **🗂️ Filters**: Filter by category and sort order
- **🔔 Push Notifications**: Firebase Cloud Messaging with local storage
- **🔐 Authentication**: Firebase Authentication (Email/Password)
- **💾 Offline Support**: Room database caching with Paging 3
- **🎨 Modern UI**: Material Design with custom Design System
- **📊 Analytics**: Firebase Analytics integration
- **🔥 Crash Reporting**: Firebase Crashlytics
- **🌐 Multi-Flavor**: Separate News and Sports variants

---

## 🏗️ Architecture

The project follows **Clean Architecture** principles with **MVVM** pattern and is organized in a multi-module structure:

```
┌─────────────────────────────────────────────────────────────┐
│                         Main Module                         │
│                    (Application Entry)                      │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
┌────────▼────────┐ ┌───▼───────┐ ┌────▼──────────┐
│  Feature Modules │ │ Navigation │ │ Core Services │
├─────────────────┤ └────────────┘ └───────────────┘
│ • Authentication │                      │
│ • News          │◄─────────────────────┘
│ • Notifications  │
└──────────────────┘

Each Feature Module:
┌────────────────────────────────────────┐
│ Presentation (View/ViewModel)          │
├────────────────────────────────────────┤
│ Domain (UseCase/Repository Interface)  │
├────────────────────────────────────────┤
│ Data (Repository Impl/Data Sources)    │
└────────────────────────────────────────┘
```

### Module Breakdown

- **`:Main`** - Application module, entry point, product flavor configuration
- **`:feature:Authentication`** - Login, registration, password recovery
- **`:feature:News`** - News feed, search, filters, details
- **`:feature:Notifications`** - Notification list and management
- **`:navigation`** - Centralized navigation logic
- **`:core:services`** - Shared services (Network, Database, Firebase, Analytics)

---

## 📁 Project Structure

```
squad03-layer-app-android/
│
├── Main/                           # Main application module
│   ├── src/
│   │   ├── main/                   # Common source
│   │   ├── LayerNews/              # News flavor specific
│   │   └── LayerSports/            # Sports flavor specific
│   ├── build.gradle.kts
│   └── google-services.json        # Firebase configuration
│
├── feature/                        # Feature modules
│   ├── Authentication/
│   │   └── src/main/.../
│   │       ├── presentation/       # UI (Activities, ViewModels)
│   │       ├── domain/             # Business logic (UseCases)
│   │       └── data/               # Data sources (Repository)
│   ├── News/
│   │   └── src/main/.../
│   │       ├── presentation/
│   │       │   ├── view/          # Activities & Adapters
│   │       │   └── viewModel/     # ViewModels
│   │       ├── domain/
│   │       │   ├── model/         # Domain models
│   │       │   ├── repository/    # Repository interfaces
│   │       │   └── usecase/       # Business use cases
│   │       └── data/
│   │           ├── local/         # Room entities & DAOs
│   │           ├── remote/        # API services
│   │           ├── paging/        # Paging 3 implementation
│   │           └── repository/    # Repository implementations
│   └── Notifications/
│
├── core/
│   └── services/                   # Core shared services
│       └── src/main/.../
│           ├── network/            # Retrofit, API services
│           ├── database/
│           │   ├── local/         # Room database, DAOs, entities
│           │   └── FirestoreService
│           ├── notification/       # FCM service, helpers
│           ├── authentication/     # Auth service
│           ├── analytics/          # Analytics service
│           └── storage/            # Preferences
│
├── navigation/                     # Navigation module
│   └── src/main/.../
│       ├── Navigator.kt            # Centralized navigation
│       └── routes/                 # Navigation routes
│
├── gradle/
│   └── libs.versions.toml          # Version catalog
│
├── build.gradle.kts                # Root build file
├── settings.gradle.kts             # Module configuration
├── FCM-RELEASE-FIX.md              # FCM troubleshooting guide
└── README.md                       # This file
```

---

## 🛠️ Tech Stack

### Core Technologies

- **Language**: Kotlin 2.1.10
- **Build System**: Gradle with Kotlin DSL & Version Catalogs
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36

### Android Jetpack

- **Navigation Component** 2.9.7 - Fragment navigation
- **Room** 2.8.4 - Local database with Paging support
- **Paging 3** 3.4.0 - Pagination with RemoteMediator
- **Lifecycle** 2.10.0 - ViewModel & LiveData
- **ViewBinding** - Type-safe view access

### Dependency Injection

- **Hilt** 2.54 - Dependency injection framework
- **KSP** 2.1.10 - Annotation processing

### Firebase Services

- **Firebase BOM** 34.8.0
  - **Authentication** - User authentication
  - **Cloud Messaging (FCM)** - Push notifications
  - **Firestore** - Cloud database
  - **Crashlytics** - Crash reporting
  - **Analytics** - User analytics
  - **Remote Config** - Remote configuration

### Networking & Data

- **Retrofit** 3.0.0 - HTTP client
- **Gson** 3.0.0 - JSON serialization
- **Glide** 4.16.0 - Image loading

### UI

- **Material Design** 1.13.0
- **ConstraintLayout** 2.2.1
- **Custom Design System** (v1.0.20) - From `rafaelKontein23.designe-system-news`
- **Shimmer** 0.5.0 - Loading effect

### Testing

- **JUnit** 4.13.2
- **Espresso** 3.7.0
- **AndroidX Test** 1.3.0

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug | 2024.2.1 or newer
- **JDK** 11 or higher
- **Android SDK** with API level 36
- **Git**

### Clone the Repository

```powershell
git clone https://github.com/your-org/squad03-layer-app-android.git
cd squad03-layer-app-android
```

### Configure Firebase

1. Create two Firebase projects or use one project with multiple apps:
   - `com.exemplo.layer.news`
   - `com.exemplo.layer.sports`

2. Download `google-services.json` and place it in `Main/` directory

3. Add SHA fingerprints to Firebase Console (see [Firebase Setup](#-firebase-setup))

### Sync Project

Open the project in Android Studio and sync Gradle:

```powershell
./gradlew --refresh-dependencies
```

---

## 🎯 Product Flavors

The app supports two product flavors:

| Flavor | Application ID | Purpose |
|--------|---------------|---------|
| **LayerNews** | `com.exemplo.layer.news` | General news application |
| **LayerSports** | `com.exemplo.layer.sports` | Sports-focused news application |

Each flavor can have:
- Different package names
- Separate Firebase projects
- Custom resources and configurations
- Independent versioning

### Flavor-Specific Files

```
Main/
├── src/
│   ├── main/                    # Common code
│   ├── LayerNews/              # News-specific resources
│   │   └── res/
│   │       ├── values/
│   │       └── drawable/
│   └── LayerSports/            # Sports-specific resources
│       └── res/
│           ├── values/
│           └── drawable/
```

---

## 🔥 Firebase Setup

### Initial Configuration

1. **Create Firebase Project**: https://console.firebase.google.com/

2. **Add Android Apps**:
   - Add app with package `com.exemplo.layer.news`
   - Add app with package `com.exemplo.layer.sports`

3. **Download Configuration**:
   - Download `google-services.json`
   - Place in `Main/google-services.json`

### SHA Fingerprints (Critical for Release)

⚠️ **Important**: Both debug and release SHA fingerprints must be registered for FCM to work in release builds.

#### Get Debug SHA:

```powershell
keytool -list -v -keystore $env:USERPROFILE\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

#### Get Release SHA:

```powershell
keytool -list -v -keystore release-keystore.jks -alias layer_key
```

Your release SHA fingerprints:
```
SHA-1:   
SHA-256: 
```

#### Register in Firebase Console:

1. Go to Project Settings → Your Apps
2. For **each app** (LayerNews and LayerSports):
   - Click "Add fingerprint"
   - Add both debug SHA-1 and SHA-256
   - Add both release SHA-1 and SHA-256
3. Download updated `google-services.json`

### Enable Firebase Services

In Firebase Console:

1. **Authentication**:
   - Enable Email/Password provider
   - Configure sign-in methods

2. **Cloud Messaging**:
   - No additional setup needed (auto-enabled)

3. **Firestore Database**:
   - Create database in production mode
   - Set up security rules

4. **Crashlytics**:
   - Enable Crashlytics in the dashboard

---

## 🔨 Build & Run

### Build Commands

```powershell
# Clean build
./gradlew clean

# Build debug APKs
./gradlew assembleDebug

# Build debug for specific flavor
./gradlew assembleLayerNewsDebug
./gradlew assembleLayerSportsDebug

# Build release APKs
./gradlew assembleRelease

# Build release for specific flavor
./gradlew assembleLayerNewsRelease
./gradlew assembleLayerSportsRelease

# Install debug on device
./gradlew installLayerNewsDebug

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

### Run from Android Studio

1. Select build variant: `Build → Select Build Variant`
   - `layerNewsDebug`
   - `layerNewsRelease`
   - `layerSportsDebug`
   - `layerSportsRelease`

2. Click Run (Shift+F10)

### APK Location

Built APKs are located at:
```
Main/build/outputs/apk/
├── LayerNews/
│   ├── debug/
│   │   └── Main-LayerNews-debug.apk
│   └── release/
│       └── Main-LayerNews-release.apk
└── LayerSports/
    ├── debug/
    │   └── Main-LayerSports-debug.apk
    └── release/
        └── Main-LayerSports-release.apk
```

---

## 🔔 Push Notifications

### Architecture

The app uses Firebase Cloud Messaging (FCM) with a custom implementation:

```
CoreFirebaseMessagingService (core:services)
    ↓
Receives notification
    ↓
Saves to Room database (NotificationEntity)
    ↓
Shows system notification
    ↓
User opens notification
    ↓
NotificationsActivity displays from database
```

### Features

- **Local Storage**: Notifications saved to Room database
- **Expiration Management**: Auto-expire old notifications
- **Deep Linking**: Open specific content from notifications
- **Permission Handling**: Runtime notification permissions (Android 13+)

### Testing Notifications

#### Using Firebase Console:

1. Go to Cloud Messaging: https://console.firebase.google.com/project/your-project/notification
2. Create new campaign
3. Select target app (LayerNews or LayerSports)
4. Send test message

#### Using FCM Verification Script:

```powershell
./verify-fcm-setup.ps1
```

This script checks:
- Google Services JSON configuration
- Firebase dependencies
- FCM service registration
- SHA fingerprints

#### Monitor FCM Logs:

```powershell
adb logcat -s CoreFCMService:D FirebaseMessaging:D
```

---

## 📄 License

This project is developed by Squad 03 for educational/commercial purposes.

---

## 👥 Team

**Squad 03** - Layer News Development Team

---

## 📚 Additional Resources

- [Firebase Documentation](https://firebase.google.com/docs/android)
- [Android Jetpack Guide](https://developer.android.com/jetpack)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Paging 3 Library](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

---

## 🐛 Troubleshooting

### Build Failures

```powershell
# Clear Gradle cache
./gradlew clean --no-daemon

# Clear Android Studio cache
# File → Invalidate Caches → Invalidate and Restart
```

### Firebase Connection Issues

```powershell
# Verify Firebase setup
./verify-fcm-setup.ps1

# Check logs
adb logcat -s FirebaseApp:D GoogleService:D
```

### Room Database Issues

```powershell
# Clear app data
adb shell pm clear com.exemplo.layer.news
adb shell pm clear com.exemplo.layer.sports
```

---------------------------------------------------------------

<div align="center">

Made with ❤️ by Squad 03

**[⬆ Back to Top](#layer-news--sports-app-)**

</div>

---------------------------------------------------------------
# Layer News & Sports App 📱


## Versão em Português



![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange.svg)
![Target SDK](https://img.shields.io/badge/Target%20SDK-36-orange.svg)

Um aplicativo moderno de notícias para Android com suporte a múltiplos flavors, 
integração com Firebase, notificações push e abordagem de arquitetura limpa.

</div>

---

## 📋 Índice

- [Visão Geral](#-visãogeral)
- [Funcionalidade](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Tecnologia](#-tecnologia)
- [Primeiros Passos](#-primeiros-passos)
- [Produto Flavors](#-produto-flavors)
- [Configuração Firebase](#-configuracao-firebase)
- [Build & Execução](#-build--execucao)
- [Notificações Push](#-notificacoes-push)
- [Soluções de Problemas](#-problemas-conhecidos)
- [Contribuição](#-contribuicao)

---

## 🌟 Visão Geral

Layer App é um agregador de notícias para Android construído com práticas modernas de desenvolvimento. Ele possui dois flavors de produto (News e Sports) com integração completa ao Firebase, incluindo autenticação, notificações push, analytics e banco de dados Firestore.

O app busca notícias de APIs externas, armazena em cache localmente usando Room com suporte a paginação e fornece notificações em tempo real com gerenciamento de expiração.

---

## ✨ Funcionalidades

- **📰 Feed de Notícias**: Navegue pelas principais manchetes com paginação
- **🔍 Busca**: Pesquise artigos em tempo real com debounce
- **🗂️ Filtros**: Filtre por categoria e ordem de classificação
- **🔔 Notificações Push**: Firebase Cloud Messaging com armazenamento local
- **🔐 Authenticação**: Firebase Authentication (Email/Senha)
- **💾 Supporte Offline**: Cache com Room e Paging 3
- **🎨 UI Moderna**: Material Design com Design System customizado
- **📊 Analytics**: Integração com Firebase Analytics 
- **🔥 Relatórios de Crash **: Firebase Crashlytics
- **🌐 Multi-Flavor**: Variantes separadas para Notícias e Esportes

---

## 🏗️ Arquitetura

Segue princípios de Clean Architecture com padrão MVVM, organizado em estrutura multi-módulo.

```
┌─────────────────────────────────────────────────────────────┐
│                         Main Module                         │
│                    (Application Entry)                      │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
┌────────▼────────┐ ┌───▼───────┐ ┌────▼──────────┐
│  Feature Modules │ │ Navigation │ │ Core Services │
├─────────────────┤ └────────────┘ └───────────────┘
│ • Authentication │                      │
│ • News          │◄─────────────────────┘
│ • Notifications  │
└──────────────────┘

Cada módulo é dividido em camadas:
┌────────────────────────────────────────┐
│ Presentation (View/ViewModel)          │
├────────────────────────────────────────┤
│ Domain (UseCase/Repository Interface)  │
├────────────────────────────────────────┤
│ Data (Repository Impl/Data Sources)    │
└────────────────────────────────────────┘
```

### Organização modular incluindo:

- **`:Main`** - módulo principal e configuração de flavors
- **`:feature:Authentication`** - Login, registro e recuperação de senha
- **`:feature:News`** - Feed de notícias, busca, filtros e detalhes
- **`:feature:Notifications`** - Lista de notificação 
- **`:navigation`** - Lógica centralizada de navegação
- **`:core:services`** - Serviços compartilhados (Rede, Database, Firebase, Analytics)

---

## 📁 Estrutura do projeto

```
squad03-layer-app-android/
│
├── Main/                           # Main application module
│   ├── src/
│   │   ├── main/                   # Common source
│   │   ├── LayerNews/              # News flavor specific
│   │   └── LayerSports/            # Sports flavor specific
│   ├── build.gradle.kts
│   └── google-services.json        # Firebase configuration
│
├── feature/                        # Feature modules
│   ├── Authentication/
│   │   └── src/main/.../
│   │       ├── presentation/       # UI (Activities, ViewModels)
│   │       ├── domain/             # Business logic (UseCases)
│   │       └── data/               # Data sources (Repository)
│   ├── News/
│   │   └── src/main/.../
│   │       ├── presentation/
│   │       │   ├── view/          # Activities & Adapters
│   │       │   └── viewModel/     # ViewModels
│   │       ├── domain/
│   │       │   ├── model/         # Domain models
│   │       │   ├── repository/    # Repository interfaces
│   │       │   └── usecase/       # Business use cases
│   │       └── data/
│   │           ├── local/         # Room entities & DAOs
│   │           ├── remote/        # API services
│   │           ├── paging/        # Paging 3 implementation
│   │           └── repository/    # Repository implementations
│   └── Notifications/
│
├── core/
│   └── services/                   # Core shared services
│       └── src/main/.../
│           ├── network/            # Retrofit, API services
│           ├── database/
│           │   ├── local/         # Room database, DAOs, entities
│           │   └── FirestoreService
│           ├── notification/       # FCM service, helpers
│           ├── authentication/     # Auth service
│           ├── analytics/          # Analytics service
│           └── storage/            # Preferences
│
├── navigation/                     # Navigation module
│   └── src/main/.../
│       ├── Navigator.kt            # Centralized navigation
│       └── routes/                 # Navigation routes
│
├── gradle/
│   └── libs.versions.toml          # Version catalog
│
├── build.gradle.kts                # Root build file
├── settings.gradle.kts             # Module configuration
├── FCM-RELEASE-FIX.md              # FCM troubleshooting guide
└── README.md                       # This file
```

---

## 🛠️ Tecnologias 

### Core Technologies

- **Linguagem**: Kotlin 2.1.10
- **Sistema**: Gradle with Kotlin DSL & Version Catalogs
- **SDK Minima**: 26 (Android 8.0)
- **SDK**: 36

### Android Jetpack

- **Navigation Component** 2.9.7 - Fragment navigation
- **Room** 2.8.4 - Local database with Paging support
- **Paging 3** 3.4.0 - Pagination with RemoteMediator
- **Lifecycle** 2.10.0 - ViewModel & LiveData
- **ViewBinding** - Type-safe view access

### Dependency Injection

- **Hilt** 2.54 - Dependency injection framework
- **KSP** 2.1.10 - Annotation processing

### Firebase Services

- **Firebase BOM** 34.8.0
  - **Authentication** - User authentication
  - **Cloud Messaging (FCM)** - Push notifications
  - **Firestore** - Cloud database
  - **Crashlytics** - Crash reporting
  - **Analytics** - User analytics
  - **Remote Config** - Remote configuration

### Networking & Data

- **Retrofit** 3.0.0 - HTTP client
- **Gson** 3.0.0 - JSON serialization
- **Glide** 4.16.0 - Image loading

### UI

- **Material Design** 1.20.0
- **ConstraintLayout** 2.2.1
- **Custom Design System** (v1.0.20) - From `rafaelKontein23.designe-system-news`
- **Shimmer** 0.5.0 - Loading effect

### Testing

- **JUnit** 4.13.2
- **Espresso** 3.7.0
- **AndroidX Test** 1.3.0

---

## 🚀 Primeiros passos

### Pré-requsitos

- **Android Studio** Ladybug | 2024.2.1 or newer
- **JDK** 11 or higher
- **Android SDK** with API level 36
- **Git**

### Clonar o repositório

```powershell
git clone https://github.com/your-org/squad03-layer-app-android.git
cd squad03-layer-app-android
```

### Configuração do Firebase

1. Crie dois projetos Firebase ou um com multiplos apps:
   - `com.exemplo.layer.news`
   - `com.exemplo.layer.sports`

2. Baixe o arquivo `google-services.json` e o coloque na pasta `Main/`  

3. Adicione SHA fingerprints no Console Firebase (veja [Firebase Setup](#-firebase-setup))

### Sincronizando o Projeto

Abra o projeto no Android Studio e sincronize o Gradle:

```powershell
./gradlew --refresh-dependencies
```

---

## 🎯 Produto Flavors

O app suporta dois produtos flavors:

| Flavor | Applicação ID | Proposito |
|--------|---------------|---------|
| **LayerNews** | `com.exemplo.layer.news` | Aplicativo de notícias em geral |
| **LayerSports** | `com.exemplo.layer.sports` | Aplicativo de notícias focado em esportes |

Cada flavor possui:
- Pacotes com nomes diferentes
- Projetos independentes no Firebase
- Recursos customizados e configuraçõpes
- Vensionamento independente 

### Arquivos específicaos de cada Flavor

```
Main/
├── src/
│   ├── main/                    # Common code
│   ├── LayerNews/              # News-specific resources
│   │   └── res/
│   │       ├── values/
│   │       └── drawable/
│   └── LayerSports/            # Sports-specific resources
│       └── res/
│           ├── values/
│           └── drawable/
```

---

## 🔥 Configuração do Firebase 

### Configuração Inicial

1. **Crie um Firebase Project**: https://console.firebase.google.com/

2. **Adicione um Android Apps**:
   - Adicione um app com o pacote `com.exemplo.layer.news`
   - Adicione um app com o pacote `com.exemplo.layer.sports`

3. **Baixando a configuração**:
   - Baixe o arquivo `google-services.json`
   - Coloque ele no local: `Main/google-services.json`

### SHA Fingerprints (Critical for Release)

⚠️ **Importante**: Para que o DCM funcione em versões de lançamento, é necessário registrar tanto SHA de depuração quando a de lançamento.

#### Obter sua SHA de depuração:

```powershell
keytool -list -v -keystore $env:USERPROFILE\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

#### Obter SHA de depuração:

```powershell
keytool -list -v -keystore release-keystore.jks -alias layer_key
```

Suas SHA fingerprints:
```
SHA-1:   
SHA-256: 
```

#### Registre no Firebase Console:

1. Vá em Project Settings → Your Apps
2. Para **cada app** (LayerNews e LayerSports):
   - Clique "Add fingerprint"
   - Adicione as duas SHA de depuração: SHA-1 e SHA-256
   - Adicione as duas SHA de depuração: SHA-1 e SHA-256
3. Baixe o arquivo atualizado `google-services.json`

### Ativando o Firebase Services

No Firebase Console:

1. **Authenticação**:
   - Habilitar provedor de Email/Password 
   - Configurar métodos de login

2. **Mensagens na Nuvem**:
   - Nenhuma configuração adicional é necessária (ativado automaticamente)

3. **Firestore Database**:
   - Criar banco de dados em modo de produção
   - Configurar regras de segurança

4. **Crashlytics**:
   - Habilitar o Crashlytics no painel de controle

---

## 🔨 Build & Execução

### Comandos de execução

```powershell
# Limpar o  build
./gradlew clean

# Criar APKs de depuração
./gradlew assembleDebug

# Criar versão de APK de depuração para cada flavor
./gradlew assembleLayerNewsDebug
./gradlew assembleLayerSportsDebug

# Criar APK de lançamento
./gradlew assembleRelease

# Criar versão de APK de lançamento para cada flavor
./gradlew assembleLayerNewsRelease
./gradlew assembleLayerSportsRelease

# Instalar a versão de depuração no dispositivo
./gradlew installLayerNewsDebug

# Rodar os testes
./gradlew test
./gradlew connectedAndroidTest
```

### Rodar no Android Studio

1. Selecione variante: `Build → Select Build Variant`
   - `layerNewsDebug`
   - `layerNewsRelease`
   - `layerSportsDebug`
   - `layerSportsRelease`

2. Clique em Run (Shift+F10)

### Local das APKs

 APKs estão localizadas em:
 - debug para depuração
 - release para lançamento
```
Main/build/outputs/apk/
├── LayerNews/
│   ├── debug/
│   │   └── Main-LayerNews-debug.apk
│   └── release/
│       └── Main-LayerNews-release.apk
└── LayerSports/
    ├── debug/
    │   └── Main-LayerSports-debug.apk
    └── release/
        └── Main-LayerSports-release.apk
```

---

## 🔔 Notificações Push 

### Arquitetura

O app usa Firebase Cloud Messaging (FCM) com uma implementação customizada:

```
CoreFirebaseMessagingService (core:services)
    ↓
Recebe a notificação
    ↓
Salva no Room database (NotificationEntity)
    ↓
Exibe notificação do sistema
    ↓
Usuário abre a notificação
    ↓
NotificationsActivity exibe a notificação do banco de dados
```

### Funcionalidades

- **Armazenamento Local**: Notificações salvas no banco de dados do Room 
- **Gerenciamento de Expiração**: Expira automaticamente notitificações antigas
- **Links Diretos**: Abre contéudo específico a partir das notificações
- **Gerenciamento de Permissões**: Permissões de notificação em tempo de execução (Android 13+)

### Testando Notificações

#### Usando Firebase Console:

1. Acesse o Cloud Messaging: https://console.firebase.google.com/project/your-project/notification
2. Crie uma nova campanha
3. Selecione o aplicativo de destino(LayerNews or LayerSports)
4. Envie uma mensagem de teste

#### Usando o FCM Verification Script:

```powershell
./verify-fcm-setup.ps1
```

Esse script checa:
- Configuração do Google Services JSON 
- Dependências do Firebase 
- Registro de serviço FCM 
- SHA fingerprints

#### Monitorar registros do FCM:

```powershell
adb logcat -s CoreFCMService:D FirebaseMessaging:D
```

---

## 📄 Licença

Projeto desenvolvido pela Squad 03 para fins educacionais e/ou comerciais.

---

## 👥 Time

**Squad 03** - Layer News Development Team

---

## 📚 Recursos Adicionais

- [Documentação_Firebase](https://firebase.google.com/docs/android)
- [Guia do Android Jetpack](https://developer.android.com/jetpack)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Paging 3 Library](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

---

## 🐛 Solução de problemas

### Falhas de Build

```powershell
# Limpar o Gradle cache
./gradlew clean --no-daemon

# Limpar o Android Studio cache
# File → Invalidate Caches → Invalidate and Restart
```

### Problemas de Conexão do Firebase 

```powershell
# Verifica a configuração do Firebase 
./verify-fcm-setup.ps1

# Checa os logs
adb logcat -s FirebaseApp:D GoogleService:D
```

### Problemas com o banco de dados

```powershell
# Limpar o app data
adb shell pm clear com.exemplo.layer.news
adb shell pm clear com.exemplo.layer.sports
```

---------------------------------------------------------------

<div align="center">

Feito com ❤️ pela Squad 03

**[⬆ Voltar ao topo](#layer-news--sports-app-)**



 
