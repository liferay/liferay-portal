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
public abstract class BaseExpiredLicenseTestCase extends BaseLicenseTestCase {

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
	public void testAppLicenseExpired() throws Exception {
		for (App app : getApps()) {
			_testAppLicenseExpired(app);
		}
	}

	@Test
	public void testEnterpriseLicenseExpired() throws Exception {
		assertLicensePropertiesNotExisted(getPortalProductId());

		assertPortalLicenseNotRegistered();

		deployEnterprisePortalLicense(GRACE_PERIOD + VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();

		Thread.sleep(VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseExpired();
	}

	protected abstract App[] getApps();

	protected static final long GRACE_PERIOD = -2 * Time.DAY;

	protected static final long VALIDITY_PERIOD = 15 * Time.SECOND;

	private void _testAppLicenseExpired(App app) throws Exception {
		assertLicensePropertiesNotExisted(getProductId(app));

		deployAppLicense(app, GRACE_PERIOD + VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getProductId(app));

		Assert.assertTrue(LicenseManagerUtil.isAppEnabled(app));

		Thread.sleep(VALIDITY_PERIOD);

		assertLicensePropertiesExisted(getProductId(app));

		Assert.assertFalse(LicenseManagerUtil.isAppEnabled(app));
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

	private SafeCloseable _safeCloseable;

}