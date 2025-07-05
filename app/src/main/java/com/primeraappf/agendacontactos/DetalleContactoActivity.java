package com.primeraappf.agendacontactos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class DetalleContactoActivity extends AppCompatActivity {

    private EditText etNombreDetalle, etApellidoDetalle;
    private TextView tvTipoDetalle, tvTelefonosDetalle, tvAtributosDetalle;
    private LinearLayout layoutTelefonos, layoutAtributos, layoutAsociados;
    private Button btnBorrarContacto, btnGuardarCambios;

    private Contacto contacto;
    private GestorContactos gestor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contacto);

        gestor = GestorContactos.getInstance(this);

        String contactoId = getIntent().getStringExtra("contacto_id");
        contacto = gestor.getPorId(contactoId);

        if (contacto == null) {
            Toast.makeText(this, "Contacto no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etNombreDetalle = findViewById(R.id.etNombreDetalle);
        etApellidoDetalle = findViewById(R.id.etApellidoDetalle);
        tvTipoDetalle = findViewById(R.id.tvTipoDetalle);
        tvTelefonosDetalle = findViewById(R.id.tvTelefonosDetalle);
        layoutTelefonos = findViewById(R.id.layoutTelefonos);
        layoutAtributos = findViewById(R.id.layoutAtributos);
        layoutAsociados = findViewById(R.id.layoutAsociados);
        btnBorrarContacto = findViewById(R.id.btnBorrarContacto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        mostrarContacto();

        btnBorrarContacto.setOnClickListener(v -> {
            gestor.eliminarContacto(contacto.getId());
            gestor.guardarContactos();
            Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void mostrarContacto() {
        String nombre = contacto.getAtributos().getOrDefault("nombre", "");
        String apellido = contacto.getAtributos().getOrDefault("apellido", "");

        etNombreDetalle.setText(nombre);
        etApellidoDetalle.setText(apellido);

        tvTipoDetalle.setText("Tipo: " + contacto.getTipo());

        layoutTelefonos.removeAllViews();
        for (int i = 0; i < contacto.getTotalTelefonos(); i++) {
            EditText etTelefono = new EditText(this);
            etTelefono.setHint("Teléfono " + (i + 1));
            etTelefono.setText(contacto.getTelefonosArray()[i]);
            layoutTelefonos.addView(etTelefono);
        }

        layoutAtributos.removeAllViews();
        for (Map.Entry<String, String> entry : contacto.getAtributos().entrySet()) {
            TextView atributoView = new TextView(this);
            atributoView.setText(entry.getKey() + ": " + entry.getValue());
            layoutAtributos.addView(atributoView);
        }

        layoutAsociados.removeAllViews();
        Contacto[] asociados = contacto.getAsociadosArray();
        int total = contacto.getTotalAsociados();

        for (int i = 0; i < total; i++) {
            Contacto asociado = asociados[i];
            if (asociado == null) continue;

            String nom = asociado.getAtributos().getOrDefault("nombre", "Sin nombre");
            String ape = asociado.getAtributos().getOrDefault("apellido", "");
            String nomCompleto = (nom + " " + ape).trim();

            TextView asociadoView = new TextView(this);
            asociadoView.setText("👤 " + nomCompleto);
            asociadoView.setTextSize(16);
            asociadoView.setPadding(16, 8, 16, 8);

            asociadoView.setOnClickListener(v -> {
                Intent intent = new Intent(DetalleContactoActivity.this, DetalleContactoActivity.class);
                intent.putExtra("contacto_id", asociado.getId());
                startActivity(intent);
            });

            layoutAsociados.addView(asociadoView);
        }
    }

    private void guardarCambios() {
        String nuevoNombre = etNombreDetalle.getText().toString().trim();
        String nuevoApellido = etApellidoDetalle.getText().toString().trim();

        if (TextUtils.isEmpty(nuevoNombre) && TextUtils.isEmpty(nuevoApellido)) {
            Toast.makeText(this, "Debe ingresar al menos un nombre o apellido", Toast.LENGTH_SHORT).show();
            return;
        }

        contacto.addAtributo("nombre", nuevoNombre);
        contacto.addAtributo("apellido", nuevoApellido);

        contacto.getTelefonosArray();

        try {
            java.lang.reflect.Field fieldTotalTel = Contacto.class.getDeclaredField("totalTelefonos");
            fieldTotalTel.setAccessible(true);
            fieldTotalTel.setInt(contacto, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error interno al guardar teléfonos", Toast.LENGTH_SHORT).show();
            return;
        }

        int countTelefonosNuevos = 0;
        for (int i = 0; i < layoutTelefonos.getChildCount(); i++) {
            View child = layoutTelefonos.getChildAt(i);
            if (child instanceof EditText) {
                String tel = ((EditText) child).getText().toString().trim();
                if (!tel.isEmpty()) {
                    contacto.addTelefono(tel);
                    countTelefonosNuevos++;
                }
            }
        }

        if (countTelefonosNuevos == 0) {
            Toast.makeText(this, "Debe ingresar al menos un teléfono", Toast.LENGTH_SHORT).show();
            return;
        }

        gestor.guardarContactos();
        Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();

        mostrarContacto();
    }
}
