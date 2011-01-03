package com.nathanwong.teeessarr;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class TeeEssArr extends Activity {
	
	String username;
	String password_hash;
	
	static final int DIALOG_LOGIN = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		SharedPreferences settings = getPreferences(0);
		username = settings.getString("username", "");
		password_hash = settings.getString("password", "");

		if (username == "" || password_hash == "") {
			showDialog(0);
		}
		
		attemptLogin(username, password_hash);
		
		
		
		final WebView tsrView = (WebView) findViewById(R.id.webview);
		
		tsrView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				tsrView.loadUrl("javascript:(function () {" +
						"$('table:first').hide();" +
						"$('#main_menu').hide();" +
						"$('#subnavigation').hide();" +
						"$('#gutter_content').hide();" +
						"$('#breadcrumb').hide();" +
						"$('.widget_sidebar_toggler').hide();" +
						"$('#primary_content>*').not('.chatbox').hide();" +
						"$('#mgc_cb_evo_opened .alt1:first').hide();" +
						"$('.chatbox *').css('background-color','white');" +
					"})()");
			}
		});
		
		
		WebSettings tsrSettings = tsrView.getSettings();
		tsrSettings.setJavaScriptEnabled(true);

	}

	private void attemptLogin(String try_username, String try_passwordHash) {
		final WebView tsrView = (WebView) findViewById(R.id.webview);
	
		String postData = "vb_login_username=" + try_username + "&vb_login_md5password=" + try_passwordHash +
							"&vb_login_password=&vb_login_md5password_utf=" + try_passwordHash + 
							"&do=login&url=forumdisplay.php?f=91&securitytoken=guest&s=";
		
		tsrView.postUrl("http://www.thestudentroom.co.uk/login.php?do=login", EncodingUtils.getBytes(postData, "BASE64"));

	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_LOGIN:
			dialog = createLoginPrompt();
		default:
			dialog = null;
		}
		return dialog;
	}

	private Dialog createLoginPrompt() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		
		LayoutInflater inflater = (LayoutInflater) TeeEssArr.this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.login_dialog, (ViewGroup) findViewById(R.id.login_dialog_root));
		
		final EditText usernamefield = (EditText) layout.findViewById(R.id.login_dialog_username);
		final EditText passwordfield = (EditText) layout.findViewById(R.id.login_dialog_password);
		
		usernamefield.setText(username);
		
		builder = new AlertDialog.Builder(TeeEssArr.this);
		builder.setView(layout);
		builder.setTitle("Login details");
		
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				username = usernamefield.getText().toString();
				String password = passwordfield.getText().toString();
				
				password_hash = getHash(password);
				
				SharedPreferences settings = getPreferences(0);
				SharedPreferences.Editor editor = settings.edit();
				
				editor.putString("username", username);
				editor.putString("password", password_hash);
				
			}
		});
		
		alertDialog = builder.create();
		
		return alertDialog;
	}
	
	private String getHash(String password) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		md.update(password.getBytes());
		byte[] theDigest = md.digest();
		
		StringBuffer hash = new StringBuffer();
		
		for(int i=0; i<theDigest.length; i++) {
			hash.append(Integer.toHexString(0xFF & theDigest[i]));
		}
		
		return hash.toString();
	}
	
}