import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Button, NativeModules } from 'react-native';
import { DeviceEventEmitter } from 'react-native';

const MicrophoneListener = NativeModules.IOMicrophoneListener;
const MicrophoneCallback = NativeModules.IOMicrophoneCallback;

type Props = {};
export default class App extends Component<Props> {
  constructor() {
      super()
      this.state = {
         myText: 'My Original Text'
      }
	  
	  MicrophoneListener.setBufferSize(1024);
	  DeviceEventEmitter.addListener('onNewSoundData', (e)=>{ this.onNewSoundData(e);});
   }
   
   onNewSoundData(e){
	   var data = e.data;
	   this.setState({myText: data[1000]});
   }
   
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>{this.state.myText}</Text>
		<Button onPress={()=>this.start()} title='Start'/>
		<Button onPress={()=>this.stop()} title='Stop'/>
      </View>
    );
  }
  
  start(){
	  MicrophoneListener.start();
  }
  
  stop(){
	  MicrophoneListener.stop();
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
