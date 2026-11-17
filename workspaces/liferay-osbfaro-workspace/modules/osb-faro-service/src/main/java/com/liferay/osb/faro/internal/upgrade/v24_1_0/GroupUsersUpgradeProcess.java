/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v24_1_0;

import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eudaldo Alonso
 */
public class GroupUsersUpgradeProcess extends UpgradeProcess {

	public GroupUsersUpgradeProcess(
		GroupLocalService groupLocalService,
		UserLocalService userLocalService) {

		_groupLocalService = groupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId, liveUserId from OSBFaro_FaroUser where " +
					"liveUserId > 0 and status = ?")) {

			preparedStatement.setInt(1, FaroUserConstants.STATUS_APPROVED);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long groupId = resultSet.getLong("groupId");

					long liveUserId = resultSet.getLong("liveUserId");

					User user = _userLocalService.fetchUser(liveUserId);

					if (user == null) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								StringBundler.concat(
									"Unable to add user ", liveUserId,
									" to group ", groupId,
									" because the user does not exist"));
						}

						continue;
					}

					_groupLocalService.addUserGroup(liveUserId, groupId);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupUsersUpgradeProcess.class);

	private final GroupLocalService _groupLocalService;
	private final UserLocalService _userLocalService;

}