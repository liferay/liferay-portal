/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Time;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DXPExpiredLicenseTest extends BaseExpiredLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && !isCMS());
	}

	@Test
	public void testFreeAndEnterpriseExpired() throws Exception {
		assertLicensePropertiesNotExisted(getPortalProductId());

		assertPortalLicenseNotRegistered();

		deployFreeTierPortalLicense(Time.HOUR);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		Assert.assertTrue(LicenseManagerUtil.isFreeTier());

		deployEnterprisePortalLicense(GRACE_PERIOD + VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		Assert.assertFalse(LicenseManagerUtil.isFreeTier());

		Thread.sleep(VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseExpired();

		resetLifecycleAction();

		checkLicense(getPortalProductId());

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		Assert.assertTrue(LicenseManagerUtil.isFreeTier());
	}

	@Test
	public void testFreeTierLicenseExpired() throws Exception {
		assertLicensePropertiesNotExisted(getPortalProductId());

		assertPortalLicenseNotRegistered();

		deployFreeTierPortalLicense(GRACE_PERIOD + VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		Thread.sleep(VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseExpired();
	}

	@Override
	protected App[] getApps() {
		return App.values();
	}

}