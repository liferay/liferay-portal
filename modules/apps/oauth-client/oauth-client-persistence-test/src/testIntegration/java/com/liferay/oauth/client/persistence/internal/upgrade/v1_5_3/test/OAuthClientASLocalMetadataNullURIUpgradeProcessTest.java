/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.v1_5_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class OAuthClientASLocalMetadataNullURIUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		JSONObject brokenMetadataJSONObject = _baseMetadataJSONObject(
		).put(
			"authorization_endpoint", "null"
		).put(
			"registration_endpoint", "null"
		).put(
			"token_endpoint", "null"
		).put(
			"userinfo_endpoint", "null"
		);
		JSONObject brokenOAuthASMetadataJSONObject =
			_baseOAuthASMetadataJSONObject(
			).put(
				"authorization_endpoint", "null"
			).put(
				"jwks_uri", "null"
			).put(
				"registration_endpoint", "null"
			).put(
				"token_endpoint", "null"
			);

		OAuthClientASLocalMetadata brokenOAuthClientASLocalMetadata =
			_persistOAuthClientASLocalMetadata(
				brokenMetadataJSONObject.toString(),
				brokenOAuthASMetadataJSONObject.toString());

		JSONObject cleanMetadataJSONObject = _baseMetadataJSONObject(
		).put(
			"registration_endpoint",
			_ISSUER + "/clients-registrations/openid-connect"
		).put(
			"userinfo_endpoint", _ISSUER + "/protocol/openid-connect/userinfo"
		);
		JSONObject cleanOAuthASMetadataJSONObject =
			_baseOAuthASMetadataJSONObject().put(
				"registration_endpoint",
				_ISSUER + "/clients-registrations/openid-connect");

		OAuthClientASLocalMetadata cleanOAuthClientASLocalMetadata =
			_persistOAuthClientASLocalMetadata(
				cleanMetadataJSONObject.toString(),
				cleanOAuthASMetadataJSONObject.toString());

		String cleanMetadataJSON =
			cleanOAuthClientASLocalMetadata.getMetadataJSON();
		String cleanOAuthASMetadataJSON =
			cleanOAuthClientASLocalMetadata.getOAuthASMetadataJSON();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ALL)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}

		brokenOAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(
					brokenOAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId());

		String upgradedMetadataJSON =
			brokenOAuthClientASLocalMetadata.getMetadataJSON();

		Assert.assertFalse(upgradedMetadataJSON.contains("\":\"null\""));

		OIDCProviderMetadata oidcProviderMetadata = OIDCProviderMetadata.parse(
			upgradedMetadataJSON);

		Assert.assertNull(oidcProviderMetadata.getAuthorizationEndpointURI());
		Assert.assertNull(oidcProviderMetadata.getRegistrationEndpointURI());
		Assert.assertNull(oidcProviderMetadata.getTokenEndpointURI());
		Assert.assertNull(oidcProviderMetadata.getUserInfoEndpointURI());

		String upgradedOAuthASMetadataJSON =
			brokenOAuthClientASLocalMetadata.getOAuthASMetadataJSON();

		Assert.assertFalse(upgradedOAuthASMetadataJSON.contains("\":\"null\""));

		AuthorizationServerMetadata authorizationServerMetadata =
			AuthorizationServerMetadata.parse(upgradedOAuthASMetadataJSON);

		Assert.assertNull(
			authorizationServerMetadata.getAuthorizationEndpointURI());
		Assert.assertNull(authorizationServerMetadata.getJWKSetURI());
		Assert.assertNull(
			authorizationServerMetadata.getRegistrationEndpointURI());
		Assert.assertNull(authorizationServerMetadata.getTokenEndpointURI());

		cleanOAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(
					cleanOAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId());

		Assert.assertEquals(
			cleanMetadataJSON,
			cleanOAuthClientASLocalMetadata.getMetadataJSON());
		Assert.assertEquals(
			cleanOAuthASMetadataJSON,
			cleanOAuthClientASLocalMetadata.getOAuthASMetadataJSON());
	}

	private JSONObject _baseMetadataJSONObject() {
		return JSONUtil.put(
			"authorization_endpoint", _ISSUER + "/protocol/openid-connect/auth"
		).put(
			"issuer", _ISSUER
		).put(
			"jwks_uri", _ISSUER + "/protocol/openid-connect/certs"
		).put(
			"subject_types_supported", JSONUtil.putAll("public")
		).put(
			"token_endpoint", _ISSUER + "/protocol/openid-connect/token"
		);
	}

	private JSONObject _baseOAuthASMetadataJSONObject() {
		return JSONUtil.put(
			"authorization_endpoint", _ISSUER + "/protocol/openid-connect/auth"
		).put(
			"issuer", _ISSUER
		).put(
			"jwks_uri", _ISSUER + "/protocol/openid-connect/certs"
		).put(
			"token_endpoint", _ISSUER + "/protocol/openid-connect/token"
		);
	}

	private OAuthClientASLocalMetadata _persistOAuthClientASLocalMetadata(
			String metadataJSON, String oAuthASMetadataJSON)
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				createOAuthClientASLocalMetadata(
					_counterLocalService.increment());

		oAuthClientASLocalMetadata.setMetadataJSON(metadataJSON);
		oAuthClientASLocalMetadata.setOAuthASMetadataJSON(oAuthASMetadataJSON);

		return _oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	private static final String _CLASS_NAME =
		"com.liferay.oauth.client.persistence.internal.upgrade.v1_5_3." +
			"OAuthClientASLocalMetadataNullURIUpgradeProcess";

	private static final String _ISSUER =
		"https://example.com/auth/realms/master";

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.oauth.client.persistence.internal.upgrade.registry.OAuthClientPersistenceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}