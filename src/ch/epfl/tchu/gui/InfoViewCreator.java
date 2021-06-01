package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.util.Map;

import static javafx.geometry.Orientation.HORIZONTAL;
/**
 * Class InfoViewCreator
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */
final class InfoViewCreator {
    /**
     *createInfoView, permet de créer la vue des informations
     *
     * @param id l'identité du joueur auquel l'interface correspond
     * @param playerNames la table associative des noms des joueurs
     * @param gameState l'état de jeu observable,
     * @param infos une liste (observable) contenant les informations sur le déroulement de la partie, sous la forme d'instances de Text.
     * @return  la vue des informations
     */
    public static Node createInfoView(PlayerId id, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> infos) {
        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("info.css", "colors.css");
        VBox statsPlayers = new VBox();
        statsPlayers.setId("player-stats");


        for (int i = 0; i < PlayerId.COUNT; ++i) {

            PlayerId playerId = i == 0 ? id : id.next();
            System.out.println(playerId);


            TextFlow playerCurrent = new TextFlow();
            playerCurrent.getStyleClass().add(playerId.name());

            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");

            Text textPlayer = new Text();
            textPlayer.fillProperty().set(Paint.valueOf("white"));

            textPlayer.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(playerId), gameState.getTickets(playerId), gameState.getCards(playerId), gameState.getWagon(playerId), gameState.getPoints(playerId)));
            playerCurrent.getChildren().addAll(circle, textPlayer);
            statsPlayers.getChildren().add(playerCurrent);
        }


        Separator separator = new Separator();

        TextFlow messages = new TextFlow();

        messages.setId("game-info");
        Bindings.bindContent(messages.getChildren(), infos);




        vBox.getChildren().addAll(statsPlayers, separator, messages);


        return vBox;

    }
}
