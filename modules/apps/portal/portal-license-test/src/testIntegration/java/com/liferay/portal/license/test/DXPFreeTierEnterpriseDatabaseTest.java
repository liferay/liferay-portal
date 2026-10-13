/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class DXPFreeTierEnterpriseDatabaseTest extends BaseLicenseTestCase {

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
	public void testFreeTierLicense() throws Exception {
		DB db = DBManagerUtil.getDB();

		for (DBType dbType : _DB_TYPES) {
			try (AutoCloseable autoCloseable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						db, "_dbType", dbType);
				SafeCloseable safeCloseable =
					resetLicenseDataWithSafeCloseble()) {

				deployFreeTierPortalLicense(Time.HOUR);

				assertPortalLicenseRegistered();
			}
		}
	}

	@Test
	public void testFreeTierLicenseSetupWizard() throws Exception {
		Assume.assumeTrue(PropsValues.SETUP_WIZARD_ENABLED);

		assertPortalLicenseNotRegistered();

		deployFreeTierPortalLicense(Time.HOUR);

		assertPortalLicenseRegistered();

		String response = hitHomePage("localhost", getLocalPort());

		for (DBType dbType : _DB_TYPES) {
			Assert.assertTrue(
				response.contains("value=\"" + dbType.getName() + "\""));
		}
	}

	private static final DBType[] _DB_TYPES = {
		DBType.DB2, DBType.HYPERSONIC, DBType.MARIADB, DBType.MYSQL,
		DBType.POSTGRESQL, DBType.ORACLE, DBType.SQLSERVER
	};

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

	private SafeCloseable _safeCloseable;

}