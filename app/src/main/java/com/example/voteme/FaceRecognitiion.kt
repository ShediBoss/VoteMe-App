package com.example.voteme

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.voteme.Helper.RectOverlay
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_face_recognitiion.*


class FaceRecognitiion : AppCompatActivity() {

    lateinit var waitingDialog: android.app.AlertDialog


    override fun onResume() {
        super.onResume()
        camera_view.start()
        camera_view.captureImage()
        camera_view.isFacingFront
        camera_view.clipBounds

    }

    override fun onPause() {
        super.onPause()
        camera_view.stop()
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognitiion)
        waitingDialog = SpotsDialog.Builder().setContext(this)
            .setMessage("Please wait....")
            .setCancelable(false)
            .build()



        btn_detect.setOnClickListener {
            camera_view.start()
            graphic_overlay.clear()

        }

        camera_view.addCameraKitListener(object : CameraKitEventListener {
            override fun onEvent(p0: CameraKitEvent?) {

            }

            override fun onError(p0: CameraKitError?) {

            }

            override fun onImage(p0: CameraKitImage?) {
                waitingDialog.show()
                var bitmap = p0!!.bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap,camera_view.width,camera_view.height,false)
                camera_view.stop()

                runFaceDetector(bitmap)
            }



            override fun onVideo(p0: CameraKitVideo?) {

            }

        })

    }

    private fun runFaceDetector(bitmap: Bitmap?) {
        val image = FirebaseVisionImage.fromBitmap(bitmap!!)
        val options = FirebaseVisionFaceDetectorOptions.Builder().build()
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        detector.detectInImage(image)
            .addOnSuccessListener { result -> processFaceResult(result) }
            .addOnFailureListener { e -> Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
    }

    private fun processFaceResult(result: List<FirebaseVisionFace>) {
        var count = 0;
        for (face in result)
        {
            val bounds = face.boundingBox

            val rectOverlay = RectOverlay(graphic_overlay,bounds)
            graphic_overlay.add(rectOverlay)

            count++
            waitingDialog.dismiss()
            startActivity(Intent(this,HomeActivity::class.java))
        }
        Toast.makeText(this,String.format("Detected %d faces in picture",count),Toast.LENGTH_SHORT).show()

    }


//    override fun onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
}