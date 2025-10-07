/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.data.source.test.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncPrintWriter;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.hsqldb.jdbc.JDBCDriver;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class ExternalDataSourceControllerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeFalse(DBPartition.isPartitionEnabled());
	}

	@Before
	public void setUp() throws Exception {
		PropsUtil.set("jdbc.test.driverClassName", JDBCDriver.class.getName());
		PropsUtil.set("jdbc.test.url", _JDBC_URL);
		PropsUtil.set("jdbc.test.username", "sa");
		PropsUtil.set("jdbc.test.password", "");
		PropsUtil.set("jdbc.test.initializationFailTimeout", "0");

		Bundle testBundle = FrameworkUtil.getBundle(
			ExternalDataSourceControllerTest.class);

		_bundleContext = testBundle.getBundleContext();

		_apiBundle = _installBundle(
			"/com.liferay.external.data.source.test.api.jar");
		_serviceBundle = _installServiceBundle();

		_apiBundle.start();

		String originalUpgradeDatabaseAutoRun = PropsUtil.get(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN);

		try {
			PropsUtil.set(PropsKeys.UPGRADE_DATABASE_AUTO_RUN, "true");

			_serviceBundle.start();

			ComponentDescriptionDTO componentDescriptionDTO =
				_serviceComponentRuntime.getComponentDescriptionDTO(
					_serviceBundle,
					"com.liferay.external.data.source.test.internal.upgrade." +
						"registry.TestEntityUpgradeStepRegistrator");

			Promise<Void> promise = _serviceComponentRuntime.enableComponent(
				componentDescriptionDTO);

			promise.getValue();
		}
		finally {
			PropsUtil.set(
				PropsKeys.UPGRADE_DATABASE_AUTO_RUN,
				originalUpgradeDatabaseAutoRun);
		}
	}

	@After
	public void tearDown() throws Exception {
		_serviceBundle.uninstall();

		_apiBundle.uninstall();

		FileUtil.deltree(_HYPERSONIC_TEMP_DIR_NAME);
	}

	@Test
	public void testExternalDataSourceTests() throws Throwable {
		TestRunListener testRunListener = new TestRunListener();

		ServiceRegistration<RunListener> serviceRegistration =
			_bundleContext.registerService(
				RunListener.class, testRunListener, null);

		Bundle bundle = _installBundle(
			"/com.liferay.external.data.source.test.jar");

		try {
			bundle.start();

			testRunListener.rethrow(null);
		}
		catch (Exception exception) {
			testRunListener.rethrow(exception);
		}
		finally {
			serviceRegistration.unregister();

			bundle.uninstall();
		}
	}

	protected String getResourceDestination() {
		return "META-INF/spring/ext-spring.xml";
	}

	protected String getResourceSource() {
		return "/META-INF/spring/ext-spring.xml";
	}

	private byte[] _getServiceJarBytes(String path) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (InputStream inputStream =
				ExternalDataSourceControllerTest.class.getResourceAsStream(
					path);
			JarInputStream jarInputStream = new JarInputStream(inputStream);
			JarOutputStream jarOutputStream = new JarOutputStream(
				unsyncByteArrayOutputStream)) {

			Manifest manifest = jarInputStream.getManifest();

			jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

			manifest.write(jarOutputStream);

			jarOutputStream.closeEntry();

			JarEntry jarEntry = null;

			while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
				jarOutputStream.putNextEntry(jarEntry);

				StreamUtil.transfer(
					jarInputStream, jarOutputStream, StreamUtil.BUFFER_SIZE,
					false, jarEntry.getSize());

				jarOutputStream.closeEntry();
			}

			try (InputStream extSpringInputStream =
					ExternalDataSourceControllerTest.class.getResourceAsStream(
						getResourceSource())) {

				jarOutputStream.putNextEntry(
					new JarEntry(getResourceDestination()));

				StreamUtil.transfer(
					extSpringInputStream, jarOutputStream, false);

				jarOutputStream.closeEntry();
			}
		}

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private Bundle _installBundle(String path) throws Exception {
		try (InputStream inputStream =
				ExternalDataSourceControllerTest.class.getResourceAsStream(
					path)) {

			return _bundleContext.installBundle(path, inputStream);
		}
	}

	private Bundle _installServiceBundle() throws Exception {
		String path = "/com.liferay.external.data.source.test.service.jar";

		return _bundleContext.installBundle(
			path, new UnsyncByteArrayInputStream(_getServiceJarBytes(path)));
	}

	private static final String _EXTERNAL_DATABASE_NAME = "external";

	private static final String _HYPERSONIC_TEMP_DIR_NAME =
		PropsValues.LIFERAY_HOME + "/data/hypersonic_temp/";

	private static final String _JDBC_URL = StringBundler.concat(
		"jdbc:hsqldb:", _HYPERSONIC_TEMP_DIR_NAME, _EXTERNAL_DATABASE_NAME,
		";hsqldb.write_delay=false;shutdown=true");

	private Bundle _apiBundle;
	private BundleContext _bundleContext;
	private Bundle _serviceBundle;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

	/**
	 * A carrier Throwable to overcome Arquillian Exception serialization
	 * limitation which can not handle suppressed Throwables.
	 */
	private static class ArquillianThrowable extends Throwable {

		private ArquillianThrowable(String message) {
			super(message, null, false, false);
		}

	}

	private static class TestRunListener extends RunListener {

		public void rethrow(Throwable throwable) throws Throwable {
			if (throwable == null) {
				if (_failures.isEmpty()) {
					return;
				}

				throwable = new AssertionError(
					"Inner test bundle junit execution errors:");
			}

			for (Failure failure : _failures) {
				throwable.addSuppressed(failure.getException());
			}

			try (UnsyncStringWriter unsyncStringWriter =
					new UnsyncStringWriter();
				UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(
					unsyncStringWriter)) {

				throwable.printStackTrace(unsyncPrintWriter);

				throw new ArquillianThrowable(unsyncStringWriter.toString());
			}
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			_failures.add(failure);
		}

		private final List<Failure> _failures = new ArrayList<>();

	}

}