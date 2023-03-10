package com.example.docapp.controllers.utilisateurs;

import com.example.docapp.dao.UtilisateurDAO;
import com.example.docapp.models.Permission;
import com.example.docapp.models.Utilisateur;
import com.example.docapp.models.ViewModel;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Vector;

public class UtilisateursController implements Initializable {
    public ListView<BorderPane> listUser;
    public TextField searchField;
    public JFXButton newUser;
    public JFXButton searchBtn;
    public VBox vbox;
    public Label totalLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            HBox root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/docapp/view/util/topBar.fxml")));
            vbox.getChildren().add(0,root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Vector<Utilisateur> userList = UtilisateurDAO.getUtilisateurs();
        totalLabel.setText(String.valueOf(userList.size()));
        for (Utilisateur user : userList) {
            BorderPane bp = createCard(user.getFirstName(),user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getId());
            listUser.getItems().add(bp);
        }

        for (Permission permission : Utilisateur.currentUser.getRole().getPermissions()) {
            if (permission.getSubject().equals("utilisateur")) {
                if (!permission.isCanAdd())
                    newUser.setDisable(true);
            }
        }

        newUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ViewModel.getInstance().getViewFactory().showNewUser();
            }
        });

        searchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String search = searchField.getText();
                listUser.getItems().clear();
                Vector<Utilisateur> userList = UtilisateurDAO.searchUtilisateurs(search);
                for (Utilisateur utilisateur : userList) {
                    BorderPane bp = createCard(utilisateur.getFirstName(),utilisateur.getLastName(), utilisateur.getEmail(), utilisateur.getPhoneNumber(), utilisateur.getId());
                    listUser.getItems().add(bp);
                }
            }
        });
    }

    public BorderPane createCard(String firstName, String lastName, String email, String phone, Integer id) {
        BorderPane root = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/docapp/view/utilisateurs/userItem.fxml"
                    )
            );
            root = (BorderPane) loader.load();
            UtilisateurItemController uc = (UtilisateurItemController) loader.getController();
            uc.setNom(firstName);
            uc.setPrenom(lastName);
            uc.setEmail(email);
            uc.setPhone(phone);
            uc.setId(id.toString());
            if(id == Utilisateur.currentUser.getId())
                uc.editBtn.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }
}
