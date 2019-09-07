# react-native-local-api

## Getting started

`$ npm install react-native-local-api --save`

### Mostly automatic installation

`$ react-native link react-native-local-api`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-local-api` and add `LocalApi.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libLocalApi.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.LocalApiPackage;` to the imports at the top of the file
  - Add `new LocalApiPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-local-api'
  	project(':react-native-local-api').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-local-api/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-local-api')
  	```


## Usage
```javascript
import LocalApi from 'react-native-local-api';

// TODO: What to do with the module?
LocalApi;
```
