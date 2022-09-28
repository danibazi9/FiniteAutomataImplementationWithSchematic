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

public class ChematicDFA extends Application {
    private static Map<String, Map<Character, String>> transitions = Main.transitionsDFA;
    private static List<String> statesNames = Main.statesNames;
    private static Set<String> finalStates = Main.finalStates1;
    private static boolean hasMultiStates = Main.hasMultiStates;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        String initialState = "";
        Map<String, Map<Character, String>> transitions0 = new HashMap<>();
        Map<String, Map<Character, String>> newTransition = new HashMap<>();
        Image image = new Image(new FileInputStream("src/Project1/Arrow.png"));

        if (hasMultiStates) {
            Map<String, String> multiStates = new HashMap<>();
            List<String> newStatesName = new ArrayList<>();
            Set<String> newFinalStates = new HashSet<>();
            int counterState = 0;
            for (String state : statesNames) {
                multiStates.put(state, "g" + counterState);
                newStatesName.add("g" + counterState);
                counterState += 1;
            }

            for (String finalState : finalStates) {
                newFinalStates.add(multiStates.get(finalState));
            }

            statesNames = newStatesName;
            finalStates = newFinalStates;
            for (String from : transitions.keySet()) {
                newTransition.put(multiStates.get(from), new HashMap<>());
                for (Character symbol : transitions.get(from).keySet()) {
                    newTransition.get(multiStates.get(from)).put(symbol, multiStates.get(transitions.get(from).get(symbol)));
                }
            }
        } else {
            newTransition = transitions;
        }

        for (String state: statesNames) {
            if (state.contains("q0") || state.contains("g0")) {
                initialState = state;
                transitions0.put(initialState, new HashMap<>());
                for (Character c: newTransition.get(state).keySet()) {
                    transitions0.get(initialState).put(c, newTransition.get(state).get(c));
                }
                statesNames.remove(state);
                newTransition.remove(state);
                break;
            }
        }

        HashMap<String, int[]> positions = new HashMap<>();
        Collections.sort(statesNames);
//        statesNames.clear();
//        Collections.addAll(statesNames, "g1", "g2", "g3", "g4", "g5", "g6", "g7", "g8", "g9", "g10");



        Group root = new Group();

        Text title = new Text("Chematic Visualization of DFA");
        title.setX((statesNames.size() - 1) * 200 * 4.2 / 10);
        title.setY(70);
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 36));
        title.setFill(Color.rgb(255, 0, 30));
        root.getChildren().add(title);

        int counterX = 0;
        Circle circle0 = new Circle();
        circle0.setCenterX(100 + counterX);
        circle0.setCenterY(400);
        circle0.setRadius(30);
        int[] position0 = new int[2];
        position0[0] = 100 + counterX;
        position0[1] = 400;
        positions.put(initialState, position0);

        circle0.setFill(Color.GOLD);
        circle0.setStroke(Color.BLUE);
        circle0.setStrokeWidth(4);
        root.getChildren().add(circle0);

        if (finalStates.contains(initialState)) {
            Circle circleFinal0 = new Circle();
            circleFinal0.setCenterX(100);
            circleFinal0.setCenterY(400);
            circleFinal0.setFill(Color.GOLD);
            circleFinal0.setStroke(Color.BLUE);
            circleFinal0.setStrokeWidth(4);
            circleFinal0.setRadius(25);
            root.getChildren().add(circleFinal0);
        }

        Text text0 = new Text(initialState);
        text0.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 26));
        text0.setX(85);
        text0.setY(405);
        root.getChildren().add(text0);

        counterX = 0;
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

            if (finalStates.contains(state)) {
                Circle circleFinal = new Circle();
                circleFinal.setCenterX(circleX);
                circleFinal.setCenterY(circleY);
                circleFinal.setFill(Color.GOLD);
                circleFinal.setStroke(Color.BLUE);
                circleFinal.setStrokeWidth(4);
                circleFinal.setRadius(24);
                root.getChildren().add(circleFinal);
            }

            Text text = new Text(state);
            text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 26));
            text.setX(265 + counterX);
            text.setY(405);

            root.getChildren().add(text);
            counterX += 150;
        }

        for (String srcState: newTransition.keySet()) {
            for (String destState : newTransition.get(srcState).values()) {
                int firstNum = Integer.parseInt(srcState.substring(1, 2));
                int secondNum = Integer.parseInt(destState.substring(1, 2));

                int differenceOfNums = Math.abs(firstNum - secondNum);

                int middleX = (positions.get(srcState)[0] + positions.get(destState)[0]) / 2;
                int middleY = (differenceOfNums == 1) ? 350 : (350 - 150 * (differenceOfNums - 1));
                middleY = (differenceOfNums > 4) ? (350 + 180 * (differenceOfNums - 3)) : middleY;

                ImageView arrowHead = new ImageView(image);
                arrowHead.setFitHeight(25);
                arrowHead.setFitWidth(25);
                arrowHead.setX(middleX + 15);

                if (differenceOfNums == 1) {
                    arrowHead.setY(middleY + 13);
                } else {
                    arrowHead.setY(middleY + 88);
                }

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
                                break;
                            case 7:
                                arrowHead.setY(middleY - 290);
                                break;
                            case 8:
                                arrowHead.setY(middleY - 300);
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
                                break;
                            case 7:
                                arrowHead.setY(middleY - 323);
                                break;
                            case 8:
                                arrowHead.setY(middleY - 412);
                                break;
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
                for (Character alph: newTransition.get(srcState).keySet()) {
                    if (newTransition.get(srcState).get(alph).equals(destState)) {
                        alphabets.add(alph);
                    }
                }

                String text = "";
                for (Character alph: alphabets) {
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
                    }
                    else if (differenceOfNums == 7) {
                        textY += 20;
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

        for (String srcState: transitions0.keySet()) {
            for (String destState : transitions0.get(srcState).values()) {
                int firstNum = Integer.parseInt(srcState.substring(1, 2));
                int secondNum = Integer.parseInt(destState.substring(1, 2));

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
                                break;
                            case 7:
                                arrowHead.setY(middleY - 290);
                                break;
                            case 8:
                                arrowHead.setY(middleY - 360);
                                break;
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
                for (Character alph: transitions0.get(srcState).keySet()) {
                    if (transitions0.get(srcState).get(alph).equals(destState)) {
                        alphabets.add(alph);
                    }
                }

                String text = "";
                for (Character alph: alphabets) {
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

                if (differenceOfNums == 7)
                    textY += 30;
                if (differenceOfNums == 8)
                    textY += 30;

                if (firstNum == secondNum) {
                    textY += 80;
                }
                transitionAlphabet.setY(textY);
                transitionAlphabet.setX(textX);

                root.getChildren().add(arrowHead);
                root.getChildren().add(transitionAlphabet);
            }
        }


        Scene scene = new Scene(root, (statesNames.size() + 1) * 200 - 50, 600 + (115 * (newTransition.size() / 4)));
        scene.setFill(Color.rgb(206, 197, 146));
        primaryStage.setTitle("Schematic DFA");
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
