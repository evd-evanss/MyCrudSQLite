package com.sayhitoiot.mycrudsqlite.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    //Classe responsável por fazer a tradução dos objetos para o banco de dados

    private final String TABLE_CLIENTES = "Clientes";
    private DbGateway gateway;

    public ClienteDAO(Context ctx){
        gateway = DbGateway.getInstance(ctx);
    }

    //CREATE
    public boolean salvar(String nome, String sexo, String uf, boolean vip){
        return salvar(0, nome, sexo, uf, vip);
    }

    public boolean salvar(int id, String nome, String sexo, String uf, boolean vip){
        ContentValues cv = new ContentValues();
        cv.put("Nome", nome);
        cv.put("Sexo", sexo);
        cv.put("UF", uf);
        cv.put("Vip", vip ? 1 : 0);
        if(id > 0)
            return gateway.getDatabase().update(TABLE_CLIENTES, cv, "ID=?", new String[]{ id + "" }) > 0;
        else
            return gateway.getDatabase().insert(TABLE_CLIENTES, null, cv) > 0;
    }
    //READ
    public List<Cliente> retornarTodos(){
        List<Cliente> clientes = new ArrayList<>();
        try {
            Cursor cursor = gateway.getDatabase().rawQuery("SELECT * FROM Clientes", null);
            while(cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String nome = cursor.getString(cursor.getColumnIndex("Nome"));
                String sexo = cursor.getString(cursor.getColumnIndex("Sexo"));
                String uf = cursor.getString(cursor.getColumnIndex("UF"));
                boolean vip = cursor.getInt(cursor.getColumnIndex("Vip")) > 0;
                clientes.add(new Cliente(id, nome, sexo, uf, vip));
            }
            cursor.close();
        }catch (Exception e){

        }
        return clientes;
    }
    //READ
    public Cliente retornarUltimo(){
        try {
            Cursor cursor = gateway.getDatabase().rawQuery("SELECT * FROM Clientes ORDER BY ID DESC", null);
            if(cursor.moveToFirst()){
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String nome = cursor.getString(cursor.getColumnIndex("Nome"));
                String sexo = cursor.getString(cursor.getColumnIndex("Sexo"));
                String uf = cursor.getString(cursor.getColumnIndex("UF"));
                boolean vip = cursor.getInt(cursor.getColumnIndex("Vip")) > 0;
                cursor.close();
                return new Cliente(id, nome, sexo, uf, vip);
            }
        }catch (Exception e){

        }
        return null;
    }
    //DELETE
    public boolean excluir(int id){
        return gateway.getDatabase().delete(TABLE_CLIENTES, "ID=?", new String[]{ id + "" }) > 0;
    }


}
