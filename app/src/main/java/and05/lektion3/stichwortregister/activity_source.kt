package and05.lektion3.stichwortregister

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.database.SQLException
import android.util.Log
import android.widget.*
import kotlin.math.log

class activity_source : AppCompatActivity() {
    private val dbHelper = StichwortRegisterHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source)

        val textView = findViewById<AutoCompleteTextView>(R.id.auto_complete_text_view_kurzbeichnung_der_quelle)
        val helper = StichwortRegisterHelper(this)
        val adapter = ArrayAdapter<String>(this, R.layout.list_item, helper.getKurzbezeichungen() as ArrayList<String>)
        textView.setAdapter(adapter)

        val textview2 = findViewById<AutoCompleteTextView>(R.id.auto_complete_text_view_kurzbeichnung_der_quelle)
        textview2.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val kurzbezeichnung = (view as TextView).text.toString()
            Log.d(activity_source.javaClass.simpleName, "Item: $kurzbezeichnung")
            val tableLine = helper.getQuelle(kurzbezeichnung)
            if (tableLine.size != 0) {
                findViewById<TextView>(R.id.edit_text_verlag_ort_url).text =
                    tableLine[StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_VERLAG_ORT_URL]
                findViewById<TextView>(R.id.edit_text_titel_der_quelle).text =
                    tableLine[StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_TITEL]
                findViewById<TextView>(R.id.edit_text_autoren).text =
                    tableLine[StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_AUTOREN]
                findViewById<TextView>(R.id.edit_text_publikationsdatum).text =
                    tableLine[StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_PUBLIKATIONSDATUM]
            }
        }

        adapter.sort(object : Comparator<String> {
            override fun compare(p0: String, p1: String): Int {
                return p0.compareTo(p1, ignoreCase = true)
            }})


    }

    companion object Constants{
        const val QUELLEN_ID = "QUELLEN ID"
    }



    fun onButtonQuelleZurueckliefernClick(view: View?) {
        val kurzbezeichung = findViewById<TextView>(R.id.auto_complete_text_view_kurzbeichnung_der_quelle).text.toString()
        val titel = findViewById<TextView>(R.id.edit_text_titel_der_quelle).text.toString()
        val autoren = findViewById<TextView>(R.id.edit_text_autoren).text.toString()
        val verlagOrtUrl = findViewById<TextView>(R.id.edit_text_verlag_ort_url).text.toString()
        val publikationsdatum = findViewById<TextView>(R.id.edit_text_publikationsdatum).text.toString()

        if (kurzbezeichung.isBlank() || titel.isBlank() || autoren.isBlank() || verlagOrtUrl.isBlank() || publikationsdatum.isBlank()) {
            Toast.makeText(this, R.string.quellen_not_null, Toast.LENGTH_LONG).show()
            return
        }

        val helper = StichwortRegisterHelper(this)
        helper.insertQuelle(kurzbezeichung, titel, autoren, verlagOrtUrl, publikationsdatum)
        val pushIntent = this.intent
        pushIntent.putExtra(QUELLEN_ID, kurzbezeichung)
        setResult(Activity.RESULT_OK, pushIntent)
        finish()

    }


}