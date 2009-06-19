package com.secureandclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Menu extends Activity {
    private OnClickListener myOcl;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		myOcl = new OnClickListener() {
			// @Override
			public void onClick(View aView) {
				Button clickedButton = (Button) aView;

				int buttonNumber = clickedButton.getId();

				switch (buttonNumber) {
				case R.id.Button01:
					break;
				case R.id.Button02:
					break;
				case R.id.Button03:
					break;
				}
			}
		};

    
    }

}
