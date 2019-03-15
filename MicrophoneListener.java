package com.example.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaSyncEvent;
import android.util.Log;

public class MicrophoneListener {

    private final int SAMPLE_DELAY = 50;
    private final int SAMPLE_RATE = 44100;

    private byte[] buffer;

    private int innerBufferSize;
    private AudioRecord audioRecorder;

    private boolean stopThread;

    public MicrophoneListener(int bufferSizeCallback) {
        audioRecorder = findAudioRecord();

        buffer = new byte[bufferSizeCallback];
    }

    public AudioFormat GetFormat() {
        return audioRecorder.getFormat();
    }

    public void start(final MicrophoneCallback callback) {
        stopThread = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int numBytesRead;
                int bytesRead = 0;
                int i = 0;
                int currentPosition = 0;

                byte[] tmpData = new byte[innerBufferSize];


                audioRecorder.startRecording();

                while (!stopThread) {
                    try {
                        Thread.sleep(SAMPLE_DELAY);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    i = 0;
                    numBytesRead = audioRecorder.read(tmpData, 0, innerBufferSize);

                    while(i < numBytesRead){
                        buffer[currentPosition++] = tmpData[i++];

                        if (currentPosition == buffer.length) {
                            callback.onNewData(buffer);
                            currentPosition = 0;
                        }
                    }
                }

                stopThread = true;
                audioRecorder.stop();
            }
        });

        thread.start();
    }

    public void stop() {
        stopThread = true;
    }

    public AudioRecord findAudioRecord() {
        for (int rate : new int[]{44100, 22050, 11025, 8000}) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                try {
                    innerBufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, audioFormat);

                    if (innerBufferSize != AudioRecord.ERROR_BAD_VALUE) {
                        // check if we can instantiate and have a success
                        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, AudioFormat.CHANNEL_IN_MONO, audioFormat, innerBufferSize);

                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                            return recorder;
                    }
                } catch (Exception e) {
                }

            }
        }
        return null;
    }
}
