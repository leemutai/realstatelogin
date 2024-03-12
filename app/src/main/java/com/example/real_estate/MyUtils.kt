//package com.example.real_estate
//
//import android.content.Context
//import android.text.format.DateFormat
//import android.icu.util.Calendar
//import android.widget.Toast
//
//object MyUtils {
//
//    const val USER_TYPE_GOOGLE = "Google" //if user created account using google Login/SignIn method
//    const val USER_TYPE_EMAIL = "Email"//if user created account using email method
//    const val USER_TYPE_PHONE = "Phone"// if user creates account using phone Login/SignIn
//
//    fun toast(context: Context, message: String){
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//    }
//
//    fun timestamp() : Long{
//        return System.currentTimeMillis()
//    }
//    fun formatTimestampDate(timestamp: Long) :String {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = timestamp
//
//        return   DateFormat.format("dd/MM/yyyy", calendar).toString()
//    }
//}

package com.example.real_estate
import android.text.format.DateFormat
import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar

object MyUtils {

    const val USER_TYPE_GOOGLE = "Google" //if user created account using google Login/SignIn method
    const val USER_TYPE_EMAIL = "Email"//if user created account using email method
    const val USER_TYPE_PHONE = "Phone"// if user creates account using phone Login/SignIn

    fun toast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun timestamp() : Long{
        return System.currentTimeMillis()
    }

    fun formatTimestampDate(timestamp: Long) :String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        return  DateFormat.format("dd/MM/yyyy", calendar).toString()
    }
}
