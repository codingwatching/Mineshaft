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

import java.util.AbstractList;
import java.util.ArrayList;

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
	
	public static AbstractList<Point> pointers = new ArrayList<Point>();
	
	private long touch_update_timer = System.currentTimeMillis();
	
	public ClassicByteView(Context context) {
        super(context);
        
        this.setRenderer(renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        
        this.renderer.setContext(this.getContext());
        this.renderer.setView(this);
        
        ClassicByte.view = this;
        Thread t = new Thread(this);
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
	
	public boolean onTouchEvent(MotionEvent event) {
		try {
			int pointerCount = event.getPointerCount();
	    	
	    	for (int i = 0; i < pointerCount; i++)
	    	{
	    		int x = (int) event.getX(i);
	    		int y = (int) event.getY(i);    		
	    		int id = event.getPointerId(i);
	    		int action = event.getActionMasked();
	    		//int actionIndex = event.getActionIndex();
	    		
	    		
	    		switch (action)
	    		{
	    			case MotionEvent.ACTION_DOWN:
	    			case MotionEvent.ACTION_POINTER_DOWN:
	    				ClassicByteView.pointers.add(id, new Point(event,x,y,x,y));
	    				break;
	    			case MotionEvent.ACTION_UP:
	    			case MotionEvent.ACTION_POINTER_UP:
	    				ClassicByteView.pointers.remove(id);
	    				break;	
	    			case MotionEvent.ACTION_MOVE:
	    				Point p = ClassicByteView.pointers.get(id);
	    				p.event = event;
	    				p.last_x = p.x;
	    				p.last_y = p.y;
	    				p.x = x;
	    				p.y = y;
	    				ClassicByteView.pointers.set(id, p);
	    				//if(p.start_x)
	    				float difference_x = p.x-p.last_x;
		        		float difference_y = p.y-p.last_y;
		        		this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x + difference_x*0.01F;
	        			this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y + difference_y*0.01F;
	    				break;
	    		}
	    	}
			
			/*if(event.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN) {
				ClassicByteView.pointers.add(event.getPointerId(event.getActionIndex())+0, new Point(event,event.getX(event.getActionIndex()), event.getY(event.getActionIndex()), event.getX(event.getActionIndex()), event.getY(event.getActionIndex())));
			}
			if(event.getActionMasked()==MotionEvent.ACTION_POINTER_UP) {
				//ClassicByteView.pointers.set(event.getPointerId(event.getActionIndex())+0, null);
				ClassicByteView.pointers.remove(event.getPointerId(event.getActionIndex())+0);
			}
			
			if(event.getActionMasked()==MotionEvent.ACTION_MOVE) {
				Point p = ClassicByteView.pointers.get(event.getPointerId(event.getActionIndex()));
				p.x = event.getX(event.getActionIndex());
				p.y = event.getY(event.getActionIndex());
				p.event = event;
				ClassicByteView.pointers.set(event.getPointerId(event.getActionIndex())+0, p);
			}*/
			
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
			
			if(event.getAction()==MotionEvent.ACTION_DOWN && ClassicByteView.current_screen==null && event.getX()>=0 && event.getX()<TextureManager.getBitmap(27).getWidth() && event.getY()>this.renderer.height-TextureManager.getBitmap(27).getHeight() && event.getY()<this.renderer.height) {
				((ClassicByte)this.getContext()).openInputRequester("Chat", "", false, "send_chat");
			}
			if(event.getAction()==MotionEvent.ACTION_DOWN && ClassicByteView.current_screen==null && event.getX()>=(ClassicByte.view.renderer.width-TextureManager.getBitmap(29).getWidth())/2 && event.getX()<(ClassicByte.view.renderer.width-TextureManager.getBitmap(29).getWidth())/2+TextureManager.getBitmap(29).getWidth() && event.getY()>ClassicByte.view.renderer.height-TextureManager.getBitmap(29).getHeight() && event.getY()<this.renderer.height) {
				this.renderer.player.setPosition(this.renderer.player.getXPosition(), this.renderer.player.getYPosition()+2.0F, this.renderer.player.getZPosition());
			}
			
			IngameRenderer.current_button = 0;
			if(ClassicByteView.current_screen==null && event.getX()>=0 && event.getX()<TextureManager.getBitmap(27).getWidth() && event.getY()>this.renderer.height-TextureManager.getBitmap(27).getHeight() && event.getY()<this.renderer.height) {
				if(event.getAction()==MotionEvent.ACTION_MOVE || event.getAction()==MotionEvent.ACTION_DOWN) {
					IngameRenderer.current_button = 1;
				}
			}
			if(ClassicByteView.current_screen==null && event.getX()>=(ClassicByte.view.renderer.width-TextureManager.getBitmap(29).getWidth())/2 && event.getX()<(ClassicByte.view.renderer.width-TextureManager.getBitmap(29).getWidth())/2+TextureManager.getBitmap(29).getWidth() && event.getY()>ClassicByte.view.renderer.height-TextureManager.getBitmap(29).getHeight() && event.getY()<this.renderer.height) {
				if(event.getAction()==MotionEvent.ACTION_MOVE || event.getAction()==MotionEvent.ACTION_DOWN) {
					IngameRenderer.current_button = 2;
				}
			}
			if(ClassicByteView.current_screen==null && event.getX()>=TextureManager.getBitmap(32).getWidth() && event.getX()<TextureManager.getBitmap(32).getWidth()*2 && event.getY()>this.renderer.height-TextureManager.getBitmap(32).getHeight() && event.getY()<this.renderer.height) {
				if(event.getAction()==MotionEvent.ACTION_MOVE || event.getAction()==MotionEvent.ACTION_DOWN) {
					IngameRenderer.current_button = 3;
				}
			}
			if(ClassicByteView.current_screen==null && event.getX()>=(ClassicByte.view.renderer.width-TextureManager.getBitmap(35).getWidth()) && event.getX()<(ClassicByte.view.renderer.width-TextureManager.getBitmap(35).getWidth())+TextureManager.getBitmap(35).getWidth() && event.getY()>this.renderer.height-TextureManager.getBitmap(32).getHeight() && event.getY()<this.renderer.height) {
				if(event.getAction()==MotionEvent.ACTION_MOVE || event.getAction()==MotionEvent.ACTION_DOWN) {
					IngameRenderer.current_button = 4;
				}
				if(event.getAction()==MotionEvent.ACTION_UP) {
					IngameRenderer.show_block_selection = !IngameRenderer.show_block_selection;
				}
			}
			
			if(event.getAction()==MotionEvent.ACTION_DOWN && ClassicByteView.current_screen==null && IngameRenderer.isPaused) {
				IngameRenderer.isPaused = false;
				ClassicByte.view.renderer.networkManager.disconnect();
				ClassicByte.view.setScreen(new ScreenMainMenu(ClassicByte.view));
			}
		} catch(Exception e) {}
		
		return true;
	}
	
	public void touchUpdate() {
		if(System.currentTimeMillis()-this.touch_update_timer>25 && ClassicByteView.current_screen==null) {
        	for(int k=0;k!=ClassicByteView.pointers.size();k++) {
        		if(k>=ClassicByteView.pointers.size()) {
        			break;
        		}
        		Point p = ClassicByteView.pointers.get(k);
        		if(p!=null && !p.disabled) {
		        	float start_x = p.start_x;
		        	//float start_y = p.start_y;
		        	if(!IngameRenderer.isPaused) {
			        	if(start_x<this.renderer.width/2.0F && IngameRenderer.current_button==0 && Math.sqrt((p.x-p.start_x)*(p.x-p.start_x)+(p.y-p.start_y)*(p.y-p.start_y))>Options.pointer_size) {
			        		
			        		this.movement_pointer_center_x = (int)(p.start_x);
			        		this.movement_pointer_center_y = (int)(p.start_y);
			        		
			        		float a = 0.0F;
			        		
			        		if(p.x>this.movement_pointer_center_x+30 && p.y>this.movement_pointer_center_y-30 && p.y<this.movement_pointer_center_y+30) {
			        			// pure movement to the right
			        			a = 90.0F;
			        		}

			        		if(p.x<this.movement_pointer_center_x-30 && p.y>this.movement_pointer_center_y-30 && p.y<this.movement_pointer_center_y+30) {
			        			// pure movement to the left
			        			a = -90.0F;
			        		}
			        		
			        		if(p.x>this.movement_pointer_center_x-30 && p.x<this.movement_pointer_center_x+30 && p.y<this.movement_pointer_center_y-30) {
			        			// pure movement to the top
			        			a = 0.0F;
			        		}
			        		
			        		if(p.x>this.movement_pointer_center_x-30 && p.x<this.movement_pointer_center_x+30 && p.y>this.movement_pointer_center_y+30) {
			        			// pure movement to the bottom
			        			a = 180.0F;
			        		}
			        		
			        		if(p.x<this.movement_pointer_center_x-30 && p.y<this.movement_pointer_center_y-30) {
			        			// movement to the left upper part
			        			a = -45.0F;
			        		}
			        		
			        		if(p.x>this.movement_pointer_center_x+30 && p.y<this.movement_pointer_center_y-30) {
			        			// movement to the right upper part
			        			a = 45.0F;
			        		}
			        		
			        		if(p.x<this.movement_pointer_center_x-30 && p.y>this.movement_pointer_center_y+30) {
			        			// movement to the left lower part
			        			a = -135.0F;
			        		}
			        		
			        		if(p.x>this.movement_pointer_center_x+30 && p.y>this.movement_pointer_center_y+30) {
			        			// movement to the right lower part
			        			a = 135.0F;
			        		}
			        		
			        		a = (float)(a/360.0F*(Math.PI*2));
			        		
			        		float x = (float)(this.renderer.player.getXPosition()+0.1F*Math.cos(this.renderer.player.camera_rot_x+a) + 0.1F*Math.sin(this.renderer.player.camera_rot_x+a));
			    			float z = (float)(this.renderer.player.getZPosition()+0.1F*Math.sin(this.renderer.player.camera_rot_x+a) - 0.1F*Math.cos(this.renderer.player.camera_rot_x+a));
			    			
			    			if(Block.canGoTrough(this.renderer.world.getBlock((int)x, (int)this.renderer.player.getYPosition()+1, (int)z)) && Block.canGoTrough(this.renderer.world.getBlock((int)x, (int)this.renderer.player.getYPosition()+2, (int)z))) {
				    			this.renderer.player.setPosition(x, this.renderer.player.getYPosition(), z);
			    			}
			        	}
			        	
			        	/*if(IngameRenderer.current_button==0 && start_x>this.renderer.width/2.0F) {
			        		float difference_x = p.x-p.last_x;
			        		float difference_y = p.y-p.last_y;
			        		this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x + difference_x;
		        			this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y + difference_y;
			        	}*/
			        	
			        	/*if(start_x>this.renderer.width/2.0F && Math.sqrt((p.x-p.start_x)*(p.x-p.start_x)+(p.y-p.start_y)*(p.y-p.start_y))>Options.pointer_size && IngameRenderer.current_button==0) {
			        		this.look_pointer_center_x = (int)(p.start_x);
			        		this.look_pointer_center_y = (int)(p.start_y);
			        		
			        		if(p.x>this.look_pointer_center_x+30 && p.y>this.look_pointer_center_y-30 && p.y<this.look_pointer_center_y+30) {
			        			// pure movement to the right
			        			this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x + 0.02F;
			        		}

			        		if(p.x<this.look_pointer_center_x-30 && p.y>this.look_pointer_center_y-30 && p.y<this.look_pointer_center_y+30) {
			        			// pure movement to the left
			        			this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x - 0.02F;
			        		}
			        		
			        		if(p.x>this.look_pointer_center_x-30 && p.x<this.look_pointer_center_x+30 && p.y<this.look_pointer_center_y-30) {
			        			// pure movement to the top
			        			this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y - 0.02F;
			        		}
			        		
			        		if(p.x>this.look_pointer_center_x-30 && p.x<this.look_pointer_center_x+30 && p.y>this.look_pointer_center_y+30) {
			        			// pure movement to the bottom
			        			this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y + 0.02F;
			        		}
			        		
			        		if(p.x<this.look_pointer_center_x-30 && p.y<this.look_pointer_center_y-30) {
			        			// movement to the left upper part
			        			this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x - 0.02F;
			        			this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y - 0.02F;
			        		}
			        		
			        		if(p.x>this.look_pointer_center_x+30 && p.y<this.look_pointer_center_y-30) {
			        			// movement to the right upper part
			        			this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x + 0.02F;
			        			this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y - 0.02F;
			        		}
			        		
			        		if(p.x<this.look_pointer_center_x-30 && p.y>this.look_pointer_center_y+30) {
			        			// movement to the left lower part
			        			this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x - 0.02F;
			        			this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y + 0.02F;
			        		}
			        		
			        		if(p.x>this.look_pointer_center_x+30 && p.y>this.look_pointer_center_y+30) {
			        			// movement to the right lower part
			        			this.renderer.player.camera_rot_x = this.renderer.player.camera_rot_x + 0.02F;
			        			this.renderer.player.camera_rot_y = this.renderer.player.camera_rot_y + 0.02F;
			        		}
			        	}*/
		        	}
        		}
        	}
        	this.touch_update_timer = System.currentTimeMillis();
        }
	}

	@Override
	public void run() {
		long physics_update_timer = System.currentTimeMillis();
		long chunk_update_timer = System.currentTimeMillis();
		while(true) {
	        this.renderer.networkManager.run();
	        this.renderer.player.run();
	        this.touchUpdate();
	        
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
	        	physics_update_timer = System.currentTimeMillis();
	        }
		}
	}
}