/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Fragment} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Button,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

import Share from 'react-native-share';
import RNGeniusScan from '@thegrizzlylabs/react-native-genius-scan';

const App = () => {
  // Refer to the Genius Scan SDK demo README.md for a list of the available options
  const configuration = {
    source: 'camera',
  }
  return (
    <Fragment>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />

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

        </ScrollView>
      </SafeAreaView>
    </Fragment>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
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
  },
});

export default App;
