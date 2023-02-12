# EZ Recipes Android App

[![Fastlane](https://github.com/Abhiek187/ez-recipes-android/actions/workflows/fastlane.yml/badge.svg)](https://github.com/Abhiek187/ez-recipes-android/actions/workflows/fastlane.yml)

<div>
    <img src="screenshots/phone/home-screen-1.png" alt="find recipe button" width="300">
    <img src="screenshots/phone/home-screen-2.png" alt="navigation drawer" width="300">
    <img src="screenshots/phone/recipe-screen-1.png" alt="recipe header" width="300">
    <img src="screenshots/phone/recipe-screen-2.png" alt="nutrition label" width="300">
    <img src="screenshots/phone/recipe-screen-3.png" alt="summary box" width="300">
    <img src="screenshots/phone/recipe-screen-4.png" alt="ingredients list" width="300">
    <img src="screenshots/phone/recipe-screen-5.png" alt="step cards & footer" width="300">
</div>

## Overview

Cooking food at home is an essential skill for anyone looking to save money and eat healthily. However, learning how to cook can be daunting, since there are so many recipes to choose from. And even when meal prepping, knowing what ingredients to buy, what equipment is required, and the order of steps to make the meal can be hard to remember for many different recipes. Plus, during busy days, it's nice to be able to cook up something quick and tasty.

Introducing EZ Recipes, an app that lets chefs find low-effort recipes that can be made in under an hour, use common kitchen ingredients, and can produce multiple servings. On one page, chefs can view what the recipe looks like, its nutritional qualities, the total cooking time, all the ingredients needed, and step-by-step instructions showing what ingredients and equipment are required per step. Each recipe can be shared so other chefs can learn how to make the same recipes.

## Features

- Android app created using Jetpack Compose and MVVM architecture
- Material Design UI
- Responsive and accessible mobile design
- REST APIs to a custom [server](https://github.com/Abhiek187/ez-recipes-server) using Retrofit, which fetches recipe information from [spoonacular](https://spoonacular.com/food-api)
- App Links to open recipes from the web app to the mobile app
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

### Deployment

```mermaid
flowchart LR

A --> B
B --> C

subgraph A [Package App]
direction TB
D(Sync local metadata from Google Play) --> E(Write release notes for the next version code)
E --> F{Major, minor, or patch update?}
F --> G(Update the version name and increment the version code)
G --> H(Clean cache)
H --> I(Build app & generate an AAB)
I --> J(Sign AAB using the upload key)
end

subgraph B [Play App Signing]
direction TB
K(Verify signer's identity using the upload certificate) --> L(Generate APKs optimized for each device configuration)
L --> M(Sign APK using the signing key)
end

subgraph C [Distribute on Google Play]
direction TB
N(Test app in the internal track) --> O(Promote release to production)
O --> P(Await approval from Google)
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
chmod u+x gradlew
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

### Deployment

Follow the steps on [Fastlane's docs](https://docs.fastlane.tools/getting-started/android/setup/#setting-up-supply) to generate a private key to connect to the Google Play Developer API. Validate the connection by running:

```bash
bundle exec fastlane run validate_play_store_json_key json_key:JSON_KEY_PATH
```

Then follow these steps to create a new release for select testers in the internal track:

1. Make sure the `fastlane/metadata` directory is up-to-date by running `bundle exec fastlane android sync_metadata`
2. Write the release notes for the next version code in `fastlane/metadata/android/en-US/changelogs`, where the filename is `VERSION_CODE.txt`. `VERSION_CODE` is the latest version code + 1.
3. Run `bundle exec fastlane android internal` and select whether this is a major, minor, or patch update. The version name and code will be adjusted in the app's `build.gradle` file accordingly.

Once the internal build is tested and ready for production, run `bundle exec fastlane android deploy` to promote the internal release to the production track. Send the changes for approval on the Google Play Console and wait for Google to approve the app (usually a few days to a week on average).

## Future Updates

Check the [EZ Recipes web repo](https://github.com/Abhiek187/ez-recipes-web#future-updates) for a list of future updates.

## Related Repos

- [Web app](https://github.com/Abhiek187/ez-recipes-web)
- [iOS app](https://github.com/Abhiek187/ez-recipes-ios)
- [Server](https://github.com/Abhiek187/ez-recipes-server)
