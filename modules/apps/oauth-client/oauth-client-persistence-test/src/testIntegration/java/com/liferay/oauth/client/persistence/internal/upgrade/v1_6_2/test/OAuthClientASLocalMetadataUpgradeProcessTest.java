/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.v1_6_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge García Jiménez
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class OAuthClientASLocalMetadataUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select oAuthClientASLocalMetadataId, " +
					"oAuthASLocalWellKnownURI, oAuthASMetadataJSON from " +
						"OAuthClientASLocalMetadata");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				_oAuthClientASLocalMetadataColumnValues.put(
					resultSet.getLong("oAuthClientASLocalMetadataId"),
					new ObjectValuePair<>(
						resultSet.getString("oAuthASLocalWellKnownURI"),
						resultSet.getString("oAuthASMetadataJSON")));
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update OAuthClientASLocalMetadata set " +
						"oAuthASLocalWellKnownURI = ?, oAuthASMetadataJSON = " +
							"? where oAuthClientASLocalMetadataId = ?")) {

			for (Map.Entry<Long, ObjectValuePair<String, String>> entry :
					_oAuthClientASLocalMetadataColumnValues.entrySet()) {

				ObjectValuePair<String, String> objectValuePair =
					entry.getValue();

				preparedStatement.setString(1, objectValuePair.getKey());
				preparedStatement.setString(2, objectValuePair.getValue());

				preparedStatement.setLong(3, entry.getKey());

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}

		_oAuthClientASLocalMetadataColumnValues.clear();

		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				_oAuthClientASLocalMetadatas) {

			_oAuthClientASLocalMetadataLocalService.
				deleteOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
		}

		_oAuthClientASLocalMetadatas.clear();
	}

	@Test
	public void testUpgrade() throws Exception {
		String path = _createPath();

		String issuer = _ISSUER + path;

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_addOAuthClientASLocalMetadata(issuer, _createMetadataJSON(issuer));

		_runUpgrade();

		oAuthClientASLocalMetadata = _getOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadata);

		Assert.assertEquals(
			_ISSUER + _OAUTH_AS_LOCAL_WELL_KNOWN_PATH + path,
			oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI());

		AuthorizationServerMetadata authorizationServerMetadata =
			AuthorizationServerMetadata.parse(
				oAuthClientASLocalMetadata.getOAuthASMetadataJSON());

		Assert.assertEquals(
			issuer, String.valueOf(authorizationServerMetadata.getIssuer()));

		Assert.assertEquals(
			issuer + "/protocol/openid-connect/auth",
			String.valueOf(
				authorizationServerMetadata.getAuthorizationEndpointURI()));
		Assert.assertEquals(
			issuer + "/protocol/openid-connect/certs",
			String.valueOf(authorizationServerMetadata.getJWKSetURI()));
		Assert.assertEquals(
			issuer + "/protocol/openid-connect/introspect",
			String.valueOf(
				authorizationServerMetadata.getIntrospectionEndpointURI()));
		Assert.assertEquals(
			issuer + "/protocol/openid-connect/token",
			String.valueOf(authorizationServerMetadata.getTokenEndpointURI()));
	}

	@Test
	public void testUpgradeInvalidMetadataJSON() throws Exception {
		_testUpgradeInvalidMetadataJSON(RandomTestUtil.randomString());
		_testUpgradeInvalidMetadataJSON(null);
	}

	@Test
	public void testUpgradeOAuthASLocalWellKnownURI() throws Exception {
		_testUpgradeOAuthASLocalWellKnownURI(
			RandomTestUtil.randomString(), _createPath());
		_testUpgradeOAuthASLocalWellKnownURI(
			null,
			StringBundler.concat(
				StringPool.SLASH, RandomTestUtil.randomString(), "%20a/",
				RandomTestUtil.randomString()));
	}

	@Test
	public void testUpgradeSameAuthorityIssuers() throws Exception {
		String path1 = _createPath();

		String issuer1 = _ISSUER + path1;

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata1 =
			_addOAuthClientASLocalMetadata(
				issuer1, _createMetadataJSON(issuer1));

		String path2 = _createPath();

		String issuer2 = _ISSUER + path2;

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata2 =
			_addOAuthClientASLocalMetadata(
				issuer2, _createMetadataJSON(issuer2));

		_runUpgrade();

		oAuthClientASLocalMetadata1 = _getOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadata1);

		Assert.assertEquals(
			_ISSUER + _OAUTH_AS_LOCAL_WELL_KNOWN_PATH + path1,
			oAuthClientASLocalMetadata1.getOAuthASLocalWellKnownURI());

		oAuthClientASLocalMetadata2 = _getOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadata2);

		Assert.assertEquals(
			_ISSUER + _OAUTH_AS_LOCAL_WELL_KNOWN_PATH + path2,
			oAuthClientASLocalMetadata2.getOAuthASLocalWellKnownURI());
	}

	@Test
	public void testUpgradeUnusableIssuer() throws Exception {
		_testUpgradeUnusableIssuer(
			StringPool.SLASH + RandomTestUtil.randomString(),
			"its issuer has no scheme or authority");
		_testUpgradeUnusableIssuer(
			"https://" + RandomTestUtil.randomString(220) + ".com",
			"the generated URI is longer than 256 characters");
		_testUpgradeUnusableIssuer(null, "its issuer is null");
	}

	private OAuthClientASLocalMetadata _addOAuthClientASLocalMetadata(
			String issuer, String metadataJSON)
		throws Exception {

		return _addOAuthClientASLocalMetadata(issuer, metadataJSON, null, null);
	}

	private OAuthClientASLocalMetadata _addOAuthClientASLocalMetadata(
			String issuer, String metadataJSON, String oAuthASLocalWellKnownURI,
			String oAuthASMetadataJSON)
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				createOAuthClientASLocalMetadata(
					_counterLocalService.increment());

		oAuthClientASLocalMetadata.setCompanyId(TestPropsValues.getCompanyId());
		oAuthClientASLocalMetadata.setIssuer(issuer);
		oAuthClientASLocalMetadata.setMetadataJSON(metadataJSON);
		oAuthClientASLocalMetadata.setOAuthASLocalWellKnownURI(
			oAuthASLocalWellKnownURI);
		oAuthClientASLocalMetadata.setOAuthASMetadataJSON(oAuthASMetadataJSON);

		oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				updateOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);

		_oAuthClientASLocalMetadatas.add(oAuthClientASLocalMetadata);

		return oAuthClientASLocalMetadata;
	}

	private String _createMetadataJSON(String issuer) {
		return JSONUtil.put(
			"authorization_endpoint", issuer + "/protocol/openid-connect/auth"
		).put(
			"issuer", issuer
		).put(
			"jwks_uri", issuer + "/protocol/openid-connect/certs"
		).put(
			"subject_types_supported", JSONUtil.putAll("public")
		).put(
			"token_endpoint", issuer + "/protocol/openid-connect/token"
		).toString();
	}

	private String _createPath() {
		return StringBundler.concat(
			StringPool.SLASH, RandomTestUtil.randomString(), StringPool.SLASH,
			RandomTestUtil.randomString());
	}

	private String _getOAuthASMetadataJSON(long oAuthClientASLocalMetadataId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select oAuthASMetadataJSON from OAuthClientASLocalMetadata " +
					"where oAuthClientASLocalMetadataId = ?")) {

			preparedStatement.setLong(1, oAuthClientASLocalMetadataId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				return resultSet.getString("oAuthASMetadataJSON");
			}
		}
	}

	private OAuthClientASLocalMetadata _getOAuthClientASLocalMetadata(
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws Exception {

		return _oAuthClientASLocalMetadataLocalService.
			getOAuthClientASLocalMetadata(
				oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId());
	}

	private List<String> _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();

			return logCapture.getMessages();
		}
	}

	private void _testUpgradeInvalidMetadataJSON(String oAuthASMetadataJSON)
		throws Exception {

		String path = _createPath();

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_addOAuthClientASLocalMetadata(
				_ISSUER + path, RandomTestUtil.randomString(), null,
				oAuthASMetadataJSON);

		long oAuthClientASLocalMetadataId =
			oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId();

		if (oAuthASMetadataJSON == null) {
			_unsetOAuthASMetadataJSON(oAuthClientASLocalMetadataId);
		}

		List<String> messages = _runUpgrade();

		oAuthClientASLocalMetadata = _getOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadata);

		Assert.assertEquals(
			_ISSUER + _OAUTH_AS_LOCAL_WELL_KNOWN_PATH + path,
			oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI());

		Assert.assertEquals(
			GetterUtil.getString(oAuthASMetadataJSON),
			_getOAuthASMetadataJSON(oAuthClientASLocalMetadataId));

		String message = StringBundler.concat(
			"Unable to generate OAuth 2 authorization server metadata for ",
			"OAuth 2 client authorization server local metadata ",
			oAuthClientASLocalMetadataId);

		Assert.assertTrue(messages.toString(), messages.contains(message));
	}

	private void _testUpgradeOAuthASLocalWellKnownURI(
			String oAuthASLocalWellKnownURI, String path)
		throws Exception {

		String issuer = _ISSUER + path;

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_addOAuthClientASLocalMetadata(
				issuer, _createMetadataJSON(issuer), oAuthASLocalWellKnownURI,
				null);

		_runUpgrade();

		oAuthClientASLocalMetadata = _getOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadata);

		Assert.assertEquals(
			_ISSUER + _OAUTH_AS_LOCAL_WELL_KNOWN_PATH + path,
			oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI());
	}

	private void _testUpgradeUnusableIssuer(String issuer, String reason)
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_addOAuthClientASLocalMetadata(issuer, null);

		List<String> messages = _runUpgrade();

		oAuthClientASLocalMetadata = _getOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadata);

		long oAuthClientASLocalMetadataId =
			oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId();

		Assert.assertEquals(
			_OAUTH_AS_LOCAL_WELL_KNOWN_PATH + StringPool.SLASH +
				oAuthClientASLocalMetadataId,
			oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI());

		String message = StringBundler.concat(
			"Unable to generate an OAuth 2 authorization server local well ",
			"known URI for OAuth 2 client authorization server local metadata ",
			oAuthClientASLocalMetadataId, " because ", reason,
			", so a placeholder is stored instead");

		Assert.assertTrue(messages.toString(), messages.contains(message));
	}

	private void _unsetOAuthASMetadataJSON(long oAuthClientASLocalMetadataId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update OAuthClientASLocalMetadata set oAuthASMetadataJSON = " +
					"null where oAuthClientASLocalMetadataId = ?")) {

			preparedStatement.setLong(1, oAuthClientASLocalMetadataId);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.oauth.client.persistence.internal.upgrade.v1_6_2." +
			"OAuthClientASLocalMetadataUpgradeProcess";

	private static final String _ISSUER =
		"https://" + RandomTestUtil.randomString() + ".com";

	private static final String _OAUTH_AS_LOCAL_WELL_KNOWN_PATH =
		"/.well-known/oauth-authorization-server";

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	private final Map<Long, ObjectValuePair<String, String>>
		_oAuthClientASLocalMetadataColumnValues = new HashMap<>();

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	private final List<OAuthClientASLocalMetadata>
		_oAuthClientASLocalMetadatas = new ArrayList<>();

	@Inject(
		filter = "(&(component.name=com.liferay.oauth.client.persistence.internal.upgrade.registry.OAuthClientPersistenceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}