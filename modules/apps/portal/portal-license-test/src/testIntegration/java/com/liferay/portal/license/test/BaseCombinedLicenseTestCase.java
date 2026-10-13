/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogEntry;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Kevin Lee
 */
public abstract class BaseCombinedLicenseTestCase extends BaseLicenseTestCase {

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
	public void testAppLicensesWithPortalLicense() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			testAppLicensesWithPortalLicense(ENTERPRISE_LICENSE_TYPE);
		}
	}

	@Test
	public void testDuplicateLicenses() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			deployLicenses(
				new String[][] {
					{ENTERPRISE_LICENSE_TYPE, String.valueOf(Time.HOUR)},
					{ENTERPRISE_LICENSE_TYPE, String.valueOf(Time.HOUR)}
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
	public void testSingleLicense() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getPortalProductId());

			assertPortalLicenseNotRegistered();

			deployLicenses(
				new String[][] {
					{ENTERPRISE_LICENSE_TYPE, String.valueOf(Time.HOUR)}
				});

			assertLicensePropertiesExisted(getPortalProductId());

			assertPortalLicenseRegistered();
		}
	}

	@Test
	public void testUnsupportedPortalLicense() throws Exception {
		String productName = ENTERPRISE_PRODUCT_NAME_CMS;

		if (isCMS()) {
			productName = ENTERPRISE_PRODUCT_NAME_DXP;
		}

		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			deployEnterprisePortalLicense(
				StringPool.BLANK, 0, productName, Time.HOUR);

			Assert.fail();
		}
		catch (LogEntriesException logEntriesException) {
			List<LogEntry> logEntries = logEntriesException.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				productName + " license validation failed",
				logEntry.getMessage());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				StringBundler.concat(
					"This product does not support the \"", productName,
					"\" portal license"),
				throwable.getMessage());
		}
	}

	protected abstract App[] getApps();

	protected void testAppLicensesWithPortalLicense(String portalLicenseType)
		throws Exception {

		assertLicensePropertiesNotExisted(getPortalProductId());

		for (App app : getApps()) {
			assertLicensePropertiesNotExisted(getProductId(app));
		}

		assertPortalLicenseNotRegistered();

		List<String[]> licenses = new ArrayList<>();

		licenses.add(
			new String[] {portalLicenseType, String.valueOf(Time.HOUR)});

		for (App app : getApps()) {
			licenses.add(new String[] {app.name(), String.valueOf(Time.HOUR)});
		}

		deployLicenses(licenses.toArray(new String[0][]));

		assertPortalLicenseRegistered();

		assertLicensePropertiesExisted(getPortalProductId());

		for (App app : getApps()) {
			assertLicensePropertiesExisted(getProductId(app));
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

}