package com.nathanwong.teeessarr;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TeeEssArr extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		String username = "username";
		String password = "password";
		
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
		
		String postData = "vb_login_username=" + username + "&vb_login_md5password=" + hash.toString() +
							"&vb_login_password=&vb_login_md5password_utf=" + hash.toString() + 
							"&do=login&url=signin.php&securitytoken=guest&s=";
		
		WebView TSRView = (WebView) findViewById(R.id.webview);
		
		TSRView.setWebViewClient(new WebViewClient());
		
		TSRView.postUrl("http://www.thestudentroom.co.uk/login.php?do=login", EncodingUtils.getBytes(postData, "BASE64"));

		WebSettings tsrSettings = TSRView.getSettings();
		tsrSettings.setJavaScriptEnabled(true);

	}
	
}