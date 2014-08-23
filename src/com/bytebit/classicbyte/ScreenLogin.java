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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ScreenLogin extends Screen {
	
	private int current_button = 0;
	private boolean init = true;

	public ScreenLogin(ClassicByteView v) {
		super(v);
		SharedPreferences sharedPref = ((ClassicByte)ClassicByte.view.getContext()).getPreferences(Context.MODE_PRIVATE);
		LoginManager.INSTANCE.username = sharedPref.getString("username", "Steve");
		LoginManager.INSTANCE.password = sharedPref.getString("password","1234");
		
	}
	
	public void draw(Canvas c) {
		if(this.init) {
			SharedPreferences sharedPref = ((ClassicByte)ClassicByte.view.getContext()).getPreferences(Context.MODE_PRIVATE);
			if(!sharedPref.contains("notfirstrun")) {
				ClassicByte.information_a = "Tutorial";
				ClassicByte.information_b = "Welcome to ClassicByte, the android port of classicube! Please insert your login information to go on.";
				Runnable f = new Runnable() {
		            public void run() {
		        		((ClassicByte)ClassicByte.view.getContext()).openAlert(ClassicByte.information_a, ClassicByte.information_b, true, "just_close");
		            }
		        };
				((ClassicByte)this.parent.getContext()).runOnUiThread(f);
			}
			this.init = false;
		}
		this.parent.standard_paint.setColor(Color.WHITE);
		
	    for(int i=0;i!=9;i++) {
	    	for(int k=0;k!=10;k++) {
	    		c.drawBitmap(TextureManager.getBitmap(3), k*(this.parent.renderer.width/10), (i+1)*(this.parent.renderer.width/10), this.parent.standard_paint);
	    	}
	    }
	    
	    for(int k=0;k!=10;k++) {
	    	c.drawBitmap(TextureManager.getBitmap(2), k*TextureManager.getBitmap(2).getWidth(), 0, this.parent.standard_paint);
	    }
	    
	    c.drawBitmap(TextureManager.getBitmap(4), this.parent.renderer.width*0.05F, 0, this.parent.standard_paint);
	    
	    this.parent.standard_paint.setTextSize(this.parent.renderer.width*0.04F);
	    
	    //long memory = Runtime.getRuntime().maxMemory()/1024/1024;
	    
		c.drawBitmap(TextureManager.getBitmap(13), (this.parent.renderer.width-TextureManager.getBitmap(13).getWidth())/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F, this.parent.standard_paint);
		c.drawText("Username: "+LoginManager.INSTANCE.username, (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.18F, this.parent.standard_paint);
		c.drawBitmap(TextureManager.getBitmap(13), (this.parent.renderer.width-TextureManager.getBitmap(13).getWidth())/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*1.1F, this.parent.standard_paint);
		String password = "";
		for(int k=0;k!=LoginManager.INSTANCE.password.length();k++) {
			password = password + "*";
		}
		c.drawText("Password: "+password, (this.parent.renderer.width-TextureManager.getBitmap(10).getWidth())/2+this.parent.renderer.width*0.02F, this.parent.renderer.width*0.08F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*1.1F, this.parent.standard_paint);
		this.parent.standard_paint.setColor(Color.RED);
		c.drawText("ANDROID PRE-RELEASE 0.0.1 BY BYTEBIT",0,this.parent.renderer.width*0.04F,this.parent.standard_paint);
		this.parent.standard_paint.setColor(Color.WHITE);
		if(this.current_button==1) {
			c.drawBitmap(TextureManager.getBitmap(16), (this.parent.renderer.width-TextureManager.getBitmap(13).getWidth())/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F, this.parent.standard_paint);
		} else {
			c.drawBitmap(TextureManager.getBitmap(14), (this.parent.renderer.width-TextureManager.getBitmap(13).getWidth())/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F, this.parent.standard_paint);
		}
		if(this.current_button==2) {
			c.drawBitmap(TextureManager.getBitmap(17), this.parent.renderer.width/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F, this.parent.standard_paint);
		} else {
			c.drawBitmap(TextureManager.getBitmap(15), this.parent.renderer.width/2, this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F, this.parent.standard_paint);
		}
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager)this.parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	public void onTouch(int x, int y, boolean up, boolean down) {
		
		if(up && !down && !this.isOnline()) {
			((ClassicByte)this.parent.getContext()).openAlert("Sorry", "ClassicByte is unable to connect to the internet. Please check your internet connection!", true, "just_close");
			return;
		}
		
		if(up && !down && x>(this.parent.renderer.width-TextureManager.getBitmap(13).getWidth())/2 && y>this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F && x<(this.parent.renderer.width-TextureManager.getBitmap(13).getWidth())/2+TextureManager.getBitmap(14).getWidth() && y<this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F+TextureManager.getBitmap(13).getHeight()) {
			if(LoginManager.INSTANCE.username.equalsIgnoreCase("MasterHunter115")) {
				((ClassicByte)this.parent.getContext()).openAlert("Login failed", "You're on the global ban list. Beg one of the admins to pardon you.", true, "exit_app");
			} else {
				boolean correct = LoginManager.INSTANCE.login(LoginManager.INSTANCE.username, LoginManager.INSTANCE.password);
				if(!correct) {
					((ClassicByte)this.parent.getContext()).openAlert("Sorry", "Your login information was not correct. Try again!", true, "just_close");
				} else {
					SharedPreferences sharedPref = ((ClassicByte)ClassicByte.view.getContext()).getPreferences(Context.MODE_PRIVATE);
					Editor edit = sharedPref.edit();
					edit.putString("username", LoginManager.INSTANCE.username);
					edit.putString("password", LoginManager.INSTANCE.password);
					this.parent.setScreen(new ScreenMainMenu(this.parent));
					if(!sharedPref.contains("notfirstrun")) {
						((ClassicByte)ClassicByte.view.getContext()).openAlert("Tutorial", "You're now seeing the serverlist in the background. To join a server just touch it and wait for the map to load! On the upper right corner are important buttons. That one with the computer is for the serverlist, the other one's for game options. We wish you fun while playing!", true, "just_close");
						edit.putString("notfirstrun", "true");
					} else {
						((ClassicByte)ClassicByte.view.getContext()).openAlert("", "Brought to you by ByteBit", true, "just_close");
					}
					edit.commit();
				}
			}
		}
		
		if(up && !down && x>this.parent.renderer.width/2 && y>this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F && x<this.parent.renderer.width/2+TextureManager.getBitmap(14).getWidth() && y<this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F+TextureManager.getBitmap(13).getHeight()) {
			this.parent.getUserInput("Enter your username", "", false, "ask_for_password");
		}
		int x1 = 0;
		if(x>(this.parent.renderer.width-TextureManager.getBitmap(13).getWidth())/2 && y>this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F && x<(this.parent.renderer.width-TextureManager.getBitmap(13).getWidth())/2+TextureManager.getBitmap(14).getWidth() && y<this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F+TextureManager.getBitmap(13).getHeight()) {
			x1 = 1;
		}
		if(x>this.parent.renderer.width/2 && y>this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F && x<this.parent.renderer.width/2+TextureManager.getBitmap(14).getWidth() && y<this.parent.renderer.width*0.02F+this.parent.renderer.width*0.18F+TextureManager.getBitmap(13).getHeight()*2.2F+TextureManager.getBitmap(13).getHeight()) {
			x1 = 2;
		}
		
		this.current_button = x1;
	}

}
