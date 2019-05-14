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
    image: null,
    originalImage: null,
    bwFilter: false
  }

  render() {
    const scanOptions = this.state.bwFilter ? {defaultEnhancement: RNGeniusScan.ENHANCEMENT_BW} : {};
    const ToggleComponent = Platform.OS === 'ios' ? Switch : CheckBox
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          GS SDK React Native Demo
        </Text>
        <View style={styles.controls}>
          <View style={{ flexDirection: 'row' }}>
            <View style={{ height: 150}}>
              {
                this.state.originalImage && <Image
                  style={{
                    width: 150,
                    height: 150
                  }}
                  resizeMode="contain"
                  source={{ uri: this.state.originalImage }}
                />
              }
            </View>
            <View style={{ height: 150}}>
              {
                this.state.image && <Image
                  style={{
                    width: 150,
                    height: 150
                  }}
                  resizeMode="contain"
                  source={{ uri: this.state.image }}
                />
              }
            </View>
          </View>
          <View style={{ flexDirection: 'row', height: 30, marginTop: 10 }}>
            <ToggleComponent
              value={this.state.bwFilter}
              onValueChange={() => this.setState({ bwFilter: !this.state.bwFilter })}
            />
            <Text style={{ marginTop: 5, marginLeft: 5 }}> B&W filter</Text>
          </View>

          <View style={styles.button}>
            <Button
              onPress={() => {
                RNGeniusScan.scanCamera(scanOptions)
                  .then((result) => this.setState({ image: result["enhancedImageUri"], originalImage: result["originalImageUri"] }))
                  .catch(e => alert(e))
              }}
              title="Scan with camera"
            />
          </View>


          <View style={styles.button}>
            <Button
              style={styles.button}
              disabled={!this.state.image}
              onPress={() => {
                // Scanning the image previously scanned with the camera
                RNGeniusScan.scanImage(this.state.originalImage, scanOptions)
                  .then((result) => this.setState({ image: result["enhancedImageUri"], originalImage: result["originalImageUri"] }))
                  .catch(e => alert(e))
              }}
              title="Re-scan image"
            />
          </View>

          <View style={styles.button}>
            <Button
              style={styles.button}
              disabled={!this.state.image}
              style={{ margin: 5 }}
              onPress={() => {
                // Scanning the image previously scanned with the camera
                RNGeniusScan.generatePDF('Scan', [this.state.image], { /*password: 'test'*/ })
                  .then((pdf) => {
                    return Share.open({ url: Platform.OS === 'android' ? `file://${pdf}` : pdf })
                  })
                  .catch(e => alert(JSON.stringify(e)))
              }}
              title="Generate PDF"
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