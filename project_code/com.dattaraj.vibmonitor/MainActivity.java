package com.dattaraj.vibmonitor;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

public class MainActivity extends Activity  implements SensorEventListener, Runnable {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	public static int DELAY = 40;
	public final static float G = 9.81f;
	public static int fftN = 256;

	private byte axis = 3; // 1 - X, 2 - Y and 3 - Z
	private float bufferX[], bufferY[], bufferZ[];
	private float currentX = 0.0f;
	private float currentY = 0.0f;
	private float currentZ = 0.0f;
	
	private boolean pause = false;
	
	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 
	private DrawView myDraw;
	
	private int FILE_COUNT = 0;
	
	/** Called when the activity is first created. */

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        FILE_COUNT = 1;
        
        bufferX = new float[fftN];
        bufferY = new float[fftN];
        bufferZ = new float[fftN];

        LinearLayout layout = (LinearLayout) findViewById(R.id.chartBlock);
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.chartBlock2);

        myDraw = new DrawView(this);
        layout.addView(myDraw.getmChart());
        layout2.addView(myDraw.getFftChart());
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
        // Start the Thread
        Thread t = new Thread(this);
        t.start();

        // Set window to not sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
        	      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        

        // START Multi media advertisment
        MMSDK.initialize(this);

		MMAdView adView = new MMAdView(this);
		adView.setApid("152490");
		
		//Set your metadata in the MMRequest object
		MMRequest request = new MMRequest();
		
		//Add metadata here.
		request.put("key", "vibration");
		request.put("subject", "vibration");
		
		//Add the MMRequest object to your MMAdView.
		adView.setMMRequest(request);
		
		//Sets the id to preserve your ad on configuration changes.
		adView.setId(MMSDK.getDefaultAdId());

		//Get layout to show ad
		RelativeLayout adRelativeLayout = (RelativeLayout) findViewById(R.id.adBlock);

		// Show ad
		adRelativeLayout.addView(adView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		adView.getAd();		
		
		// END Multi media advertisment
        
        // Code for radio button selection
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.frequencyRadio);        
        	radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	        {
	            public void onCheckedChanged(RadioGroup group, int checkedId) {
	                // checkedId is the RadioButton selected
	            	switch(checkedId) {
	            	case R.id.radio25:
	            		DELAY = 1000/25;
	            		break;
	            	case R.id.radio50:
	            		DELAY = 1000/50;
	            		break;
	            	case R.id.radio100:
	            		DELAY = 1000/100;
	            		break; 
	            	case R.id.radio250:
	            		DELAY = 1000/250;
	            		break;
	            	}
	            }
	        });
    	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /** Called when the user clicks the Send button */
    public void changeAxes(View view) {
    	TextView editText = (TextView) findViewById(R.id.axisLabel);
    	
    	axis++;
    	if(axis > 3)
    		axis = 1;
    	
    	switch(axis) {
    	case 1:
        	editText.setText("X-Axis");
        	break;
    	case 2:
        	editText.setText("Y-Axis");
        	break;
    	case 3:
        	editText.setText("Z-Axis");
        	break;
    	}
    }
    
    /** Called when the user clicks the HELP button */
    public void showHelp(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);
/*
        // Standard Intent action that can be sent to have the camera
        // application capture an image and return it.  
         
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
         
         // intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
          
         intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
          
        startActivityForResult( intent, 1);
        
     /*************************** Camera Intent End ************************/
    }
    
    /** Called when the user clicks the PAUSE button */
    public void doPause(View view) {
    	pause = !pause;
    	
    	Button pauseB = (Button) findViewById(R.id.button_pause);
    	pauseB.setText(pause?"START":"PAUSE");
    }

    /** Called when the user clicks the SAVE button */
    public void doSave(View view) {
        String strDir = Environment.getExternalStorageDirectory() + "/vibanalyzer";
        String file1 = "vibchart" + FILE_COUNT + ".png";
        String file2 = "fftchart" + FILE_COUNT + ".png";
        
        myDraw.getmChart().setDrawingCacheEnabled(true);
        myDraw.getFftChart().setDrawingCacheEnabled(true);
        
        saveImageToLocalStore(strDir, myDraw.getmChart().getDrawingCache(), file1);
        saveImageToLocalStore(strDir, myDraw.getFftChart().getDrawingCache(), file2);
    	showAlert("Images of charts saved in " + strDir + ". With file counter - " + FILE_COUNT + ".");
    	
    	FILE_COUNT++;
    }

    protected void onResume() {
    	super.onResume();
    	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    	
    }
    
    
    protected void onPause() {
    	super.onPause();
    	mSensorManager.unregisterListener(this);
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	// can be safely ignored for this demo
    }

    @Override
    public void onSensorChanged(SensorEvent event) {    
    	currentX = event.values[0];
    	currentY = event.values[1];
    	
    	// For Z axis subtract G value
    	currentZ = event.values[2] - G;
    }

  
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(;;) {
			
			if(!pause) {
				for(int i=0;i<fftN-1;i++) {
					bufferX[i] = bufferX[i+1];
					bufferY[i] = bufferY[i+1];
					bufferZ[i] = bufferZ[i+1];
				}
				
				bufferX[fftN-1] = currentX;
				bufferY[fftN-1] = currentY;
				bufferZ[fftN-1] = currentZ;
				
		    	switch(axis) {
		    	case 1:
					myDraw.setData(bufferX);
		        	break;
		    	case 2:
					myDraw.setData(bufferY);
		        	break;
		    	case 3:
					myDraw.setData(bufferZ);
		        	break;
		    	}
	
		    	myDraw.getmChart().postInvalidate();
		    	myDraw.getFftChart().postInvalidate();
			}
			
			try {
				Thread.sleep(DELAY);
			} catch(Exception exp) {
				// Do nothing for now
			}
		}
	}
	
	// Save image to local storage
	private void saveImageToLocalStore(String strDir, Bitmap finalBitmap, String fname) { 
        File myDir = new File(strDir);    
        myDir.mkdirs(); 
        File file = new File (myDir, fname);
        if (file.exists()) file.delete(); 
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close(); 
        } catch (Exception e) {
           	e.printStackTrace();
           	showAlert(e.toString());
        }
    }
	
	private void showAlert(String strDir) {
        // Show dialog alert
       new AlertDialog.Builder(this)
        .setTitle("Images saved!")
        .setMessage(strDir)
        .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            }
         })
         .show();        
	}

}
