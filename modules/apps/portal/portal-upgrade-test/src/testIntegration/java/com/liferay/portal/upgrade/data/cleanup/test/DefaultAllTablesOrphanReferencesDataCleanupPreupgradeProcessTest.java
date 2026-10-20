/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.test.log.LogCapture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcessTest
	extends BaseOrphanReferencesDataCleanupPreupgradeProcessTestCase {

	@Before
	public void setUp() {
		_companyId1 = RandomTestUtil.nextLong();
		_companyId2 = RandomTestUtil.nextLong();
	}

	@After
	public void tearDown() throws Exception {
		db.runSQL("drop table " + _CUSTOM_TABLE_NAME);
		db.runSQL("drop table " + _TABLE_NAME);
	}

	@Override
	protected UnsafeRunnable<Exception> getInsertDataUnsafeRunnable() {
		return () -> {
			db.runSQL(
				StringBundler.concat(
					"create table ", _CUSTOM_TABLE_NAME,
					" (customTestId LONG not null primary key, companyId ",
					"LONG)"));

			db.runSQL(
				StringBundler.concat(
					"insert into ", _CUSTOM_TABLE_NAME,
					" (customTestId, companyId) values (",
					RandomTestUtil.nextLong(), ", ", _companyId1, ")"));

			db.runSQL(
				StringBundler.concat(
					"create table ", _TABLE_NAME,
					" (mvccVersion LONG default 0 not null, testId LONG not ",
					"null primary key, companyId VARCHAR(50))"));

			db.runSQL(
				StringBundler.concat(
					"insert into ", _TABLE_NAME, " (mvccVersion, testId, ",
					"companyId ) values (0, 0, '", _companyId1, "')"));
			_insert(_companyId1);
			_insert(_companyId1);
			_insert(_companyId2);
		};
	}

	@Override
	protected UnsafeBiConsumer<LogCapture, LogCapture, Exception>
		getLogAssertionUnsafeBiConsumer() {

		return (logCapture1, logCapture2) -> {
			Assert.assertEquals(1, _getCount(_CUSTOM_TABLE_NAME));

			List<String> messages = logCapture1.getMessages();

			Assert.assertFalse(
				messages.contains(
					getExpectedMessage(
						1, "companyId", _CUSTOM_TABLE_NAME,
						new String[] {"companyId"}, "Company", _companyId1)));
			Assert.assertTrue(
				messages.contains(
					getExpectedMessage(
						2, "companyId", "Image", new String[] {"companyId"},
						"Company", _companyId1)));
			Assert.assertTrue(
				messages.contains(
					getExpectedMessage(
						1, "companyId", "Image", new String[] {"companyId"},
						"Company", _companyId2)));
			Assert.assertTrue(
				messages.contains(
					getExpectedMessage(
						2, "companyId", "Portlet", new String[] {"companyId"},
						"Company", _companyId1)));
			Assert.assertTrue(
				messages.contains(
					getExpectedMessage(
						1, "companyId", "Portlet", new String[] {"companyId"},
						"Company", _companyId2)));

			messages = logCapture2.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", dbInspector.normalizeName(_TABLE_NAME),
						" and column ", dbInspector.normalizeName("companyId"),
						" has an incompatible type with table ",
						dbInspector.normalizeName("Company"), " and column ",
						dbInspector.normalizeName("companyId"))));
		};
	}

	@Override
	protected String getLoggerClassName() {
		return OrphanReferencesDataCleanupUtil.class.getName();
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
			"companyId", "Company");
	}

	private long _getCount(String tableName) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) as count from " + tableName);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			return resultSet.getLong("count");
		}
	}

	private void _insert(long companyId) throws Exception {
		db.runSQL(
			connection,
			StringBundler.concat(
				"insert into Image (mvccVersion, ctCollectionId, imageId, ",
				"companyId) values (0, 0, ", RandomTestUtil.nextLong(), ", ",
				companyId, ")"));

		db.runSQL(
			connection,
			StringBundler.concat(
				"insert into Portlet (mvccVersion, id_, companyId, portletId, ",
				"active_) values (0, ", RandomTestUtil.nextLong(), ", ",
				companyId, ", '", RandomTestUtil.randomString(),
				"', [$FALSE$])"));
	}

	private static final String _CUSTOM_TABLE_NAME = "CustomTestTable";

	private static final String _TABLE_NAME = "r_TestTable";

	private long _companyId1;
	private long _companyId2;

}