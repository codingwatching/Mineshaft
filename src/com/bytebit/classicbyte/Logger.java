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

public class Logger {
	
	public static boolean enabled = true;
	
	public static void log(Object c, String message) {
		if(Logger.enabled && c!=null) {
			System.err.println("["+c.getClass().getName()+"] "+message);
		}
		if(Logger.enabled && c==null) {
			System.err.println("[location unknown] "+message);
		}
	}
	
	public static void log(Object c, int x) {
		Logger.log(c, ""+x);
	}
	
	public static void log(Object c, boolean x) {
		Logger.log(c, ""+x);
	}
	
	public static void log(Object c, float x) {
		Logger.log(c, ""+x);
	}
	
	public static void log(Object c, byte x) {
		Logger.log(c, ""+x);
	}
	
	public static void log(Object c, long x) {
		Logger.log(c, ""+x);
	}
	
	public static void log(Object c, double x) {
		Logger.log(c, ""+x);
	}
}
