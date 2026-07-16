/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class AppModuleLicenseTest extends BaseLicenseTestCase {

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
	public void testEnterpriseLicense() throws Exception {
		for (App app : App.values()) {
			_testEnterpriseLicense(app);
		}
	}

	@Test
	public void testFreeTierLicense() throws Exception {
		for (App app : App.values()) {
			_testFreeTierLicense(app);
		}
	}

	private String[] _getAppSymbolicNames(App app) {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		List<String> symbolicNames = new ArrayList<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (symbolicName.contains(
					"." + StringUtil.toLowerCase(app.toString()) + ".")) {

				symbolicNames.add(symbolicName);
			}
		}

		return ArrayUtil.toStringArray(symbolicNames);
	}

	private void _testEnterpriseLicense(App app) throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			String[] appSymbolicNames = _getAppSymbolicNames(app);

			Assert.assertFalse(
				appSymbolicNames.toString(),
				ArrayUtil.isEmpty(appSymbolicNames));

			assertLicensePropertiesNotExisted(getProductId(app));

			assertBundlesExisted(appSymbolicNames);

			assertPortalLicenseNotRegistered();

			assertBundlesExisted(appSymbolicNames);

			Assert.assertNull(LicenseManagerUtil.getAppExpirationDate(app));

			deployEnterprisePortalLicense(Time.HOUR);

			assertLicensePropertiesNotExisted(getProductId(app));

			assertPortalLicenseRegistered();

			assertBundlesNotExisted(appSymbolicNames);

			Assert.assertNull(LicenseManagerUtil.getAppExpirationDate(app));

			long startTime = System.currentTimeMillis();

			File binaryFile = deployAppLicense(app, startTime, Time.HOUR);

			assertLicensePropertiesExisted(getProductId(app));

			assertPortalLicenseRegistered();

			assertBundlesExisted(appSymbolicNames);

			Date expirationDate = LicenseManagerUtil.getAppExpirationDate(app);

			Assert.assertEquals(
				getDateString(new Date(startTime + Time.HOUR)),
				getDateString(expirationDate));

			binaryFile.delete();

			checkLicense(getProductId(app));

			assertLicensePropertiesNotExisted(getProductId(app));

			resetLifecycleAction();

			assertPortalLicenseRegistered();

			assertBundlesNotExisted(appSymbolicNames);

			Assert.assertNull(LicenseManagerUtil.getAppExpirationDate(app));
		}
	}

	private void _testFreeTierLicense(App app) throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			String[] appSymbolicNames = _getAppSymbolicNames(app);

			Assert.assertFalse(
				appSymbolicNames.toString(),
				ArrayUtil.isEmpty(appSymbolicNames));

			assertLicensePropertiesNotExisted(getProductId(app));

			assertBundlesExisted(appSymbolicNames);

			assertPortalLicenseNotRegistered();

			assertBundlesExisted(appSymbolicNames);

			deployFreeTierPortalLicense(Time.HOUR);

			assertLicensePropertiesNotExisted(getProductId(app));

			assertPortalLicenseRegistered();

			assertBundlesNotExisted(appSymbolicNames);

			File binaryFile = deployAppLicense(app, Time.HOUR);

			assertLicensePropertiesExisted(getProductId(app));

			assertPortalLicenseRegistered();

			assertBundlesNotExisted(appSymbolicNames);

			binaryFile.delete();

			checkLicense(getProductId(app));

			assertLicensePropertiesNotExisted(getProductId(app));

			resetLifecycleAction();

			assertPortalLicenseRegistered();

			assertBundlesNotExisted(appSymbolicNames);
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

}