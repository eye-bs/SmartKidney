package th.ac.kku.smartkidney

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream


class ReadJSON(val context: Context) {

    private fun readJSONfromFile(fileName: String): JSONArray? {
        try {
            lateinit var json: String
            val inputStream: InputStream = context.assets.open(fileName)
            json = inputStream.bufferedReader().use { it.readText() }
            var jsonArr = JSONArray(json)

            return jsonArr

        } catch (e: IOException) {
            return null
        }

    }
    fun getJSONObject(fileName: String,name: String): JSONObject?{

        val jsonArr = readJSONfromFile(fileName)
        if(jsonArr != null){
            for (i in 0 until jsonArr.length()) {
                var jsonObj = jsonArr.getJSONObject(i)
                var getName = jsonObj.getString("name")
                if(name == getName){
                    return jsonObj
                }

            }
        }
        return null
    }

}