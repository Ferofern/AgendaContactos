package com.primeraappf.agendacontactos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    public interface OnContactoClickListener {
        void onContactoClick(Contacto contacto);
    }

    private List<Contacto> listaContactos;
    private OnContactoClickListener listener;

    public ContactoAdapter(List<Contacto> listaContactos, OnContactoClickListener listener) {
        this.listaContactos = listaContactos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = listaContactos.get(position);
        String nombre = contacto.getAtributos().getOrDefault("nombre", "Sin nombre");
        holder.tvNombre.setText(nombre);
        holder.tvTipo.setText(contacto.getTipo());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactoClick(contacto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTipo;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvTipo = itemView.findViewById(R.id.tvTipo);
        }
    }
}
