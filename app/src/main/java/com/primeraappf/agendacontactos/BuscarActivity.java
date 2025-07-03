package com.primeraappf.agendacontactos;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
        actualizarLista(); // Mostrar todos al principio
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
            Comparator<Contacto> compuesto = comparadoresActivos.iterator().next();
            for (Comparator<Contacto> c : comparadoresActivos) {
                if (c != compuesto) {
                    compuesto = compuesto.thenComparing(c);
                }
            }
            listaFiltrada.sort(compuesto);
        }

        adapter = new ContactoAdapter(listaFiltrada, contacto -> {
            // Si deseas abrir el detalle desde aquí:
            // Intent intent = new Intent(this, DetalleContactoActivity.class);
            // intent.putExtra("contacto_id", contacto.getId());
            // startActivity(intent);
        });

        recyclerBusqueda.setAdapter(adapter);
    }
}

