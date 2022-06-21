package and05.lektion3.stichwortregister


import and05.lektion3.stichwortregister.StichwortRegisterHelper.Contract.SQL_CREATE_QUELLEN
import and05.lektion3.stichwortregister.StichwortRegisterHelper.Contract.SQL_CREATE_STICHWORTE
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class StichwortRegisterHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object Contract {
        const val DATABASE_NAME = "StichwortRegister"
        const val DATABASE_VERSION = 1

        object Quellen {
            const val TABLE_NAME = "quellen"
            const val COLUMN_NAME_KURZBEZEICHNUNG = "kurzbezeichnung"
            const val COLUMN_NAME_TITEL = "titel"
            const val COLUMN_NAME_AUTOREN = "autoren"
            const val COLUMN_NAME_VERLAG_ORT_URL = "verlag_ort_url"
            const val COLUMN_NAME_PUBLIKATIONSDATUM = "publikationsdatum"
        }

        object Stichworte {
            const val TABLE_NAME = "Stichworte"
            const val COLUMN_NAME_ID = "id"
            const val COLUMN_NAME_STICHWORTE = "stichworte"
            const val COLUMN_NAME_QUELLE = "quelle"
            const val COLUMN_NAME_FUNDSTELLE = "fundstelle"
            const val COLUMN_NAME_TEXT = "text"
        }

        const val SQL_CREATE_QUELLEN = "CREATE TABLE ${Quellen.TABLE_NAME} (" +
                "${Quellen.COLUMN_NAME_KURZBEZEICHNUNG} TEXT PRIMARY KEY," +
                "${Quellen.COLUMN_NAME_TITEL} TEXT NOT NULL," +
                "${Quellen.COLUMN_NAME_AUTOREN} TEXT NOT NULL," +
                "${Quellen.COLUMN_NAME_VERLAG_ORT_URL} TEXT NOT NULL," +
                "${Quellen.COLUMN_NAME_PUBLIKATIONSDATUM} TEXT )"

        const val SQL_CREATE_STICHWORTE = "CREATE TABLE ${Stichworte.TABLE_NAME} (" +
                "${Stichworte.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${Stichworte.COLUMN_NAME_STICHWORTE} TEXT NOT NULL," +
                "${Stichworte.COLUMN_NAME_QUELLE} TEXT NOT NULL," +
                "${Stichworte.COLUMN_NAME_FUNDSTELLE} TEXT NOT NULL," +
                "${Stichworte.COLUMN_NAME_TEXT} TEXT," +
                "CONSTRAINT QuellenFK FOREIGN KEY (${Stichworte.COLUMN_NAME_QUELLE})" +
                "REFERENCES ${Quellen.TABLE_NAME} (${Quellen.COLUMN_NAME_KURZBEZEICHNUNG})" +
                "ON DELETE RESTRICT ON UPDATE CASCADE)"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_QUELLEN)
        db?.execSQL(SQL_CREATE_STICHWORTE)
        Log.i(javaClass.simpleName, "Datenbank erzeugt in: \"" + db?.getPath() + "\"")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertQuelle(
        kurzbezeichung: String,
        titel: String,
        autoren: String,
        verlagorturl: String,
        publikationsdatum: String
    ) {
        val values = ContentValues(5)
        with(values) {
            put(
                StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_KURZBEZEICHNUNG,
                kurzbezeichung
            )
            put(StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_TITEL, titel)
            put(StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_AUTOREN, autoren)
            put(StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_VERLAG_ORT_URL, verlagorturl)
            put(
                StichwortRegisterHelper.Contract.Quellen.COLUMN_NAME_PUBLIKATIONSDATUM,
                publikationsdatum
            )
        }
        val db = writableDatabase
        try {
            db.insertOrThrow(StichwortRegisterHelper.Contract.Quellen.TABLE_NAME, null, values)
        } catch (ex: SQLException) {
            Log.d(javaClass.simpleName, ex.toString())
        } finally {
            db.close()
        }
    }

    fun insertStichwort(stichwort: String, quelle: String, funstelle: String, text: String) {
        val values = ContentValues(4)
        with(values) {
            values.put(Stichworte.COLUMN_NAME_STICHWORTE, stichwort)
            values.put(Stichworte.COLUMN_NAME_QUELLE, quelle)
            values.put(Stichworte.COLUMN_NAME_FUNDSTELLE, funstelle)
            values.put(Stichworte.COLUMN_NAME_TEXT, text)
        }
        val db = writableDatabase
        try {
            db.insertOrThrow(Stichworte.TABLE_NAME, null, values)
        } catch (ex: SQLException) {
            Log.i(javaClass.simpleName, ex.toString())
        } finally {
            db.close()
        }
    }

    fun getKurzbezeichungen(): ArrayList<String>? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            Quellen.TABLE_NAME, arrayOf(Quellen.COLUMN_NAME_KURZBEZEICHNUNG),
            null, null, null, null, Quellen.COLUMN_NAME_KURZBEZEICHNUNG, null
        )
        val kurzbezeichnungen = ArrayList<String>()
        while (cursor.moveToNext()) {
            kurzbezeichnungen.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return kurzbezeichnungen
    }

    fun getQuelle(kurzbezeichung: String): HashMap<String, String> {
        val db = readableDatabase
        val cursor = db.query(
            Quellen.TABLE_NAME, null, "${Quellen.COLUMN_NAME_KURZBEZEICHNUNG}='$kurzbezeichung'",
            null, null, null, null, null)
        val result = HashMap<String, String>()
        if (cursor.moveToNext()) {
            for (i in 0 until cursor.columnCount) {
                result[cursor.getColumnName(i)] = cursor.getString(i)
            }
        }
        return result
    }
}

