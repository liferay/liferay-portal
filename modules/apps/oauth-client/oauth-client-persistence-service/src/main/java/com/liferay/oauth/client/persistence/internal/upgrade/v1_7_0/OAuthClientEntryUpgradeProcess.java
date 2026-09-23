/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.v1_7_0;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author István András Dézsi
 */
public class OAuthClientEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasColumn("OAuthClientEntry", "parametersJSON")) {
			return;
		}

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update OAuthClientEntry set authRequestParametersJSON = " +
						"?, tokenRequestParametersJSON = '{}' where " +
							"oAuthClientEntryId = ?");

			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(
				"select oAuthClientEntryId, parametersJSON from " +
					"OAuthClientEntry")) {

			while (resultSet.next()) {
				preparedStatement.setString(
					1, resultSet.getString("parametersJSON"));
				preparedStatement.setLong(
					2, resultSet.getLong("oAuthClientEntryId"));

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns(
				"OAuthClientEntry", "parametersJSON")
		};
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"OAuthClientEntry",
				"authRequestParametersJSON VARCHAR(3999) null",
				"tokenRequestParametersJSON VARCHAR(3999) null"),
			UpgradeProcessFactory.alterColumnType(
				"OAuthClientEntry", "clientId", "VARCHAR(256) null")
		};
	}

}