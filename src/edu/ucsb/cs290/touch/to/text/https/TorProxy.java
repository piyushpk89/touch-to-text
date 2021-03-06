package edu.ucsb.cs290.touch.to.text.https;

import info.guardianproject.onionkit.trust.StrongHttpsClient;

import java.io.IOException;
import java.io.Serializable;
import java.security.cert.CertificateException;

import ch.boye.httpclientandroidlib.entity.ByteArrayEntity;

import android.content.Context;
import android.util.Log;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.conn.params.ConnRoutePNames;
import edu.ucsb.cs290.touch.to.text.R;
import edu.ucsb.cs290.touch.to.text.remote.Helpers;
import edu.ucsb.cs290.touch.to.text.remote.messages.TokenAuthMessage;
import edu.ucsb.cs290.touch.to.text.remote.register.RegisterUser;

public class TorProxy {

	public static void postThroughTor(Context c, TokenAuthMessage tm) throws CertificateException {
		executeHttpsPost(c, TokenAuthMessage.FIELD_NAME, tm);
	}

	public static void postThroughTor(Context c, RegisterUser ru) throws CertificateException {
		executeHttpsPost(c, RegisterUser.FIELD_NAME, ru);
	}

	private static void executeHttpsPost(Context c, String name, Serializable value) throws CertificateException{
		StrongHttpsClient http = new StrongHttpsClient(c);
		Log.d("touch-to-text","Registering with server");
		http.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				new HttpHost("localhost", 8118));
		http.getParams().setParameter("http.useragent", "No specific user agent info!");

		HttpPost method = new HttpPost("https://"
				+ c.getString(R.string.service_url) + "/" + name);
		
		HttpResponse response;
		try {
			method.setEntity(new ByteArrayEntity(Helpers.serialize(value)));
			response = http.execute(method);
			Log.d("HTTPtor", response.getStatusLine().getStatusCode()+"");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
