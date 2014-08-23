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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES10;

public class Tesselator {
	public static Tesselator INSTANCE = new Tesselator();
	
	private FloatBuffer vertices = null;
	private FloatBuffer texture_coords = null;
	private float texture_x = 0.0F;
	private float texture_y = 0.0F;
	private int type = GLES10.GL_TRIANGLES;
	private int count = 0;
	
	public void glBegin(int type) {
		this.type = type;
		
		if(this.vertices==null && this.texture_coords==null) {
			ByteBuffer b = ByteBuffer.allocateDirect(1200);
			b.order(ByteOrder.nativeOrder());
			this.vertices = b.asFloatBuffer();
			ByteBuffer b2 = ByteBuffer.allocateDirect(800);
			b2.order(ByteOrder.nativeOrder());
			this.texture_coords = b2.asFloatBuffer();
		}
		this.vertices.position(0);
		this.texture_coords.position(0);
		this.count = 0;
	}
	
	public void glTexCoord2f(float x, float y) {
		this.texture_x = x;
		this.texture_y = y;
	}
	
	public void glVertex3f(float x, float y, float z) {
		this.vertices.put(x);
		this.vertices.put(y);
		this.vertices.put(z);
		this.texture_coords.put(this.texture_x);
		this.texture_coords.put(this.texture_y);
		this.count++;
	}
	
	public void glEnd() {
		this.vertices.position(0);
		this.texture_coords.position(0);
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, this.vertices);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, this.texture_coords);
		GLES10.glDrawArrays(this.type, 0, this.count);
		GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
	}
}
