/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.upgrade.v2_0_0;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Leslie Wong
 */
public class OAuth2ApplicationAnalyticsCloudUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select oAuth2ApplicationId, redirectURIs from " +
					"OAuth2Application where externalReferenceCode = ?");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update OAuth2Application set homePageURL = ?, " +
						"redirectURIs = ? where oAuth2ApplicationId = ?")) {

			preparedStatement1.setString(1, "ANALYTICS-CLOUD");

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					List<String> redirectURIs = ListUtil.fromArray(
						StringUtil.split(
							resultSet.getString("redirectURIs"),
							CharPool.NEW_LINE));

					if (!redirectURIs.contains(_REDIRECT_URI)) {
						redirectURIs.add(_REDIRECT_URI);
					}

					preparedStatement2.setString(1, "https://ldp.liferay.com");
					preparedStatement2.setString(
						2, StringUtil.merge(redirectURIs, StringPool.NEW_LINE));
					preparedStatement2.setLong(
						3, resultSet.getLong("oAuth2ApplicationId"));

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final String _REDIRECT_URI =
		"https://ldp.liferay.com/oauth/receive";

}