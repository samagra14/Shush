# Shush
[![platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16s)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![by-SDS-MDG](https://img.shields.io/badge/by-SDS%2C%20MDG-blue.svg)](https://mdg.sdslabs.co)

Shush Me is an intelligent manager for your phone’s ringer. It manages the status of your ringer based on your location and /or your schedule. Now, gone are the days when you were caught by your teacher because of your phone.


## Features
 1. Manage your ringer on the basis of  your location.
 1. Manage your ringer on the basis of your schedule.
 1. When you turn your ringer off for a movie, meeting, meal, or nap, this app turns it back on afterwards.
 1. Set your phone's weekly ring schedule.
 1. Select places at which your mobile phone should not ring.
 1. Add exceptions for important callers;
 1. Add a temporary location so that the ringer is silenced for that particular place or time

## Getting Started
These instructions will get you a copy of the project up and running.

#### Prerequisites
* Android Studio
* Google Play Repository

#### Setting up the API key
  * Go to the [Google API console](https://accounts.google.com/signin/v2/identifier?service=cloudconsole&passive=1209600&osid=1&continue=https%3A%2F%2Fconsole.developers.google.com%2Fapis%2Fcredentials%3Fproject%3Dshush-1501523759825&followup=https%3A%2F%2Fconsole.developers.google.com%2Fapis%2Fcredentials%3Fproject%3Dshush-1501523759825&flowName=GlifWebSignIn&flowEntry=ServiceLogin) and register yourself
  for google places API key restricted to android. The instructions can be found [here](https://developers.google.com/places/android-api/signup).
  * After registering your API key, make a file named ` google_places_api.xml ` <br>
  at ` ./Shush/app/src/main/res/values`
  * Paste the following code in google_places_api.xml
    ```
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="your_google_places_api_key">YOUR_KEY_HERE</string>
    </resources>
    ```
  * Replace `YOUR_KEY_HERE` with your registered places api key.
  * You are good to go.
## How you can help

* Make us aware of the imperfections and bugs by opening [issues](https://github.com/samagra14/Shush/issues)
* Join our [Chat Room](https://mdg.sdslabs.co/chat) and introduce yourself, please ask any general questions here -- how the product is supposed to work, usability issues, technical questions, whatever you want to know, we want to hear about.  If you are interested in collaborating with us, let us know.
* Read our [Contribution Guide](CONTRIBUTING.md) -- everything you do with us is a contribution to the public domain, there are also some tips for getting started
* Dive into our [existing issues](https://github.com/samagra14/Shush/issues) -- there are both dev and design opportunities there



## License

Shush is registered under MIT License.

For more information, see [license](LICENSE.md).

