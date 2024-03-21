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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImageViewController {

    @FXML
    private ImageView imageViewShowImage;
    @FXML
    private ListView listViewImages;

    @FXML
    private MFXListView MFXlistViewImages;

    public void setup() {
        List<File> imageList = listImages("resources/images");

        List<String> imageNames = new ArrayList<>();
        for (File imageFile : imageList) {
            imageNames.add(imageFile.getName());
        }

        ObservableList<String> observableImageNames = FXCollections.observableArrayList(imageNames);
        MFXlistViewImages.setItems(observableImageNames);
    }

    public static List<File> listImages(String folderPath) {
        List<File> imageFiles = new ArrayList<>();
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
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

    private static boolean isImageFile(String fileName) {
        String[] imageExtensions = {".jpg"};
        for (String extension : imageExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
    @FXML
    private void handleSelectedImage(MouseEvent mouseEvent) throws InterruptedException {

        String imageName = MFXlistViewImages.getSelectionModel().getSelection().toString();

        for (String image : trimImages(imageName)) {
            showImage(image);
        }
        //showImage(trimImages(imageName));
    }

    private List<String> trimImages(String stringToBeTrimmed){
        List<String> firstTrimmingofImages = new ArrayList<>();
        List<String> secondTrimmingofImages = new ArrayList<>();
        List<String> finalListofImages = new ArrayList<>();

        firstTrimmingofImages.addAll(List.of(stringToBeTrimmed.split(",")));
        for (String sr : firstTrimmingofImages){
            String[] parts = sr.split("=");
            if (parts.length > 1) {
                secondTrimmingofImages.add(parts[1]);
            } else {
                secondTrimmingofImages.add(sr);
            }
        }
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

    private void showImage(String imageName) {
        if (imageName != null) {
            String imagePath = "resources/images/" + imageName;
            Image image = new Image(new File(imagePath).toURI().toString());
            imageViewShowImage.setImage(image);
        }
    }
    @FXML
    private void handlePreviousImage(ActionEvent actionEvent) {
    }

    @FXML
    private void handleNextImage(ActionEvent actionEvent) {
        ObservableList allimages = MFXlistViewImages.getItems();



    }
}

