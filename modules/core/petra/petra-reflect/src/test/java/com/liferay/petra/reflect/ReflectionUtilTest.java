/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.reflect;

import com.liferay.portal.kernel.test.SwappableSecurityManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.net.URL;
import java.net.URLClassLoader;

import java.security.Permission;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class ReflectionUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.JVMArgsLine("-Djava.security.manager=allow")
	@Test
	public void testClassInitializationFailure() throws Exception {
		SecurityException securityException = new SecurityException();

		try (SwappableSecurityManager swappableSecurityManager =
				new SwappableSecurityManager() {

					@Override
					public void checkPermission(Permission permission) {
						if (Objects.equals(
								permission.getName(),
								"accessDeclaredMembers")) {

							throw securityException;
						}
					}

				}) {

			swappableSecurityManager.install();

			Class.forName(ReflectionUtil.class.getName());

			Assert.fail();
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertSame(securityException, eiie.getCause());
		}
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.JVMArgsLine("-Djava.security.manager=allow")
	@Test
	public void testClassInitializationFallback() throws Exception {
		_runInFallbackMode(
			() -> {
				Class.forName(ReflectionUtil.class.getName());

				Field field = ReflectionUtil.class.getDeclaredField(
					"_fetchDeclaredFieldMethodHandle");

				field.setAccessible(true);

				Assert.assertNull(field.get(null));

				field = ReflectionUtil.class.getDeclaredField(
					"_fetchDeclaredMethodMethodHandle");

				field.setAccessible(true);

				Assert.assertNull(field.get(null));

				field = ReflectionUtil.class.getDeclaredField(
					"_fetchFieldMethodHandle");

				field.setAccessible(true);

				Assert.assertNull(field.get(null));

				field = ReflectionUtil.class.getDeclaredField(
					"_fetchMethodMethodHandle");

				field.setAccessible(true);

				Assert.assertNull(field.get(null));

				return null;
			});
	}

	@Test
	public void testClone() {
		try {
			ReflectionUtil.clone(new NotCloneableClass());
			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				CloneNotSupportedException.class, exception.getClass());
		}

		CloneableClass cloneableClass1 = new CloneableClass();

		CloneableClass cloneableClass2 = ReflectionUtil.clone(cloneableClass1);

		Assert.assertNotSame(cloneableClass1, cloneableClass2);
		Assert.assertEquals(cloneableClass1._id, cloneableClass2._id);
		Assert.assertSame(cloneableClass1._name, cloneableClass2._name);
		Assert.assertSame(cloneableClass1._obj, cloneableClass2._obj);
	}

	@Test
	public void testConstructor() throws Exception {
		new ReflectionUtil();

		Field field = ReflectionUtil.class.getDeclaredField(
			"_fetchDeclaredFieldMethodHandle");

		field.setAccessible(true);

		Assert.assertNotNull(field.get(null));

		field = ReflectionUtil.class.getDeclaredField(
			"_fetchDeclaredMethodMethodHandle");

		field.setAccessible(true);

		Assert.assertNotNull(field.get(null));

		field = ReflectionUtil.class.getDeclaredField(
			"_fetchFieldMethodHandle");

		field.setAccessible(true);

		Assert.assertNotNull(field.get(null));

		field = ReflectionUtil.class.getDeclaredField(
			"_fetchMethodMethodHandle");

		field.setAccessible(true);

		Assert.assertNotNull(field.get(null));
	}

	@Test
	public void testFetchDeclaredField() throws Exception {
		Field staticField = ReflectionUtil.fetchDeclaredField(
			TestClass.class, "_privateStaticFinalObject");

		Assert.assertTrue(staticField.isAccessible());
		Assert.assertSame(
			TestClass._privateStaticFinalObject, staticField.get(null));

		staticField = ReflectionUtil.fetchDeclaredField(
			false, TestClass.class, "_privateStaticFinalObject");

		Assert.assertFalse(staticField.isAccessible());
		Assert.assertSame(
			TestClass._privateStaticFinalObject, staticField.get(null));

		TestClass testClass = new TestClass();

		Field field = ReflectionUtil.fetchDeclaredField(
			TestClass.class, "_privateFinalObject");

		Assert.assertTrue(field.isAccessible());
		Assert.assertTrue(Modifier.isFinal(field.getModifiers()));
		Assert.assertSame(testClass._privateFinalObject, field.get(testClass));

		Assert.assertNull(
			ReflectionUtil.fetchDeclaredField(
				TestClass.class, "_notExistField"));

		try {
			ReflectionUtil.fetchDeclaredField(null, "_notExistField");

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
		}
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.JVMArgsLine("-Djava.security.manager=allow")
	@Test
	public void testFetchDeclaredFieldFallback() throws Exception {
		_runInFallbackMode(
			() -> {
				Field staticField = ReflectionUtil.fetchDeclaredField(
					TestClass.class, "_privateStaticFinalObject");

				Assert.assertTrue(staticField.isAccessible());
				Assert.assertSame(
					TestClass._privateStaticFinalObject, staticField.get(null));

				TestClass testClass = new TestClass();

				Field field = ReflectionUtil.fetchDeclaredField(
					TestClass.class, "_privateFinalObject");

				Assert.assertTrue(field.isAccessible());
				Assert.assertTrue(Modifier.isFinal(field.getModifiers()));
				Assert.assertSame(
					testClass._privateFinalObject, field.get(testClass));

				Assert.assertNull(
					ReflectionUtil.fetchDeclaredField(
						TestClass.class, "_notExistField"));

				return null;
			});
	}

	@Test
	public void testFetchDeclaredMethod() throws Exception {
		Method method = ReflectionUtil.fetchDeclaredMethod(
			TestClass.class, "_getPrivateStaticObject");

		Assert.assertTrue(method.isAccessible());
		Assert.assertSame(TestClass._privateStaticObject, method.invoke(null));

		method = ReflectionUtil.fetchDeclaredMethod(
			false, TestClass.class, "_getPrivateStaticObject");

		Assert.assertFalse(method.isAccessible());
		Assert.assertSame(TestClass._privateStaticObject, method.invoke(null));

		Assert.assertNull(
			ReflectionUtil.fetchDeclaredMethod(
				TestClass.class, "_notExistMethod"));

		try {
			ReflectionUtil.fetchDeclaredMethod(null, "_notExistMethod");

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
		}
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.JVMArgsLine("-Djava.security.manager=allow")
	@Test
	public void testFetchDeclaredMethodFallback() throws Exception {
		_runInFallbackMode(
			() -> {
				Method method = ReflectionUtil.fetchDeclaredMethod(
					TestClass.class, "_getPrivateStaticObject");

				Assert.assertTrue(method.isAccessible());
				Assert.assertSame(
					TestClass._privateStaticObject, method.invoke(null));

				Assert.assertNull(
					ReflectionUtil.fetchDeclaredMethod(
						TestClass.class, "_notExistMethod"));

				return null;
			});
	}

	@Test
	public void testFetchField() throws Exception {
		Field staticField = ReflectionUtil.fetchField(
			TestClass.class, "publicStaticObject");

		Assert.assertTrue(staticField.isAccessible());
		Assert.assertSame(TestClass.publicStaticObject, staticField.get(null));

		staticField = ReflectionUtil.fetchField(
			false, TestClass.class, "publicStaticObject");

		Assert.assertFalse(staticField.isAccessible());
		Assert.assertSame(TestClass.publicStaticObject, staticField.get(null));

		TestClass testClass = new TestClass();

		Field field = ReflectionUtil.fetchField(
			TestClass.class, "publicObject");

		Assert.assertTrue(field.isAccessible());
		Assert.assertTrue(Modifier.isFinal(field.getModifiers()));
		Assert.assertSame(testClass.publicObject, field.get(testClass));

		Assert.assertNull(
			ReflectionUtil.fetchField(TestClass.class, "_notExistField"));

		try {
			ReflectionUtil.fetchField(null, "_notExistField");

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
		}
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.JVMArgsLine("-Djava.security.manager=allow")
	@Test
	public void testFetchFieldFallback() throws Exception {
		_runInFallbackMode(
			() -> {
				Field staticField = ReflectionUtil.fetchField(
					TestClass.class, "publicStaticObject");

				Assert.assertTrue(staticField.isAccessible());
				Assert.assertSame(
					TestClass.publicStaticObject, staticField.get(null));

				staticField = ReflectionUtil.fetchField(
					false, TestClass.class, "publicStaticObject");

				Assert.assertFalse(staticField.isAccessible());
				Assert.assertSame(
					TestClass.publicStaticObject, staticField.get(null));

				TestClass testClass = new TestClass();

				Field field = ReflectionUtil.fetchField(
					TestClass.class, "publicObject");

				Assert.assertTrue(field.isAccessible());
				Assert.assertTrue(Modifier.isFinal(field.getModifiers()));
				Assert.assertSame(testClass.publicObject, field.get(testClass));

				Assert.assertNull(
					ReflectionUtil.fetchField(
						TestClass.class, "_notExistField"));

				return null;
			});
	}

	@Test
	public void testFetchMethod() throws Exception {
		Method method = ReflectionUtil.fetchMethod(
			TestClass.class, "getPrivateStaticObject");

		Assert.assertTrue(method.isAccessible());
		Assert.assertSame(TestClass._privateStaticObject, method.invoke(null));

		method = ReflectionUtil.fetchMethod(
			false, TestClass.class, "getPrivateStaticObject");

		Assert.assertFalse(method.isAccessible());
		Assert.assertSame(TestClass._privateStaticObject, method.invoke(null));

		Assert.assertNull(
			ReflectionUtil.fetchMethod(TestClass.class, "_notExistMethod"));

		try {
			ReflectionUtil.fetchMethod(null, "_notExistMethod");

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
		}
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.JVMArgsLine("-Djava.security.manager=allow")
	@Test
	public void testFetchMethodFallback() throws Exception {
		_runInFallbackMode(
			() -> {
				Method method = ReflectionUtil.fetchMethod(
					TestClass.class, "getPrivateStaticObject");

				Assert.assertTrue(method.isAccessible());
				Assert.assertSame(
					TestClass._privateStaticObject, method.invoke(null));

				method = ReflectionUtil.fetchMethod(
					false, TestClass.class, "getPrivateStaticObject");

				Assert.assertFalse(method.isAccessible());
				Assert.assertSame(
					TestClass._privateStaticObject, method.invoke(null));

				Assert.assertNull(
					ReflectionUtil.fetchMethod(
						TestClass.class, "_notExistMethod"));

				return null;
			});
	}

	@Test
	public void testGetDeclaredField() throws Exception {
		Field staticField = ReflectionUtil.getDeclaredField(
			TestClass.class, "_privateStaticFinalObject");

		Assert.assertTrue(staticField.isAccessible());
		Assert.assertSame(
			TestClass._privateStaticFinalObject, staticField.get(null));

		staticField = ReflectionUtil.getDeclaredField(
			false, TestClass.class, "_privateStaticFinalObject");

		Assert.assertFalse(staticField.isAccessible());
		Assert.assertSame(
			TestClass._privateStaticFinalObject, staticField.get(null));

		TestClass testClass = new TestClass();

		Field field = ReflectionUtil.getDeclaredField(
			TestClass.class, "_privateFinalObject");

		Assert.assertTrue(field.isAccessible());
		Assert.assertTrue(Modifier.isFinal(field.getModifiers()));
		Assert.assertSame(testClass._privateFinalObject, field.get(testClass));
	}

	@Test
	public void testGetDeclaredFields() throws Exception {
		Field[] fields = ReflectionUtil.getDeclaredFields(TestClass.class);

		for (Field field : fields) {
			Assert.assertTrue(field.isAccessible());

			String name = field.getName();

			if (name.equals("_privateStaticFinalObject")) {
				Assert.assertSame(
					TestClass._privateStaticFinalObject, field.get(null));
			}
			else if (name.equals("_privateStaticObject")) {
				Assert.assertSame(
					TestClass._privateStaticObject, field.get(null));
			}
		}

		fields = ReflectionUtil.getDeclaredFields(false, TestClass.class);

		for (Field field : fields) {
			Assert.assertFalse(field.isAccessible());

			String name = field.getName();

			if (name.equals("_privateStaticFinalObject")) {
				Assert.assertSame(
					TestClass._privateStaticFinalObject, field.get(null));
			}
			else if (name.equals("_privateStaticObject")) {
				Assert.assertSame(
					TestClass._privateStaticObject, field.get(null));
			}
		}
	}

	@Test
	public void testGetDeclaredMethod() throws Exception {
		Method method = ReflectionUtil.getDeclaredMethod(
			TestClass.class, "_getPrivateStaticObject");

		Assert.assertTrue(method.isAccessible());
		Assert.assertSame(TestClass._privateStaticObject, method.invoke(null));

		method = ReflectionUtil.getDeclaredMethod(
			false, TestClass.class, "_getPrivateStaticObject");

		Assert.assertFalse(method.isAccessible());
		Assert.assertSame(TestClass._privateStaticObject, method.invoke(null));
	}

	@Test
	public void testGetImplLookup() {
		Assert.assertNotNull(ReflectionUtil.getImplLookup());
	}

	@Test
	public void testGetInterfaces() {
		Class<?>[] interfaces = ReflectionUtil.getInterfaces(new TestClass());

		Assert.assertEquals(Arrays.toString(interfaces), 1, interfaces.length);
		Assert.assertSame(TestInterface.class, interfaces[0]);

		AtomicReference<ClassNotFoundException> atomicReference =
			new AtomicReference<>();

		interfaces = ReflectionUtil.getInterfaces(
			new TestClass(), new URLClassLoader(new URL[0], null));

		Assert.assertEquals(Arrays.toString(interfaces), 0, interfaces.length);

		interfaces = ReflectionUtil.getInterfaces(
			new TestClass(), new URLClassLoader(new URL[0], null),
			atomicReference::set);

		Assert.assertEquals(Arrays.toString(interfaces), 0, interfaces.length);

		Assert.assertNotNull(atomicReference.get());
	}

	@Test
	public void testThrowException() {
		Exception exception1 = new Exception();

		try {
			ReflectionUtil.throwException(exception1);

			Assert.fail();
		}
		catch (Exception exception2) {
			Assert.assertSame(exception1, exception2);
		}
	}

	private void _runInFallbackMode(Callable<Void> callable) throws Exception {
		Thread currentThread = Thread.currentThread();

		AtomicInteger counter = new AtomicInteger();

		try (SwappableSecurityManager swappableSecurityManager =
				new SwappableSecurityManager() {

					@Override
					public void checkPermission(Permission permission) {
						if ((currentThread == Thread.currentThread()) &&
							Objects.equals(
								permission.getName(),
								"accessDeclaredMembers") &&
							(counter.incrementAndGet() == 2)) {

							throw new SecurityException();
						}
					}

				}) {

			swappableSecurityManager.install();

			callable.call();
		}
	}

	private static class TestClass implements TestInterface {

		public static final Object publicStaticObject = new Object();

		public static Object getPrivateStaticObject() {
			return _privateStaticObject;
		}

		public static void setPrivateStaticObject(Object privateStaticObject) {
			_privateStaticObject = privateStaticObject;
		}

		public void setPrivateObject(Object privateObject) {
			_privateObject = privateObject;
		}

		public final Object publicObject = new Object();

		@SuppressWarnings("unused")
		private static Object _getPrivateStaticObject() {
			return _privateStaticObject;
		}

		@SuppressWarnings("unused")
		private Object _getPrivateObject() {
			return _privateObject;
		}

		private static final Object _privateStaticFinalObject = new Object();
		private static Object _privateStaticObject = new Object();

		private final Object _privateFinalObject = new Object();
		private Object _privateObject = new Object();

	}

	private class CloneableClass implements Cloneable {

		private final long _id = 1;
		private final String _name = CloneableClass.class.getName();
		private final Object _obj = new Object();

	}

	private class NotCloneableClass {
	}

	private interface TestInterface {
	}

}