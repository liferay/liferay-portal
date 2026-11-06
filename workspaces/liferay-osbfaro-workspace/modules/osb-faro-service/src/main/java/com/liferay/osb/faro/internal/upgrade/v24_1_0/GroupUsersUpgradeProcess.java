/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v24_1_0;

import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eudaldo Alonso
 */
public class GroupUsersUpgradeProcess extends UpgradeProcess {

	public GroupUsersUpgradeProcess(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId, liveUserId from OSBFaro_FaroUser where " +
					"liveUserId > 0 and status = ?")) {

			preparedStatement.setInt(1, FaroUserConstants.STATUS_APPROVED);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					_groupLocalService.addUserGroup(
						resultSet.getLong("liveUserId"),
						resultSet.getLong("groupId"));
				}
			}
		}
	}

	private final GroupLocalService _groupLocalService;

}