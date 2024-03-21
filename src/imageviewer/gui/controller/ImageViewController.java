package imageviewer.gui.controller;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private void handleSelectedImage(MouseEvent mouseEvent) {
        List<String> listOfImagesSelected = new ArrayList<>();

        String imageName = MFXlistViewImages.getSelectionModel().getSelection().toString();
        listOfImagesSelected.add(imageName);
        for (String sr : listOfImagesSelected){
            System.out.println(sr);
        }


        //String imageName = (String) listViewImages.getSelectionModel().getSelectedItem();

        showImage(imageName);
    }

    private void showImage(String imageName) {
        if (imageName != null) {
            String imagePath = "resources/images/" + imageName;
            Image image = new Image(new File(imagePath).toURI().toString());
            imageViewShowImage.setImage(image);
        }
    }
}

