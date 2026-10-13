/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Time;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class DXPCombinedLicenseTest extends BaseCombinedLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && !isCMS());
	}

	@Test
	public void testAppLicensesWithPortalLicenseFreeTier() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			testAppLicensesWithPortalLicense(FREE_TIER_LICENSE_TYPE);

			Assert.assertTrue(LicenseManagerUtil.isFreeTier());
		}
	}

	@Test
	public void testPortalLicensesEnterpriseAndFreeTier() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertPortalLicenseNotRegistered();

			deployLicenses(
				new String[][] {
					{ENTERPRISE_LICENSE_TYPE, String.valueOf(Time.HOUR)},
					{FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)}
				});

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();

			Assert.assertFalse(LicenseManagerUtil.isFreeTier());
		}
	}

	@Override
	protected App[] getApps() {
		return App.values();
	}

}