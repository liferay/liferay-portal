/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v24_2_0;

import com.liferay.osb.faro.util.FaroProjectUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eudaldo Alonso
 */
public class GroupFriendlyURLUpgradeProcess extends UpgradeProcess {

	public GroupFriendlyURLUpgradeProcess(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId from OSBFaro_FaroProject")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Group group = _groupLocalService.fetchGroup(
						resultSet.getLong("groupId"));

					if ((group == null) ||
						Validator.isNotNull(group.getFriendlyURL())) {

						continue;
					}

					_groupLocalService.updateFriendlyURL(
						group.getGroupId(),
						FaroProjectUtil.getDefaultFriendlyURL(
							group.getGroupId()));
				}
			}
		}
	}

	private final GroupLocalService _groupLocalService;

}