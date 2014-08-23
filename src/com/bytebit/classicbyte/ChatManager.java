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

import java.util.ArrayList;
import java.util.List;

public class ChatManager {
	public List<String> lines_colored = new ArrayList<String>();
	public List<String> lines = new ArrayList<String>();
	
	public void clear() {
		this.lines.clear();
		this.lines_colored.clear();
	}
	
	public static String deleteColorCodes(String x) {
		x = x.replaceAll("&0","");
		x = x.replaceAll("&1","");
		x = x.replaceAll("&2","");
		x = x.replaceAll("&3","");
		x = x.replaceAll("&4","");
		x = x.replaceAll("&5","");
		x = x.replaceAll("&6","");
		x = x.replaceAll("&7","");
		x = x.replaceAll("&8","");
		x = x.replaceAll("&9","");
		x = x.replaceAll("&a","");
		x = x.replaceAll("&b","");
		x = x.replaceAll("&c","");
		x = x.replaceAll("&d","");
		x = x.replaceAll("&e","");
		x = x.replaceAll("&f","");
		return x;
	}
	
	public void addLine(String message) {
		if(this.lines.size()!=0) {
			this.lines.remove(0);
			this.lines_colored.remove(0);
		} else {
			for(int k=0;k!=Options.chat_history_size;k++) {
				this.lines.add("");
				this.lines_colored.add("");
			}
		}
		String m2 = message;
		m2 = m2.replaceAll(""+(char)0,"");
		m2 = m2.replaceAll(""+(char)1,"");
		m2 = m2.replaceAll(""+(char)2,"");
		m2 = m2.replaceAll(""+(char)3,"");
		m2 = m2.replaceAll(""+(char)4,"");
		m2 = m2.replaceAll(""+(char)5,"");
		m2 = m2.replaceAll(""+(char)6,"");
		m2 = m2.replaceAll(""+(char)7,"");
		m2 = m2.replaceAll(""+(char)8,"");
		m2 = m2.replaceAll(""+(char)9,"");
		m2 = m2.replaceAll(""+(char)10,"");
		m2 = m2.replaceAll(""+(char)11,"");
		m2 = m2.replaceAll(""+(char)12,"");
		m2 = m2.replaceAll(""+(char)13,"");
		m2 = m2.replaceAll(""+(char)14,"");
		m2 = m2.replaceAll(""+(char)15,"");
		this.lines.add(m2);
		this.lines_colored.add(message);
	}
}
