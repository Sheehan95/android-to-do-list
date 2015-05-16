package com.placella.todo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class Synchronise {
	private static String response = "";
	private static Context context;
	private static List<Item> list;

	public static void start(Context context, List<Item> list) {
		Synchronise.context = context;
		Synchronise.list = list;
		new Thread(
			new Runnable() {
				public void run() {
					Synchronise.request();
				}
			}
		).start();
	}
	
	@SuppressWarnings("deprecation")
	private static synchronized void request() {
		Intent intent;
		PendingIntent pIntent;
		Notification msg;
		boolean success = put(createJson(list), context);
		if (! success) {
			intent = new Intent(context, ActivityNotification.class);
	        Bundle b = new Bundle();
	        b.putString("response", response);
	        b.putBoolean("success", false);
	        intent.putExtras(b);
		} else {
			System.out.println(response);
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(response));
		}
        pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        if (success) {
    		msg = new Notification(
	    		R.drawable.ic_menu_about,
	    		context.getResources().getString(R.string.to_do_sync_success),
	    		System.currentTimeMillis()
	        );
        	msg.setLatestEventInfo(
    			context,
    			context.getResources().getString(R.string.sync_success),
    			context.getResources().getString(R.string.link_to_notes),
    			pIntent
        	);
        } else {
    		msg = new Notification(
	    		R.drawable.ic_menu_about,
	    		context.getResources().getString(R.string.to_do_sync_failed),
	    		System.currentTimeMillis()
	        );
        	msg.setLatestEventInfo(
    			context,
    			context.getResources().getString(R.string.sync_failed),
    			"Tap here to see why",
    			pIntent
        	);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        msg.flags |= Notification.FLAG_AUTO_CANCEL;
        msg.defaults |= Notification.DEFAULT_SOUND;
        notificationManager.notify(0, msg);
	}
	
	private static boolean put(String data, Context context) {
		boolean success = true;
		try {
			HttpResponse httpResponse;
			URI url;
			url = new URI("http://todo.placella.com/");
			HttpClient client = new DefaultHttpClient();
			HttpPut put = new HttpPut(url);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("data", data));
			put.setEntity(new UrlEncodedFormEntity(pairs));
			httpResponse = client.execute(put);
			int statuscode = httpResponse.getStatusLine().getStatusCode();
			if (statuscode == 200) {
				HttpEntity responseEntity = httpResponse.getEntity();
				if (responseEntity != null) {
					try {
						response = EntityUtils.toString(responseEntity);
					} catch (Exception e) {
						success = false;
						response = e.toString();
					}
				} else {
					success = false;
					response = context.getResources().getString(R.string.decode_failure);
				}
			} else {
				success = false;
                response = String.format(context.getResources().getString(R.string.http_error), statuscode);
			}
		} catch (Exception e) {
			success = false;
			response = e.toString();
		}
		return success;
	}
	
	private static String createJson(List<Item> list) {
    	JSONArray root = new JSONArray();
        try {
        	for (Item i : list) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("name", i.getName());
                itemObj.put("type", i.getType());
                itemObj.put("note", i.getNotecontent());
            	JSONArray array = new JSONArray();
            	for (Item inner : i.getListcontent()) {
                    JSONObject innerObj = new JSONObject();
            		innerObj.put("txt", inner.getName());
            		innerObj.put("sel", inner.getState());
            		array.put(innerObj);
            	}
            	itemObj.put("list", array);
            	root.put(itemObj);
        	}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return root.toString();
	}
}
