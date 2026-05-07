/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.internal.resource.v1_0;

import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataService;
import com.liferay.oauth.client.rest.dto.v1_0.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.rest.internal.dto.v1_0.util.OAuthClientPRLocalMetadataUtil;
import com.liferay.oauth.client.rest.resource.v1_0.OAuthClientPRLocalMetadataResource;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jorge García Jiménez
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/o-auth-client-pr-local-metadata.properties",
	scope = ServiceScope.PROTOTYPE,
	service = OAuthClientPRLocalMetadataResource.class
)
public class OAuthClientPRLocalMetadataResourceImpl
	extends BaseOAuthClientPRLocalMetadataResourceImpl {

	@Override
	public void deleteOAuthClientPRLocalMetadataByExternalReferenceCode(
			String oAuthClientPRLocalMetadataExternalReferenceCode)
		throws Exception {

		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			oAuthClientPRLocalMetadata =
				_oAuthClientPRLocalMetadataService.
					getOAuthClientPRLocalMetadataByExternalReferenceCode(
						oAuthClientPRLocalMetadataExternalReferenceCode,
						contextCompany.getCompanyId());

		_oAuthClientPRLocalMetadataService.deleteOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId());
	}

	@Override
	public OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				String oAuthClientPRLocalMetadataExternalReferenceCode)
		throws Exception {

		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			serviceBuilderOAuthClientPRLocalMetadata =
				_oAuthClientPRLocalMetadataService.
					getOAuthClientPRLocalMetadataByExternalReferenceCode(
						oAuthClientPRLocalMetadataExternalReferenceCode,
						contextCompany.getCompanyId());

		return OAuthClientPRLocalMetadataUtil.toOAuthClientPRLocalMetadata(
			_portal, serviceBuilderOAuthClientPRLocalMetadata,
			_userLocalService.fetchUser(
				serviceBuilderOAuthClientPRLocalMetadata.getUserId()));
	}

	@Override
	public Page<OAuthClientPRLocalMetadata> getOAuthClientPRLocalMetadatasPage()
		throws Exception {

		return Page.of(
			transform(
				_oAuthClientPRLocalMetadataService.
					getCompanyOAuthClientPRLocalMetadata(
						contextCompany.getCompanyId()),
				serviceBuilderOAuthClientPRLocalMetadata ->
					OAuthClientPRLocalMetadataUtil.toOAuthClientPRLocalMetadata(
						_portal, serviceBuilderOAuthClientPRLocalMetadata,
						_userLocalService.fetchUser(
							serviceBuilderOAuthClientPRLocalMetadata.
								getUserId()))));
	}

	@Override
	public OAuthClientPRLocalMetadata postOAuthClientPRLocalMetadata(
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata)
		throws Exception {

		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			serviceBuilderOAuthClientPRLocalMetadata =
				OAuthClientPRLocalMetadataUtil.addOAuthClientPRLocalMetadata(
					_jsonFactory, oAuthClientPRLocalMetadata,
					_oAuthClientPRLocalMetadataService);

		return OAuthClientPRLocalMetadataUtil.toOAuthClientPRLocalMetadata(
			_portal, serviceBuilderOAuthClientPRLocalMetadata,
			_userLocalService.fetchUser(
				serviceBuilderOAuthClientPRLocalMetadata.getUserId()));
	}

	@Override
	public OAuthClientPRLocalMetadata
			putOAuthClientPRLocalMetadataByExternalReferenceCode(
				String oAuthClientPRLocalMetadataExternalReferenceCode,
				OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata)
		throws Exception {

		oAuthClientPRLocalMetadata.setExternalReferenceCode(
			() -> oAuthClientPRLocalMetadataExternalReferenceCode);

		com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			serviceBuilderOAuthClientPRLocalMetadata =
				_oAuthClientPRLocalMetadataService.
					fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
						oAuthClientPRLocalMetadataExternalReferenceCode,
						contextCompany.getCompanyId());

		if (serviceBuilderOAuthClientPRLocalMetadata != null) {
			JSONObject metadataJSONObject = _jsonFactory.createJSONObject(
				oAuthClientPRLocalMetadata.getMetadataJSON());

			serviceBuilderOAuthClientPRLocalMetadata =
				_oAuthClientPRLocalMetadataService.
					updateOAuthClientPRLocalMetadata(
						serviceBuilderOAuthClientPRLocalMetadata.
							getOAuthClientPRLocalMetadataId(),
						JSONUtil.toStringArray(
							metadataJSONObject.getJSONArray(
								"authorization_servers")),
						JSONUtil.toStringArray(
							metadataJSONObject.getJSONArray(
								"bearer_methods_supported")),
						oAuthClientPRLocalMetadata.getLocalWellKnownEnabled(),
						oAuthClientPRLocalMetadata.getResource(),
						metadataJSONObject.getString("resource_name"),
						JSONUtil.toStringArray(
							metadataJSONObject.getJSONArray(
								"scopes_supported")));

			return OAuthClientPRLocalMetadataUtil.toOAuthClientPRLocalMetadata(
				_portal, serviceBuilderOAuthClientPRLocalMetadata,
				_userLocalService.fetchUser(
					serviceBuilderOAuthClientPRLocalMetadata.getUserId()));
		}

		return postOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuthClientPRLocalMetadataService
		_oAuthClientPRLocalMetadataService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}