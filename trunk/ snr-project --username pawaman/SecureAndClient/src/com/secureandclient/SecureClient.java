package com.secureandclient;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SecureClient extends Activity {
	private OnClickListener myOcl;
	private ImageView image_container;
	private SecureConnection connection;
	private BitmapDrawable image;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
    	connection = new SecureConnection();
    	connection.initialize();

    	setContentView(R.layout.main);
    	image_container = (ImageView) findViewById(R.id.ImageView01);
    	
		myOcl = new OnClickListener() {
			// @Override
			public void onClick(View aView) {
				Button clickedButton = (Button) aView;

				int buttonNumber = clickedButton.getId();

				switch (buttonNumber) {
					case R.id.Button01: 
			        	connection.secureSend("IMAGE 01");
						break;
				
					case R.id.Button02: 
			        	connection.secureSend("IMAGE 02");
						break;
			
					case R.id.Button03: 
			        	connection.secureSend("IMAGE 03");
						break;
					
				}
	        	//image = (BitmapDrawable) connection.secureReceive();
	    		//image_container.setImageDrawable(image);

			}
		};
    }
}