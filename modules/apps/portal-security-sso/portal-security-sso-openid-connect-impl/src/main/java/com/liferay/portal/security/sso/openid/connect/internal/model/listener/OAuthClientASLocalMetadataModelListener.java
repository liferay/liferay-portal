/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.model.listener;

import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Álvaro Saugar
 */
@Component(service = ModelListener.class)
public class OAuthClientASLocalMetadataModelListener
	extends BaseModelListener<OAuthClientASLocalMetadata> {

	@Override
	public void onAfterUpdate(
			OAuthClientASLocalMetadata originalOAuthClientASLocalMetadata,
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws ModelListenerException {

		String originalLocalWellKnownURI =
			originalOAuthClientASLocalMetadata.getLocalWellKnownURI();

		if (Validator.isNull(originalLocalWellKnownURI) ||
			originalLocalWellKnownURI.equals(
				oAuthClientASLocalMetadata.getLocalWellKnownURI())) {

			return;
		}

		for (OAuthClientEntry oAuthClientEntry :
				_oAuthClientEntryLocalService.getCompanyOAuthClientEntries(
					oAuthClientASLocalMetadata.getCompanyId())) {

			if (!originalLocalWellKnownURI.equals(
					oAuthClientEntry.getAuthServerWellKnownURI())) {

				continue;
			}

			oAuthClientEntry.setAuthServerWellKnownURI(
				oAuthClientASLocalMetadata.getLocalWellKnownURI());

			_oAuthClientEntryLocalService.updateOAuthClientEntry(
				oAuthClientEntry);
		}

		DynamicQuery dynamicQuery =
			_openIdConnectSessionLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"authServerWellKnownURI", originalLocalWellKnownURI));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", oAuthClientASLocalMetadata.getCompanyId()));

		List<OpenIdConnectSession> openIdConnectSessions =
			_openIdConnectSessionLocalService.dynamicQuery(dynamicQuery);

		for (OpenIdConnectSession openIdConnectSession :
				openIdConnectSessions) {

			openIdConnectSession.setAuthServerWellKnownURI(
				oAuthClientASLocalMetadata.getLocalWellKnownURI());

			_openIdConnectSessionLocalService.updateOpenIdConnectSession(
				openIdConnectSession);
		}
	}

	@Reference
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	@Reference
	private OpenIdConnectSessionLocalService _openIdConnectSessionLocalService;

}