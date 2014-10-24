/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.closure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import fitlibrary.runtime.RuntimeContextContainer;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.utility.CollectionUtility;

public class TestActionSignature {
	RuntimeContextInternal runtime = new RuntimeContextContainer();
	@Test
	public void equality() {
		assertThat(new ActionSignature("act",0,runtime),is(new ActionSignature("act",0,runtime)));
		assertThat(new ActionSignature("act",0,runtime),not(new ActionSignature("act",1,runtime)));
		assertThat(new ActionSignature("act",0,runtime),not(new ActionSignature("actIve",0,runtime)));
	}
	@Test
	public void hash() {
		assertThat(new ActionSignature("act",0,runtime).hashCode(),is(new ActionSignature("act",0,runtime).hashCode()));
		assertThat(new ActionSignature("act",0,runtime).hashCode(),not(new ActionSignature("act",1,runtime).hashCode()));
		assertThat(new ActionSignature("act",0,runtime).hashCode(),not(new ActionSignature("actIve",0,runtime).hashCode()));
	}
	@Test
	public void display() {
		assertThat(new ActionSignature("act",0,runtime).toString(),is("act/0"));
		assertThat(new ActionSignature("act",1,runtime).toString(),is("act/1"));
		assertThat(new ActionSignature("actIve",2,runtime).toString(),is("actIve/2"));
	}
	@Test
	public void doStyle() {
		assertThat(ActionSignature.doStyle(runtime,"act"),is(new ActionSignature("act",0,runtime)));
		assertThat(ActionSignature.doStyle(runtime,"act","1"),is(new ActionSignature("act",1,runtime)));
		assertThat(ActionSignature.doStyle(runtime,"act","1","ive"),is(new ActionSignature("actIve",1,runtime)));
		assertThat(ActionSignature.doStyle(runtime,"act","1","ive","2"),is(new ActionSignature("actIve",2,runtime)));
		assertThat(ActionSignature.doStyle(runtime,"act","1","ive","2","ly"),is(new ActionSignature("actIveLy",2,runtime)));
		assertThat(ActionSignature.doStyle(runtime,CollectionUtility.list("act","1","ive","2","ly")),is(new ActionSignature("actIveLy",2,runtime)));
	}
	@Test
	public void seqStyle() {
		assertThat(ActionSignature.seqStyle(runtime,"act"),is(new ActionSignature("act",0,runtime)));
		assertThat(ActionSignature.seqStyle(runtime,"act","1"),is(new ActionSignature("act",1,runtime)));
		assertThat(ActionSignature.seqStyle(runtime,"act","1","ive"),is(new ActionSignature("act",2,runtime)));
		assertThat(ActionSignature.seqStyle(runtime,"act","1","ive","2"),is(new ActionSignature("act",3,runtime)));
		assertThat(ActionSignature.seqStyle(runtime,"act","1","ive","2","ly"),is(new ActionSignature("act",4,runtime)));
		assertThat(ActionSignature.seqStyle(runtime,CollectionUtility.list("act","1","ive","2","ly")),is(new ActionSignature("act",4,runtime)));
	}
}
