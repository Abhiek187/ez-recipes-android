# EZ Recipes Android App

[![Fastlane](https://github.com/Abhiek187/ez-recipes-android/actions/workflows/fastlane.yml/badge.svg)](https://github.com/Abhiek187/ez-recipes-android/actions/workflows/fastlane.yml)

## Overview

Cooking food at home is an essential skill for anyone looking to save money and eat healthily. However, learning how to cook can be daunting, since there are so many recipes to choose from. And even when meal prepping, knowing what ingredients to buy, what equipment is required, and the order of steps to make the meal can be hard to remember for many different recipes. Plus, during busy days, it's nice to be able to cook up something quick and tasty.

Introducing EZ Recipes, an app that lets chefs find low-effort recipes that can be made in under an hour, use common kitchen ingredients, and can produce multiple servings. On one page, chefs can view what the recipe looks like, its nutritional qualities, the total cooking time, all the ingredients needed, and step-by-step instructions showing what ingredients and equipment are required per step. Each recipe can be shared so other chefs can learn how to make the same recipes.

## Features

- Android app created using Jetpack Compose and MVVM architecture
- Material Design UI
- Responsive and accessible mobile design
- REST APIs to a custom [server](https://github.com/Abhiek187/ez-recipes-server), which fetches recipe information from [spoonacular](https://spoonacular.com/food-api)
- Automated testing and deployment using CI/CD pipelines in GitHub Actions and Fastlane
- Mermaid to write diagrams as code

## Pipeline Diagrams

### Unit Tests

```mermaid
flowchart LR

A(Checkout repository) --> B(Install Java 11)
B --> C(Make gradlew executable:\nchmod u+x gradlew)
C --> D(Install Fastlane)
D --> E(Run unit tests:\ngradlew test -p .)
```

### Instrumented Tests

```mermaid
flowchart LR

A(Checkout repository) --> B(Install Java 11)
B --> C(Make gradlew executable:\nchmod u+x gradlew)
C -->|API 29, 31, 33| D
D --> E(Upload build reports)

subgraph D [Run instrumented tests]
direction TB
F(Accept licenses) --> G(Install build tools, platform tools, and platforms)
G --> H(Install emulator)
H --> I(Install system images)
I --> J(Create test AVD)
J --> K(Configure AVD with 2 cores)
K --> L(Boot up the emulator)
L --> M(Press the menu button)
M --> N(Disable animations)
N --> O(Run instrumented tests:\ngradlew connectedAndroidTest)
O --> P(Kill emulator)
end
```

## Installing Locally

Android Studio and Java are required to run Android apps locally.

1. [Clone](https://github.com/Abhiek187/ez-recipes-android.git) this repo.
2. Open `EZRecipes` in Android Studio.
3. Build the project using Gradle.
4. Run the **app** configuration.

The recipes will be fetched from the EZ Recipes server hosted on https://ez-recipes-server.onrender.com/api/recipes. To connect to the server locally, follow the directions in the [EZ Recipes server repo](https://github.com/Abhiek187/ez-recipes-server#installing-locally) and change `RECIPE_BASE_URL` under `Constants.kt` to `http://10.0.2.2:5000/api/recipes/`. (`10.0.2.2` points to `localhost` on the development machine. Since the Android emulator is a virtual machine, `127.0.0.1` points to `localhost` on the emulator instead of the development machine.)

To allow `http://` connections, add `android:usesCleartextTraffic="true"` to the `<application>` tag in `AndroidManifest.xml`. Make sure not to keep this enabled since it will make the Android app insecure.

### Testing

Unit and instrumented tests can be run directly from Android Studio or through the command line using Fastlane. Follow the [docs](https://docs.fastlane.tools/getting-started/android/setup/) to setup Fastlane on Android. In addition, run the following to install all dependencies locally:

```bash
cd EZRecipes
bundle config set --local path 'vendor/bundle'
bundle install
```

For unit tests, run the following command:

```bash
bundle exec fastlane android test
```

For instrumented tests, run the following command, where API_LEVEL is the Android API version to install on the emulator:

```bash
bundle exec fastlane android ui_test api:API_LEVEL
```

This [table](https://source.android.com/docs/setup/about/build-numbers#platform-code-names-versions-api-levels-and-ndk-releases) shows which API level corresponds with each Android release.

### Screenshots

Screenshots can be generated automatically using Fastlane. Run the following command to generate screenshots at `ez-recipes-android/EZRecipes/fastlane/screenshots` (ignored by git):

```bash
bundle exec fastlane android screenshots
```

Make sure a device is running by checking `adb devices`.

## Future Updates

Check the [EZ Recipes web repo](https://github.com/Abhiek187/ez-recipes-web#future-updates) for a list of future updates.
