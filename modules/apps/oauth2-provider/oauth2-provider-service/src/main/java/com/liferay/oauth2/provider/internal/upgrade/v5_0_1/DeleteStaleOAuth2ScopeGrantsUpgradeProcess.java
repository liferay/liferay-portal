/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.upgrade.v5_0_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Tejesh Boggavarapu
 */
public class DeleteStaleOAuth2ScopeGrantsUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasTable("ObjectDefinition")) {
			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select oAuth2ScopeGrantId from OAuth2ScopeGrant where ",
					"bundleSymbolicName = 'com.liferay.object.rest.impl' and ",
					"not exists (select 1 from ObjectDefinition where ",
					"ObjectDefinition.companyId = OAuth2ScopeGrant.companyId ",
					"and LOWER(ObjectDefinition.name) = ",
					"OAuth2ScopeGrant.applicationName)"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from OA2Auths_OA2ScopeGrants where " +
						"oAuth2ScopeGrantId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from OAuth2ScopeGrant where oAuth2ScopeGrantId = " +
						"?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long oAuth2ScopeGrantId = resultSet.getLong(
					"oAuth2ScopeGrantId");

				preparedStatement2.setLong(1, oAuth2ScopeGrantId);

				preparedStatement2.addBatch();

				preparedStatement3.setLong(1, oAuth2ScopeGrantId);

				preparedStatement3.addBatch();
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}
	}

}