# ez-recipes-android

[![Fastlane](https://github.com/Abhiek187/ez-recipes-android/actions/workflows/fastlane.yml/badge.svg)](https://github.com/Abhiek187/ez-recipes-android/actions/workflows/fastlane.yml)

Easy recipes finder Android app

## Fastlane

Follow the [docs](https://docs.fastlane.tools/getting-started/android/setup/) to setup Fastlane on Android.

```bash
cd EZRecipes
bundle config set --local path 'vendor/bundle'
bundle install
bundle exec fastlane android test
bundle exec fastlane android ui_test api:API_LEVEL
```
