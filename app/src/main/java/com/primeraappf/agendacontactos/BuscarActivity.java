package com.primeraappf.agendacontactos;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class BuscarActivity extends AppCompatActivity {

    private GestorContactos gestorContactos;
    private ContactoAdapter adapter;

    private CheckBox cbApellidoNombre, cbCantidadAtributos, cbCumpleanos, cbTipo, cbEmpresa, cbProvincia;
    private RecyclerView recyclerBusqueda;

    private List<Contacto> listaFiltrada;
    private final Set<Comparator<Contacto>> comparadoresActivos = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        gestorContactos = GestorContactos.getInstance(this);

        cbApellidoNombre = findViewById(R.id.cbApellidoNombre);
        cbCantidadAtributos = findViewById(R.id.cbCantidadAtributos);
        cbCumpleanos = findViewById(R.id.cbCumpleanos);
        cbTipo = findViewById(R.id.cbTipo);
        cbEmpresa = findViewById(R.id.cbEmpresa);
        cbProvincia = findViewById(R.id.cbProvincia);

        recyclerBusqueda = findViewById(R.id.recyclerBusqueda);
        recyclerBusqueda.setLayoutManager(new LinearLayoutManager(this));

        setListeners();
        actualizarLista();
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

        if (cbApellidoNombre.isChecked())
            comparadoresActivos.add(ComparadoresContacto.porApellidoNombre);

        if (cbCantidadAtributos.isChecked())
            comparadoresActivos.add(ComparadoresContacto.porCantidadAtributos);

        if (cbCumpleanos.isChecked())
            comparadoresActivos.add(ComparadoresContacto.porCumpleanos);

        if (cbTipo.isChecked())
            comparadoresActivos.add(ComparadoresContacto.porTipo);

        if (cbEmpresa.isChecked())
            comparadoresActivos.add(ComparadoresContacto.porEmpresa);

        if (cbProvincia.isChecked())
            comparadoresActivos.add(ComparadoresContacto.porProvincia);
    }

    private void actualizarLista() {
        Contacto[] todos = gestorContactos.getTodos();
        listaFiltrada = new ArrayList<>(Arrays.asList(todos));

        if (!comparadoresActivos.isEmpty()) {
            // Construir comparador compuesto con thenComparing
            Iterator<Comparator<Contacto>> it = comparadoresActivos.iterator();
            Comparator<Contacto> compuesto = it.next();
            while (it.hasNext()) {
                compuesto = compuesto.thenComparing(it.next());
            }

            listaFiltrada.sort(compuesto);

            // Debug: imprimir nombre y provincia de cada contacto después de ordenar
            for (Contacto c : listaFiltrada) {
                String nombre = c.getAtributos().getOrDefault("nombre", "N/A");
                String provincia = c.getAtributos().getOrDefault("provincia", "N/A");
                Log.d("BuscarActivity", "Contacto: " + nombre + " - Provincia: " + provincia);
            }
        }

        adapter = new ContactoAdapter(listaFiltrada, contacto -> {
            // Aquí puedes agregar acción al hacer clic en un contacto si quieres
        });

        recyclerBusqueda.setAdapter(adapter);
    }
}
