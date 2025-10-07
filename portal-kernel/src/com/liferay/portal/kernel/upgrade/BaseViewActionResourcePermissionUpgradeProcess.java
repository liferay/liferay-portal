/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseViewActionResourcePermissionUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			StringBundler.concat(
				"update ResourcePermission set actionIds = BITOR(",
				_getBitwiseValue(), ", actionIds) where name = '",
				getClassName(),
				"' and primKeyId != 0 and viewActionId = [$TRUE$]"));
	}

	protected abstract String getActionId();

	protected abstract String getClassName();

	private long _getBitwiseValue() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select bitwiseValue from ResourceAction where name = ? and " +
					"actionId = ?")) {

			preparedStatement.setString(1, getClassName());
			preparedStatement.setString(2, getActionId());

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getLong("bitwiseValue");
			}

			return 0;
		}
	}

}