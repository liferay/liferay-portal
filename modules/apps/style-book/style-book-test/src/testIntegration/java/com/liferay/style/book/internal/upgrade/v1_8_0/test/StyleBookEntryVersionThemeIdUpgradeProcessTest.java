/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_8_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class StyleBookEntryVersionThemeIdUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@FeatureFlag("LPD-30204")
	@Test
	public void testUpgrade() throws Exception {
		long groupId = RandomTestUtil.randomLong();
		String themeId = "classic_WAR_classictheme";

		StyleBookEntry orphanedStyleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				groupId, false, null, RandomTestUtil.randomString(), null,
				themeId, _serviceContext);

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, RandomTestUtil.randomString(),
				null, themeId, _serviceContext);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.style.book.internal.upgrade.v1_8_0." +
					"StyleBookEntryVersionThemeIdUpgradeProcess",
				LoggerTestUtil.WARN)) {

			_runUpgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Deleting style book entry version for style book entry ",
					orphanedStyleBookEntry.getStyleBookEntryId(),
					" because group ", groupId, " does not exist"),
				logEntry.getMessage());
		}

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select uuid_, themeId from StyleBookEntryVersion where " +
					"styleBookEntryId = ? or styleBookEntryId = ?")) {

			preparedStatement.setLong(1, styleBookEntry.getStyleBookEntryId());
			preparedStatement.setLong(
				2, orphanedStyleBookEntry.getStyleBookEntryId());

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(
					styleBookEntry.getUuid(), resultSet.getString("uuid_"));
				Assert.assertEquals(
					"classic_WAR_classictheme", resultSet.getString("themeId"));

				Assert.assertFalse(resultSet.next());
			}
		}
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.style.book.internal.upgrade.v1_8_0." +
			"StyleBookEntryVersionThemeIdUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.style.book.internal.upgrade.registry.StyleBookServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}