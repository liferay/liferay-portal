/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.internal.upgrade.v2_2_0;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.model.OAuthClientEntryTable;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.cm.file.ConfigurationHandler;

/**
 * @author Christian Moura
 */
public class OpenIdConnectProviderConfigurationUpgradeProcess
	extends UpgradeProcess {

	public OpenIdConnectProviderConfigurationUpgradeProcess(
		OAuthClientEntryLocalService oAuthClientEntryLocalService) {

		_oAuthClientEntryLocalService = oAuthClientEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasTable("Configuration_")) {
			return;
		}

		try (Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				StringBundler.concat(
					"select * from Configuration_ where configurationId LIKE ",
					"'%com.liferay.portal.security.sso.openid.connect.",
					"internal.configuration.",
					"OpenIdConnectProviderConfiguration%'"));
			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update Configuration_ set dictionary = ? where " +
						"configurationId = ?")) {

			while (resultSet.next()) {
				String dictionaryString = resultSet.getString("dictionary");

				if (Validator.isNull(dictionaryString)) {
					return;
				}

				Dictionary<String, Object> dictionary =
					ConfigurationHandler.read(
						new UnsyncByteArrayInputStream(
							dictionaryString.getBytes(StringPool.UTF8)));

				dictionary = HashMapDictionaryBuilder.putAll(
					dictionary
				).put(
					"authorizationEndpoint",
					GetterUtil.getString(
						dictionary.get("authorizationEndPoint"))
				).put(
					"discoveryEndpoint",
					GetterUtil.getString(dictionary.get("discoveryEndPoint"))
				).put(
					"discoveryEndpointCacheInMillis",
					GetterUtil.getLong(
						dictionary.get("discoveryEndPointCacheInMillis"))
				).put(
					"tokenEndpoint",
					GetterUtil.getString(dictionary.get("tokenEndPoint"))
				).put(
					"userInfoEndpoint",
					GetterUtil.getString(dictionary.get("userInfoEndPoint"))
				).remove(
					"authorizationEndPoint"
				).remove(
					"discoveryEndPoint"
				).remove(
					"discoveryEndPointCacheInMillis"
				).remove(
					"tokenEndPoint"
				).remove(
					"userInfoEndPoint"
				).build();

				UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
					new UnsyncByteArrayOutputStream();

				ConfigurationHandler.write(
					unsyncByteArrayOutputStream, dictionary);

				preparedStatement.setString(
					1, unsyncByteArrayOutputStream.toString());

				preparedStatement.setString(
					2, resultSet.getString("configurationId"));

				preparedStatement.addBatch();

				_updateOAuthClientEntry(
					GetterUtil.getLong(dictionary.get("companyId")),
					GetterUtil.getLong(
						dictionary.get("discoveryEndpointCacheInMillis")),
					GetterUtil.getString(
						dictionary.get("openIdConnectClientId")));
			}

			preparedStatement.executeBatch();
		}
	}

	private void _updateOAuthClientEntry(
		long companyId, long discoveryEndpointCacheInMillis,
		String openIdConnectClientId) {

		List<OAuthClientEntry> oAuthClientEntries =
			_oAuthClientEntryLocalService.dslQuery(
				DSLQueryFactoryUtil.selectDistinct(
					OAuthClientEntryTable.INSTANCE
				).from(
					OAuthClientEntryTable.INSTANCE
				).where(
					Predicate.and(
						OAuthClientEntryTable.INSTANCE.clientId.eq(
							openIdConnectClientId),
						OAuthClientEntryTable.INSTANCE.companyId.eq(companyId))
				));

		if (oAuthClientEntries.isEmpty()) {
			return;
		}

		OAuthClientEntry oAuthClientEntry = oAuthClientEntries.get(0);

		oAuthClientEntry.setMetadataCacheTime(discoveryEndpointCacheInMillis);

		_oAuthClientEntryLocalService.updateOAuthClientEntry(oAuthClientEntry);
	}

	private final OAuthClientEntryLocalService _oAuthClientEntryLocalService;

}