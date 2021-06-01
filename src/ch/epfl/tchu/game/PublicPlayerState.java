package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public class PublicPlayerState {
    private int ticketCount;
    private int cardCount;
    private List<Route> routes;
    private  int carCount;
    private int claimPoints;




    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {

        Preconditions.checkArgument(ticketCount >= 0);
        Preconditions.checkArgument(cardCount >= 0);

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = routes;
        int length=0;

        for(Route route:this.routes){
            length+=route.length();
            this.claimPoints+=route.claimPoints();
        }

        this.carCount=Constants.INITIAL_CAR_COUNT-length;

    }

    public int ticketCount() {
        return ticketCount;
    }

    public int cardCount() {
        return cardCount;
    }

    public List<Route> routes() {
        return routes;
    }

    public int carCount() {
        return carCount;
    }

    public int claimPoints() {
        return claimPoints;
    }


}
