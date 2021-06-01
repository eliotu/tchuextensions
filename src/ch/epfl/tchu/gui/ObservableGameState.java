package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe ObservableGameState.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public class ObservableGameState {

    private final PlayerId id;

    //groupe1
    private final IntegerProperty pourcentageBillets;
    private final IntegerProperty pourcentageCartes;



    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routes;

    private final Map<Ticket,BooleanProperty> ticketObjectPropertyMap;

    //groupe2
    private final  IntegerProperty nbTicketPoints;

    private final Map<PlayerId, IntegerProperty> nbTickets;
    private final Map<PlayerId, IntegerProperty> nbCartes;
    private final Map<PlayerId, IntegerProperty> nbWagons;
    private final Map<PlayerId, IntegerProperty> nbPoints;


    //groupe3
    private final ObservableList<Ticket> listeBillets;


    private final Map<Card, IntegerProperty> cartesEnMain;
    private final Map<Route, BooleanProperty> routesPossibles;

    private PublicGameState gameState;
    private PlayerState playerState;

    private final StringProperty stringProperty;

    private final ListView<Ticket> ticketListView;


    private final ObjectProperty<Trail> trail;




    /**
     * Constructeur crééant l'état du jeu à son aspect initial avec la totalité des propriétés
     * de l'état à leur valeur par défaut.
     * @param id
     *          identité du joueur auquel elle correspond
     */

    public ObservableGameState(PlayerId id) {
        this.id = id;
        this.gameState = null;
        this.playerState = null;
        this.pourcentageBillets = new SimpleIntegerProperty();

        this.pourcentageCartes = new SimpleIntegerProperty();

        this.faceUpCards = new ArrayList<>();
        Constants.FACE_UP_CARD_SLOTS.forEach(integer -> this.faceUpCards.add(new SimpleObjectProperty<>()));

        this.routes = new HashMap<>();
        ChMap.routes().forEach(route -> this.routes.put(route, new SimpleObjectProperty<>()));


        this.listeBillets = FXCollections.observableArrayList();


        this.routesPossibles = new HashMap<>();
        ChMap.routes().forEach(r -> this.routesPossibles.put(r, new SimpleBooleanProperty()));


        this.cartesEnMain = new HashMap<>();
        Card.ALL.forEach(carte -> cartesEnMain.put(carte, new SimpleIntegerProperty()));


        this.nbTickets = new HashMap<>();
        this.nbPoints = new HashMap<>();
        this.nbWagons = new HashMap<>();
        this.nbCartes = new HashMap<>();
        this.nbTicketPoints=new SimpleIntegerProperty();
        PlayerId.ALL.forEach(playerId -> {
            nbCartes.put(playerId, new SimpleIntegerProperty());
            nbWagons.put(playerId, new SimpleIntegerProperty());
            nbTickets.put(playerId, new SimpleIntegerProperty());
            nbPoints.put(playerId, new SimpleIntegerProperty());

        });
        this.ticketObjectPropertyMap = new HashMap<>();
        ChMap.tickets().forEach(ticket -> this.ticketObjectPropertyMap.put(ticket,new SimpleBooleanProperty()));


        /*TODO effacer avent le rendu*/
        //etape12
        this.trail=new SimpleObjectProperty<>();
        this.stringProperty=new SimpleStringProperty();

        this.ticketListView=new ListView<>(listeBillets);

    }


    public void setTrail(){
        this.trail.set(Trail.longest(playerState.routes()));
    }
    public ReadOnlyObjectProperty<Trail> trailReadOnlyObjectProperty(){
        return this.trail;
    }

    /**
     * Cette méthode met à jour la totalité des propriétés décrites ci-dessous en fonction de ces deux états.
     * @param newgamestate la partie publique du jeu
     * @param playerState l'état complet du joueur
     */

    public void setState(PublicGameState newgamestate,PlayerState playerState){
        gameState=newgamestate;
        this.playerState=playerState;

        //groupe 1
        setPourcentageBillets();
        setPourcentageCartes();
        createFaceUpCards();
        setTicketObjectPropertyMap();


        for (Route route : ChMap.routes()) {

            if (gameState.claimedRoutes().contains(route)) {
                if (playerState.routes().contains(route)) {
                    this.routes.get(route).set(id);
                } else {
                    this.routes.get(route).set(id.next());
                }
            }
            if(playerState.hasVoleur()){
                routesPossibles.get(route).set(true);
            }
            else {
                routesPossibles.get(route).set(gameState.currentPlayerId() == id &&
                        !gameState.claimedRoutes().contains(route) &&
                        playerState.canClaimRoute(route) &&
                        gameState.claimedRoutes().stream().noneMatch(r -> r.routeDouble(route)));
            }
        }


        for(PlayerId id:PlayerId.ALL){
            this.nbTickets.get(id).set(gameState.playerState(id).ticketCount());
            nbCartes.get(id).set(gameState.playerState(id).cardCount());
            nbWagons.get(id).set(gameState.playerState(id).carCount());
            nbPoints.get(id).set(gameState.playerState(id).claimPoints());

        }

        //groupe 3
        setListeBillets();
        setCartesEnMain();

        /*TODO Effacer avanr le rendu*/
        setStringlong();
        setNbTicketPoints();

    }
    private boolean canclaim(Route r){
        if(gameState.claimedRoutes().contains(r) && playerState.hasVoleur()){
            System.out.println(true);
            return true;
        }
        return !gameState.claimedRoutes().contains(r);
    }

    /**
     * Fonction calculant le pourcentage des billets restant dans la pioche de billets.
     */

    private void setPourcentageBillets() {
        this.pourcentageBillets.set(this.gameState.ticketsCount() * 100 / ChMap.tickets().size());
    }

    /**
     * Fonction permettant d'accéder au pourcentage des billets restant dans la pioche.
     */

    public ReadOnlyIntegerProperty getPourcentageBillets() {
        return this.pourcentageBillets;
    }

    /**
     * Fonction calculant le pourcentage des cartes restant dans la pioche.
     */

    private void setPourcentageCartes(){ this.pourcentageCartes.set(this.gameState.cardState().deckSize()*100/Constants.TOTAL_CARDS_COUNT); }

    /**
     * Fonction permettant d'accéder au pourcentage des cartes restant dans la pioche.
     */

    public ReadOnlyIntegerProperty getPourcentageCartes(){ return pourcentageCartes; }

    /**
     * Fonction permettant de créer les FaceUpCards avec leur emplacement correspondant.
     */

    private void createFaceUpCards(){
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = this.gameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
    }

    /**
     * Fonction permettant d'accéder à une carte précise des FaceUpCards.
     * @param slot
     *           emplacement de la carte choisie
     * @return carte choisie
     */

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }



    /**
     * Fonction retournant l'idendité du joueur qui possède la route.
     * @param route route dont on veut savoir l'idendite
     * @return identité du joueur possédant la route
     */

    public ReadOnlyObjectProperty<PlayerId> getRouteId(Route route){
        return this.routes.get(route);
    }



    /**
     * Fonction permettant d'accéder au nombre de tickets du joueur choisi.
     */

    public ReadOnlyIntegerProperty getTickets(PlayerId id) {
        return nbTickets.get(id);
    }




    /**
     * Fonction permettant d'accéder au nombre de cartes du joueur choisi.
     */

    public ReadOnlyIntegerProperty getCards(PlayerId id) { return nbCartes.get(id); }



    /**
     * Fonction permettant d'accéder au nombre de wagons du joueur choisi.
     */

    public ReadOnlyIntegerProperty getWagon(PlayerId id) {
        return nbWagons.get(id);
    }



    /**
     * Fonction permettant d'accéder au nombre de points du joueur choisi.
     */

    public ReadOnlyIntegerProperty getPoints(PlayerId id) {
        return nbPoints.get(id);
    }

    /**
     * Fonction permettant d'associer à chaque joueur sa liste de billets.
     */

    private void setListeBillets() {
        this.listeBillets.setAll(playerState.tickets().toList());
    }

    /**
     * Fonction permettant d'accéder à la liste de billets du joueur choisi.
     */

    public ObservableList<Ticket> getListBillets() {
        return FXCollections.unmodifiableObservableList(listeBillets);
    }




    /**
     * Fonction prenant en argument une route et renvoie un boolean déterminant
     * s'il est possible de prendre cette route pour le joueur.
     * @param route
     *            route choisie
     * @return une proprite de boolean si la route peut etre prise
     */

    public BooleanProperty getRoutepossible(Route route){
        return routesPossibles.get(route);
    }

    /**
     * Fonction permettant de créer les cartes en main de chaque joueur.
     */

    private void setCartesEnMain(){
        Card.ALL.forEach(carte->{
            int compteur=playerState.cards().countOf(carte);
            cartesEnMain.get(carte).set(compteur);
        });
    }

    /**
     * Fonction retournant true si il est possible de tirer une carte.
     * @return true si il est possible de tirer une carte.
     */

    public boolean canDrawCards(){ return gameState.canDrawCards(); }

    /**
     * Fonction retournant true si il est possible de tirer un billet.
     * @return true si il est possible de tirer une carte.
     */

    public boolean canDrawTickets(){ return gameState.canDrawTickets(); }

    /**
     * Fonction retournant la multiplicité de chaque carte de la main du joueur.
     * @param carte
     *            carte de la main
     * @return multiplicité de la carte
     */

    public ReadOnlyIntegerProperty getCartesEnMain(Card carte) {
        return cartesEnMain.get(carte);
    }

    /**
     * Retourne la liste d'ensemble qu'un joueur peut utiliser pour prendre une route.
     * @param route
     *            route choisie
     * @return la liste d'ensemble qu'un joueur peut utiliser pour prendre une route
     */

    public List<SortedBag<Card>> getPossibleClaimCars(Route route){
        return playerState.possibleClaimCards(route);
    }


    public ListView<Ticket> getTicketListView() {
        return ticketListView;
    }







 /**ETAPE12**/

    private void setStringlong(){
        if(Trail.longest(playerState.routes()).length()>Trail.longest(gameState.playerState(id.next()).routes()).length()){
            this.stringProperty.set(String.format(StringsFr.LONGEST,id.name(),Trail.longest(playerState.routes())));
        }
        else if(Trail.longest(playerState.routes()).length()<Trail.longest(gameState.playerState(id.next()).routes()).length()){
            this.stringProperty.set( String.format(StringsFr.LONGEST,id.next().name(),Trail.longest(gameState.playerState(id.next()).routes())));
        }
        else if(Trail.longest(playerState.routes()).length()==Trail.longest(gameState.playerState(id.next()).routes()).length()) {
            this.stringProperty.set( "Les deux joueurs sont aexequo");
        }
    }

    public Trail getCheminLong(){
        if(Trail.longest(playerState.routes()).length()>Trail.longest(gameState.playerState(id.next()).routes()).length()){
            return Trail.longest(playerState.routes());
        }
        else if(Trail.longest(playerState.routes()).length()<Trail.longest(gameState.playerState(id.next()).routes()).length()){
                return Trail.longest(gameState.playerState(id.next()).routes());        }
        else if(Trail.longest(playerState.routes()).length()==Trail.longest(gameState.playerState(id.next()).routes()).length()) {
            return Trail.longest(playerState.routes());

        }
        return null;
    }
    public ReadOnlyStringProperty getLongest(){
        return this.stringProperty;
    }


    private void setNbTicketPoints(){
            nbTicketPoints.set(playerState.ticketPoints());

    }
    public ReadOnlyIntegerProperty getNbTicketPoints(){
        return nbTicketPoints;
    }


    public BooleanProperty getTicketComplete(Ticket ticket){
        return this.ticketObjectPropertyMap.get(ticket);
    }
    private void setTicketObjectPropertyMap(){

        int count=this.playerState.routes().stream().mapToInt(r->Math.max(r.station1().id(),r.station2().id()))
                .max().orElse(0);
        count+=1;
        StationPartition.Builder a=new StationPartition.Builder(count);
        this.playerState.routes().forEach(route-> a.connect(route.station1(), route.station2()));
        StationPartition b=a.build();
        ChMap.tickets().forEach(ticket -> this.ticketObjectPropertyMap.get(ticket).set(ticket.isConnected(b)));

    }
}
