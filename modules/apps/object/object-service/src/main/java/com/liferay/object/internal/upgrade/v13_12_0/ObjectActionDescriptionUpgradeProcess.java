/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_12_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Nathaly Gomes
 */
public class ObjectActionDescriptionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select objectActionId, companyId, description from " +
					"ObjectAction");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update ObjectAction set description = ? where " +
						"objectActionId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String description = resultSet.getString("description");

				if (Validator.isNull(description) ||
					description.startsWith("<?xml")) {

					continue;
				}

				String defaultLanguageId =
					UpgradeProcessUtil.getDefaultLanguageId(
						resultSet.getLong("companyId"));

				preparedStatement2.setString(
					1,
					LocalizationUtil.updateLocalization(
						HashMapBuilder.put(
							LocaleUtil.fromLanguageId(defaultLanguageId),
							description
						).build(),
						StringPool.BLANK, "Description", defaultLanguageId));

				preparedStatement2.setLong(
					2, resultSet.getLong("objectActionId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

}