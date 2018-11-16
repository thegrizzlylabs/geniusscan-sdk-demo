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
  Image
} from 'react-native';
import RNGeniusScan from '@thegrizzlylabs/react-native-genius-scan';

export default class App extends Component {
  state = {
    image: null,
    bwFilter: false
  }


  render() {
    const scanOptions = this.state.bwFilter ? {defaultEnhancement: RNGeniusScan.ENHANCEMENT_BW} : {};

    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Demo app for react-native-genius-scan
        </Text>
        <View style={styles.controls}>
          <View style={{ height: 200}}>
            {
              this.state.image && <Image
                style={{
                  width: 200,
                  height: 200
                }}
                resizeMode="contain"
                source={{ uri: this.state.image }}
              />
            }
          </View>

          <View style={{ flexDirection: 'row', height: 30 }}>
            <CheckBox
              value={this.state.bwFilter}
              onValueChange={() => this.setState({ bwFilter: !this.state.bwFilter })}
            />
            <Text style={{ marginTop: 5 }}> B&W filter</Text>
          </View>

          <View style={styles.button}>
            <Button
              onPress={() => {
                RNGeniusScan.scanCamera(scanOptions)
                  .then((image) => this.setState({ image }))
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
                RNGeniusScan.scanImage(this.state.image, scanOptions)
                  .then((image) => this.setState({ image }))
                  .catch(e => alert(e))
              }}
              title="Re-Scan image"
            />
          </View>

          <View style={styles.button}>
            <Button
              style={styles.button}
              disabled={!this.state.image}
              style={{ margin: 5 }}
              onPress={() => {
                // Scanning the image previously scanned with the camera
                RNGeniusScan.generatePDF('Scan', [this.state.image], {password: 'test'})
                  .then((pdf) => alert(`PDF generated: ${pdf}`))
                  .catch(e => alert(e))
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
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  controls: {
    flexDirection: 'column',
    alignItems: 'center'
  },
  button: {
    margin: 5
  }
});