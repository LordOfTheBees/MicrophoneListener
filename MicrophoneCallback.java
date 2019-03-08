import javax.sound.sampled.LineUnavailableException;

public interface MicrophoneCallback{
    void onNewData(byte[] data);
}