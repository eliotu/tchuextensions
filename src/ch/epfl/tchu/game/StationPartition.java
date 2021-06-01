package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Classe StationPartition
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public final class StationPartition implements StationConnectivity {
    private int[] tab;

    /**
     * Construit le tableau d'entiers contenant les liens liant chaque
     * élément au représentant de leur sous-ensemble.
     *
     * @param tab
     *            tableau d'entiers contenant les liens liant
     *            chaque élément au représentant de leur sous-ensemble;
     */

    private StationPartition(int[] tab) {
        this.tab = tab.clone();
    }

    /**
     * Renvoie true que si les deux gares ont la même identité
     *
     * @param s1
     *            première gare
     * @param s2
     *            deuxième gare
     * @return true si les deux gares ont la même identité
     *         sinon false
     */


    @Override
    public boolean connected(Station s1, Station s2) {
        if(Math.max(s1.id(),s2.id())>=this.tab.length){
            if(s1.id()< tab.length){
                return this.tab[s1.id()]==s2.id();
            }
            else if(s2.id()<tab.length){
                return this.tab[s2.id()]==s1.id();

            }
            else{
                return s1.id()==s2.id();
            }
        }
        else {


            if (this.tab[s1.id()] == this.tab[s2.id()]) {
                return true;
            }
        }
        return false;

    }

    /**
     * Classe imbriquée qui construit
     * la version profonde de la partition.
     *
     */

    public static final class Builder {
        private int[] tab;

        /**
         * Construit un bâtisseur de partition d'un ensemble de gares
         * dont l'identité est comprise entre 0 (inclus) et stationCount (exclus).
         *
         * @param stationCount
         *            taille du tableau
         * @throws IllegalArgumentException
         *             si stationCount est négatif
         */

        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            this.tab = new int[stationCount];

            for (int i = 0; i < stationCount; ++i) {
                tab[i] = ChMap.stations().get(i).id();

            }

        }

        /**
         * Retourne le représentant du sous-ensemble de la
         * gare fournie en argument.
         *
         * @param id
         *          identité d'une gare
         *
         * @return le numéro d'identification du représentant
         *         du sous-ensemble.
         */

        private int representative(int id) {
            while (id != this.tab[id]) {
                id = this.tab[id];

            }
            return this.tab[id];
        }


        /**
         * Joint les sous-ensembles contenant les deux gares passées en argument,
         * en choissisant la deuxième gare fournie comme représentant du sous-ensemble joint;
         * Retourne le bâtisseur.
         *
         * @param s1
         *          première gare
         * @param s2
         *          deuxième gare
         *
         * @return le bâtisseur (this).
         *
         */

        public Builder connect(Station s1, Station s2) {//s2 sera tjrs notre representant

            int b=representative(s2.id());
            int a=representative(s1.id());
            for(int i=0;i<this.tab.length;++i){
                if(this.tab[i]==a){
                    this.tab[i]=b;


                }
            }

            return this;
        }


        /**
         * Retourne la partition aplatie des gares correspondant à la
         * partition profonde en cours de construction par ce bâtisseur.
         *
         * @return la partition aplatie des gares
         *
         */


        public StationPartition build() {

            return new StationPartition(this.tab);

        }

    }
}
