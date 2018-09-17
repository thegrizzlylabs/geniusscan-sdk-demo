/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  Button,
  View,
  Image
} from 'react-native';
import RNGeniusScan from 'react-native-genius-scan';

export default class App extends Component {
  state = {
    image: null
  }
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Demo app for react-native-genius-scan
        </Text>
        <Button
          onPress={() => {
            RNGeniusScan.scanCamera()
              .then((image) => this.setState({ image }))
              .catch(e => alert(e))
          }}
          title="Scan with camera"
        />
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
        {
          this.state.image && <Button
            onPress={() => {
              // Scanning the image previously scanned with the camera
              RNGeniusScan.scanImage(this.state.image)
                .then((image) => this.setState({ image }))
                .catch(e => alert(e))
            }}
            title="Scan image"
          />
        }
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
});
