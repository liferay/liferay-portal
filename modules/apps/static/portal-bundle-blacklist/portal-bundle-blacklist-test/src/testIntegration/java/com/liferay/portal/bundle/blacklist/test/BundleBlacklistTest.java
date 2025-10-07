/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.bundle.blacklist.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.util.service.OSGiServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.lpkg.deployer.test.util.LPKGTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Dictionary;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Matthew Tambara
 */
@RunWith(Arquillian.class)
public class BundleBlacklistTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(BundleBlacklistTest.class);

		_bundleContext = bundle.getBundleContext();

		_bundleBlacklistConfiguration = OSGiServiceUtil.callService(
			_bundleContext, ConfigurationAdmin.class,
			configurationAdmin -> configurationAdmin.getConfiguration(
				_CONFIG_NAME, StringPool.QUESTION));

		_properties = _bundleBlacklistConfiguration.getProperties();

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		_bundleBlacklistConfiguration.update(properties);

		CountDownLatch countDownLatch = new CountDownLatch(1);

		BundleTracker<Bundle> bundleTracker = new BundleTracker<Bundle>(
			_bundleContext, Bundle.ACTIVE, null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent event) {
				String symbolicName = bundle.getSymbolicName();

				if (symbolicName.equals(_LPKG_NAME)) {
					countDownLatch.countDown();

					close();
				}

				return null;
			}

		};

		bundleTracker.open();

		File deploymentDir = new File(
			PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR);

		deploymentDir = deploymentDir.getCanonicalFile();

		_lpkgPath = Paths.get(
			deploymentDir.toString(), _LPKG_NAME.concat(".lpkg"));

		LPKGTestUtil.createLPKG(_lpkgPath, _SYMBOLIC_NAME, true);

		countDownLatch.await();
	}

	@After
	public void tearDown() throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(1);

		BundleTracker<Bundle> bundleTracker = new BundleTracker<Bundle>(
			_bundleContext, Bundle.UNINSTALLED, null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent event) {
				String symbolicName = bundle.getSymbolicName();

				if (symbolicName.equals(_LPKG_NAME)) {
					countDownLatch.countDown();

					close();
				}

				return null;
			}

		};

		bundleTracker.open();

		Files.delete(_lpkgPath);

		countDownLatch.await();

		_updateConfiguration(_properties);
	}

	@Test
	public void testBundleBlacklist() throws Exception {
		Bundle jarBundle = null;
		Bundle lpkgBundle = null;
		Bundle warBundle = null;
		Bundle warWrapperBundle = null;

		for (Bundle bundle : _bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (symbolicName.equals(_LPKG_NAME)) {
				lpkgBundle = bundle;
			}
			else if (symbolicName.equals(_SYMBOLIC_NAME)) {
				jarBundle = bundle;
			}
			else if (symbolicName.equals(_SYMBOLIC_NAME.concat("-war"))) {
				warBundle = bundle;
			}
			else if (symbolicName.equals(
						StringBundler.concat(
							_LPKG_NAME, StringPool.DASH, _SYMBOLIC_NAME,
							"-war-wrapper"))) {

				warWrapperBundle = bundle;
			}
		}

		Assert.assertNotNull("No JAR bundle found", jarBundle);
		Assert.assertNotNull("No LPKG bundle found", lpkgBundle);
		Assert.assertNotNull("No WAR bundle found", warBundle);
		Assert.assertNotNull("No WAR wrapper bundle found", warWrapperBundle);

		// Blacklist WAR wrapper

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				_PROP_KEY, warWrapperBundle.getSymbolicName()
			).build();

		_updateConfiguration(properties);

		Assert.assertEquals(
			"WAR wrapper bundle not uninstalled", Bundle.UNINSTALLED,
			warWrapperBundle.getState());

		Assert.assertEquals(
			"WAR bundle not uninstalled", Bundle.UNINSTALLED,
			warBundle.getState());

		properties.remove(_PROP_KEY);

		_updateConfiguration(properties);

		warWrapperBundle = _findBundle(warWrapperBundle.getSymbolicName());

		Assert.assertEquals(
			"WAR wrapper bundle not reinstalled", Bundle.ACTIVE,
			warWrapperBundle.getState());

		warBundle = _findBundle(warBundle.getSymbolicName());

		Assert.assertEquals(
			"WAR bundle not reinstalled", Bundle.ACTIVE, warBundle.getState());

		// Blacklist WAR

		properties.put(_PROP_KEY, warBundle.getSymbolicName());

		_updateConfiguration(properties);

		Assert.assertEquals(
			"WAR bundle not uninstalled", Bundle.UNINSTALLED,
			warBundle.getState());

		properties.remove(_PROP_KEY);

		_updateConfiguration(properties);

		warBundle = _findBundle(warBundle.getSymbolicName());

		Assert.assertEquals(
			"WAR bundle not reinstalled", Bundle.ACTIVE, warBundle.getState());

		// Blacklist JAR

		properties.put(_PROP_KEY, jarBundle.getSymbolicName());

		_updateConfiguration(properties);

		Assert.assertEquals(
			"JAR bundle not uninstalled", Bundle.UNINSTALLED,
			jarBundle.getState());

		properties.remove(_PROP_KEY);

		_updateConfiguration(properties);

		jarBundle = _findBundle(jarBundle.getSymbolicName());

		Assert.assertEquals(
			"JAR bundle not reinstalled", Bundle.ACTIVE, jarBundle.getState());

		// Blacklist LPKG

		properties.put(_PROP_KEY, lpkgBundle.getSymbolicName());

		_updateConfiguration(properties);

		Assert.assertEquals(
			"LPKG not uninstalled", Bundle.UNINSTALLED, lpkgBundle.getState());

		Assert.assertEquals(
			"JAR bundle not uninstalled", Bundle.UNINSTALLED,
			jarBundle.getState());

		Assert.assertEquals(
			"WAR wrapper bundle not uninstalled", Bundle.UNINSTALLED,
			warWrapperBundle.getState());

		Assert.assertEquals(
			"WAR bundle not uninstalled", Bundle.UNINSTALLED,
			warBundle.getState());

		properties.remove(_PROP_KEY);

		_updateConfiguration(properties);

		lpkgBundle = _findBundle(lpkgBundle.getSymbolicName());

		Assert.assertEquals(
			"LPKG not reinstalled", Bundle.ACTIVE, lpkgBundle.getState());

		jarBundle = _findBundle(jarBundle.getSymbolicName());

		Assert.assertEquals(
			"JAR bundle not reinstalled", Bundle.ACTIVE, jarBundle.getState());

		warWrapperBundle = _findBundle(warWrapperBundle.getSymbolicName());

		Assert.assertEquals(
			"WAR wrapper bundle not reinstalled", Bundle.ACTIVE,
			warWrapperBundle.getState());

		warBundle = _findBundle(warBundle.getSymbolicName());

		Assert.assertEquals(
			"WAR bundle not reinstalled", Bundle.ACTIVE, warBundle.getState());
	}

	private Bundle _findBundle(String symbolicName) {
		Bundle bundle = BundleUtil.getBundle(_bundleContext, symbolicName);

		if (bundle == null) {
			throw new IllegalArgumentException(
				"No bundle installed with symbolic name " + symbolicName);
		}

		return bundle;
	}

	private void _updateConfiguration(Dictionary<String, Object> dictionary)
		throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		ServiceRegistration<?> serviceRegistration =
			_bundleContext.registerService(
				ConfigurationListener.class,
				configurationEvent -> {
					if (Objects.equals(
							_CONFIG_NAME, configurationEvent.getPid())) {

						countDownLatch.countDown();
					}
				},
				null);

		try {
			if (dictionary == null) {
				_bundleBlacklistConfiguration.delete();
			}
			else {
				_bundleBlacklistConfiguration.update(dictionary);
			}

			countDownLatch.await();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private static final String _CONFIG_NAME =
		"com.liferay.portal.bundle.blacklist.internal.configuration." +
			"BundleBlacklistConfiguration";

	private static final String _LPKG_NAME = "Bundle Blacklist Test";

	private static final String _PROP_KEY = "blacklistBundleSymbolicNames";

	private static final String _SYMBOLIC_NAME =
		"com.liferay.portal.bundle.blacklist.test.bundle";

	private Configuration _bundleBlacklistConfiguration;
	private BundleContext _bundleContext;
	private Path _lpkgPath;
	private Dictionary<String, Object> _properties;

}