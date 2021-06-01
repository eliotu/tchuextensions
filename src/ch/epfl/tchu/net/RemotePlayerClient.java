package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Classe RemotePlayerClient.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public final class RemotePlayerClient {

    private final Player player;
    private final String name;
    private final int port;

    /**
     * Constructeur du client de joueur distant.
     *
     * @param player joueur
     * @param name   nom
     * @param port   port pour se connecter au mandataire
     */

    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }

    /**
     * Cette méthode effectue une boucle durant laquelle elle attend un message en provenance du mandataire,
     * le découpe en utilisant le caractère d'espacement comme séparateur,
     * détermine le type du message en fonction de la première chaîne résultant du découpage,
     * en fonction de ce type de message, désérialise les arguments, appelle la méthode correspondante du joueur si
     * cette méthode retourne un résultat, le sérialise pour le renvoyer au mandataire en réponse.
     */


    public void run() {
        try (Socket socket = new Socket(name, port);
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(),
                                     US_ASCII));

             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(),
                                     US_ASCII))) {
            String line;
            while ((line = r.readLine()) != null) {
                List<String> messagge = Arrays.stream(line.split(Pattern.quote(" "), -1))
                        .collect(Collectors.toList());
                MessageId id = MessageId.valueOf(messagge.get(0));
                switch (id) {
                    case INIT_PLAYERS:
                        List<String> names = Serdes.LIST_STRING_SERDE.deserialize(messagge.get(2));
                        player.initPlayers(Serdes.PLAYER_ID_SERDE.deserialize(messagge.get(1)), Map.of(PlayerId.PLAYER_1, names.get(0), PlayerId.PLAYER_2, names.get(1)));
                        break;

                    case RECEIVE_INFO:
                        player.receiveInfo(Serdes.STRING_SERDE.deserialize(messagge.get(1)));
                        break;
                    case UPDATE_STATE:
                        PublicGameState game = Serdes.GET_PUBLIC_GAME_STATE.deserialize(messagge.get(1));
                        PlayerState playerState = Serdes.GET_PLAYER_STATE.deserialize(messagge.get(2));
                        player.updateState(game, playerState);
                        break;


                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.SORTED_TICKET_SERDE.deserialize(messagge.get(1)));
                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        write(w, Serdes.SORTED_TICKET_SERDE.serialize(player.chooseInitialTickets()));
                        break;
                    case NEXT_TURN:
                        write(w, Serdes.TURN_KIND_SERDE.serialize(player.nextTurn()));
                        break;
                    case CHOOSE_TICKETS:
                        write(w, Serdes.SORTED_TICKET_SERDE.serialize(player.chooseTickets(Serdes.SORTED_TICKET_SERDE.deserialize(messagge.get(1)))));
                        break;
                    case DRAW_SLOT:
                        write(w, Serdes.INTEGER_SERDE.serialize(player.drawSlot()));
                        break;
                    case ROUTE:
                        write(w, Serdes.ROUTE_SERDE.serialize(player.claimedRoute()));
                        break;
                    case CARDS:
                        write(w, Serdes.SORTED_CARD_SERDE.serialize(player.initialClaimCards()));
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        write(w, Serdes.SORTED_CARD_SERDE.serialize(player.chooseAdditionalCards(Serdes.LIST_SORTEDCARD_SERDE.deserialize(messagge.get(1)))));
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }


        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }


    }


    private void write(BufferedWriter w, String str) throws IOException {
        w.write(str);
        w.write('\n');
        w.flush();

    }
}
