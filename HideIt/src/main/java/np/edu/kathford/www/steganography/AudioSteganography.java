package np.edu.kathford.www.steganography;

import np.edu.kathford.www.util.UniqueNumberGenerator;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AudioSteganography {
    private AudioInputStream audioInputStream;

    public int getLimit(String filename) throws IOException, UnsupportedAudioFileException {
        int length = readSND(filename).length;

        return length / 8 - 4;
    }

    public void encode(String filename, String outFileName, String message, String key)
            throws Exception {
        byte[] audioBytes = readSND(filename);

        byte[] messageBuffer = message.getBytes();

        UniqueNumberGenerator uniqueNumberGenerator = new UniqueNumberGenerator(audioBytes.length, key.hashCode());
        ArrayList<Integer> byteNumberList = new ArrayList<>();
        int count = messageBuffer.length * 8 + 33;
        for (int m = 0; m < count; m++) {
            byteNumberList.add(uniqueNumberGenerator.nextInt());
        }
        int k = 1;
        int length = messageBuffer.length;
        for (int j = 1; j <= 32; j++, k++) {
            if ((length & 0x80000000) != 0) // MSB of length is '1'
                audioBytes[byteNumberList.get(k)] = (byte) (audioBytes[byteNumberList.get(k)] | 0x01);
            else if ((audioBytes[byteNumberList.get(k)] & 0x01) != 0) { //MSB of length '0' and lsb of audio '1'
                audioBytes[byteNumberList.get(k)] = (byte) (audioBytes[byteNumberList.get(k)] >>> 1);
                audioBytes[byteNumberList.get(k)] = (byte) (audioBytes[byteNumberList.get(k)] << 1);
            }
            length = (length << 1);
        }

        for (byte aMessageBuffer : messageBuffer) {
            byte pb = aMessageBuffer;
            for (int j = 1; j <= 8; j++, k++) {
                if ((pb & 0x80) != 0) // MSB of pb is '1'
                    audioBytes[byteNumberList.get(k)] = (byte) (audioBytes[byteNumberList.get(k)] | 0x01);
                else if ((audioBytes[byteNumberList.get(k)] & 0x01) != 0) { //MSB of pt '0' and lsb of audio '1'
                    audioBytes[byteNumberList.get(k)] = (byte) (audioBytes[byteNumberList.get(k)] >>> 1);
                    audioBytes[byteNumberList.get(k)] = (byte) (audioBytes[byteNumberList.get(k)] << 1);
                }
                pb = (byte) (pb << 1);
            }
        }

        ByteArrayInputStream byteIS = new ByteArrayInputStream(audioBytes);
        AudioInputStream audioIS = new AudioInputStream(byteIS,
                audioInputStream.getFormat(), audioInputStream.getFrameLength());
        audioIS.close();
        AudioSystem.write(audioIS, AudioFileFormat.Type.WAVE, new File(outFileName));
    }

    public String decode(String filename, String key) throws Exception {
        byte[] audioBytes = readSND(filename);

        UniqueNumberGenerator uniqueNumberGenerator = new UniqueNumberGenerator(audioBytes.length, key.hashCode());
        ArrayList<Integer> byteNumberList = new ArrayList<>();
        for (int m = 0; m <= 32; m++) {
            byteNumberList.add(uniqueNumberGenerator.nextInt());
        }
        int k = 1;
        int length = 0;
        for (int j = 1; j <= 32; j++, k++) {
            length = length << 1;
            if ((audioBytes[byteNumberList.get(k)] & 0x01) != 0) {
                length = length | 0x00000001;
            }
        }
        byte[] buff = new byte[length];
        for (int m = 33; m <= length*8+33; m++) {
            byteNumberList.add(uniqueNumberGenerator.nextInt());
        }

        for (int i = 0; i < length; i++) {
            byte out = (byte) 0;
            for (int j = 1; j <= 8; j++, k++) {
                out = (byte) (out << 1);
                if ((audioBytes[byteNumberList.get(k)] & 0x01) != 0) {
                    out = (byte) (out | 0x01);
                }
            }
            buff[i] = out;
        }
        return new String(buff);
    }

    private byte[] readSND(final String filePath) throws IOException, UnsupportedAudioFileException {
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
        byte[] buffer = new byte[4096];
        int c;
        while ((c = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
            baout.write(buffer, 0, c);
        }
        baout.close();
        return baout.toByteArray();
    }

    private boolean possible(String inputFile, String pt) throws IOException, UnsupportedAudioFileException {

        byte[] buff = pt.getBytes();
        byte[] audioBytes = readSND(inputFile);
        return buff.length * 8 <= audioBytes.length;
    }

}