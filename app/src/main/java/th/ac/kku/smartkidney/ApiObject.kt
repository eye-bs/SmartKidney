package th.ac.kku.smartkidney

class ApiObject{
    var firstLogin: Boolean? = null
    var id: String? = null
    var user: User? = null
    var bloodPressure = listOf<BloodPressure>()
    var kidneyLev = listOf<KidneyLev>()
    var bloodSugar = listOf<BloodSugar>()
    var isNewData: Boolean = false
    var notFound404: Boolean? = null
    var bpHashByWeek =  HashMap<Int,ArrayList<BloodPressure>>()
    var bsHashByWeek =  HashMap<Int,ArrayList<BloodSugar>>()
    var gitHashByWeek =  HashMap<Int,ArrayList<KidneyLev>>()

    var email: String? = null
    var  name: String? = null
    var birthDate: String? = null
    var gender: String? = null
    var hospital: String? = null
    var weight: Int? = null
    var height:Int? = null


    private constructor(){
        println("ser obj")
    }
    companion object{
        val instant:ApiObject by lazy { ApiObject() }
    }

}