package com.primeraappf.agendacontactos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private GestorContactos gestorContactos;
    private NavegadorContactos navegador;

    private TextView tvNombreCompleto, tvTipo, tvTelefonos;
    private Button btnAnterior, btnSiguiente;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestorContactos = GestorContactos.getInstance(this);

        Contacto[] contactosArray = gestorContactos.getTodos();

        // Ordenar alfabéticamente por nombre + apellido
        List<Contacto> contactosOrdenados = Arrays.stream(contactosArray)
                .sorted(Comparator.comparing(
                        c -> (c.getAtributos().getOrDefault("nombre", "") + " " + c.getAtributos().getOrDefault("apellido", ""))
                                .toLowerCase()
                ))
                .collect(Collectors.toList());

        if (contactosOrdenados.isEmpty()) {
            Toast.makeText(this, "No hay contactos para mostrar", Toast.LENGTH_SHORT).show();
            return;
        }

        navegador = new NavegadorContactos(contactosOrdenados);

        // Referencias UI
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        tvTipo = findViewById(R.id.tvTipo);
        tvTelefonos = findViewById(R.id.tvTelefonos);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        btnAnterior.setOnClickListener(v -> mostrarContacto(navegador.anterior()));
        btnSiguiente.setOnClickListener(v -> mostrarContacto(navegador.siguiente()));

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_buscar) {
                buscarContacto();
                return true;
            } else if (id == R.id.nav_anadir) {
                anadirContacto();
                return true;
            } else if (id == R.id.nav_eliminar) {
                eliminarContacto();
                return true;
            }
            return false;
        });

        // Mostrar primer contacto
        mostrarContacto(navegador.actual());
    }

    private void mostrarContacto(Contacto c) {
        String nombre = c.getAtributos().getOrDefault("nombre", "");
        String apellido = c.getAtributos().getOrDefault("apellido", "");
        tvNombreCompleto.setText(nombre + " " + apellido);
        tvTipo.setText("Tipo: " + c.getTipo());

        StringBuilder telefonos = new StringBuilder();
        for (int i = 0; i < c.getTotalTelefonos(); i++) {
            telefonos.append(c.getTelefonosArray()[i]);
            if (i < c.getTotalTelefonos() - 1) telefonos.append(", ");
        }
        tvTelefonos.setText("Teléfonos: " + telefonos.toString());
    }

    private void buscarContacto() {
        Intent intent = new Intent(this, BuscarActivity.class);
        startActivity(intent);
    }


    private void anadirContacto() {
        Intent intent = new Intent(this, AgregarContactoActivity.class);
        startActivity(intent);
    }

    private void eliminarContacto() {
        Contacto c = navegador.actual();
        gestorContactos.eliminarContacto(c.getId());
        Toast.makeText(this, "Contacto eliminado: " + c.getAtributos().getOrDefault("nombre", ""), Toast.LENGTH_SHORT).show();

        // Recargar lista y navegador
        Contacto[] contactosArray = gestorContactos.getTodos();
        if (contactosArray.length == 0) {
            tvNombreCompleto.setText("");
            tvTipo.setText("");
            tvTelefonos.setText("");
            Toast.makeText(this, "No hay más contactos.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Contacto> contactosOrdenados = Arrays.stream(contactosArray)
                .sorted(Comparator.comparing(
                        cont -> (cont.getAtributos().getOrDefault("nombre", "") + " " + cont.getAtributos().getOrDefault("apellido", ""))
                                .toLowerCase()
                ))
                .collect(Collectors.toList());

        navegador = new NavegadorContactos(contactosOrdenados);
        mostrarContacto(navegador.actual());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recarga para reflejar cambios si se agregó contacto nuevo
        Contacto[] contactosArray = gestorContactos.getTodos();
        if (contactosArray.length == 0) return;

        List<Contacto> contactosOrdenados = Arrays.stream(contactosArray)
                .sorted(Comparator.comparing(
                        c -> (c.getAtributos().getOrDefault("nombre", "") + " " + c.getAtributos().getOrDefault("apellido", ""))
                                .toLowerCase()
                ))
                .collect(Collectors.toList());

        if (navegador == null) {
            navegador = new NavegadorContactos(contactosOrdenados);
        }
    }


}






