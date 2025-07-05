package com.primeraappf.agendacontactos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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

public class AgregarContactoActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etCorreo, etCumpleanos;
    private Button btnAgregarTelefono;
    private Spinner spinnerCiudad;
    private LinearLayout layoutTelefonos;
    private Button btnGuardar;

    private GestorContactos gestorContactos;

    private String fechaCumpleanosGuardar = "";

    private Spinner spinnerTipo;

    private final String[] fotosPorDefectoPersona = {"foto_persona1.jpg", "foto_persona2.jpg"};
    private final String[] fotosPorDefectoEmpresa = {"foto_empresa1.jpg", "foto_empresa2.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        btnAgregarTelefono = findViewById(R.id.btnAgregarTelefono);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etCumpleanos = findViewById(R.id.etCumpleanos);
        spinnerCiudad = findViewById(R.id.spinnerCiudad);
        layoutTelefonos = findViewById(R.id.layoutTelefonos);
        btnGuardar = findViewById(R.id.btnGuardar);
        spinnerTipo = findViewById(R.id.spinnerTipo);

        String[] tipos = {"Persona", "Empresa"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        String[] ciudades = {
                "Azuay", "Bolívar", "Cañar", "Carchi", "Chimborazo", "Cotopaxi", "El Oro",
                "Esmeraldas", "Galápagos", "Guayas", "Imbabura", "Loja", "Los Ríos",
                "Manabí", "Morona Santiago", "Napo", "Orellana", "Pastaza", "Pichincha",
                "Santa Elena", "Santo Domingo de los Tsáchilas", "Sucumbíos", "Tungurahua",
                "Zamora Chinchipe"
        };

        ArrayAdapter<String> adapterProvincias = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ciudades
        );
        adapterProvincias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCiudad.setAdapter(adapterProvincias);

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipoSeleccionado = (String) parent.getItemAtPosition(position);
                if (tipoSeleccionado.equalsIgnoreCase("Persona")) {
                    etNombre.setVisibility(View.VISIBLE);
                    etApellido.setVisibility(View.VISIBLE);
                    etCumpleanos.setVisibility(View.VISIBLE);
                    etCorreo.setVisibility(View.VISIBLE);
                    spinnerCiudad.setVisibility(View.VISIBLE);
                } else if (tipoSeleccionado.equalsIgnoreCase("Empresa")) {
                    etNombre.setVisibility(View.GONE);
                    etApellido.setVisibility(View.GONE);
                    etCumpleanos.setVisibility(View.GONE);
                    etCorreo.setVisibility(View.GONE);
                    spinnerCiudad.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        gestorContactos = GestorContactos.getInstance(this);

        agregarCajaTelefono();

        etCumpleanos.setInputType(InputType.TYPE_NULL);
        etCumpleanos.setOnClickListener(v -> mostrarDatePicker());
        etCumpleanos.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) mostrarDatePicker();
        });

        btnGuardar.setOnClickListener(v -> {
            String tipoSeleccionado = spinnerTipo.getSelectedItem().toString();

            if (layoutTelefonos.getChildCount() == 0) {
                Toast.makeText(this, "Debe ingresar al menos un teléfono", Toast.LENGTH_SHORT).show();
                return;
            }
            EditText primerTelefonoET = (EditText) layoutTelefonos.getChildAt(0);
            String telefonoPrincipal = primerTelefonoET.getText().toString().trim();
            if (telefonoPrincipal.isEmpty()) {
                Toast.makeText(this, "El teléfono principal no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            Contacto nuevoContacto;
            try {
                nuevoContacto = gestorContactos.crearContacto(tipoSeleccionado, telefonoPrincipal);
            } catch (Exception e) {
                Toast.makeText(this, "Error al crear contacto: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            for (int i = 1; i < layoutTelefonos.getChildCount(); i++) {
                EditText etTelefono = (EditText) layoutTelefonos.getChildAt(i);
                String tel = etTelefono.getText().toString().trim();
                if (!tel.isEmpty()) {
                    nuevoContacto.addTelefono(tel);
                }
            }

            if (tipoSeleccionado.equalsIgnoreCase("Persona")) {
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String cumpleanos = etCumpleanos.getText().toString().trim();
                String ciudad = spinnerCiudad.getSelectedItem() != null ? spinnerCiudad.getSelectedItem().toString() : "";

                if (!nombre.isEmpty()) nuevoContacto.addAtributo("nombre", nombre);
                if (!apellido.isEmpty()) nuevoContacto.addAtributo("apellido", apellido);
                if (!correo.isEmpty()) nuevoContacto.addAtributo("correo", correo);
                if (!cumpleanos.isEmpty()) nuevoContacto.addAtributo("cumpleaños", cumpleanos);
                if (!ciudad.isEmpty()) nuevoContacto.addAtributo("ciudad", ciudad);

                for (String foto : fotosPorDefectoPersona) {
                    nuevoContacto.addFoto(foto);
                }

            } else if (tipoSeleccionado.equalsIgnoreCase("Empresa")) {
                String nombreEmpresa = etNombre.getText().toString().trim();
                if (!nombreEmpresa.isEmpty()) nuevoContacto.addAtributo("empresa", nombreEmpresa);

                for (String foto : fotosPorDefectoEmpresa) {
                    nuevoContacto.addFoto(foto);
                }
            }

            gestorContactos.guardarContactos();

            Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();

            finish();
        });

        btnAgregarTelefono.setOnClickListener(v -> {
            EditText nuevoTelefono = new EditText(this);
            nuevoTelefono.setHint("Teléfono");
            nuevoTelefono.setInputType(android.text.InputType.TYPE_CLASS_PHONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = 8; // espacio arriba
            nuevoTelefono.setLayoutParams(params);

            layoutTelefonos.addView(nuevoTelefono);
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
}
