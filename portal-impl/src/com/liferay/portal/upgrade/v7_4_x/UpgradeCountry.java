/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Albert Lee
 */
public class UpgradeCountry extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQLFile("update-7.3.0-7.4.0-country.sql", false);

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						"select countryId from Country where uuid_ is null");
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"update Country set uuid_ = ? where countryId = ?");
				ResultSet resultSet = preparedStatement1.executeQuery()) {

				while (resultSet.next()) {
					preparedStatement2.setString(1, PortalUUIDUtil.generate());
					preparedStatement2.setLong(2, resultSet.getLong(1));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}

		long companyId = 0;
		long userId = 0;
		String languageId = LocaleUtil.toLanguageId(LocaleUtil.US);

		String sql = StringBundler.concat(
			"select User_.companyId, User_.userId, User_.languageId from ",
			"User_ join Company on User_.companyId = Company.companyId where ",
			"User_.defaultUser = [$TRUE$] and Company.webId = ",
			StringUtil.quote(PropsValues.COMPANY_DEFAULT_WEB_ID));

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(sql));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				companyId = resultSet.getLong(1);
				userId = resultSet.getLong(2);
				languageId = resultSet.getString(3);
			}
		}

		runSQL(
			"update Country set defaultLanguageId = " +
				StringUtil.quote(languageId) +
					" where defaultLanguageId is null");

		if (companyId > 0) {
			runSQL(
				"update Country set companyId = " + companyId +
					" where (companyId is null or companyId = 0)");
		}

		if (userId > 0) {
			runSQL(
				"update Country set userId = " + userId +
					" where (userId is null or userId = 0)");
		}
	}

}