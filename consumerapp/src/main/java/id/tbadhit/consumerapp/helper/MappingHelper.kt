package id.tbadhit.consumerapp.helper

import android.database.Cursor
import id.tbadhit.consumerapp.db.DatabaseContract.NoteColumns.Companion.DATE
import id.tbadhit.consumerapp.db.DatabaseContract.NoteColumns.Companion.DESCRIPTION
import id.tbadhit.consumerapp.db.DatabaseContract.NoteColumns.Companion.TITLE
import id.tbadhit.consumerapp.db.DatabaseContract.NoteColumns.Companion._ID
import id.tbadhit.consumerapp.entity.Note

object MappingHelper {
    // merubah cursor ke ArrayList
    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notesList = ArrayList<Note>()

        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(_ID))
                val title = getString(getColumnIndexOrThrow(TITLE))
                val description = getString(getColumnIndexOrThrow(DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DATE))
                notesList.add(Note(id, title, description, date))
            }
        }

        return notesList
    }

    // merubah cursor ke Object
    fun mapCursorToObject(notesCursor: Cursor?): Note {
        var note = Note()
        notesCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(_ID))
            val title = getString(getColumnIndexOrThrow(TITLE))
            val description = getString(getColumnIndexOrThrow(DESCRIPTION))
            val date = getString(getColumnIndexOrThrow(DATE))
            note = Note(id, title, description, date)
        }

        return note
    }
}