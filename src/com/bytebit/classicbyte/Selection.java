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

public class Selection {
	private int start_x = 0;
	private int start_y = 0;
	private int start_z = 0;
	private int end_x = 0;
	private int end_y = 0;
	private int end_z = 0;
	
	private String name = "A selection";
	
	private int id = 0;
	
	private int red = 255;
	private int green = 255;
	private int blue = 255;
	private int opacity = 255;
	
	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}
	
	public int getOpacity() {
		return this.opacity;
	}
	
	public void setColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int getRedPartOfColor() {
		return this.red;
	}
	
	public int getGreenPartOfColor() {
		return this.green;
	}
	
	public int getBluePartOfColor() {
		return this.blue;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setStartPosition(int x, int y, int z) {
		this.start_x = x;
		this.start_y = y;
		this.start_z = z;
	}
	
	public void setEndPosition(int x, int y, int z) {
		this.end_x = x;
		this.end_y = y;
		this.end_z = z;
	}
	
	public int getStartX() {
		return this.start_x;
	}
	
	public int getStartY() {
		return this.start_y;
	}
	
	public int getStartZ() {
		return this.start_z;
	}
	
	public int getEndX() {
		return this.end_x;
	}
	
	public int getEndY() {
		return this.end_y;
	}
	
	public int getEndZ() {
		return this.end_z;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
