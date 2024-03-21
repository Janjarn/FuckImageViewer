package imageviewer.gui.controller;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.sql.Time;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ImageViewController {

    @FXML
    private ImageView imageViewShowImage;
    @FXML
    private ListView listViewImages;

    @FXML
    private MFXListView MFXlistViewImages;

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
        String[] imageExtensions = {".jpg"};
        for (String extension : imageExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    // Event handler for selecting an image from the MaterialFX ListView
    @FXML
    private void handleSelectedImage(MouseEvent mouseEvent) throws InterruptedException {
        String imageName = MFXlistViewImages.getSelectionModel().getSelection().toString();
        List<String> images = trimImages(imageName);

        // Timer for scheduling image display with a delay
        Timer timer = new Timer();
        int delay = 2000; // 2 seconds delay

        // Schedule each image to be shown after a delay
        for (int i = 0; i < images.size(); i++) {
            final int index = i;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    showImage(images.get(index));
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
        }
    }

    // Event handler for navigating to the previous image
    @FXML
    private void handlePreviousImage(ActionEvent actionEvent) {
    }

    // Event handler for navigating to the next image

    private String savedStringOfImage = null;
    private List<String> currentIndex3 = new ArrayList<>();
    private int nextIndex = 0;
    @FXML
    private void handleNextImage(ActionEvent actionEvent) {
        if (savedStringOfImage == null) {
            String imageName = (String) MFXlistViewImages.getItems().getFirst();
            savedStringOfImage = imageName;
            // Show the next image
            for (String image : trimImages(imageName)) {
                showImage(image);
            }
        }
        // Variable to hold the selected image string
        String selectedImageString = new String();
        savedStringOfImage = MFXlistViewImages.getSelectionModel().getSelection().toString();

        // Temporary variable to store the saved image string
        String imageTemp = savedStringOfImage;

        // Check if the saved image string is not null
        if (savedStringOfImage != null) {
            // Iterate through trimmed images
            for (String image : trimImages(imageTemp)) {
                selectedImageString = image;
            }
        }

        // Check if the saved image string is equal to the selected image string
        if (savedStringOfImage == selectedImageString) {
            // Check if the next index exceeds the size of the image list
            if (nextIndex >= MFXlistViewImages.getItems().size()) {
                nextIndex = 0; // Reset next index to 0
            } else {
                // Get the name of the next image
                String imageName = MFXlistViewImages.getItems().get(nextIndex + 1).toString();
                nextIndex++; // Increment next index

                // Show the next image
                for (String image : trimImages(imageName)) {
                    showImage(image);
                }
            }
        }

        // If the saved image string is not equal to the selected image string
        if (savedStringOfImage != selectedImageString) {
            nextIndex = 0; // Reset next index to 0

            // Lists to hold current index data
            List<String> currentIndex1;
            List<String> currentIndex2 = new ArrayList<>();

            // Get the selected image as a string
            String selectedImage = MFXlistViewImages.getSelectionModel().getSelection().toString();
            currentIndex1 = Collections.singletonList(selectedImage);

            // Iterate through currentIndex1
            for (String sr : currentIndex1) {
                // Split the string by "=" and add to currentIndex2
                String[] parts = sr.split("=");
                if (parts.length > 0) {
                    currentIndex2.add(parts[0]);
                } else {
                    currentIndex2.add(sr);
                }
            }

            // Iterate through currentIndex2
            for (String sr : currentIndex2) {
                // Split the string by "{" and add to currentIndex3
                String[] parts = sr.split("\\{");
                if (parts.length > 1) {
                    currentIndex3.add(parts[1]);
                } else {
                    currentIndex3.add(sr);
                }
            }

            // Get the next index and increment it
            nextIndex = Integer.parseInt(currentIndex3.getLast()) + 1;

            // Get the name of the next image
            String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();

            // Show the next image
            for (String image : trimImages(imageName)) {
                savedStringOfImage = image;
                showImage(image);
            }
        }
    }

}