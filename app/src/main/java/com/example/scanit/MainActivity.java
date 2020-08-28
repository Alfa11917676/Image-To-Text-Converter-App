package com.example.scanit;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity {
        private  static  final  String File_Name="COnvertedImage.txt";
        private TextView textView2;
    private EditText editText;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;
    private  String stringResult =null;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView2=findViewById(R.id.textView2);



        ActivityCompat.requestPermissions(this,new String[]{CAMERA}, PackageManager.PERMISSION_GRANTED);

        textToSpeech= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });

    }

    protected void onDestroy(){
        super.onDestroy();
        cameraSource.release();
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();

    }

    private void textRecognizer(){
        textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();
        cameraSource=new CameraSource.Builder(getApplicationContext(),textRecognizer)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1080,720)
                .build();
        surfaceView=findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(surfaceView.getHolder());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                SparseArray<TextBlock> sparseArray=detections.getDetectedItems();
                StringBuilder stringBuilder=new StringBuilder();

                for (int i = 0; sparseArray.size() > i; i++){
                    TextBlock textBlock=sparseArray.valueAt(i);
                    if(textBlock!=null && textBlock.getValue() !=null){
                        stringBuilder.append(textBlock.getValue()+" ");
                    }
                }
                final String string=stringBuilder.toString();
                Handler handler= new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stringResult=string;
                        resultObt();

                    }
                });
            }
        });

    }

    private void resultObt(){

        setContentView(R.layout.activity_main);

        editText =findViewById(R.id.textView);
        editText.setText(stringResult);
        textToSpeech.speak(stringResult,TextToSpeech.QUEUE_FLUSH,null,null);
        //pdfCreator();
    }

    public void buttonStart(View view){
        setContentView(R.layout.surfaceviewer);
        textRecognizer();

    }
//ACTIVATE THIS LINE TO GET THE TEXT SAVED IN YOUR DEVICE
//    public void pdfCreator(){
//        String text=stringResult;
//        FileOutputStream fos=null;
//        try {
//            fos =openFileOutput(File_Name,MODE_PRIVATE);
//            fos.write(text.getBytes());
//            editText.getText().clear();
//            Toast.makeText(this,  "Saved to    " + getExternalFilesDir(MediaStore.Downloads.CONTENT_TYPE),
//                    Toast.LENGTH_LONG).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(fos!=null){
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


}

