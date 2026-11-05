/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.internal.web.cache;

import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.webcache.WebCacheItem;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Joao Victor
 */
public class AIHubCellUserTokenWebCacheItemTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_webCachePoolUtilMockedStatic.close();
	}

	@Test
	public void testGet() throws Exception {
		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		_webCachePoolUtilMockedStatic.when(
			() -> WebCachePoolUtil.get(
				argumentCaptor.capture(), Mockito.any(WebCacheItem.class))
		).thenReturn(
			_createAccessToken(System.currentTimeMillis() + Time.HOUR)
		);

		LocalOAuthClient localOAuthClient = Mockito.mock(
			LocalOAuthClient.class);
		long userId = RandomTestUtil.randomLong();

		AIHubCellUserTokenWebCacheItem.get(
			localOAuthClient, _createOAuth2Application(1), userId);
		AIHubCellUserTokenWebCacheItem.get(
			localOAuthClient, _createOAuth2Application(1), userId);
		AIHubCellUserTokenWebCacheItem.get(
			localOAuthClient, _createOAuth2Application(2), userId);

		List<String> keys = argumentCaptor.getAllValues();

		Assert.assertEquals(keys.toString(), 3, keys.size());
		Assert.assertEquals(keys.get(0), keys.get(1));
		Assert.assertNotEquals(keys.get(0), keys.get(2));
	}

	private String _createAccessToken(long expirationTime) throws Exception {
		SignedJWT signedJWT = new SignedJWT(
			new JWSHeader(JWSAlgorithm.HS256),
			new JWTClaimsSet.Builder(
			).expirationTime(
				new Date(expirationTime)
			).build());

		signedJWT.sign(new MACSigner(new byte[32]));

		return signedJWT.serialize();
	}

	private OAuth2Application _createOAuth2Application(
		long oAuth2ApplicationScopeAliasesId) {

		OAuth2Application oAuth2Application = Mockito.mock(
			OAuth2Application.class);

		Mockito.when(
			oAuth2Application.getOAuth2ApplicationScopeAliasesId()
		).thenReturn(
			oAuth2ApplicationScopeAliasesId
		);

		return oAuth2Application;
	}

	private static final MockedStatic<WebCachePoolUtil>
		_webCachePoolUtilMockedStatic = Mockito.mockStatic(
			WebCachePoolUtil.class);

}