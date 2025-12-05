package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import sk.upjs.paz.entity.Animal;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.storage.AnimalDao;
import sk.upjs.paz.storage.Factory;


import java.util.List;

public class ViewController {

    private AnimalDao animalDao = (AnimalDao) Factory.INSTANCE.getAnimalDao();

    @FXML
    private ListView<Animal> animalsListView;

    @FXML
    private Button loadUsersButton;


    public void loadUsersButtonAction(ActionEvent actionEvent) {
        //  usersListView.getItems().clear();
        List<Animal> animals = animalDao.getAll();
        System.out.println(animals);
        animalsListView.getItems().addAll(animals);
    }


}