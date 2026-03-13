package org.cheesy.randomroulettepicker.ui;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.List;

public class RouletteWheel extends StackPane {

    private final Group wheelGroup = new Group();
    private List<String> currentParticipants;

    private final Color[] colors = {
            Color.web("#e74c3c"), Color.web("#3498db"), Color.web("#f1c40f"),
            Color.web("#2ecc71"), Color.web("#9b59b6"), Color.web("#e67e22"),
            Color.web("#1abc9c"), Color.web("#34495e")
    };

    public RouletteWheel() {

        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().add(wheelGroup);

        Polygon pointer = createPointer();
        this.getChildren().add(pointer);
    }

    private Polygon createPointer() {
        Polygon pointer = new Polygon();
        pointer.getPoints().addAll(
                0.0, 0.0,
                20.0, 0.0,
                10.0, 20.0
        );
        pointer.setFill(Color.web("#2c3e50"));
        pointer.setTranslateY(-10);
        return pointer;
    }

    public void renderWheel(List<String> participants) {
        this.currentParticipants = participants;
        wheelGroup.getChildren().clear();
        wheelGroup.setRotate(0);

        if (participants == null || participants.isEmpty()) {
            return;
        }

        double radius = 180;
        double anglePerSlice = 360.0 / participants.size();
        double currentAngle = 0;
        int totalPeople = participants.size();

        for (int i = 0; i < participants.size(); i++) {
            Arc slice = new Arc(0, 0, radius, radius, currentAngle, anglePerSlice);
            slice.setType(ArcType.ROUND);
            slice.setFill(colors[i % colors.length]);
            slice.setStroke(Color.WHITE);
            slice.setStrokeWidth(2);

            String participantName = participants.get(i);

            if (totalPeople > 15) {

                if (participantName.contains(" ")) {
                    int spaceIndex = participantName.indexOf(" ");
                    participantName = participantName.charAt(0) + ". " + participantName.substring(spaceIndex + 1);
                }

                if (participantName.length() > 12) {
                    participantName = participantName.substring(0, 10) + "...";
                }
            } else {

                if (participantName.contains(" ")) {
                    int lastSpaceIndex = participantName.lastIndexOf(" ");
                    participantName = participantName.substring(0, lastSpaceIndex) + "\n" + participantName.substring(lastSpaceIndex + 1);
                }

                if (participantName.length() > 20) {
                    participantName = participantName.substring(0, 17) + "...";
                }
            }

            Text text = new Text(participantName);
            text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            text.setTextOrigin(javafx.geometry.VPos.CENTER);
            text.setFill(Color.WHITE);

            int fontSize = 14;
            if (totalPeople > 10) fontSize = 12;
            if (totalPeople > 15) fontSize = 11;
            if (totalPeople > 25) fontSize = 9;

            text.setFont(Font.font("System", FontWeight.BOLD, fontSize));

            double textAngle = currentAngle + (anglePerSlice / 2);
            text.getTransforms().add(new Rotate(-textAngle, 0, 0));

            double distanceOffset = totalPeople > 15 ? radius * 0.55 : radius * 0.40;
            text.getTransforms().add(new Translate(distanceOffset, 0));

            Group sliceGroup = new Group(slice, text);
            wheelGroup.getChildren().add(sliceGroup);

            currentAngle += anglePerSlice;
        }
    }

    public void spinTo(String winner, Runnable onFinished) {
        if (currentParticipants == null || currentParticipants.isEmpty()) return;

        int winnerIndex = currentParticipants.indexOf(winner);
        if (winnerIndex == -1) return;

        double anglePerSlice = 360.0 / currentParticipants.size();

        double winnerSliceCenterAngle = (winnerIndex * anglePerSlice) + (anglePerSlice / 2);

        double targetAngle = winnerSliceCenterAngle - 90;

        double currentRot = wheelGroup.getRotate();

        double currentNorm = currentRot % 360;
        if (currentNorm < 0) currentNorm += 360;

        double rotationNeeded = targetAngle - currentNorm;
        if (rotationNeeded < 0) {
            rotationNeeded += 360;
        }

        double finalRotation = currentRot + rotationNeeded + (360 * 5);

        RotateTransition rt = new RotateTransition(Duration.seconds(3), wheelGroup);

        rt.setToAngle(finalRotation);

        rt.setInterpolator(Interpolator.EASE_OUT);

        rt.setOnFinished(e -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });

        rt.play();
    }
}