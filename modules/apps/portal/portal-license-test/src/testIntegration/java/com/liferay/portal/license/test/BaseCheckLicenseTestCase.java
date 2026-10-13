/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Time;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public abstract class BaseCheckLicenseTestCase extends BaseLicenseTestCase {

	@BeforeClass
	public static void setUpClass() {
		_setVersionSafeCloseable = setVersionWithSafeCloseable("2026.Q1.0 LTS");
	}

	@AfterClass
	public static void tearDownClass() {
		_setVersionSafeCloseable.close();
	}

	@Before
	public void setUp() throws Exception {
		_safeCloseable = resetLicenseDataWithSafeCloseble();
	}

	@After
	public void tearDown() {
		_safeCloseable.close();
	}

	@Test
	public void testCheckLicenseForApp() throws Exception {
		for (App app : getApps()) {
			_testCheckLicenseForApp(app);
		}
	}

	@Test
	public void testCheckLicenseWithBinaryFile2026_Q1_0() throws Exception {
		testCheckLicense("binary_file_2026_Q1_0.li");
	}

	@Test
	public void testCheckLicenseWithBinaryFileAfter2026_Q1_0()
		throws Exception {

		testCheckLicense("binary_file_after_2026_Q1_0.li");
	}

	@Test
	public void testCheckLicenseWithBinaryFileBefore2026_Q1_0()
		throws Exception {

		try (SafeCloseable safeCloseable = disableValidateWithSafeCloseable()) {
			testCheckLicense("binary_file_before_2026_Q1_0.li");
		}
	}

	protected abstract App[] getApps();

	protected abstract void testCheckLicense(String fileName) throws Exception;

	private void _testCheckLicenseForApp(App app) throws Exception {
		try (SafeCloseable safeCloseable1 = disableValidateWithSafeCloseable();
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble()) {

			assertLicensePropertiesNotExisted(getProductId(app));

			deployAppLicense(app, Time.HOUR);

			assertLicensePropertiesExisted(getProductId(app));

			Assert.assertTrue(LicenseManagerUtil.isAppEnabled(app));
		}
	}

	private static SafeCloseable _setVersionSafeCloseable;

	private SafeCloseable _safeCloseable;

}