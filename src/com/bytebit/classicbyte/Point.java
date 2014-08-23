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

import android.view.MotionEvent;

public class Point {
	public Point(MotionEvent e, float x2, float y2, float x3, float y3) {
		this.event = e;
		this.x = x2;
		this.y = y2;
		this.start_x = x3;
		this.start_y = y3;
	}
	public float x = 0.0F;
	public float y = 0.0F;
	public float start_x = 0.0F;
	public float start_y = 0.0F;
	public float last_x = 0.0F;
	public float last_y = 0.0F;
	public MotionEvent event;
	public boolean disabled = false;
}
