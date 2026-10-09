/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v12_0_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Pedro Leite
 */
public class ObjectFieldUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			StringBundler.concat(
				"update ObjectField set dbType = '",
				ObjectFieldConstants.DB_TYPE_CLOB, "' where businessType = '",
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
				"' and dbType = '", ObjectFieldConstants.DB_TYPE_STRING, "'"));

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select ObjectField.dbColumnName, ObjectField.",
						"dbTableName, ObjectField.localized, ",
						"ObjectDefinition.dbTableName as ",
						"objectDefinitionDBTableName from ObjectField inner ",
						"join ObjectDefinition on ObjectDefinition.",
						"objectDefinitionId = ObjectField.objectDefinitionId ",
						"where ObjectDefinition.status = ? and ObjectField.",
						"businessType = ?")))) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_APPROVED);
			preparedStatement.setString(
				2, ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String dbColumnName = resultSet.getString("dbColumnName");

					_alterColumnType(
						resultSet.getString("dbTableName"), dbColumnName);

					if (!resultSet.getBoolean("localized")) {
						continue;
					}

					_alterColumnType(
						resultSet.getString("objectDefinitionDBTableName") +
							"_l",
						dbColumnName);
				}
			}
		}
	}

	private void _alterColumnType(String dbTableName, String dbColumnName)
		throws Exception {

		if (!hasColumn(dbTableName, dbColumnName)) {
			return;
		}

		alterColumnType(dbTableName, dbColumnName, "TEXT null");
	}

}