package Project1;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class ChematicNFA extends Application {
    private static Map<String, Map<Character, List<String>>> transitions = Main.transitionsNFA;
    private static List<String> statesNames = Main.statesNames;
    private static Set<String> finalStates = Main.finalStates1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        String initialState = "q0";
        Map<String, Map<Character, List<String>>> transitions0 = new HashMap<>();
        Image image = new Image(new FileInputStream("src/Project1/Arrow.png"));

        for (String state: transitions.keySet()) {
            if (state.contains("q0")) {
                initialState = state;
                transitions0.put(initialState, new HashMap<>());
                for (Character c: transitions.get(state).keySet()) {
                    transitions0.get(initialState).put(c, transitions.get(state).get(c));
                }
                transitions.remove(state);
                statesNames.remove(state);
            }
        }

        HashMap<String, int[]> positions = new HashMap<>();
        Collections.sort(statesNames);

        Group root = new Group();

        Text title = new Text("Chematic Visualization of NFA");
        title.setX((statesNames.size() - 1) * 200 * 3.5 / 10);
        title.setY(70);
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 36));
        title.setFill(Color.rgb(255, 0, 30));
        root.getChildren().add(title);

        Circle circle0 = new Circle();
        circle0.setCenterX(100);
        circle0.setCenterY(400);
        circle0.setRadius(30);
        int[] position0 = new int[2];
        position0[0] = 100;
        position0[1] = 400;
        positions.put(initialState, position0);

        circle0.setFill(Color.GOLD);
        circle0.setStroke(Color.BLUE);
        circle0.setStrokeWidth(4);
        root.getChildren().add(circle0);

        for (String finalState: finalStates) {
            if (finalState.contains("q0")) {
                Circle circleFinal0 = new Circle();
                circleFinal0.setCenterX(100);
                circleFinal0.setCenterY(400);
                circleFinal0.setFill(Color.GOLD);
                circleFinal0.setStroke(Color.BLUE);
                circleFinal0.setStrokeWidth(4);
                circleFinal0.setRadius(25);
                root.getChildren().add(circleFinal0);
            }
        }

        Text text0 = new Text(initialState);
        text0.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 26));
        text0.setX(85);
        text0.setY(405);
        root.getChildren().add(text0);

        int counterX = 0;
        for (String state: statesNames) {
            Circle circle = new Circle();
            int circleX = 280 + counterX;
            int circleY = 400;
            circle.setCenterX(circleX);
            circle.setCenterY(circleY);

            int[] position = new int[2];
            position[0] = circleX;
            position[1] = circleY;
            positions.put(state, position);

            circle.setRadius(30);
            circle.setFill(Color.GOLD);
            circle.setStroke(Color.BLUE);
            circle.setStrokeWidth(4);
            root.getChildren().add(circle);

            for (String finalState: finalStates) {
                if (finalState.contains(state)) {
                    Circle circleFinal = new Circle();
                    circleFinal.setCenterX(circleX);
                    circleFinal.setCenterY(circleY);
                    circleFinal.setFill(Color.GOLD);
                    circleFinal.setStroke(Color.BLUE);
                    circleFinal.setStrokeWidth(4);
                    circleFinal.setRadius(24);
                    root.getChildren().add(circleFinal);
                }
            }

            Text text = new Text(state);
            text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 26));
            text.setX(265 + counterX);
            text.setY(405);

            root.getChildren().add(text);
            counterX += 180;
        }

        for (String srcState: transitions.keySet()) {
            for (List<String> destsList : transitions.get(srcState).values()) {
                for (String destState : destsList) {
                    int firstNum = Integer.parseInt(srcState.replace("q", ""));
                    int secondNum = Integer.parseInt(destState.replace("q", ""));

                    int differenceOfNums = Math.abs(firstNum - secondNum);

                    int middleX = (positions.get(srcState)[0] + positions.get(destState)[0]) / 2;
                    int middleY = (differenceOfNums == 1) ? 350 : (350 - 150 * (differenceOfNums - 1));
                    middleY = (differenceOfNums > 4) ? (350 + 180 * (differenceOfNums - 3)) : middleY;

                    ImageView arrowHead = new ImageView(image);
                    arrowHead.setFitHeight(25);
                    arrowHead.setFitWidth(25);
                    arrowHead.setX(middleX + 15);

                    int startX = 0, startY = 0, endX = 0, endY = 0;

                    if (firstNum < secondNum) {
                        startX = positions.get(srcState)[0] + 30;
                        startY = positions.get(srcState)[1];
                        endX = positions.get(destState)[0] - 30;
                        endY = positions.get(destState)[1];

                        if (differenceOfNums == 1) {
                            arrowHead.setY(middleY + 13);
                        } else {
                            switch (differenceOfNums) {
                                case 2:
                                    arrowHead.setY(middleY + 89);
                                    break;
                                case 3:
                                    arrowHead.setY(middleY + 163);
                                    break;
                                case 4:
                                    arrowHead.setY(middleY + 238);
                                    break;
                                case 5:
                                    arrowHead.setY(middleY - 168);
                                    break;
                                case 6:
                                    arrowHead.setY(middleY - 257);
                            }
                        }

                        Rotate rotatedArrow = new Rotate(90, arrowHead.getX(), arrowHead.getY());
                        arrowHead.getTransforms().add(rotatedArrow);
                    } else if (firstNum > secondNum) {
                        startX = positions.get(srcState)[0] - 30;
                        startY = positions.get(srcState)[1];
                        endX = positions.get(destState)[0] + 30;
                        endY = positions.get(destState)[1];

                        if (differenceOfNums == 1) {
                            middleY += 100;
                            if (middleY == 550)
                                middleY += 100;
                            arrowHead.setY(middleY - 13);
                            arrowHead.setX(middleX - 20);
                        } else {
                            switch (differenceOfNums) {
                                case 2:
                                    arrowHead.setY(middleY + 113);
                                    break;
                                case 3:
                                    arrowHead.setY(middleY + 188);
                                    break;
                                case 4:
                                    arrowHead.setY(middleY + 263);
                                    break;
                                case 5:
                                    arrowHead.setY(middleY - 143);
                                    break;
                                case 6:
                                    arrowHead.setY(middleY - 233);
                            }
                            arrowHead.setX(middleX - 15);
                        }

                        Rotate rotatedArrow = new Rotate(-90, arrowHead.getX(), arrowHead.getY());
                        arrowHead.getTransforms().add(rotatedArrow);
                    } else {
                        middleY += 100;
                        arrowHead.setX(middleX - 15);
                        arrowHead.setY(middleY - 105);

                        Rotate rotatedArrow = new Rotate(-90, arrowHead.getX(), arrowHead.getY());
                        arrowHead.getTransforms().add(rotatedArrow);
                    }

                    if (differenceOfNums != 0) {
                        QuadCurve transitionLine = new QuadCurve(startX, startY, middleX, middleY, endX, endY);
                        transitionLine.setStroke(Color.BLACK);
                        transitionLine.setStrokeWidth(3);
                        transitionLine.setFill(Color.TRANSPARENT);
                        root.getChildren().add(transitionLine);
                    } else {
                        Arc loop = new Arc(positions.get(srcState)[0], positions.get(srcState)[1] + 55.0, 30.0, 30.0, 130.0, 280.0);
                        loop.setFill(Color.TRANSPARENT);
                        loop.setStrokeWidth(3);
                        loop.setStroke(Color.BLACK);
                        root.getChildren().add(loop);
                    }

                    ArrayList<Character> alphabets = new ArrayList<>();
                    for (Character alph : transitions.get(srcState).keySet()) {
                        for (String dest: transitions.get(srcState).get(alph)) {
                            if (dest.equals(destState))
                                alphabets.add(alph);
                        }
                    }

                    String text = "";
                    for (Character alph : alphabets) {
                        text += alph.toString() + ",";
                    }

                    text = text.substring(0, text.length() - 1);

                    Text transitionAlphabet = new Text(text);
                    transitionAlphabet.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 24));
                    int textX = middleX - 8;
                    int textY = (differenceOfNums == 1) ? 360 : (360 - 75 * (differenceOfNums - 1));
                    textY = (differenceOfNums > 4) ? (360 + 80 * (differenceOfNums - 3)) : textY;

                    if (firstNum > secondNum) {
                        if (differenceOfNums == 1) {
                            textY += 90;
                        } else {
                            textY -= 10;
                        }
                    }

                    if (firstNum == secondNum)
                        textY += 80;
                    transitionAlphabet.setY(textY);
                    transitionAlphabet.setX(textX);

                    root.getChildren().add(arrowHead);
                    root.getChildren().add(transitionAlphabet);
                }
            }
        }

        for (String srcState: transitions0.keySet()) {
            for (List<String> destsList : transitions0.get(srcState).values()) {
                for (String destState : destsList) {
                    int firstNum = Integer.parseInt(srcState.replace("q", ""));
                    int secondNum = Integer.parseInt(destState.replace("q", ""));

                    int differenceOfNums = Math.abs(firstNum - secondNum);

                    int middleX = (positions.get(srcState)[0] + positions.get(destState)[0]) / 2;
                    int middleY = (differenceOfNums == 1) ? 350 : (350 - 150 * (differenceOfNums - 1));
                    middleY = (differenceOfNums > 4) ? (350 + 150 * (differenceOfNums - 3)) : middleY;

                    ImageView arrowHead = new ImageView(image);
                    arrowHead.setFitHeight(25);
                    arrowHead.setFitWidth(25);
                    arrowHead.setX(middleX + 15);

                    int startX = 0, startY = 0, endX = 0, endY = 0;

                    if (firstNum < secondNum) {
                        startX = positions.get(srcState)[0] + 30;
                        startY = positions.get(srcState)[1];
                        endX = positions.get(destState)[0] - 30;
                        endY = positions.get(destState)[1];

                        if (differenceOfNums == 1) {
                            arrowHead.setY(middleY + 13);
                        } else {
                            switch (differenceOfNums) {
                                case 2:
                                    arrowHead.setY(middleY + 89);
                                    break;
                                case 3:
                                    arrowHead.setY(middleY + 163);
                                    break;
                                case 4:
                                    arrowHead.setY(middleY + 238);
                                    break;
                                case 5:
                                    arrowHead.setY(middleY - 168);
                                    break;
                                case 6:
                                    arrowHead.setY(middleY - 257);
                            }
                        }

                        Rotate rotatedArrow = new Rotate(90, arrowHead.getX(), arrowHead.getY());
                        arrowHead.getTransforms().add(rotatedArrow);
                    } else if (firstNum > secondNum) {
                        startX = positions.get(srcState)[0] - 30;
                        startY = positions.get(srcState)[1];
                        endX = positions.get(destState)[0] + 30;
                        endY = positions.get(destState)[1];

                        if (differenceOfNums == 1) {
                            middleY += 100;
                            if (middleY == 550)
                                middleY += 100;
                            arrowHead.setY(middleY - 13);
                            arrowHead.setX(middleX - 20);
                        } else {
                            switch (differenceOfNums) {
                                case 2:
                                    arrowHead.setY(middleY + 113);
                                    break;
                                case 3:
                                    arrowHead.setY(middleY + 188);
                                    break;
                                case 4:
                                    arrowHead.setY(middleY + 263);
                                    break;
                                case 5:
                                    arrowHead.setY(middleY - 143);
                                    break;
                                case 6:
                                    arrowHead.setY(middleY - 233);
                            }
                            arrowHead.setX(middleX - 15);
                        }

                        Rotate rotatedArrow = new Rotate(-90, arrowHead.getX(), arrowHead.getY());
                        arrowHead.getTransforms().add(rotatedArrow);
                    } else {
                        middleY += 100;
                        arrowHead.setX(middleX - 15);
                        arrowHead.setY(middleY - 105);

                        Rotate rotatedArrow = new Rotate(-90, arrowHead.getX(), arrowHead.getY());
                        arrowHead.getTransforms().add(rotatedArrow);
                    }

                    if (differenceOfNums != 0) {
                        QuadCurve transitionLine = new QuadCurve(startX, startY, middleX, middleY, endX, endY);
                        transitionLine.setStroke(Color.BLACK);
                        transitionLine.setStrokeWidth(3);
                        transitionLine.setFill(Color.TRANSPARENT);
                        root.getChildren().add(transitionLine);
                    } else {
                        Arc loop = new Arc(positions.get(srcState)[0], positions.get(srcState)[1] + 55.0, 30.0, 30.0, 130.0, 280.0);
                        loop.setFill(Color.TRANSPARENT);
                        loop.setStrokeWidth(3);
                        loop.setStroke(Color.BLACK);
                        root.getChildren().add(loop);
                    }

                    ArrayList<Character> alphabets = new ArrayList<>();
                    for (Character alpha : transitions0.get(srcState).keySet()) {
                        for (String dest: transitions0.get(srcState).get(alpha)) {
                            if (dest.equals(destState))
                                alphabets.add(alpha);
                        }
                    }

                    String text = "";
                    for (Character alph : alphabets) {
                        text += alph.toString() + ",";
                    }

                    text = text.substring(0, text.length() - 1);

                    Text transitionAlphabet = new Text(text);
                    transitionAlphabet.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 24));
                    int textX = middleX - 8;
                    int textY = (differenceOfNums == 1) ? 360 : (360 - 75 * (differenceOfNums - 1));
                    textY = (differenceOfNums > 4) ? (360 + 80 * (differenceOfNums - 3)) : textY;

                    if (firstNum > secondNum) {
                        if (differenceOfNums == 1) {
                            textY += 90;
                        } else {
                            textY -= 10;
                        }
                    }

                    if (firstNum == secondNum)
                        textY += 80;
                    transitionAlphabet.setY(textY);
                    transitionAlphabet.setX(textX);

                    root.getChildren().add(arrowHead);
                    root.getChildren().add(transitionAlphabet);
                }
            }
        }

        Scene scene = new Scene(root, (statesNames.size() + 1) * 200 - 50, 600 + (100 * (transitions.size() / 4)));
        scene.setFill(Color.rgb(206, 197, 146));
        primaryStage.setTitle("Schematic NFA");
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
