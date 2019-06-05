package alex.android.recifeopendata

import android.content.Context
import android.net.ConnectivityManager
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

object EscolaHttp {

    val ESCOLAS_JSON_URL = "http://dados.recife.pe.gov.br/dataset/4d3a3b39-9ea9-46ed-bf21-a2670de519c1/resource/"+
            "d907ffdb-d8a9-4426-b582-afa776664135/download/rede-de-educacao-municipal.json"

    @Throws(IOException::class)
    private fun connect(urlAddress: String): HttpURLConnection {
        val second = 1000
        val url = URL(urlAddress)
        val connection = (url.openConnection() as
                HttpURLConnection).apply {
            readTimeout = 10 * second
            connectTimeout = 15 * second
            requestMethod = "GET"
            doInput = true
            doOutput = false
        }
        connection.connect()
        return connection
    }
    fun hasConnection(ctx: Context): Boolean{
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected
    }

    fun loadEscolas(): List<Escola>?{
        try {
            val connection = connect(ESCOLAS_JSON_URL)
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK){
                val inputStream = connection.inputStream
                val json = JSONObject(streamToString(inputStream))
                return readEscolasFromJson(json)
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }
    @Throws(JSONException::class)
    fun readEscolasFromJson(json: JSONObject): List<Escola>{
        val escolasList = mutableListOf<Escola>()
        var currentCategory: String
        val jsonEscolas = json.getJSONObject("metadados").getJSONArray("campos")


        for (i in 0 until jsonEscolas.length()){
            val escola = Escola(jsonEscolas.getJSONObject(i).optInt("codigo"),
                jsonEscolas.getJSONObject(i).optString("descricao"));
            escolasList.add(i, escola);
            System.out.println(escola.toString())
        }

        System.out.println(escolasList.toString());


//        for (i in 0 until jsonEscolas.length()){
//            val jsonCategory = jsonEscolas.getJSONObject(i)
//            currentCategory = jsonCategory.getString("campos")
//            val jsonEscolas = jsonCategory.getJSONArray("campos")
//            for (j in 0 until jsonEscolas.length()){
//                val jsonEscola = jsonEscolas.getJSONObject(j)
//                val escolas = Escola(
//                    jsonEscola.getInt("escola_codigo"),
//                    currentCategory
//                  //  jsonEscola.getString("endereco")
//                )
//                escolasList.add(escolas)
//            }
//        }
        return escolasList
    }
    @Throws(IOException::class)
    private fun streamToString(inputStream: InputStream): String{
        val buffer = ByteArray(1024)
        val bigBuffer = ByteArrayOutputStream()
        var bytesRead: Int
        while (true){
            bytesRead=inputStream.read(buffer)
            if (bytesRead == -1)break
            bigBuffer.write(buffer, 0, bytesRead)
        }
        return String(bigBuffer.toByteArray(),
            Charset.forName("UTF-8"))
    }
}