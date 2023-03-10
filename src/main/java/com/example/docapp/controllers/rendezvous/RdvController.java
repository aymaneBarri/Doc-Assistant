package com.example.docapp.controllers.rendezvous;

import com.example.docapp.dao.PatientDAO;
import com.example.docapp.dao.RendezVousDAO;
import com.example.docapp.models.*;
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

public class RdvController implements Initializable {
    public VBox vbox;
    public TextField searchField;
    public JFXButton searchBtn;
    public JFXButton newRdv;
    public ListView<BorderPane> listRdv;
    public Label totalLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            HBox root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/docapp/view/util/topBar.fxml")));
            vbox.getChildren().add(0,root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Permission permission : Utilisateur.currentUser.getRole().getPermissions()) {
            if (permission.getSubject().equals("rendez_vous")) {
                if (!permission.isCanAdd())
                    newRdv.setDisable(true);
            }
        }

        newRdv.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ViewModel.getInstance().getViewFactory().showNewRdv("", "");
            }
        });

        Vector<RendezVous> rdvList = RendezVousDAO.getAllRendezVous("");
        totalLabel.setText(String.valueOf(rdvList.size()));
        for (RendezVous rdv : rdvList) {
            BorderPane bp = createCard(rdv.getRendezVousDate(),String.valueOf(rdv.getId()), rdv.getId_patient());
            listRdv.getItems().add(bp);
        }

        searchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String search = searchField.getText();
                if(!search.isEmpty()){
                    listRdv.getItems().clear();
                    Vector<RendezVous> rdvList = RendezVousDAO.getAllRendezVous(search);
                    for (RendezVous rdv : rdvList) {
                        BorderPane bp = createCard(rdv.getRendezVousDate(),String.valueOf(rdv.getId()), rdv.getId_patient());
                        listRdv.getItems().add(bp);
                    }
                }
            }
        });
    }

    public BorderPane createCard(String date,String rdvID, Integer id) {
        BorderPane root = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/docapp/view/rendezvous/rdvItem.fxml"
                    )
            );

            root = loader.load();
            RdvItemController rc = loader.getController();
            rc.dateRdv.setText(date);
            rc.rdvID.setText(rdvID);
            rc.patientID.setText(id.toString());
            Patient patient = PatientDAO.getPatientByID(id.toString());
            rc.nomPatient.setText(patient.getFirstName()+" "+patient.getLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }
}
