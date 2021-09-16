package com.social.socialjobs.addon;

import java.util.Comparator;

/**
 * Created by Marco on 13/01/2020.
 *
 * Java class per l'ordinamento per data delle prenotazioni.
 */
public class SortbyData implements Comparator<Prenotation> {

    @Override
    public int compare(Prenotation a, Prenotation b) {
        if (a.getDataInizio().equals(b.getDataInizio()))
            return 0;
        else if (a.getDataInizio().after(b.getDataInizio()))
            return 1;
        else
            return -1;
    }
}

