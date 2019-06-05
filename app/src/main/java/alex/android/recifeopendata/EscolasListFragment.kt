package alex.android.recifeopendata

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import kotlinx.android.synthetic.main.fragment_escolas_list.*



class EscolasListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var asyncTask: EscolasDownloadTask? = null
    private var escolasList = mutableListOf<Escola>()
    private var adapter: ArrayAdapter<Escola>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_escolas_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, escolasList)
        listView.emptyView = txtMessage
        listView.adapter = adapter
        if (escolasList.isNotEmpty()){
            showProgress(false)
        }else{
            if (asyncTask==null){
                if (EscolaHttp.hasConnection(requireContext())){
                    startDownloadJson()
                }else{
                    progressBar.visibility = View.GONE
                    txtMessage.setText(R.string.error_no_connection)
                }
            }else if (asyncTask?.status ==
                    AsyncTask.Status.RUNNING){
                showProgress(true)
            }
        }
    }

   private fun showProgress(show: Boolean){
       if (show){
           txtMessage.setText(R.string.message_progress)
       }
       txtMessage.visibility = if (show) View.VISIBLE else
           View.GONE
       progressBar.visibility = if (show) View.VISIBLE else
           View.GONE
   }
    private fun startDownloadJson(){
        if (asyncTask?.status != AsyncTask.Status.RUNNING){
            asyncTask = EscolasDownloadTask()
            asyncTask?.execute()
        }
    }

    private fun updateEscolasList(result: List<Escola>?){
        if (result != null){
            escolasList.clear()
            escolasList.addAll(result)
        }else{
            txtMessage.setText(R.string.error_load_escola)
        }
        adapter?.notifyDataSetChanged()
        asyncTask = null
    }

    inner class EscolasDownloadTask: AsyncTask<Void, Void, List<Escola>?>(){
        override fun onPreExecute() {
            super.onPreExecute()
            showProgress(true)
        }
        override fun doInBackground(vararg strings: Void): List<Escola>?{
            return EscolaHttp.loadEscolas()
        }

        override fun onPostExecute(escolas: List<Escola>?) {
            super.onPostExecute(escolas)
            showProgress(false)
            updateEscolasList(escolas)
        }
    }
}
