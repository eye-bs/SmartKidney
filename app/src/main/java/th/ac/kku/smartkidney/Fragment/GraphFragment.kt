package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout


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
            val contentLayout = view.findViewById<LinearLayout>(R.id.contentLayoutFragment)

            graphFragmentNoData.visibility = View.INVISIBLE

            val readjson = ReadJSON(context!!)
            val obj = readjson.getJSONObject(Constant.GRAPH_DETAIL_JSON,param1!!)

            val setupChart = SetupChart(obj!!,context!!,contentLayout)
            setupChart.createLayout()

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
