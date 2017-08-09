package np.edu.kathford.www.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;
import np.edu.kathford.www.exception.InvalidFileException;
import np.edu.kathford.www.steganography.AudioSteganography;
import np.edu.kathford.www.steganography.ImageSteganography;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.apache.commons.io.FilenameUtils.getExtension;

public class MainController implements Initializable {

    public Text progressStatus;
    public Text statusText;
    public ProgressBar progressBar;
    public PasswordField decodeSecret;
    public TextArea decodedMessage;
    public Button btnDecode;
    public Button btnSelectOutputPath;
    public Button btnEncode;
    public PasswordField encodeSecret;
    public PasswordField encodeReSecret;
    public TextArea encodeMessage;
    public ToggleGroup tgEncodeDecode;
    public TextField inputFilePath;
    public Button btnSelectInputFile;
    public Text fileTypeStatus;
    public VBox vbEncodeFields;
    public VBox vbDecodeFields;
    public RadioButton radioEncode;
    public RadioButton radioDecode;
    public Text remainingCharacters;
    public TextField outputFilePath;
    public VBox content;

    private IntegerProperty fileType = new SimpleIntegerProperty();
    private final int FILE_TYPE_INVALID = 0;
    private final int FILE_TYPE_IMAGE = 1;
    private final int FILE_TYPE_AUDIO = 2;
    private AudioSteganography audioSteganography = new AudioSteganography();
    private ImageSteganography imageSteganography = new ImageSteganography();
    private IntegerProperty  limit = new SimpleIntegerProperty(0);
    private final int STATUS_INFO = 0;
    private final int STATUS_WARN = 1;
    private final int STATUS_ERROR = 2;

    public void initialize(URL location, ResourceBundle resources) {
        remainingCharacters.textProperty().bindBidirectional(limit, new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return Integer.valueOf(string);
            }
        });
        tgEncodeDecode.selectedToggleProperty().addListener((observable, oldValue, newValue) -> toggleEncodeDecode());
        inputFilePath.textProperty().addListener((observable, oldValue, newValue) -> {
            if (new File(newValue).exists()) {
                encodeMessage.setDisable(false);
                if (fileType.get() == FILE_TYPE_IMAGE) {
                    try {
                        limit.set(imageSteganography.getLimit(newValue));
                    } catch (IOException e) {
                        e.printStackTrace();
                        updateStatus(STATUS_ERROR, "Invalid or Unsupported File.");
                    }
                } else if (fileType.get() == FILE_TYPE_AUDIO) {
                    try {
                        limit.set(audioSteganography.getLimit(newValue));

                    } catch (IOException | UnsupportedAudioFileException e) {
                        e.printStackTrace();
                        updateStatus(STATUS_ERROR, "Invalid or Unsupported File.");
                    }
                }

            }
        });
        fileType.addListener((observable, oldValue, newValue) -> updateFileTypeChangedStatus(newValue.intValue()));
        encodeReSecret.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (wasFocused) {
                if (!encodeSecret.getText().equals(encodeReSecret.getText())) {
                    //TODO
                    updateStatus(STATUS_ERROR, "Secrets do not match.");
                }
            }

        });
        encodeMessage.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (limit.get()!=0 &&newValue.intValue() > oldValue.intValue() && encodeMessage.getText().length() >= limit.get())
                encodeMessage.setText(encodeMessage.getText().substring(0, limit.get()));
            remainingCharacters.setText(String.valueOf(limit.get() - encodeMessage.getText().length()));
        });

    }

    public void selectInputFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image files (*.bmp)", "*.bmp"),
                new ExtensionFilter("Audio files (*.wav)", "*.wav"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(((Node) event.getTarget()).getScene().getWindow());
        if (file != null) {

            switch (getExtension(file.toString()).toUpperCase()) {
                case "PNG":
                case "BMP":
                    fileType.setValue(FILE_TYPE_IMAGE);
                    break;
                case "WAV":
                    fileType.setValue(FILE_TYPE_AUDIO);
                    break;
                default:
                    fileType.setValue(FILE_TYPE_INVALID);
            }
            inputFilePath.setText(file.getAbsolutePath());
        }
    }

    private void updateFileTypeChangedStatus(int fileTypeCode) {
        switch (fileTypeCode) {
            case FILE_TYPE_AUDIO:
                fileTypeStatus.setText("Audio(.wav)");
                break;
            case FILE_TYPE_IMAGE:
                fileTypeStatus.setText("Image");
                break;
            case FILE_TYPE_INVALID:
                fileTypeStatus.setText("Invalid media");
        }

    }

    private void toggleEncodeDecode() {
        if (radioEncode.isSelected()) {
            vbEncodeFields.setVisible(true);
            vbEncodeFields.setManaged(true);
            vbDecodeFields.setVisible(false);
            vbDecodeFields.setManaged(false);
        } else {
            vbEncodeFields.setVisible(false);
            vbEncodeFields.setManaged(false);
            vbDecodeFields.setVisible(true);
            vbDecodeFields.setManaged(true);
        }
    }


    //    private void showAlert(String textAlert) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Error");
//        alert.setHeaderText(null);
//        alert.setContentText(textAlert);
//        alert.showAndWait();
//    }


    private void updateStatus(int level, String status) {
        statusText.getStyleClass().clear();
        switch (level) {
            case STATUS_WARN:
                statusText.getStyleClass().add("txt-warn");
                break;
            case STATUS_ERROR:
                statusText.getStyleClass().add("txt-error");
                break;
            case STATUS_INFO:
            default:
                statusText.getStyleClass().add("txt-info");
        }

        statusText.setText(status);
    }

    public void selectOutputFile(javafx.event.ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        ExtensionFilter extensionFilter;
        switch (fileType.get()) {
            case FILE_TYPE_AUDIO:
                extensionFilter = new ExtensionFilter("Audio files (*.wav)", "*.wav");
                break;
            case FILE_TYPE_IMAGE:
                extensionFilter = new ExtensionFilter("Image files (*.bmp)", "*.bmp");
                break;
            default:
                updateStatus(STATUS_ERROR, "Select an input file first.");
                return;
        }
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showSaveDialog(((Node) event.getTarget()).getScene().getWindow());
        if (file != null) {
            outputFilePath.setText(file.getAbsolutePath());
        }
    }

    public void decode() throws InvalidFileException {
        if (inputFilePath.getText().isEmpty())
            updateStatus(STATUS_ERROR, "Output File not selected");
        Task<String> decodingTask;
        switch (fileType.get()) {
            case FILE_TYPE_AUDIO:
                decodingTask = new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        Thread.sleep(3000L);
                        return audioSteganography.decode(inputFilePath.getText(), decodeSecret.getText());
                    }
                };
                break;
            case FILE_TYPE_IMAGE:
                decodingTask = new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        Thread.sleep(3000L);
                        return imageSteganography.decode(inputFilePath.getText(), decodeSecret.getText());
                    }
                };
                break;
            default:
                throw new InvalidFileException();
        }

        decodingTask.setOnRunning(event -> {
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressStatus.setText("Decoding...");
        });
        decodingTask.setOnSucceeded(event -> {
            progressBar.setVisible(false);
            progressStatus.setText("");
            String message =  (String) event.getSource().getValue();
            if(isPrintable(message)){
                updateStatus(STATUS_INFO, "Decoding completed successfully");
            }
            else{
                updateStatus(STATUS_WARN, "Decoded text contains unprintable characters");
            }
            decodedMessage.setText((String) event.getSource().getValue());
        });
        decodingTask.setOnFailed(event -> {
            progressBar.setVisible(false);
            progressStatus.setText("");
            Throwable e = decodingTask.getException();
            e.printStackTrace();
            updateStatus(STATUS_WARN, "Decoding failed. ");
        });

        new Thread(decodingTask).start();
    }

    private boolean isPrintable(String string){
        return string.chars().mapToObj(value -> (char) value).noneMatch(character -> character>127||character<=32);
    }


    public void encode() {
        if (outputFilePath.getText().isEmpty())
            updateStatus(STATUS_ERROR, "Output File not selected");
        Task<Void> encodingTask;
        switch (fileType.get()) {
            case FILE_TYPE_AUDIO:
                encodingTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(3000L);
                        audioSteganography.encode(inputFilePath.getText(), outputFilePath.getText(),
                                encodeMessage.getText(), encodeSecret.getText());
                        return null;
                    }
                };
                break;
            case FILE_TYPE_IMAGE:
                encodingTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(3000L);
                        imageSteganography.encode(inputFilePath.getText(), outputFilePath.getText(),
                                encodeMessage.getText(), encodeSecret.getText());
                        return null;
                    }
                };
                break;
            default:
                updateStatus(STATUS_ERROR, "Select input file first");
                return;
        }
        encodingTask.setOnRunning(event -> {
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressStatus.setText("Encoding...");
        });
        encodingTask.setOnSucceeded(event -> {
            progressBar.setVisible(false);
            progressStatus.setText("");
            decodedMessage.setText((String) event.getSource().getValue());
            updateStatus(STATUS_INFO, "Encoding completed successfully");
        });
        encodingTask.setOnFailed(event -> {
            progressBar.setVisible(false);
            progressStatus.setText("");
            Throwable e = encodingTask.getException();
            e.printStackTrace();
            updateStatus(STATUS_WARN, "Encoding failed. ");
        });

        new Thread(encodingTask).start();
    }

}
