package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;
/**
 * Class GraphicalPlayer
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */
public final class GraphicalPlayer {

    private final ObservableGameState gameState;
    private final ObservableList<Text> infos;
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRoute;
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCard;
    //TODO effacer avant le rendu
    private Clip music1;
    private final Stage primaryStage;


    /**
     * Il construit l'interface graphique, constituée d'une fenêtre.
     * Cela implique entre autres de créer une instance d'un état de jeu observable,
     * qui est d'une part stocké comme attribut de la classe et d'autre part passé en argument aux méthodes construisant les différentes vues.
     * @param id  l'identité du joueur auquel l'instance correspond,
     * @param playerNames la table associative des noms des joueurs
     */


    public GraphicalPlayer(PlayerId id, Map<PlayerId, String> playerNames) {

        assert isFxApplicationThread();

        //interface principale4
        this.primaryStage=new Stage();
        this.gameState=new ObservableGameState(id);
        this.infos= FXCollections.observableArrayList();
        this.claimRoute=new SimpleObjectProperty<>();

        this.drawTickets=new SimpleObjectProperty<>();
        this.drawCard=new SimpleObjectProperty<>();
        Node mapView = MapViewCreator
                .createMapView(gameState, claimRoute, this::chooseClaimCards);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator
                .createHandView(gameState);




        Node infoView = InfoViewCreator
                .createInfoView(id, playerNames, gameState, infos);

        VBox info=new VBox(infoView);
        Separator separator = new Separator();
        info.getChildren().addAll(separator,etape12(),ticketspoints());

        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, info);



        Scene scene=new Scene(mainPane);
        scene.getStylesheets().add("root.css");
        primaryStage.setScene(scene);


        primaryStage.setTitle(String.format("tCHu  \u2014 %s", playerNames.get(id)));



        primaryStage.show();



        //TODO effacer avant le rendu
        scene.setOnKeyPressed(event -> {

            if(event.isAltDown()){


                    Button chill=new Button();
                    chill.setText("CHILL");

                chill.setOnAction(event2->{

                    try {
                            music1 = Music("/Arcade Game Music Type Beat (RDCworld1 Outro).wav");
                        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ioException) {
                            ioException.printStackTrace();
                        }

                        music1.start();
                        music1.loop(Clip.LOOP_CONTINUOUSLY);
                        chill.getScene().getWindow().hide();


                    });
                    Button jazz=new Button();
                    jazz.setOnAction(e->{
                        try {

                            music1 = Music("/SpeakEasy - 16-bit Jazzy R&B (inspired by Yuzo Koshiro).wav");
                        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ioException) {
                            ioException.printStackTrace();
                        }

                        music1.start();
                        music1.loop(Clip.LOOP_CONTINUOUSLY);
                        chill.getScene().getWindow().hide();







                    });
                    jazz.setText("JAZZ");
                    Button rock=new Button();
                    rock.setText("ROCK");
                    rock.setOnAction(event1 -> {
                        try {

                            music1 = Music("/Metallica - Master of Puppets (16-bit Sega cover).wav");
                        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                            e.printStackTrace();
                        }

                        music1.start();
                        music1.loop(Clip.LOOP_CONTINUOUSLY);
                        chill.getScene().getWindow().hide();



                    });

                    VBox chemin=new VBox(chill,jazz,rock);


                    Stage chooseAction = new Stage(StageStyle.UTILITY);


                    Scene music = new Scene(chemin);


                    music.getStylesheets().add("chooser.css");
                    chooseAction.setScene(music);

                    chooseAction.show();


            }
            if(event.isControlDown()){
                if (music1 != null) music1.close();

            }

        });




        //interface de choix


    }


    /**
     * appele cette méthode sur l'état observable du joueur,
     * @param gameState la partie publique du jeu
     * @param playerState l'état complet du joueur
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {
        assert isFxApplicationThread();

        this.gameState.setState(gameState, playerState);

    }

    /**
     *prenant un message de type String — et l'ajoutant au bas des informations sur le déroulement de la partie,
     * qui sont présentées dans la partie inférieure de la vue des informations
     * @param info un message de type String —
     */
    public void receiveInfo(String info) {
        assert isFxApplicationThread();


        if (this.infos.size() >= 5) this.infos.remove(0);
        Text text = new Text();
        text.textProperty().set(info);
        this.infos.add(text);

    }
    /**
     * methode prive pour set les proprietes des gestionnaires d'action a null
     */
    private void setToNull(){
        drawTickets.set(null);
        drawCard.set(null);
        claimRoute.set(null);
    }

    /**
     * méthode qui, lorsque le joueur (humain) décide d'effectuer une action, le gestionnaire correspondant soit appelé.
     * @param drawCardHandler un gestionnaire d'action pour les cartes
     * @param ticketsHandler un gestionnaire d'action pour les tickets
     * @param claimRouteHandler un gestionnaire d'action pour les routes
     */
    public void startTurn(ActionHandlers.DrawCardHandler drawCardHandler, ActionHandlers.DrawTicketsHandler ticketsHandler, ActionHandlers.ClaimRouteHandler claimRouteHandler) {

        assert isFxApplicationThread();


        if (gameState.canDrawTickets()) {
            drawTickets.set(() -> {
                    ticketsHandler.onDrawTickets();
                   setToNull();
            });
        }
        if (gameState.canDrawCards()) {
            drawCard.set((slot) -> {
                drawCardHandler.onDrawCard(slot);
                setToNull();
                drawCard(drawCardHandler);

            });
        }
        claimRoute.set((t, r) -> {
            claimRouteHandler.onClaimRoute(t, r);
            setToNull();
        });


    }

    /**
     * Methode prive qui permet de modulariser le code et de faire apparaitre l'interface de choix
     * @param listView Listview
     * @param message instance de Text
     * @param bouton Boutoun
     */
    private <T> void window(ListView<T> listView, Text message,Button bouton, String titre){
        Stage chooseAction = new Stage(StageStyle.UTILITY);

        chooseAction.initOwner(primaryStage);
        chooseAction.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();
        TextFlow text = new TextFlow();
        bouton.setText("Choisir");


        text.getChildren().add(message);
        vBox.getChildren().addAll(text, listView,bouton);


        Scene scene = new Scene(vBox);


        scene.getStylesheets().add("chooser.css");
        chooseAction.setScene(scene);
        chooseAction.setOnCloseRequest(Event::consume);
        chooseAction.setTitle(titre);

        chooseAction.show();




    }

    /**
     * ouvre une fenêtre permettant au joueur de faire son choix;
     * une fois celui-ci confirmé, le gestionnaire de choix est appelé avec ce choix en argument
     * @param tickets multiensemble de tickets
     * @param chooseTicketsHandler Gestion d'action de tickets
     */

    public void chooseTickets(SortedBag<Ticket> tickets, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();



        Text message = new Text();
        int plural = tickets.size() == 5 ? 3 :1 ;
        message.textProperty().bind(Bindings.format(StringsFr.CHOOSE_TICKETS, tickets.size()-2, StringsFr.plural(plural)));

        ListView<Ticket> listView = new ListView<>(FXCollections.observableArrayList(tickets.toList()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        Button bouton = new Button();
        bouton.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(tickets.size()-2));
        bouton.setOnAction(event ->{
            chooseTicketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));

            bouton.getScene().getWindow().hide();

        } );

        window( listView,message,bouton,StringsFr.TICKETS_CHOICE);

    }

    /**
     * Fonction qui autorise le joueur a choisir une carte wagon/locomotive, soit l'une des cinq dont la face est visible, soit celle du sommet de la pioche;
     *  une fois que le joueur a cliqué sur l'une de ces cartes, le gestionnaire est appelé avec le choix du joueur;
     *  cette méthode est destinée à être appelée lorsque le joueur a déjà tiré une première carte et doit maintenant tirer la seconde,
     * @param drawCardHandler Gestion d'action de cartes
     */
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        this.drawCard.set((t)->{
            drawCardHandler.onDrawCard(t);
            this.drawCard.set(null);
        });

    }

    /**
     *ouvre une fenêtre similaire permettant au joueur de faire son choix
     * @param claimCards liste de multiensembles de cartes,
     * @param chooseCardsHandler un gestionnaire de choix de cartes
     */
    public void chooseClaimCards(List<SortedBag<Card>> claimCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        Text message = new Text();
        message.textProperty().bind(Bindings.format(StringsFr.CHOOSE_CARDS));

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableArrayList(claimCards));
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        Button bouton = new Button();
        bouton.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(1));

        bouton.setOnAction(event->{
            bouton.getScene().getWindow().hide();


            chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });


        window( listView,message,bouton,StringsFr.CHOOSE_CARDS);

    }

    /**
     *qui ouvre une fenêtre similaire  permettant au joueur de faire son choix;
     * @param claimCard une liste de multiensembles de cartes,
     * @param chooseCardsHandler un gestionnaire de choix de cartes
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> claimCard, ActionHandlers.ChooseCardsHandler chooseCardsHandler ){
        assert isFxApplicationThread();

        Text message = new Text();
        message.textProperty().bind(Bindings.format(StringsFr.CHOOSE_CARDS));

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableArrayList(claimCard));
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        Bindings.size(listView.getSelectionModel().getSelectedItems());
        Button button = new Button();
        button.setOnAction(event->{
            if(listView.getSelectionModel().getSelectedItems().isEmpty()){
                chooseCardsHandler.onChooseCards(SortedBag.of());

            }
            else {
                chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
            }
            button.getScene().getWindow().hide();

        });



        window( listView,message,button,StringsFr.CHOOSE_ADDITIONAL_CARDS);

    }









    private Button etape12() {
        if (!isFxApplicationThread()) throw new AssertionError();

        Button button = new Button();
        button.setText("Chemin plus long");
        Label textFlow = new Label();
        VBox chemin = new VBox();

        chemin.getStylesheets().add("long.css");

        gameState.getLongest().addListener((observable, oldValue, newValue) -> textFlow.setText(newValue));
        Button cache = new Button();
        cache.setText("MONTRER VOTRE CHEMIN");
        cache.setOnAction(event -> {
            gameState.setTrail();
            cache.getScene().getWindow().hide();

        });

        chemin.getChildren().setAll(textFlow, cache);
        Scene scene = new Scene(chemin);


        button.setOnAction(event -> {


            Stage chooseAction = new Stage(StageStyle.UTILITY);






            scene.getStylesheets().add("chooser.css");
            chooseAction.setScene(scene);

            chooseAction.show();

        });
        return button;

    }

    private Button ticketspoints(){
        if (!isFxApplicationThread()) throw new AssertionError();

        Button button=new Button();
        button.setText("Ticket Points");
        Label textFlow=new Label();
        gameState.getNbTicketPoints().addListener((observable, oldValue, newValue) -> {

            textFlow.setText(String.format(StringsFr.TICKET_POINTS,newValue));

        });

        button.setOnAction(event -> {
            VBox chemin=new VBox();


            chemin.getChildren().add(textFlow);
            Stage chooseAction = new Stage(StageStyle.UTILITY);


            Scene scene = new Scene(chemin);


            scene.getStylesheets().add("chooser.css");
            chooseAction.setScene(scene);

            chooseAction.show();

        });
        return button;

    }


    private Clip Music(String path) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        InputStream file = new BufferedInputStream(getClass().getResourceAsStream(path));
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }




}
