package com.project.zomato;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class CouponValidityTask extends AsyncTask<String,Void,String> {
	
	private Context context;
	private String beaconId;
	private String userId;
	private String user_query_url="http://10.0.1.69/query.php";
	private String coupon_url = "http://10.0.1.69/query_beacon.php";
	private Activity myactivity;
	private WebView webview;
	
	public CouponValidityTask(Context context,String beaconId,String userId,Activity activity,WebView webview){
		this.context = context;
		this.beaconId = beaconId;
		this.userId = userId;
		this.myactivity = activity;
		this.webview = webview;
	}

	@Override
	protected String doInBackground(String... params) {
		String _url = user_query_url + "?user=" + userId + "&&device=" + beaconId;
		URL url;
		String content = null;
		try {
			url = new URL(_url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			//conn.setReadTimeout(10000 /* milliseconds */);
			//conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.connect();
			InputStream is = conn.getInputStream();
			content = readIt(is);
			Log.d("READWRITE",content);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
	    String res="";
	    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
	    res = br.readLine();
	    return res;
	}
	
	@Override
    protected void onPostExecute(String result) {
        if(result.startsWith("TRUE")){
        	String couponUrl = coupon_url + "?bid=" + beaconId;
        	new FetchCouponData(context,myactivity,webview).execute(couponUrl);
        }
   }

}
