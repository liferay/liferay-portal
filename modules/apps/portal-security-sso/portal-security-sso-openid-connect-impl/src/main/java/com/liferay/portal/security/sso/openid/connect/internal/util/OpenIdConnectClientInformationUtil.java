/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.util;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.security.sso.openid.connect.internal.configuration.OpenIdConnectProviderConfiguration;

import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;

import java.util.Dictionary;
import java.util.Objects;

import net.minidev.json.JSONObject;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

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

	private static boolean _isOpenIdConnectProviderClientSecret(
			long companyId, String keyReferenceString)
		throws Exception {

		ConfigurationAdmin configurationAdmin =
			_configurationAdminSnapshot.get();

		Configuration[] configurations = configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(|(service.factoryPid=",
				OpenIdConnectProviderConfiguration.class.getName(),
				")(service.factoryPid=",
				OpenIdConnectProviderConfiguration.class.getName(),
				".scoped))"));

		if (configurations == null) {
			return false;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			long configurationCompanyId = GetterUtil.getLong(
				properties.get("companyId"));

			if (((configurationCompanyId == CompanyConstants.SYSTEM) ||
				 (configurationCompanyId == companyId)) &&
				Objects.equals(
					keyReferenceString,
					properties.get("openIdConnectClientSecret"))) {

				return true;
			}
		}

		return false;
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

			return _isOpenIdConnectProviderClientSecret(
				oAuthClientEntry.getCompanyId(), clientSecret);
		}

		return Objects.equals(
			keyReference.getIdentifier(),
			StringBundler.concat(
				OAuthClientEntry.class.getSimpleName(), StringPool.SLASH,
				oAuthClientEntry.getCompanyId(), StringPool.SLASH,
				oAuthClientEntry.getOAuthClientEntryId()));
	}

	private static final Snapshot<ConfigurationAdmin>
		_configurationAdminSnapshot = new Snapshot<>(
			OpenIdConnectClientInformationUtil.class, ConfigurationAdmin.class);

}