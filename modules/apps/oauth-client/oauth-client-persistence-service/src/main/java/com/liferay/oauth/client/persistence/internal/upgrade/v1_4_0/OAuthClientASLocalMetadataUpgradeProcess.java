/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.v1_4_0;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.Validator;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Alvaro Saugar
 */
public class OAuthClientASLocalMetadataUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			"update OAuthClientASLocalMetadata set localWellKnownEnabled = " +
				"[$FALSE$]");

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update OAuthClientASLocalMetadata set issuer = ? where " +
						"oAuthClientASLocalMetadataId = ?");
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				"select oAuthClientASLocalMetadataId, metadataJSON from " +
					"OAuthClientASLocalMetadata")) {

			while (resultSet.next()) {
				long oAuthClientASLocalMetadataId = resultSet.getLong(
					"oAuthClientASLocalMetadataId");

				String metadataJSON = resultSet.getString("metadataJSON");

				if (Validator.isNull(metadataJSON)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to update issuer for OAuth 2 client " +
								"authorization server local metadata " +
									oAuthClientASLocalMetadataId);
					}

					continue;
				}

				OIDCProviderMetadata oidcProviderMetadata =
					OIDCProviderMetadata.parse(metadataJSON);

				if ((oidcProviderMetadata == null) ||
					(oidcProviderMetadata.getIssuer() == null)) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to update issuer for OAuth 2 client " +
								"authorization server local metadata " +
									oAuthClientASLocalMetadataId);
					}

					continue;
				}

				preparedStatement.setString(
					1, String.valueOf(oidcProviderMetadata.getIssuer()));
				preparedStatement.setLong(2, oAuthClientASLocalMetadataId);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"OAuthClientASLocalMetadata", "issuer VARCHAR(75) null",
				"localWellKnownEnabled BOOLEAN",
				"oAuthASLocalWellKnownURI VARCHAR(256) null",
				"oAuthASMetadataJSON TEXT null")
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientASLocalMetadataUpgradeProcess.class);

}