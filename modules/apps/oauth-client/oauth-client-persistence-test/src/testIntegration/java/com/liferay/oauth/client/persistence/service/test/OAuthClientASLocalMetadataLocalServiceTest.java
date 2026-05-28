/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class OAuthClientASLocalMetadataLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		if (_oAuthClientASLocalMetadata != null) {
			_oAuthClientASLocalMetadataLocalService.
				deleteOAuthClientASLocalMetadata(_oAuthClientASLocalMetadata);
		}
	}

	@Test
	public void testAddOAuthClientASLocalMetadata() throws Exception {
		_testAddOAuthClientASLocalMetadata(null);
		_testAddOAuthClientASLocalMetadata("authorization_endpoint");
		_testAddOAuthClientASLocalMetadata("registration_endpoint");
		_testAddOAuthClientASLocalMetadata("token_endpoint");
		_testAddOAuthClientASLocalMetadata("userinfo_endpoint");
	}

	@Test
	public void testUpdateOAuthClientASLocalMetadataWhenUserInfoEndpointIsMissing()
		throws Exception {

		_oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				addOAuthClientASLocalMetadata(
					TestPropsValues.getUserId(),
					_buildFullMetadataJSONObject().toString(),
					"openid-configuration");

		String updatedIssuer = _ISSUER + "/updated";

		_oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				updateOAuthClientASLocalMetadata(
					_oAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId(),
					JSONUtil.put(
						"authorization_endpoint",
						updatedIssuer + "/protocol/openid-connect/auth"
					).put(
						"issuer", updatedIssuer
					).put(
						"jwks_uri",
						updatedIssuer + "/protocol/openid-connect/certs"
					).put(
						"subject_types_supported", JSONUtil.putAll("public")
					).put(
						"token_endpoint",
						updatedIssuer + "/protocol/openid-connect/token"
					).toString(),
					"openid-configuration");

		String storedMetadataJSON =
			_oAuthClientASLocalMetadata.getMetadataJSON();

		Assert.assertFalse(
			storedMetadataJSON.contains("\"userinfo_endpoint\":\"null\""));

		OIDCProviderMetadata oidcProviderMetadata = OIDCProviderMetadata.parse(
			storedMetadataJSON);

		Assert.assertNull(oidcProviderMetadata.getUserInfoEndpointURI());
	}

	private JSONObject _buildFullMetadataJSONObject() {
		return JSONUtil.put(
			"authorization_endpoint", _ISSUER + "/protocol/openid-connect/auth"
		).put(
			"issuer", _ISSUER
		).put(
			"jwks_uri", _ISSUER + "/protocol/openid-connect/certs"
		).put(
			"registration_endpoint",
			_ISSUER + "/clients-registrations/openid-connect"
		).put(
			"subject_types_supported", JSONUtil.putAll("public")
		).put(
			"token_endpoint", _ISSUER + "/protocol/openid-connect/token"
		).put(
			"userinfo_endpoint", _ISSUER + "/protocol/openid-connect/userinfo"
		);
	}

	private void _testAddOAuthClientASLocalMetadata(String missingKey)
		throws Exception {

		JSONObject metadataJSONObject = _buildFullMetadataJSONObject();

		if (missingKey != null) {
			metadataJSONObject.remove(missingKey);
		}

		_oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				addOAuthClientASLocalMetadata(
					TestPropsValues.getUserId(), metadataJSONObject.toString(),
					"openid-configuration");

		String storedMetadataJSON =
			_oAuthClientASLocalMetadata.getMetadataJSON();

		if (missingKey == null) {
			OIDCProviderMetadata oidcProviderMetadata =
				OIDCProviderMetadata.parse(storedMetadataJSON);

			Assert.assertEquals(
				_ISSUER + "/protocol/openid-connect/token",
				String.valueOf(oidcProviderMetadata.getTokenEndpointURI()));
			Assert.assertEquals(
				_ISSUER + "/protocol/openid-connect/userinfo",
				String.valueOf(oidcProviderMetadata.getUserInfoEndpointURI()));
		}
		else {
			String literalNullEntry = "\"" + missingKey + "\":\"null\"";

			Assert.assertFalse(storedMetadataJSON.contains(literalNullEntry));

			String oAuthASMetadataJSON =
				_oAuthClientASLocalMetadata.getOAuthASMetadataJSON();

			Assert.assertFalse(oAuthASMetadataJSON.contains(literalNullEntry));
		}

		_oAuthClientASLocalMetadataLocalService.
			deleteOAuthClientASLocalMetadata(_oAuthClientASLocalMetadata);

		_oAuthClientASLocalMetadata = null;
	}

	private static final String _ISSUER =
		"https://example.com/auth/realms/master";

	private OAuthClientASLocalMetadata _oAuthClientASLocalMetadata;

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

}