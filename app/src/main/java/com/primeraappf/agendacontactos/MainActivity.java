package com.primeraappf.agendacontactos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private GestorContactos gestorContactos;
    private RecyclerView recyclerContactos;
    private ContactoAdapter adapter;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestorContactos = GestorContactos.getInstance(this);

        recyclerContactos = findViewById(R.id.recyclerContactos);
        recyclerContactos.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigation = findViewById(R.id.bottomNavigation);
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

        if (gestorContactos.getTodos().length == 0) {
            Contacto c1 = gestorContactos.crearContacto("Personal", "0998765432");
            c1.addAtributo("nombre", "Felix Romero");
            Contacto c2 = gestorContactos.crearContacto("Trabajo", "0987654321");
            c2.addAtributo("nombre", "Ana Perez");
            gestorContactos.guardarContactos();
        }

        actualizarLista();
    }

    private void actualizarLista() {
        Contacto[] listaContactos = gestorContactos.getTodos();
        adapter = new ContactoAdapter(Arrays.asList(listaContactos), contacto -> {
            Intent intent = new Intent(MainActivity.this, DetalleContactoActivity.class);
            intent.putExtra("contacto_id", contacto.getId());
            startActivity(intent);
        });
        recyclerContactos.setAdapter(adapter);
    }

    private void buscarContacto() {
        // Implementa buscar contacto si quieres
    }

    private void anadirContacto() {
        Intent intent = new Intent(this, AgregarContactoActivity.class);
        startActivity(intent);
    }

    private void eliminarContacto() {
        // Implementa eliminar contacto si quieres
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarga y refresca la lista para mostrar contactos añadidos/eliminados
        actualizarLista();
    }
}




