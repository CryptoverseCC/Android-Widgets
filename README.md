# android-ads-sdk

SDK with view to display ads.

## Setup

### Request API key

Go to https://api.userfeeds.io/portal/apis/, register and receive key.

### Add dependency

```groovy
dependencies {
    compile 'io.userfeeds.ads.sdk:core:<latest version here>'
}
```

// TODO: upload to maven central
You may find the latest version number by going to maven central search.

### Initialize SDK

You will usually init the SDK in a class extending Application.

```
class MyAwesomeApp : Application() {

    override fun onCreate() {
        UserfeedsSdk.initialize(apiKey = "<your API key>")
    }
}
```

## Use

// TODO
