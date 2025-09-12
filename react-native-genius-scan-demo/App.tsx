/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  useColorScheme,
  View,
  Button
} from 'react-native';

import {
  Colors,
  Header,
} from 'react-native/Libraries/NewAppScreen';

import FileViewer from 'react-native-file-viewer';
import RNGeniusScan from '@thegrizzlylabs/react-native-genius-scan';


function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  // Refer to the Genius Scan SDK plugin README.md for a list of the available options
  const configuration = {
    source: 'camera',
    ocrConfiguration: {
      languages: ['en-US']
    }
  }

  // This code shows how to initialize the SDK with a license key.
  // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
  //
  // RNGeniusScan.setLicenseKey("<Your license key>", /* autoRefresh = */ true)

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <Header />
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
            padding: 20,
            gap: 20
          }}>
          <Button
            onPress={async () => {
              try {
                // Start scan flow
                let scanResult = await RNGeniusScan.scanWithConfiguration(configuration)

                // The result object contains the captured scans as well as the multipage document
                console.log(scanResult);

                // Here is how you can display the resulting document:
                await FileViewer.open(scanResult.multiPageDocumentUrl)

                // You can also generate your document separately from selected pages:
                /*
                const documentUrl = 'file://' + appFolder + '/mydocument.pdf'
                const document = {
                  pages: [{
                    imageUrl: scanResult.scans[0].enhancedUrl,
                    hocrTextLayout: scanResult.scans[0].ocrResult.hocrTextLayout
                  }]
                }
                const generationConfiguration = { outputFileUrl: documentUrl };
                await RNGeniusScan.generateDocument(document, generationConfiguration)
                await FileViewer.open(documentUrl)
                */
              } catch(e) {
                alert(e)
              }
            }}
            title="Scan documents"
          />
          <Button
            onPress={async () => {
              try {
                // Start readable code scanning
                const readableCodeConfiguration = {
                  isBatchModeEnabled: true,
                  supportedCodeTypes: ['qr', 'code128', 'ean13']
                }
                let result = await RNGeniusScan.scanReadableCodesWithConfiguration(readableCodeConfiguration)

                // The result object contains the detected readable codes
                console.log(result);

                const codesText = result.readableCodes.map(code => `${code.type}: ${code.value}`).join('\n');
                alert(`Detected codes:\n${codesText}`);
              } catch(e) {
                alert(e)
              }
            }}
            title="Scan barcodes"
          />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

export default App;
