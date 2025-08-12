/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_1_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Andrea Sbarra
 */
public class CPDefinitionLocalizationUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update CPDefinitionLocalization set CProductId = ? where " +
					"CPDefinitionId = ?");
			Statement s = connection.createStatement();
			ResultSet resultSet = s.executeQuery(
				"select * from CPDefinitionLocalization")) {

			while (resultSet.next()) {
				long cpDefinitionId = resultSet.getLong("CPDefinitionId");

				preparedStatement.setLong(1, _getCProductId(cpDefinitionId));
				preparedStatement.setLong(2, cpDefinitionId);

				preparedStatement.execute();
			}
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CPDefinitionLocalization", "CProductId LONG")
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