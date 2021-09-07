package id.tbadhit.mynotesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.tbadhit.mynotesapp.adapter.NoteAdapter
import id.tbadhit.mynotesapp.databinding.ActivityMainBinding
import id.tbadhit.mynotesapp.db.NoteHelper
import id.tbadhit.mynotesapp.entity.Note
import id.tbadhit.mynotesapp.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Notes"

        binding.apply {
            rvNotes.layoutManager = LinearLayoutManager(this@MainActivity)
            rvNotes.setHasFixedSize(true)
            adapter = NoteAdapter(this@MainActivity)
            rvNotes.adapter = adapter

            fabAdd.setOnClickListener {
                val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
//                startForResult.launch(intent)
                startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
            }
        }

        if (savedInstanceState == null) {
            // proses ambil data
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                NoteAddUpdateActivity.REQUEST_ADD -> if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                    val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackbarMessage("Satu item berhasil ditambahkan")
                }
                NoteAddUpdateActivity.REQUEST_UPDATE ->
                    when (resultCode) {
                        NoteAddUpdateActivity.RESULT_UPDATE -> {
                            val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
                            adapter.updateItem(position, note)
                            binding.rvNotes.smoothScrollToPosition(position)
                            showSnackbarMessage("Satu item berhasil diubah")
                        }
                        NoteAddUpdateActivity.RESULT_DELETE -> {
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
                            adapter.removeItem(position)
                            showSnackbarMessage("Satu item berhasil dihapus")
                        }
                    }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main){
            binding.progressbar.visibility = View.VISIBLE
            val noteHelper = NoteHelper.getInstance(applicationContext)
            noteHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }

            binding.progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }

            noteHelper.close()
        }
    }

//    private val startForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.data != null) {
//                when (result.resultCode) {
//                    NoteAddUpdateActivity.RESULT_ADD -> {
//                        val note =
//                            result.data!!.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
//
//                        adapter.addItem(note)
//                        binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)
//
//                        showSnackbarMessage("Satu item berhasil di tambahkan")
//                    }
//                    NoteAddUpdateActivity.RESULT_UPDATE -> {
//                        val note =
//                            result.data!!.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
//                        val position =
//                            result.data!!.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
//                        adapter.updateItem(position, note)
//                        binding.rvNotes.smoothScrollToPosition(position)
//                        showSnackbarMessage("Satu item berhasil diubah")
//                    }
//                    NoteAddUpdateActivity.RESULT_DELETE -> {
//                        val position =
//                            result.data!!.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
//                        adapter.removeItem(position)
//                        showSnackbarMessage("Satu item berhasil dihapus")
//                    }
//                }
//            }
//        }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }
}