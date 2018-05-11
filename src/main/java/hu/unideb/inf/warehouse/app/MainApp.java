package hu.unideb.inf.warehouse.app;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.Scanner;

/**
 * Alkalmazás indító osztály.
 */
public class MainApp extends Application {

    /**
     * Adatbázis hozzáférés jelszava.
     */
    public static String DATABASE_PASSWORD;
    private Stage primaryStage;
    private double xCoordinate, yCoordinate;
    private static Logger logger = LoggerFactory.getLogger(MainApp.class);

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        try {
            BorderPane rootView = loadFXML(new BorderPane(), "/view/MasterView.fxml");
            Scene scene = new Scene(rootView);
            scene.setFill(Color.TRANSPARENT);
            positionMod(stage, rootView);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/styles/masterStyle.css").toExternalForm());
            stage.show();
            logger.info("Alkalmazás indítása sikeres");
        } catch (Exception ex) {
            logger.error("Alkalmazás indítási hiba:\n"+ex);
        }
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception ex) {
            logger.error("Alkalmazás leállítási hiba!");
        }
    }

    private <T> T loadFXML(T identity, String file) {
        FXMLLoader loader = new FXMLLoader();
        try {
            return (T)loader.load(MainApp.class.getResource(file));
        } catch (IOException ex) {
            logger.error("Alkalmazás felállítási hiba:\n"+ex);
        }
        return identity;
    }

    /**
     * Alkalmazás belépési pontja.
     *
     * @param args paraméter
     *             adatbázis hozzáférési jelszavának megadása kötelező
     */
    public static void main(String[] args) {
        if(args.length == 0){
            Scanner bill = new Scanner(System.in);
            System.out.print("Kérem adja meg az adatbázis jelszavát: ");
            DATABASE_PASSWORD = bill.nextLine();
            System.out.println(DATABASE_PASSWORD);
            logger.info("Jelszó bekérés.");
        } else if(args.length == 1){
            DATABASE_PASSWORD = args[0];
            logger.info("Jelszó paraméteres beolvasása.");
        } else{
            System.exit(1);
            logger.info("Jelszó paraméter nem megfelelő. Kilépés.");
        }

        launch(args);
    }

    private void positionMod(Stage stage, Parent root) {
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xCoordinate = event.getSceneX();
                yCoordinate = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX()- xCoordinate);
                stage.setY(event.getScreenY()- yCoordinate);
            }
        });
    }
}
