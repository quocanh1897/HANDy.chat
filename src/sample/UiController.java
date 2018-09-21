package sample;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UiController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private AnchorPane parent;

    @FXML
    private Pane conversation1, conversation2;
    @FXML
    private JFXButton btn1, btn2;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        makeStageDrageable();
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

    @FXML
    private void close(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void maximize(MouseEvent event) {
    }

    @FXML
    private void handlerButtonAction(ActionEvent event){
        if(event.getSource() == btn1){
            conversation1.toFront();
        }else if(event.getSource() == btn2){
            conversation2.toFront();
        }
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

}
