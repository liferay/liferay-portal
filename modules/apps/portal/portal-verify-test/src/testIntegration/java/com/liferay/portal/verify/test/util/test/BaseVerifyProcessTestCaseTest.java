/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class BaseVerifyProcessTestCaseTest extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	@Override
	public void tearDown() throws Exception {
		for (Connection connection : _connections) {
			DataAccess.cleanUp(connection);
		}

		super.tearDown();
	}

	@Override
	@Test
	public void testVerify() throws Exception {
		_testVerifyWithSchedulerConnection();
		_testVerifyWithVerifyProcessConnection();
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return _verifyProcess;
	}

	private Bundle _getBundle(String symbolicName) {
		Bundle bundle = FrameworkUtil.getBundle(
			BaseVerifyProcessTestCaseTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (Bundle currentBundle : bundleContext.getBundles()) {
			if (Objects.equals(currentBundle.getSymbolicName(), symbolicName)) {
				return currentBundle;
			}
		}

		throw new IllegalStateException(
			"Unable to find bundle " + symbolicName);
	}

	private void _testVerifyWithSchedulerConnection() throws Exception {
		Bundle bundle = _getBundle("com.liferay.portal.scheduler.quartz");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.portal.scheduler.quartz.internal." +
				"QuartzConnectionProvider");

		Constructor<?> constructor = clazz.getConstructor();

		Object connectionProvider = constructor.newInstance();

		Method method = clazz.getMethod("getConnection");

		_verifyProcess = new VerifyProcess() {

			@Override
			protected void doVerify() throws Exception {
				Connection connection = (Connection)method.invoke(
					connectionProvider);

				Assert.assertNotNull(connection);

				_connections.add(connection);
			}

		};

		super.testVerify();
	}

	private void _testVerifyWithVerifyProcessConnection() throws Exception {
		_verifyProcess = new VerifyProcess() {

			@Override
			protected void doVerify() throws Exception {
				DataSource dataSource = InfrastructureUtil.getDataSource();

				_connections.add(dataSource.getConnection());
			}

		};

		try {
			super.testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"Caught an unclosed exception", exception.getMessage());

			List<Throwable> throwables = ListUtil.fromArray(
				exception.getSuppressed());

			throwables.add(exception);

			Class<?> clazz = _verifyProcess.getClass();

			Assert.assertTrue(
				ListUtil.exists(
					throwables,
					throwable -> ArrayUtil.exists(
						throwable.getStackTrace(),
						stackTraceElement ->
							Objects.equals(
								stackTraceElement.getClassName(),
								clazz.getName()) &&
							Objects.equals(
								stackTraceElement.getMethodName(),
								"doVerify"))));
		}
	}

	private final List<Connection> _connections = new ArrayList<>();
	private VerifyProcess _verifyProcess;

}