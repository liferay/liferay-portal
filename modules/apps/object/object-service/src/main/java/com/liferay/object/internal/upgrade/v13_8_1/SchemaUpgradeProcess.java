/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_8_1;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Jhosseph Gonzalez
 */
public class SchemaUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select dbTableName from ObjectDefinition");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String localizationDBTableName =
					resultSet.getString("dbTableName") + "_l";

				if (hasColumn(localizationDBTableName, "languageId")) {
					alterColumnType(
						localizationDBTableName, "languageId",
						"VARCHAR(75) not null");
				}
			}
		}
	}

}