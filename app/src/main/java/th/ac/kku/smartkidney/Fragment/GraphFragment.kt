package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import android.text.InputType
import android.util.Log
import android.widget.*


private const val ARG_PARAM1 = "param1"

// TODO: Rename parameter arguments, choose names that match


class GraphFragment : Fragment() {

    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun createLayout(view: View){

        val graphFragmentNoData = view.findViewById<RelativeLayout>(R.id.pressureFragmentNoData)
        val textViewNoData = view.findViewById<TextView>(R.id.tvFragmentNoData)
        val contentLayout = view.findViewById<LinearLayout>(R.id.contentLayoutFragment)

        graphFragmentNoData.visibility = View.INVISIBLE

        if (param1 == Constant.BLOOD_PRESSURE && ApiObject.instant.bloodPressure.isEmpty()){
            graphFragmentNoData.visibility = View.VISIBLE
            textViewNoData.text = "ไม่มีข้อมูล$param1"
        }else if (param1 == Constant.BLOOD_SUGAR_LEV && ApiObject.instant.bloodSugar.isEmpty()){
            graphFragmentNoData.visibility = View.VISIBLE
            textViewNoData.text = "ไม่มีข้อมูล$param1"
        }else if (param1 == Constant.KIDNEY_FILTRATION_RATE && ApiObject.instant.kidneyLev.isEmpty()){
            graphFragmentNoData.visibility = View.VISIBLE
            textViewNoData.text = "ไม่มีข้อมูล$param1"
        }else if(param1 == Constant.WATER && ApiObject.instant.user!!.weight == 0){
            showDialogChangeWeight()
            graphFragmentNoData.visibility = View.VISIBLE
            textViewNoData.text = "ไม่มีข้อมูล$param1"
        }
        val readjson = ReadJSON(context!!)
        val obj = readjson.getJSONObject(Constant.GRAPH_DETAIL_JSON,param1!!)

            val setupChart = SetupChart(obj!!,context!!,contentLayout,param1!!)
            val hasValue = setupChart.isHasValue()
        if (hasValue){
            setupChart.createLayout()
        }
    }

    private fun showDialogChangeWeight() {

        var m_Text = ""
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("กรุณากรอกน้ำหนัก")

        val input = EditText(context!!)

        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "น้ำหนัก (kg)"

        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            m_Text = input.text.toString()
            val progressBar = ProgressBar(context)
            progressBar.visibility = View.VISIBLE
            val apiHandler = ApiHandler(context!! , null , null)
            apiHandler.editUserInfo(ApiObject.instant.user!!.id , null,null,null,null,null,m_Text.toInt(),null)
            apiHandler.getUsers(ApiObject.instant.user!!.id)
            val waterPerDay = m_Text.toInt() * 2.2 * 30 / 2
            ApiObject.instant.waterPerDay = waterPerDay.toInt()

        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_graph, container, false)
        if (param1 == Constant.BACK_TO_HOME) {
            val intent = Intent(getActivity(), HomeActivity::class.java)
            getActivity()!!.startActivity(intent)

        }else {
            createLayout(view)
        }
        return view
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                GraphFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }
}
