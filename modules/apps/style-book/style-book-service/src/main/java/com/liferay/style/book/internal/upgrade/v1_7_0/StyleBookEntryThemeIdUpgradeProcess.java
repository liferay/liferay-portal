/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_7_0;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryThemeIdUpgradeProcess extends UpgradeProcess {

	public StyleBookEntryThemeIdUpgradeProcess(
		GroupLocalService groupLocalService) {

		_groupLocalService = groupLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, groupId, styleBookEntryId from " +
					"StyleBookEntry");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update StyleBookEntry set themeId = ? where " +
						"ctCollectionId = ? and styleBookEntryId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				Group group = _groupLocalService.getGroup(
					resultSet.getLong("groupId"));

				LayoutSet privateLayoutSet = group.getPrivateLayoutSet();

				preparedStatement2.setString(1, privateLayoutSet.getThemeId());

				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));

				preparedStatement2.setLong(
					3, resultSet.getLong("styleBookEntryId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private final GroupLocalService _groupLocalService;

}