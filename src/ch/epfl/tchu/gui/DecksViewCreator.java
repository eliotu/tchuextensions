package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Class DecksViewCreator
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

final class DecksViewCreator {
    private static final String DEFAULT_CONTROL_INNER_BACKGROUND = "derive(-fx-base,80%)";
    private static final String HIGHLIGHTED_CONTROL_INNER_BACKGROUND = "derive(palegreen, 50%)";
    public static ObservableList<Ticket> ticketsselec= FXCollections.observableArrayList();

    /**
     *  createHandView, prend en argument l'état du jeu observable et retourne la vue de la main,
     * @param gameState l'état du jeu observable
     * @return  la vue de la main
     */
    public static Node createHandView(ObservableGameState gameState){
        HBox deck=new HBox();
        deck.getStylesheets().addAll("decks.css","colors.css");


        gameState.getTicketListView().setCellFactory(new Callback<>() {

            @Override
            public ListCell<Ticket> call(ListView<Ticket> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Ticket item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
                        } else {
                            setText(item.text());
                            if (gameState.getTicketComplete(item).get()) {
                                setStyle("-fx-control-inner-background: " + HIGHLIGHTED_CONTROL_INNER_BACKGROUND + ";");
                            } else {
                                setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
                            }
                        }
                    }
                };
            }
        });



        gameState.getTicketListView().setId("tickets");
        deck.getChildren().add(gameState.getTicketListView());

        HBox children=new HBox();
        children.setId("hand-pane");
        for(Card carte:Card.ALL){

            children.getChildren().add(carteCompteur(gameState,carte));
        }
        deck.getChildren().add(children);
        return deck;
    }


    /**
     *
     * @param gameState l'état de jeu observable
     * @param drawTickets un gestionnaire d'action  gérant le tirage de billets
     * @param drawCard un gestionnaire d'action  gérant le tirage de cartes
     * @return la vue des cartes
     */
    public static Node createCardsView(ObservableGameState gameState
                                    ,  ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets,
                                       ObjectProperty<ActionHandlers.DrawCardHandler> drawCard){


        VBox vbox=new VBox();
        vbox.setId("card-pane");
        vbox.getStylesheets().addAll("decks.css","colors.css");
        //boutons

        Button tickBut=button(gameState.getPourcentageBillets(),"Billets");


        tickBut.disableProperty().bind(drawTickets.isNull());

        tickBut.setOnAction(event -> drawTickets.getValue().onDrawTickets());



        vbox.getChildren().add(tickBut);



        for(int slot:Constants.FACE_UP_CARD_SLOTS){
            StackPane card=new StackPane();
            gameState.faceUpCard(slot).addListener((observable, oldValue, newValue) -> {
                String colour=newValue.color()==null?"NEUTRAL":gameState.faceUpCard(slot).get().color().name();
                card.getStyleClass().set(0, colour);
            });
           card.getStyleClass().addAll("bogus_class","card");

            Rectangle outside=new Rectangle(60,90);
            outside.getStyleClass().add("outside");
            Rectangle filled=new Rectangle(40,70);
            filled.getStyleClass().addAll("filled","inside");
            Rectangle train=new Rectangle(40,70);
            train.getStyleClass().add("train-image");

            card.getChildren().addAll(outside,filled,train);
            card.setOnMouseClicked(event -> transition(slot,card,gameState.faceUpCard(slot).get().color(),drawCard));
            card.disableProperty().bind(drawCard.isNull());

            vbox.getChildren().add(card);


        }

        Button cardBut=button(gameState.getPourcentageCartes(),"Cartes");
        cardBut.setOnAction(event -> drawCard.getValue().onDrawCard(Constants.DECK_SLOT));


        cardBut.disableProperty().bind(drawCard.isNull());



        vbox.getChildren().add(cardBut);
        return vbox;
    }


    private static StackPane carteCompteur(ObservableGameState gameState,Card carte){
        ReadOnlyIntegerProperty count =gameState.getCartesEnMain(carte);

        StackPane card=new StackPane();
        card.visibleProperty().bind(Bindings.greaterThan(count, 0));

        String colour=carte.color()==null?"NEUTRAL":carte.color().name();

        card.getStyleClass().addAll(colour,"card");

        Rectangle outside=new Rectangle(60,90);
        outside.getStyleClass().add("outside");

        Rectangle filled=new Rectangle(40,70);
        filled.getStyleClass().addAll("filled","inside");

        Rectangle train=new Rectangle(40,70);
        train.getStyleClass().add("train-image");

        Text compteur=new Text();

        compteur.textProperty().bind(Bindings.convert(count));
        compteur.visibleProperty().bind(Bindings.greaterThan(count, 1));


        compteur.getStyleClass().add("count");

        card.getChildren().addAll(outside,filled,train,compteur);
        return card;
    }


    public static  ObservableList<Ticket> getListBillets() {
        return FXCollections.unmodifiableObservableList(ticketsselec);
    }


    private static Button button(ReadOnlyIntegerProperty integerProperty,String text){
        Button button=new Button();

        Group groupBut=new Group();
        Rectangle rect_background= new Rectangle(50,5);
        rect_background.getStyleClass().add("background");
        Rectangle gaugForeground= new Rectangle(50,5);
        gaugForeground.getStyleClass().add("foreground");
        gaugForeground.widthProperty().bind(integerProperty.multiply(50).divide(100));



        groupBut.getChildren().addAll(rect_background,gaugForeground);
        button.setGraphic(groupBut);
        button.getStyleClass().add("gauged");
        button.setText(text);
        return button;


    }

    private static void transition(int slot, StackPane card, Color color,ObjectProperty<ActionHandlers.DrawCardHandler> drawCard)  {

        Duration duration = Duration.millis(850);
        Duration duration1=Duration.millis(600);

        TranslateTransition transition = new TranslateTransition(duration, card);
        TranslateTransition transition1 = new TranslateTransition(duration1, card);
        int y=0;
        switch (slot) {
            case 0:
                transition.setByY(619);
                y=620;
                break;
            case 1:
                transition.setByY(517);
                y=520;
                break;
            case 2:
                transition.setByY(417);
                y=420;
                break;
            case 3:
                transition.setByY(315);
                y=320;
                break;
            case 4:
                transition.setByY(215);
                y=220;
                break;
            default:
                break;
        }
        if(color==null){
            transition.setByX(-328);

        }
        else {

            switch (color) {
                case BLACK:
                    transition.setByX(-897);
                    break;
                case VIOLET:
                    transition.setByX(-824);
                    break;
                case BLUE:
                    transition.setByX(-754);

                    break;
                case GREEN:
                    transition.setByX(-683);

                    break;
                case YELLOW:
                    transition.setByX(-612);

                    break;
                case ORANGE:
                    transition.setByX(-541);

                    break;
                case RED:
                    transition.setByX(-470);

                    break;
                case WHITE:
                    transition.setByX(-398);

                    break;

                case VOLEUR:
                    transition.setByX(-257);

                    break;
                default:

                    break;
            }
        }


        transition.setCycleCount(1);
        transition.setAutoReverse(true);
       // transition.jumpTo(new Duration(100));


        int finalY = y;
        transition.setOnFinished(event ->  {
            drawCard.get().onDrawCard(slot);
            transition1.setToX(0);
            transition1.setByY(-finalY);
            transition1.play();


        } );
        transition.play();


        /** FadeTransition fadeTransition=new FadeTransition();
         fadeTransition.setNode(transition.getNode());
         fadeTransition.setDuration(new Duration(2000));
         fadeTransition.setCycleCount(1);
         fadeTransition.setInterpolator(Interpolator.LINEAR);
         fadeTransition.setFromValue(1);
         fadeTransition.setToValue(0);
         fadeTransition.play();
         fadeTransition.setToValue(1);**/








    }

}
