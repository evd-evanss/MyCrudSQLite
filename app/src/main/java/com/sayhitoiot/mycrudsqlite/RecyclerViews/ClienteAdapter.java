package com.sayhitoiot.mycrudsqlite.RecyclerViews;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sayhitoiot.mycrudsqlite.R;
import com.sayhitoiot.mycrudsqlite.Utils.Cliente;
import com.sayhitoiot.mycrudsqlite.Utils.ClienteDAO;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteHolder> {

    private final List<Cliente> clientes;

    public ClienteAdapter(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    @Override
    public ClienteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClienteHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista, parent, false));
    }

    @Override
    public void onBindViewHolder(ClienteHolder holder, final int position) {
        holder.nomeCliente.setText(clientes.get(position).getNome());
        holder.btnEditar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity(v);
                Intent intent = activity.getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("cliente", clientes.get(position));
                activity.finish();
                activity.startActivity(intent);
            }
        });

        final Cliente cliente = clientes.get(position);
        holder.btnExcluir.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este cliente?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Cliente cliente = clientes.get(position);
                                ClienteDAO dao = new ClienteDAO(view.getContext());
                                boolean sucesso = dao.excluir(cliente.getId());
                                if(sucesso) {
                                    removerCliente(cliente);
                                    Snackbar.make(view, "Excluiu!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }else{
                                    Snackbar.make(view, "Erro ao excluir o cliente!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientes != null ? clientes.size() : 0;
    }

    public void adicionarCliente(Cliente cliente){
        clientes.add(cliente);
        notifyItemInserted(getItemCount());
    }

    public void atualizarCliente(Cliente cliente){
        clientes.set(clientes.indexOf(cliente), cliente);
        notifyItemChanged(clientes.indexOf(cliente));
    }

    public void removerCliente(Cliente cliente){
        int position = clientes.indexOf(cliente);
        clientes.remove(position);
        notifyItemRemoved(position);
    }

    private Activity getActivity(View view) {
        //este método permite obter uma Activity a partir de uma View qualquer
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}