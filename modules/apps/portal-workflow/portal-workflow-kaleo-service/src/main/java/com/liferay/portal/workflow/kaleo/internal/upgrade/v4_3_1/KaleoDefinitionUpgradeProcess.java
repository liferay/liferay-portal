/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_3_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Mateus Xavier
 */
public class KaleoDefinitionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select ctCollectionId, kaleoDefinitionId, name from ",
						"KaleoDefinition where name = ",
						"'message-boards-user-stats-moderation' or name = '",
						WorkflowDefinitionConstants.NAME_SINGLE_APPROVER,
						"'")));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"update KaleoDefinition set externalReferenceCode = ?,",
						"name = ? where ctCollectionId = ? and ",
						"kaleoDefinitionId = ?"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				if (StringUtil.equals(
						resultSet.getString("name"),
						"message-boards-user-stats-moderation")) {

					preparedStatement2.setString(
						1,
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_MESSAGE_BOARDS_USER_STATS_MODERATION);
					preparedStatement2.setString(
						2,
						WorkflowDefinitionConstants.
							NAME_MESSAGE_BOARDS_USER_STATS_MODERATION);
				}
				else {
					preparedStatement2.setString(
						1,
						WorkflowDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_SINGLE_APPROVER);
					preparedStatement2.setString(
						2, WorkflowDefinitionConstants.NAME_SINGLE_APPROVER);
				}

				preparedStatement2.setLong(
					3, resultSet.getLong("ctCollectionId"));
				preparedStatement2.setLong(
					4, resultSet.getLong("kaleoDefinitionId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

}