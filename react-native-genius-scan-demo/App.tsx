import React from 'react';
import {
  StatusBar,
  StyleSheet,
  useColorScheme,
  View,
  Button,
  Text,
  Alert,
} from 'react-native';

import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';

import FileViewer from 'react-native-file-viewer';
import RNGeniusScan, {
  ScanOptions,
  ReadableCodeConfiguration,
} from '@thegrizzlylabs/react-native-genius-scan';

function App() {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaProvider>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <AppContent />
    </SafeAreaProvider>
  );
}

function AppContent() {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundColor = isDarkMode ? '#000000' : '#FFFFFF';
  const textColor = isDarkMode ? '#F9FAFB' : '#111827';

  // Refer to the Genius Scan SDK plugin README.md for a list of the available options
  const configuration: ScanOptions = {
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
    <SafeAreaView
      style={[styles.safeArea, { backgroundColor }]}
      >
      <View style={styles.toolbar}>
        <Text style={[styles.toolbarTitle, { color: textColor }]}>
          GS SDK ReactNative Demo
        </Text>
      </View>
      <View style={styles.content}>
        <View style={styles.buttons}>
          <View style={styles.buttonWrapper}>
            <Button
              onPress={async () => {
                try {
                  // Start scan flow
                  let scanResult = await RNGeniusScan.scanWithConfiguration(configuration)

                  // The result object contains the captured scans as well as the multipage document
                  console.log(scanResult);

                  // Here is how you can display the resulting document:
                  await FileViewer.open(scanResult.multiPageDocumentUrl!)

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
                  Alert.alert('Scan failed', `${e}`)
                }
              }}
              title="Scan documents"
            />
          </View>
          <View style={styles.buttonWrapper}>
            <Button
              onPress={async () => {
                try {
                  // Start readable code scanning
                  const readableCodeConfiguration: ReadableCodeConfiguration = {
                    isBatchModeEnabled: true,
                    supportedCodeTypes: ['qr', 'code128', 'ean13']
                  }
                  let result = await RNGeniusScan.scanReadableCodesWithConfiguration(readableCodeConfiguration)

                  // The result object contains the detected readable codes
                  console.log(result);

                  const codesText = result.readableCodes.map(code => `${code.type}: ${code.value}`).join('\n');
                  Alert.alert('Detected codes', codesText);
                } catch(e) {
                  Alert.alert('Readable code scan failed', `${e}`)
                }
              }}
              title="Scan barcodes"
            />
          </View>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
  },
  toolbar: {
    paddingHorizontal: 24,
    paddingVertical: 20,
  },
  toolbarTitle: {
    fontSize: 18,
    fontWeight: '600',
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 24,
    paddingBottom: 24,
  },
  buttons: {
    width: '100%',
  },
  buttonWrapper: {
    marginBottom: 16,
  },
});

export default App;
