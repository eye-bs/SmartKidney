package th.ac.kku.smartkidney

import android.content.Context
import android.util.Log

class CalcInput(val context: Context){

    fun calcBloodPressure(upper: Float , lower: Float):Int?{
        return if (upper >= 160 || lower >= 100){
            4
        }else if (upper > 139 || lower > 89){
            3
        }else if (upper > 129 || lower > 84){
            2
        }else if (upper > 119 || lower > 79){
            1
        } else {
            0
        }
    }
    fun calcKidney(cr:Float,age: Int,gender:String):Double {
        var eGFR: Double
        var doubleAge = age.toDouble()
        eGFR = if (gender == "female") {
            if (cr <= 0.7) {
                144 * Math.pow((cr / 0.7), -0.329) * Math.pow(0.993, doubleAge)
            } else {
                144 * Math.pow((cr / 0.7), -1.209) * Math.pow(0.993, doubleAge)
            }
        } else {
            if (cr <= 0.9) {
                141 * Math.pow((cr / 0.9), -0.411) * Math.pow(0.993, doubleAge)
            } else {
                141 * Math.pow((cr / 0.9), -1.209) * Math.pow(0.993, doubleAge)
            }
        }

        ApiObject.instant.kidneyRange = when{
          eGFR >= 90 -> 0
          eGFR >= 60 -> 1
          eGFR >= 45 -> 2
          eGFR >= 30 -> 3
          eGFR >= 15 -> 4
          eGFR < 15 -> 5
          else -> 0
      }
        return eGFR
    }

    fun calcGlucose(glucose:Float):Int{
        return when {
            glucose < 50 -> 0
            glucose < 70 -> 1
            glucose < 131 -> 2
            glucose < 181 -> 3
            glucose < 241 -> 4
            glucose >= 241 -> 5
            else -> 0
        }
    }
    fun calchba1c(glucose:Float,hba1c:Float):Int{
        return when {
            hba1c < 7 -> 0
            hba1c in 7.0..8.0 -> {
                if(glucose in 70.0..130.0){
                    2
                }else{
                    1
                }
            }
            hba1c > 8 -> {
                if (glucose in 70.0..130.0){
                    4
                }else {
                    3
                }
            }
            else -> 0
        }
    }
}