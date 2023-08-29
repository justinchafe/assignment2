package Part1;

import Utility.ColorUtility;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    final int NUM_CIRCLE = 40;
    final int NUM_MENU_ITEMS = 10;
    final float CIRCLE_RADIUS = 25;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        CenterPane centerPane = new CenterPane(Color.BLACK);
        FlowPane flowPane = createFlowPane();

        //Label:
        Label bottomLabel = new Label("List View Selection: " + "Nothing Selected");
        bottomLabel.setStyle("-fx-background-color: DAE6F3;");
        bottomLabel.setMaxWidth(Double.MAX_VALUE);
        bottomLabel.setAlignment(Pos.CENTER);

        //ListView:
        ListView<String> listView = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList(ColorUtility.getColorNameList());
        listView.setItems(items);
        //EventHandler for listView items:
        listView.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldVal, newVal) -> {
                    bottomLabel.setText("List View Selection: " + newVal);
                    bottomLabel.setTextFill(Color.web(newVal));
                    centerPane.setCenterPaneColor(Color.web(newVal));
                });

        //FlowPane Circles:
        List<Circle> cList = createCircles();
        for (Circle l : cList) {
            flowPane.getChildren().add(l);
            //Listen for mouse events on each circle:
            l.setOnMousePressed(event->{
                Color c = (Color) l.getFill();
                int index = items.indexOf(getKeyFromValue(Utility.ColorUtility.getColorsMap(),c));
                listView.getSelectionModel().select(items.get(index)); //Fires the ListView listener to set bottomLabel and Color
            });
        }

        //Menu Bar:
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: DAE6F3;");
        //Menus:
        Menu menuFile = createMenuItems("File", NUM_MENU_ITEMS);
        Menu menuEdit = createMenuItems("Edit", NUM_MENU_ITEMS);
        Menu menuView = createMenuItems("View", NUM_MENU_ITEMS);
        //Add Menus to MenuBar:
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

        //BorderPane:
        borderPane.setTop(menuBar);
        borderPane.setLeft(flowPane);
        borderPane.setBottom(bottomLabel);
        borderPane.setRight(listView);
        borderPane.setCenter(centerPane);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Part 1 - Assignment 2");
        primaryStage.show();

    }

    private String getKeyFromValue(Map<String, Color> map, Color value) {
        for (Map.Entry<String, Color> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Menu createMenuItems(String text, int numItems) {
        int i;
        Menu menu = new Menu(text);
        for (i = 0; i < numItems; i++) {
            menu.getItems().add(new MenuItem(text + " Menu Item " + (i+1)));
        }
        return menu;
    }

    private List<Circle> createCircles() {
        List<Circle> list = new ArrayList<>();
        List<Color> randColors = ColorUtility.getColorList();
        Collections.shuffle(randColors);
        for (int i = 0; i < NUM_CIRCLE; i++) {
            list.add(new Circle(CIRCLE_RADIUS));
            list.get(i).setFill(randColors.get(i));
        }
        return list;
    }

    private FlowPane createFlowPane() {
        FlowPane flowPane = new FlowPane();
        flowPane.setPadding(new Insets(5, 0, 5, 0));
        flowPane.setVgap(4);
        flowPane.setHgap(4);
        flowPane.setPrefWrapLength(220);
        flowPane.setStyle("-fx-background-color: DAE6F3;");
        return flowPane;
    }
}
