package com.dantv.listadetarefas;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText edtTxt;
    private Button btnAdd;
    private ListView listView;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            //recover screen elements
            edtTxt = (EditText) findViewById(R.id.edTxtId);
            btnAdd = (Button) findViewById(R.id.btnAddId);

            //list
            listView = (ListView) findViewById(R.id.listViweId);

            //create DB
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //create table
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String textoDigitado = edtTxt.getText().toString();
                    salvarTarefa(textoDigitado);
                }
            });

            //delete with a long click
//            listView.setLongClickable(true);
//            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
//                    removerTarefa(ids.get(position));
//                    return false;
//                }
//            });


            //delete with a menu
            listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                                ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.add(0, 1, 0, "deletar");
                }
            });
            //list tasks
            recuperarTarefas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //delete with a menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        switch (item.getItemId()) {
            case 1:
                removerTarefa(ids.get(position));
        }
        return super.onContextItemSelected(item);
    }

    private void salvarTarefa(String texto) {

        try {
            if (texto.equals("")) {
                Toast.makeText(MainActivity.this, "Digite uma tarefa.", Toast.LENGTH_SHORT).show();
            } else {
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + texto + "') ");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso.", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                edtTxt.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recuperarTarefas() {
        try {
            //retrieve the tasks
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //retrieve columns IDS
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //create the adapter
            ids = new ArrayList<Integer>();
            itens = new ArrayList<String>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    itens);
            listView.setAdapter(itensAdaptador);

            //list tasks
            cursor.moveToFirst();
            while (cursor != null) {
                Log.i("Resultado - ", "Tarefa: " + cursor.getString(indiceColunaTarefa));
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removerTarefa(Integer id) {

        try {
            bancoDados.execSQL("DELETE FROM tarefas WHERE id =" + id);
            recuperarTarefas();
            Toast.makeText(MainActivity.this, "Tarefa removida com sucesso.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
