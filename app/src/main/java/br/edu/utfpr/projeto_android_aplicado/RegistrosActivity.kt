package br.edu.utfpr.projeto_android_aplicado

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        // Recupere a lista de registros (você deve passá-la para esta atividade)
        val registros = intent.getStringArrayExtra("registros")

        if (registros != null && registros.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, registros)
            val listView = findViewById<ListView>(R.id.list_view)
            listView.adapter = adapter
        } else {
            Toast.makeText(this, "Nenhum registro encontrado", Toast.LENGTH_SHORT).show()
        }
    }
}
