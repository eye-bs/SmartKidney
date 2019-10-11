package th.ac.kku.smartkidney

class ApiObject{
    var firstLogin: Boolean? = null
    var id: String? = null
    var user: User? = null
    var age:Int? = null
    var bloodPressure = listOf<BloodPressure>()
    var kidneyLev = listOf<KidneyLev>()
    var bloodSugar = listOf<BloodSugar>()
    var waterIn = 0
    var waterPerDay = 0
    var bmi = ArrayList<Float>()
    var isNewData: Boolean = false
    var notFound404: Boolean? = null
    var bpHashByWeek =  HashMap<Int,HashMap<Int , BloodPressure>>()
    var bsHashByWeek =  HashMap<Int,HashMap<Int , BloodSugar>>()
    var girHashByWeek =  HashMap<Int,HashMap<Int , KidneyLev>>()
    var startDateQuery:String? = null
    var endDateQuery:String? = null
    var weekQuery:Int? = null
    var bloodPressurePerDay = listOf<BloodPressure>()
    var kidneyLevPerDay = listOf<KidneyLev>()
    var bloodSugarPerDay = listOf<BloodSugar>()

    var kidneyRange:Int? = null

    private constructor(){
        println("ser obj")
    }
    companion object{
        val instant:ApiObject by lazy { ApiObject() }
    }

}