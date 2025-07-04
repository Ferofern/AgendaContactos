package com.primeraappf.agendacontactos;

import android.content.Context;
import java.io.*;
import java.util.*;

public class GestorContactos {
    private static GestorContactos instancia;
    private TreeMap<String, Contacto> contactos;
    private Queue<Integer> colaIds;
    private static final String NOMBRE_ARCHIVO = "contactos.txt";
    private Context context;

    private GestorContactos(Context context) {
        this.context = context.getApplicationContext();
        contactos = new TreeMap<>();
        colaIds = new ArrayDeque<>();
        for (int i = 1; i <= 1000; i++) {
            colaIds.add(i);
        }
        cargarContactos();
    }

    public static synchronized GestorContactos getInstance(Context context) {
        if (instancia == null) {
            instancia = new GestorContactos(context);
        }
        return instancia;
    }

    public Contacto crearContacto(String tipo, String telefonoInicial) {
        if (telefonoInicial == null || telefonoInicial.length() < 2) {
            throw new IllegalArgumentException("El teléfono inicial debe tener al menos 2 dígitos.");
        }
        if (colaIds.isEmpty()) {
            throw new RuntimeException("No hay más IDs disponibles.");
        }
        int numeroCola = colaIds.poll();
        String ultimosDos = telefonoInicial.substring(telefonoInicial.length() - 2);
        String id = numeroCola + ultimosDos;
        Contacto c = new Contacto(id, tipo, telefonoInicial);
        contactos.put(id, c);
        return c;
    }

    public void eliminarContacto(String id) {
        if (contactos.containsKey(id)) {
            contactos.remove(id);
            try {
                String numeroStr = id.substring(0, id.length() - 2);
                int numero = Integer.parseInt(numeroStr);
                colaIds.add(numero);
            } catch (NumberFormatException ignored) {}
        }
    }

    public Contacto getPorId(String id) {
        return contactos.get(id);
    }

    public Contacto[] getTodos() {
        Collection<Contacto> valores = contactos.values();
        return valores.toArray(new Contacto[0]);
    }

    public boolean existeId(String id) {
        return contactos.containsKey(id);
    }

    public void asociarContactos(String id1, String id2) {
        Contacto c1 = contactos.get(id1);
        Contacto c2 = contactos.get(id2);
        if (c1 != null && c2 != null) {
            c1.addAsociado(c2);
            c2.addAsociado(c1);
        }
    }

    public void guardarContactos() {
        try (FileOutputStream fos = context.openFileOutput(NOMBRE_ARCHIVO, Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {

            for (Contacto c : contactos.values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(c.getId()).append("|");
                sb.append(c.getTipo()).append("|");

                for (int i = 0; i < c.getTotalTelefonos(); i++) {
                    sb.append(c.getTelefonosArray()[i]);
                    if (i < c.getTotalTelefonos() - 1) sb.append(",");
                }
                sb.append("|");

                int countAttr = 0;
                for (Map.Entry<String, String> entry : c.getAtributos().entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                    if (countAttr < c.getAtributos().size() - 1) sb.append(",");
                    countAttr++;
                }
                sb.append("|");

                sb.append("|"); // No fotos

                for (int i = 0; i < c.getTotalAsociados(); i++) {
                    sb.append(c.getAsociadosArray()[i].getId());
                    if (i < c.getTotalAsociados() - 1) sb.append(",");
                }
                sb.append("\n");

                writer.write(sb.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarContactos() {
        try (FileInputStream fis = context.openFileInput(NOMBRE_ARCHIVO);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            contactos.clear();
            colaIds.clear();
            for (int i = 1; i <= 1000; i++) {
                colaIds.add(i);
            }

            String linea;
            Map<String, Contacto> tempContactos = new HashMap<>();
            Map<String, List<String>> asociadosMap = new HashMap<>();

            while ((linea = reader.readLine()) != null) {
                android.util.Log.d("GestorContactos", "Leyendo línea: " + linea);

                String[] partes = linea.split("\\|");
                if (partes.length < 4) {
                    android.util.Log.w("GestorContactos", "Línea ignorada por formato incorrecto: " + linea);
                    continue;
                }

                String id = partes[0];
                String tipo = partes[1];
                String[] telefonos = partes[2].isEmpty() ? new String[0] : partes[2].split(",");
                String[] atributosStr = partes[3].isEmpty() ? new String[0] : partes[3].split(",");

                Contacto c = new Contacto(id, tipo, telefonos.length > 0 ? telefonos[0] : "");
                for (int i = 1; i < telefonos.length; i++) {
                    c.addTelefono(telefonos[i]);
                }
                for (String attr : atributosStr) {
                    String[] kv = attr.split("=", 2);
                    if (kv.length == 2) {
                        c.addAtributo(kv[0], kv[1]);
                    }
                }

                tempContactos.put(id, c);
                // No asociados ya que no usas fotos y puedes manejar esta parte si quieres
            }

            contactos.putAll(tempContactos);

            for (String id : contactos.keySet()) {
                try {
                    String numeroStr = id.substring(0, id.length() - 2);
                    int numero = Integer.parseInt(numeroStr);
                    colaIds.remove(numero);
                } catch (Exception ignored) {}
            }

            android.util.Log.d("GestorContactos", "Contactos cargados: " + contactos.size());

        } catch (FileNotFoundException e) {
            android.util.Log.i("GestorContactos", "Archivo no encontrado: " + NOMBRE_ARCHIVO + ". Se creará uno nuevo al guardar.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
