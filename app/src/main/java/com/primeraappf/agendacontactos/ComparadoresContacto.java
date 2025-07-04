package com.primeraappf.agendacontactos;

import java.text.SimpleDateFormat;
import java.util.*;

public class ComparadoresContacto {
    public static Comparator<Contacto> porNombre = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("nombre", "").toLowerCase(),
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    public static Comparator<Contacto> porApellido = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("apellido", "").toLowerCase(),
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    public static Comparator<Contacto> porApellidoNombre = porApellido.thenComparing(porNombre);

    public static Comparator<Contacto> porTipo = Comparator.comparing(
            Contacto::getTipo,
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    public static Comparator<Contacto> porProvincia = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("provincia", "").toLowerCase(),
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    public static Comparator<Contacto> porEmpresa = Comparator.comparing(
            c -> c.getAtributos().getOrDefault("empresa", "").toLowerCase(),
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    public static Comparator<Contacto> porCantidadAtributos = Comparator.comparingInt(
            c -> -c.getAtributos().size());

    public static Comparator<Contacto> porCumpleanos = (c1, c2) -> {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date fecha1 = sdf.parse(c1.getAtributos().getOrDefault("cumpleaños", ""));
            Date fecha2 = sdf.parse(c2.getAtributos().getOrDefault("cumpleaños", ""));
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(fecha1);
            cal2.setTime(fecha2);
            int mesDia1 = cal1.get(Calendar.MONTH) * 100 + cal1.get(Calendar.DAY_OF_MONTH);
            int mesDia2 = cal2.get(Calendar.MONTH) * 100 + cal2.get(Calendar.DAY_OF_MONTH);
            return Integer.compare(mesDia1, mesDia2);
        } catch (Exception e) {
            return 0;
        }
    };
}