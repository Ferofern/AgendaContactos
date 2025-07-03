package com.primeraappf.agendacontactos;

import java.util.*;

public class ComparadorMultiple implements Comparator<Contacto> {
    private final List<Comparator<Contacto>> comparadores;

    public ComparadorMultiple(List<Comparator<Contacto>> comparadores) {
        this.comparadores = comparadores;
    }

    @Override
    public int compare(Contacto c1, Contacto c2) {
        for (Comparator<Contacto> comp : comparadores) {
            int res = comp.compare(c1, c2);
            if (res != 0) return res;
        }
        return 0;
    }
}