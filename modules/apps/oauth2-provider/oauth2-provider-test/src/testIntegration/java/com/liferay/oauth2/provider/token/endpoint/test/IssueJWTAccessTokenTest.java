/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.token.endpoint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.PasswordAuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.util.JWTAssertionUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;

import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Arthur Chan
 */
@RunWith(Arquillian.class)
public class IssueJWTAccessTokenTest extends BaseTokenEndpointTestCase {

	@Test
	public void testIssuedJWTAccessToken() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		_verifyJWTAccessToken(
			getAccessToken(
				new PasswordAuthorizationGrant(
					user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
				clientAuthentications.get(TEST_CLIENT_ID_1)));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new IssueJWTAccessTokenTestPreparatorBundleActivator();
	}

	private void _verifyJWTAccessToken(String jwtAccessToken) {
		JwsJwtCompactConsumer jwsJwtCompactConsumer = new JwsJwtCompactConsumer(
			jwtAccessToken);

		JwtToken jwtToken = jwsJwtCompactConsumer.getJwtToken();

		JwsHeaders jwsHeaders = jwtToken.getJwsHeaders();

		JwsSignatureVerifier jwsSignatureVerifier =
			JwsUtils.getSignatureVerifier(
				JwkUtils.readJwkKey(JWTAssertionUtil.JWK));

		Assert.assertTrue(
			jwsSignatureVerifier.verify(
				jwsHeaders, jwsJwtCompactConsumer.getUnsignedEncodedSequence(),
				jwsJwtCompactConsumer.getDecodedSignature()));
	}

	private class IssueJWTAccessTokenTestPreparatorBundleActivator
		extends TestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			autoCloseables.add(
				new ConfigurationTemporarySwapper(
					"com.liferay.oauth2.provider.rest.internal.configuration." +
						"OAuth2AuthorizationServerConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"oauth2.authorization.server.issue.jwt.access.token",
						true
					).put(
						"oauth2.authorization.server.jwt.access.token." +
							"signing.json.web.key",
						JWTAssertionUtil.JWK
					).build()));

			super.prepareTest();
		}

	}

}