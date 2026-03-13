package org.cheesy.randomroulettepicker.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String name;
    private List<String> employees;

    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEmployees() {
        return employees;
    }

    public void addEmployee(String employeeName) {
        if (employeeName != null && !employeeName.isBlank() && !employees.contains(employeeName.trim())) {
            employees.add(employeeName.trim());
        }
    }

    public void removeEmployee(String employeeName) {
        employees.remove(employeeName);
    }

    @Override
    public String toString() {
        return name;
    }
}