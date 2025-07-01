package com.primeraappf.agendacontactos;

import android.os.Bundle;
import android.widget.TextView;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleContactoActivity extends AppCompatActivity {

    private GestorContactos gestorContactos;

    private TextView tvNombreDetalle;
    private TextView tvTipoDetalle;
    private TextView tvTelefonosDetalle;
    private TextView tvAtributosDetalle;
    private TextView tvAsociadosDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contacto);

        gestorContactos = GestorContactos.getInstance(this);

        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvTipoDetalle = findViewById(R.id.tvTipoDetalle);
        tvTelefonosDetalle = findViewById(R.id.tvTelefonosDetalle);
        tvAtributosDetalle = findViewById(R.id.tvAtributosDetalle);
        tvAsociadosDetalle = findViewById(R.id.tvAsociadosDetalle);

        String contactoId = getIntent().getStringExtra("contacto_id");
        if (contactoId != null) {
            mostrarDetalleContacto(contactoId);
        }
    }

    private void mostrarDetalleContacto(String id) {
        Contacto c = gestorContactos.getPorId(id);
        if (c == null) return;

        tvNombreDetalle.setText(c.getAtributos().getOrDefault("nombre", "Sin nombre"));
        tvTipoDetalle.setText("Tipo: " + c.getTipo());

        StringBuilder telefonos = new StringBuilder("Teléfonos:\n");
        for (int i = 0; i < c.getTotalTelefonos(); i++) {
            telefonos.append("- ").append(c.getTelefonosArray()[i]).append("\n");
        }
        tvTelefonosDetalle.setText(telefonos.toString());

        StringBuilder atributos = new StringBuilder("Atributos:\n");
        for (Map.Entry<String, String> entry : c.getAtributos().entrySet()) {
            atributos.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        tvAtributosDetalle.setText(atributos.toString());

        StringBuilder asociados = new StringBuilder("Contactos asociados:\n");
        for (int i = 0; i < c.getTotalAsociados(); i++) {
            Contacto asociado = c.getAsociadosArray()[i];
            String nombreAsociado = asociado.getAtributos().getOrDefault("nombre", asociado.getId());
            asociados.append("- ").append(nombreAsociado).append("\n");
        }
        tvAsociadosDetalle.setText(asociados.toString());
    }
}

