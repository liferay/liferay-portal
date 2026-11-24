/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.spi.bearer.token.provider;

import com.liferay.oauth2.provider.rest.internal.spi.bearer.token.provider.configuration.DefaultBearerTokenProviderConfiguration;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.petra.io.BigEndianCodec;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.rest.internal.spi.bearer.token.provider.configuration.DefaultBearerTokenProviderConfiguration",
	property = "name=default", service = BearerTokenProvider.class
)
public class DefaultBearerTokenProvider implements BearerTokenProvider {

	@Override
	public boolean isValid(AccessToken accessToken) {
		return isValid(accessToken.getExpiresIn(), accessToken.getIssuedAt());
	}

	@Override
	public boolean isValid(RefreshToken refreshToken) {
		return isValid(refreshToken.getExpiresIn(), refreshToken.getIssuedAt());
	}

	@Override
	public void onBeforeCreate(AccessToken accessToken) {
		accessToken.setExpiresIn(
			_defaultBearerTokenProviderConfiguration.accessTokenExpiresIn());
		accessToken.setTokenKey(
			generateTokenKey(
				_defaultBearerTokenProviderConfiguration.
					accessTokenKeyByteSize()));
	}

	@Override
	public void onBeforeCreate(RefreshToken refreshToken) {
		refreshToken.setExpiresIn(
			_defaultBearerTokenProviderConfiguration.refreshTokenExpiresIn());
		refreshToken.setTokenKey(
			generateTokenKey(
				_defaultBearerTokenProviderConfiguration.
					refreshTokenKeyByteSize()));
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_defaultBearerTokenProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				DefaultBearerTokenProviderConfiguration.class, properties);
	}

	protected String generateTokenKey(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Token key size is less than 0");
		}

		int count = (int)Math.ceil((double)size / 8);

		byte[] buffer = new byte[count * 8];

		for (int i = 0; i < count; i++) {
			BigEndianCodec.putLong(buffer, i * 8, SecureRandomUtil.nextLong());
		}

		return StringUtil.bytesToHexString(Arrays.copyOf(buffer, size));
	}

	protected boolean isValid(long expiresIn, long issuedAt) {
		long expiresInMillis = expiresIn * 1000;

		if (expiresInMillis < 0) {
			return false;
		}

		long issuedAtMillis = issuedAt * 1000;

		if ((issuedAtMillis > System.currentTimeMillis()) ||
			((issuedAtMillis + expiresInMillis) < System.currentTimeMillis())) {

			return false;
		}

		return true;
	}

	private DefaultBearerTokenProviderConfiguration
		_defaultBearerTokenProviderConfiguration;

}