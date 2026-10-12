/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.module.service;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.util.Dictionary;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class SnapshotTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, NewEnvTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Bundle bundle = bundleContext.getBundle();

		ReflectionTestUtil.setFieldValue(
			bundle, "bundleContext",
			ProxyUtil.newDelegateProxyInstance(
				BundleContext.class.getClassLoader(), BundleContext.class,
				new Object() {

					public Bundle getBundle() {
						if (!_valid.getAndSet(true)) {
							throw new IllegalStateException();
						}

						return bundle;
					}

				},
				bundleContext));

		Mockito.when(
			FrameworkUtil.getBundle(SnapshotTest.class)
		).thenReturn(
			bundle
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
	}

	@Test
	public void testDynamicWithFilter() {
		Snapshot<TestService<String>> snapshot1 = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class),
			"(name=test2)", true);

		Assert.assertNull(snapshot1.get());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		TestService<String> testService1 = new TestService<>();

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(
				TestService.class, testService1,
				MapUtil.singletonDictionary("name", "test1"));

		Assert.assertNull(snapshot1.get());

		TestService<String> testService2 = new TestService<>();

		ServiceRegistration<?> serviceRegistration2 =
			bundleContext.registerService(
				TestService.class, testService2,
				MapUtil.singletonDictionary("name", "test2"));

		Assert.assertSame(testService2, snapshot1.get());

		Snapshot<TestService<String>> snapshot2 = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class),
			"(name=test1)", true);

		Assert.assertSame(testService1, snapshot2.get());

		serviceRegistration1.unregister();
		serviceRegistration2.unregister();

		Assert.assertNull(snapshot1.get());
		Assert.assertNull(snapshot2.get());
	}

	@Test
	public void testDynamicWithInvalidBundleContext() {
		Snapshot<TestService<String>> snapshot = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class), null, true);

		Assert.assertNull(snapshot.get());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		TestService<String> testService = new TestService<>();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(TestService.class, testService, null);

		Assert.assertSame(testService, snapshot.get());

		_valid.set(false);

		Assert.assertSame(testService, snapshot.get());

		serviceRegistration.unregister();

		Assert.assertNull(snapshot.get());
	}

	@Test
	public void testDynamicWithoutFilter() {
		Snapshot<TestService<String>> snapshot1 = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class), null, true);

		Assert.assertNull(snapshot1.get());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		TestService<String> testService1 = new TestService<>();

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(
				TestService.class, testService1, null);

		Assert.assertSame(testService1, snapshot1.get());

		TestService<String> testService2 = new TestService<>();

		ServiceRegistration<?> serviceRegistration2 =
			bundleContext.registerService(
				TestService.class, testService2,
				MapUtil.singletonDictionary(Constants.SERVICE_RANKING, 1));

		Assert.assertSame(testService2, snapshot1.get());

		Snapshot<TestService<String>> snapshot2 = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class), null, true);

		Assert.assertSame(testService2, snapshot2.get());

		serviceRegistration2.unregister();

		Assert.assertSame(testService1, snapshot1.get());
		Assert.assertSame(testService1, snapshot2.get());

		serviceRegistration1.unregister();

		Assert.assertNull(snapshot1.get());
		Assert.assertNull(snapshot2.get());
	}

	@Test
	public void testInvalidFilter() throws InvalidSyntaxException {
		Mockito.when(
			FrameworkUtil.getBundle(SnapshotTest.class)
		).thenReturn(
			null
		);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				TestService.class, new TestService<>(), null);

		Snapshot<TestService<String>> snapshot = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class), "(name=test");

		try {
			snapshot.get();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				InvalidSyntaxException.class, exception.getClass());

			Assert.assertEquals(
				"Missing closing parenthesis: (name=test",
				exception.getMessage());
		}

		snapshot = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class), "(name=test",
			true);

		try {
			snapshot.get();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				InvalidSyntaxException.class, exception.getClass());

			Assert.assertEquals(
				"Missing closing parenthesis: (&(objectClass=" +
					"com.liferay.portal.kernel.module.service.SnapshotTest" +
						"$TestService)(name=test)",
				exception.getMessage());
		}

		Dictionary<String, Object> dictionary =
			ReflectionTestUtil.getFieldValue(serviceRegistration, "properties");

		dictionary.put(Constants.SERVICE_ID, "(1");

		snapshot = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class));

		try {
			snapshot.get();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				InvalidSyntaxException.class, exception.getClass());

			Assert.assertEquals(
				"Unknown operator: : (service.id=(1)", exception.getMessage());
		}

		serviceRegistration.unregister();
	}

	@Test
	public void testStaticWithFilter() {
		Snapshot<TestService<String>> snapshot1 = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class),
			"(name=test2)");

		Assert.assertNull(snapshot1.get());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		TestService<String> testService1 = new TestService<>();

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(
				TestService.class, testService1,
				MapUtil.singletonDictionary("name", "test1"));

		Assert.assertNull(snapshot1.get());

		TestService<String> testService2 = new TestService<>();

		ServiceRegistration<?> serviceRegistration2 =
			bundleContext.registerService(
				TestService.class, testService2,
				MapUtil.singletonDictionary("name", "test2"));

		Assert.assertSame(testService2, snapshot1.get());

		Snapshot<TestService<String>> snapshot2 = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class),
			"(name=test1)");

		Assert.assertSame(testService1, snapshot2.get());

		serviceRegistration1.unregister();

		Assert.assertSame(testService2, snapshot1.get());
		Assert.assertNull(snapshot2.get());

		serviceRegistration2.unregister();

		Assert.assertNull(snapshot1.get());
		Assert.assertNull(snapshot2.get());
	}

	@Test
	public void testStaticWithoutFilter() {
		Snapshot<TestService<String>> snapshot1 = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class));

		Assert.assertNull(snapshot1.get());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		TestService<String> testService1 = new TestService<>();

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(
				TestService.class, testService1, null);

		Assert.assertSame(testService1, snapshot1.get());

		TestService<String> testService2 = new TestService<>();

		ServiceRegistration<?> serviceRegistration2 =
			bundleContext.registerService(
				TestService.class, testService2,
				MapUtil.singletonDictionary(Constants.SERVICE_RANKING, 1));

		Assert.assertSame(testService1, snapshot1.get());

		Snapshot<TestService<String>> snapshot2 = new Snapshot<>(
			SnapshotTest.class, Snapshot.cast(TestService.class));

		Assert.assertSame(testService2, snapshot2.get());

		serviceRegistration1.unregister();

		Assert.assertSame(testService2, snapshot1.get());
		Assert.assertSame(testService2, snapshot2.get());

		serviceRegistration2.unregister();

		Assert.assertNull(snapshot1.get());
		Assert.assertNull(snapshot2.get());
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);
	private static final AtomicBoolean _valid = new AtomicBoolean(true);

	private static class TestService<T> {
	}

}