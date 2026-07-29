/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.aop;

import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Preston Crary
 */
public class AopCacheManagerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_aopInvocationHandler = AopCacheManager.create(
			new TestInterfaceImpl(), null);

		_testInterfaceProxy = (TestInterface)ProxyUtil.newProxyInstance(
			AopCacheManagerTest.class.getClassLoader(),
			new Class<?>[] {TestInterface.class}, _aopInvocationHandler);

		Runtime runtime = Runtime.getRuntime();

		_executorService = Executors.newFixedThreadPool(
			runtime.availableProcessors());
	}

	@After
	public void tearDown() {
		AopCacheManager.destroy(_aopInvocationHandler);

		_executorService.shutdownNow();
	}

	@Test
	public void testConcurrentRegistrationAndInvocations() throws Exception {
		List<Callable<Void>> callables = new ArrayList<>(1000);

		for (int i = 0; i < 1000; i++) {
			ClassLoader classLoader =
				AopCacheManagerTest.class.getClassLoader();

			Class<?> clazz = classLoader.loadClass(
				TestChainableMethodAdvice.class.getName() + (i % 10));

			TestChainableMethodAdvice testChainableMethodAdvice =
				(TestChainableMethodAdvice)clazz.newInstance();

			callables.add(
				() -> {
					BundleContext bundleContext =
						SystemBundleUtil.getBundleContext();

					ServiceRegistration<?> serviceRegistration =
						bundleContext.registerService(
							ChainableMethodAdvice.class,
							testChainableMethodAdvice, null);

					List<Object> advices = new ArrayList<>();

					synchronized (_aopInvocationHandler) {
						_testInterfaceProxy.assertAop(advices, null);
					}

					Assert.assertTrue(
						advices.toString(),
						advices.contains(testChainableMethodAdvice));

					serviceRegistration.unregister();

					advices.clear();

					synchronized (_aopInvocationHandler) {
						_testInterfaceProxy.assertAop(advices, null);
					}

					Assert.assertFalse(
						advices.toString(),
						advices.contains(testChainableMethodAdvice));

					_aopInvocationHandler.setTarget(new TestInterfaceImpl());

					return null;
				});
		}

		Collections.shuffle(callables);

		List<Future<Void>> futures = _executorService.invokeAll(callables);

		for (Future<Void> future : futures) {
			future.get();
		}
	}

	public static class TestChainableMethodAdvice
		extends ChainableMethodAdvice {

		@Override
		public Object createMethodContext(
			Object target, Method method,
			Map<Class<? extends Annotation>, Annotation> annotations) {

			Assert.assertSame(TestInterfaceImpl.class, target.getClass());

			return nullResult;
		}

		@Override
		protected Object before(
			AopMethodInvocation aopMethodInvocation, Object[] arguments) {

			List<Object> advices = (List<Object>)arguments[0];

			advices.add(this);

			Object target = arguments[1];

			if (target == null) {
				arguments[1] = aopMethodInvocation.getThis();
			}
			else {
				Assert.assertSame(target, aopMethodInvocation.getThis());
			}

			return null;
		}

	}

	public static class TestChainableMethodAdvice0
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice1
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice2
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice3
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice4
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice5
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice6
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice7
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice8
		extends TestChainableMethodAdvice {
	}

	public static class TestChainableMethodAdvice9
		extends TestChainableMethodAdvice {
	}

	private AopInvocationHandler _aopInvocationHandler;
	private ExecutorService _executorService;
	private TestInterface _testInterfaceProxy;

	private static class TestInterfaceImpl implements TestInterface {

		@Override
		public void assertAop(List<Object> advices, Object targetPlaceholder) {
			if (advices.isEmpty()) {
				Assert.assertNull(targetPlaceholder);
			}
			else {
				Assert.assertSame(this, targetPlaceholder);

				List<String> adviceNames = new ArrayList<>(advices.size());

				for (Object advice : advices) {
					Class<?> clazz = advice.getClass();

					adviceNames.add(clazz.getName());
				}

				List<String> expectedAdviceNames = new ArrayList<>(adviceNames);

				expectedAdviceNames.sort(null);

				Assert.assertEquals(
					adviceNames.toString(), expectedAdviceNames, adviceNames);
			}
		}

	}

	private interface TestInterface {

		public void assertAop(List<Object> advices, Object targetPlaceholder);

	}

}