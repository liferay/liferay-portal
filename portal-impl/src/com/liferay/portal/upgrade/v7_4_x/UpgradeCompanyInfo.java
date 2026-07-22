/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author István András Dézsi
 */
public class UpgradeCompanyInfo extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		for (String columnDefinition : _COMPANY_INFO_COLUMN_DEFINITIONS) {
			int index = columnDefinition.indexOf(StringPool.SPACE);

			String columnName = columnDefinition.substring(0, index);

			if (!hasColumn("CompanyInfo", columnName)) {
				alterTableAddColumn(
					"CompanyInfo", columnName,
					columnDefinition.substring(index + 1));
			}
		}

		if (!hasColumn("Company", "homeURL")) {
			return;
		}

		Long companyId = null;

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			companyId = CompanyThreadLocal.getCompanyId();
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select companyId, homeURL, indexNameCurrent, ",
					"indexNameNext, industry, legalId, legalName, legalType, ",
					"logoId, name, sicCode, size_, tickerSymbol, type_ from ",
					"Company",
					(companyId == null) ? StringPool.BLANK :
						" where companyId = ?"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update CompanyInfo set homeURL = ?, indexNameCurrent ",
						"= ?, indexNameNext = ?, industry = ?, legalId = ?, ",
						"legalName = ?, legalType = ?, logoId = ?, name = ?, ",
						"sicCode = ?, size_ = ?, tickerSymbol = ?, type_ = ? ",
						"where companyId = ?"))) {

			if (companyId != null) {
				preparedStatement1.setLong(1, companyId);
			}

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					preparedStatement2.setString(
						1, resultSet.getString("homeURL"));
					preparedStatement2.setString(
						2, resultSet.getString("indexNameCurrent"));
					preparedStatement2.setString(
						3, resultSet.getString("indexNameNext"));
					preparedStatement2.setString(
						4, resultSet.getString("industry"));
					preparedStatement2.setString(
						5, resultSet.getString("legalId"));
					preparedStatement2.setString(
						6, resultSet.getString("legalName"));
					preparedStatement2.setString(
						7, resultSet.getString("legalType"));
					preparedStatement2.setLong(8, resultSet.getLong("logoId"));
					preparedStatement2.setString(
						9, resultSet.getString("name"));
					preparedStatement2.setString(
						10, resultSet.getString("sicCode"));
					preparedStatement2.setString(
						11, resultSet.getString("size_"));
					preparedStatement2.setString(
						12, resultSet.getString("tickerSymbol"));
					preparedStatement2.setString(
						13, resultSet.getString("type_"));
					preparedStatement2.setLong(
						14, resultSet.getLong("companyId"));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns(
				"Company", "homeURL", "indexNameCurrent", "indexNameNext",
				"industry", "legalId", "legalName", "legalType", "logoId",
				"name", "sicCode", "size_", "tickerSymbol", "type_")
		};
	}

	private static final String[] _COMPANY_INFO_COLUMN_DEFINITIONS = {
		"homeURL STRING null", "indexNameCurrent VARCHAR(75) null",
		"indexNameNext VARCHAR(75) null", "industry VARCHAR(75) null",
		"legalId VARCHAR(75) null", "legalName VARCHAR(75) null",
		"legalType VARCHAR(75) null", "logoId LONG", "name VARCHAR(75) null",
		"sicCode VARCHAR(75) null", "size_ VARCHAR(75) null",
		"tickerSymbol VARCHAR(75) null", "type_ VARCHAR(75) null"
	};

}