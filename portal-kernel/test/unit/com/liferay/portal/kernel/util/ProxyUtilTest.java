/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ProxyUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		new CodeCoverageAssertor() {

			@Override
			public void appendAssertClasses(List<Class<?>> assertClasses) {
				assertClasses.clear();

				for (Class<?> clazz : ProxyUtil.class.getDeclaredClasses()) {
					if (InvocationHandler.class.isAssignableFrom(clazz)) {
						assertClasses.add(clazz);

						break;
					}
				}
			}

		};

	@Test
	public void testDelegateInvocationHandlerInitialization() throws Exception {
		Class<?> clazz = Class.forName(
			ProxyUtil.class.getName() + "$DelegateInvocationHandler");

		Assert.assertEquals(
			Object.class.getMethod("equals", Object.class),
			ReflectionTestUtil.getFieldValue(clazz, "_equalsMethod"));
		Assert.assertEquals(
			Object.class.getMethod("hashCode"),
			ReflectionTestUtil.getFieldValue(clazz, "_hashCodeMethod"));
		Assert.assertEquals(
			Object.class.getMethod("toString"),
			ReflectionTestUtil.getFieldValue(clazz, "_toStringMethod"));
	}

	@Test
	public void testLazyDelegateProxyInstance() {
		AtomicInteger counter = new AtomicInteger();

		Supplier<TestInterface> supplier = () -> {
			counter.incrementAndGet();

			return new TestInterface() {

				@Override
				public String method1() {
					return "method1";
				}

				@Override
				public String method2() {
					return "method2";
				}

				@Override
				public void method3() {
				}

			};
		};

		TestInterface testInterface = ProxyUtil.newLazyDelegateProxyInstance(
			TestInterface.class.getClassLoader(), TestInterface.class,
			supplier);

		Assert.assertEquals(0, counter.get());
		Assert.assertEquals("method1", testInterface.method1());
		Assert.assertEquals(1, counter.get());
		Assert.assertEquals("method2", testInterface.method2());
		Assert.assertEquals(1, counter.get());
	}

	@Test
	public void testNewDelegateProxyInstance() {
		TestInterface defaultTestInterface = new TestInterface() {

			@Override
			public String method1() {
				return "defaultMethod1";
			}

			@Override
			public String method2() {
				return "defaultMethod2";
			}

			@Override
			public void method3() {
			}

			@Override
			public String toString() {
				return "Default Object";
			}

		};

		Object identityObject = new Object();

		TestInterface testInterface = ProxyUtil.newDelegateProxyInstance(
			TestInterface.class.getClassLoader(), TestInterface.class,
			new DelegateClass(identityObject), defaultTestInterface);

		Assert.assertEquals("delegateMethod1", testInterface.method1());
		Assert.assertEquals("defaultMethod2", testInterface.method2());

		try {
			testInterface.method3();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertSame(
				DelegateClass._ILLEGAL_STATE_EXCEPTION, illegalStateException);
		}

		Assert.assertEquals("Delegate Object", testInterface.toString());
		Assert.assertEquals(testInterface, identityObject);
		Assert.assertNotEquals(testInterface, defaultTestInterface);
		Assert.assertEquals(
			testInterface.hashCode(), identityObject.hashCode());
	}

	private static class DelegateClass {

		public static void irrelevantMethod4() {
		}

		public void equals(Long number) {
		}

		public void equals(Long number1, Long number2) {
		}

		@Override
		public boolean equals(Object object) {
			if (object == _identityObject) {
				return true;
			}

			return false;
		}

		public void equals(Object object1, Object object2) {
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(_identityObject);
		}

		public int hashCode(Object object) {
			return object.hashCode();
		}

		public void irrelevantMethod1() {
		}

		public String method1() {
			return "delegateMethod1";
		}

		public void method3() {
			throw _ILLEGAL_STATE_EXCEPTION;
		}

		@Override
		public String toString() {
			return "Delegate Object";
		}

		public String toString(Object object) {
			return object.toString();
		}

		protected void irrelevantMethod2() {
		}

		private DelegateClass(Object identityObject) {
			_identityObject = identityObject;

			_irrelevantMethod3();
		}

		private void _irrelevantMethod3() {
		}

		private static final IllegalStateException _ILLEGAL_STATE_EXCEPTION =
			new IllegalStateException();

		private final Object _identityObject;

	}

	private interface TestInterface {

		public String method1();

		public String method2();

		public void method3();

	}

}