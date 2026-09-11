/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.spi.bearer.token.provider;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.osb.faro.web.internal.util.AccessTokenExpiresInUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class AnalyticsCloudBearerTokenProviderTest {

	@After
	public void tearDown() {
		AccessTokenExpiresInUtil.removeExpiresIn();
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(
			_analyticsCloudBearerTokenProvider.isValid(
				_HOUR_IN_SECONDS, _nowInSeconds()));
	}

	@Test
	public void testIsValidWithExpiredToken() {
		Assert.assertFalse(
			_analyticsCloudBearerTokenProvider.isValid(
				_HOUR_IN_SECONDS, _nowInSeconds() - (2 * _HOUR_IN_SECONDS)));
	}

	@Test
	public void testIsValidWithFutureIssuedAt() {
		Assert.assertFalse(
			_analyticsCloudBearerTokenProvider.isValid(
				_HOUR_IN_SECONDS, _nowInSeconds() + _HOUR_IN_SECONDS));
	}

	@Test
	public void testIsValidWithNegativeExpiresIn() {
		Assert.assertFalse(
			_analyticsCloudBearerTokenProvider.isValid(-1, _nowInSeconds()));
	}

	@Test
	public void testOnBeforeCreate() {
		BearerTokenProvider.AccessToken accessToken = _createAccessToken(
			"SOME-OTHER-APPLICATION");

		_analyticsCloudBearerTokenProvider.onBeforeCreate(accessToken);

		Assert.assertEquals(0, accessToken.getExpiresIn());
	}

	@Test
	public void testOnBeforeCreateWithApplicationAIHubCell() {
		BearerTokenProvider.AccessToken accessToken = _createAccessToken(
			"AI-HUB-CELL");

		_analyticsCloudBearerTokenProvider.onBeforeCreate(accessToken);

		Assert.assertEquals(
			_EXPIRATION_AI_HUB_CELL_IN_SECONDS, accessToken.getExpiresIn());
		Assert.assertTrue(
			_analyticsCloudBearerTokenProvider.isValid(accessToken));
	}

	@Test
	public void testOnBeforeCreateWithExpiresIn() {
		AccessTokenExpiresInUtil.setExpiresIn(_HOUR_IN_SECONDS);

		BearerTokenProvider.AccessToken accessToken = _createAccessToken(
			"SOME-OTHER-APPLICATION");

		_analyticsCloudBearerTokenProvider.onBeforeCreate(accessToken);

		Assert.assertEquals(_HOUR_IN_SECONDS, accessToken.getExpiresIn());

		accessToken = _createAccessToken("AI-HUB-CELL");

		_analyticsCloudBearerTokenProvider.onBeforeCreate(accessToken);

		Assert.assertEquals(
			_EXPIRATION_AI_HUB_CELL_IN_SECONDS, accessToken.getExpiresIn());
	}

	private BearerTokenProvider.AccessToken _createAccessToken(
		String externalReferenceCode) {

		OAuth2Application oAuth2Application = Mockito.mock(
			OAuth2Application.class);

		Mockito.when(
			oAuth2Application.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		return new BearerTokenProvider.AccessToken(
			oAuth2Application, null, null, 0, null, null, null, _nowInSeconds(),
			null, null, null, null, null, null, null, null, 0, null);
	}

	private long _nowInSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	private static final long _EXPIRATION_AI_HUB_CELL_IN_SECONDS = 2592000L;
	private static final long _HOUR_IN_SECONDS = 3600L;

	private final AnalyticsCloudBearerTokenProvider
		_analyticsCloudBearerTokenProvider =
			new AnalyticsCloudBearerTokenProvider();

}