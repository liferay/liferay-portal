/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.aop;

import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ChainableMethodAdviceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAfterReturningKeepsResultByDefault() {
		TestInterface testInterface = _createTestInterface(
			new TestChainableMethodAdvice());

		Assert.assertSame(_ORIGINAL_VALUE, testInterface.getValue());
	}

	@Test
	public void testAfterReturningReplacesResult() {
		TestInterface testInterface = _createTestInterface(
			new TestChainableMethodAdvice() {

				@Override
				protected Object afterReturning(
					AopMethodInvocation aopMethodInvocation, Object[] arguments,
					Object result) {

					return _REPLACED_VALUE;
				}

			});

		Assert.assertSame(_REPLACED_VALUE, testInterface.getValue());
	}

	@Test
	public void testAfterReturningReturnsNull() {
		TestInterface testInterface = _createTestInterface(
			new TestChainableMethodAdvice() {

				@Override
				protected Object afterReturning(
					AopMethodInvocation aopMethodInvocation, Object[] arguments,
					Object result) {

					return null;
				}

			});

		Assert.assertNull(testInterface.getValue());
	}

	private TestInterface _createTestInterface(
		ChainableMethodAdvice chainableMethodAdvice) {

		AopInvocationHandler aopInvocationHandler = new AopInvocationHandler(
			new TestInterfaceImpl(),
			new ChainableMethodAdvice[] {chainableMethodAdvice}, null);

		return (TestInterface)ProxyUtil.newProxyInstance(
			ChainableMethodAdviceTest.class.getClassLoader(),
			new Class<?>[] {TestInterface.class}, aopInvocationHandler);
	}

	private static final String _ORIGINAL_VALUE = "original";

	private static final String _REPLACED_VALUE = "replaced";

	private static class TestChainableMethodAdvice
		extends ChainableMethodAdvice {

		@Override
		public Object createMethodContext(
			Object target, Method method,
			Map<Class<? extends Annotation>, Annotation> annotations) {

			return method;
		}

	}

	private static class TestInterfaceImpl implements TestInterface {

		@Override
		public String getValue() {
			return _ORIGINAL_VALUE;
		}

	}

	private interface TestInterface {

		public String getValue();

	}

}