import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneListener {
    private final int CHUNK_SIZE = 2048;

    private boolean stopThread = true;
    private  AudioFormat format;

    private byte[] data;

    public MicrophoneListener(float sampleRate, int sampleSizeInBits, int channels, int arraySizeForSave){
        format = new AudioFormat(sampleRate, sampleSizeInBits, channels, true, true);
        data = new byte[arraySizeForSave];
    }

    public void setAudioFormat(float sampleRate, int sampleSizeInBits,
                               int channels, boolean signed, boolean bigEndian){
        format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public AudioFormat getAudioFormat(){
        return format;
    }

    public void start(MicrophoneCallback callback) throws Exception {
        if (!stopThread) throw new Exception("Microphone already start");

        stopThread = false;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TargetDataLine microphone = AudioSystem.getTargetDataLine(format);

                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                    microphone = (TargetDataLine) AudioSystem.getLine(info);
                    microphone.open(format);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int numBytesRead;

                    byte[] tmpData = new byte[CHUNK_SIZE];
                    microphone.start();

                    int bytesRead = 0;
                    int i = 0;
                    int currentPosition = 0;

                    while (!stopThread) { // Just so I can test if recording
                        try {
                            // my mic works...
                            numBytesRead = microphone.read(tmpData, 0, CHUNK_SIZE);
                            for (i = 0; i < numBytesRead && currentPosition < data.length; ++i) {
                                data[currentPosition++] = tmpData[i];
                            }

                            if (currentPosition == data.length) {
                                callback.onNewData(data);
                                currentPosition = 0;
                            }

                            for (; i < numBytesRead && currentPosition < data.length; ++i) {
                                data[currentPosition++] = tmpData[i];
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    microphone.close();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } finally {
                    stopThread = true;
                }
            }
        });
        thread.start();
    }

    public void stop(){
        stopThread = true;
    }
}
