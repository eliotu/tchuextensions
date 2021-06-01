package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Classe RemotePlayerProxy.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */


public final class RemotePlayerProxy implements Player {

    private final BufferedWriter w;
    private final BufferedReader r;

    /**
     * Son constructeur prend en argument la «prise» (socket), de type Socket,
     * que le mandataire utilise pour communiquer à travers le réseau
     * avec le client par échange de messages textuels.
     * On initialise aussi le BufferedWriter et le BufferedReader
     *
     * @param sock socket
     */
    public RemotePlayerProxy(Socket sock) throws IOException {

        w = new BufferedWriter(
                new OutputStreamWriter(sock.getOutputStream(),
                        US_ASCII));
        r = new BufferedReader(
                new InputStreamReader(sock.getInputStream(),
                        US_ASCII));

    }

    /**
     * On serialise le message id INIT_PLAYERS
     * On serialise les arguments qui sont ensuite envoyes
     *
     * @param ownId       id du jouer
     * @param playerNames tables des noms associe aux id
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        List<String> names = new ArrayList<>();


        for (PlayerId id : PlayerId.ALL) {
            names.add(playerNames.get(id));
        }


        send(MessageId.INIT_PLAYERS, String.join(" ", Serdes.PLAYER_ID_SERDE.serialize(ownId), Serdes.LIST_STRING_SERDE.serialize(names)));

    }

    /**
     * On serialise le message id RECEIVE_INFO
     * On serialise l' argument qui est ensuite envoye
     *
     * @param info string dinformation
     */
    @Override
    public void receiveInfo(String info) {
        send(MessageId.RECEIVE_INFO, Serdes.STRING_SERDE.serialize(info));


    }

    /**
     * * On serialise le message id UPDATE_STATE
     * On serialise les arguments qui sont ensuite envoyes
     *
     * @param newState publicgamestate
     * @param ownState playerstate
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {


        send(MessageId.UPDATE_STATE, String.join(" ", Serdes.GET_PUBLIC_GAME_STATE.serialize(newState), Serdes.GET_PLAYER_STATE.serialize(ownState)));
    }

    /**
     * On serialise le message id SET_INITIAL_TICKETS
     * On serialise l' arguments qui est ensuite envoye
     *
     * @param tickets multiensemble de tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        send(MessageId.SET_INITIAL_TICKETS, Serdes.SORTED_TICKET_SERDE.serialize(tickets)); //setinitiALTICKETS TICKETS(SRIALISSE)
    }

    /**
     * On serialise le message id CHOOSE_INITIAL_TICKETS
     *
     * @return le multiensemble deserialise recu du client
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        send(MessageId.CHOOSE_INITIAL_TICKETS);
        return Serdes.SORTED_TICKET_SERDE.deserialize(receive());
    }

    /**
     * On serialise le message id NEXT_TURN
     *
     * @return le turnkind deserialise recu du client
     */
    @Override
    public TurnKind nextTurn() {
        send(MessageId.NEXT_TURN);

        return Serdes.TURN_KIND_SERDE.deserialize(receive());
    }

    /**
     * On serialise le message id CHOOSE_TICKETS
     *
     * @return le multiensemble deserialise recu du client
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        send(MessageId.CHOOSE_TICKETS, Serdes.SORTED_TICKET_SERDE.serialize(options));
        return Serdes.SORTED_TICKET_SERDE.deserialize(receive());
    }

    /**
     * On serialise le message id DRAW_SLOT
     *
     * @return le integer deserialise recu du client
     */
    @Override
    public int drawSlot() {
        send(MessageId.DRAW_SLOT);

        return Serdes.INTEGER_SERDE.deserialize(receive());
    }

    /**
     * On serialise le message id ROUTE
     *
     * @return la Route deserialise recu du client
     */
    @Override
    public Route claimedRoute() {
        send(MessageId.ROUTE);
        return Serdes.ROUTE_SERDE.deserialize(receive());
    }

    /**
     * On serialise le message id CARDS
     *
     * @return le multiensemble deserialise recu du client
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        send(MessageId.CARDS, Serdes.STRING_SERDE.serialize(""));

        return Serdes.SORTED_CARD_SERDE.deserialize(receive());

    }

    /**
     * On serialise le message id CHOOSE_ADDITIONAL_CARDS
     *
     * @return le multiensemble deserialise recu du client
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        send(MessageId.CHOOSE_ADDITIONAL_CARDS, Serdes.LIST_SORTEDCARD_SERDE.serialize(options));
        return Serdes.SORTED_CARD_SERDE.deserialize(receive());


    }


    /**
     * Methode prive pour envoyer des messages au client
     *
     * @param id            instance de messageId
     * @param serialisation elements serialise
     */
    private void send(MessageId id, String... serialisation) {
        try {
            List<String> messages = new ArrayList<>();
            messages.add(id.name());
            messages.addAll(Arrays.asList(serialisation));


            w.write(String.join(" ", messages));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);

        }
    }

    /**
     * @return un message envoye par le client
     */
    private String receive() {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}