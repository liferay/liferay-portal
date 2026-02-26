/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v5_22_0;

import com.liferay.commerce.product.internal.upgrade.v5_22_0.util.CPSpecificationOptionListTypeDefinitionRelTable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Danny Situ
 */
public class CPSpecificationOptionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select * from CPSpecificationOption");

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"insert into CPSOListTypeDefinitionRel (mvccVersion, ",
						"ctCollectionId, CPSOListTypeDefinitionRelId,",
						"companyId, CPSpecificationOptionId, ",
						"listTypeDefinitionId) values (?, ?, ?, ?, ?, ?)"))) {

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long listTypeDefinitionId = resultSet.getLong(
						"listTypeDefinitionId");

					if (listTypeDefinitionId <= 0) {
						continue;
					}

					preparedStatement.setLong(1, 0);
					preparedStatement.setLong(2, 0);
					preparedStatement.setLong(3, increment());
					preparedStatement.setLong(
						4, resultSet.getLong("companyId"));
					preparedStatement.setLong(
						5, resultSet.getLong("CPSpecificationOptionId"));
					preparedStatement.setLong(6, listTypeDefinitionId);

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns(
				"CPSpecificationOption", "listTypeDefinitionId")
		};
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			CPSpecificationOptionListTypeDefinitionRelTable.create()
		};
	}

}