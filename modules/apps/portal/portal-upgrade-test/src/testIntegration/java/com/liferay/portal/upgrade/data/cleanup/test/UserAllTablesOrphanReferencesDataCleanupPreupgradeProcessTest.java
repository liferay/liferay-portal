/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.UserAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.test.log.LogCapture;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class UserAllTablesOrphanReferencesDataCleanupPreupgradeProcessTest
	extends BaseOrphanReferencesDataCleanupPreupgradeProcessTestCase {

	@Before
	public void setUp() throws Exception {
		_companyId = PortalInstancePool.getDefaultCompanyId();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(_companyId)) {

			_adminUser = UserTestUtil.getAdminUser(_companyId);
		}

		_userId = RandomTestUtil.nextLong();
	}

	@After
	public void tearDown() throws Exception {
		db.runSQL("drop table " + _TABLE_NAME);
	}

	@Override
	protected UnsafeRunnable<Exception> getInsertDataUnsafeRunnable() {
		return () -> {
			db.runSQL(
				StringBundler.concat(
					"create table ", _TABLE_NAME,
					" (mvccVersion LONG default 0 not null, testId LONG not ",
					"null, userId VARCHAR(50) not null, companyId LONG not ",
					"null, primary key(testId, userId))"));

			db.runSQL(
				StringBundler.concat(
					"insert into ", _TABLE_NAME, " (mvccVersion, testId, ",
					"companyId, userId) values (0, 0, ", _companyId, ", '",
					_userId, "')"));
			db.runSQL(
				connection,
				StringBundler.concat(
					"insert into Layout (mvccVersion, ctCollectionId, plid, ",
					"groupId, companyId, userId, layoutId) values (0, 0, ",
					RandomTestUtil.nextLong(), ", ", RandomTestUtil.nextLong(),
					", ", _companyId, ", ", _userId, ", ",
					RandomTestUtil.nextLong(), ")"));
			db.runSQL(
				connection,
				StringBundler.concat(
					"insert into MBDiscussion (mvccVersion, ctCollectionId, ",
					"discussionId, companyId, userId) values (", 0, ", ", 0,
					", ", RandomTestUtil.nextLong(), ", 0, ", _userId, ")"));
			db.runSQL(
				connection,
				StringBundler.concat(
					"insert into Users_Roles (companyId, roleId, userId, ",
					"ctCollectionId) values (", _companyId, ", ",
					RandomTestUtil.nextLong(), ", ", _userId, ", 0)"));
		};
	}

	@Override
	protected UnsafeBiConsumer<LogCapture, LogCapture, Exception>
		getLogAssertionUnsafeBiConsumer() {

		return (logCapture1, logCapture2) -> {
			List<String> messages = logCapture1.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", dbInspector.normalizeName("Layout"), ", 1 ",
						"row updated column ",
						dbInspector.normalizeName("userId"), " to value ",
						_adminUser.getUserId(), " because ",
						dbInspector.normalizeName("userId"), StringPool.SPACE,
						_userId, " was not found in column ",
						dbInspector.normalizeName("userId"), " from table ",
						dbInspector.normalizeName("User_"))));
			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", dbInspector.normalizeName("Users_Roles"),
						", 1 row deleted because ",
						dbInspector.normalizeName("userId"), StringPool.SPACE,
						_userId, " was not found in column ",
						dbInspector.normalizeName("userId"), " from table ",
						dbInspector.normalizeName("User_"))));
			Assert.assertTrue(
				messages.contains("No admin user found for company 0"));

			messages = logCapture2.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", dbInspector.normalizeName(_TABLE_NAME),
						" and column ", dbInspector.normalizeName("userId"),
						" has an incompatible type with table ",
						dbInspector.normalizeName("User_"), " and column ",
						dbInspector.normalizeName("userId"))));
		};
	}

	@Override
	protected String getLoggerClassName() {
		return UserAllTablesOrphanReferencesDataCleanupPreupgradeProcess.class.
			getName();
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new UserAllTablesOrphanReferencesDataCleanupPreupgradeProcess();
	}

	private static final String _TABLE_NAME = "TestTable";

	private User _adminUser;
	private long _companyId;
	private long _userId;

}