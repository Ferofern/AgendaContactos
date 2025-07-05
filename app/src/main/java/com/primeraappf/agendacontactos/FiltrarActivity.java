package com.primeraappf.agendacontactos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Vector;

public class FiltrarActivity extends AppCompatActivity {

    private RadioGroup rgFiltros;
    private EditText etFiltroTexto;
    private Button btnAplicarFiltro;
    private RecyclerView recyclerResultados;

    private GestorContactos gestorContactos;
    private ContactoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar);

        gestorContactos = GestorContactos.getInstance(this);

        rgFiltros = findViewById(R.id.rgFiltros);
        etFiltroTexto = findViewById(R.id.etFiltroTexto);
        btnAplicarFiltro = findViewById(R.id.btnAplicarFiltro);
        recyclerResultados = findViewById(R.id.recyclerResultados);
        recyclerResultados.setLayoutManager(new LinearLayoutManager(this));

        rgFiltros.setOnCheckedChangeListener((group, checkedId) -> {
            etFiltroTexto.setText("");
            etFiltroTexto.setVisibility(View.VISIBLE);
            etFiltroTexto.setInputType(android.text.InputType.TYPE_CLASS_TEXT);

            if (checkedId == R.id.rbNombre) {
                etFiltroTexto.setHint("Ingrese nombre o apellido");
            } else if (checkedId == R.id.rbCantidadAtributos) {
                etFiltroTexto.setHint("Ingrese mínimo cantidad de atributos (ej: 3)");
                etFiltroTexto.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            } else if (checkedId == R.id.rbCumpleanos) {
                etFiltroTexto.setHint("Ingrese mes (1-12) para cumpleaños");
                etFiltroTexto.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            } else if (checkedId == R.id.rbEmpresa) {
                etFiltroTexto.setHint("Ingrese nombre de empresa");
            } else if (checkedId == R.id.rbCiudad) {
                etFiltroTexto.setHint("Ingrese ciudad");
            } else if (checkedId == R.id.rbTipo) {
                etFiltroTexto.setHint("Ingrese tipo (Persona natural / Empresa)");
            } else {
                etFiltroTexto.setVisibility(View.GONE);
            }
        });

        btnAplicarFiltro.setOnClickListener(v -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        int idSeleccionado = rgFiltros.getCheckedRadioButtonId();
        if (idSeleccionado == -1) {
            Toast.makeText(this, "Seleccione un filtro", Toast.LENGTH_SHORT).show();
            return;
        }
        String textoFiltro = etFiltroTexto.getText().toString().trim();
        if (TextUtils.isEmpty(textoFiltro)) {
            Toast.makeText(this, "Ingrese el valor para filtrar", Toast.LENGTH_SHORT).show();
            return;
        }

        Contacto[] todos = gestorContactos.getTodos();
        Vector<Contacto> filtrados = new Vector<>();

        if (idSeleccionado == R.id.rbNombre) {
            String filtroNombre = textoFiltro.toLowerCase(Locale.ROOT);
            for (Contacto c : todos) {
                String nombre = c.getAtributos().getOrDefault("nombre", "").toLowerCase(Locale.ROOT);
                String apellido = c.getAtributos().getOrDefault("apellido", "").toLowerCase(Locale.ROOT);
                if (nombre.contains(filtroNombre) || apellido.contains(filtroNombre)) {
                    filtrados.add(c);
                }
            }
        } else if (idSeleccionado == R.id.rbCantidadAtributos) {
            int minAtributos;
            try {
                minAtributos = Integer.parseInt(textoFiltro);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
                return;
            }
            for (Contacto c : todos) {
                if (c.getAtributos().size() >= minAtributos) {
                    filtrados.add(c);
                }
            }
        } else if (idSeleccionado == R.id.rbCumpleanos) {
            int mes;
            try {
                mes = Integer.parseInt(textoFiltro);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Mes inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mes < 1 || mes > 12) {
                Toast.makeText(this, "Mes debe ser entre 1 y 12", Toast.LENGTH_SHORT).show();
                return;
            }
            for (Contacto c : todos) {
                String cumple = c.getAtributos().getOrDefault("cumpleaños", "");
                if (!cumple.isEmpty()) {
                    try {
                        String[] partes = cumple.split("-");
                        int mesCumple = Integer.parseInt(partes[1]);
                        if (mesCumple == mes) {
                            filtrados.add(c);
                        }
                    } catch (Exception ignored) {}
                }
            }
        } else if (idSeleccionado == R.id.rbEmpresa) {
            String filtroEmpresa = textoFiltro.toLowerCase(Locale.ROOT);
            for (Contacto c : todos) {
                String empresa = c.getAtributos().getOrDefault("empresa", "").toLowerCase(Locale.ROOT);
                if (empresa.contains(filtroEmpresa)) {
                    filtrados.add(c);
                }
            }
        } else if (idSeleccionado == R.id.rbCiudad) {
            String filtroCiudad = textoFiltro.toLowerCase(Locale.ROOT);
            for (Contacto c : todos) {
                String ciudad = c.getAtributos().getOrDefault("ciudad", "").toLowerCase(Locale.ROOT);
                if (ciudad.contains(filtroCiudad)) {
                    filtrados.add(c);
                }
            }
        } else if (idSeleccionado == R.id.rbTipo) {
            String filtroTipo = textoFiltro.toLowerCase(Locale.ROOT);
            for (Contacto c : todos) {
                String tipo = c.getTipo().toLowerCase(Locale.ROOT);
                if (tipo.contains(filtroTipo)) {
                    filtrados.add(c);
                }
            }
        }

        if (filtrados.isEmpty()) {
            Toast.makeText(this, "No se encontraron contactos con ese filtro", Toast.LENGTH_LONG).show();
        }

        adapter = new ContactoAdapter(filtrados, contacto -> {
            Intent intent = new Intent(FiltrarActivity.this, DetalleContactoActivity.class);
            intent.putExtra("contacto_id", contacto.getId());
            startActivity(intent);
        });

        recyclerResultados.setAdapter(adapter);
    }
}
