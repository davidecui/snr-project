package com.secureandclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author Davide Cui
 *
 */
public class SecureClient extends Activity {
	private OnClickListener myOcl;
	private Button confirm, exit;
	private EditText host, uname, pssw;
	private Intent menu;

    /** Called when the activity is first created. */
	/**
	 *     
	 */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
		menu = new Intent(this, Menu.class);
        
        host = (EditText) findViewById(R.id.EditHost);
        uname = (EditText) findViewById(R.id.EditUsername);
        pssw = (EditText) findViewById(R.id.EditPassword);
        confirm = (Button) findViewById(R.id.Confirm);
        exit = (Button) findViewById(R.id.Exit);

        myOcl = new OnClickListener() {
			// @Override
			public void onClick(View aView) {
				Button clickedButton = (Button) aView;

				int buttonNumber = clickedButton.getId();

				switch (buttonNumber) {
					case R.id.Confirm:
						 menu.putExtra("HOST", host.getText().toString());
						 menu.putExtra("UNAME", uname.getText().toString());
						 menu.putExtra("PSSW", pssw.getText().toString());
						 startActivity(menu);
						 break;

					case R.id.Exit:
						finish();
						break;
				}
			}
		};
		confirm.setOnClickListener(myOcl);
		exit.setOnClickListener(myOcl);
    }

}