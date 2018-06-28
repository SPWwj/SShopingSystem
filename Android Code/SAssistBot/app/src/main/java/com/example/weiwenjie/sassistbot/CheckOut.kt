package com.example.weiwenjie.sassistbot

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_check_out.*
import kotlinx.android.synthetic.main.activity_goods_info.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Console
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Array
import java.util.ArrayList

private var itemsAdapter: ArrayAdapter<String>? = null
private var items: ArrayList<String>? = null

private var buysList: ArrayList<String>? = null
private var buyUnit :IntArray? = null
private var boughtUnit :IntArray? = null
internal var buyName: kotlin.Array<String>? = null

class CheckOut : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        buyUnit = intent.extras.getIntArray("buyUnit")
        buyName = intent.extras.getStringArray("buyName")
        boughtUnit = intent.extras.getIntArray("boughtUnit")

        buysList = ArrayList()
        items = ArrayList()

        itemsAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, items)
        list2buy!!.setAdapter(itemsAdapter)


        val totalPrice = intent.extras.getString("totalPrice")

        textView.text=totalPrice
        if (buyUnit!!.size!=0)
        for( i in 0..buyUnit!!.size-1 ) {

            if(buyUnit!![i]>0) {
                val s: String = buyName!![i].toString() +
                        " " + buyUnit!![i].toString()
                itemsAdapter!!.add(s)
            }
        }
        imageView.setOnClickListener(){

        }
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(totalPrice, BarcodeFormat.QR_CODE, 400, 400)
            val imageViewQrCode = findViewById(R.id.imageView) as ImageView
            imageViewQrCode.setImageBitmap(bitmap)
            textView.text=("Totle Price is $"+totalPrice + " "+"Scan to Pay")
        } catch (e: Exception) {

        }

        imageView.setOnClickListener(){
            val mAlertDialog = AlertDialog.Builder(this@CheckOut)
            mAlertDialog.setTitle("Check Out")
            mAlertDialog.setMessage("Total price is : $" + totalPrice + "\nComfirm to Proceed")
            mAlertDialog.setIcon(R.mipmap.ic_launcher);
            mAlertDialog.setPositiveButton("Comfirm") { dialog, id ->
                Toast.makeText(this@CheckOut, "Thank you!", Toast.LENGTH_SHORT).show()
                writeItemsTobuy()
                writeBought()
                getInstance().bvClear()
                getInstance().finish()
                finish()

            }
            mAlertDialog.setNegativeButton("Cancel") { dialog, id ->
                dialog.dismiss()
            }
            mAlertDialog.show()
        }
    }

    private val FILE_BUY = "buy.txt"

    private fun writeItemsTobuy() {
        for(i in 0..buyUnit!!.size-1){
            if (buyUnit!![i]<0){
                buyUnit!![i]=0
            }

        }
        var fos: FileOutputStream? = null
        if(buysList!=null)
        buysList!!.clear()
        for (i in buyUnit!!.indices) {
            buysList!!.add(buyName!![i] + " " + buyUnit!![i])

        }
        try {
            fos = openFileOutput(FILE_BUY, MODE_PRIVATE)
            fos!!.write(buysList.toString().toByteArray())

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private val FILE_BOUGHT = "bought.txt"

    private fun writeBought() {
        var fos: FileOutputStream? = null
        if(buysList!=null)
            buysList!!.clear()
        for (i in boughtUnit!!.indices) {
            buysList!!.add(buyName!![i] + " " + boughtUnit!![i])

        }
        try {
            fos = openFileOutput(FILE_BOUGHT, MODE_PRIVATE)
            fos!!.write(buysList.toString().toByteArray())

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }
    fun getInstance(): BotView {
        return com.example.weiwenjie.sassistbot.botView!!
    }

}
