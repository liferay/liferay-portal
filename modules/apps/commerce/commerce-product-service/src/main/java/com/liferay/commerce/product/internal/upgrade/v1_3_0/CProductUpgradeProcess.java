/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v1_3_0;

import com.liferay.commerce.product.internal.upgrade.v1_3_0.util.CProductTable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Ethan Bustad
 * @author Alessio Antonio Rendina
 */
public class CProductUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		String insertCProductSQL = StringBundler.concat(
			"insert into CProduct (uuid_, CProductId, groupId, companyId, ",
			"userId, userName, createDate, modifiedDate, ",
			"publishedCPDefinitionId, latestVersion) values (?, ?, ?, ?, ?, ",
			"?, ?, ?, ?, 1)");
		String updateCPDefinitionSQL =
			"update CPDefinition set CProductId = ?, version = 1 where " +
				"CPDefinitionId = ?";

		try (PreparedStatement preparedStatement1 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, insertCProductSQL);
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, updateCPDefinitionSQL);
			Statement s = connection.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = s.executeQuery(
				"select cpDefinitionId, groupId, companyId, userId, userName " +
					"from CPDefinition")) {

			while (resultSet.next()) {
				String uuid = PortalUUIDUtil.generate();
				long cProductId = increment();
				long groupId = resultSet.getLong("groupId");
				long companyId = resultSet.getLong("companyId");
				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");
				long cpDefinitionId = resultSet.getLong("CPDefinitionId");

				preparedStatement1.setString(1, uuid);
				preparedStatement1.setLong(2, cProductId);
				preparedStatement1.setLong(3, groupId);
				preparedStatement1.setLong(4, companyId);
				preparedStatement1.setLong(5, userId);
				preparedStatement1.setString(6, userName);

				Date date = new Date(System.currentTimeMillis());

				preparedStatement1.setDate(7, date);
				preparedStatement1.setDate(8, date);

				preparedStatement1.setLong(9, cpDefinitionId);

				preparedStatement1.addBatch();

				preparedStatement2.setLong(1, cProductId);
				preparedStatement2.setLong(2, cpDefinitionId);

				preparedStatement2.addBatch();
			}

			preparedStatement1.executeBatch();

			preparedStatement2.executeBatch();
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			CProductTable.create(),
			UpgradeProcessFactory.addColumns(
				"CPDefinition", "CProductId LONG", "version INTEGER")
		};
	}

}