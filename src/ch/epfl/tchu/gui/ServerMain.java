package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
/**
 * Class ServerMain
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */
public class ServerMain extends Application {
    /**
     * Fonction qui se charge de démarrer le server
     * @param primaryStage non utilise
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

            ServerSocket serverSocket = new ServerSocket(5108);
            Socket socket = serverSocket.accept() ;
            Player playerProxy = new RemotePlayerProxy(socket);
            Map<PlayerId,String> playerNames=Map.of(PLAYER_1,getParameters().getRaw().isEmpty()?"Ada":getParameters().getRaw().get(0),
                    PLAYER_2,getParameters().getRaw().isEmpty()?"Charles": getParameters().getRaw().get(1));
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

            Map<PlayerId, Player> players =
                    Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                            PLAYER_2, playerProxy);
            Random rng = new Random();
/**
            File file = new File("C:\\Users\\ullmo\\Desktop\\tchuetape11\\tchu\\resources\\19th Floor - Bobby Richards.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            **/

            new Thread(() -> Game.play(players, playerNames, tickets, rng))
                    .start();




        }
    public static void main(String[] args) {
        launch(args);
    }
}
