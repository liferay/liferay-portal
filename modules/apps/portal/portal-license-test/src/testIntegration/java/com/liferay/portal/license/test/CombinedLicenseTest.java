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

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class CombinedLicenseTest extends BaseLicenseTestCase {

	@BeforeClass
	public static void setUpClass() {
		_disableKeyValidatorSafeCloseable = disableValidateWithSafeCloseable();
		_setVersionSafeCloseable = setVersionWithSafeCloseable("2026.Q1.0 LTS");
	}

	@AfterClass
	public static void tearDownClass() {
		_disableKeyValidatorSafeCloseable.close();
		_setVersionSafeCloseable.close();
	}

	@Test
	public void testAppLicensesWithPortalLicenseEnterprise() throws Exception {
		_testAppLicensesWithPortalLicense(false);
	}

	@Test
	public void testAppLicensesWithPortalLicenseFreeTier() throws Exception {
		_testAppLicensesWithPortalLicense(true);
	}

	@Test
	public void testDuplicateLicenses() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			deployLicenses(
				new String[][] {
					{FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)},
					{FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)}
				});

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();
		}
	}

	@Test
	public void testNoLicenses() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertPortalLicenseNotRegistered();

			deployLicenses(new String[0][]);

			assertPortalLicenseNotRegistered();
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

	@Test
	public void testSingleLicense() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			deployLicenses(
				new String[][] {
					{FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)}
				});

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();
		}
	}

	private void _testAppLicensesWithPortalLicense(boolean freeTier)
		throws Exception {

		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			for (App app : App.values()) {
				assertLicensePropertiesNotExisted(getProductId(app));
			}

			assertPortalLicenseNotRegistered();

			List<String[]> licenses = new ArrayList<>();

			if (freeTier) {
				licenses.add(
					new String[] {
						FREE_TIER_LICENSE_TYPE, String.valueOf(Time.HOUR)
					});
			}
			else {
				licenses.add(
					new String[] {
						ENTERPRISE_LICENSE_TYPE, String.valueOf(Time.HOUR)
					});
			}

			for (App app : App.values()) {
				licenses.add(
					new String[] {app.name(), String.valueOf(Time.HOUR)});
			}

			deployLicenses(licenses.toArray(new String[0][]));

			assertPortalLicenseRegistered();

			assertLicensePropertiesExisted(getPortalProductId());

			for (App app : App.values()) {
				assertLicensePropertiesExisted(getProductId(app));
			}

			if (freeTier) {
				Assert.assertTrue(LicenseManagerUtil.isFreeTier());
			}
			else {
				Assert.assertFalse(LicenseManagerUtil.isFreeTier());
			}
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

}