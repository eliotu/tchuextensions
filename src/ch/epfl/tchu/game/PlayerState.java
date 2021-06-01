package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe PlayerState.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    /**
     * Construit l'état d'un joueur possédant
     * les billets, cartes et routes donnés.
     *
     * @param tickets billets du joueur
     * @param cards   cartes du joueur
     */

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.cards = cards;
        this.tickets = tickets;
    }

    /**
     * Retourne l'état initial d'un joueur auquel les
     * cartes initiales données ont été distribuées
     *
     * @param initialCards état initial, le joueur ne possède encore aucun billet,
     *                     et ne s'est emparé d'aucune route
     * @throws IllegalArgumentException si le nombre de cartes initial ne vaut pas 4.
     */


    public static PlayerState initial(SortedBag<Card> initialCards) {

        Preconditions.checkArgument(initialCards.size() == 4);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }


    /**
     * Retourne les billets du joueur
     *
     * @return les billets du joueur.
     */

    public SortedBag<Ticket> tickets() {
        return tickets;
    }


    /**
     * Retourne retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés.
     *
     * @param newTickets billets donnés
     * @return retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés.
     */

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        SortedBag<Ticket> ticketsNew = SortedBag.of(this.tickets);
        return new PlayerState(ticketsNew.union(newTickets), this.cards, this.routes());

    }

    /**
     * Retourne les cartes wagon/locomotive du joueur
     *
     * @return les cartes wagon/locomotive du joueur
     */

    public SortedBag<Card> cards() {
        return cards;
    }


    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     *
     * @param card carte donnée
     * @return les billets du joueur.
     */


    public PlayerState withAddedCard(Card card) {
        SortedBag<Card> cardsNew = SortedBag.of(this.cards);
        return new PlayerState(this.tickets, cardsNew.union(SortedBag.of(card)), this.routes());
    }

    /**
     * Retourne vrai ssi le joueur peut s'emparer de la route donnée.
     *
     * @param route route dont on souhaite s'emparer
     * @return vrai ssi le joueur peut s'emparer de la route donnée
     * sinon retourne false
     */


    public boolean canClaimRoute(Route route) {
        List<SortedBag<Card>> possibilites = route.possibleClaimCards();

        for (SortedBag<Card> combi : possibilites) {
            if (cards.contains(combi) && carCount()>=route.length()) {
                return true;
            }
        }

        return false;
    }
    public boolean hasVoleur(){
        return cards.contains(SortedBag.of(3,Card.VOLEUR));
    }
    /**
     * Retourne la liste de tous les ensembles de cartes que le
     * joueur pourrait utiliser pour prendre possession de la route donnée.
     *
     * @param route route dont on souhaite s'emparer
     * @return Retourne la liste de tous les ensembles de cartes que le
     * joueur pourrait utiliser pour prendre possession de la route donnée
     * si c'est possible
     * @throws IllegalArgumentException si le joueur n'a pas assez de wagons pour s'emparer de la route
     */


    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount()>=route.length());
        List<SortedBag<Card>> options=new ArrayList<>();

        if (!canClaimRoute(route)) {
            return List.of();
        }
        else{
            List<SortedBag<Card>> possibilités = route.possibleClaimCards();
            for (SortedBag<Card> combi : possibilités) {
                if (cards.contains(combi)) {
                     options.add(combi);
                }
            }

        }
        return options;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser
     * pour s'emparer d'un tunnel apres avoir tire les additional Cards, trié par ordre croissant du nombre de cartes locomotives.
     *
     * @param additionalCardsCount nombre de cartes additionelles à poser
     * @param initialCards         cartes posées initialement par le joueur
     * @return la liste de tous les ensembles de cartes que le joueur
     * pourrait utiliser pour s'emparer d'un tunnel.
     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus)
     *                                  si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents
     *                                  si l'ensemble des cartes tirées ne contient pas exactement 3 cartes
     */


    public List<SortedBag<Card>> possibleAdditionalCards
    (int additionalCardsCount, SortedBag<Card> initialCards) {

        Preconditions.checkArgument(additionalCardsCount >= 1);
        Preconditions.checkArgument(additionalCardsCount <= 3);
        Preconditions.checkArgument(initialCards.size() != 0);
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);


        SortedBag<Card> cartesDef = this.cards.difference(initialCards);
        SortedBag.Builder<Card> cartesJoue = new SortedBag.Builder<>();

        for (Card init : cartesDef) {
            if (init == Card.LOCOMOTIVE || initialCards.contains(init)) {
                cartesJoue.add(init);
            }

        }
            if(additionalCardsCount>cartesJoue.size()){
                return List.of();

            }
            List<SortedBag<Card>> options = new ArrayList<>(cartesJoue.build().subsetsOfSize(additionalCardsCount));

            options.sort(
                    Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return options;


    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le
     * joueur s'est de plus emparé de la route donnée au moyen des cartes données.
     *
     * @param route      route dont le joueur souhaite s'emparer
     * @param claimCards route posées par le joueur pour s'emparer de la route
     * @return un état identique au récepteur, si ce n'est que le
     * joueur s'est de plus emparé de la route donnée
     * au moyen des cartes données.
     */


    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routes = new ArrayList<>(this.routes());
        routes.add(route);
        return new PlayerState(this.tickets, this.cards.difference(claimCards), routes);
    }

    /**
     * Retourne le nombre de points, éventuellement négatif, obtenus par le joueur grâce à ses billets.
     *
     * @return le nombre de points, éventuellement négatif, obtenus par le joueur grâce à ses billets.
     */


    public int ticketPoints() {


        int count=this.routes().stream().mapToInt(r->Math.max(r.station1().id(),r.station2().id()))
                .max().orElse(0);
        count+=1;
        StationPartition.Builder a=new StationPartition.Builder(count);
        this.routes().forEach(route-> a.connect(route.station1(), route.station2()));
        StationPartition b=a.build();
        return this.tickets.stream().mapToInt(t -> t.points(b)).reduce(0 ,(x, y)-> x+y);
    }

    /**
     * Retourne la totalité des points obtenus par le joueur à la fin de la partie
     *
     * @return la totalité des points obtenus par le joueur à la fin de la partie.
     */

    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }

    public List<Station> stations(){
        List<Station> stations=new ArrayList<>();
        for(Route route:this.routes()){
            stations.addAll(route.stations());
        }
        return stations;
    }
}
