package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * Classe Game.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */


public final class Game {

    /**
     * INITIALISE LA GAME
     *
     * @param players HashmAp permettant relier le player Id au PlayerState respectif
     * @param playerNames  HashmAp permettant relier le player Id ä leurs noms
     * @param tickets tickets de la game
     * @param rng Random
     *
     * @throws IllegalArgumentException si players et playerNames n'ont pas un size de 2
     *
     */

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {

        //PRECONDITIONS
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);
        Preconditions.checkArgument(players.size() == PlayerId.COUNT);


        //INITIALISER LES JOUEURS
        players.forEach((PlayerId, Player) -> Player.initPlayers(PlayerId, playerNames));//methode InitPlayers
        Info player1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        Info player2 = new Info(playerNames.get(PlayerId.PLAYER_2));
        Map<PlayerId, Info> inf = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);

        //INITIALISER LA GAME
        GameState currentGame = GameState.initial(tickets, rng);

        receive(players, inf.get(currentGame.currentPlayerId()).willPlayFirst()); //1

        //DISTRIBUER LES TICKETS
        SortedBag<Ticket> init = currentGame.topTickets(Constants.INITIAL_TICKETS_COUNT);
        currentGame = currentGame.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        players.get(currentGame.currentPlayerId()).setInitialTicketChoice(init);

        init = currentGame.topTickets(Constants.INITIAL_TICKETS_COUNT);
        currentGame = currentGame.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        players.get(currentGame.currentPlayerId().next()).setInitialTicketChoice(init);

        update(players, currentGame);


        ArrayList<Integer> sizes=new ArrayList<>();

        //ELECTIONS DES TICKETS
        SortedBag<Ticket> choix = players.get(currentGame.currentPlayerId()).chooseInitialTickets();
        currentGame = currentGame.withInitiallyChosenTickets(currentGame.currentPlayerId(), choix);

        SortedBag<Ticket> choix1 = players.get(currentGame.currentPlayerId().next()).chooseInitialTickets();
        currentGame = currentGame.withInitiallyChosenTickets(currentGame.currentPlayerId().next(), choix1);

        receive(players,inf.get(currentGame.currentPlayerId()).keptTickets(choix.size()));
        receive(players,inf.get(currentGame.currentPlayerId().next()).keptTickets(choix1.size()));

        while (true) {


            Info joueur = new Info(playerNames.get(currentGame.currentPlayerId()));

            update(players, currentGame);
            receive(players, joueur.canPlay());


            Player.TurnKind etape = players.get(currentGame.currentPlayerId()).nextTurn();


            switch (etape) {

                case DRAW_TICKETS:
                    receive(players, joueur.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> drawnTickets = currentGame.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    SortedBag<Ticket> elections = players.get(currentGame.currentPlayerId()).chooseTickets(currentGame.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    receive(players, joueur.keptTickets(elections.size()));
                    currentGame = currentGame.withChosenAdditionalTickets(drawnTickets, elections);
                    break;


                case DRAW_CARDS:
                    for (int i = 0; i < 2; ++i) {
                        currentGame = currentGame.withCardsDeckRecreatedIfNeeded(rng);
                        int premiere = players.get(currentGame.currentPlayerId()).drawSlot();
                        if (premiere < Constants.FACE_UP_CARDS_COUNT && premiere >= 0) {
                            receive(players, joueur.drewVisibleCard(currentGame.cardState().faceUpCard(premiere)));

                            currentGame = currentGame.withDrawnFaceUpCard(premiere);

                        } else {
                            currentGame = currentGame.withBlindlyDrawnCard();
                            receive(players, joueur.drewBlindCard());
                        }
                        update(players, currentGame);
                    }
                    break;


                case CLAIM_ROUTE:
                    Route claim = players.get(currentGame.currentPlayerId()).claimedRoute();

                    SortedBag<Card> claims = players.get(currentGame.currentPlayerId()).initialClaimCards();

                    if (claim.level() == Route.Level.OVERGROUND) {
                        receive(players, joueur.claimedRoute(claim, claims));
                        currentGame = currentGame.withClaimedRoute(claim, claims);
                    } else {

                        receive(players, joueur.attemptsTunnelClaim(claim, claims));


                        ArrayList<Card> drawn = new ArrayList<>();

                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                            currentGame = currentGame.withCardsDeckRecreatedIfNeeded(rng);

                            Card card = currentGame.topCard();
                            currentGame = currentGame.withoutTopCard();
                            drawn.add(card);
                        }

                        SortedBag<Card> additional = SortedBag.of(drawn);
                        int additionalCount = claim.additionalClaimCardsCount(claims, additional);
                        receive(players, joueur.drewAdditionalCards(additional, additionalCount));

                        if (additionalCount > 0) {
                            List<SortedBag<Card>> options = currentGame.currentPlayerState().possibleAdditionalCards(additionalCount, claims);
                            if (!options.isEmpty()) {
                                SortedBag<Card> chooseAdditional = players.get(currentGame.currentPlayerId()).chooseAdditionalCards(options);

                                if (chooseAdditional.isEmpty()) {
                                    receive(players, joueur.didNotClaimRoute(claim));
                                } else {
                                    receive(players, joueur.claimedRoute(claim, claims));
                                    currentGame = currentGame.withClaimedRoute(claim, claims.union(chooseAdditional));
                                }
                            } else {
                                receive(players, joueur.didNotClaimRoute(claim));
                            }


                        } else {

                            receive(players, joueur.claimedRoute(claim, claims));
                            currentGame = currentGame.withClaimedRoute(claim, claims);

                        }

                        currentGame = currentGame.withMoreDiscardedCards(additional);

                    }

                    break;

            }

            if (currentGame.currentPlayerId() == currentGame.lastPlayer()) {
                break;
            }
            if (currentGame.lastTurnBegins()) {
                receive(players, joueur.lastTurnBegins(currentGame.playerState(currentGame.currentPlayerId()).carCount()));
            }
            currentGame = currentGame.forNextTurn();
        }

        //COMPTER LES POINTS
        int points1 = currentGame.playerState(PlayerId.PLAYER_1).finalPoints();
        int points2 = currentGame.playerState(PlayerId.PLAYER_2).finalPoints();
        Trail trail1 = Trail.longest(currentGame.playerState(PlayerId.PLAYER_1).routes());
        Trail trail2 = Trail.longest(currentGame.playerState(PlayerId.PLAYER_2).routes());


        if (trail1.length() > trail2.length()) {
            receive(players, player1.getsLongestTrailBonus(trail1));
            points1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        } else if (trail2.length() > trail1.length()) {
            receive(players, player2.getsLongestTrailBonus(trail2));

            points2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        } else if (trail1.length() == trail2.length()) {
            receive(players, player1.getsLongestTrailBonus(trail1));
            receive(players, player2.getsLongestTrailBonus(trail2));


            points2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            points1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        }

        update(players, currentGame);
        if(points1>points2){
            receive(players, player1.won(points1, points2));
        }

        else if (points2 > points1) {
            receive(players, player2.won(points2, points1));
        } else {
            ArrayList<String> noms = new ArrayList<>();
            playerNames.forEach((x, nom) -> noms.add(nom));

            receive(players, Info.draw(noms, points1));
        }
    }

    private static void receive(Map<PlayerId, Player> players, String Info) {
        players.forEach((Id, player) -> player.receiveInfo(Info));
    }

    private static void update(Map<PlayerId, Player> players, GameState nouv) {
        players.forEach((PlayerId, Player) -> Player.updateState(nouv, nouv.playerState(PlayerId)));
    }

}



