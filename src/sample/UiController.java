package sample;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class UiController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXTextField txtContMess;
    @FXML
    private JFXListView listChat;
    @FXML
    private Label lblNotification;
    @FXML
    private Button btnSetUser;

    @FXML
    private TextField txtUser;
    @FXML
    private AnchorPane parent;

    @FXML
    private Pane chatAll, conversation1;
    @FXML
    private JFXButton btnChatAll, btn1, btnSendMess;

    @FXML
    private void close(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void maximize(MouseEvent event) {
    }

    //CHANGE TAB TO ANOTHER CONVERSATION

    @FXML
    private void handlerButtonAction(ActionEvent event){
        if(event.getSource() == btn1){
            conversation1.toFront();
        }
        else if(event.getSource() == btnChatAll){
            chatAll.toFront();
        }
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    //SET UP CONNECTION
    private String Username="kimochi";
    final String serverHost = "localhost";
    int port = 5000;
    Socket socketOfClient = null;
    BufferedWriter os = null;
    BufferedReader is = null;
    SocketReaderThread reader = null;

    public void Get_Connection(){
        try{
            socketOfClient = new Socket(serverHost, port);
            os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
        }catch (UnknownHostException e){
            System.err.println("Don't know about host " + serverHost);
            return;
        }catch (IOException e){
            System.err.println("Could't get IO for the connection to "+ serverHost);
            return;
        }
    }

    public void SendMessage(String Username, String mess) {
        try {
            os.write(Username+":"+mess);
            os.newLine();
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
    public void EnterToSendMessage(KeyEvent event) {
        if(event.getCode()==KeyCode.ENTER) {
            SendMessage();
        }
    }


    public void EnterToSetUser(KeyEvent event) {
        if(event.getCode()==KeyCode.ENTER) {
            SetUser();
        }
    }
*/
    public void SetUser() {
        this.Username = txtUser.getText();
        this.reader = new SocketReaderThread(this,this.Username);
        this.reader.start();
    }
    public void SendMessage() {
        String mess = txtContMess.getText();
        if(mess.length()!=0) {
            this.SendMessage(this.Username,mess);
        }
    }

    public String Terminate_connection() {
        try {
            os.close();
            is.close();
            socketOfClient.close();
            return "Username: "+this.Username+" terminate connection !";
        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
            return "Trying to connect to unknown host: " + e;
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException: " + e;
        }

    }
    public void ReceiveMessage(String mess) {
        listChat.getItems().add(mess);
        txtContMess.setText(null);
    }
    public void ReceiveNotification(String note) {
        lblNotification.setText("Notification: "+note);
    }

    private void makeStageDrageable() {
        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.stage.setX(event.getScreenX() - xOffset);
                Main.stage.setY(event.getScreenY() - yOffset);
                Main.stage.setOpacity(0.7f);
            }
        });
        parent.setOnDragDone((e) -> {
            Main.stage.setOpacity(1.0f);
        });
        parent.setOnMouseReleased((e) -> {
            Main.stage.setOpacity(1.0f);
        });

    }


    class SocketReaderThread extends Thread {
        UiController cont;
        String Username;
        public SocketReaderThread(UiController cont, String username){
            this.cont = cont;
            this.Username = username;
        }
        @Override
        public void run() {
            String responseLine;
            System.out.println("I am wating ... ");
            try {
                while ((responseLine = is.readLine()) != null) {
                    OnMessage(this.cont,responseLine);
                    if (responseLine.indexOf("User "+this.Username+" quit !") != -1) {
                        break;
                    }
                }

                this.cont.Terminate_connection();
                System.out.println("test terminate !");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void OnMessage(UiController cont, String line) {
            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    cont.ReceiveMessage(line);
                }
            });
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Get_Connection();
        makeStageDrageable();
    }
}
