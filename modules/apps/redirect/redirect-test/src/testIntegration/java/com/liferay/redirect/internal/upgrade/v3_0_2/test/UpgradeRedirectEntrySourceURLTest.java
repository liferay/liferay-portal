/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.internal.upgrade.v3_0_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.RedirectEntryLocalServiceUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Attila Bakay
 */
@RunWith(Arquillian.class)
public class UpgradeRedirectEntrySourceURLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_db = DBManagerUtil.getDB();

		_timestamp = new Timestamp(System.currentTimeMillis());
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();

		_setUpUpgradeSourceURL();
	}

	@After
	public void tearDown() throws Exception {
		_db.runSQL(
			"delete from RedirectEntry where groupId = " +
				_group1.getGroupId());
		_db.runSQL(
			"delete from RedirectEntry where groupId = " +
				_group2.getGroupId());
	}

	@Test
	public void testUpgradeWithMultipleGroups() throws Exception {
		long redirectEntryId1 = _counterLocalService.increment();
		long redirectEntryId2 = _counterLocalService.increment();
		long redirectEntryId3 = _counterLocalService.increment();
		long redirectEntryId4 = _counterLocalService.increment();
		long redirectEntryId5 = _counterLocalService.increment();
		long redirectEntryId6 = _counterLocalService.increment();

		_addRedirectEntry(
			redirectEntryId1, _group1.getGroupId(), _group1.getCompanyId(),
			"destination1", "Page1");
		_addRedirectEntry(
			redirectEntryId2, _group1.getGroupId(), _group1.getCompanyId(),
			"destination2", "Page2");
		_addRedirectEntry(
			redirectEntryId3, _group1.getGroupId(), _group1.getCompanyId(),
			"destination3", "Page3");

		_addRedirectEntry(
			redirectEntryId4, _group2.getGroupId(), _group2.getCompanyId(),
			"destination1", "Page1");
		_addRedirectEntry(
			redirectEntryId5, _group2.getGroupId(), _group2.getCompanyId(),
			"destination2", "Page2");
		_addRedirectEntry(
			redirectEntryId6, _group2.getGroupId(), _group2.getCompanyId(),
			"destination3", "Page3");

		_runUpgrade();

		RedirectEntry redirectEntry1 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId1);
		RedirectEntry redirectEntry2 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId2);
		RedirectEntry redirectEntry3 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId3);
		RedirectEntry redirectEntry4 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId4);
		RedirectEntry redirectEntry5 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId5);
		RedirectEntry redirectEntry6 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId6);

		Assert.assertEquals("page1", redirectEntry1.getSourceURL());
		Assert.assertEquals("page2", redirectEntry2.getSourceURL());
		Assert.assertEquals("page3", redirectEntry3.getSourceURL());
		Assert.assertEquals("page1", redirectEntry4.getSourceURL());
		Assert.assertEquals("page2", redirectEntry5.getSourceURL());
		Assert.assertEquals("page3", redirectEntry6.getSourceURL());
	}

	@Test
	public void testUpgradeWithSingleGroup() throws Exception {
		long redirectEntryId1 = _counterLocalService.increment();
		long redirectEntryId2 = _counterLocalService.increment();
		long redirectEntryId3 = _counterLocalService.increment();

		_addRedirectEntry(
			redirectEntryId1, _group1.getGroupId(), _group1.getCompanyId(),
			"destination1", "PageA");
		_addRedirectEntry(
			redirectEntryId2, _group1.getGroupId(), _group1.getCompanyId(),
			"destination2", "PageB");
		_addRedirectEntry(
			redirectEntryId3, _group1.getGroupId(), _group1.getCompanyId(),
			"destination3", "PageC");

		_runUpgrade();

		RedirectEntry redirectEntry1 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId1);
		RedirectEntry redirectEntry2 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId2);
		RedirectEntry redirectEntry3 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId3);

		Assert.assertEquals("pagea", redirectEntry1.getSourceURL());
		Assert.assertEquals("pageb", redirectEntry2.getSourceURL());
		Assert.assertEquals("pagec", redirectEntry3.getSourceURL());
	}

	@Test
	public void testUpgradeWithSourceURLCollision() throws Exception {
		Assume.assumeFalse(
			"Skip this test because MySQL is not case sensitive",
			_db.getDBType() == DBType.MYSQL);
		Assume.assumeFalse(
			"Skip this test because mariadb is not case sensitive",
			_db.getDBType() == DBType.MARIADB);
		Assume.assumeFalse(
			"Skip this test because sqlserver is not case sensitive",
			_db.getDBType() == DBType.SQLSERVER);

		long redirectEntryId1 = _counterLocalService.increment();
		long redirectEntryId2 = _counterLocalService.increment();

		_addRedirectEntry(
			redirectEntryId1, _group1.getGroupId(), _group1.getCompanyId(),
			"destination1", "testpage");
		_addRedirectEntry(
			redirectEntryId2, _group1.getGroupId(), _group1.getCompanyId(),
			"destination2", "TestPage");

		_runUpgrade();

		RedirectEntry redirectEntry1 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId1);
		RedirectEntry redirectEntry2 =
			RedirectEntryLocalServiceUtil.getRedirectEntry(redirectEntryId2);

		Assert.assertEquals("testpage", redirectEntry1.getSourceURL());
		Assert.assertEquals("TestPage", redirectEntry2.getSourceURL());
	}

	private void _addRedirectEntry(
			long redirectEntryId, long groupId, long companyId,
			String destinationURL, String sourceURL)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into RedirectEntry (uuid_, redirectEntryId,",
					"groupId, companyId, userId, userName, createDate, ",
					"modifiedDate, destinationURL, sourceURL) values (?, ?, ",
					"?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());
			preparedStatement.setLong(2, redirectEntryId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, TestPropsValues.getUserId());
			preparedStatement.setString(6, null);
			preparedStatement.setTimestamp(7, _timestamp);
			preparedStatement.setTimestamp(8, _timestamp);
			preparedStatement.setString(9, destinationURL);
			preparedStatement.setString(10, sourceURL);

			preparedStatement.executeUpdate();
		}
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			_redirectEntrySourceURLUpgradeProcess.upgrade();
		}
	}

	private void _setUpUpgradeSourceURL() {
		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<?> clazz = upgradeStep.getClass();

						if (!Objects.equals(clazz.getName(), _CLASS_NAME)) {
							continue;
						}

						_redirectEntrySourceURLUpgradeProcess =
							(UpgradeProcess)upgradeStep;
					}
				}

			});
	}

	private static final String _CLASS_NAME =
		"com.liferay.redirect.internal.upgrade.v3_0_2." +
			"RedirectEntrySourceURLUpgradeProcess";

	@Inject
	private CounterLocalService _counterLocalService;

	private DB _db;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	private UpgradeProcess _redirectEntrySourceURLUpgradeProcess;
	private Timestamp _timestamp;

	@Inject(
		filter = "(&(component.name=com.liferay.redirect.internal.upgrade.registry.RedirectServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}