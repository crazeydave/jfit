// Modified or written by Object Mentor, Inc. for inclusion with FitNesse.
// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.package fit;

package fit;

import java.lang.reflect.Method;

import fit.exception.CouldNotParseFitFailureException;
import fit.exception.FitFailureException;
import fit.exception.NoSuchMethodFitFailureException;

public class ActionFixture extends Fixture {
	protected Parse cells;
	public static Fixture actor;
	protected static Class<?> empty[] = {};

	// Traversal ////////////////////////////////

	@SuppressWarnings("hiding")
	@Override
	public void doCells(Parse cells) {
		this.cells = cells;
		try {
			Method action = getClass().getMethod(cells.text(), empty);
			action.invoke(this);
		} catch (Exception e) {
			exception(cells, e);
		}
	}

	// Actions //////////////////////////////////

	public void start() throws Throwable {
		Parse fixture = cells.more;
		if (fixture == null)
			throw new FitFailureException(
					"You must specify a fixture to start.");
		actor = loadFixture(fixture.text());
	}

	public void enter() throws Exception {
		Method method = method(1);
		Class<?> type = method.getParameterTypes()[0];
		final Parse argumentCell = cells.more.more;
		if (argumentCell == null)
			throw new FitFailureException("You must specify an argument.");
		String text = argumentCell.text();
		try {
			Object[] args2 = new Object[] { TypeAdapter.on(actor, type).parse(
					text) };
			method.invoke(actor, args2);
		} catch (NumberFormatException e) {
			throw new CouldNotParseFitFailureException(text, type.getName());
		}
	}

	public void press() throws Exception {
		method(0).invoke(actor);
	}

	public void check() throws Throwable {
		TypeAdapter adapter;
		Method theMethod = method(0);
		Class<?> type = theMethod.getReturnType();
		try {
			adapter = TypeAdapter.on(actor, theMethod);
		} catch (Throwable e) {
			throw new FitFailureException("Can not parse return type: "
					+ type.getName());
		}
		Parse checkValueCell = cells.more.more;
		if (checkValueCell == null)
			throw new FitFailureException("You must specify a value to check.");

		check(checkValueCell, adapter);
	}

	// Utility //////////////////////////////////

	protected Method method(int args2) {
		final Parse methodCell = cells.more;
		if (methodCell == null)
			throw new FitFailureException("You must specify a method.");
		return method(camel(methodCell.text()), args2);
	}

	protected Method method(String test, int args2) {
		if (actor == null)
			throw new FitFailureException(
					"You must start a fixture using the 'start' keyword.");
		Method methods[] = actor.getClass().getMethods();
		Method result = null;
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			if (m.getName().equals(test)
					&& m.getParameterTypes().length == args2) {
				if (result == null) {
					result = m;
				} else {
					throw new FitFailureException("You can only have one "
							+ test + "(arg) method in your fixture.");
				}
			}
		}
		if (result == null) {
			throw new NoSuchMethodFitFailureException(test);
		}
		return result;
	}
}