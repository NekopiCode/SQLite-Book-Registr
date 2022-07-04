package and05.lektion3.stichwortregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val helper = StichwortRegisterHelper(this)
        val db = helper.writableDatabase
       db.close()

        if (aktuelleQuelle.isBlank()) {
            val intent = Intent(this, activity_source::class.java)
            startActivityForResult(intent, 0)
        }
    }

    fun onButtonQuelleErfassenClick (view: View?) {
        val intent = Intent(this, activity_source::class.java)
        startActivityForResult(intent, 0)
        deleteText()


    }


    var aktuelleQuelle : String = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val extras = data?.extras
        if (extras != null) {
            val quelle = extras.getString(activity_source.QUELLEN_ID)
            val textView = findViewById<TextView>(R.id.textview_aktuelle_quelle)
            aktuelleQuelle = if (quelle != null) quelle else ""
            textView.text = getString(R.string.quellen_prefix) + aktuelleQuelle
        }
    }

    fun onButtonEintragSpeichernClick(view: View?) {
        if (aktuelleQuelle.isBlank()) {
            Toast.makeText(this, R.string.keine_quelle_ausgewaehlt, Toast.LENGTH_SHORT).show()
            return
        }
        val stichwort = findViewById<TextView>(R.id.edit_text_stichwort).text.toString()
        val fundstelle = findViewById<TextView>(R.id.edit_text_fundstelle).text.toString()
        val text = findViewById<TextView>(R.id.edit_text_freier_text_zum_stichwort).text.toString()
        if (stichwort.isBlank() || fundstelle.isBlank() || text.isBlank()) {
            Toast.makeText(this, R.string.stichworte_nicht_null, Toast.LENGTH_SHORT).show()
            return
        }
        val helper = StichwortRegisterHelper(this)
        helper.insertStichwort(stichwort, aktuelleQuelle, fundstelle, text)

        Toast.makeText(this, R.string.eintrag_gespeichert, Toast.LENGTH_SHORT).show()
    }

    fun deleteText () {
        findViewById<TextView>(R.id.auto_complete_text_view_kurzbeichnung_der_quelle).text = ""
        findViewById<TextView>(R.id.edit_text_titel_der_quelle).text = ""
        findViewById<TextView>(R.id.edit_text_autoren).text = ""
        findViewById<TextView>(R.id.edit_text_verlag_ort_url).text = ""
        findViewById<TextView>(R.id.edit_text_publikationsdatum).text = ""
    }

}