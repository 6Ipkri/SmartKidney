package th.ac.kku.smartkidney

class ApiObject{
    var firstLogin: Boolean? = null
    var id: String? = null
    var user: User? = null
    var age:Int? = null
    var bloodPressure = HashMap<Int, HashMap<Int,BloodPressure>>()
    var kidneyLev = HashMap<Int, HashMap<Int,KidneyLev>>()
    var bloodSugar = HashMap<Int, HashMap<Int,BloodSugar>>()
    var waterIn = 0
    var waterPerDay = 0
    var bmi = ArrayList<Float>()
    var isNewData: Boolean = false
    var notFound404: Boolean? = null

    var startDateQuery:String? = null
    var endDateQuery:String? = null
    var weekQuery:Int? = null
    var currentWeek:Int? = null
    var bloodPressurePerDay = ArrayList<BloodPressure>()
    var kidneyLevPerDay = ArrayList<KidneyLev>()
    var bloodSugarPerDay = ArrayList<BloodSugar>()

    var kidneyRange:Int? = null
    var healthEdPostion:Int? = null
    var weekKeysbp = arrayListOf<Int>()
    var weekKeysbs = arrayListOf<Int>()
    var weekKeysgir = arrayListOf<Int>()

    private constructor(){
        println("ser obj")
    }
    companion object{
        val instant:ApiObject by lazy { ApiObject() }
    }

    fun resetData(){

        firstLogin = null
        id = null
         user = null
         age = null
         bloodPressure = HashMap<Int, HashMap<Int,BloodPressure>>()
         kidneyLev = HashMap<Int, HashMap<Int,KidneyLev>>()
         bloodSugar = HashMap<Int, HashMap<Int,BloodSugar>>()
         waterIn = 0
         waterPerDay = 0
         bmi = ArrayList<Float>()
         isNewData = false
         notFound404 = null

         startDateQuery = null
         endDateQuery = null
         weekQuery = null
         currentWeek = null
         bloodPressurePerDay = ArrayList<BloodPressure>()
         kidneyLevPerDay = ArrayList<KidneyLev>()
         bloodSugarPerDay = ArrayList<BloodSugar>()

         kidneyRange = null
         healthEdPostion = null
         weekKeysbp = arrayListOf<Int>()
         weekKeysbs = arrayListOf<Int>()
         weekKeysgir = arrayListOf<Int>()
    }

}