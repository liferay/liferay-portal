/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.sql.Connection;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Luis Ortiz
 */
public abstract class BasePostUpgradeDataCleanupProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		connection = DataAccess.getConnection();

		dbInspector = new DBInspector(connection);
	}

	@AfterClass
	public static void tearDownClass() {
		DataAccess.cleanUp(connection);
	}

	protected abstract Object[] getPostUpgradeDataCleanupProcessArguments();

	protected abstract Class<?>[]
		getPostUpgradeDataCleanupProcessArgumentTypes();

	protected abstract String getPostUpgradeDataCleanupProcessClassName();

	protected void installBundle(Bundle bundle, BundleContext bundleContext)
		throws Exception {

		com.liferay.osgi.util.BundleUtil.installBundle(
			bundleContext, _lpkgDeployer, bundle.getLocation(), 1);

		com.liferay.osgi.util.BundleUtil.refreshBundles(
			bundleContext, Collections.singletonList(bundle));
	}

	protected void test(
			UnsafeConsumer<LogCapture, Exception> assertUnsafeConsumer,
			UnsafeRunnable<Exception> cleanUpDataUnsafeRunnable,
			UnsafeRunnable<Exception> initializeDataUnsafeRunnable)
		throws Exception {

		test(
			assertUnsafeConsumer, cleanUpDataUnsafeRunnable,
			initializeDataUnsafeRunnable,
			getPostUpgradeDataCleanupProcessClassName(), LoggerTestUtil.INFO);
	}

	protected void test(
			UnsafeConsumer<LogCapture, Exception> assertUnsafeConsumer,
			UnsafeRunnable<Exception> cleanUpDataUnsafeRunnable,
			UnsafeRunnable<Exception> initializeDataUnsafeRunnable,
			String logCaptureClass, String logLevel)
		throws Exception {

		try (LogCapture directoryWatcherLogCapture =
				LoggerTestUtil.configureLog4JLogger(
					"com.liferay.portal.file.install.internal.DirectoryWatcher",
					LoggerTestUtil.OFF)) {

			initializeDataUnsafeRunnable.run();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					logCaptureClass, logLevel)) {

				_runPostUpgradeDataCleanUpVerifyProcess();

				if (assertUnsafeConsumer != null) {
					assertUnsafeConsumer.accept(logCapture);
				}
			}
			finally {
				cleanUpDataUnsafeRunnable.run();
			}
		}
	}

	protected void testObjectDefinition(
			UnsafeBiConsumer<LogCapture, ObjectDefinition, Exception>
				assertUnsafeBiConsumer)
		throws Exception {

		testObjectDefinition(assertUnsafeBiConsumer, null);
	}

	protected void testObjectDefinition(
			UnsafeBiConsumer<LogCapture, ObjectDefinition, Exception>
				assertUnsafeBiConsumer,
			UnsafeConsumer<ObjectDefinition, Exception>
				initializeDataUnsafeConsumer)
		throws Exception {

		AtomicReference<ObjectDefinition> objectDefinitionAtomicReference =
			new AtomicReference<>();

		test(
			logCapture -> assertUnsafeBiConsumer.accept(
				logCapture, objectDefinitionAtomicReference.get()),
			() -> {
				ObjectDefinition objectDefinition =
					objectDefinitionAtomicReference.get();

				if (objectDefinition == null) {
					return;
				}

				ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
					objectDefinition);

				ClassName className = ClassNameLocalServiceUtil.fetchClassName(
					objectDefinition.getClassName());

				if (className != null) {
					ClassNameLocalServiceUtil.deleteClassName(className);
				}
			},
			() -> {
				ObjectDefinition objectDefinition =
					ObjectDefinitionTestUtil.publishObjectDefinition();

				objectDefinitionAtomicReference.set(objectDefinition);

				if (initializeDataUnsafeConsumer != null) {
					initializeDataUnsafeConsumer.accept(objectDefinition);
				}
			});
	}

	protected Bundle uninstallBundle(
			BundleContext bundleContext, String bundleName)
		throws Exception {

		for (Bundle bundle : bundleContext.getBundles()) {
			if (Objects.equals(bundle.getSymbolicName(), bundleName) &&
				(bundle.getState() == Bundle.ACTIVE)) {

				bundle.uninstall();

				com.liferay.osgi.util.BundleUtil.refreshBundles(
					bundleContext, Collections.singletonList(bundle));

				return bundle;
			}
		}

		return null;
	}

	protected static Connection connection;
	protected static DBInspector dbInspector;

	private void _runPostUpgradeDataCleanUpVerifyProcess() throws Exception {
		Bundle bundle = BundleUtil.getBundle(
			SystemBundleUtil.getBundleContext(),
			"com.liferay.data.cleanup.impl");

		Class<?> postUpgradeDataCleanupProcessClass = bundle.loadClass(
			getPostUpgradeDataCleanupProcessClassName());

		Method method = postUpgradeDataCleanupProcessClass.getMethod("cleanUp");

		Constructor<?> constructor =
			postUpgradeDataCleanupProcessClass.getConstructor(
				getPostUpgradeDataCleanupProcessArgumentTypes());

		Object object = constructor.newInstance(
			getPostUpgradeDataCleanupProcessArguments());

		method.invoke(object);
	}

	@Inject
	private LPKGDeployer _lpkgDeployer;

}