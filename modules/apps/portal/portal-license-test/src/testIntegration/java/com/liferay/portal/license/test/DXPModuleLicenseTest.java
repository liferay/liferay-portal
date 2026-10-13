/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.Time;

import java.io.File;

import java.util.Objects;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Kevin Lee
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DXPModuleLicenseTest extends BaseLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && !isCMS());
	}

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

	@Before
	public void setUp() throws Exception {
		_safeCloseable = resetLicenseDataWithSafeCloseble();
	}

	@After
	public void tearDown() {
		_safeCloseable.close();
	}

	@Test
	public void testLicenseEnterprise() throws Exception {
		_testLicense(() -> deployEnterprisePortalLicense(Time.HOUR), true);
	}

	@Test
	public void testLicenseFreeTier() throws Exception {
		_testLicense(() -> deployFreeTierPortalLicense(Time.HOUR), false);
	}

	@Test
	public void testLicenseFreeTierWithManualDeploy() throws Exception {
		assertLicensePropertiesNotExisted(getPortalProductId());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Bundle dxpOnlyBundle = null;
		Bundle enterpriseAppBundle = null;

		for (Bundle bundle : bundleContext.getBundles()) {
			if (Objects.equals(
					bundle.getSymbolicName(),
					_getDxpOnlyModuleSymbolicName())) {

				dxpOnlyBundle = bundle;

				continue;
			}

			if (!Objects.equals(
					bundle.getSymbolicName(),
					_getEnterpriseAppSymbolicName())) {

				continue;
			}

			enterpriseAppBundle = bundle;
		}

		Assert.assertNotNull(dxpOnlyBundle);
		Assert.assertNotNull(enterpriseAppBundle);

		Assert.assertEquals(Bundle.ACTIVE, dxpOnlyBundle.getState());
		Assert.assertEquals(Bundle.ACTIVE, enterpriseAppBundle.getState());

		assertPortalLicenseNotRegistered();

		Assert.assertEquals(Bundle.ACTIVE, dxpOnlyBundle.getState());
		Assert.assertEquals(Bundle.ACTIVE, enterpriseAppBundle.getState());

		deployFreeTierPortalLicense(Time.HOUR);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		Assert.assertEquals(Bundle.UNINSTALLED, dxpOnlyBundle.getState());
		Assert.assertEquals(Bundle.UNINSTALLED, enterpriseAppBundle.getState());

		dxpOnlyBundle = bundleContext.installBundle(
			dxpOnlyBundle.getLocation());
		enterpriseAppBundle = bundleContext.installBundle(
			enterpriseAppBundle.getLocation());

		try {
			dxpOnlyBundle.start();

			Assert.assertEquals(Bundle.ACTIVE, dxpOnlyBundle.getState());

			resetCheckInterval();

			assertPortalLicenseInvalid(
				"Bundle " + _getDxpOnlyModuleSymbolicName() +
					" is not allowed");
		}
		finally {
			dxpOnlyBundle.uninstall();
		}

		try {
			enterpriseAppBundle.start();

			Assert.assertEquals(Bundle.ACTIVE, enterpriseAppBundle.getState());

			resetCheckInterval();

			assertPortalLicenseInvalid(
				"Bundle " + _getEnterpriseAppSymbolicName() +
					" is not allowed");
		}
		finally {
			enterpriseAppBundle.uninstall();
		}
	}

	private String _getDxpOnlyModuleSymbolicName() {
		return getProperty("dxp.only.module.symbolic.name");
	}

	private String _getEnterpriseAppSymbolicName() {
		return getProperty("enterprise.app.symbolic.name");
	}

	private void _testLicense(
			UnsafeSupplier<File, Exception> deployPortalLicenseUnsafeSupplier,
			boolean dxpModulesAllowed)
		throws Exception {

		assertLicensePropertiesNotExisted(getPortalProductId());

		assertBundlesExisted(
			_getDxpOnlyModuleSymbolicName(), _getEnterpriseAppSymbolicName());

		assertPortalLicenseNotRegistered();

		assertBundlesExisted(
			_getDxpOnlyModuleSymbolicName(), _getEnterpriseAppSymbolicName());

		File binaryFile = deployPortalLicenseUnsafeSupplier.get();

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		if (dxpModulesAllowed) {
			assertBundlesExisted(
				_getDxpOnlyModuleSymbolicName(),
				_getEnterpriseAppSymbolicName());
		}
		else {
			assertBundlesNotExisted(
				_getDxpOnlyModuleSymbolicName(),
				_getEnterpriseAppSymbolicName());
		}

		binaryFile.delete();

		checkLicense(getPortalProductId());

		assertLicensePropertiesNotExisted(getPortalProductId());

		resetLifecycleAction();

		assertBundlesExisted(
			_getDxpOnlyModuleSymbolicName(), _getEnterpriseAppSymbolicName());

		assertPortalLicenseNotRegistered();
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

	private SafeCloseable _safeCloseable;

}