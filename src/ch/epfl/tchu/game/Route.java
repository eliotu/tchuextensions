package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.epfl.tchu.game.Constants.MAX_ROUTE_LENGTH;
import static ch.epfl.tchu.game.Constants.MIN_ROUTE_LENGTH;

/**
 * Classe Route.
 *
 * @author Elias Naha (326050)
 * @author Eliot Ullmo (312917)
 */


public final class Route {
    public enum Level {
        OVERGROUND,
        UNDERGROUND;
    }

    private String id;
    private Station station1;
    private Station station2;
    private int length;
    private Level level;
    private Color color;

    /**
     * Construit une route avec ses
     * différents paramètres.
     *
     * @param id
     *            identité de la route
     * @param station1
     *            la première station
     * @param station2
     *            la dernière station
     * @param length
     *            longueur de la route
     * @param color
     *            couleur de la route
     * @throws IllegalArgumentException
     *            si les deux sont égales
     *            si la longueur de la route n'est pas dans
     *            les limites acceptables
     * @throws NullPointerException
     *            si l'une des deux gares ou le niveau est nul
     */


    public Route(String id, Station station1, Station station2, int length, Level level, Color color){

        Preconditions.checkArgument(!(station1.equals(station2)));
        Preconditions.checkArgument(length <= MAX_ROUTE_LENGTH);
        Preconditions.checkArgument(length >= MIN_ROUTE_LENGTH);


        if ( id==null || station1==null || station2==null || level==null){
            throw new NullPointerException();
        }

        this.color = color;
        this.id = id;
        this.length = length;
        this.level = level;
        this.station1 = station1;
        this.station2 = station2;
    }

    /**
     * Retourne l'identité de la route.
     *
     * @return l'identité de la route
     *
     */


    public String id() {
        return id;
    }

    /**
     * Retourne la première gare de la route
     *
     * @return la première gare de la route.
     *
     */

    public Station station1() {
        return station1;
    }

    /**
     * Retourne la seconde gare de la route.
     *
     * @return la seconde gare de la route.
     *
     */

    public Station station2() {
        return station2;
    }

    /**
     * Retourne la longueur de la route
     *
     * @return la longueur de la route
     *
     */

    public int length() {
        return length;
    }

    /**
     * Retourne le niveau du chemin
     *
     * @return le niveau du chemin
     *
     */

    public Level level() {
        return level;
    }

    /**
     * Retourne la couleur de la route.
     *
     * @return la couleur de la route
     *
     */

    public Color color() {
        return color;
    }

    /**
     * Retourne la liste des deux gares de la route
     * dans l'ordre auquel elles ont été passées au
     * constructeur.
     *
     * @return la liste des deux gares de la route
     *
     */

    public List<Station> stations(){

        ArrayList<Station> stations = new ArrayList<Station>();

        stations.add(station1);

        stations.add(station2);

        List<Station> stationsdef=stations;

        return stationsdef;

    }

    /**
     * Retourne la gare de la route qui n'est pas donnée.
     *
     * @param station
     *           station dont on choisira la gare opposée
     * @throws IllegalArgumentException
     *           si la gare donnée n'est ni la première
     *           ni la seconde gare de la route
     * @return  la station opposée à celle entrée en paramètre

     *
     */

    public Station stationOpposite(Station station){

        Preconditions.checkArgument(station == station1 || station == station2);

        return station == station1 ? station2 : station1;

    }

    /**
     * Retourne la liste de tous les ensembles de cartes
     * qui pourraient être joués pour s'emparer de la route.
     * ( trié par ordre croissant de nombre de cartes locomotive, puis par couleur)
     *
     * @return tous les ensembles de cartes qui pourraient être joués
     *
     */

    public List<SortedBag<Card>> possibleClaimCards(){

        List<SortedBag<Card>> cartes = new ArrayList<SortedBag<Card>>();

        int locomotive = this.level == Level.UNDERGROUND ? this.length : 0;
        List<Card> valide = new ArrayList<>();
        valide = this.color == null ? List.copyOf(Card.CARS) : List.of(Card.of(this.color));
        for (int i = 0; i <= locomotive; i++) {
            if (i != this.length) {
                for (int j = 0; j < valide.size(); ++j) {
                    cartes.add(SortedBag.of(this.length - i, valide.get(j), i, Card.LOCOMOTIVE));
                }
            } else {
                cartes.add(SortedBag.of(i, Card.LOCOMOTIVE));

            }
        }

        cartes.add(SortedBag.of(3,Card.VOLEUR));

        return cartes;


    }

    /**
     * Retourne le nombre de cartes additionnelles
     * à jouer pour s'emparer de la route.
     *
     * @param claimCards
     *           cartes posées par le joueur pour s'emparer de la route
     * @param drawnCards
     *           les trois cartes tirées du sommet de la pioche
     * @throws IllegalArgumentException
     *           si la route à laquelle on l'applique n'est pas un tunnel
     *           si drawnCards ne contient exactement 3 cartes.
     * @return le nombre de cartes additionnelles à jouer
     *         pour s'emparer de la route.
     *
     */


    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(this.level==Level.UNDERGROUND);
        Preconditions.checkArgument(drawnCards.size()==Constants.ADDITIONAL_TUNNEL_CARDS);
        if(claimCards.equals(SortedBag.of(3,Card.VOLEUR))){
            return 0;
        }
        claimCards.stream().filter(claim-> claim.color()!=null).collect(Collectors.toList());

        List<Card> additional=drawnCards.stream()
                .filter(claim-> claim==Card.LOCOMOTIVE || claimCards.contains(claim))
                .collect(Collectors.toList());


        return additional.size();
    }

    /**
     * Retourne le nombre de points de construction
     * qu'un joueur obtient lorsqu'il s'empare de la route.
     *
     * @return de points de construction qu'un joueur
     * obtient lorsqu'il s'empare de la route.
     *
     */

    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(this.length);
    }


    @Override
    public String toString(){
        return(this.station1+"-"+this.station2+"("+this.level+"  "+this.length+")");
    }

    /**
     * methode utilise uniquement pour savoir si une route double a deja ete prise
     * @param route
     * @return si
     */
    public Boolean routeDouble(Route route){
        return route.stations().contains(station1) && route.stations().contains(station2);

    }
}
