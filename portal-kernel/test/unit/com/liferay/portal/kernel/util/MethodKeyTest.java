/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.process.ClassPathUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.net.URLClassLoader;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class MethodKeyTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testConstructors() throws NoSuchMethodException {
		_assertMethodKey(
			TestClass.class, _METHOD_NAME, new Class<?>[0],
			new MethodKey(TestClass.class, _METHOD_NAME));
		_assertMethodKey(
			TestClass.class, _METHOD_NAME, new Class<?>[] {String.class},
			new MethodKey(TestClass.class, _METHOD_NAME, String.class));
		_assertMethodKey(
			TestClass.class, _METHOD_NAME,
			new Class<?>[] {String.class, String.class},
			new MethodKey(
				TestClass.class, _METHOD_NAME, String.class, String.class));
		_assertMethodKey(
			TestClass.class, _METHOD_NAME, new Class<?>[0],
			new MethodKey(TestClass.class.getMethod(_METHOD_NAME)));
	}

	@Test
	public void testEquals() throws Exception {
		MethodKey methodKey = new MethodKey(
			TestClass.class, _METHOD_NAME, String.class);

		Assert.assertEquals(methodKey, methodKey);
		Assert.assertEquals(methodKey, _clone(methodKey));
		Assert.assertEquals(
			methodKey,
			new MethodKey(TestClass.class, _METHOD_NAME, String.class));
		Assert.assertNotEquals(methodKey, new Object());
		Assert.assertNotEquals(
			methodKey,
			new MethodKey(
				TestClass.class, _METHOD_NAME, String.class, String.class));
		Assert.assertNotEquals(
			methodKey, new MethodKey(TestClass.class, _METHOD_NAME, int.class));
		Assert.assertNotEquals(
			methodKey,
			new MethodKey(TestClass.class, "testMethodAnother", String.class));
		Assert.assertNotEquals(
			methodKey, new MethodKey(Object.class, _METHOD_NAME, String.class));
	}

	@Test
	public void testGetMethodAndResetCache() throws NoSuchMethodException {
		MethodKey methodKey = new MethodKey(
			TestClass.class, _METHOD_NAME, String.class);

		Method method = methodKey.getMethod();

		Assert.assertSame(method, methodKey.getMethod());
		Assert.assertTrue(
			"The method should be accessible", method.isAccessible());

		method.setAccessible(false);

		Assert.assertFalse(
			"The method should not be accessible", method.isAccessible());
		Assert.assertSame(method, methodKey.getMethod());
		Assert.assertTrue(
			"The method should be accessible", method.isAccessible());

		Map<MethodKey, Method> methods = ReflectionTestUtil.getFieldValue(
			MethodKey.class, "_methods");

		Assert.assertEquals(methods.toString(), 1, methods.size());
		Assert.assertEquals(method, methods.get(methodKey));

		MethodKey.resetCache();

		Assert.assertTrue(methods.toString(), methods.isEmpty());
	}

	@Test
	public void testHashCode() throws Exception {
		Method method = TestClass.class.getMethod(_METHOD_NAME, String.class);

		MethodKey methodKey = new MethodKey(
			TestClass.class, _METHOD_NAME, String.class);

		Assert.assertEquals(method.hashCode(), methodKey.hashCode());

		MethodKey serializedMethodKey = _clone(methodKey);

		Assert.assertEquals(method.hashCode(), serializedMethodKey.hashCode());
	}

	@Test
	public void testToString() {
		MethodKey methodKey = new MethodKey(TestClass.class, _METHOD_NAME);

		Assert.assertEquals(
			TestClass.class.getName() + ".testMethod()", methodKey.toString());

		methodKey = new MethodKey(TestClass.class, _METHOD_NAME, String.class);

		Assert.assertEquals(
			TestClass.class.getName() + ".testMethod(java.lang.String)",
			methodKey.toString());

		methodKey = new MethodKey(
			TestClass.class, _METHOD_NAME, String.class, String.class);

		Assert.assertEquals(
			TestClass.class.getName() +
				".testMethod(java.lang.String,java.lang.String)",
			methodKey.toString());

		Assert.assertSame(methodKey.toString(), methodKey.toString());
	}

	@Test
	public void testTransform() throws Exception {
		MethodKey methodKey = new MethodKey(
			TestClass.class, _METHOD_NAME, String.class);

		MethodKey transformedMethodKey = methodKey.transform(
			new URLClassLoader(
				ClassPathUtil.getClassPathURLs(
					ClassPathUtil.getJVMClassPath(false)),
				null));

		Assert.assertNotEquals(methodKey, transformedMethodKey);

		Class<?> transformedClass = transformedMethodKey.getDeclaringClass();

		Constructor<?> transformedClassConstructor =
			transformedClass.getDeclaredConstructor();

		transformedClassConstructor.setAccessible(true);

		Object transformedClassInstance =
			transformedClassConstructor.newInstance();

		Method method = methodKey.getMethod();

		try {
			method.invoke(transformedClassInstance, "test");

			Assert.fail("IllegalArgumentException was not thrown");
		}
		catch (IllegalArgumentException illegalArgumentException) {
			String message = illegalArgumentException.getMessage();

			Assert.assertTrue(message.contains("is not an instance of"));
		}

		Method transformedMethod = transformedMethodKey.getMethod();

		Assert.assertEquals(
			"test", transformedMethod.invoke(transformedClassInstance, "test"));
	}

	private void _assertMethodKey(
		Class<?> expectedClass, String expectedMethodName,
		Class<?>[] expectedParameters, MethodKey methodKey) {

		Assert.assertSame(expectedClass, methodKey.getDeclaringClass());
		Assert.assertEquals(expectedMethodName, methodKey.getMethodName());
		Assert.assertArrayEquals(
			expectedParameters, methodKey.getParameterTypes());
	}

	private MethodKey _clone(MethodKey methodKey) throws Exception {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					unsyncByteArrayOutputStream)) {

				objectOutputStream.writeObject(methodKey);
			}

			try (UnsyncByteArrayInputStream unsyncByteArrayInputStream =
					new UnsyncByteArrayInputStream(
						unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
						unsyncByteArrayOutputStream.size());

				ObjectInputStream objectInputStream = new ObjectInputStream(
					unsyncByteArrayInputStream)) {

				return (MethodKey)objectInputStream.readObject();
			}
		}
	}

	private static final String _METHOD_NAME = "testMethod";

	private static class TestClass {

		public void testMethod() {
		}

		public void testMethod(int parameter) {
		}

		public String testMethod(String parameter) {
			return parameter;
		}

		public void testMethod(String parameter1, String parameter2) {
		}

		public void testMethodAnother(String parameter) {
		}

	}

}