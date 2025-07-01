package com.primeraappf.agendacontactos;

import java.util.*;

public class ComparadoresContacto {

    public static Comparator<Contacto> porNombre = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("nombre", "").toLowerCase()
    );

    public static Comparator<Contacto> porTipo = Comparator.comparing(
            c -> c.getTipo().toLowerCase()
    );

    public static Comparator<Contacto> porPais = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("pais", "").toLowerCase()
    );

    public static Comparator<Contacto> porCantidadAtributos = (c1, c2) ->
            Integer.compare(c2.getAtributos().size(), c1.getAtributos().size());
}
