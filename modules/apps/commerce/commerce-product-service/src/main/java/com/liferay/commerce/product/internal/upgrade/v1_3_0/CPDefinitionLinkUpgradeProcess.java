/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v1_3_0;

import com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Ethan Bustad
 */
public class CPDefinitionLinkUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update CPDefinitionLink set CProductId = ? where " +
					"CPDefinitionId2 = ?");

			Statement s = connection.createStatement();

			ResultSet resultSet = s.executeQuery(
				"select * from CPDefinitionLink")) {

			while (resultSet.next()) {
				long cpDefinitionId2 = resultSet.getLong("CPDefinitionId2");

				preparedStatement.setLong(1, _getCProductId(cpDefinitionId2));
				preparedStatement.setLong(2, cpDefinitionId2);

				preparedStatement.execute();
			}
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns(
				CPDefinitionLinkModelImpl.TABLE_NAME, "CPDefinitionId2")
		};
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CPDefinitionLink", "CProductId LONG"),
			UpgradeProcessFactory.alterColumnName(
				"CPDefinitionLink", "CPDefinitionId1", "CPDefinitionId")
		};
	}

	private long _getCProductId(long cpDefinitionId) throws Exception {
		try (Statement s = connection.createStatement();

			ResultSet resultSet = s.executeQuery(
				"select CProductId from CPDefinition where CPDefinitionId = " +
					cpDefinitionId)) {

			if (resultSet.next()) {
				return resultSet.getLong("CProductId");
			}
		}

		return 0;
	}

}