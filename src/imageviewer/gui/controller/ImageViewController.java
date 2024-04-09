package imageviewer.gui.controller;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ImageViewController {

    @FXML
    private Label lblImageName;
    @FXML
    private Label lblRedPixels;
    @FXML
    private Label lblBluePixels;
    @FXML
    private Label lblGreenPixels;
    @FXML
    private ImageView imageViewShowImage;

    @FXML
    private MFXListView MFXlistViewImages;

    private String savedStringOfImage = null;
    private int nextIndex = 0;
    private Timer slideshowTimer;

    // Setup method to initialize the view
    public void setup() {
        // Get list of image files from resources/images folder
        List<File> imageList = listImages("resources/images");

        // Extract image file names
        List<String> imageNames = new ArrayList<>();
        for (File imageFile : imageList) {
            imageNames.add(imageFile.getName());
        }

        // Populate the MaterialFX ListView with image names
        ObservableList<String> observableImageNames = FXCollections.observableArrayList(imageNames);
        MFXlistViewImages.setItems(observableImageNames);
    }

    // Method to list image files in a folder
    public static List<File> listImages(String folderPath) {
        List<File> imageFiles = new ArrayList<>();
        File folder = new File(folderPath);

        // Check if the folder exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Add only image files to the list
                    if (file.isFile() && isImageFile(file.getName())) {
                        imageFiles.add(file);
                    }
                }
            }
        } else {
            System.out.println("Folder doesn't exist or is not a directory.");
        }

        return imageFiles;
    }

    // Method to check if a file is an image file based on its extension
    private static boolean isImageFile(String fileName) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        for (String extension : imageExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private void stopSlideShow () {
        // Cancel ongoing slideshow, if any
        if (slideshowTimer != null) {
            slideshowTimer.cancel();
            slideshowTimer = null;
        }
    }

    @FXML
    private void handleSelectedImage(MouseEvent mouseEvent){
        stopSlideShow();

        // Get selected image name from the list view
        String imageName = MFXlistViewImages.getSelectionModel().getSelection().toString();
        savedStringOfImage = imageName;

        // Extract individual image names
        List<String> images = trimImages(imageName);

        // Set up timer for slideshow
        slideshowTimer = new Timer();
        int delay = 2500;

        // Display each image with delay
        for (int i = 0; i < images.size(); i++) {
            final int index = i;
            slideshowTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Update UI on JavaFX Application Thread
                    Platform.runLater(() -> {
                        showImage(images.get(index));
                        lblImageName.setText(images.get(index));
                    });
                }
            }, i * delay);
        }
    }


    // Method to extract image names from the selection string
    private List<String> trimImages(String stringToBeTrimmed){
        List<String> firstTrimmingofImages = new ArrayList<>();
        List<String> secondTrimmingofImages = new ArrayList<>();
        List<String> finalListofImages = new ArrayList<>();

        // Split the selection string by comma
        firstTrimmingofImages.addAll(List.of(stringToBeTrimmed.split(",")));
        for (String sr : firstTrimmingofImages){
            String[] parts = sr.split("=");
            if (parts.length > 1) {
                secondTrimmingofImages.add(parts[1]);
            } else {
                secondTrimmingofImages.add(sr);
            }
        }
        // Removes the lat part of the filename that is not needed
        for (String sr : secondTrimmingofImages){
            String[] parts = sr.split("}");
            if (parts.length > 0) {
                finalListofImages.add(parts[0]);
            } else {
                finalListofImages.add(sr);
            }
        }
        return finalListofImages;
    }

    // Method to display the selected image
    private void showImage(String imageName) {
        if (imageName != null) {
            String imagePath = "resources/images/" + imageName;
            Image image = new Image(new File(imagePath).toURI().toString());
            imageViewShowImage.setImage(image);
            int redPixels = 0;
            int greenPixels = 0;
            int bluePixels = 0;

            // Get pixel reader for the image
            PixelReader pixelReader = image.getPixelReader();

            // Iterate over each pixel in the image
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get color of the pixel at coordinates (x, y)
                    Color color = pixelReader.getColor(x, y);

                    // Extract RGB components of the color
                    double red = color.getRed();
                    double green = color.getGreen();
                    double blue = color.getBlue();

                    // Check if the pixel is predominantly red, green, or blue
                    if (red > green && red > blue) {
                        redPixels++;
                    } else if (green > red && green > blue) {
                        greenPixels++;
                    } else if (blue > red && blue > green) {
                        bluePixels++;
                    }
                }
            }

            // Print the count of red, green, and blue pixels
            lblRedPixels.setText("Red Pixels: " + redPixels);
            lblGreenPixels.setText("Green Pixels: " + greenPixels);
            lblBluePixels.setText("Blue Pixels: " + bluePixels);
        }
    }

    // Extracts the index of the selected image from a list of images displayed in buttons
    private int trimImageForButtons(String image) {
        List<String> currentIndex1 = Collections.singletonList(image); // Convert the image string into a list
        List<String> currentIndex2 = new ArrayList<>(); // Stores the result of the first split
        List<String> currentIndex3 = new ArrayList<>(); // Stores the result of the second split

        // Split the string by "=" and add the first part to currentIndex2
        for (String part : currentIndex1) {
            String[] parts = part.split("=");
            currentIndex2.add(parts.length > 0 ? parts[0] : part); // Add the first part before "=" to currentIndex2
        }

        // Split the string by "{" and add the second part to currentIndex3
        for (String part : currentIndex2) {
            String[] parts = part.split("\\{");
            currentIndex3.add(parts.length > 1 ? parts[1] : part); // Add the second part after "{" to currentIndex3
        }

        // Parse the first element of currentIndex3 as an integer (the index of the image) and return it
        return Integer.parseInt(currentIndex3.get(0));
    }

    // Event handler for navigating to the previous image
    @FXML
    private void handlePreviousImage(ActionEvent actionEvent) {
        stopSlideShow();

        String selectedImageString = MFXlistViewImages.getItems().get(nextIndex).toString();

        // If there's no previously saved image, display the last image
        if (savedStringOfImage == null) {
            String imageName = (String) MFXlistViewImages.getItems().get(MFXlistViewImages.getItems().size() - 1);
            savedStringOfImage = imageName;
            nextIndex = MFXlistViewImages.getItems().size() - 1;
            for (String image : trimImages(imageName)) {
                showImage(image);
            }
        } else if (savedStringOfImage.equals(selectedImageString)) {
            // Navigate to the previous image
            nextIndex--;
            if (nextIndex == -1) {
                nextIndex = MFXlistViewImages.getItems().size() - 1;
            }
            if (nextIndex <= MFXlistViewImages.getItems().size()) {
                String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();
                savedStringOfImage = imageName;
                for (String image : trimImages(imageName)) {
                    showImage(image);
                }
            }
        } else if (!savedStringOfImage.equals(selectedImageString)) {
            // Navigate to the previous image based on the saved image string
            nextIndex = trimImageForButtons(savedStringOfImage) - 1;
            String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();
            for (String image : trimImages(imageName)) {
                savedStringOfImage = image;
                showImage(image);
            }
        }
    }



    // Event handler for navigating to the next image
    @FXML
    private void handleNextImage(ActionEvent actionEvent) {
        stopSlideShow();

        String selectedImageString = MFXlistViewImages.getItems().get(nextIndex).toString();

        // If there's no previously saved image, display the first image
        if (savedStringOfImage == null) {
            String imageName = (String) MFXlistViewImages.getItems().get(0);
            savedStringOfImage = imageName;
            for (String image : trimImages(imageName)) {
                showImage(image);
            }
        } else if (savedStringOfImage.equals(selectedImageString)) {
            // Navigate to the next image
            nextIndex++;
            if (nextIndex >= MFXlistViewImages.getItems().size()) {
                nextIndex = 0;
            }
            if (nextIndex <= MFXlistViewImages.getItems().size()) {
                String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();
                savedStringOfImage = imageName;
                for (String image : trimImages(imageName)) {
                    showImage(image);
                }
            }
        } else if (!savedStringOfImage.equals(selectedImageString)) {
            // Navigate to the next image based on the saved image string
            nextIndex = trimImageForButtons(savedStringOfImage) + 1;
            String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();
            for (String image : trimImages(imageName)) {
                savedStringOfImage = image;
                showImage(image);
            }
        }
    }


    public void handleOnDragDropped(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();
        if (dragboard.hasFiles()) {
            for (File file : dragboard.getFiles()) {
                // Construct the destination path
                Path destinationPath = Paths.get("resources/images", file.getName());
                try {
                    // Copy the file to the destination folder
                    Files.copy(file.toPath(), destinationPath);
                } catch (IOException e) {
                    System.err.println("Error saving file " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        setup();
    }

    public void handleOnDragOver(DragEvent dragEvent) {
        if (dragEvent.getGestureSource() != this) {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }
}