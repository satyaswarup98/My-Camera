package com.example.cam;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.hardware.Camera;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);


        camera = Camera.open();
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);

        }


    public static void saveFrameLayout(FrameLayout frameLayout, String path) {
        frameLayout.setDrawingCacheEnabled(true);
        frameLayout.buildDrawingCache();
        Bitmap cache = frameLayout.getDrawingCache();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            cache.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            frameLayout.destroyDrawingCache();
        }
    }
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture_file = getOutputMediaFile();
            if(picture_file == null){
                return;
            }
            else {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(picture_file);
                    fileOutputStream.write(data);
                    fileOutputStream.close();

                    camera.startPreview();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private File getOutputMediaFile(){
        String state = Environment.getExternalStorageState();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return  null;
        }
        else{
            File MyCam = new File(Environment.getExternalStorageDirectory() + File.separator + "MyCamera");
            if(!MyCam.exists()){
                MyCam.mkdir();
            }

            File outputFile = new  File(MyCam,"IMG_"+ timeStamp + ".jpg");
            return outputFile;
        }
    }

    public void captureImage(View v){
        if(camera!=null){
            camera.takePicture(null,null,mPictureCallback);
            ImageView imageView = (ImageView) findViewById(R.id.img);
           saveFrameLayout(frameLayout,File.separator + "MyCamera2");

        }
    }

}
