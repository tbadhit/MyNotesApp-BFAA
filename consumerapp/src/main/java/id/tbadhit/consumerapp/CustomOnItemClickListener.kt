package id.tbadhit.consumerapp

import android.view.View

// Kelas ini bertugas membuat item seperti CardView bisa diklik di dalam adapter.
class CustomOnItemClickListener(
    private val position: Int,
    private val onItemClickCallback: OnItemClickCallback
) : View.OnClickListener {

    interface OnItemClickCallback {
        fun onItemClicked(view: View, position: Int)
    }

    override fun onClick(v: View) {
        onItemClickCallback.onItemClicked(v, position)
    }
}