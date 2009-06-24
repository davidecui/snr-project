package com.secureandclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author Davide Cui
 * 
 */
public class Menu extends Activity {
	private OnClickListener myOcl;
	private Button button01, button02, button03, buttonBye;
	private ImageView image_container;
	private SecureConnection connection;
	private BitmapDrawable image;

	/** Called when the activity is first created. */
	/**
	 *     
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent prev = getIntent();
		Bundle extras = prev.getExtras();
		
		connection = new SecureConnection(extras.getString("HOST"), extras.getString("UNAME"), extras.getString("PSSW"));
		if (connection.initialize()){
			image_container = (ImageView) findViewById(R.id.ImageView01);
			button01 = (Button) findViewById(R.id.Button01);
			button02 = (Button) findViewById(R.id.Button02);
			button03 = (Button) findViewById(R.id.Button03);
			buttonBye = (Button) findViewById(R.id.ButtonBye);
	
			myOcl = new OnClickListener() {
				// @Override
				public void onClick(View aView) {
					Button clickedButton = (Button) aView;
	
					int buttonNumber = clickedButton.getId();
	
					switch (buttonNumber) {
					case R.id.Button01:
						connection.secureSend("IMAGE01");
						break;
	
					case R.id.Button02:
						connection.secureSend("IMAGE02");
						break;
	
					case R.id.Button03:
						connection.secureSend("IMAGE03");
						break;
	
					case R.id.ButtonBye:
						connection.secureSend("Bye");
						finish();
						break;
					}
					if (buttonNumber != R.id.ButtonBye) {
						image = new BitmapDrawable((Bitmap) connection
								.secureReceive());
						image_container.setImageDrawable(image);
					}
				}
			};
			button01.setOnClickListener(myOcl);
			button02.setOnClickListener(myOcl);
			button03.setOnClickListener(myOcl);
			buttonBye.setOnClickListener(myOcl);
		}
		else{
			Toast.makeText(this, "Unable to open connection", 5);
			finish();
		}
	}
}
