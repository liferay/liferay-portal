/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.util;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;

import java.util.Objects;

import net.minidev.json.JSONObject;

/**
 * @author Caio Farias
 */
public class OpenIdConnectClientInformationUtil {

	public static OIDCClientInformation getOIDCClientInformation(
			OAuthClientEntry oAuthClientEntry)
		throws Exception {

		JSONObject jsonObject = JSONObjectUtils.parse(
			oAuthClientEntry.getInfoJSON());

		Object clientSecret = jsonObject.get("client_secret");

		if ((clientSecret instanceof String clientSecretString) &&
			_isResolvable(clientSecretString, oAuthClientEntry)) {

			jsonObject.put(
				"client_secret",
				SecretResolverUtil.resolve(
					oAuthClientEntry.getCompanyId(), clientSecretString));
		}

		return OIDCClientInformation.parse(jsonObject);
	}

	private static boolean _isResolvable(
			String clientSecret, OAuthClientEntry oAuthClientEntry)
		throws Exception {

		KeyReference keyReference = KeyReferenceUtil.parseKeyReference(
			clientSecret);

		if (keyReference == null) {
			return false;
		}

		if (oAuthClientEntry.getUserId() == UserLocalServiceUtil.getGuestUserId(
				oAuthClientEntry.getCompanyId())) {

			return true;
		}

		return Objects.equals(
			keyReference.getIdentifier(),
			StringBundler.concat(
				OAuthClientEntry.class.getSimpleName(), StringPool.SLASH,
				oAuthClientEntry.getCompanyId(), StringPool.SLASH,
				oAuthClientEntry.getOAuthClientEntryId()));
	}

}