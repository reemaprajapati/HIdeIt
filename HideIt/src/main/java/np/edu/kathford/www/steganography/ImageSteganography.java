package np.edu.kathford.www.steganography;

import np.edu.kathford.www.image.Coord;
import np.edu.kathford.www.image.Picture;
import np.edu.kathford.www.util.Log;
import np.edu.kathford.www.util.UniqueNumberGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
//Note: Java uses Big Endian by default

/**
 * Encodes and decodes messages to and from images.(Steganography)
 */
public class ImageSteganography {
    private static final int CHANNEL_RED = 0;
    private static final int CHANNEL_GREEN = 1;
    private static final int CHANNEL_BLUE = 2;


    public int getLimit(String filename) throws IOException {
        BufferedImage image = ImageIO.read(new File(filename));
        return image.getHeight() * image.getWidth() * 3 / 8;
    }

    private Coord nextCoord(UniqueNumberGenerator uniqueNumberGenerator, int width) throws Exception {
        int rand = uniqueNumberGenerator.nextInt();
        return new Coord(rand % width, rand / width);

    }

    /**
     * Decodes the message encoded in the given np.edu.kathford.www.image.
     *
     * @param filename The full Path of the np.edu.kathford.www.image to encode the data in
     * @return The decoded message
     */
    public String decode(String filename, String password) throws Exception {
        ArrayList<Coord> coordArrayList = new ArrayList<>();
        Log.d("Decoding Image");
        Log.d("Filename: " + filename);

        Picture picture = new Picture(filename);

        UniqueNumberGenerator uniqueNumberGenerator = new UniqueNumberGenerator(picture.height() * picture.width(), password.hashCode());

        //6x3=18 > 16
        for (int i = 0; i < 6; i++) {
            coordArrayList.add(nextCoord(uniqueNumberGenerator, picture.width()));
        }
        int length = 0;
        byte[] lengthByte = new byte[2];
        for (int i = 0; i < 16; i++) {
            int bitIndex = i % 8;
            int byteIndex = i / 8;
            int pixNo = i / 3;
            int x = coordArrayList.get(pixNo).x;
            int y = coordArrayList.get(pixNo).y;
            int channelNo = i % 3;

            Color color = picture.get(x, y);
            int channel = getChannel(color, channelNo);

            //decode the bit from that color channel byte
            lengthByte[byteIndex] |= (channel & 1) << bitIndex;
            Log.d("Byte " + i + "= " + (channel));
        }
        length += (lengthByte[0] & 0x000000FF) << 8;
        length += (lengthByte[1] & (0x000000FF));

        Log.d("Length = " + length);

        int noOfBits = ((length + 2) * 8);
        int noOfPixels = noOfBits / 3;
        noOfPixels += noOfBits % 3 > 0 ? 1 : 0;
        System.out.println(noOfPixels);

        for (int i = 6; i <= noOfPixels; i++) {
            coordArrayList.add(nextCoord(uniqueNumberGenerator, picture.width()));
        }

        byte[] dataBytes = new byte[length];
        System.out.println(coordArrayList.size());
        for (int i = 16; i < noOfBits; i++) {

            int j = i - 16;
            int bitIndex = j % 8;
            int byteIndex = j / 8;
            int pixNo = i / 3;
            int x = coordArrayList.get(pixNo).x;
            int y = coordArrayList.get(pixNo).y;
            int channelNo = i % 3;
            Color color = picture.get(x, y);
            int channel = getChannel(color, channelNo);
            dataBytes[byteIndex] = putBit(dataBytes[byteIndex], (byte) (channel & 1), bitIndex);
        }

        return new String(dataBytes);
    }

    private int getChannel(Color color, int channelNo) {
        int channel = 0;
        switch (channelNo) {
            case CHANNEL_RED:
                channel = color.getRed();
                break;
            case CHANNEL_GREEN:
                channel = color.getGreen();
                break;
            case CHANNEL_BLUE:
                channel = color.getBlue();
                break;
        }
        return channel;
    }

    /**
     * Encodes the given data into the given np.edu.kathford.www.image.
     *
     * @param inputFile The full Path of the np.edu.kathford.www.image to encode the data in
     * @param data      The String data to encode into the np.edu.kathford.www.image
     */
    public void encode(String inputFile, String outputFile, String data, String password) throws IOException {
        ArrayList<Coord> coordArrayList = new ArrayList<>();

        Log.d("Encoding Image");
        Log.d("Filename: " + inputFile);
        Log.d("Data: \"" + data + "\"");

        Picture picture = new Picture(inputFile);

        UniqueNumberGenerator uniqueNumberGenerator = new UniqueNumberGenerator(picture.height()* picture.width(), password.hashCode());

        byte[] dataBytes = data.getBytes();
        Log.d("Data size: " + dataBytes.length + " Bytes");
        byte[] dataLengthBytes = intToBytes(data.length());

        //Concatenate the length bytes with the string data bytes
        //Since we are using two byte integer we only use the value in the highest two bytes
        int actualLength = dataBytes.length + 2;
        byte[] finalDataBytes = new byte[actualLength];
        finalDataBytes[0] = dataLengthBytes[2];
        finalDataBytes[1] = dataLengthBytes[3];
        System.arraycopy(dataBytes, 0, finalDataBytes, 2, dataBytes.length);

        int noOfBits = (actualLength * 8);
        int noOfPixels = noOfBits / 3;
        if (noOfBits % 3 > 0) {
            noOfPixels++;
        }

        try {
            for (int i = 0; i < noOfPixels; i++) {
                coordArrayList.add(nextCoord(uniqueNumberGenerator, picture.width()));

            }
        } catch (Exception e) {
            System.out.println(" Encryption Out of Range");
        }

        //Bit by bit put the message into the pixels
        for (int i = 0; i < finalDataBytes.length * 8; i++) {
            int bitIndex = i % 8;
            int byteIndex = i / 8;
            int pixNo = i / 3;
            int x = coordArrayList.get(pixNo).x;
            int y = coordArrayList.get(pixNo).y;
            int channelNo = i % 3;

            // hiding 3 bits per pixel, 1 bit in each color
            Color color = picture.get(x, y);
            //hide 1st bit in red channel, 2nd in the green channel and 3rd in blue channel
            //select the channel to hide the bit in
            int channel = getChannel(color, channelNo);
            //encode the bit into that color channel byte
            int encryptedColor = encrypt(channel, getBit(finalDataBytes[byteIndex], bitIndex));
            Color newColor = null;
            switch (channelNo) {
                case CHANNEL_RED:
                    newColor = new Color(encryptedColor, color.getGreen(), color.getBlue());
                    break;
                case CHANNEL_GREEN:
                    newColor = new Color(color.getRed(), encryptedColor, color.getBlue());
                    break;
                case CHANNEL_BLUE:
                    newColor = new Color(color.getRed(), color.getGreen(), encryptedColor);
                    break;
            }
            picture.set(x, y, newColor);
        }
        System.out.println("saving encoded image");
        System.out.println("output file :" + outputFile);

        picture.save(outputFile);
    }

    private byte putBit(byte dest, byte source, int n) {
        if (source == 0) {
            dest &= ~(1 << n);
        } else {
            dest |= (1 << n);

        }
        return dest;
    }

    /**
     * @param color represents the color R or G or B  of a pixel
     * @param bit   represents the  nibbles  of character that is to be hidden
     * @return the new color value  in which the nibble is appended
     */
    private int encrypt(int color, byte bit) {
        color = (color & 0xfE) | bit;
        return color;
    }

    /**
     * Gets the nth bit of the byte as the LSB of another byte.
     *
     * @param byt The byte to get the bit from
     * @param n   The bit to get.
     * @return A byte with its LSB equal to the nth bit of the input byte.
     * All other bytes are cleared.
     */
    private byte getBit(byte byt, int n) {
        return (byte) (byt >> (n % 8) & 1);
    }

    /**
     * Convert int to bytes.
     *
     * @param n The integer to convert to bytes
     * @return The bytes of the integer
     */
    private byte[] intToBytes(int n) {
        return ByteBuffer.allocate(4).putInt(n).array();
    }
}
