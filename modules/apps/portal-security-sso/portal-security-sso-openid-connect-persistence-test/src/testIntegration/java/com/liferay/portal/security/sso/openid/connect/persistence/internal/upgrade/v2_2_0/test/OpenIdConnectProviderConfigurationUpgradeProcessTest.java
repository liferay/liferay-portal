/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.internal.upgrade.v2_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Dictionary;

import org.apache.felix.cm.file.ConfigurationHandler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class OpenIdConnectProviderConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule integrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_discoveryEndpointCacheInMillis = RandomTestUtil.randomLong();

		_properties = HashMapDictionaryBuilder.<String, Object>put(
			"authorizationEndPoint", RandomTestUtil.randomString()
		).put(
			"companyId", TestPropsValues.getCompanyId()
		).put(
			"discoveryEndPoint", RandomTestUtil.randomString()
		).put(
			"discoveryEndPointCacheInMillis", _discoveryEndpointCacheInMillis
		).put(
			"openIdConnectClientId", RandomTestUtil.randomString()
		).put(
			"tokenEndPoint", RandomTestUtil.randomString()
		).put(
			"userInfoEndPoint", RandomTestUtil.randomString()
		).build();

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		ConfigurationHandler.write(unsyncByteArrayOutputStream, _properties);

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into Configuration_ (configurationId, dictionary) " +
					"values(?, ?)")) {

			preparedStatement.setString(1, _CONFIGURATION_ID);
			preparedStatement.setString(
				2, unsyncByteArrayOutputStream.toString());

			preparedStatement.execute();
		}

		_oAuthClientEntry1 = _oAuthClientEntryLocalService.addOAuthClientEntry(
			TestPropsValues.getUserId(), StringPool.BLANK,
			"https://accounts.google.com/.well-known/openid-configuration",
			null,
			JSONUtil.put(
				"client_id", _properties.get("openIdConnectClientId")
			).put(
				"client_name", RandomTestUtil.randomString()
			).put(
				"client_secret", RandomTestUtil.randomString()
			).put(
				"redirect_uris", JSONUtil.put("")
			).put(
				"scope", "openid email profile"
			).put(
				"subject_type", "public"
			).toString(),
			0, OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			StringPool.BLANK);
		_oAuthClientEntry2 = _oAuthClientEntryLocalService.addOAuthClientEntry(
			TestPropsValues.getUserId(), StringPool.BLANK,
			"https://accounts.google.com/.well-known/openid-configuration",
			null,
			JSONUtil.put(
				"client_id", RandomTestUtil.randomString()
			).put(
				"client_name", RandomTestUtil.randomString()
			).put(
				"client_secret", RandomTestUtil.randomString()
			).put(
				"redirect_uris", JSONUtil.put("")
			).put(
				"scope", "openid email profile"
			).put(
				"subject_type", "public"
			).toString(),
			OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			StringPool.BLANK);
	}

	@After
	public void tearDown() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"delete from Configuration_ where configurationId ='" +
				_CONFIGURATION_ID + "'");
	}

	@Test
	public void testUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			StringBundler.concat(
				"com.liferay.portal.security.sso.openid.connect.persistence.",
				"internal.upgrade.v2_2_0.",
				"OpenIdConnectProviderConfigurationUpgradeProcess"));

		upgradeProcess.upgrade();

		try (Connection connection = DataAccess.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				StringBundler.concat(
					"select dictionary from Configuration_ where ",
					"configurationId = '", _CONFIGURATION_ID, "'"))) {

			if (resultSet.next()) {
				String dictionaryString = resultSet.getString("dictionary");

				Dictionary<String, Object> dictionary =
					ConfigurationHandler.read(
						new UnsyncByteArrayInputStream(
							dictionaryString.getBytes(StringPool.UTF8)));

				Assert.assertEquals(
					_properties.get("authorizationEndPoint"),
					dictionary.get("authorizationEndpoint"));
				Assert.assertEquals(
					_properties.get("discoveryEndPoint"),
					dictionary.get("discoveryEndpoint"));
				Assert.assertEquals(
					_properties.get("discoveryEndPointCacheInMillis"),
					dictionary.get("discoveryEndpointCacheInMillis"));
				Assert.assertEquals(
					_properties.get("tokenEndPoint"),
					dictionary.get("tokenEndpoint"));
				Assert.assertEquals(
					_properties.get("userInfoEndPoint"),
					dictionary.get("userInfoEndpoint"));
			}
		}

		_oAuthClientEntry1 = _oAuthClientEntryLocalService.getOAuthClientEntry(
			_oAuthClientEntry1.getOAuthClientEntryId());

		Assert.assertEquals(
			_discoveryEndpointCacheInMillis,
			_oAuthClientEntry1.getMetadataCacheTime());

		_oAuthClientEntry2 = _oAuthClientEntryLocalService.getOAuthClientEntry(
			_oAuthClientEntry2.getOAuthClientEntryId());

		Assert.assertEquals(
			OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			_oAuthClientEntry2.getMetadataCacheTime());
	}

	private static final String _CONFIGURATION_ID =
		"com.liferay.portal.security.sso.openid.connect.internal." +
			"configuration.OpenIdConnectProviderConfiguration.scoped~" +
				RandomTestUtil.randomString();

	private long _discoveryEndpointCacheInMillis;
	private OAuthClientEntry _oAuthClientEntry1;
	private OAuthClientEntry _oAuthClientEntry2;

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	private HashMapDictionary<String, Object> _properties;

	@Inject(
		filter = "component.name=com.liferay.portal.security.sso.openid.connect.persistence.internal.upgrade.registry.OpenIdConnectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}