package org.cheesy.randomroulettepicker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.cheesy.randomroulettepicker.model.AppData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataManager {

    private static final String USER_HOME = System.getProperty("user.home");

    private static final String APP_DIR = USER_HOME + File.separator + ".randomroulette";

    private static final String FILE_PATH = APP_DIR + File.separator + "data.json";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DataManager() {
        File directory = new File(APP_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void save(AppData appData) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(appData, writer);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisywania danych: " + e.getMessage());
        }
    }

    public AppData load() {
        Path path = Paths.get(FILE_PATH);

        if (!Files.exists(path)) {
            return new AppData();
        }

        try (Reader reader = new FileReader(FILE_PATH)) {
            AppData data = gson.fromJson(reader, AppData.class);
            return data != null ? data : new AppData();
        } catch (IOException e) {
            return new AppData();
        }
    }
}