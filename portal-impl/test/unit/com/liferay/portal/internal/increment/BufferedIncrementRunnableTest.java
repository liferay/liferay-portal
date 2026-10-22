/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.internal.increment;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.increment.Increment;
import com.liferay.portal.kernel.increment.NumberIncrement;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.service.ServiceContextAdvice;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class BufferedIncrementRunnableTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

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

		_companyLocalServiceUtilMockedStatic.when(
			() -> CompanyLocalServiceUtil.fetchCompany(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(Company.class)
		);
	}

	@After
	public void tearDown() {
		_companyLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testExecutionCompanyThreadLocal() {
		BatchablePipe<Serializable, Increment<?>> batchablePipe =
			new BatchablePipe<Serializable, Increment<?>>() {
				{
					put(_invoke(_TEST_COMPANY_ID_1));
					put(_invoke(_TEST_COMPANY_ID_2));
					put(_invoke(_TEST_COMPANY_ID_1));
				}
			};

		BufferedIncrementRunnable bufferedIncrementRunnable =
			new BufferedIncrementRunnable(
				Mockito.mock(BufferedIncrementConfiguration.class),
				batchablePipe, new AtomicInteger(), Thread.currentThread());

		bufferedIncrementRunnable.run();

		Assert.assertEquals(
			Integer.valueOf(1),
			_testInterceptedClass.companyInvokedMethodCounter.get(
				_TEST_COMPANY_ID_1));
		Assert.assertEquals(
			Integer.valueOf(1),
			_testInterceptedClass.companyInvokedMethodCounter.get(
				_TEST_COMPANY_ID_2));

		Assert.assertEquals(
			Integer.valueOf(2),
			_testInterceptedClass.companyIncrement.get(_TEST_COMPANY_ID_1));
		Assert.assertEquals(
			Integer.valueOf(1),
			_testInterceptedClass.companyIncrement.get(_TEST_COMPANY_ID_2));
	}

	@Test
	public void testExecutionDeletedCompany() {
		_companyLocalServiceUtilMockedStatic.when(
			() -> CompanyLocalServiceUtil.fetchCompany(CompanyConstants.SYSTEM)
		).thenReturn(
			null
		);

		_companyLocalServiceUtilMockedStatic.when(
			() -> CompanyLocalServiceUtil.fetchCompany(_TEST_COMPANY_ID_2)
		).thenReturn(
			null
		);

		BatchablePipe<Serializable, Increment<?>> batchablePipe =
			new BatchablePipe<Serializable, Increment<?>>() {
				{
					put(_invoke(CompanyConstants.SYSTEM));
					put(_invoke(_TEST_COMPANY_ID_2));
				}
			};

		BufferedIncrementRunnable bufferedIncrementRunnable =
			new BufferedIncrementRunnable(
				Mockito.mock(BufferedIncrementConfiguration.class),
				batchablePipe, new AtomicInteger(), Thread.currentThread());

		bufferedIncrementRunnable.run();

		Assert.assertEquals(
			Integer.valueOf(1),
			_testInterceptedClass.companyInvokedMethodCounter.get(
				CompanyConstants.SYSTEM));
		Assert.assertNull(
			_testInterceptedClass.companyInvokedMethodCounter.get(
				_TEST_COMPANY_ID_2));
	}

	private AopMethodInvocation _createTestMethodInvocation(Method method) {
		return ReflectionTestUtil.invoke(
			_aopInvocationHandler, "_getAopMethodInvocation",
			new Class<?>[] {Method.class}, method);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private IncreasableEntry<Serializable, Increment<?>> _invoke(
		long companyId) {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			return new BufferedIncreasableEntry(
				_createTestMethodInvocation(
					ReflectionTestUtil.getMethod(
						TestInterceptedClass.class, "method", long.class,
						long.class, long.class, int.class)),
				new Object[] {
					companyId, RandomTestUtil.randomLong(),
					RandomTestUtil.randomLong(), 1
				},
				"key" + companyId, _increment);
		}
	}

	private static final long _TEST_COMPANY_ID_1 = 1L;

	private static final long _TEST_COMPANY_ID_2 = 2L;

	private AopInvocationHandler _aopInvocationHandler;
	private final MockedStatic<CompanyLocalServiceUtil>
		_companyLocalServiceUtilMockedStatic = Mockito.mockStatic(
			CompanyLocalServiceUtil.class);
	private final Increment<Number> _increment = new NumberIncrement(1);
	private final TestInterceptedClass _testInterceptedClass =
		new TestInterceptedClass();

	private static class TestInterceptedClass {

		@SuppressWarnings("unused")
		public void method(
			long companyId, long classNameId, long classPK, int increment) {

			Assert.assertEquals(
				Long.valueOf(companyId), CompanyThreadLocal.getCompanyId());

			companyIncrement.put(companyId, increment);
			companyInvokedMethodCounter.compute(
				companyId, (key, value) -> (value == null) ? 1 : value + 1);
		}

		public Map<Long, Integer> companyIncrement = new ConcurrentHashMap<>();
		public Map<Long, Integer> companyInvokedMethodCounter =
			new ConcurrentHashMap<>();

	}

}