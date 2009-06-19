package com.secureandclient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SecureClient extends Activity {
	private OnClickListener myOcl;
	private Button button01, button02, button03;
	private ImageView image_container;
	private SecureConnection connection;
	private BitmapDrawable image;
	private Thread imgLdr = new Thread(new ImageLoader());

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
    	connection = new SecureConnection();
    	connection.initialize();

    	setContentView(R.layout.main);
    	image_container = (ImageView) findViewById(R.id.ImageView01);
    	button01 = (Button) findViewById(R.id.Button01);
    	button02 = (Button) findViewById(R.id.Button02);
    	button03 = (Button) findViewById(R.id.Button03);
    	
		myOcl = new OnClickListener() {
			// @Override
			public void onClick(View aView) {
				Button clickedButton = (Button) aView;

				int buttonNumber = clickedButton.getId();

				switch (buttonNumber) {
					case R.id.Button01: 
						//Log.d("SECURECLIENT", "01");
			        	connection.secureSend("IMAGE01");
//			        	imgLdr.start();
						break;
				
					case R.id.Button02: 
						//Log.d("SECURECLIENT", "02");
			        	connection.secureSend("IMAGE02");
						break;
			
					case R.id.Button03: 
						//Log.d("SECURECLIENT", "03");
			        	connection.secureSend("IMAGE03");
						break;
				}
	        	image = new BitmapDrawable((Bitmap) connection.secureReceive());
				image_container.setImageDrawable(image);
			}
		};
		button01.setOnClickListener(myOcl);
		button02.setOnClickListener(myOcl);
		button03.setOnClickListener(myOcl);
    }
    
	private class ImageLoader implements Runnable {
		// @Override
		public void run() {
        	image = (BitmapDrawable) connection.secureReceive();
			image_container.setImageDrawable(image);
		}
	}

}