/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.instance.lifecycle;

import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.EveryNodeEveryStartup;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.internal.util.OpenIdConnectProviderUtil;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	enabled = false, property = "service.ranking:Integer=100",
	service = PortalInstanceLifecycleListener.class
)
public class OAuthClientASLocalMetadataPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener
	implements EveryNodeEveryStartup {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!_clusterMasterExecutor.isMaster()) {
			return;
		}

		_updateLocalWellKnownURIs(company.getCompanyId());

		_checkOrphanedOAuthClientEntries(company.getCompanyId());
	}

	private void _checkOrphanedOAuthClientEntries(long companyId) {
		Set<String> localWellKnownURIs = new HashSet<>();

		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				_oAuthClientASLocalMetadataLocalService.
					getCompanyOAuthClientASLocalMetadata(companyId)) {

			localWellKnownURIs.add(
				oAuthClientASLocalMetadata.getLocalWellKnownURI());
		}

		for (OAuthClientEntry oAuthClientEntry :
				_oAuthClientEntryLocalService.getCompanyOAuthClientEntries(
					companyId)) {

			String authServerWellKnownURI =
				oAuthClientEntry.getAuthServerWellKnownURI();

			if (!authServerWellKnownURI.endsWith("/local") ||
				localWellKnownURIs.contains(authServerWellKnownURI)) {

				continue;
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"OAuth client entry ",
						oAuthClientEntry.getOAuthClientEntryId(),
						" points to the local well known URI ",
						authServerWellKnownURI,
						", which has no OAuth 2 client authorization server ",
						"local metadata"));
			}
		}
	}

	private void _updateLocalWellKnownURIs(long companyId) {
		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				_oAuthClientASLocalMetadataLocalService.
					getCompanyOAuthClientASLocalMetadata(companyId)) {

			String localWellKnownURI =
				oAuthClientASLocalMetadata.getLocalWellKnownURI();

			if (!localWellKnownURI.endsWith("/local")) {
				continue;
			}

			try {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					oAuthClientASLocalMetadata.getMetadataJSON());

				String tokenEndpoint = jsonObject.getString("token_endpoint");

				if (Validator.isNull(tokenEndpoint)) {
					continue;
				}

				String newLocalWellKnownURI =
					OpenIdConnectProviderUtil.generateLocalWellKnownURI(
						oAuthClientASLocalMetadata.getIssuer(), tokenEndpoint);

				if (newLocalWellKnownURI.equals(localWellKnownURI)) {
					continue;
				}

				int maxLength = ModelHintsUtil.getMaxLength(
					OAuthClientASLocalMetadata.class.getName(),
					"localWellKnownURI");

				if (newLocalWellKnownURI.length() > maxLength) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to update the local well known URI of ",
								"OAuth 2 client authorization server local ",
								"metadata ",
								oAuthClientASLocalMetadata.
									getOAuthClientASLocalMetadataId(),
								" because it would exceed ", maxLength,
								" characters"));
					}

					continue;
				}

				oAuthClientASLocalMetadata.setLocalWellKnownURI(
					newLocalWellKnownURI);

				oAuthClientASLocalMetadata =
					_oAuthClientASLocalMetadataLocalService.
						updateOAuthClientASLocalMetadata(
							oAuthClientASLocalMetadata);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to update the local well known URI of ",
							"OAuth 2 client authorization server local ",
							"metadata ",
							oAuthClientASLocalMetadata.
								getOAuthClientASLocalMetadataId()),
						exception);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientASLocalMetadataPortalInstanceLifecycleListener.class);

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Reference
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

}