package com.primeraappf.agendacontactos;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class BuscarActivity extends AppCompatActivity {
    private GestorContactos gestorContactos;
    private ContactoAdapter adapter;
    private TextView tvCriterios;
    private CheckBox cbApellidoNombre, cbCantidadAtributos, cbCumpleanos, cbTipo, cbEmpresa, cbProvincia;
    private RecyclerView recyclerBusqueda;
    private Contacto[] listaFiltrada;
    private final Set<Comparator<Contacto>> comparadoresActivos = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        gestorContactos = GestorContactos.getInstance(this);
        tvCriterios = findViewById(R.id.tvCriterios);
        inicializarVistas();
        setListeners();
        actualizarLista();
    }

    private void inicializarVistas() {
        cbApellidoNombre = findViewById(R.id.cbApellidoNombre);
        cbCantidadAtributos = findViewById(R.id.cbCantidadAtributos);
        cbCumpleanos = findViewById(R.id.cbCumpleanos);
        cbTipo = findViewById(R.id.cbTipo);
        cbEmpresa = findViewById(R.id.cbEmpresa);
        cbProvincia = findViewById(R.id.cbProvincia);
        recyclerBusqueda = findViewById(R.id.recyclerBusqueda);
        recyclerBusqueda.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setListeners() {
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            actualizarComparadores();
            actualizarLista();
        };
        cbApellidoNombre.setOnCheckedChangeListener(listener);
        cbCantidadAtributos.setOnCheckedChangeListener(listener);
        cbCumpleanos.setOnCheckedChangeListener(listener);
        cbTipo.setOnCheckedChangeListener(listener);
        cbEmpresa.setOnCheckedChangeListener(listener);
        cbProvincia.setOnCheckedChangeListener(listener);
    }

    private void actualizarComparadores() {
        comparadoresActivos.clear();
        if (cbApellidoNombre.isChecked()) comparadoresActivos.add(ComparadoresContacto.porApellidoNombre);
        if (cbCantidadAtributos.isChecked()) comparadoresActivos.add(ComparadoresContacto.porCantidadAtributos);
        if (cbCumpleanos.isChecked()) comparadoresActivos.add(ComparadoresContacto.porCumpleanos);
        if (cbTipo.isChecked()) comparadoresActivos.add(ComparadoresContacto.porTipo);
        if (cbEmpresa.isChecked()) comparadoresActivos.add(ComparadoresContacto.porEmpresa);
        if (cbProvincia.isChecked()) comparadoresActivos.add(ComparadoresContacto.porProvincia);
        actualizarTextoCriterios();
    }

    private void actualizarTextoCriterios() {
        StringBuilder sb = new StringBuilder("Ordenando por: ");
        for (Comparator<Contacto> comp : comparadoresActivos) {
            if (comp == ComparadoresContacto.porApellidoNombre) sb.append("Apellido/Nombre, ");
            else if (comp == ComparadoresContacto.porCantidadAtributos) sb.append("Cant. Atributos, ");
        }
        tvCriterios.setText(sb.length() > 15 ? sb.substring(0, sb.length()-2) : "Sin criterios activos");
    }

    private void actualizarLista() {
        Contacto[] todos = gestorContactos.getTodos();
        listaFiltrada = Arrays.copyOf(todos, todos.length);

        if (!comparadoresActivos.isEmpty()) {
            Comparator<Contacto> compuesto = comparadoresActivos.stream()
                    .reduce(Comparator::thenComparing)
                    .orElse((c1, c2) -> 0);
            Arrays.sort(listaFiltrada, compuesto);
            logContactosOrdenados();
        }

        adapter = new ContactoAdapter(Arrays.asList(listaFiltrada), contacto -> {});
        recyclerBusqueda.setAdapter(adapter);
    }

    private void logContactosOrdenados() {
        Log.d("ORDENAMIENTO", "--- Contactos ordenados ---");
        for (Contacto c : listaFiltrada) {
            Log.d("CONTACTO", String.format("%s %s (%s) - %s atributos",
                    c.getAtributos().getOrDefault("nombre", "?"),
                    c.getAtributos().getOrDefault("apellido", "?"),
                    c.getTipo(),
                    c.getAtributos().size()));
        }
    }
}