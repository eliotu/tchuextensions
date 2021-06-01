package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Interface générique Serde.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public interface Serde<T> {

    /**
     * Prend en argument un objet à sérialiser et retourne la chaîne correspondante.
     * @param object
     *          Objet à sérialiser
     * @return la chaîne correspondante
     */

     String serialize(T object);

    /**
     * Prend en argument une chaîne et retourne l'objet correspondant.
     * @param message
     *       chaîne de caractères
     * @return objet correspondant
     */

      T deserialize(String message);

    /**
     * Méthode générique prenant en arguments une fonction de
     * sérialisation et une fonction de désérialisation, et retournant le serde correspondant.
     *
     * @param serialize
     *             type de la fonction de sérialisation
     * @param deserialize
     *             type de la fonction de désérialisation
     * @param <T>
     *             paramètre de type de la méthode
     * @return le serde serialise
     */


    static <T> Serde<T> of(Function<T, String> serialize, Function<String, T> deserialize) {



        return new Serde<>() {
            @Override
            public String serialize(T object) {
                return serialize.apply(object);
            }

            @Override
            public T deserialize(String a) {
                return deserialize.apply(a);
            }
        };
    }

    /**
     * Méthode générique prenant en argument la liste de toutes les valeurs d'un
     * ensemble de valeurs énuméré et retournant le serde correspondant.
     * @param list
     *              liste de toutes les valeurs d'un ensemble de valeurs énumérées
     * @return serde correspondant
     */

    static <T> Serde<T> oneOf(List<T> list) {
        Preconditions.checkArgument(!list.isEmpty());
        Function<T, String> serialize = (objet) -> String.valueOf(list.indexOf(objet));
        Function<String, T> deserialize = (str) -> list.get(Integer.parseInt(str));
        return of(serialize,deserialize);
    }

    /**
     * Méthode générique prenant en argument un serde et un caractère de séparation et retournant un serde
     * capable de (dé)sérialiser des listes de valeurs (dé)sérialisées par le serde donné.
     * @param serde
     *           serde fourni
     * @param charSequence
     *           caractère de séparation
     * @return serde capable de désérialiser par le serde donné
     */

    static <T> Serde<List<T>> listOf(Serde<T> serde, CharSequence charSequence) {

        Preconditions.checkArgument(!charSequence.equals(""));
        Function<List<T>, String> serialize = list -> list.isEmpty() ? "" : list.stream().map(serde::serialize)
                                                                            .collect(Collectors.joining(charSequence));


        Function<String, List<T>> deserialize = str -> str.equals("") ? List.of() : Arrays.stream(str.split(Pattern.quote(charSequence.toString()), -1))
                                                                                            .map(serde::deserialize).collect(Collectors.toList());
        return of(serialize, deserialize);
    }

    /**
     * Méthode générique similar à listOf mais pour les multiensembles triés.
     * @param serde
     *           serde fourni
     * @param charSequence
     *           caractère de séparation
     * @return serde capable de désérialiser par le serde donné
     */

    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, CharSequence charSequence) {

        Preconditions.checkArgument(!charSequence.equals(""));

        return of(sortedBag -> listOf(serde, charSequence).serialize(sortedBag.toList()),
                str -> SortedBag.of(listOf(serde, charSequence).deserialize(str)));
    }
}
