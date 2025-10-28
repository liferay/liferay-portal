/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_6;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Michael Bowerman
 */
public class UpgradeResourceAction extends UpgradeProcess {

	protected void deleteDuplicateBitwiseValuesOnResource() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select name, bitwiseValue, min(resourceActionId) as " +
					"minResourceActionId from ResourceAction group by name, " +
						"bitwiseValue having count(resourceActionId) > 1");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select resourceActionId, actionId from ResourceAction where " +
					"name = ? and bitwiseValue = ? and resourceActionId != ?");
			ResultSet resultSet1 = preparedStatement1.executeQuery()) {

			while (resultSet1.next()) {
				String name = resultSet1.getString("name");
				long bitwiseValue = resultSet1.getLong("bitwiseValue");
				long minResourceActionId = resultSet1.getLong(
					"minResourceActionId");

				preparedStatement2.setString(1, name);
				preparedStatement2.setLong(2, bitwiseValue);
				preparedStatement2.setLong(3, minResourceActionId);

				try (ResultSet resultSet2 = preparedStatement2.executeQuery()) {
					while (resultSet2.next()) {
						if (_log.isInfoEnabled()) {
							_log.info(
								StringBundler.concat(
									"Deleting resource action ",
									resultSet2.getString("actionId"),
									" from resource ", name,
									" because its bitwise value is the same ",
									"as another resource action on the same ",
									"resource"));
						}

						try (PreparedStatement preparedStatement3 =
								connection.prepareStatement(
									"delete from ResourceAction where " +
										"resourceActionId = ?")) {

							preparedStatement3.setLong(
								1, resultSet2.getLong("resourceActionId"));

							preparedStatement3.execute();
						}
					}
				}
			}
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		deleteDuplicateBitwiseValuesOnResource();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeResourceAction.class);

}