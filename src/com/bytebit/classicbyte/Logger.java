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

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Intent;
import android.os.Build;

public class Logger implements Thread.UncaughtExceptionHandler {
	
	private static Logger exception_handler = new Logger();
	public static boolean enabled = true;
	private static PrintWriter log_file_out = null;
	private static boolean init = false;
	
	public static Logger getExceptionHandler() {
		return exception_handler;
	}
	
	public static void log(Object c, String message) {
		/*if(init && Logger.enabled) {
			File f = new File(Environment.getExternalStorageDirectory().toString()+"/classicbytelog2.txt");
			MediaFile m = new MediaFile(ClassicByte.view.getContext().getContentResolver(),f);
			System.err.println(f.toString());
			try {
				m.delete();
				log_file_out = new PrintWriter(m.write());
			} catch(Exception e) { System.err.println(e.getMessage()); }
			init = false;
		}*/
		if(Logger.enabled && c!=null) {
			//log_file_out.println("["+c.getClass().getName()+"] "+message);
			System.err.println("["+c.getClass().getName()+"] "+message);
		}
		if(Logger.enabled && c==null) {
			//log_file_out.println("[unknown location] "+message);
			System.err.println("[unknown location] "+message);
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

	public void uncaughtException(Thread thread, Throwable ex) {
		String LINE_SEPARATOR = "\n";
		StringWriter stackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ START OF CRASH REPORT ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);

        Intent intent = new Intent(ClassicByte.view.getContext(), AnotherActivity.class);
        intent.putExtra("error", errorReport.toString());
        ClassicByte.view.getContext().startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
	}
}
