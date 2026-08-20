/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.spi.bearer.token.provider;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eudaldo Alonso
 */
public class AnalyticsCloudBearerTokenProviderTest {

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

	private long _nowInSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	private static final long _HOUR_IN_SECONDS = 3600;

	private final AnalyticsCloudBearerTokenProvider
		_analyticsCloudBearerTokenProvider =
			new AnalyticsCloudBearerTokenProvider();

}