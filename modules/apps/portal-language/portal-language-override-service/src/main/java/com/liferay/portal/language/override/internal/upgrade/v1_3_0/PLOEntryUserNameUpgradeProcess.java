/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal.upgrade.v1_3_0;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Thiago Buarque
 */
public class PLOEntryUserNameUpgradeProcess extends UpgradeProcess {

	public PLOEntryUserNameUpgradeProcess(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			"select distinct userId from PLOEntry",
			"update PLOEntry set userName = ? where userId = ?",
			resultSet -> new Object[] {resultSet.getLong("userId")},
			(values, preparedStatement) -> {
				long userId = (Long)values[0];

				User user = _userLocalService.fetchUser(userId);

				if (user == null) {
					return;
				}

				preparedStatement.setString(1, user.getFullName());
				preparedStatement.setLong(2, userId);

				preparedStatement.addBatch();
			},
			null);
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"PLOEntry", "userName VARCHAR(75) null")
		};
	}

	private final UserLocalService _userLocalService;

}