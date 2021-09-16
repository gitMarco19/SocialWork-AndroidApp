package com.social.socialjobs.addon;

import java.util.Comparator;

/**
 * Created by Marco on 13/01/2020.
 *
 * Java class per l'ordinamento per ora delle prenotazioni .
 */
public class SortbyTime implements Comparator<Prenotation> {

    @Override
    public int compare(Prenotation a, Prenotation b) {
        if (a.getStartTime().equals(b.getStartTime()))
            return 0;
        else if (a.getStartTime().after(b.getStartTime()))
            return 1;
        else
            return -1;
    }
}
