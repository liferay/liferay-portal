/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.upgrade.v4_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Yuri Monteiro
 */
@RunWith(Arquillian.class)
public class NotificationTemplateUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"delete from NotificationTemplate where companyId in (",
				_COMPANY_ID_1, ", ", _COMPANY_ID_2, ")"));
	}

	@Test
	public void testUpgrade() throws Exception {
		_insertNotificationTemplate(
			_COMPANY_ID_1, "COMMERCE_ORDER_TEMPLATE",
			_NOTIFICATION_TEMPLATE_ID_1);
		_insertNotificationTemplate(
			_COMPANY_ID_1, "L_COMMERCE_ORDER_TEMPLATE",
			_NOTIFICATION_TEMPLATE_ID_2);
		_insertNotificationTemplate(
			_COMPANY_ID_2, "L_COMMERCE_ORDER_TEMPLATE",
			_NOTIFICATION_TEMPLATE_ID_3);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.INFO)) {

			upgradeProcess.upgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to modify \"L_COMMERCE_ORDER_TEMPLATE\" to ",
					"\"COMMERCE_ORDER_TEMPLATE\" because it is already used ",
					"by notification template ", _NOTIFICATION_TEMPLATE_ID_1),
				logEntry.getMessage());
		}

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.getNotificationTemplate(
				_NOTIFICATION_TEMPLATE_ID_1);

		Assert.assertEquals(
			"COMMERCE_ORDER_TEMPLATE",
			notificationTemplate.getExternalReferenceCode());

		notificationTemplate =
			_notificationTemplateLocalService.getNotificationTemplate(
				_NOTIFICATION_TEMPLATE_ID_2);

		Assert.assertEquals(
			"L_COMMERCE_ORDER_TEMPLATE",
			notificationTemplate.getExternalReferenceCode());

		notificationTemplate =
			_notificationTemplateLocalService.getNotificationTemplate(
				_NOTIFICATION_TEMPLATE_ID_3);

		Assert.assertEquals(
			"COMMERCE_ORDER_TEMPLATE",
			notificationTemplate.getExternalReferenceCode());
	}

	private void _insertNotificationTemplate(
			long companyId, String externalReferenceCode,
			long notificationTemplateId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into NotificationTemplate (mvccVersion, uuid_, ",
					"externalReferenceCode, notificationTemplateId, ",
					"companyId, userId, userName, createDate, modifiedDate, ",
					"objectDefinitionId, body, description, editorType, name, ",
					"recipientType, subject, system_, type_) values (?, ?, ?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, RandomTestUtil.nextLong());
			preparedStatement.setString(2, RandomTestUtil.randomString());
			preparedStatement.setString(3, externalReferenceCode);
			preparedStatement.setLong(4, notificationTemplateId);
			preparedStatement.setLong(5, companyId);
			preparedStatement.setLong(6, RandomTestUtil.nextLong());
			preparedStatement.setString(7, RandomTestUtil.randomString());
			preparedStatement.setTimestamp(8, _timestamp);
			preparedStatement.setTimestamp(9, _timestamp);
			preparedStatement.setLong(10, RandomTestUtil.nextLong());
			preparedStatement.setString(11, RandomTestUtil.randomString());
			preparedStatement.setString(12, RandomTestUtil.randomString());
			preparedStatement.setString(13, RandomTestUtil.randomString());
			preparedStatement.setString(14, RandomTestUtil.randomString());
			preparedStatement.setString(15, RandomTestUtil.randomString());
			preparedStatement.setString(16, RandomTestUtil.randomString());
			preparedStatement.setBoolean(17, false);
			preparedStatement.setString(18, RandomTestUtil.randomString());

			preparedStatement.executeUpdate();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.notification.internal.upgrade.v4_0_1." +
			"NotificationTemplateUpgradeProcess";

	private static final long _COMPANY_ID_1 = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID_2 = RandomTestUtil.randomLong();

	private static final long _NOTIFICATION_TEMPLATE_ID_1 =
		RandomTestUtil.randomLong();

	private static final long _NOTIFICATION_TEMPLATE_ID_2 =
		RandomTestUtil.randomLong();

	private static final long _NOTIFICATION_TEMPLATE_ID_3 =
		RandomTestUtil.randomLong();

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	private final Timestamp _timestamp = new Timestamp(
		System.currentTimeMillis());

	@Inject(
		filter = "component.name=com.liferay.notification.internal.upgrade.registry.NotificationUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}