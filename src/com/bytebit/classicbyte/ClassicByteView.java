/*
Mineshaft
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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class ClassicByteView extends GLSurfaceView implements Runnable {
	
	public ClassicByteRenderer renderer = new ClassicByteRenderer();
	public Paint standard_paint = new Paint();
	public static Screen current_screen = null;
	public int movement_pointer_center_x = 0;
	public int movement_pointer_center_y = 0;
	
	public int look_pointer_center_x = 0;
	public int look_pointer_center_y = 0;
	
	public int last_touch_x = 0;
	public int last_touch_y = 0;
	
	public static AbstractList<EventData> pointers = new ArrayList<EventData>();
	
	private long touch_update_timer = System.currentTimeMillis();
	
	public ClassicByteView(Context context) {
        super(context);
        
        this.setRenderer(renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        
        this.renderer.setView(this);
        
        ClassicByte.view = this;
        Thread t = new Thread(this);
        Thread.setDefaultUncaughtExceptionHandler(Logger.getExceptionHandler());
        t.setUncaughtExceptionHandler(Logger.getExceptionHandler());
        t.start();
    }
	
	public void getUserInput(String title, String message, boolean has_message, String finish_action) {
		((ClassicByte)this.getContext()).openInputRequester(title, message, has_message, finish_action);
	}
	
	public void setScreen(Screen s) {
		ClassicByteView.current_screen = s;
	}
	
	public int getAsPositive(int x) {
		if(x<0) {
			return -x;
		}
		return x;
	}
	
	public Map<Integer, EventData> eventDataMap = new HashMap<Integer, EventData>();
	
	public boolean onTouchEvent(MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_UP && ClassicByteView.current_screen!=null) {
				ClassicByteView.current_screen.onTouch(Math.round(event.getX()), Math.round(event.getY()), true, false);
			}
			if(event.getAction()==MotionEvent.ACTION_DOWN && ClassicByteView.current_screen!=null) {
				ClassicByteView.current_screen.onTouch(Math.round(event.getX()), Math.round(event.getY()), false, true);
			}
			if(event.getAction()==MotionEvent.ACTION_MOVE && ClassicByteView.current_screen!=null) {
				ClassicByteView.current_screen.onTouch(Math.round(event.getX()), Math.round(event.getY()), false, false);
			}
			this.last_touch_x = (int)event.getX();
			this.last_touch_y = (int)event.getY();
			
			int action = event.getActionMasked();

		    int pointerIndex = event.getActionIndex();
		    int pointerId = event.getPointerId(pointerIndex);

		    switch (action) {
		    case MotionEvent.ACTION_DOWN:
		    case MotionEvent.ACTION_POINTER_DOWN:
		        EventData eventData = new EventData();
		        eventData.x = event.getX(pointerIndex);
		        eventData.y = event.getY(pointerIndex);
		        eventData.x_start = eventData.x;
		        eventData.y_start = eventData.y;
		        eventDataMap.put(Integer.valueOf(pointerId), eventData);
		        if(ClassicByteView.current_screen==null && IngameRenderer.isPaused) {
					IngameRenderer.isPaused = false;
					ClassicByte.view.renderer.networkManager.disconnect();
					ClassicByte.view.setScreen(new ScreenMainMenu(ClassicByte.view));
				}
		        if(ClassicByteView.current_screen==null && !IngameRenderer.isPaused && Math.sqrt((eventData.x)*(eventData.x)+(eventData.y)*(eventData.y))<20.0F) {
		        	((ClassicByte)this.getContext()).openInputRequester("Chat", "", false, "send_chat");
				}
		        break;
		    case MotionEvent.ACTION_MOVE:
		        for(int i = 0; i < event.getPointerCount(); i++)
		        {
		            int curPointerId = event.getPointerId(i);
		            if(eventDataMap.containsKey(Integer.valueOf(curPointerId)))
		            {
		                EventData moveEventData = eventDataMap.get(Integer.valueOf(curPointerId));
		                moveEventData.last_x = moveEventData.x;
		                moveEventData.last_y = moveEventData.y;
		                moveEventData.x = event.getX(i);
		                moveEventData.y = event.getY(i);
						if(!IngameRenderer.isPaused) {
							if(moveEventData.x_start>this.renderer.height/2.0F || moveEventData.x_start>this.renderer.height/2.0F || moveEventData.y_start<this.renderer.height-this.renderer.height/2.0F || moveEventData.y_start<this.renderer.height-this.renderer.height/2.0F) {
								float difference_x = moveEventData.x-moveEventData.last_x;
								float difference_y = moveEventData.y-moveEventData.last_y;
								this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x + difference_x*0.02F;
								this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y + difference_y*0.01F;
							}
						}
		            }
		        }
		        break;
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		        eventDataMap.remove(Integer.valueOf(pointerId));
		        break;
		    case MotionEvent.ACTION_OUTSIDE:
		        break;
		    }
		   return true;
	}
	
	public void touchUpdate() {
		if(System.currentTimeMillis()-this.touch_update_timer>25 && ClassicByteView.current_screen==null) {
			boolean any_pointer_on_joystick = false;
			for(EventData event : this.eventDataMap.values()) {
				if(!IngameRenderer.isPaused && event.x_start<this.renderer.height/2.0F && event.x_start<this.renderer.height/2.0F && event.y_start>this.renderer.height-this.renderer.height/2.0F && event.y_start>this.renderer.height-this.renderer.height/2.0F) {
					IngameRenderer.joystick_x = -0.5F+(event.x/(this.renderer.height/2.0F));
					IngameRenderer.joystick_y = -0.5F+(1.0F-(((this.renderer.height-event.y)/(this.renderer.height/2.0F))));
					if(IngameRenderer.joystick_x>0.5F) {
						IngameRenderer.joystick_x = IngameRenderer.joystick_x-Math.abs(IngameRenderer.joystick_x-0.5F);
					}
					if(IngameRenderer.joystick_x<-0.5F) {
						IngameRenderer.joystick_x = IngameRenderer.joystick_x+Math.abs(IngameRenderer.joystick_x+0.5F);
					}
					if(IngameRenderer.joystick_y>0.5F) {
						IngameRenderer.joystick_y = IngameRenderer.joystick_y-Math.abs(IngameRenderer.joystick_y-0.5F);
					}
					if(IngameRenderer.joystick_y<-0.5F) {
						IngameRenderer.joystick_y = IngameRenderer.joystick_y+Math.abs(IngameRenderer.joystick_y+0.5F);
					}
					float b = (Math.abs(IngameRenderer.joystick_x)+Math.abs(IngameRenderer.joystick_y))*2;
					float a = (float) (Math.PI*2.0F-Math.atan2(IngameRenderer.joystick_x, IngameRenderer.joystick_y)+Math.PI);
					if(b<0.25F) {
						b = 0.0F;
					}
					float x = (float)(this.renderer.player.getXPosition()+0.1F*b*Math.cos(this.renderer.player.camera_rot_x+a) + 0.1F*b*Math.sin(this.renderer.player.camera_rot_x+a));
					float z = (float)(this.renderer.player.getZPosition()+0.1F*b*Math.sin(this.renderer.player.camera_rot_x+a) - 0.1F*b*Math.cos(this.renderer.player.camera_rot_x+a));
					if(Block.canGoTrough(this.renderer.world.getBlock((int)x, (int)this.renderer.player.getYPosition()+2, (int)z))) {
						if(!Block.canGoTrough(this.renderer.world.getBlock((int)x, (int)this.renderer.player.getYPosition(), (int)z)) && !Block.canGoTrough(this.renderer.world.getBlock((int)x, (int)this.renderer.player.getYPosition()+1, (int)z))) {
							this.renderer.player.setPosition(x, this.renderer.player.getYPosition()+1, z);
						} else {
							this.renderer.player.setPosition(x, this.renderer.player.getYPosition(), z);
						}
					}
					any_pointer_on_joystick = true;
				}
			}
			if(!any_pointer_on_joystick) {
				IngameRenderer.joystick_x = 0.0F;
				IngameRenderer.joystick_y = 0.0F;
			}
			this.touch_update_timer = System.currentTimeMillis();
		}
	}

	public void run() {
		long physics_update_timer = System.currentTimeMillis();
		long chunk_update_timer = System.currentTimeMillis();
		while(true) {
	        this.renderer.player.run();
	        try { this.renderer.networkManager.update(); } catch(Exception e) { e.printStackTrace(); };
	        try { this.touchUpdate(); } catch(Exception e) {};
	        
	        if(this.renderer.player.camera_rot_y<0.0F) {
	        	this.renderer.player.camera_rot_y = 0.0F;
	        }
	        if(this.renderer.player.camera_rot_y>Math.PI) {
	        	this.renderer.player.camera_rot_y = (float) Math.PI;
	        }
	        if(this.renderer.player.camera_rot_x<0.0F) {
	        	this.renderer.player.camera_rot_x = (float) (Math.PI*2.0D+this.renderer.player.camera_rot_x);
	        }
	        if(this.renderer.player.camera_rot_x>Math.PI*2.0D) {
	        	this.renderer.player.camera_rot_x = (float) (this.renderer.player.camera_rot_x-Math.PI*2.0D);
	        }
	        
	        if(System.currentTimeMillis()-chunk_update_timer>200 && ClassicByteView.current_screen==null) {
	        	((ClassicByte)ClassicByte.view.getContext()).runOnUiThread(new ChunkListUpdater());
	        	chunk_update_timer = System.currentTimeMillis();
	        }
	        
	        if(System.currentTimeMillis()-physics_update_timer>10 && ClassicByteView.current_screen==null) {
	        	if(Block.canGoTrough(this.renderer.world.getBlock((int)this.renderer.player.getXPosition(),(int)(this.renderer.player.getYPosition()),(int)this.renderer.player.getZPosition()))) {
	        		this.renderer.player.setPosition(this.renderer.player.getXPosition(), this.renderer.player.getYPosition()-0.0981F, this.renderer.player.getZPosition());
	        	}
	        	this.renderer.cloud_offset = ((float)(System.currentTimeMillis()%600000))/600000.0F;
	        	physics_update_timer = System.currentTimeMillis();
	        }
		}
	}
}