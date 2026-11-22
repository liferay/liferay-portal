/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.module.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.BundleContextTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;
import java.io.InputStream;

import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Tina Tian
 */
@Ignore
@RunWith(Arquillian.class)
public class SnapshotIntegrationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		BundleContext bundleContext = BundleContextTestUtil.getBundleContext(
			SnapshotIntegrationTest.class);

		_bundle = bundleContext.installBundle(
			SnapshotIntegrationTest.class.getName(),
			_createBundle(SnapshotIntegrationTest.class.getName(), "1.0.0"));
	}

	@After
	public void tearDown() throws Exception {
		_bundle.uninstall();
	}

	@Test
	public void testDynamicWithBundleRestart() throws Exception {
		_testWithBundleRestart(true);
	}

	@Test
	public void testStaticWithBundleRestart() throws Exception {
		_testWithBundleRestart(false);
	}

	private InputStream _createBundle(
			String bundleSymbolicName, String bundleVersion)
		throws Exception {

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (JarOutputStream jarOutputStream = new JarOutputStream(
					unsyncByteArrayOutputStream)) {

				_writeManifest(
					bundleSymbolicName, bundleVersion, jarOutputStream);

				_writeClasses(jarOutputStream, SnapshotIntegrationTest.class);
			}

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	private void _testWithBundleRestart(boolean dynamic) throws Exception {
		_bundle.start();

		Assert.assertEquals(Bundle.ACTIVE, _bundle.getState());

		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

		Class<?> holderClass = bundleClassLoader.loadClass(
			SnapshotIntegrationTest.class.getName());

		Snapshot<TestService<String>> snapshot = new Snapshot<>(
			holderClass, Snapshot.cast(TestService.class), null, dynamic);

		Assert.assertNull(snapshot.get());

		TestService<String> testService = new TestService<>();

		BundleContext bundleContext = _bundle.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(TestService.class, testService, null);

		Assert.assertSame(testService, snapshot.get());

		serviceRegistration.unregister();

		_bundle.stop();

		Assert.assertEquals(Bundle.RESOLVED, _bundle.getState());

		_bundle.start();

		Assert.assertEquals(Bundle.ACTIVE, _bundle.getState());

		Assert.assertNull(snapshot.get());

		BundleContext newBundleContext = _bundle.getBundleContext();

		serviceRegistration = newBundleContext.registerService(
			TestService.class, testService, null);

		Assert.assertSame(testService, snapshot.get());

		serviceRegistration.unregister();

		_bundle.stop();
	}

	private void _writeClasses(
			JarOutputStream jarOutputStream, Class<?>... classes)
		throws IOException {

		for (Class<?> clazz : classes) {
			String className = clazz.getName();

			String path = StringUtil.replace(
				className, CharPool.PERIOD, CharPool.SLASH);

			String resourcePath = path.concat(".class");

			jarOutputStream.putNextEntry(new ZipEntry(resourcePath));

			ClassLoader classLoader = clazz.getClassLoader();

			StreamUtil.transfer(
				classLoader.getResourceAsStream(resourcePath), jarOutputStream,
				false);

			jarOutputStream.closeEntry();
		}
	}

	private void _writeManifest(
			String bundleSymbolicName, String bundleVersion,
			JarOutputStream jarOutputStream)
		throws IOException {

		Manifest manifest = new Manifest();

		Attributes attributes = manifest.getMainAttributes();

		attributes.putValue(Constants.BUNDLE_ACTIVATIONPOLICY, "lazy");
		attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
		attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, bundleSymbolicName);
		attributes.putValue(Constants.BUNDLE_VERSION, bundleVersion);
		attributes.putValue("Manifest-Version", "2");

		jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

		manifest.write(jarOutputStream);

		jarOutputStream.closeEntry();
	}

	private Bundle _bundle;

	private static class TestService<T> {
	}

}