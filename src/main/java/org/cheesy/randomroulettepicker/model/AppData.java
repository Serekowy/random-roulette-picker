package org.cheesy.randomroulettepicker.model;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private String lastWinner;
    private List<Department> departments;

    public AppData() {
        this.lastWinner = "Brak danych";
        this.departments = new ArrayList<>();
    }

    public String getLastWinner() {
        return lastWinner;
    }

    public void setLastWinner(String lastWinner) {
        this.lastWinner = lastWinner;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public List<String> getAllParticipants() {
        List<String> allEmployees = new ArrayList<>();
        for (Department dept : departments) {
            allEmployees.addAll(dept.getEmployees());
        }
        return allEmployees;
    }

    public Department getDepartmentByName(String name) {
        for (Department dept : departments) {
            if (dept.getName().equals(name)) {
                return dept;
            }
        }
        return null;
    }
}