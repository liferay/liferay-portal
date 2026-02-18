/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_1_1;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Jorge Ferrer
 * @author Julio Camarero
 */
public class UpgradeLayoutSet extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updateLayoutSets();
	}

	protected void updateLayoutSets() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			StringBundler sb = new StringBundler(4);

			sb.append("select Group_.groupId as groupId, LayoutSet.");
			sb.append("layoutSetId as layoutSetId from LayoutSet inner join ");
			sb.append("Group_ on (LayoutSet.groupId = Group_.groupId and ");
			sb.append("Group_.liveGroupId > 0 and LayoutSet.logo = ?)");

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(sb.toString())) {

				preparedStatement.setBoolean(1, true);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						long groupId = resultSet.getLong("groupId");
						long layoutSetId = resultSet.getLong("layoutSetId");

						runSQL(
							StringBundler.concat(
								"update LayoutSet set logoId = 0 where ",
								"groupId = ", String.valueOf(groupId),
								"and layoutSetId = ",
								String.valueOf(layoutSetId)));
					}
				}
			}
		}
	}

}