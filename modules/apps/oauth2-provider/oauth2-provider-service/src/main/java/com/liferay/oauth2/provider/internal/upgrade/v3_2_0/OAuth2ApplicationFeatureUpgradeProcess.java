/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.upgrade.v3_2_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Marta Medio
 */
public class OAuth2ApplicationFeatureUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select oAuth2ApplicationId, features from OAuth2Application " +
					"where features IS NOT NULL");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String[] features = StringUtil.split(
					resultSet.getString("features"));

				if (!ArrayUtil.contains(features, "token_introspection")) {
					continue;
				}

				long oAuth2ApplicationId = resultSet.getLong(
					"oAuth2ApplicationId");

				_updateTokenIntrospectionFeature(oAuth2ApplicationId, features);
			}
		}
	}

	private void _updateTokenIntrospectionFeature(
			long oAuth2ApplicationId, String[] features)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update OAuth2Application set features = ? where " +
					"oAuth2ApplicationId = ?")) {

			for (int i = 0; i < features.length; i++) {
				if (!features[i].equals("token_introspection")) {
					continue;
				}

				features[i] = "token.introspection";
			}

			preparedStatement.setString(1, StringUtil.merge(features));

			preparedStatement.setLong(2, oAuth2ApplicationId);

			preparedStatement.execute();
		}
	}

}