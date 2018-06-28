package com.example.weiwenjie.sassistbot

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_import_items.*

class ImportItems : AppCompatActivity() {
    private  lateinit var  tvImportBarcode : TextView
    private lateinit var  svBarcode: SurfaceView
    private  lateinit var detector: BarcodeDetector
    private  lateinit var cameraSource: CameraSource
    var codeString :String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_items)
        svBarcode=findViewById(R.id.svBarcodeImport)
        tvImportBarcode=findViewById(R.id.tvImportBarcode)

        detector= BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val barcodes = detections?.detectedItems
                if(barcodes!!.size()>0){
                    tvImportBarcode.post{
                        codeString=barcodes.valueAt(0).displayValue
                        tvImportBarcode.text= codeString

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
