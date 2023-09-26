package br.edu.utfpr.projeto_android_aplicado

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tipoSpinner: Spinner
    private lateinit var detalheSpinner: Spinner
    private lateinit var valorEditText: EditText
    private lateinit var dataEditText: EditText

    private lateinit var databaseHelper: DatabaseHelper

    private val tipos = arrayOf("Crédito", "Débito")
    private val detalhesCredito = arrayOf("Salário", "Extras")
    private val detalhesDebito = arrayOf("Alimentação", "Transporte", "Saúde", "Moradia")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tipoSpinner = findViewById(R.id.sp_tipo)
        detalheSpinner = findViewById(R.id.sp_detalhe)
        valorEditText = findViewById(R.id.et_valor)
        dataEditText = findViewById(R.id.et_data)

        databaseHelper = DatabaseHelper(this)

        // Configurar o adapter para o spinner de tipo (Crédito/Débito)
        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoSpinner.adapter = tipoAdapter

        // Configurar o adapter para o spinner de detalhe com as opções iniciais (Salário/Extras)
        val detalheAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, detalhesCredito)
        detalheAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        detalheSpinner.adapter = detalheAdapter

        // Configuração do Spinner tipo
        tipoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // Atualiza o Spinner de Detalhe conforme o tipo
                val selectedTipo = tipos[position]
                val newDetalhes = if (selectedTipo == "Crédito") detalhesCredito else detalhesDebito
                val newDetalheAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, newDetalhes)
                newDetalheAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                detalheSpinner.adapter = newDetalheAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        tipoSpinner = findViewById(R.id.sp_tipo)
        detalheSpinner = findViewById(R.id.sp_detalhe)
        valorEditText = findViewById(R.id.et_valor)
        dataEditText = findViewById(R.id.et_data)

        databaseHelper = DatabaseHelper(this)

        val lancarButton = findViewById<Button>(R.id.bt_lancar)
        lancarButton.setOnClickListener { inserirRegistro() }

        val verLancamentoButton = findViewById<Button>(R.id.bt_ver_lancamento)
        verLancamentoButton.setOnClickListener {
            exibirRegistros()
        }

        val saldoButton = findViewById<Button>(R.id.bt_saldo)
        saldoButton.setOnClickListener {
            calcularSaldo()
        }
    }
    class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "fluxo_caixa.db"
            private const val DATABASE_VERSION = 1
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE lancamentos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "tipo TEXT, " +
                        "detalhe TEXT, " +
                        "valor REAL, " +
                        "dataLancamento TEXT)"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // Você pode adicionar lógica de atualização aqui se necessário
        }
    }
    private fun inserirRegistro() {
        val tipo = tipoSpinner.selectedItem.toString()
        val detalhe = detalheSpinner.selectedItem.toString()
        val valorText = valorEditText.text.toString()
        val data = dataEditText.text.toString()

        if (tipo.isNotEmpty() && detalhe.isNotEmpty() && valorText.isNotEmpty() && data.isNotEmpty()) {
            val valor = valorText.toDouble()

            // Inserir o registro no banco de dados
            val db = databaseHelper.writableDatabase
            val values = ContentValues().apply {
                put("tipo", tipo)
                put("detalhe", detalhe)
                put("valor", valor)
                put("dataLancamento", data)
            }

            val newRowId = db.insert("lancamentos", null, values)
            if (newRowId != -1L) {
                // Registro inserido com sucesso
                Toast.makeText(this, "Registro inserido com sucesso", Toast.LENGTH_SHORT).show()
                limparCampos()
            } else {
                // Erro ao inserir o registro
                Toast.makeText(this, "Erro ao inserir o registro", Toast.LENGTH_SHORT).show()
            }

            db.close()
        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exibirRegistros() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            "lancamentos",
            arrayOf("tipo", "detalhe", "valor", "dataLancamento"),
            null,
            null,
            null,
            null,
            null
        )

        val registros = mutableListOf<String>()

        while (cursor.moveToNext()) {
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
            val data = cursor.getString(cursor.getColumnIndexOrThrow("dataLancamento"))
            val detalhe = cursor.getString(cursor.getColumnIndexOrThrow("detalhe"))
            val valor = cursor.getDouble(cursor.getColumnIndexOrThrow("valor"))


            val registro = "Tipo: $tipo, Detalhe: $detalhe, Valor: $valor, Data: $data"
            registros.add(registro)
        }

        cursor.close()
        db.close()

        if (registros.isNotEmpty()) {
            val registrosArray = registros.toTypedArray()
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, registrosArray)

            val listaRegistros = ListView(this)
            listaRegistros.adapter = adapter

            val intent = Intent(this, RegistrosActivity::class.java)
            intent.putExtra("registros", registrosArray)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Nenhum registro encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limparCampos() {
        // Limpar os campos após inserir um registro
        valorEditText.text.clear()
        dataEditText.text.clear()
    }

    private fun calcularSaldo() {
        val db = databaseHelper.readableDatabase

        // Calcular o saldo de crédito
        val cursorCredito = db.rawQuery("SELECT SUM(valor) FROM lancamentos WHERE tipo = 'Crédito'", null)
        val saldoCredito = if (cursorCredito.moveToFirst()) cursorCredito.getDouble(0) else 0.0
        cursorCredito.close()

        // Calcular o saldo de débito
        val cursorDebito = db.rawQuery("SELECT SUM(valor) FROM lancamentos WHERE tipo = 'Débito'", null)
        val saldoDebito = if (cursorDebito.moveToFirst()) cursorDebito.getDouble(0) else 0.0
        cursorDebito.close()

        db.close()

        val saldoTotal = saldoCredito - saldoDebito

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Saldo")
            .setMessage("Saldo Total: $saldoTotal")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()

        alertDialog.show()
    }

}
