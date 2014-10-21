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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES10;

public class Cube {
	   private ShortBuffer vertexBuffer;  // Buffer for vertex-array
	   private FloatBuffer texCoordBuffer;
	        
	   // Constructor - Set up the buffers
	   public Cube() {
	      // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
	      ByteBuffer vbb = ByteBuffer.allocateDirect(24*3*4);
	      vbb.order(ByteOrder.nativeOrder()); // Use native byte order
	      vertexBuffer = vbb.asShortBuffer(); // Convert from byte to float
	      
	      ByteBuffer v = ByteBuffer.allocateDirect(24*2*4);
	      v.order(ByteOrder.nativeOrder()); // Use native byte order
	      texCoordBuffer = v.asFloatBuffer(); // Convert from byte to float
	   }
	   
	   public void prepaireData(short x, short y, short z, int texture_x, int texture_y) {
		   short[] vertices = {  // Vertices of the 6 faces
				      // FRONT
				       (short)(x+0),  (short)(y+0),  (short)(z+1),  // 0. left-bottom-front
				       (short)(x+1),  (short)(y+0),  (short)(z+1),  // 1. right-bottom-front
				       (short)(x+0),  (short)(y+1),  (short)(z+1),  // 2. left-top-front
				       (short)(x+1),  (short)(y+1),  (short)(z+1),  // 3. right-top-front
				      // BACK
				       (short)(x+1),  (short)(y+0),  (short)(z+0),  // 6. right-bottom-back
				       (short)(x+0),  (short)(y+0),  (short)(z+0),  // 4. left-bottom-back
				       (short)(x+1),  (short)(y+1),  (short)(z+0),  // 7. right-top-back
				       (short)(x+0),  (short)(y+1),  (short)(z+0),  // 5. left-top-back
				      // LEFT
				       (short)(x+0),  (short)(y+0),  (short)(z+0),  // 4. left-bottom-back
				       (short)(x+0),  (short)(y+0),  (short)(z+1),  // 0. left-bottom-front 
				       (short)(x+0),  (short)(y+1),  (short)(z+0),  // 5. left-top-back
				       (short)(x+0),  (short)(y+1),  (short)(z+1),  // 2. left-top-front
				      // RIGHT
				       (short)(x+1),  (short)(y+0),  (short)(z+1),  // 1. right-bottom-front
				       (short)(x+1),  (short)(y+0),  (short)(z+0),  // 6. right-bottom-back
				       (short)(x+1),  (short)(y+1),  (short)(z+1),  // 3. right-top-front
				       (short)(x+1),  (short)(y+1),  (short)(z+0),  // 7. right-top-back
				      // TOP
				   	   (short)(x+0),  (short)(y+1),  (short)(z+1),  // 2. left-top-front
				       (short)(x+1),  (short)(y+1),  (short)(z+1),  // 3. right-top-front
				       (short)(x+0),  (short)(y+1),  (short)(z+0),  // 5. left-top-back
				       (short)(x+1),  (short)(y+1),  (short)(z+0),  // 7. right-top-back
				      // BOTTOM
				       (short)(x+0),  (short)(y+0),  (short)(z+0),  // 4. left-bottom-back
				       (short)(x+1),  (short)(y+0),  (short)(z+0),  // 6. right-bottom-back
				       (short)(x+0),  (short)(y+0),  (short)(z+1),  // 0. left-bottom-front
				       (short)(x+1),  (short)(y+0),  (short)(z+1)   // 1. right-bottom-front
				   };
		   
		      vertexBuffer.put(vertices);         // Copy data into buffer
		      vertexBuffer.position(0);
		      
		      float b = 1.0F/16.0F;
		      float xt = texture_x*b;
		      float yt = texture_y*b;
		      
		      float[] texCoords = {  // Vertices of the 6 faces
				      // FRONT
		    		  xt+0.0F,  yt+0.0F,  // 0. left-bottom-front
		    		  xt+b,  yt+0.0F,  // 1. right-bottom-front
		    		  xt+0.0F,  yt+b,  // 2. left-top-front
		    		  xt+b,  yt+b,  // 3. right-top-front
				      // BACK
		    		  xt+b,  yt,  // 6. right-bottom-back
		    		  xt+0.0F,  yt+0.0F,  // 4. left-bottom-back
		    		  xt+b,  yt+b,  // 7. right-top-back
		    		  xt+0.0F,  yt+b,  // 5. left-top-back
				      // LEFT
		    		  xt+0.0F,  yt,  // 4. left-bottom-back
		    		  xt+0.0F,  yt+b,  // 0. left-bottom-front 
		    		  xt+b,  yt+0.0F,  // 5. left-top-back
		    		  xt+b,  yt+b,  // 2. left-top-front
				      // RIGHT
		    		  xt+0.0F,  yt+b,  // 1. right-bottom-front
		    		  xt+0.0F,  yt+0.0F,  // 6. right-bottom-back
		    		  xt+b,  yt+b,  // 3. right-top-front
		    		  xt+b,  yt+0.0F,  // 7. right-top-back
				      // TOP
		    		  xt+0.0F,  yt+b,  // 2. left-top-front
		    		  xt+b,  yt+b,  // 3. right-top-front
		    		  xt+0.0F,  yt+0.0F,  // 5. left-top-back
		    		  xt+b,  yt+0.0F,  // 7. right-top-back
				      // BOTTOM
		    		  xt+0.0F,  yt+0.0F,  // 4. left-bottom-back
		    		  xt+b,  yt+0.0F,  // 6. right-bottom-back
		    		  xt+0.0F,  yt+b,  // 0. left-bottom-front
		    		  xt+b,  yt+b,   // 1. right-bottom-front
				   };
		      
		      texCoordBuffer.put(texCoords);
		      texCoordBuffer.position(0);
		      
			  GLES10.glVertexPointer(3, GL10.GL_SHORT, 0, vertexBuffer);
			  GLES10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
	   }
	   
	   public void prepaireData(short x, short y, short z, float start_x, float start_y, float width, float height) {
		   short[] vertices = {  // Vertices of the 6 faces
				      // FRONT
				       (short)(x+0),  (short)(y+0),  (short)(z+1),  // 0. left-bottom-front
				       (short)(x+1),  (short)(y+0),  (short)(z+1),  // 1. right-bottom-front
				       (short)(x+0),  (short)(y+1),  (short)(z+1),  // 2. left-top-front
				       (short)(x+1),  (short)(y+1),  (short)(z+1),  // 3. right-top-front
				      // BACK
				       (short)(x+1),  (short)(y+0),  (short)(z+0),  // 6. right-bottom-back
				       (short)(x+0),  (short)(y+0),  (short)(z+0),  // 4. left-bottom-back
				       (short)(x+1),  (short)(y+1),  (short)(z+0),  // 7. right-top-back
				       (short)(x+0),  (short)(y+1),  (short)(z+0),  // 5. left-top-back
				      // LEFT
				       (short)(x+0),  (short)(y+0),  (short)(z+0),  // 4. left-bottom-back
				       (short)(x+0),  (short)(y+0),  (short)(z+1),  // 0. left-bottom-front 
				       (short)(x+0),  (short)(y+1),  (short)(z+0),  // 5. left-top-back
				       (short)(x+0),  (short)(y+1),  (short)(z+1),  // 2. left-top-front
				      // RIGHT
				       (short)(x+1),  (short)(y+0),  (short)(z+1),  // 1. right-bottom-front
				       (short)(x+1),  (short)(y+0),  (short)(z+0),  // 6. right-bottom-back
				       (short)(x+1),  (short)(y+1),  (short)(z+1),  // 3. right-top-front
				       (short)(x+1),  (short)(y+1),  (short)(z+0),  // 7. right-top-back
				      // TOP
				   	   (short)(x+0),  (short)(y+1),  (short)(z+1),  // 2. left-top-front
				       (short)(x+1),  (short)(y+1),  (short)(z+1),  // 3. right-top-front
				       (short)(x+0),  (short)(y+1),  (short)(z+0),  // 5. left-top-back
				       (short)(x+1),  (short)(y+1),  (short)(z+0),  // 7. right-top-back
				      // BOTTOM
				       (short)(x+0),  (short)(y+0),  (short)(z+0),  // 4. left-bottom-back
				       (short)(x+1),  (short)(y+0),  (short)(z+0),  // 6. right-bottom-back
				       (short)(x+0),  (short)(y+0),  (short)(z+1),  // 0. left-bottom-front
				       (short)(x+1),  (short)(y+0),  (short)(z+1)   // 1. right-bottom-front
				   };
		   
		      vertexBuffer.put(vertices);         // Copy data into buffer
		      vertexBuffer.position(0);
		      
		      float xt = start_x;
		      float yt = start_y;
		      
		      float[] texCoords = {  // Vertices of the 6 faces
				      // FRONT
		    		  xt+0.0F,  yt+0.0F,  // 0. left-bottom-front
		    		  xt+width,  yt+0.0F,  // 1. right-bottom-front
		    		  xt+0.0F,  yt+height,  // 2. left-top-front
		    		  xt+width,  yt+height,  // 3. right-top-front
				      // BACK
		    		  xt+width,  yt,  // 6. right-bottom-back
		    		  xt+0.0F,  yt+0.0F,  // 4. left-bottom-back
		    		  xt+width,  yt+height,  // 7. right-top-back
		    		  xt+0.0F,  yt+height,  // 5. left-top-back
				      // LEFT
		    		  xt+0.0F,  yt,  // 4. left-bottom-back
		    		  xt+0.0F,  yt+height,  // 0. left-bottom-front 
		    		  xt+width,  yt+0.0F,  // 5. left-top-back
		    		  xt+width,  yt+height,  // 2. left-top-front
				      // RIGHT
		    		  xt+0.0F,  yt+height,  // 1. right-bottom-front
		    		  xt+0.0F,  yt+0.0F,  // 6. right-bottom-back
		    		  xt+width,  yt+height,  // 3. right-top-front
		    		  xt+width,  yt+0.0F,  // 7. right-top-back
				      // TOP
		    		  xt+0.0F,  yt+height,  // 2. left-top-front
		    		  xt+width,  yt+height,  // 3. right-top-front
		    		  xt+0.0F,  yt+0.0F,  // 5. left-top-back
		    		  xt+width,  yt+0.0F,  // 7. right-top-back
				      // BOTTOM
		    		  xt+0.0F,  yt+0.0F,  // 4. left-bottom-back
		    		  xt+width,  yt+0.0F,  // 6. right-bottom-back
		    		  xt+0.0F,  yt+height,  // 0. left-bottom-front
		    		  xt+width,  yt+height,   // 1. right-bottom-front
				   };
		      
		      texCoordBuffer.put(texCoords);
		      texCoordBuffer.position(0);
		      
			  GLES10.glVertexPointer(3, GL10.GL_SHORT, 0, vertexBuffer);
			  GLES10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
	   }
	  
	   // Draw the shape
	   public void draw(GL10 gl, int side) {
	      if(side==1) {
	    	  GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	      }
	      if(side==2) {
	    	  GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
	      }
	      if(side==3) {
	    	  GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
	      }
	      if(side==4) {
	    	  GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
	      }
	      if(side==5) {
	    	  GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
	      }
	      if(side==6) {
	    	  GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
	      }
	   }
	}