/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Paulo Albuquerque
 */
public class ObjectDefinitionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select objectDefinitionId, dbTableName, ",
						"pkObjectFieldDBColumnName, pkObjectFieldName from ",
						"ObjectDefinition where modifiable = [$TRUE$] and ",
						"system_ = [$TRUE$]")));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"update ObjectDefinition set ",
						"pkObjectFieldDBColumnName = ?, pkObjectFieldName = ? ",
						"where objectDefinitionId = ?"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String oldPKObjectFieldDBColumnName = resultSet.getString(
					"pkObjectFieldDBColumnName");

				String newPKObjectFieldDBColumnName = StringUtil.replaceFirst(
					oldPKObjectFieldDBColumnName, "c_", "l_");

				preparedStatement2.setString(1, newPKObjectFieldDBColumnName);

				preparedStatement2.setString(
					2,
					StringUtil.replaceFirst(
						resultSet.getString("pkObjectFieldName"), "c_", "l_"));
				preparedStatement2.setLong(
					3, resultSet.getLong("objectDefinitionId"));

				preparedStatement2.addBatch();

				_alterColumnName(
					resultSet.getString("dbTableName"),
					oldPKObjectFieldDBColumnName, newPKObjectFieldDBColumnName);
				_alterColumnName(
					resultSet.getString("dbTableName") + "_x",
					oldPKObjectFieldDBColumnName, newPKObjectFieldDBColumnName);
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _alterColumnName(
			String dbTableName, String oldPKObjectFieldDBColumnName,
			String newPKObjectFieldDBColumnName)
		throws Exception {

		if (!hasColumn(dbTableName, oldPKObjectFieldDBColumnName)) {
			return;
		}

		alterColumnName(
			dbTableName, oldPKObjectFieldDBColumnName,
			newPKObjectFieldDBColumnName + " LONG not null");
	}

}