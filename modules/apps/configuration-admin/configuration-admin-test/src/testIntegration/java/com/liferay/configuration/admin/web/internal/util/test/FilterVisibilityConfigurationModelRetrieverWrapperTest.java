/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.display.ConfigurationVisibilityController;
import com.liferay.osgi.util.BundleUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class FilterVisibilityConfigurationModelRetrieverWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			FilterVisibilityConfigurationModelRetrieverWrapperTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() throws Exception {
		if (_configuration != null) {
			_configuration.delete();
		}

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetConfiguration() throws Exception {
		String pid = _addConfiguration();

		Configuration configuration = _getConfiguration(pid);

		Assert.assertEquals(pid, configuration.getPid());
	}

	@Test
	public void testGetConfigurationDoesNotWireConfigurationPackage()
		throws Exception {

		String packageName =
			"com.liferay.configuration.admin.web.internal.util.test." +
				"configuration";

		Bundle exporterBundle = _bundleContext.installBundle(
			RandomTestUtil.randomString(),
			_createBundle(
				HashMapBuilder.put(
					"Export-Package",
					packageName + ";probe=true;mandatory:=probe"
				).build()));
		Bundle probeBundle = _bundleContext.installBundle(
			RandomTestUtil.randomString(),
			_createBundle(
				HashMapBuilder.put(
					"DynamicImport-Package", packageName + ";probe=true"
				).build()));

		try {
			exporterBundle.start();
			probeBundle.start();

			String pid =
				packageName + StringPool.PERIOD + RandomTestUtil.randomString();

			_getConfiguration(pid);

			BundleWiring bundleWiring = probeBundle.adapt(BundleWiring.class);

			List<BundleWire> bundleWires = bundleWiring.getRequiredWires(
				PackageNamespace.PACKAGE_NAMESPACE);

			Assert.assertEquals(bundleWires.toString(), 0, bundleWires.size());
		}
		finally {
			exporterBundle.uninstall();
			probeBundle.uninstall();

			BundleUtil.refreshBundles(
				_bundleContext, Arrays.asList(exporterBundle, probeBundle));
		}
	}

	@Test
	public void testGetConfigurationWhenVisibilityControllerHidesConfiguration()
		throws Exception {

		String pid = _addConfiguration();

		_serviceRegistration = _bundleContext.registerService(
			ConfigurationVisibilityController.class, (scope, scopePK) -> false,
			HashMapDictionaryBuilder.<String, Object>put(
				"configuration.pid", pid
			).build());

		Assert.assertNull(_getConfiguration(pid));
	}

	private String _addConfiguration() throws Exception {
		String pid = RandomTestUtil.randomString();

		_configuration = _configurationAdmin.getConfiguration(
			pid, StringPool.QUESTION);

		_configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).build());

		return pid;
	}

	private InputStream _createBundle(Map<String, String> headers)
		throws Exception {

		Manifest manifest = new Manifest();

		Attributes mainAttributes = manifest.getMainAttributes();

		mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		mainAttributes.putValue("Bundle-ManifestVersion", "2");
		mainAttributes.putValue(
			"Bundle-SymbolicName", RandomTestUtil.randomString());

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			mainAttributes.putValue(entry.getKey(), entry.getValue());
		}

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		JarOutputStream jarOutputStream = new JarOutputStream(
			unsyncByteArrayOutputStream, manifest);

		jarOutputStream.close();

		return new UnsyncByteArrayInputStream(
			unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
			unsyncByteArrayOutputStream.size());
	}

	private Configuration _getConfiguration(String pid) {
		return ReflectionTestUtil.invoke(
			_configurationModelRetriever, "getConfiguration",
			new Class<?>[] {
				String.class, ExtendedObjectClassDefinition.Scope.class,
				Serializable.class
			},
			pid, ExtendedObjectClassDefinition.Scope.SYSTEM, null);
	}

	private BundleContext _bundleContext;
	private Configuration _configuration;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject(
		filter = "(&(objectClass=com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever)(filter.visibility=true))",
		type = Inject.NoType.class
	)
	private Object _configurationModelRetriever;

	private ServiceRegistration<ConfigurationVisibilityController>
		_serviceRegistration;

}