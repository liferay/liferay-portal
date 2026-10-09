/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lpkg.deployer.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.Collections;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.FrameworkWiring;

/**
 * @author Matthew Tambara
 */
@RunWith(Arquillian.class)
public class LPKGControllerVerifyTest {

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(LPKGControllerVerifyTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testLPKGController() throws Exception {
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

		// Stop jar and restart by refreshing LPKG

		jarBundle.stop();

		Assert.assertEquals(Bundle.RESOLVED, jarBundle.getState());

		_refreshBundle(lpkgBundle);

		Assert.assertEquals(Bundle.ACTIVE, jarBundle.getState());

		// Uninstall LPKG contents and reinstall by refreshing LPKG

		jarBundle.uninstall();

		Assert.assertEquals(Bundle.UNINSTALLED, jarBundle.getState());

		// WAB should uninstall when wrapper is uninstalled

		warWrapperBundle.uninstall();

		Assert.assertEquals(Bundle.UNINSTALLED, warWrapperBundle.getState());
		Assert.assertEquals(Bundle.UNINSTALLED, warBundle.getState());

		_refreshBundle(lpkgBundle);

		for (Bundle bundle : _bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (symbolicName.equals(_SYMBOLIC_NAME)) {
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

		Assert.assertEquals(
			"JAR bundle was not reinstalled", Bundle.ACTIVE,
			jarBundle.getState());
		Assert.assertEquals(
			"WAR bundle was not reinstalled", Bundle.ACTIVE,
			warBundle.getState());
		Assert.assertEquals(
			"WAR wrapper bundle was not reinstalled", Bundle.ACTIVE,
			warWrapperBundle.getState());

		// Stop wab and restart by refreshing wrapper

		warBundle.stop();

		Assert.assertEquals(Bundle.RESOLVED, warBundle.getState());

		_refreshBundle(warWrapperBundle);

		Assert.assertEquals(Bundle.ACTIVE, warBundle.getState());

		// Uninstall wab and reinstall by refreshing wrapper

		warBundle.uninstall();

		Assert.assertEquals(Bundle.UNINSTALLED, warBundle.getState());

		_refreshBundle(warWrapperBundle);

		for (Bundle bundle : _bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (!symbolicName.equals(_SYMBOLIC_NAME.concat("-war"))) {
				continue;
			}

			warBundle = bundle;
		}

		Assert.assertEquals(
			"WAR bundle was not reinstalled", Bundle.ACTIVE,
			warBundle.getState());

		// Uninstall LPKG and check that contents have been uninstalled

		lpkgBundle.uninstall();

		Assert.assertEquals(Bundle.UNINSTALLED, lpkgBundle.getState());
		Assert.assertEquals(Bundle.UNINSTALLED, jarBundle.getState());
		Assert.assertEquals(Bundle.UNINSTALLED, warBundle.getState());
		Assert.assertEquals(Bundle.UNINSTALLED, warWrapperBundle.getState());
	}

	private void _refreshBundle(Bundle bundle) throws Exception {
		Bundle frameworkBundle = _bundleContext.getBundle(0);

		FrameworkWiring frameworkWiring = frameworkBundle.adapt(
			FrameworkWiring.class);

		final DefaultNoticeableFuture<FrameworkEvent> defaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		frameworkWiring.refreshBundles(
			Collections.<Bundle>singletonList(bundle),
			new FrameworkListener() {

				@Override
				public void frameworkEvent(FrameworkEvent frameworkEvent) {
					defaultNoticeableFuture.set(frameworkEvent);
				}

			});

		FrameworkEvent frameworkEvent = defaultNoticeableFuture.get();

		Assert.assertEquals(
			FrameworkEvent.PACKAGES_REFRESHED, frameworkEvent.getType());
	}

	private static final String _LPKG_NAME = "Liferay Controller Test";

	private static final String _SYMBOLIC_NAME = "lpkg.controller.test";

	private static BundleContext _bundleContext;

}