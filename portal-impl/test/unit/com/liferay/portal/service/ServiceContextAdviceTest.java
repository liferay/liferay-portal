/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service;

import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class ServiceContextAdviceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Constructor<AopInvocationHandler> constructor =
			AopInvocationHandler.class.getDeclaredConstructor(
				Object.class, ChainableMethodAdvice[].class,
				TransactionExecutor.class);

		constructor.setAccessible(true);

		_aopInvocationHandler = constructor.newInstance(
			_testInterceptedClass,
			new ChainableMethodAdvice[] {new ServiceContextAdvice()}, null);
	}

	@Test
	public void testThreadLocalValue() throws Throwable {
		ServiceContext serviceContext1 = new ServiceContext();

		ServiceContextThreadLocal.pushServiceContext(serviceContext1);

		Method method = ReflectionTestUtil.getMethod(
			TestInterceptedClass.class, "method", ServiceContext.class);

		ServiceContext serviceContext2 = new ServiceContext();

		AopMethodInvocation aopMethodInvocation = _createTestMethodInvocation(
			method);

		aopMethodInvocation.proceed(new Object[] {serviceContext2});

		Assert.assertSame(
			serviceContext1, ServiceContextThreadLocal.popServiceContext());
	}

	@Test
	public void testWithException() {
		AopMethodInvocation aopMethodInvocation = _createTestMethodInvocation(
			ReflectionTestUtil.getMethod(
				TestInterceptedClass.class, "method", ServiceContext.class));

		try {
			aopMethodInvocation.proceed(new Object[] {null});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertTrue(throwable instanceof IllegalStateException);
		}
	}

	@Test
	public void testWithNoArguments() {
		AopMethodInvocation aopMethodInvocation = ReflectionTestUtil.invoke(
			_aopInvocationHandler, "_getAopMethodInvocation",
			new Class<?>[] {Method.class},
			ReflectionTestUtil.getMethod(TestInterceptedClass.class, "method"));

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				aopMethodInvocation, "_nextAopMethodInvocation"));
	}

	@Test
	public void testWithNullServiceContext() throws Throwable {
		ServiceContext serviceContext = new ServiceContext();

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		AopMethodInvocation aopMethodInvocation = _createTestMethodInvocation(
			ReflectionTestUtil.getMethod(
				TestInterceptedClass.class, "method", ServiceContext.class));

		aopMethodInvocation.proceed(new Object[] {null});

		Assert.assertSame(
			serviceContext, ServiceContextThreadLocal.popServiceContext());
	}

	@Test
	public void testWithServiceContextWrapper() throws Throwable {
		ServiceContext serviceContext = new ServiceContext();

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		Method method = ReflectionTestUtil.getMethod(
			TestInterceptedClass.class, "method",
			TestServiceContextWrapper.class, Object.class);

		TestServiceContextWrapper testServiceContextWrapper =
			new TestServiceContextWrapper();

		AopMethodInvocation aopMethodInvocation = _createTestMethodInvocation(
			method);

		aopMethodInvocation.proceed(
			new Object[] {testServiceContextWrapper, null});

		Assert.assertSame(
			serviceContext, ServiceContextThreadLocal.popServiceContext());
	}

	@Test
	public void testWithoutServiceContextParameter() {
		ServiceContextThreadLocal.pushServiceContext(new ServiceContext());

		AopMethodInvocation aopMethodInvocation = ReflectionTestUtil.invoke(
			_aopInvocationHandler, "_getAopMethodInvocation",
			new Class<?>[] {Method.class},
			ReflectionTestUtil.getMethod(
				TestInterceptedClass.class, "method", Object.class));

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				aopMethodInvocation, "_nextAopMethodInvocation"));
	}

	private AopMethodInvocation _createTestMethodInvocation(Method method) {
		return ReflectionTestUtil.invoke(
			_aopInvocationHandler, "_getAopMethodInvocation",
			new Class<?>[] {Method.class}, method);
	}

	private AopInvocationHandler _aopInvocationHandler;
	private final TestInterceptedClass _testInterceptedClass =
		new TestInterceptedClass();

	private static class TestInterceptedClass {

		@SuppressWarnings("unused")
		public void method() {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unused")
		public void method(Object object) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unused")
		public void method(ServiceContext serviceContext) {
			if (ServiceContextThreadLocal.getServiceContext() == null) {
				throw new IllegalStateException();
			}

			if (serviceContext == null) {
				Assert.assertNotNull(
					ServiceContextThreadLocal.getServiceContext());
			}
			else {
				Assert.assertSame(
					serviceContext,
					ServiceContextThreadLocal.getServiceContext());
			}
		}

		@SuppressWarnings("unused")
		public void method(
			TestServiceContextWrapper serviceContextWrapper, Object object) {

			Assert.assertSame(
				serviceContextWrapper,
				ServiceContextThreadLocal.getServiceContext());
		}

	}

	private static class TestServiceContextWrapper extends ServiceContext {
	}

}