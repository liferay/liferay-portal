/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.verify.PreupgradeVerifyDatabaseState;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.net.URL;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class PreupgradeVerifyDatabaseStateTest
	extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			_currentSchemaVersion =
				PortalUpgradeProcess.getCurrentSchemaVersion(connection);

			PortalUpgradeProcess.updateSchemaVersion(
				connection, _TEST_SCHEMA_VERSION);
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			PortalUpgradeProcess.updateSchemaVersion(
				connection, _currentSchemaVersion);
		}
	}

	@Override
	@Test
	public void testVerify() throws Exception {
		try {
			super.testVerify();
		}
		catch (Exception exception) {
				_verifyException(
					exception,
					"Stale tables from a previous upgrade detected:");
		}
	}

	@Test
	public void testVerifyPreupgradeMissingTable() throws Exception {
		long serviceComponentId = RandomTestUtil.nextLong();

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				serviceComponentId);

		serviceComponent.setMvccVersion(RandomTestUtil.nextLong());

		serviceComponent.setBuildNamespace("com.liferay.test.service.impl");

		serviceComponent.setData("<![CDATA[create table TestTable (");

		_serviceComponentLocalService.addServiceComponent(serviceComponent);

		try {
			super.testVerify();
		}
		catch (Exception exception) {
			_verifyException(
				exception, "Missing tables detected:\n[testtable]");
		}
		finally {
			_serviceComponentLocalService.deleteServiceComponent(serviceComponent);
		}
	}


	@Override
	protected VerifyProcess getVerifyProcess() {
		return new PreupgradeVerifyDatabaseState();
	}

	private void _verifyException(Exception exception, String expectedMessage)
		throws Exception {

		String message = exception.getMessage();

		Assert.assertTrue(message.contains(expectedMessage));
	}

	private static final Version _TEST_SCHEMA_VERSION = new Version(0, 0, 0);

	private static Version _currentSchemaVersion;

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}