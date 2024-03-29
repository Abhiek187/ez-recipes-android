fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android test

```sh
[bundle exec] fastlane android test
```

Run unit tests

### android ui_test

```sh
[bundle exec] fastlane android ui_test
```

Run instrumented tests

### android screenshots

```sh
[bundle exec] fastlane android screenshots
```

Generate screenshots for the Play Store

### android sync_metadata

```sh
[bundle exec] fastlane android sync_metadata
```

Sync metadata with the Play Store

### android internal

```sh
[bundle exec] fastlane android internal
```

Submit a new internal build to the Play Store

### android deploy

```sh
[bundle exec] fastlane android deploy
```

Deploy a new version to the Play Store

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
