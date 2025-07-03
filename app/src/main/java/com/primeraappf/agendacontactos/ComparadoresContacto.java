package com.primeraappf.agendacontactos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ComparadoresContacto {

    public static Comparator<Contacto> porNombre = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("nombre", "").toLowerCase()
    );

    public static Comparator<Contacto> porApellido = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("apellido", "").toLowerCase()
    );

    public static Comparator<Contacto> porApellidoNombre = porApellido.thenComparing(porNombre);

    public static Comparator<Contacto> porTipo = Comparator.comparing(
            c -> c.getTipo().toLowerCase()
    );

    public static Comparator<Contacto> porProvincia = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("provincia", "").toLowerCase()
    );

    public static Comparator<Contacto> porEmpresa = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("empresa", "").toLowerCase()
    );

    public static Comparator<Contacto> porPais = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("pais", "").toLowerCase()
    );

    public static Comparator<Contacto> porCantidadAtributos = (c1, c2) ->
            Integer.compare(c2.getAtributos().size(), c1.getAtributos().size());

    public static Comparator<Contacto> porCumpleanos = (c1, c2) -> {
        String fecha1 = c1.getAtributos().getOrDefault("cumpleaños", "");
        String fecha2 = c2.getAtributos().getOrDefault("cumpleaños", "");

        if (fecha1.isEmpty() && fecha2.isEmpty()) return 0;
        if (fecha1.isEmpty()) return 1;
        if (fecha2.isEmpty()) return -1;

        try {
            Calendar hoy = Calendar.getInstance();
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date date1 = formato.parse(fecha1);
            Date date2 = formato.parse(fecha2);

            Calendar cFecha1 = Calendar.getInstance();
            Calendar cFecha2 = Calendar.getInstance();

            cFecha1.setTime(date1);
            cFecha2.setTime(date2);

            // Reemplazamos el año por el actual
            cFecha1.set(Calendar.YEAR, hoy.get(Calendar.YEAR));
            cFecha2.set(Calendar.YEAR, hoy.get(Calendar.YEAR));

            // Si la fecha ya pasó este año, ponemos para el siguiente
            if (cFecha1.before(hoy)) cFecha1.add(Calendar.YEAR, 1);
            if (cFecha2.before(hoy)) cFecha2.add(Calendar.YEAR, 1);

            return cFecha1.compareTo(cFecha2);

        } catch (ParseException e) {
            return 0;
        }
    };
}

