/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_11_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class StyleBookEntryStatusUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgrade() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, null,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		for (String tableName : _TABLE_NAMES) {
			try (Connection connection = DataAccess.getConnection();

				PreparedStatement preparedStatement =
					connection.prepareStatement(
						"update " + tableName +
							" set status = ? where groupId = ?")) {

				preparedStatement.setInt(1, WorkflowConstants.STATUS_ANY);
				preparedStatement.setLong(2, _group.getGroupId());

				preparedStatement.executeUpdate();
			}
		}

		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(1, 11, 0));

		UpgradeProcess upgradeProcess =
			upgradeProcesses[upgradeProcesses.length - 1];

		upgradeProcess.upgrade();

		_assertStatus(
			draftStyleBookEntry.getStyleBookEntryId(), "StyleBookEntry");
		_assertStatus(styleBookEntry.getStyleBookEntryId(), "StyleBookEntry");
		_assertStatus(
			styleBookEntry.getStyleBookEntryId(), "StyleBookEntryVersion");
	}

	private void _assertStatus(long styleBookEntryId, String tableName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select status from ", tableName,
					" where styleBookEntryId = ?"))) {

			preparedStatement.setLong(1, styleBookEntryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(
					WorkflowConstants.STATUS_APPROVED,
					resultSet.getInt("status"));
			}
		}
	}

	private static final String[] _TABLE_NAMES = {
		"StyleBookEntry", "StyleBookEntryVersion"
	};

	private Group _group;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.style.book.internal.upgrade.registry.StyleBookServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}