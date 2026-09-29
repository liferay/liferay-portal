/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_10_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokensValuesException;
import com.liferay.style.book.internal.util.StyleBookEntryFrontendTokensValuesUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryFrontendTokensValuesUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_doUpgrade("styleBookEntryId", "StyleBookEntry");
		_doUpgrade("styleBookEntryVersionId", "StyleBookEntryVersion");
	}

	private void _doUpgrade(String primaryKeyColumnName, String tableName)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ctCollectionId, ", primaryKeyColumnName,
					", frontendTokensValues, themeId from ", tableName,
					" where frontendTokensValues is not null"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update ", tableName,
						" set frontendTokensValues = ? where ctCollectionId = ",
						"? and ", primaryKeyColumnName, " = ?"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String normalizedFrontendTokensValues = null;

				try {
					normalizedFrontendTokensValues =
						StyleBookEntryFrontendTokensValuesUtil.
							normalizeFrontendTokensValues(
								resultSet.getString("frontendTokensValues"),
								resultSet.getString("themeId"));
				}
				catch (StyleBookEntryFrontendTokensValuesException.
							MustBeValidJSON
								styleBookEntryFrontendTokensValuesException) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to parse frontend tokens values of ",
								tableName, " with primary key ",
								resultSet.getLong(primaryKeyColumnName)),
							styleBookEntryFrontendTokensValuesException);
					}

					continue;
				}

				preparedStatement2.setString(1, normalizedFrontendTokensValues);
				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));
				preparedStatement2.setLong(
					3, resultSet.getLong(primaryKeyColumnName));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookEntryFrontendTokensValuesUpgradeProcess.class);

}