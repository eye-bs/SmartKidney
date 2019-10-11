package th.ac.kku.smartkidney

import java.text.SimpleDateFormat

class Constant {
    companion object {
        const val BLOOD_PRESSURE = "ความดันโลหิต"
        const val KIDNEY_FILTRATION_RATE = "อัตราการกรองของไต"
        const val BLOOD_SUGAR_LEV= "ระดับน้ำตาลในเลือด"
        const val WATER = "ปริมาณน้ำที่ควรดื่ม/วัน"
        const val BMI = "ดัชนีมวลกาย"
        const val TAG = "SmartKidneyTag"
        const val GRAPH_DETAIL_JSON = "graphFragment.json"
        const val ANALYZE_DETAL_JSON = "analyzeDetail.json"
        const val HEALTHED_TOPIC_JSON = "healthEdTopic.json"
        const val BACK_TO_HOME = "BACK_TO_HOME"
        const val HEALTH_ED_TOPIC = "เกร็ดความรู้"
        const val API_BASE_PATH = "http://45.76.50.243:1323/"
        val formatOfDetail = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val formatOfGetbyDate = SimpleDateFormat("yyyy-MM-dd")
    }
}