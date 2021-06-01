package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * Class ClientMain
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public class ClientMain extends Application {

   private RemotePlayerClient client;

    /**
     * Fonction qui se charge de démarrer le client
      * @param primaryStage non utilise
     * @throws Exception
     *
     */
   @Override
    public void start(Stage primaryStage) throws Exception {
       GraphicalPlayerAdapter player = new GraphicalPlayerAdapter();

        client = (getParameters().getRaw().isEmpty() ? new RemotePlayerClient(player,"localhost",5108) :
                new RemotePlayerClient(player,getParameters().getRaw().get(0),
                     5108));
       File file = new File("C:\\Users\\ullmo\\Desktop\\tchuetape11\\tchu\\resources\\19th Floor - Bobby Richards.wav");
       AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
       Clip clip = AudioSystem.getClip();
       clip.open(audioStream);
        new Thread(() ->{client.run();
               }
)
                .start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
