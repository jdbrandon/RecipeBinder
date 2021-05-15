# RecipeBinder
An [android app](https://play.google.com/store/apps/details?id=com.jeffbrandon.recipebinder) for keeping recipes.

## Features
- Catalog and search for recipes based on name and categorization tags
  - Tags based on
    - Cooking style
    - Type of dish
    - Tools used in preparation
    - Dietary restrictions
    - Time and complexity
- Share recipes with friends with easy recipe exporting via text (or QR code if they use the app too)
- Easy to use responsive user interface
  - re-order ingredient/instructs for a recipe with simplicity of drag and drop
- Converts between measurement units with the press of a button
- Data stored locally (It's basically a fancy notepad)
  - Note: this does mean your data will not transfer if you download this app on a new device

## Tools utilized
- [Room](https://developer.android.com/jetpack/androidx/releases/room) database
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) / [Dagger2](https://dagger.dev) dependency injection
- [View Binding](https://developer.android.com/topic/libraries/view-binding)
- [Jenkins](https://jenkins.io) Continuous integration
- [Material](https://material.io) Design components and practices
- [AppCompat](https://developer.android.com/jetpack/androidx/releases/appcompat) Compatibility library
- [zxing](https://github.com/zxing/zxing) QR code generation
- [Timber](https://github.com/JakeWharton/timber) Logging library
- [Moshi](https://github.com/square/moshi) json serialization

### Note on Firebase integration
I use [Firebase](https://firebase.google.com/) to track issues occurring with the app in the wild.
I have left the required `google-services.json` file out of this repository, to avoid exposing api 
keys someone might use to pollute my data. 

*This will cause build failures.*

To get the app building, there are two options:
1. Create your own project on Firebase and generate your own `google-services.json` and place it in
the app directory: `RecipeBinderApp`
1. Remove firebase integrations from the project and app `build.gradle`, they are not necessary to
build and run the app. The easiest way to do this is to run the following command:
    ```bash
    git revert 7328082b0bab7cd1926e9e5f3060499bb8032e12
    ```
1. _Optional_: If you are using continuous integration, one may wish to edit
[Jenkinsfile](/Jenkinsfile) to remove the step in checkout that reverts the firebase dependencies
that will otherwise cause build failures
