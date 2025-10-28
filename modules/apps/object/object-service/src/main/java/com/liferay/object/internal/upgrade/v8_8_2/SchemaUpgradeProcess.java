/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v8_8_2;

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
public class SchemaUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select ObjectField.dbColumnName, ObjectField.",
						"dbTableName from ObjectField inner join ",
						"ObjectDefinition on ObjectDefinition.",
						"objectDefinitionId = ObjectField.objectDefinitionId ",
						"where ObjectDefinition.status = ? and ObjectField.",
						"businessType = '",
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, "'")))) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_APPROVED);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					alterColumnType(
						resultSet.getString("dbTableName"),
						resultSet.getString("dbColumnName"),
						"VARCHAR(75) null");
				}
			}
		}
	}

}