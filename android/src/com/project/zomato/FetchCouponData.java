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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class FetchCouponData extends AsyncTask<String,Void,String> {
	
	private Context context;
	private Activity activity;
	private WebView webview;
	
	public FetchCouponData(Context context,Activity activity,WebView webview){
		this.context = context;
		this.activity = activity;
		this.webview = webview;
	}

	@Override
	protected String doInBackground(String... params) {
		URL url;
		String content = null;
		try {
			url = new URL(params[0]);
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
        if(!result.isEmpty()){
        	final String values[] = result.split("_");
        	Log.d("COP",result);
        	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Congratulations!!");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(values[1]);
            builder.setPositiveButton("Ok",null);
            final AlertDialog alert = builder.create();
            activity.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					webview.loadUrl(values[0]);
					alert.show();
				}
                
            });
        }
	}

}
