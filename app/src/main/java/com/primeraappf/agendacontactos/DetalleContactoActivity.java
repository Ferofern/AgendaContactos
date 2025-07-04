package com.primeraappf.agendacontactos;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class DetalleContactoActivity extends AppCompatActivity {

    private GestorContactos gestorContactos;
    private Contacto contacto;

    private TextView tvNombreDetalle;
    private TextView tvTipoDetalle;
    private TextView tvTelefonosDetalle;
    private TextView tvAsociadosDetalle;
    private LinearLayout layoutAtributos;
    private Button btnBorrarContacto;
    private Button btnGuardarCambios;

    private String contactoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contacto);

        gestorContactos = GestorContactos.getInstance(this);

        // Referencias UI
        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvTipoDetalle = findViewById(R.id.tvTipoDetalle);
        tvTelefonosDetalle = findViewById(R.id.tvTelefonosDetalle);
        tvAsociadosDetalle = findViewById(R.id.tvAsociadosDetalle);
        layoutAtributos = findViewById(R.id.layoutAtributos);
        btnBorrarContacto = findViewById(R.id.btnBorrarContacto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        contactoId = getIntent().getStringExtra("contacto_id");

        if (contactoId != null) {
            contacto = gestorContactos.getPorId(contactoId);
            if (contacto != null) {
                mostrarDetalleContacto(contacto);
            }
        }

        btnBorrarContacto.setOnClickListener(v -> {
            gestorContactos.eliminarContacto(contactoId);
            gestorContactos.guardarContactos();
            Toast.makeText(this, "Contacto borrado", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnGuardarCambios.setOnClickListener(v -> {
            Map<String, String> nuevosAtributos = new HashMap<>();

            for (int i = 0; i < layoutAtributos.getChildCount(); i++) {
                LinearLayout fila = (LinearLayout) layoutAtributos.getChildAt(i);
                TextView tvClave = (TextView) fila.getChildAt(0);
                EditText etValor = (EditText) fila.getChildAt(1);

                String clave = tvClave.getText().toString().replace(":", "").trim();
                String valor = etValor.getText().toString().trim();
                nuevosAtributos.put(clave, valor);
            }

            contacto.setAtributos(nuevosAtributos);
            gestorContactos.guardarContactos();
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarDetalleContacto(Contacto c) {
        // Nombre y tipo
        String nombre = c.getAtributos().getOrDefault("nombre", "Sin nombre");
        String apellido = c.getAtributos().getOrDefault("apellido", "");
        tvNombreDetalle.setText(nombre + " " + apellido);
        tvTipoDetalle.setText("Tipo: " + c.getTipo());

        // Teléfonos
        StringBuilder telefonos = new StringBuilder("Teléfonos:\n");
        for (int i = 0; i < c.getTotalTelefonos(); i++) {
            telefonos.append("- ").append(c.getTelefonosArray()[i]).append("\n");
        }
        tvTelefonosDetalle.setText(telefonos.toString());

        // Atributos (editable)
        layoutAtributos.removeAllViews();
        for (Map.Entry<String, String> entry : c.getAtributos().entrySet()) {
            String clave = entry.getKey();
            String valor = entry.getValue();

            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvClave = new TextView(this);
            tvClave.setText(clave + ": ");
            fila.addView(tvClave);

            EditText etValor = new EditText(this);
            etValor.setText(valor);
            etValor.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            fila.addView(etValor);

            Button btnEliminar = new Button(this);
            btnEliminar.setText("🗑");
            btnEliminar.setOnClickListener(v -> {
                contacto.eliminarAtributo(clave);
                layoutAtributos.removeView(fila);
                Toast.makeText(this, "Atributo eliminado", Toast.LENGTH_SHORT).show();
            });
            fila.addView(btnEliminar);

            layoutAtributos.addView(fila);
        }

        // Asociados
        StringBuilder asociados = new StringBuilder("Contactos asociados:\n");
        for (int i = 0; i < c.getTotalAsociados(); i++) {
            Contacto asociado = c.getAsociadosArray()[i];
            String nombreAsociado = asociado.getAtributos().getOrDefault("nombre", asociado.getId());
            asociados.append("- ").append(nombreAsociado).append("\n");
        }
        tvAsociadosDetalle.setText(asociados.toString());
    }
}
