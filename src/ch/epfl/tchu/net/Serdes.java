package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Classe Serdes, contenant tous la totalité des serdes utiles au projet.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public class Serdes {


    /**
     * serde d'un integer
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(integer -> Integer.toString(integer), Integer::parseInt);

    /**
     * serde d'un string
     */
    public static final Serde<String> STRING_SERDE = Serde.of(string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8)),
                                                    string -> new String(Base64.getDecoder().decode(string)));

    /**
     * serde d'une instance de PlayerID
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * serde d'une instance de TurnKind
     */
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * serde d'une instance de Card
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * serde d'une intansce de Route
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * serde d'une intansce de ticket
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * serde d'une liste de string
     */
    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");

    /**
     * serde d'une liste de card
     */
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, ",");

    /**
     * serde d'une liste de route
     */
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");

    /**
     * serde d'un multiensemble de cartes
     */
    public static final Serde<SortedBag<Card>> SORTED_CARD_SERDE = Serde.bagOf(CARD_SERDE, ",");

    /**
     * serde d'une liste de de multiensemble de tickets
     */
    public static final Serde<SortedBag<Ticket>> SORTED_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ",");

    /**
     * serde d'une liste de de multiensemble de cartes
     */
    public static final Serde<List<SortedBag<Card>>> LIST_SORTEDCARD_SERDE = Serde.listOf(SORTED_CARD_SERDE, ";");


    /**
     * serde d'une instance de Cardstate
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(publicCardState -> {
        ArrayList<String> phrases = new ArrayList<>();
        phrases.add(LIST_CARD_SERDE.serialize(publicCardState.faceUpCards()));
        phrases.add(INTEGER_SERDE.serialize(publicCardState.deckSize()));
        phrases.add(INTEGER_SERDE.serialize(publicCardState.discardsSize()));
        return String.join(";", phrases);
    }, string -> {
        String[] strings = string.split(Pattern.quote(";"), -1);
        return new PublicCardState(LIST_CARD_SERDE.deserialize(strings[0]), INTEGER_SERDE.deserialize(strings[1]), INTEGER_SERDE.deserialize(strings[2]));
    });

    /**
     * serde d'une instance de publicplayerstate
     */
    public static final Serde<PublicPlayerState> GET_PUBLIC_PLAYER_SERDE = Serde.of(t -> {
                ArrayList<String> phrases = new ArrayList<>();
                phrases.add(INTEGER_SERDE.serialize(t.ticketCount()));
                phrases.add(INTEGER_SERDE.serialize(t.cardCount()));
                phrases.add(LIST_ROUTE_SERDE.serialize(t.routes()));
                return String.join(";", phrases);
            },str -> {
                String[] serializedElements = str.split(Pattern.quote(";"), -1);
                return new PublicPlayerState(
                        INTEGER_SERDE.deserialize(
                                serializedElements[0]
                        ),
                        INTEGER_SERDE.deserialize(
                                serializedElements[1]
                        ),
                        LIST_ROUTE_SERDE.deserialize(
                                serializedElements[2]
                         ));
            }

    );

    /**
     * serde d'une instance de playerstate
     */
    public static final Serde<PlayerState> GET_PLAYER_STATE = Serde.of(playerState -> {
        ArrayList<String> phrases = new ArrayList<>();
        phrases.add(SORTED_TICKET_SERDE.serialize(playerState.tickets()));
        phrases.add(SORTED_CARD_SERDE.serialize(playerState.cards()));
        phrases.add(LIST_ROUTE_SERDE.serialize(playerState.routes()));
        return String.join(";", phrases);
    }, string -> {
        String[] des = string.split(Pattern.quote(";"), -1);
        return new PlayerState(SORTED_TICKET_SERDE.deserialize(des[0]), SORTED_CARD_SERDE.deserialize(des[1]), LIST_ROUTE_SERDE.deserialize(des[2]));
    });

    /**
     * serde d'une instance de publicgamestate
     */
    public static final  Serde<PublicGameState> GET_PUBLIC_GAME_STATE = Serde.of(publicGameState -> {
        ArrayList<String> phrases = new ArrayList<>();
        phrases.add(INTEGER_SERDE.serialize(publicGameState.ticketsCount()));
        phrases.add(PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState()));
        phrases.add(PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()));
        phrases.add(GET_PUBLIC_PLAYER_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)));
        phrases.add(GET_PUBLIC_PLAYER_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)));
        phrases.add(publicGameState.lastPlayer() == null ? "" : PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer()));

        return String.join(":", phrases);
    },str -> {
        String[] serializedElements =str.split(Pattern.quote(":"), -1);
        return new PublicGameState(
                INTEGER_SERDE.deserialize(
                        serializedElements[0]
                ),
                PUBLIC_CARD_STATE_SERDE.deserialize(
                        serializedElements[1]
                ),
                PLAYER_ID_SERDE.deserialize(
                        serializedElements[2]
                ),
                Map.of(
                        PlayerId.PLAYER_1,
                        GET_PUBLIC_PLAYER_SERDE.deserialize(
                                serializedElements[3]
                        ),
                        PlayerId.PLAYER_2,
                        GET_PUBLIC_PLAYER_SERDE.deserialize(
                                serializedElements[4]
                        )
                ),
                serializedElements[5].equals("") ? null :
                        PLAYER_ID_SERDE.deserialize(serializedElements[5])
        );

    });
}
