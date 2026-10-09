/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lpkg.deployer.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

/**
 * @author Matthew Tambara
 */
@RunWith(Arquillian.class)
public class LPKGPersistenceVerifyUpgradeTest {

	@Test
	public void testVerifyBundleUpgradePersistence() {
		Bundle bundle = FrameworkUtil.getBundle(
			LPKGPersistenceVerifyTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Bundle jarBundle = null;

		Bundle lpkgBundle = null;

		for (Bundle testBundle : bundleContext.getBundles()) {
			if ((jarBundle != null) && (lpkgBundle != null)) {
				break;
			}

			String symbolicName = testBundle.getSymbolicName();

			if (symbolicName.equals("lpkg.persistence.test")) {
				jarBundle = testBundle;
			}

			if (!symbolicName.equals("Liferay Persistence Test")) {
				continue;
			}

			lpkgBundle = testBundle;
		}

		Assert.assertNotNull(jarBundle);
		Assert.assertEquals(Bundle.ACTIVE, jarBundle.getState());

		Version version = new Version(2, 0, 0);

		Assert.assertEquals(version, jarBundle.getVersion());

		Assert.assertNotNull(lpkgBundle);
		Assert.assertEquals(Bundle.ACTIVE, lpkgBundle.getState());
		Assert.assertEquals(version, lpkgBundle.getVersion());
	}

}