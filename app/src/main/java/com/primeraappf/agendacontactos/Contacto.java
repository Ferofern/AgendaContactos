package com.primeraappf.agendacontactos;

import java.util.Map;
import java.util.TreeMap;

public class Contacto {
    private final String id;
    private String tipo;
    private TreeMap<String, String> atributos;

    private String[] telefonos = new String[10];
    private int totalTelefonos = 0;

    private Contacto[] asociados = new Contacto[10];
    private int totalAsociados = 0;

    private String[] fotos;
    private int fotoActual = 0;

    public Contacto(String id, String tipo, String telefonoInicial) {
        this.id = id;
        this.tipo = tipo;
        this.atributos = new TreeMap<>();
        addTelefono(telefonoInicial);
    }

    public String getId() { return id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public TreeMap<String, String> getAtributos() { return atributos; }

    public void setAtributos(Map<String, String> nuevosAtributos) {
        this.atributos.clear();
        this.atributos.putAll(nuevosAtributos);
    }

    public void setFotos(String[] rutas) {
        this.fotos = rutas;
        this.fotoActual = 0;
    }

    public String[] getFotos() {
        return fotos;
    }

    public String getFotoActual() {
        if (fotos == null || fotos.length == 0) return null;
        return fotos[fotoActual];
    }

    public String fotoSiguiente() {
        if (fotos == null || fotos.length == 0) return null;
        fotoActual = (fotoActual + 1) % fotos.length;
        return fotos[fotoActual];
    }

    public String fotoAnterior() {
        if (fotos == null || fotos.length == 0) return null;
        fotoActual = (fotoActual - 1 + fotos.length) % fotos.length;
        return fotos[fotoActual];
    }

    public String[] getTelefonosArray() {
        return telefonos;
    }

    public int getTotalTelefonos() {
        return totalTelefonos;
    }

    public void addTelefono(String telefono) {
        if (telefono != null && !telefono.isEmpty() && totalTelefonos < telefonos.length) {
            for (int i = 0; i < totalTelefonos; i++) {
                if (telefonos[i].equals(telefono)) return;
            }
            telefonos[totalTelefonos++] = telefono;
        }
    }

    public Contacto[] getAsociadosArray() {
        return asociados;
    }

    public int getTotalAsociados() {
        return totalAsociados;
    }

    public void addAsociado(Contacto c) {
        if (totalAsociados < asociados.length) {
            asociados[totalAsociados++] = c;
        }
    }

    public void addAtributo(String clave, String valor) {
        atributos.put(clave, valor);
    }

    public void eliminarAtributo(String clave) {
        atributos.remove(clave);
    }
}
