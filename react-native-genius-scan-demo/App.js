/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import React, { Component } from 'react';
import {
  StyleSheet,
  CheckBox,
  Text,
  Button,
  View,
  Image,
  Switch,
  Platform,
} from 'react-native';
import RNGeniusScan from '@thegrizzlylabs/react-native-genius-scan';
import Share from 'react-native-share';
import { YellowBox } from "react-native";

// react-native-share v1.0.23 has unexpected warnings  (https://github.com/react-native-community/react-native-share/issues/329),
// but later version has some issues with Android 8 (https://github.com/react-native-community/react-native-share/issues/200),
// so we just hide warnings for now as this is just a demonstration.
YellowBox.ignoreWarnings([
  "Class GenericShare",
  "Class GooglePlusShare",
  "Class WhatsAppShare",
  "Class InstagramShare"
]);


export default class App extends Component {
  state = {
  }

  render() {
    // Refer to the Genius Scan SDK demo README.md for a list of the available options
    const configuration = {
      source: 'camera',
    }
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          GS SDK React Native Demo
        </Text>
        <View>
          <View style={styles.button}>
            <Button
              onPress={() => {
                RNGeniusScan.scanWithConfiguration(configuration)
                  .then((result) => {
                    // Here you can get the pdf file and the scans from the result
                    // object.
                    // As an example, we show here how you can share the resulting PDF:
                    console.log(result);
                    const shareOptions = { url: result.pdfUrl };
                    console.log(shareOptions);
                    Share.open(shareOptions)
                      .then((res) => { console.log(res) })
                      .catch(e => alert(e));
                  })
                  .catch(e => alert(e))
              }}
              title="Start scanning"
            />
          </View>
        </View>

      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  controls: {
    flexDirection: 'column',
    alignItems: 'center'
  },
  button: {
    margin: 5
  }
});