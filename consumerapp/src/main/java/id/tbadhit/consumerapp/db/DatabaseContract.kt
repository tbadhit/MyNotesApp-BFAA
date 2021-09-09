package id.tbadhit.consumerapp.db

import android.net.Uri
import android.provider.BaseColumns

/*
Kelas contract ini akan digunakan untuk mempermudah akses nama tabel dan nama kolom di
dalam database.
 */
object DatabaseContract {

    const val AUTHORITY = "id.tbadhit.mynotesapp"
    const val SCHEME = "content"

    internal class NoteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "note"
            const val _ID = "_id"
            const val TITLE = "title"
            const val DESCRIPTION = "description"
            const val DATE = "date"

            // untuk membuat URI content://id.tbadhit.picodiploma.mynotesapp/note
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}