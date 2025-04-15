/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.util.service.OSGiServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.lpkg.deployer.test.util.LPKGTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class MarketplaceAppManagerPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			MarketplaceAppManagerPortletTest.class);

		_bundleContext = bundle.getBundleContext();

		_bundleBlacklistConfiguration = OSGiServiceUtil.callService(
			_bundleContext, ConfigurationAdmin.class,
			configurationAdmin -> configurationAdmin.getConfiguration(
				_BUNDLE_BLACKLIST_CONFIGURATION_PID, StringPool.QUESTION));

		_properties = _bundleBlacklistConfiguration.getProperties();

		_updateConfiguration(new HashMapDictionary<>());

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

		LPKGTestUtil.createLPKG(_lpkgPath, _SYMBOLIC_NAME, false);

		countDownLatch.await();
	}

	@After
	public void tearDown() throws Exception {
		_updateConfiguration(_properties);

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
	}

	@Test
	public void testUninstallBundles() throws Exception {
		Bundle bundle = _findBundle(_SYMBOLIC_NAME);

		Assert.assertEquals(Bundle.ACTIVE, bundle.getState());

		Collection<String> blacklistBundleSymbolicNames =
			_getBlacklistBundleSymbolicNames();

		Assert.assertFalse(
			_SYMBOLIC_NAME + " should not be blacklisted",
			blacklistBundleSymbolicNames.contains(_SYMBOLIC_NAME));

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setParameter(
			"bundleIds", String.valueOf(bundle.getBundleId()));

		ReflectionTestUtil.invoke(
			_portlet, "uninstallBundles",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			new Object[] {mockActionRequest, null});

		for (Bundle curBundle : _bundleContext.getBundles()) {
			Assert.assertFalse(
				curBundle + " should not be installed",
				_SYMBOLIC_NAME.equals(curBundle.getSymbolicName()));
		}

		blacklistBundleSymbolicNames = _getBlacklistBundleSymbolicNames();

		Assert.assertTrue(
			_SYMBOLIC_NAME + " should be blacklisted",
			blacklistBundleSymbolicNames.contains(_SYMBOLIC_NAME));
	}

	private Bundle _findBundle(String symbolicName) {
		Bundle bundle = BundleUtil.getBundle(_bundleContext, symbolicName);

		Assert.assertNotNull(
			"No bundle installed with symbolic name " + symbolicName, bundle);

		return bundle;
	}

	private Collection<String> _getBlacklistBundleSymbolicNames()
		throws Exception {

		Configuration configuration = OSGiServiceUtil.callService(
			_bundleContext, ConfigurationAdmin.class,
			configurationAdmin -> configurationAdmin.getConfiguration(
				_BUNDLE_BLACKLIST_CONFIGURATION_PID, StringPool.QUESTION));

		Dictionary<String, Object> properties = configuration.getProperties();

		String[] blacklistBundleSymbolicNames = (String[])properties.get(
			"blacklistBundleSymbolicNames");

		if (blacklistBundleSymbolicNames == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(blacklistBundleSymbolicNames);
	}

	private void _updateConfiguration(Dictionary<String, Object> dictionary)
		throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		ServiceRegistration<?> serviceRegistration =
			_bundleContext.registerService(
				ConfigurationListener.class,
				configurationEvent -> {
					if (Objects.equals(
							_BUNDLE_BLACKLIST_CONFIGURATION_PID,
							configurationEvent.getPid())) {

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

	private static final String _BUNDLE_BLACKLIST_CONFIGURATION_PID =
		"com.liferay.portal.bundle.blacklist.internal.configuration." +
			"BundleBlacklistConfiguration";

	private static final String _LPKG_NAME =
		"Marketplace Bundle Blacklist Test";

	private static final String _SYMBOLIC_NAME =
		"com.liferay.marketplace.bundle.blacklist.test.bundle";

	private Configuration _bundleBlacklistConfiguration;
	private BundleContext _bundleContext;
	private Path _lpkgPath;

	@Inject(
		filter = "component.name=com.liferay.marketplace.app.manager.web.internal.portlet.MarketplaceAppManagerPortlet"
	)
	private Portlet _portlet;

	private Dictionary<String, Object> _properties;

}