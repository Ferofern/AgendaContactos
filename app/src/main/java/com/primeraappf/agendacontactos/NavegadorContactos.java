package com.primeraappf.agendacontactos;

public class NavegadorContactos {
    private Contacto[] lista;
    private int totalContactos;
    private int actual;

    public NavegadorContactos(Contacto[] contactos) {
        if (contactos == null || contactos.length == 0) {
            throw new IllegalArgumentException("La lista de contactos no puede estar vacía.");
        }
        this.lista = contactos;
        this.totalContactos = contactos.length;
        this.actual = 0;
    }

    public Contacto siguiente() {
        actual = (actual + 1) % totalContactos;
        return lista[actual];
    }

    public Contacto anterior() {
        actual = (actual - 1 + totalContactos) % totalContactos;
        return lista[actual];
    }

    public Contacto actual() {
        return lista[actual];
    }
}