/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_13_2;

import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTableUtil;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Shuyang Zhou
 */
public class ObjectEntryExtensionTableUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					"select dbTableName, pkObjectFieldDBColumnName from " +
						"ObjectDefinition where modifiable = [$TRUE$] or " +
							"system_ = [$FALSE$]"));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String dbTableName = resultSet.getString("dbTableName");

				String extensionDBTableName = dbTableName + "_x";

				if (!hasTable(dbTableName) || !hasTable(extensionDBTableName)) {
					continue;
				}

				runSQL(
					DynamicObjectDefinitionTableUtil.
						getInsertMissingExtensionTableRowsSQL(
							extensionDBTableName,
							resultSet.getString("pkObjectFieldDBColumnName"),
							dbTableName));
			}
		}
	}

}