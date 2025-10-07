/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_1_0;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.text.DecimalFormat;

/**
 * @author Ivica Cardic
 */
public class CPConfigurationEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

		decimalFormat.setMinimumFractionDigits(0);

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CPConfigurationEntry set allowedOrderQuantities " +
						"= ? where ctCollectionId = ? and " +
							"CPConfigurationEntryId = ?");
			Statement statement = connection.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery(
				"select distinct ctCollectionId, CPConfigurationEntryId, " +
					"allowedOrderQuantities from CPConfigurationEntry")) {

			while (resultSet.next()) {
				String allowedOrderQuantities = resultSet.getString(
					"allowedOrderQuantities");

				if (Validator.isNotNull(allowedOrderQuantities)) {
					allowedOrderQuantities = StringUtil.replace(
						allowedOrderQuantities, CharPool.COMMA, CharPool.SPACE);

					allowedOrderQuantities = StringUtil.replace(
						allowedOrderQuantities, CharPool.PERIOD,
						CharPool.SPACE);

					String[] allowedOrderQuantitiesParts =
						allowedOrderQuantities.split(StringPool.SPACE);

					StringBundler sb = new StringBundler(
						allowedOrderQuantitiesParts.length * 2);

					for (String allowedOrderQuantitiesPart :
							allowedOrderQuantitiesParts) {

						sb.append(
							decimalFormat.format(
								GetterUtil.getDouble(
									allowedOrderQuantitiesPart)));
						sb.append(StringPool.SPACE);
					}

					allowedOrderQuantities = sb.toString();

					preparedStatement.setString(
						1, allowedOrderQuantities.trim());

					preparedStatement.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement.setLong(
						3, resultSet.getLong("CPConfigurationEntryId"));

					preparedStatement.execute();
				}
			}

			preparedStatement.executeBatch();
		}
	}

}