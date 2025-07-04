package com.primeraappf.agendacontactos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

public class AgregarContactoActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etCorreo, etCumpleanos;
    private Spinner spinnerCiudad;
    private LinearLayout layoutTelefonos;
    private Button btnGuardar;

    private GestorContactos gestorContactos;

    private String fechaCumpleanosGuardar = "";

    private Spinner spinnerTipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etCumpleanos = findViewById(R.id.etCumpleanos);
        spinnerCiudad = findViewById(R.id.spinnerCiudad);
        layoutTelefonos = findViewById(R.id.layoutTelefonos);
        btnGuardar = findViewById(R.id.btnGuardar);
        spinnerTipo = findViewById(R.id.spinnerTipo);

        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this, R.array.tipos_contacto, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        String[] provincias = {
                "Azuay", "Bolívar", "Cañar", "Carchi", "Chimborazo", "Cotopaxi", "El Oro",
                "Esmeraldas", "Galápagos", "Guayas", "Imbabura", "Loja", "Los Ríos",
                "Manabí", "Morona Santiago", "Napo", "Orellana", "Pastaza", "Pichincha",
                "Santa Elena", "Santo Domingo de los Tsáchilas", "Sucumbíos", "Tungurahua",
                "Zamora Chinchipe"
        };

        ArrayAdapter<String> adapterProvincias = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                provincias
        );
        adapterProvincias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCiudad.setAdapter(adapterProvincias);

        gestorContactos = GestorContactos.getInstance(this);

        agregarCajaTelefono();

        etCumpleanos.setInputType(InputType.TYPE_NULL);
        etCumpleanos.setOnClickListener(v -> mostrarDatePicker());
        etCumpleanos.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) mostrarDatePicker();
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String provinciaSeleccionada = spinnerCiudad.getSelectedItem().toString();
            String tipoSeleccionado = spinnerTipo.getSelectedItem().toString();

            String[] telefonos = obtenerTelefonos();

            if (nombre.isEmpty() || apellido.isEmpty() || telefonos.length == 0) {
                Toast.makeText(this, "Por favor completa nombre, apellido y al menos un teléfono.", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                Contacto nuevo = gestorContactos.crearContacto(tipoSeleccionado, telefonos[0]);
                for (int i = 1; i < telefonos.length; i++) {
                    nuevo.addTelefono(telefonos[i]);
                }

                nuevo.addAtributo("nombre", nombre);
                nuevo.addAtributo("apellido", apellido);
                nuevo.addAtributo("correo", correo);
                nuevo.addAtributo("provincia", provinciaSeleccionada);  // CORRECCIÓN AQUÍ
                if (!fechaCumpleanosGuardar.isEmpty()) {
                    nuevo.addAtributo("cumpleaños", fechaCumpleanosGuardar);
                }

                gestorContactos.guardarContactos();

                Toast.makeText(this, "Contacto añadido correctamente", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Error al crear el contacto: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(year, month, dayOfMonth);

            SimpleDateFormat formatoVisible = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etCumpleanos.setText(formatoVisible.format(fechaSeleccionada.getTime()));

            SimpleDateFormat formatoGuardar = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaCumpleanosGuardar = formatoGuardar.format(fechaSeleccionada.getTime());

        }, año, mes, dia);
        picker.show();
    }

    private void agregarCajaTelefono() {
        EditText nuevoTelefono = new EditText(this);
        nuevoTelefono.setHint("Teléfono");
        nuevoTelefono.setInputType(InputType.TYPE_CLASS_PHONE);
        nuevoTelefono.setSingleLine(true);
        layoutTelefonos.addView(nuevoTelefono);

        nuevoTelefono.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    if (layoutTelefonos.getChildAt(layoutTelefonos.getChildCount() - 1) == nuevoTelefono) {
                        agregarCajaTelefono();
                    }
                }
            }
        });
    }

    private String[] obtenerTelefonos() {
        Vector<String> telefonos = new Vector<>();
        for (int i = 0; i < layoutTelefonos.getChildCount(); i++) {
            View v = layoutTelefonos.getChildAt(i);
            if (v instanceof EditText) {
                String tel = ((EditText) v).getText().toString().trim();
                if (!tel.isEmpty()) {
                    telefonos.add(tel);
                }
            }
        }
        return telefonos.toArray(new String[0]);
    }
}


