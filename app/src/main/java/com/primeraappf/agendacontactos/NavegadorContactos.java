package com.primeraappf.agendacontactos;

import java.util.List;

public class NavegadorContactos {
    private Nodo actual;
    private int totalContactos;

    private static class Nodo {
        Contacto contacto;
        Nodo siguiente;
        Nodo anterior;

        Nodo(Contacto contacto) {
            this.contacto = contacto;
        }
    }

    public NavegadorContactos(List<Contacto> contactos) {
        if (contactos == null || contactos.isEmpty()) {
            throw new IllegalArgumentException("La lista de contactos no puede estar vacía.");
        }

        totalContactos = contactos.size();

        Nodo primero = new Nodo(contactos.get(0));
        Nodo prev = primero;

        for (int i = 1; i < contactos.size(); i++) {
            Nodo nuevo = new Nodo(contactos.get(i));
            prev.siguiente = nuevo;
            nuevo.anterior = prev;
            prev = nuevo;
        }

        prev.siguiente = primero;
        primero.anterior = prev;

        actual = primero;
    }

    public Contacto siguiente() {
        actual = actual.siguiente;
        return actual.contacto;
    }

    public Contacto anterior() {
        actual = actual.anterior;
        return actual.contacto;
    }

    public Contacto actual() {
        return actual.contacto;
    }

    public int getTotalContactos() {
        return totalContactos;
    }
}

