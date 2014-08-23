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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class ClassicByte extends Activity implements OnClickListener {
	
	public static ClassicByteView view;
	private String finish_action = "";
	public static String information_a = ""; //used to communicate over threads
	public static String information_b = ""; //used to communicate over threads
	public static Object information_c;      //used to communicate over threads
	private EditText dialog_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		//StrictMode.setThreadPolicy(policy);
		//long memory = Runtime.getRuntime().maxMemory()/1024/1024;
		//if(memory<64) {
			//VMRuntime.getRuntime().setMinimumHeapSize(128*1024*1024);
		//}
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //ActivityManager manager = (ActivityManager)this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        //manager.killBackgroundProcesses("");
        
		ClassicByteView mGLView = new ClassicByteView(this);
		this.setContentView(mGLView);
	}
	
	public void openInputRequester(String title, String message, boolean has_message, String finish_action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(title);
		if(has_message) {
			alertDialogBuilder.setMessage(message);
		}
		
		alertDialogBuilder.setCancelable(false).setPositiveButton("Ok",this);
		
		EditText input = new EditText(this);
		this.dialog_text = input;
		alertDialogBuilder.setView(input);
		
		alertDialogBuilder.create();
		alertDialogBuilder.show();
		this.finish_action = finish_action;
	}
	
	public AlertDialog.Builder openAlert(String title, String message, boolean has_message, String finish_action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(title);
		if(has_message) {
			alertDialogBuilder.setMessage(message);
		}
		
		alertDialogBuilder.setCancelable(false).setPositiveButton("Ok",this);
		this.dialog_text = null;
		
		alertDialogBuilder.create();
		alertDialogBuilder.show();
		this.finish_action = finish_action;
		return alertDialogBuilder;
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.classic_byte, menu);
		return true;
	}
	
	public void onBackPressed() {
		if(ClassicByteView.current_screen==null) {
			IngameRenderer.isPaused = !IngameRenderer.isPaused;
		} else {
			super.onBackPressed();
		}
		this.close();
	}
	
	public void onPause() {
		super.onPause();
		this.close();
	}
	
	public void onRestart() {
		super.onRestart();
		this.close();
	}
	
	public void onResume() {
		super.onResume();
		this.close();
	}
	
	public void close() {
		/*if(ClassicByte.view.renderer.networkManager.already_connected) {
			ClassicByte.view.renderer.networkManager.disconnect();
		}
		System.exit(0);*/
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode==KeyEvent.KEYCODE_VOLUME_UP && ClassicByte.view.renderer.networkManager.already_connected) {
				try { ClassicByte.view.renderer.networkManager.sendBlockPacket(0, 64, 0, Block.BLOCK_SPONGE); } catch(Exception e) {}
				return true;
			}
			if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN && ClassicByte.view.renderer.networkManager.already_connected){
				try { ClassicByte.view.renderer.networkManager.sendBlockPacket(0, 64, 0, Block.BLOCK_COBBLESTONE); } catch(Exception e) {}
				return true;
			}
			if(keyCode==KeyEvent.KEYCODE_BACK) {
				this.onBackPressed();
				return true;
			}
        return false;
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(this.finish_action.equals("ask_for_password")) {
			LoginManager.INSTANCE.username = this.dialog_text.getText().toString();
			this.openInputRequester("Enter your password", "", false, "login");
			return;
		}
		
		if(this.finish_action.equals("exit_app")) {
			System.exit(1);
		}
		
		if(this.finish_action.equals("login")) {
			LoginManager.INSTANCE.password = this.dialog_text.getText().toString();
			return;
		}
		
		if(this.finish_action.equals("b")) {
			ClassicByte.information_a = "";
			return;
		}
		
		if(this.finish_action.equals("send_chat") && ClassicByte.view.renderer.networkManager.already_connected) {
			String message = this.dialog_text.getText().toString();
			if(!message.equals("")) {
				try {
					ClassicByte.view.renderer.networkManager.sendChatPacket(message);
				} catch (Exception e) {}
			}
			return;
		}
	}

}