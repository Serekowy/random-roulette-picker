package org.cheesy.randomroulettepicker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.cheesy.randomroulettepicker.model.AppData;
import org.cheesy.randomroulettepicker.model.Department;
import org.cheesy.randomroulettepicker.service.DataManager;
import org.cheesy.randomroulettepicker.ui.RouletteWheel;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private ComboBox<Department> departmentComboBox;
    @FXML private Button addDepartmentBtn;
    @FXML private TextField employeeNameField;
    @FXML private Button addEmployeeBtn;
    @FXML private ListView<String> employeeListView;
    @FXML private Button removeEmployeeBtn;
    @FXML private Label lastWinnerLabel;
    @FXML private StackPane rouletteContainer;
    @FXML private Button spinButton;
    @FXML private Label winnerLabel;
    @FXML private Button removeDepartmentBtn;

    private AppData appData;
    private final DataManager dataManager = new DataManager();
    private final Random random = new Random();

    private RouletteWheel rouletteWheel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        appData = dataManager.load();

        rouletteWheel = new RouletteWheel();
        rouletteContainer.getChildren().clear();
        rouletteContainer.getChildren().add(rouletteWheel);

        departmentComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            refreshEmployeeList(newValue);
        });

        lastWinnerLabel.setText("Ostatni zwycięzca: " + appData.getLastWinner());

        refreshDepartmentComboBox();

        updateRouletteWheel();
    }

    private void refreshDepartmentComboBox() {
        departmentComboBox.getItems().clear();
        departmentComboBox.getItems().addAll(appData.getDepartments());
        if (!appData.getDepartments().isEmpty()) {
            departmentComboBox.getSelectionModel().selectFirst();
        }
    }

    private void refreshEmployeeList(Department department) {
        employeeListView.getItems().clear();
        if (department != null) {
            employeeListView.getItems().addAll(department.getEmployees());
        }
    }

    private void updateRouletteWheel() {
        List<String> allParticipants = appData.getAllParticipants();
        rouletteWheel.renderWheel(allParticipants);
    }

    @FXML
    private void handleAddDepartment(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nowy dział");
        dialog.setHeaderText("Dodawanie nowego działu");
        dialog.setContentText("Podaj nazwę działu:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank() && appData.getDepartmentByName(name.trim()) == null) {
                Department newDept = new Department(name.trim());
                appData.getDepartments().add(newDept);
                dataManager.save(appData);
                refreshDepartmentComboBox();
                departmentComboBox.getSelectionModel().select(newDept);

            }
        });
    }

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        Department selectedDept = departmentComboBox.getSelectionModel().getSelectedItem();
        if (selectedDept == null) {
            showAlert("Uwaga", "Musisz najpierw utworzyć i wybrać dział!");
            return;
        }

        String empName = employeeNameField.getText();
        if (empName != null && !empName.isBlank()) {
            selectedDept.addEmployee(empName);
            dataManager.save(appData);

            refreshEmployeeList(selectedDept);
            employeeNameField.clear();

            updateRouletteWheel();
        }
    }

    @FXML
    private void handleRemoveDepartment(ActionEvent event) {
        Department selectedDept = departmentComboBox.getSelectionModel().getSelectedItem();

        if (selectedDept != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdzenie usunięcia");
            alert.setHeaderText("Usuwasz dział: " + selectedDept.getName());
            alert.setContentText("Czy na pewno chcesz usunąć ten dział wraz ze wszystkimi przypisanymi do niego pracownikami? Tej operacji nie można cofnąć.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    appData.getDepartments().remove(selectedDept);
                    dataManager.save(appData);
                    refreshDepartmentComboBox();
                    updateRouletteWheel();
                }
            });
        } else {
            showAlert("Uwaga", "Najpierw wybierz dział do usunięcia.");
        }
    }

    @FXML
    private void handleRemoveEmployee(ActionEvent event) {
        Department selectedDept = departmentComboBox.getSelectionModel().getSelectedItem();
        String selectedEmp = employeeListView.getSelectionModel().getSelectedItem();

        if (selectedDept != null && selectedEmp != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdzenie usunięcia");
            alert.setHeaderText("Usuwasz pracownika: " + selectedEmp);
            alert.setContentText("Czy na pewno chcesz usunąć tę osobę z listy losowania?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    selectedDept.removeEmployee(selectedEmp);
                    dataManager.save(appData);
                    refreshEmployeeList(selectedDept);
                    updateRouletteWheel();
                }
            });
        } else {
            showAlert("Uwaga", "Wybierz pracownika z listy, którego chcesz usunąć.");
        }
    }

    @FXML
    private void handleSpin(ActionEvent event) {
        List<String> allParticipants = appData.getAllParticipants();

        if (allParticipants.isEmpty()) {
            winnerLabel.setText("Dodaj uczestników!");
            return;
        }

        spinButton.setDisable(true);
        winnerLabel.setText("Trwa losowanie...");

        int winningIndex = random.nextInt(allParticipants.size());
        String winner = allParticipants.get(winningIndex);

        rouletteWheel.spinTo(winner, () -> {

            winnerLabel.setText("Wygrywa: " + winner);

            appData.setLastWinner(winner);
            lastWinnerLabel.setText("Ostatni zwycięzca: " + winner);
            dataManager.save(appData);

            spinButton.setDisable(false);
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}