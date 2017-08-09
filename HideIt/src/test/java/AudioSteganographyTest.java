import np.edu.kathford.www.FormattedLogger;
import np.edu.kathford.www.steganography.AudioSteganography;

import static np.edu.kathford.www.FormattedLogger.printKVFormatted;

public class AudioSteganographyTest {

    public static void main(String[] args) throws Exception {
        AudioSteganography audioSteganography = new AudioSteganography();

        FormattedLogger.printHeading1("TESTING AUDIO STEGANOGRAPHY");
        FormattedLogger.printHeading2("Test Case 1: Using same key");

        String message = "Hello";
        String infile = "test_file.wav";
        String outfile = "out.wav";
        String key = "a";

        FormattedLogger.printProcessStatus("Encoding");

        printKVFormatted("Message", message);
        printKVFormatted("Input file", infile);
        printKVFormatted("Output file ", outfile);
        printKVFormatted("Key", key);

        audioSteganography.encode(infile, outfile, message, key);

        FormattedLogger.printProcessStatus("Encoding complete");
        FormattedLogger.printProcessStatus("Decoding");

        printKVFormatted("Input file", outfile);
        printKVFormatted("Key", key);

        String decodedMessage = audioSteganography.decode(outfile, key);

        FormattedLogger.printProcessStatus("Decoding complete");

        printKVFormatted("Decoded message", decodedMessage);

        if (decodedMessage.equals(message)) {
            System.out.println("Encoded message equals Decoded message");

            printKVFormatted("Result", "Success");
        }


    }

}
