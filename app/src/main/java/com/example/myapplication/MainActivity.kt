package com.example.myapplication

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //變數宣告
        var twrate_local:Float = 4.256F
        var paybaorate_local:Float = 4.275F
        var local_date=DateFormat.format("yyyy-MM-dd", Date())
        var db_date:String=""
        var timestamp:String =""
        //firebase
        var db = FirebaseFirestore.getInstance()
        var dbpath=db.collection("exchange_rate")
        var checkdate=dbpath.document("last_update")
        checkdate.get()
            .addOnSuccessListener { document ->
                db_date=document.getString("date").toString()
                var abc= document.getString("timestamp").toString().toLong()
                var triggerTime="123"
                val dv: Long =
                    java.lang.Long.valueOf(abc.toString()) * 1000 // its need to be in milisecond

                val df = Date(dv)
                val vv: String = SimpleDateFormat("yyyy-MM-dd ahh:mm").format(df)
                serverdate_tv.text=vv
                var getexchangerate=dbpath.document(abc.toString())
                getexchangerate.get()
                    .addOnSuccessListener { document ->
                        twrate_local=document.getString("twbank_cny").toString().toFloat()
                        twexc_tv.text=twrate_local.toString()
                        paybaorate_local=document.getString("paybao_cny").toString().toFloat()
                        paybaoexc_tv.text=paybaorate_local.toString()
                    }
                    .addOnFailureListener { exception ->
                        twexc_tv.text="error"
                        paybaoexc_tv.text="error"
                    }
            }
            .addOnFailureListener { exception -> toast("請連接網路") }
        //轉換
        calculate_button.setOnClickListener {
            try{
                val mynum: Int = inputnum.text.toString().toInt()
                twresult_tv.text = ((((mynum * twrate_local)*100).toInt().toFloat())/100).toString()
                paybaoresult_tv.text = ((((mynum * paybaorate_local)*100).toInt().toFloat())/100).toString()
                var result=(((((mynum * twrate_local) - (mynum * paybaorate_local))*100).toInt()).toFloat())/100
                diff_tv.text = (result.toString())
            }
            catch(e: Exception){
                toast("請輸入有效值")
            }
            if(twrate_local<paybaorate_local){twresult_tv.setBackgroundColor(Color.parseColor("#66BB6A"))}
            else{paybaoresult_tv.setBackgroundColor(Color.parseColor("#66BB6A"))}
        }
    }
}


