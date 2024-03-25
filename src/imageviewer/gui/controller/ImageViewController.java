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
        // Check if there's an ongoing slideshow and cancel it before starting a new one
        if (slideshowTimer != null) {
            slideshowTimer.cancel(); // Cancels the ongoing slideshow
            slideshowTimer = null;   // Resets the timer reference
        }

        // Get the selected image name from the list view
        String imageName = MFXlistViewImages.getSelectionModel().getSelection().toString();
        savedStringOfImage = imageName; // Save the selected image name

        // Trim the selected image name to get a list of individual image names
        List<String> images = trimImages(imageName);

        // Timer for scheduling image display with a delay
        slideshowTimer = new Timer(); // Create a new timer for the slideshow
        int delay = 2000; // Delay between each image display (in milliseconds)

        // Schedule each image to be shown after a delay
        for (int i = 0; i < images.size(); i++) {
            final int index = i; // Create a final variable to hold the current index
            slideshowTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    showImage(images.get(index)); // Display the image at the current index
                }
            }, i * delay); // Schedule the task with a delay proportional to the index
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

    // Method to extract the index of the image selected from a list of images displayed in buttons
    private int trimImageForButtons(String image) {
        List<String> currentIndex1; // List to store the intermediate result of splitting the image string
        List<String> currentIndex2 = new ArrayList<>(); // List to store the result of the first split
        List<String> currentIndex3 = new ArrayList<>(); // List to store the result of the second split

        // Get the selected image as a string and put it into a list
        currentIndex1 = Collections.singletonList(image);

        // Iterate through currentIndex1
        for (String sr : currentIndex1) {
            // Split the string by "=" and add the first part to currentIndex2
            String[] parts = sr.split("=");
            if (parts.length > 0) {
                currentIndex2.add(parts[0]); // Add the first part before "=" to currentIndex2
            } else {
                currentIndex2.add(sr); // If no "=", add the whole string to currentIndex2
            }
        }

        // Iterate through currentIndex2
        for (String sr : currentIndex2) {
            // Split the string by "{" and add the second part to currentIndex3
            String[] parts = sr.split("\\{");
            if (parts.length > 1) {
                currentIndex3.add(parts[1]); // Add the second part after "{" to currentIndex3
            } else {
                currentIndex3.add(sr); // If no "{", add the whole string to currentIndex3
            }
        }

        // Parse the first element of currentIndex3 as an integer (the index of the image) and return it
        int finalIndex = Integer.parseInt(currentIndex3.get(0));
        return finalIndex; // Return the index of the image
    }

    // Event handler for navigating to the previous image
    @FXML
    private void handlePreviousImage(ActionEvent actionEvent) {
        // Retrieve the string representation of the currently selected image
        String selectedImageString = MFXlistViewImages.getItems().get(nextIndex).toString();

        // Check if there's no previously saved image
        if (savedStringOfImage == null) {
            // Retrieve the name of the last image in the list
            String imageName = (String) MFXlistViewImages.getItems().get(MFXlistViewImages.getItems().size() - 1);
            savedStringOfImage = imageName; // Save the name of the image
            nextIndex = MFXlistViewImages.getItems().size() - 1; // Set the next index to the last image index

            // Display the last image
            for (String image : trimImages(imageName)) {
                showImage(image);
            }
        }
        // Check if the saved image string is equal to the selected image string
        else if (savedStringOfImage.equals(selectedImageString)) {
            // Decrement the next index to navigate to the previous image
            nextIndex--;

            // If the index goes below zero, loop back to the last image
            if (nextIndex == -1) {
                nextIndex = MFXlistViewImages.getItems().size() - 1; // Reset next index to the last image index
            }

            // Check if the next index is within the bounds of the image list
            if (nextIndex <= MFXlistViewImages.getItems().size()) {
                // Get the name of the previous image
                String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();
                savedStringOfImage = imageName; // Save the name of the image

                // Display the previous image
                for (String image : trimImages(imageName)) {
                    showImage(image);
                }
            }
        }
        // If the saved image string is not equal to the selected image string
        else if (!savedStringOfImage.equals(selectedImageString)) {
            // Calculate the index of the previous image based on the saved image string
            nextIndex = trimImageForButtons(savedStringOfImage) - 1;
            String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();

            // Display the previous image
            for (String image : trimImages(imageName)) {
                savedStringOfImage = image; // Save the name of the image
                showImage(image);
            }
        }
    }


    // Event handler for navigating to the next image
    @FXML
    private void handleNextImage(ActionEvent actionEvent) {
        // Variable to hold the selected image string
        String selectedImageString = MFXlistViewImages.getItems().get(nextIndex).toString();

        // Check if there's no previously saved image
        if (savedStringOfImage == null) {
            // Retrieve the name of the first image in the list
            String imageName = (String) MFXlistViewImages.getItems().get(0);
            savedStringOfImage = imageName; // Save the name of the image

            // Display the first image
            for (String image : trimImages(imageName)) {
                showImage(image);
            }
        }
        // Check if the saved image string is equal to the selected image string
        else if (savedStringOfImage.equals(selectedImageString)) {
            // Increment the next index to navigate to the next image
            nextIndex++;

            // If the index exceeds the size of the image list, loop back to the first image
            if (nextIndex >= MFXlistViewImages.getItems().size()) {
                nextIndex = 0; // Reset next index to 0
            }

            // Check if the next index is within the bounds of the image list
            if (nextIndex <= MFXlistViewImages.getItems().size()) {
                // Get the name of the next image
                String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();
                savedStringOfImage = imageName; // Save the name of the image

                // Display the next image
                for (String image : trimImages(imageName)) {
                    showImage(image);
                }
            }
        }
        // If the saved image string is not equal to the selected image string
        else if (!savedStringOfImage.equals(selectedImageString)) {
            // Calculate the index of the next image based on the saved image string
            nextIndex = trimImageForButtons(savedStringOfImage) + 1;
            String imageName = MFXlistViewImages.getItems().get(nextIndex).toString();

            // Display the next image
            for (String image : trimImages(imageName)) {
                savedStringOfImage = image; // Save the name of the image
                showImage(image);
            }
        }
    }

}