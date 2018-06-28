package com.example.weiwenjie.sassistbot

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_bot_view.*
import kotlinx.android.synthetic.main.card.view.*
import java.io.*

@SuppressLint("StaticFieldLeak")
//use to close activity from checkout
var botView:BotView? = null


var cartList=ArrayList<Int>()

//Load Data From server
val nameArray= arrayOf("Apple","Pear","Orange","Grape","Watermelon","Apple2","Apple3")
val priceArray= arrayOf(1.0,2.0,3.0,4.0,5.0,1.0,1.0)
val imageArray= arrayOf(R.drawable.apple,R.drawable.pear,R.drawable.orange,R.drawable.grape,R.drawable.watermelon,R.drawable.apple,R.drawable.apple)
var listPrice= ArrayList<Double>()
var btndelete :Boolean = false
var codeString :String?=null

class BotView : AppCompatActivity() {
    private lateinit var  svBarcode: SurfaceView
    private  lateinit var  tvBarcode: TextView

    private  lateinit var detector: BarcodeDetector
    private  lateinit var cameraSource: CameraSource
    var listOfGoods= ArrayList<Goods>()

    private var buysList: java.util.ArrayList<String>? = null

    //Support data type
    private var buyUnit = intArrayOf(0, 0, 0, 0, 0)
    private var boughtUnit = intArrayOf(0, 0, 0, 0, 0)
    private val buyName = arrayOf("Apple", "Pear", "Orange", "Grape", "Watermelon")
    private var ta: TextAnalysis?=null

    private var adapter:GoodsAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bot_view)
        botView=this

        svBarcode=findViewById(R.id.sv_barcode)
        tvBarcode=findViewById(R.id.tv_barcode)

        btnDelete.setOnClickListener{if(btndelete) {
            btndelete=false
            btnDelete.setBackgroundColor(Color.argb(255,213,212,212))
        } else {
            btndelete=true
            btnDelete.setBackgroundColor(Color.YELLOW)
        }}

        buysList = java.util.ArrayList()

        readItemsToBuy()
        btnCheckout.setOnClickListener {
            /*
            if (listPrice.sum() != 0.0) {
                val mAlertDialog = AlertDialog.Builder(this@MainActivity)
                mAlertDialog.setTitle("Check Out")
                mAlertDialog.setMessage("Total price is : $" + listPrice.sum() + "\nComfirm to Proceed")
                mAlertDialog.setIcon(R.mipmap.ic_launcher);
                mAlertDialog.setPositiveButton("Comfirm") { dialog, id ->
                    Toast.makeText(this@MainActivity, "Thank you!", Toast.LENGTH_SHORT).show()
                }
                mAlertDialog.setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
                mAlertDialog.show()
            }
            else Toast.makeText(this@MainActivity, "Please buy something first!", Toast.LENGTH_SHORT).show()
            */
            if (listPrice.sum() != 0.0){
                val passPrice:String = listPrice.sum().toString()
                val intent= Intent(this,CheckOut::class.java)
                intent.putExtra("totalPrice",passPrice)
                intent.putExtra("buyUnit",buyUnit)
                intent.putExtra("boughtUnit",boughtUnit)
                intent.putExtra("buyName",buyName)
                startActivity(intent)
            }else Toast.makeText(this@BotView, "Please buy something first!", Toast.LENGTH_SHORT).show()
        }

        adapter= GoodsAdapter(this,listOfGoods)
        lvCart.adapter=adapter

        if(cartList.size!=0){
            for (i in 0..cartList.size-1){
                listOfGoods.add(0,Goods(nameArray[cartList.get(i)],"Price : $" + priceArray[cartList.get(i)], imageArray[cartList.get(i)]))
                adapter!!.notifyDataSetChanged()
                btnCheckout.text = "Check out : $"+listPrice.sum().toString()
                if(cartList.get(i)>4){
                    buyUnit[0]-=1
                    boughtUnit[0]+=1
                }
                else{
                    buyUnit[cartList.get(i)]-=1
                    boughtUnit[cartList.get(i)]+=1
                }
            }

        }
        detector= BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val barcodes = detections?.detectedItems
                if(barcodes!!.size()>0){
                    tvBarcode.post{
                        codeString=barcodes.valueAt(0).displayValue
                        tvBarcode.text= codeString
                        for(j in 0 until nameArray.size) {
                            if (codeString== nameArray[j]) {
                                if(listOfGoods.size==0) {
                                    add(j)
                                }
                                else {
                                    for (x in (listOfGoods.size - 1) downTo 0) {
                                        if (listOfGoods[x].name == codeString) {
                                            break
                                        } else {
                                            if (x == 0) {
                                                add(j)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })

        cameraSource= CameraSource.Builder(this,detector).setRequestedPreviewSize(1024,768)
                .setRequestedFps(25f).setAutoFocusEnabled(true).build()

        svBarcode.holder.addCallback(object : SurfaceHolder.Callback2 {


            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, w: Int, h: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ContextCompat.checkSelfPermission(this@BotView, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                    cameraSource.start(holder)
                else ActivityCompat.requestPermissions(this@BotView, arrayOf(android.Manifest.permission.CAMERA),123)
            }



        })
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123)
            if(grantResults.isNotEmpty()&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
                cameraSource.start(svBarcode.holder)
            else Toast.makeText(this,"Scanner won't work without permission.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }

    fun delete(index:Int){
        listOfGoods.removeAt(index)
        adapter!!.notifyDataSetChanged()
        listPrice.removeAt((listPrice.size-1) -index)
        btnCheckout.text= "Check out : $"+listPrice.sum().toString()
        if(cartList.get((cartList.size-1) -index)>4){
            buyUnit[0]+=1
            boughtUnit[0]-=1
        }
        else{
            buyUnit[cartList.get((cartList.size-1)-index)]+=1
            boughtUnit[cartList.get((cartList.size-1)-index)]-=1
        }
        cartList.removeAt((cartList.size-1)-index)
    }



    fun add(index: Int){
        listOfGoods.add(0,Goods(nameArray[index],"Price : $" + priceArray[index], imageArray[index]))
        adapter!!.notifyDataSetChanged()
        listPrice.add(priceArray[index])
        if(index>4){
            buyUnit[0]-=1
            boughtUnit[0]+=1
        }
        else{
            buyUnit[index]-=1
            boughtUnit[index]+=1
        }
        btnCheckout.text = "Check out : $"+listPrice.sum().toString()
        cartList.add(index)
    }


    inner class  GoodsAdapter: BaseAdapter {
        var listOfGoods=ArrayList<Goods>()
        var context: Context?=null
        constructor(context: Context, listOfGoods: ArrayList<Goods>):super(){
            this.listOfGoods=listOfGoods
            this.context=context
        }
        @SuppressLint("InflateParams", "ViewHolder")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val goods =listOfGoods[p0]
            var inflater=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val myView = inflater.inflate(R.layout.card,null)
            myView.tvName.text=goods.name!!
            myView.tvDes.text=goods.des!!
            myView.ivName.setImageResource(goods.image!!)
            myView.tvNumber.text= goods.number.toString()
            myView.setOnClickListener{
                if(btndelete) {
                    delete(p0)
                }
                else{
                    val intent= Intent(context,GoodsInfo::class.java)
                    intent.putExtra("name",goods.name)
                    intent.putExtra("des",goods.des)
                    intent.putExtra("image",goods.image!!)
                    context!!.startActivity(intent)
                }
            }

            return myView
        }

        override fun getItem(p0: Int): Any {
            return listOfGoods[p0]
        }



        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listOfGoods.size
        }

    }


    private val FILE_BUY = "buy.txt"

    private fun readItemsToBuy() {
        var fis: FileInputStream? = null


        try {
            fis = openFileInput(FILE_BUY)
            val isr = InputStreamReader(fis!!)
            val br = BufferedReader(isr)
            val sb = StringBuilder()
            var text: String?
            text = br.readLine()
            while (text != null) {
                sb.append(text)
                text = br.readLine()
            }

            if (sb.toString() !== "") {
                sb.deleteCharAt(0)
                sb.deleteCharAt(sb.length - 1)
            }
            var nTemp = ""
            //Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show()
            for (i in 0 until sb.length) {
                if (sb[i] != ',') {
                    nTemp += sb[i]
                    if (i == sb.length - 1) {
                        ta = TextAnalysis(tempList(nTemp))
                        if (ta!!.valid!!) {
                            buyUnit[ta!!.namePosition] = buyUnit[ta!!.namePosition] + ta!!.quantity
                        }
                        nTemp = ""
                    }
                } else {
                    ta = TextAnalysis(tempList(nTemp))
                    if (ta!!.valid!!) {
                        buyUnit[ta!!.namePosition] = buyUnit[ta!!.namePosition] + ta!!.quantity
                    }
                    nTemp = ""
                }
            }


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }


    }

    fun bvClear(){
        listPrice.clear()
        cartList.clear()
    }



    private fun tempList(s: String): List<String> {
        var stemp = ""
        val tempList = java.util.ArrayList<String>()
        for (i in 0 until s.length) {
            if (s[i] == ' ' || i == s.length - 1) {
                if (i == s.length - 1) stemp += s[i]
                tempList.add(stemp)
                stemp = ""
            } else
                stemp += s[i]
        }

        return tempList
    }


}
