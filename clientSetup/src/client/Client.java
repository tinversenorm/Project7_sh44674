package client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pranavh on 11/28/16.
 */
public class Client extends Application {

    final String MEMBER_LIST = "  Member List:                  \n";

    // IOStreams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;

    @Override
    public void start(Stage primaryStage) {
        // Text field for message entry
        BorderPane textFieldBorderPane = new BorderPane();
        textFieldBorderPane.setPadding(new Insets(5, 5, 5, 5));
        textFieldBorderPane.setStyle("-fx-border-color: green");
        TextField tf = new TextField();
        tf.setAlignment(Pos.BOTTOM_CENTER);
        textFieldBorderPane.setCenter(tf);

        // Main text area for messages, tabbed
        BorderPane messagePane = new BorderPane();
        TabPane tabPane = new TabPane();
        Tab tab = new Tab();
        tab.setText("You");
        TextArea messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        ScrollPane scrollPaneTextArea = new ScrollPane(messageArea);
        scrollPaneTextArea.setFitToHeight(true);
        scrollPaneTextArea.setFitToWidth(true);
        tab.setContent(scrollPaneTextArea);

        
        // Test changing member list
        List<String> namestab1 = new ArrayList<>();
        namestab1.add("Nandakumar");
        namestab1.add("Pranav");
        tab.setUserData(namestab1);

        Tab tab2 = new Tab();
        tab2.setText("More 2");
        // Test changing member list
        List<String> namestab2 = new ArrayList<>();
        namestab2.add("Vallath");
        namestab2.add("Harathi");
        tab2.setUserData(namestab2);


        tabPane.getTabs().addAll(tab, tab2);
        messagePane.setCenter(tabPane);
        messagePane.setBottom(textFieldBorderPane);

        // Text info for list of members in group
        BorderPane memberPane = new BorderPane();
        TextFlow textFlow = new TextFlow();
        Text memberList = new Text();
        memberList.setText(MEMBER_LIST); // 2 sp left, 3 right, \n
        Text testMember = new Text("  Nandakumar\n");
        textFlow.getChildren().addAll(memberList, testMember);
        memberPane.setCenter(new ScrollPane(textFlow));

        // Change the Member list
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                //if(newTab == tab) {
                List<Text> memberTexts = new ArrayList<Text>();
                textFlow.getChildren().clear();
                textFlow.getChildren().add(new Text(MEMBER_LIST));
                for(String name : (List<String>) newTab.getUserData()) {
                    textFlow.getChildren().add(new Text("  " + name + "\n"));
                }
            }
        });

        // Button to create new group

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(messagePane);
        mainPane.setRight(memberPane);

        // Scene for all this
        Scene scene = new Scene(mainPane, 500, 500);
        primaryStage.setTitle("Chat Room App");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }

}
