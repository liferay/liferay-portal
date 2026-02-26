/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v16_0_0;

import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Matthew Kong
 */
public class UpgradeUserGroupRoleUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select roleId from Role_ where name in (?, ?, ?)");
			PreparedStatement updatePreparedStatement =
				connection.prepareStatement(
					"delete from UserGroupRole where roleId = ?")) {

			preparedStatement.setString(1, RoleConstants.SITE_ADMINISTRATOR);
			preparedStatement.setString(2, RoleConstants.SITE_MEMBER);
			preparedStatement.setString(3, RoleConstants.SITE_OWNER);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				updatePreparedStatement.setLong(1, resultSet.getLong(1));

				updatePreparedStatement.addBatch();
			}

			updatePreparedStatement.executeBatch();
		}
	}

}