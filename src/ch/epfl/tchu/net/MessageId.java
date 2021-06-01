package ch.epfl.tchu.net;

/**
 * Classe énumerée MessageId.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

/** Cette classe énumère les types de messages que le serveur peut envoyer aux clients.**/

public enum MessageId {

    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS;

}