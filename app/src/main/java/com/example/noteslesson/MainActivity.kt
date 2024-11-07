package com.example.noteslesson

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {

    var db: NoteDatabase? = null
    private lateinit var noteET: EditText
    private lateinit var textView: TextView
    private lateinit var saveBTN: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        noteET = findViewById(R.id.noteET)
        textView = findViewById(R.id.textView)
        saveBTN = findViewById(R.id.saveBTN)
        db = NoteDatabase.getDatabase(this)
        readDatabase(db!!)
    }

    override fun onResume() {
        super.onResume()
        saveBTN.setOnClickListener {
            val note = Note(noteET.text.toString(), Date().time.toString())
            addNote(db!!, note)
            readDatabase(db!!)
        }
    }
    @DelicateCoroutinesApi
    private fun addNote(db: NoteDatabase, note: Note) = GlobalScope.async {
        db.getNoteDao().insert(note)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun readDatabase(db: NoteDatabase) = GlobalScope.async {
        textView.text = ""
        val list = db.getNoteDao().getAllNotes()
        list.forEach { i -> textView.append(i.text + "\n") }
    }
}