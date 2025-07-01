package com.primeraappf.agendacontactos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarContactoActivity extends AppCompatActivity {

    private EditText etTipo, etNombre, etTelefono;
    private Button btnGuardar;
    private GestorContactos gestorContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        etTipo = findViewById(R.id.etTipo);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        btnGuardar = findViewById(R.id.btnGuardar);

        gestorContactos = GestorContactos.getInstance(this);

        btnGuardar.setOnClickListener(v -> {
            String tipo = etTipo.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            if (tipo.isEmpty() || nombre.isEmpty() || telefono.length() < 2) {
                Toast.makeText(this, "Por favor, completa todos los campos y asegúrate que el teléfono tenga al menos 2 dígitos.", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                Contacto nuevo = gestorContactos.crearContacto(tipo, telefono);
                nuevo.addAtributo("nombre", nombre);

                gestorContactos.guardarContactos();

                Toast.makeText(this, "Contacto añadido correctamente", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Error al crear el contacto: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

