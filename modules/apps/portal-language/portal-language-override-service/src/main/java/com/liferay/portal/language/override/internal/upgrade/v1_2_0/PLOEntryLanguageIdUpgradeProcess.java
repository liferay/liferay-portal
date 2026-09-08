/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal.upgrade.v1_2_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Thiago Buarque
 */
public class PLOEntryLanguageIdUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Set<String> rowKeys = new HashSet<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select companyId, key_, languageId from PLOEntry");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				rowKeys.add(
					_getRowKey(
						resultSet.getLong("companyId"),
						resultSet.getString("key_"),
						resultSet.getString("languageId")));
			}
		}

		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select ploEntryId, companyId, key_, languageId from " +
						"PLOEntry");
			PreparedStatement deletePreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection, "delete from PLOEntry where ploEntryId = ?");
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update PLOEntry set languageId = ? where ploEntryId = ?");

			ResultSet resultSet = selectPreparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String languageId = resultSet.getString("languageId");

				if (Validator.isNull(languageId)) {
					continue;
				}

				String normalizedLanguageId = LocaleUtil.toLanguageId(
					LocaleUtil.fromLanguageId(languageId, false));

				if (Objects.equals(languageId, normalizedLanguageId)) {
					continue;
				}

				long ploEntryId = resultSet.getLong("ploEntryId");

				if (!rowKeys.add(
						_getRowKey(
							resultSet.getLong("companyId"),
							resultSet.getString("key_"),
							normalizedLanguageId))) {

					deletePreparedStatement.setLong(1, ploEntryId);

					deletePreparedStatement.addBatch();

					continue;
				}

				updatePreparedStatement.setString(1, normalizedLanguageId);
				updatePreparedStatement.setLong(2, ploEntryId);

				updatePreparedStatement.addBatch();
			}

			deletePreparedStatement.executeBatch();

			updatePreparedStatement.executeBatch();
		}
	}

	private String _getRowKey(long companyId, String key, String languageId) {
		return StringBundler.concat(
			companyId, StringPool.POUND, key, StringPool.POUND, languageId);
	}

}