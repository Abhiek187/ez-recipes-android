# Agent Instructions

## Project Overview

This is an Android app that lets users learning to cook to search, view, and save a variety of recipes. The UI is built using Jetpack Compose and follows Google's Material Design guidelines. It connects to a backend service called EZ Recipes Server that routes all API requests to the appropriate services. API calls are handled using Retrofit. The CI/CD pipeline is handled using Fastlane & GitHub Actions. Finally, the app is deployed to the Play Store using a signed, minified app bundle.

App versioning should be automated where possible. Use semantic versioning for the version name and increment the version code with every signed build uploaded to the Play Store. App builds should be deployed to an internal test track to check for any prod-specific issues before being promoted to a production release. Each release requires writing release notes under the Fastlane changelogs directory, named after the version code. The release notes must stay within the Play Store's 500-character limit and shouldn't go into technical detail. Instead, they should give a high-level summary of changes made since the last release that can be understood by the average user.

Before writing any code, review the existing architecture and propose your implementation plan for approval by the user. If something is uncertain, always prefer asking for clarity over making any assumptions. If you're not sure how to implement a solution, it's ok to be honest and admit it to the user.

## Build and Test Commands

Before committing changes, make sure the following commands succeed:

```bash
# Test
bundle exec fastlane android test
# Build
./gradlew clean bundleRelease
```

The test command only runs unit tests. Instrumented tests should be run manually by the user by running `./gradlew connectedAndroidTest`, though they can be flaky.

The build command generates an unsigned AAB. It's important to check for any warnings produced by ProGuard during the minification step. When the app is ready for release, users will need to generate a signed AAB manually by inputting the Keystore credentials.

## Code Style Guidelines

This project should follow Google's best practices for Jetpack Compose apps. Do not create any UI using XML views, unless a feature isn't available in Jetpack Compose. The code should follow MVVM architecture, with a clear separation between the UI and business logic. All user-facing strings should be localized and included in string resources in case the app should support multiple languages. Make sure to account for different string variants, such as pluralization or device types.

The UI must be designed to be responsive on various screen sizes and accessible to all users. The UI should match what users expect for native Android apps and follow Material Design best practices. Any changes should be compatible with the min SDK version (23 or later) on Android phones, tablets, and foldables. Small features that require the latest Android version may be used with version checks. Ensure the app is efficient on mobile devices, balancing performance with memory and power management. Avoid memory leaks or ANRs on the UI. Make sure the app's state is retained upon configuration changes, such as rotating the screen, folding the screen, or switching between light and dark mode. ViewModels and saved state will help in this regard.

For every composable in Jetpack Compose, include previews that test the UI under different states. Additional composables can be created to break up a large composable or make certain sections more reusable. The app should support light/dark mode, portrait/landscape mode, and small/large font sizes. Ensure the keyboard is optimized for certain inputs, such as email addresses or numbers. The user must validate the styling on their end to make sure the app looks neat and presentable for the end user. Provide some guidance on things to check, such as color contrast or accessibility labels via TalkBack. If you're able to analyze screenshots, you may suggest that the user share some screenshots of the app to provide feedback.

Follow the best practices for writing Kotlin code. No Java code should be used unless something can't be coded in Kotlin. Avoid writing code that can risk the app crashing. Handle all potential crash scenarios gracefully so the app can continue running efficiently. Do not use !! to assert non-null values. Always prefer using ? or let statements to check for null values. Comments can be used to explain more complicated code, but shouldn't be overused if the code is self-explanatory. Make sure test logs and unused imports are removed prior to committing changes. Android Studio warnings must be kept to a minimum. Commented or unused code should be removed unless the user intends to reference it in the future. Avoid using deprecated code, or replace any existing deprecations as long as the minimum SDK is respected. Avoid using experimental features where possible. Always consider edge cases when implementing features. Above anything else, make sure the functionality is understandable to someone reading the code.

Data can be stored locally using DataStore or Room. DataStore should be used for simple key-value pairs, like settings. Room should be used for more complex, relational data. The Keystore should be used to encrypt sensitive credentials, such as the JWT shared by the server.

When you write commit messages, prepend them with 🤖 so it's clear the changes were made with AI. When you raise PRs, make sure to disclose the AI tool used. All changes must be made to a feature branch and then merged to main via a PR.

When working with the user, ensure you follow all guidelines for ethical AI, such as keeping the human in the loop, taking accountability for changes, and being transparent about the thought process and where you retrieve your ideas from.

## Testing Instructions

Helper methods should be unit tested where possible with reasonable coverage. Mock any external dependencies needed for unit tests.

Write instrumented tests for journeys that mimic how real users would use the app. Since these kinds of tests take longer and can be flaky, don't overrely on them. Utilize Fastlane's Screengrab to take screenshots as the instrumented test is running. This will help when gathering screenshots for the Play Store.

In addition to running the test command above, ensure the user tests the app running locally on various devices, both on the emulator and physical devices. Ideally, the user should test that the app works on phones, tablets, and foldables across different Android versions. Foldables can be tested at compact, medium, and expanded screen widths.

Before each release, the user should test a signed release build to make sure it runs properly and doesn't crash. If there are prod-specific issues, refer to the ProGuard rules to see if anything needs to be updated. For example, GSON data mappings or models should not be obfuscated to preserve the server's JSON schema. Provide any other suggestions to ensure the app is compliant for publishing on the Play Store.

## Security Considerations

This is a frontend app. Therefore, NO secrets should be managed in this repo since mobile apps can be reverse-engineered. Secrets should be delegated to EZ Recipes Server. If the user has to enter sensitive credentials, such as passwords, they should be masked, but provide an option to view what they're entering to ensure they're typing things properly. Consider also if a feature on the app requires the user to be authenticated first. If so, utilize the Keystore to manage the user's token. Keep in mind any security features specific to certain Android devices or those incompatible with an emulator.

While the server will validate all inputs, it's still good practice to validate inputs on the client side. This way, users can receive immediate feedback on their inputs before sending them to the server.

Use Android's Log library for structured logs that should be saved on the user's device. All API requests and responses should be logged for auditing purposes, but sensitive information like passwords, cookies, or API keys should be masked in the logs. Important transactions can be logged as well, but don't make logs excessive when it comes time to search logs to troubleshoot bugs with the app. If an API errors out, make sure to provide a user-friendly message explaining what went wrong. Don't go into too much technical detail, especially if the error exposes information that would benefit malicious actors, such as if the username is correct, but the password is invalid.

Use Gradle with Kotlin DSL to manage dependencies. Group dependencies that share the same version for ease of update. Prefer using BOMs if available for a particular library. All dependencies should be kept up-to-date to minimize any vulnerabilities. Any new packages added to this project should be regularly updated and not abandoned after several years or contain lingering vulnerabilities. If a feature can be implemented trivially without introducing another dependency, that's preferred. Minimize the number of permissions required to run the app, especially if they're dangerous.
