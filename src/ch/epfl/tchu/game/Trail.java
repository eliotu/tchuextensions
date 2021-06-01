package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
/**
 * Classe Trail.
 *
 * @author Elias Naha (326050)
 * @author Eliot Ullmo (312917)
 */

public final class Trail {

    private final Station station1;
    private final Station station2;
    private final List<Route> routes;

    /**
     * Construit un chemin à l'aide des routes
     * du joueur.
     *
     * @param station1
     *            la première station
     * @param station2
     *            la dernière station
     * @param routes
     *            liste de routes du joueur
     *
     */

    private Trail(Station station1, Station station2, List<Route> routes) {
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;


    }

    /**
     * Renvoie le chemin le plus long
     * du joueur.
     *
     * @param routes
     *            la liste de routes
     * @return chemin_long
     */


    public static Trail longest(List<Route> routes) {

        if (routes.size() == 0) {
            return new Trail(null, null, List.of());

        }

        ArrayList<Trail> chemin_cs = new ArrayList<>();
        Trail chemin_long = null;
        int conteur=0;

        for (Route route23 : routes) {

            Trail voyage = new Trail(route23.station1(), route23.station2(), List.of(route23));
            chemin_cs.add(voyage);
            voyage = new Trail(route23.station2(), route23.station1(), List.of(route23));
            chemin_cs.add(voyage);


        }


        while (chemin_cs.size() != 0) {
            ArrayList<Trail> aux = new ArrayList<>();


            for (Trail chemin : chemin_cs) {
                List<Route> liste_route = new ArrayList(routes);

                for (Route route : liste_route) {

                    List<Route> liste_route_nouv = new ArrayList(chemin.routes);
                    if(!liste_route_nouv.contains(route)) {

                        if (chemin.station2.equals(route.station1())
                                || chemin.station2.equals(route.station2())) {
                            liste_route_nouv.add(route);
                            Trail c = new Trail(chemin.station1, route.stationOpposite(chemin.station2), liste_route_nouv);

                            aux.add(c);


                        }
                    }


                }
            }
            int length = 0;
            for (Route route : cheminLong(chemin_cs).routes) {
                length += route.length();
            }
            if(length>conteur){
                conteur=length;
                chemin_long=cheminLong(chemin_cs);
            }
            chemin_cs = aux;


            // }


        }

        return chemin_long;
    }





    private static Trail cheminLong(List<Trail> chemin_cs){
        Trail chemin_long = null;

        int length;
        int count = 0;
        int tmp = 0;
        for (Trail voyage : chemin_cs) {
            for (Route route : voyage.routes) {
                tmp += route.length();

            }
            length = tmp;
            tmp = 0;

            if (length > count) {
                count = length;
                chemin_long = voyage;
            }
        }
        return chemin_long;

    }

    /**
     * Retourne la longueur du chemin.
     *
     * @return length
     *
     */
    public int length() {

        return longest(routes).routes.stream()
                .mapToInt(Route::length)
                .reduce(0, Integer::sum);
    }

    /**
     * Retourne la première gare du chemin
     * ou null ssi la longueur du chemin est 0.
     *
     * @return station1
     *
     */


    public Station station1() {
        return (this.routes.size() == 0 ? null : this.station1);
    }

    /**
     * Retourne la seconde gare du chemin
     * ou null ssi la longueur du chemin est 0.
     *
     * @return station2
     *
     */

    public Station station2() {
        return (this.routes.size() == 0 ? null : this.station2);
    }

    /**
     * Retourne une représentation textuelle
     * du chemin incluant toutes les gares
     * le composant.
     *
     * @return s
     *
     */


    @Override
    public String toString() {

        Station station = longest(routes).station1;
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(station);

        for (Route route : longest(routes).routes) {
            stations.add(route.stationOpposite(station));
            station = route.stationOpposite(station);
        }

        String s = "";

        for (int i = 0; i < stations.size(); ++i) {
            s += i == stations.size() - 1 ? stations.get(i).name() + " " + "(" + length() + ")"
                    : stations.get(i).name() + " - ";

        }

        return s;
    }



    public List<Route> routes(){
        return this.routes;
    }
}
