package org.cheesy.randomroulettepicker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteEngine {
    private final List<String> participants = new ArrayList<>();
    private final Random random = new Random();

    public void addParticipant(String name) {
        if (name != null && !name.isBlank()) {
            participants.add(name.trim());
        }
    }

    public void removeParticipant(String name) {
        participants.remove(name);
    }

    public String getRandomName() {
        if (participants.isEmpty()) {
            return "Brak uczestników";
        }
        return participants.get(random.nextInt(participants.size()));
    }

    public List<String> getParticipants() {
        return new ArrayList<>(participants);
    }

    public int getCount() {
        return participants.size();
    }
}