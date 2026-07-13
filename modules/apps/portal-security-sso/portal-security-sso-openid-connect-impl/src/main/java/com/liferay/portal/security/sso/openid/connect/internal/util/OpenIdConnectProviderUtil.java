/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.util;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.net.URI;

import java.security.MessageDigest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Renan Vasconcelos
 */
public class OpenIdConnectProviderUtil {

	public static String generateLocalWellKnownURI(
			String issuer, String tokenEndpoint)
		throws Exception {

		URI issuerURI = URI.create(issuer);
		MessageDigest messageDigest = MessageDigest.getInstance(
			PropsValues.FIPS_ENABLED ? DigesterUtil.SHA_256 : DigesterUtil.MD5);

		return StringBundler.concat(
			issuerURI.getScheme(), "://", issuerURI.getAuthority(),
			"/.well-known/openid-configuration", issuerURI.getPath(), '/',
			Base64.encodeToURL(messageDigest.digest(tokenEndpoint.getBytes())),
			"/local");
	}

	public static long getOAuthClientEntryId(
		long companyId, String providerName,
		OAuthClientEntryLocalService oAuthClientEntryLocalService) {

		Map<String, Long> oAuthClientEntryIds = _getOAuthClientEntryIds(
			companyId, oAuthClientEntryLocalService);

		if (oAuthClientEntryIds.isEmpty()) {
			oAuthClientEntryIds = _getOAuthClientEntryIds(
				CompanyConstants.SYSTEM, oAuthClientEntryLocalService);
		}

		if (oAuthClientEntryIds.isEmpty()) {
			return 0;
		}

		Long oAuthClientEntryId = oAuthClientEntryIds.get(providerName);

		if (oAuthClientEntryId == null) {
			return 0;
		}

		return oAuthClientEntryId;
	}

	public static Map<String, Long> removeOAuthClientEntryIdsByCompanyId(
		long companyId) {

		return _oAuthClientEntryIds.remove(companyId);
	}

	private static Map<String, Long> _getOAuthClientEntryIds(
		long companyId,
		OAuthClientEntryLocalService oAuthClientEntryLocalService) {

		return _oAuthClientEntryIds.computeIfAbsent(
			companyId,
			key -> {
				Map<String, Long> oAuthClientEntryIds = new HashMap<>();

				for (OAuthClientEntry oAuthClientEntry :
						oAuthClientEntryLocalService.
							getCompanyOAuthClientEntries(companyId)) {

					try {
						JSONObject jsonObject =
							JSONFactoryUtil.createJSONObject(
								oAuthClientEntry.getInfoJSON());

						String clientName = jsonObject.getString(
							"client_name", null);

						if (clientName != null) {
							clientName = clientName.substring(
								_CLIENT_TO.length());
						}

						oAuthClientEntryIds.put(
							clientName,
							oAuthClientEntry.getOAuthClientEntryId());
					}
					catch (JSONException jsonException) {
						throw new RuntimeException(jsonException);
					}
				}

				return oAuthClientEntryIds;
			});
	}

	private static final String _CLIENT_TO = "Client to ";

	private static final Map<Long, Map<String, Long>> _oAuthClientEntryIds =
		new ConcurrentHashMap<>();

}