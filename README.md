
# react-native-ark-audio-player

## Features

- Background audio streaming of remote stream
- Control via sticky notification on android and media center on iOS
- Shoutcast/Icy meta data support
- Simple UI player component (if needed, an api to control the sound is available)

If you are only looking to play local audio file with app in foreground, please [see other audio libs](https://github.com/tlenclos/react-native-ark-audio-player/blob/master/README.md#other-rn-audio-projects).

![Demo iOS](https://raw.githubusercontent.com/tlenclos/react-native-ark-audio-player/master/demo_ios.gif)
![Demo android](https://raw.githubusercontent.com/tlenclos/react-native-ark-audio-player/master/demo_android.gif)

## Getting started

`$ npm install react-native-ark-audio-player --save`

### Mostly automatic installation

`$ react-native link react-native-ark-audio-player`

Go to `node_modules` ➜ `react-native-ark-audio-player` => `Pods` and drag/drop `Pods.xcodeproj` to the Libraries folder in your XCode project.

In XCode, in the project navigator, select your project. Add `libReactNativeAudioPlayer.a` and `libStreamingKit.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-ark-audio-player` => `ios`
   - run `pod install` to download StreamingKit dependency
   - add `ReactNativeAudioPlayer.xcodeproj` to the Libraries folder in your XCode project
   - add `Pods/Pods.xcodeproj` to the Libraries folder in your XCode project
3. In XCode, in the project navigator, select your project. Add `libReactNativeAudioPlayer.a` and `libStreamingKit.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.audioStreaming.ReactNativeAudioPlayerPackage;` to the imports at the top of the file
  - Add `new ReactNativeAudioPlayerPackage()` to the list returned by the `getPackages()` method
  If you're using Android 23 or above
  - Add `new ReactNativeAudioPlayerPackage(MainActivity.class)` to he list returned by the `getPackages()`method instead.
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-ark-audio-player'
  	project(':react-native-ark-audio-player').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-ark-audio-player/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-ark-audio-player')
  	```

## Usage

### iOS only : udate the Info.plist file of your project and add background mode

```xml
    <key>UIBackgroundModes</key>
    <array>
    <string>audio</string>
```

### Playing sound (similar code used by the player UI)

```javascript
import { ReactNativeAudioPlayer } from 'react-native-ark-audio-player';

const url = "http://lacavewebradio.chickenkiller.com:8000/stream.mp3";
ReactNativeAudioPlayer.pause();
ReactNativeAudioPlayer.resume();
ReactNativeAudioPlayer.play(url, {showIniOSMediaCenter: true, showInAndroidNotifications: true});
ReactNativeAudioPlayer.stop();
```

For more information see the Example app.

### Player UI

```javascript
import { Player } from 'react-native-ark-audio-player';

class PlayerUI extends Component {
  render() {
    return (
        <Player url={"http://lacavewebradio.chickenkiller.com:8000/stream.mp3"} />
    );
  }
}
```

## TODO

- [X] Allow to play local files
- [ ] Allow to specify custom style for the android notification (maybe a custom view ?)
- [ ] Allow to specify custom styles for the player
- [ ] Handle artwork of artist
- [ ] Add tests

## Other RN audio projects

- [jsierles/react-native-audio](https://github.com/jsierles/react-native-audio) to play local audio and record
- [zmxv/react-native-sound](https://github.com/zmxv/react-native-sound) to play local audio with more controls

## Credits

- Android version based on the work of @EstebanFuentealba https://github.com/EstebanFuentealba/react-native-android-audio-player-aac
- iOS version based on the work of @jhabdas https://github.com/jhabdas/lumpen-radio

See also the list of [contributors](https://github.com/tlenclos/react-native-ark-audio-player/graphs/contributors) who participated in this project.

## Contribute

Since symlink support is [still lacking](https://github.com/facebook/react-native/issues/637) on React Native, I use the [wml](https://github.com/wix/wml) cli tool created by the nice folks at wix.

`wml add ~/react-native-ark-audio-player ~/react-native-ark-audio-player/Example/node_modules/react-native-ark-audio-player`

## [Changelog](https://github.com/tlenclos/react-native-ark-audio-player/blob/master/CHANGELOG.md)

## License

This project is licensed under the MIT License - see the LICENSE file for details
