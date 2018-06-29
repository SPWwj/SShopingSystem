package com.example.weiwenjie.sassistbot

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_import_items.*


class ImportItems : AppCompatActivity() {
    internal var database = FirebaseDatabase.getInstance()
    var myStoreRef=database.getReference("Store0");
    private  lateinit var  tvImportBarcode : TextView
    private lateinit var  svBarcode: SurfaceView
    private  lateinit var detector: BarcodeDetector
    private  lateinit var cameraSource: CameraSource
    var codeStringImport :String?=null
    var size=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_items)
        svBarcode=findViewById(R.id.svBarcodeImport)
        tvImportBarcode=findViewById(R.id.tvImportBarcode)

        com.example.weiwenjie.sassistbot.myStoreRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //val value= dataSnapshot.getValue(Integer::class.java)
                if (dataSnapshot.exists()) {
                    size = dataSnapshot.childrenCount.toInt();
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })



        detector= BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val barcodes = detections?.detectedItems
                if(barcodes!!.size()>0){
                    val alert = AlertDialog.Builder(this@ImportItems)
                    tvImportBarcode.post{
                        codeStringImport=barcodes.valueAt(0).displayValue
                        tvImportBarcode.text= codeStringImport
                        if(codeStringImport!!.length==16){
                            var S:String
                            S=codeStringImport!!.substring(0,2)
                            if(S=="WM") S="Watermelon"
                            else if (S=="AP") S="Apple"
                            else if (S=="GP") S="Grape"
                            else if (S=="OR") S="Orange"
                            else if (S=="PR") S="Pear"
                            tvItem.text=S
                            S=codeStringImport!!.substring(3,6)
                            tvQuantity.text=S
                            S=codeStringImport!!.substring(7,codeStringImport!!.length)
                            tvDate.text=S

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
                if (ContextCompat.checkSelfPermission(this@ImportItems, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                    cameraSource.start(holder)
                else ActivityCompat.requestPermissions(this@ImportItems, arrayOf(android.Manifest.permission.CAMERA),123)
            }



        })

    }

    fun funImport(view: View) {
        val builder = AlertDialog.Builder(this@ImportItems)

        // Set the alert dialog title
        builder.setTitle("Import")

        // Display a message on alert dialog
        builder.setMessage("Are you want to Import Goods?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES"){dialog, which ->
            // Do something when user press the positive button
            if(size!=-1) {
                var myStoreRefImport=database.getReference("Store0/"+(size)+"/Item");
                myStoreRefImport.setValue(tvItem.text.toString())
                myStoreRefImport=database.getReference("Store0/"+(size)+"/Stock");
                myStoreRefImport.setValue((tvQuantity.text.toString().trim()).toInt())
                myStoreRefImport=database.getReference("Store0/"+(size)+"/Expire");
                myStoreRefImport.setValue(tvDate.text.toString())

                Toast.makeText(applicationContext, "Goods Imported", Toast.LENGTH_SHORT).show()
            }

        }


        // Display a negative button on alert dialog
        builder.setNegativeButton("No"){dialog,which ->
        }


        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()

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

}
