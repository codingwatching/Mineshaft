/*
Ace of Spades remake
Copyright (C) 2014 ByteBit

This program is free software; you can redistribute it and/or modify it under the terms of
the GNU General Public License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program;
if not, see <http://www.gnu.org/licenses/>.
*/


package com.bytebit.classicbyte;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class LoginManager {
	public static LoginManager INSTANCE = new LoginManager();
	public String username = "Steve";
	public String password = "1234";
	public HttpClient client;
	public boolean logged_in = false;
	
	public void logout() {
		HttpGet request = new HttpGet("http://www.classicube.net/acc/logout/");
		try {
			client.execute(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.username = "Steve";
		this.password = "1234";
		this.logged_in = false;
	}
	
	public boolean login(String username, String password) {
		
		if(this.logged_in) {
			this.logout();
		}
		
		this.username = username;
		this.password = password;
		this.client = new DefaultHttpClient();
		String csrf_token = "";
		try {
			HttpGet request = new HttpGet("http://www.classicube.net/acc/login");
			HttpResponse response = client.execute(request);

			String html = "";
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
			{
			    str.append(line);
			}
			in.close();
			html = str.toString();
			csrf_token = html.substring(html.indexOf("type=\"hidden\" value=\"")+21,html.indexOf("\">",html.indexOf("type=\"hidden\" value=\"")));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	    HttpPost httppost = new HttpPost("http://www.classicube.net/acc/login");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	        nameValuePairs.add(new BasicNameValuePair("username", username));
	        nameValuePairs.add(new BasicNameValuePair("password", password));
	        nameValuePairs.add(new BasicNameValuePair("csrf_token", csrf_token));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = client.execute(httppost);
	        
	        String html = "";
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
			{
			    str.append(line);
			}
			in.close();
			html = str.toString();
			if(html.contains("This field is required") || html.contains("loginForm")) {
				this.logged_in = false;
				return false;
			}
	        
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    this.logged_in = true; 
	    return true;
	}
}
