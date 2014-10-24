/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.parser.graphic;

import java.awt.Point;

import fitlibrary.parser.graphic.ObjectDotGraphic;

import junit.framework.TestCase;

public class TestObjectDotGraphic extends TestCase {
	public void testPoint() {
		assertDot("digraph G {\nn0 [label = \"java.awt.Point[x=0,y=0]\"];\n}\n",
				new Point());
	}
	public void testMyPoint() {
		assertDot("digraph G {\n"
				+ "n0 [label = \""+MyPoint.class.getName()+"\"];\n"
				+ "n1 [label = \"0\"];\n"
				+ "n0 -> n1 [label=\"x\"];\n"
				+ "n0 -> n1 [label=\"y\"];\n"
				+ "}\n",
				new MyPoint());
	}
	
	static class MyPoint extends Point {
		private static final long serialVersionUID = 1L;
	}
	static class MyRectangle {
		MyPoint pt1 = new MyPoint();
		Point pt2 = new Point(1,2);

		public MyPoint getPt1() {
			return pt1;
		}
		public Point getPt2() {
			return pt2;
		}
	}

	private void assertDot(String expected, Object object) {
		assertEquals(expected, new ObjectDotGraphic(object).getDot());
	}
}
