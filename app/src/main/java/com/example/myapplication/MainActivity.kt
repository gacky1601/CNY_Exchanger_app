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
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //變數宣告
        var twrate_local:Float = 4.256F
        var paybaorate_local:Float = 4.275F
        var local_date=DateFormat.format("yyyy-MM-dd", Date())
        var db_date:String=""
        var timestamp =DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        //firebase
        var db = FirebaseFirestore.getInstance()
        var dbpath=db.collection("exchange_rate")
        var checkdate=dbpath.document("last_update")
        val test_timestamp = 1594687935L
        val triggerTime: LocalDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(test_timestamp),
            TimeZone.getDefault().toZoneId()
        )
        checkdate.get()
            .addOnSuccessListener { document ->
                db_date=document.getString("date").toString()
                serverdate_tv.text=db_date.toString()
                if(local_date!=db_date){
                    toast("伺服器尚未更新因此使用"+db_date+"之值")
                }
                var getexchangerate=dbpath.document(db_date.toString())
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


