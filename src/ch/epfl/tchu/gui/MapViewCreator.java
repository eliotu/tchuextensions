package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;
/**
 * Class MapViewCreator
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

final class MapViewCreator {
    /**
     * Fonction qui permet de créer la vue de la carte.
     * @param gameState l'état du jeu observable
     * @param routeHandlerm une propriété contenant le gestionnaire d'action à utiliser lorsque le joueur désire s'emparer d'une route
     * @param cardChooser un «sélectionneur de cartes»
     * @return la vue de la carte
     */

    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ActionHandlers.ClaimRouteHandler> routeHandlerm, CardChooser cardChooser) {


        Pane canvas = new Pane();


        canvas.getStylesheets().addAll("map.css","colors.css","long.css");

        ImageView image = new ImageView();



        canvas.getChildren().add(image);

        for(Route route: ChMap.routes()) {

            Group groupRoute = new Group();
            groupRoute.disableProperty().bind(
                    routeHandlerm.isNull().or(gameState.getRoutepossible(route).not()));

            groupRoute.setId(route.id());
            String colour = route.color() == null ? "NEUTRAL" : route.color().name();
            groupRoute.getStyleClass().addAll("route", colour, route.level().toString());


            groupRoute.setOnMouseClicked(event -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.getPossibleClaimCars(route);

                ActionHandlers.ClaimRouteHandler claimRouteH = routeHandlerm.getValue();
                if(possibleClaimCards.size()>1) {
                    ActionHandlers.ChooseCardsHandler chooseCardsH =
                            chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
                else{
                    claimRouteH.onClaimRoute(route,possibleClaimCards.get(0));
                }
            });



            gameState.getRouteId(route).addListener((observable, oldValue, newValue) -> groupRoute.getStyleClass().add(newValue.name()));

            for (int i = 0; i < route.length(); i++) {
                Group groupCase = new Group();
                String id = String.format("%s_%d", route.id(), i + 1);
                groupCase.setId(id);

                Rectangle rectangle = new Rectangle(36, 12);
                Glow glow=new Glow();
                glow.setLevel(0.5);
                rectangle.setEffect(glow);
                rectangle.getStyleClass().addAll("filled","track");



                Group groupWagon = new Group();
                Rectangle rectTaken = new Rectangle(36, 12);
                rectTaken.setEffect(glow);
                rectTaken.getStyleClass().add("filled");

                groupWagon.getStyleClass().add("car");
                Circle circleTaken1 = new Circle(12, 6, 3);
                Circle circleTaken2 = new Circle(24, 6, 3);
                groupWagon.getChildren().addAll(rectTaken,circleTaken1,circleTaken2);

                groupCase.getChildren().add(rectangle);


                groupCase.getChildren().add(groupWagon);

                groupRoute.getChildren().add(groupCase);







            }

            canvas.getChildren().add(groupRoute);

        }
        gameState.getTicketListView().getSelectionModel().selectedIndexProperty().addListener(observable -> {
            System.out.println(canvas.getChildren().size());
            System.out.println("routes:"+ChMap.routes().size());
            Group group=new Group();

            if(canvas.getChildren().size()!=89){
                canvas.getChildren().remove(canvas.getChildren().size()-1);
            }

            System.out.printf("Indice sélectionné: %d", gameState.getTicketListView().getSelectionModel().getSelectedIndex()).println();
            if(!gameState.getTicketListView().getSelectionModel().isEmpty()) {
                for (Station station : gameState.getTicketListView().getSelectionModel().getSelectedItem().stations()) {
                    Circle circle = new Circle(0, 0, 10);
                    circle.setId(String.format("%s", station.id()));
                    circle.setFill(Paint.valueOf("GREEN"));

                    group.getChildren().add(circle);

                }
                canvas.getChildren().add(group);
            }






        });


        gameState.trailReadOnlyObjectProperty().addListener(observable -> {
            Group group = new Group();
            System.out.println("1");
                for (Route route : gameState.trailReadOnlyObjectProperty().get().routes()) {
                    System.out.println(2);
                    for (int i = 0; i < route.length(); i++) {
                        Group groupCase = new Group();

                        String id = String.format("%s_%d", route.id(), i + 1);
                        groupCase.setId(id);
                        Rectangle rectangle = new Rectangle(36, 12);
                        rectangle.setFill(Paint.valueOf("PURPLE"));
                        groupCase.getChildren().add(rectangle);
                        group.getChildren().add(groupCase);

                    }

                }
                canvas.getChildren().add(group);


            group.setOnMouseClicked(event -> canvas.getChildren().remove(group));



        });
        return canvas;

    }





    /**
     * Interface  CardChooser
     * interface  destinée à être appelée lorsque le joueur doit choisir les cartes qu'il désire utiliser pour s'emparer d'une route
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         *
         * @param options List de multiensemble de cartes
         * @param handler Gestionaire d'action
         */
        void chooseCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler handler);
    }
}
