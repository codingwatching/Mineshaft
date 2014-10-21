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

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES10;
//import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureManager {
	public static Bitmap[] textures = new Bitmap[128];
	private static boolean initalized_buffers = false;
	public static ShortBuffer screen_overlay_vertex_buffer;
	public static FloatBuffer screen_overlay_texture_coords_buffer;
	
	public static void run(int width, int height) {
		if(!initalized_buffers) {
			ByteBuffer b = ByteBuffer.allocateDirect(16);
			b.order(ByteOrder.nativeOrder());
			ShortBuffer v = b.asShortBuffer();
			short a[] =  
				{
				    (short) 0, (short) 0,
				     (short) width, (short) 0,
				    (short) 0,  (short) height,
				     (short) width,  (short) height,
				  };
			v.put(a);
			v.position(0);
			screen_overlay_vertex_buffer = v;
			
			ByteBuffer b2 = ByteBuffer.allocateDirect(32);
			b2.order(ByteOrder.nativeOrder());
			FloatBuffer v2 = b2.asFloatBuffer();
			float w = ((float)width)/TextureManager.getBitmap(20).getWidth();
			float h = ((float)height)/TextureManager.getBitmap(20).getHeight();
			float a2[] =  
				{
				    0.0F, 0.0F,
				    w, 0.0F,
				    0.0F, h,
				    w, h,
				  };
			v2.put(a2);
			v2.position(0);
			screen_overlay_texture_coords_buffer = v2;
			initalized_buffers = true;
		}
	}
	
	public static void loadTexture(Context context, int resource, int slot) {
        try {
            InputStream is = context.getResources().openRawResource(resource);
            textures[slot] = BitmapFactory.decodeStream(is);
            is.close();
        } catch(Exception e) {}
	}
	
	public static Bitmap getBitmap(int slot) {
		return TextureManager.textures[slot];
	}
	
	public static void bindTexture(int slot) {
		 GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textures[slot], 0);
	     GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST);
	     GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST);
	}
	
	public static void loadTextureFromBitmap(Bitmap b, int slot) {
        textures[slot] = b;
	}
	
	public static Bitmap rotateBitmap(Bitmap b, float f) {
		Matrix matrix = new Matrix();
		matrix.postRotate(f);
		return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
	}
	
	public static Bitmap scaleBitmap(Bitmap b, int scale_x, int scale_y) {
		return Bitmap.createScaledBitmap(b, scale_x, scale_y, true);
	}
	
	public static Bitmap scaleBitmapNoQualityLoss(Bitmap b, int scale_x, int scale_y) {
		return Bitmap.createScaledBitmap(b, scale_x, scale_y, false);
	}
}