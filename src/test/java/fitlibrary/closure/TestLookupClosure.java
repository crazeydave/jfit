package fitlibrary.closure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;

import fitlibrary.global.PlugBoard;
import fitlibraryGeneric.typed.GenericTypedObject;

public class TestLookupClosure {
	private Derived subject= new Derived();
	private GenericTypedObject target= new GenericTypedObject(subject);

	@Test
	public void cannotFindPrivateMethod() throws Exception {
		assertNull(PlugBoard.lookupClosure.findMethodClosure(target, "privateFunc", 1));
	}
	@Test
	public void cannotFindInheritedPrivateMethod() throws Exception {
		assertNull(PlugBoard.lookupClosure.findMethodClosure(target, "inheritedPrivateFunc", 1));
	}
	@Test
	public void findInheritedPublicMethodInOrderOfInheritance() throws Exception {
		assertMethodFoundInClass("inheritedPublicFunc", 1, BaseOfBase.class);
	}
	@Test
	public void findOverriddenPublicMethodInOrderOfInheritance() throws Exception {
		assertMethodFoundInClass("overridenPublicFunc", 1, Derived.class);
	}
	@Test
	public void findLocalGetterInOrderOfInheritance() throws Exception {
		assertMethodFoundInClass("getInt", 0, Derived.class);
	}
	@Test
	public void findInheritedPrivateGetterInOrderOfInheritance() throws Exception {
		assertMethodFoundInClass("getPrivateInt", 0, BaseOfBase.class);
	}
	@Test
	public void findSetterInOrderOfInheritance() throws Exception {
		assertMethodFoundInClass("setInt", 1, Derived.class);
		assertMethodFoundInClass("setPrivateInt", 1, BaseOfBase.class);
	}
	@Test
	public void doesNotFindObjectEqualsButDerivedClassEquals() throws Exception {
//		assertNull(LookupClosure.findMethodClosure(Traverse.asTypedObject(new BaseOfBase()), "equals", 1));
		assertMethodFoundInClass("equals", 1, Base.class);
	}
	private void assertMethodFoundInClass(String methodName, int argCount, Class<?> expectedClass) throws Exception {
		Closure method = PlugBoard.lookupClosure.findMethodClosure(target, methodName, argCount);
		assertNotNull(method);
		method.invoke(getArgs(argCount));
		assertEquals(expectedClass, subject.classCalled);
	}
	private Object[] getArgs(int argCount) {
		Object[] objects = new Object[argCount];
		Arrays.fill(objects, 1);
		return objects;
	}
	@SuppressWarnings("unused")
	static class BaseOfBase {
		public Class<?> classCalled;
		public void inheritedPublicFunc(int i) { classCalled = BaseOfBase.class; }
		private int getPrivateInt() { classCalled = BaseOfBase.class; return 1; }
		private void setPrivateInt(int i) { classCalled = BaseOfBase.class; }
		public int getInt() { classCalled = Derived.class; return 1; }
		public void setInt(int i) { classCalled = Derived.class; }
	}
	@SuppressWarnings("unused")
	static class Base extends BaseOfBase {
		private void privateFunc(int i) { classCalled = Base.class; }
		private void inheritedPrivateFunc(int i) { classCalled = Base.class; }
		public void overridenPublicFunc(int i) { classCalled = Base.class; }
		@Override
		public boolean equals(Object obj) {
			classCalled = Base.class;
			return super.equals(obj);
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}
	static class Derived extends Base {
		@SuppressWarnings("unused")
		private void privateFunc(int i) { classCalled = Derived.class; }
		@Override
		public void overridenPublicFunc(int i) { classCalled = Derived.class; }
		@Override
		public int getInt() { classCalled = Derived.class; return 1; }
		@Override
		public void setInt(int i) { classCalled = Derived.class; }
	}
}
