/* CHATROOM Client.java
 * EE422C Project 7 submission by
 * Pranav Harathi
 * sh44674
 * 16460
 * Slip days used: 1
 * Fall 2016
 */
package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Client class handles UI and socket connection
 */
public class Client extends Application {

    // configuration constants
    private final int port = 8007;

    // Add field to username dialog when needed
    private String HOST_IP = "10.148.217.6"; //"128.62.43.85"; //"64.189.125.67";

    private final String MEMBER_LIST = "  Member List:                  \n";

    // Client data
    private String clientName = "Client";
    private String clientID; // name + number assigned by server
    private HashMap<String, UserGroup> myGroups = new HashMap<>(); // group id: group obj
    private List<String> allUsers = new ArrayList<>();

    // IO
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread readerThread;

    // UI Elements
    private TabPane tabPane;
    private TextField tf;
    private Button createGroupButton;

    /**
     * sets client id
     * @param id
     */
    public void setClientID(String id) {
        clientID = id;
        // Set up "You" group
        myGroups.put(clientID, new UserGroup(clientID, Collections.singletonList(clientName + clientID)));
        tabPane.getSelectionModel().getSelectedItem().setUserData(myGroups.get(clientID));
        // Send name create message
        writer.println(new NameCreateMessage(this.clientName, this.clientID));
        writer.flush();
    }

    /**
     * changes all users list
     * @param newAllUsers
     */
    public void updateAllUsers(List<String> newAllUsers) {
        allUsers = newAllUsers;
        createGroupButton.setDisable(false);
    }

    /**
     * Adds new user group
     * @param g
     */
    public void updateGroups(UserGroup g) {
        myGroups.put(g.getGroupId(), g);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Tab t = new Tab();
                t.setText(g.getGroupId());
                TextArea messageArea = new TextArea();
                messageArea.setEditable(false);
                messageArea.setWrapText(true);
                ScrollPane scrollPaneTextArea = new ScrollPane(messageArea);
                scrollPaneTextArea.setFitToHeight(true);
                scrollPaneTextArea.setFitToWidth(true);
                t.setContent(scrollPaneTextArea);
                t.setUserData(g);
                tabPane.getTabs().add(t);
            }
        });
    }

    /**
     * Set up UI
     * @param primaryStage Main UI Window
     */
    @Override
    public void start(Stage primaryStage) {
        // Text field for message entry
        BorderPane textFieldBorderPane = new BorderPane();
        textFieldBorderPane.setPadding(new Insets(5, 5, 5, 5));
        textFieldBorderPane.setStyle("-fx-border-color: green");
        tf = new TextField();
        tf.setAlignment(Pos.BOTTOM_CENTER);
        textFieldBorderPane.setCenter(tf);

        // Main text area for messages, tabbed
        // First tab created allows you to message yourself
        BorderPane messagePane = new BorderPane();
        tabPane = new TabPane();
        Tab tab = new Tab();
        tab.setText("You");
        TextArea messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        ScrollPane scrollPaneTextArea = new ScrollPane(messageArea);
        scrollPaneTextArea.setFitToHeight(true);
        scrollPaneTextArea.setFitToWidth(true);
        tab.setContent(scrollPaneTextArea);

        tabPane.getTabs().addAll(tab);
        messagePane.setCenter(tabPane);
        messagePane.setBottom(textFieldBorderPane);

        // Text info for list of members in group
        BorderPane memberPane = new BorderPane();
        TextFlow textFlow = new TextFlow();
        Text memberList = new Text();
        memberList.setText(MEMBER_LIST); // 2 sp left, 3 right, \n
        //Text testMember = new Text("  Nandakumar\n");
        textFlow.getChildren().addAll(memberList);
        memberPane.setCenter(new ScrollPane(textFlow));

        // Change the Member list
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                textFlow.getChildren().clear();
                textFlow.getChildren().add(new Text(MEMBER_LIST));
                if(((UserGroup) newTab.getUserData()) == null) return;
                for(String name : ((UserGroup) newTab.getUserData()).getMemberIds()) {
                    textFlow.getChildren().add(new Text("  " + name + "\n"));
                }
            }
        });

        // Button to create new group
        createGroupButton = new Button("New Chat Group");
        createGroupButton.setOnAction((javafx.event.ActionEvent e) -> {
            groupCreateDialog(allUsers);
        });

        memberPane.setBottom(createGroupButton);
        createGroupButton.setAlignment(Pos.CENTER);
        createGroupButton.setDisable(true);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(messagePane);
        mainPane.setRight(memberPane);

        // Scene for all this
        Scene scene = new Scene(mainPane, 500, 500);
        primaryStage.setTitle("Chat Room App");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        tf.setOnAction(event -> {
            String gId = ((UserGroup)tabPane.getSelectionModel().getSelectedItem().getUserData()).getGroupId();
            writer.println(new ChatMessage(clientName + clientID, gId, tf.getText()));
            writer.flush();
            tf.setText("");
            tf.requestFocus();
        });

        nameIPPrompt();

        tf.setEditable(false); // can be edited when name and group are assigned
        run();
    }

    // Prompt for username and IP address
    private void nameIPPrompt() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Configure Chat Room");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20,150,10,10));

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField ip = new TextField();
        ip.setPromptText("local IP");

        gridPane.add(new Label("Name: "), 0, 0);
        gridPane.add(name, 1, 0);
        gridPane.add(new Label("IP: "), 0, 1);
        gridPane.add(ip, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        ip.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == ButtonType.OK) {
                return new Pair<>(name.getText(), ip.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            this.clientName = pair.getKey();
            this.HOST_IP = pair.getValue();
        });
    }

    // Dialog to create groups
    private void groupCreateDialog(List<String> allUsers) {
        String groupName = "Group";
        Dialog<String> gcreatedialog = new Dialog<>();
        gcreatedialog.setTitle("Create Group");
        gcreatedialog.setHeaderText("Add a comma separated list of names.");

        gcreatedialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField names = new TextField();
        names.setPromptText("Comma separated list");
        grid.add(new Label("Names: "), 0, 0);
        grid.add(names, 1, 0);

        // Add list of names with a scrollview
        TextFlow userlist = new TextFlow();
        Text userlisttitle = new Text("  User List:                            \n");
        List<Text> userlistmem = new ArrayList<>();
        userlistmem.add(userlisttitle);
        for(String name : allUsers) userlistmem.add(new Text("  " + name + "  \n"));
        userlist.getChildren().addAll(userlistmem);
        grid.add(new ScrollPane(userlist), 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node okButton = gcreatedialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        names.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        gcreatedialog.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        gcreatedialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return names.getText();
            }
            return null;
        });

        Optional<String> gcreateResult = gcreatedialog.showAndWait();

        // Send Server message to create group
        gcreateResult.ifPresent(item -> {
            // Send Group Create Message Request
            List<String> groupmembers = new ArrayList<String>();
            String[] gmems = item.split(",");
            for(String s : gmems) groupmembers.add(s.trim());
            if(!groupmembers.contains(clientName + clientID)) groupmembers.add(clientName + clientID);
            writer.println(new GroupCreateMessage.Request("Group", clientID, groupmembers));
            writer.flush();
        });
    }

    // Opens socket and streams
    private void run() {
        try {
            Socket sock = new Socket(HOST_IP, port);
            InputStream istream = sock.getInputStream();
            reader = new BufferedReader(new InputStreamReader(istream));
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
            readerThread = new Thread(new IncomingReader());
            readerThread.start();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void stop(){
        writer.println("delete " + clientName + clientID);
        writer.flush();
        try {
            readerThread.interrupt();
            reader.close();
        } catch (IOException e) {

        }
        writer.close();
    }

    class IncomingReader implements Runnable {
        /**
         * Reads all messages and handles them.
         */
        public void run() {
            if(Thread.interrupted()) return;
            String message;
            try {
                while((message = reader.readLine()) != null) {
                    Message m = Message.getObject(message.trim());
                    String text = "";
                    if(m instanceof AssignClientIDMessage) {
                        setClientID(((AssignClientIDMessage) m).getClientId());
                        tf.setEditable(true);
                        text = "***Your name is " + clientName + clientID + "***\n";
                    } else if(m instanceof ChatMessage) {
                        ChatMessage cm = (ChatMessage) m;
                        for(Tab t : tabPane.getTabs()) {
                            UserGroup g = (UserGroup) t.getUserData();
                            if(cm.getGroupID().equals(g.getGroupId())) {
                                String addText = cm.getClientID() + ": " + cm.getContent();
                                TextArea ta = ((TextArea) ((ScrollPane) t.getContent()).getContent());
                                ta.appendText(addText + "\n");
                                break;
                            }
                        }
                    } else if(m instanceof UserListMessage) {
                        updateAllUsers(((UserListMessage) m).getUsers());
                    } else if(m instanceof GroupCreateMessage) {
                        GroupCreateMessage gmess = (GroupCreateMessage) m;
                        UserGroup gnew = new UserGroup(gmess.getGroupNameId(), gmess.getGroupMembers());
                        updateGroups(gnew);
                    }

                    // message added to current tab
                    ScrollPane sp =((ScrollPane) tabPane.getSelectionModel().getSelectedItem().getContent());
                    TextArea editArea = (TextArea) sp.getContent();
                    editArea.appendText(text);
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

}
