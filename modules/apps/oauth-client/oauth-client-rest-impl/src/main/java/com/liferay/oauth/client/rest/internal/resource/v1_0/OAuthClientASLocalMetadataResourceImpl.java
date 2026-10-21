/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.internal.resource.v1_0;

import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataService;
import com.liferay.oauth.client.rest.dto.v1_0.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.rest.internal.dto.v1_0.util.OAuthClientASLocalMetadataUtil;
import com.liferay.oauth.client.rest.resource.v1_0.OAuthClientASLocalMetadataResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
 * @author Manuele Castro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/o-auth-client-as-local-metadata.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE,
	service = OAuthClientASLocalMetadataResource.class
)
public class OAuthClientASLocalMetadataResourceImpl
	extends BaseOAuthClientASLocalMetadataResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate
		<OAuthClientASLocalMetadata> {

	@Override
	public void deleteOAuthClientASLocalMetadataByExternalReferenceCode(
			String oAuthClientASLocalMetadataExternalReferenceCode)
		throws Exception {

		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataService.
					getOAuthClientASLocalMetadataByExternalReferenceCode(
						oAuthClientASLocalMetadataExternalReferenceCode,
						contextCompany.getCompanyId());

		_oAuthClientASLocalMetadataService.deleteOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId());
	}

	@Override
	public ExportImportDescriptor
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public String getKey() {
				return OAuthClientASLocalMetadataResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "oauth-client-as-local-metadata-entries";
			}

			@Override
			public Class
				<com.liferay.oauth.client.persistence.model.
					OAuthClientASLocalMetadata> getModelClass() {

				return com.liferay.oauth.client.persistence.model.
					OAuthClientASLocalMetadata.class;
			}

			@Override
			public String getPortletId() {
				return OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN;
			}

			@Override
			public int getRank() {
				return 99;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_OTHER;
			}

			@Override
			public boolean isActive(PortletDataContext portletDataContext) {
				if (!FeatureFlagManagerUtil.isEnabled(
						portletDataContext.getCompanyId(), "LPD-49855")) {

					return false;
				}

				return ExportImportDescriptor.super.isActive(
					portletDataContext);
			}

		};
	}

	@Override
	public OAuthClientASLocalMetadata
			getOAuthClientASLocalMetadataByExternalReferenceCode(
				String oAuthClientASLocalMetadataExternalReferenceCode)
		throws Exception {

		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			serviceBuilderOAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataService.
					getOAuthClientASLocalMetadataByExternalReferenceCode(
						oAuthClientASLocalMetadataExternalReferenceCode,
						contextCompany.getCompanyId());

		return OAuthClientASLocalMetadataUtil.toOAuthClientASLocalMetadata(
			_portal, serviceBuilderOAuthClientASLocalMetadata,
			_userLocalService.fetchUser(
				serviceBuilderOAuthClientASLocalMetadata.getUserId()));
	}

	@Override
	public Page<OAuthClientASLocalMetadata> getOAuthClientASLocalMetadatasPage()
		throws Exception {

		return Page.of(
			transform(
				_oAuthClientASLocalMetadataService.
					getCompanyOAuthClientASLocalMetadata(
						contextCompany.getCompanyId()),
				serviceBuilderOAuthClientASLocalMetadata ->
					OAuthClientASLocalMetadataUtil.toOAuthClientASLocalMetadata(
						_portal, serviceBuilderOAuthClientASLocalMetadata,
						_userLocalService.fetchUser(
							serviceBuilderOAuthClientASLocalMetadata.
								getUserId()))));
	}

	@Override
	public OAuthClientASLocalMetadata postOAuthClientASLocalMetadata(
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws Exception {

		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			serviceBuilderOAuthClientASLocalMetadata =
				OAuthClientASLocalMetadataUtil.addOAuthClientASLocalMetadata(
					oAuthClientASLocalMetadata,
					_oAuthClientASLocalMetadataService);

		return OAuthClientASLocalMetadataUtil.toOAuthClientASLocalMetadata(
			_portal, serviceBuilderOAuthClientASLocalMetadata,
			_userLocalService.fetchUser(
				serviceBuilderOAuthClientASLocalMetadata.getUserId()));
	}

	@Override
	public OAuthClientASLocalMetadata
			putOAuthClientASLocalMetadataByExternalReferenceCode(
				String oAuthClientASLocalMetadataExternalReferenceCode,
				OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws Exception {

		oAuthClientASLocalMetadata.setExternalReferenceCode(
			() -> oAuthClientASLocalMetadataExternalReferenceCode);

		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			serviceBuilderOAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataService.
					fetchOAuthClientASLocalMetadataByExternalReferenceCode(
						oAuthClientASLocalMetadataExternalReferenceCode,
						contextCompany.getCompanyId());

		if (serviceBuilderOAuthClientASLocalMetadata != null) {
			JSONObject metadataJSONObject = _jsonFactory.createJSONObject(
				oAuthClientASLocalMetadata.getMetadataJSON());
			JSONObject oAuthASMetadataJSONObject =
				_jsonFactory.createJSONObject(
					oAuthClientASLocalMetadata.getOAuthASMetadataJSON());

			serviceBuilderOAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataService.
					updateOAuthClientASLocalMetadata(
						serviceBuilderOAuthClientASLocalMetadata.
							getOAuthClientASLocalMetadataId(),
						metadataJSONObject.getString("authorization_endpoint"),
						oAuthClientASLocalMetadata.getIssuer(),
						metadataJSONObject.getString("jwks_uri"),
						oAuthClientASLocalMetadata.getLocalWellKnownEnabled(),
						oAuthASMetadataJSONObject.getString(
							"registration_endpoint"),
						JSONUtil.toStringArray(
							metadataJSONObject.getJSONArray(
								"grant_types_supported")),
						JSONUtil.toStringArray(
							metadataJSONObject.getJSONArray(
								"scopes_supported")),
						JSONUtil.toStringArray(
							metadataJSONObject.getJSONArray(
								"subject_types_supported")),
						metadataJSONObject.getString("token_endpoint"),
						metadataJSONObject.getString("userinfo_endpoint"));

			return OAuthClientASLocalMetadataUtil.toOAuthClientASLocalMetadata(
				_portal, serviceBuilderOAuthClientASLocalMetadata,
				_userLocalService.fetchUser(
					serviceBuilderOAuthClientASLocalMetadata.getUserId()));
		}

		return postOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuthClientASLocalMetadataService
		_oAuthClientASLocalMetadataService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}