package com.sayhitoiot.mycrudsqlite;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sayhitoiot.mycrudsqlite.RecyclerViews.ClienteAdapter;
import com.sayhitoiot.mycrudsqlite.Utils.Cliente;
import com.sayhitoiot.mycrudsqlite.Utils.ClienteDAO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    EditText txtNome;
    Spinner spnEstado;
    RadioGroup rgSexo;
    CheckBox chkVip;
    RecyclerView recyclerView;
    ClienteAdapter adapterCliente;
    Cliente clienteEditado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeViews();

        //verifica se começou agora ou se veio de uma edição
        Intent intent = getIntent();
        if(intent.hasExtra("cliente")){
            findViewById(R.id.includemain).setVisibility(View.INVISIBLE);
            findViewById(R.id.includecadastro).setVisibility(View.VISIBLE);
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            clienteEditado = (Cliente) intent.getSerializableExtra("cliente");

            txtNome.setText(clienteEditado.getNome());
            chkVip.setChecked(clienteEditado.getVip());
            spnEstado.setSelection(getIndex(spnEstado, clienteEditado.getUf()));
            if(clienteEditado.getSexo() != null){
                RadioButton rb;
                if(clienteEditado.getSexo().equals("M"))
                    rb = (RadioButton)findViewById(R.id.rbMasculino);
                else
                    rb = (RadioButton)findViewById(R.id.rbFeminino);
                rb.setChecked(true);
            }
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.includemain).setVisibility(View.INVISIBLE);
                findViewById(R.id.includecadastro).setVisibility(View.VISIBLE);
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);

            }
        });

        Button btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.includemain).setVisibility(View.VISIBLE);
                findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
            }
        });

        Button btnSalvar = (Button)findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pegando os valores
                String nome = txtNome.getText().toString();
                String uf = spnEstado.getSelectedItem().toString();
                boolean vip = chkVip.isChecked();
                String sexo = rgSexo.getCheckedRadioButtonId() == R.id.rbMasculino ? "M" : "F";

                //salvando os dados
                ClienteDAO dao = new ClienteDAO(getBaseContext());
                boolean sucesso;
                if(clienteEditado != null)
                    sucesso = dao.salvar(clienteEditado.getId(), nome, sexo, uf, vip);
                else
                    sucesso = dao.salvar(nome, sexo, uf, vip);

                if(sucesso) {
                    Cliente cliente = dao.retornarUltimo();
                    if(clienteEditado != null){
                        adapterCliente.atualizarCliente(cliente);
                        clienteEditado = null;
                    }else
                        adapterCliente.adicionarCliente(cliente);

                    //limpa os campos
                    txtNome.setText("");
                    rgSexo.setSelected(false);
                    spnEstado.setSelection(0);
                    chkVip.setChecked(false);

                    Snackbar.make(view, "Salvou!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    findViewById(R.id.includemain).setVisibility(View.VISIBLE);
                    findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
                    findViewById(R.id.fab).setVisibility(View.VISIBLE);
                }else{
                    Snackbar.make(view, "Erro ao salvar, consulte os logs!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });
        configurarRecycler();
    }



    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    private void configurarRecycler() {
        // Configurando o gerenciador de layout para ser uma lista.
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapterCliente que irá anexar os objetos à lista.
        ClienteDAO dao = new ClienteDAO(this);
        adapterCliente = new ClienteAdapter(dao.retornarTodos());
        recyclerView.setAdapter(adapterCliente);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void initializeViews() {
        //carregando os campos
        txtNome = findViewById(R.id.txtNome);
        spnEstado = findViewById(R.id.spnEstado);
        rgSexo = findViewById(R.id.rgSexo);
        chkVip = findViewById(R.id.chkVip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
