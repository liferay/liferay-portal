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
import com.liferay.oauth.client.rest.internal.resource.v1_0.util.DateRangeUtil;
import com.liferay.oauth.client.rest.resource.v1_0.OAuthClientASLocalMetadataResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.permission.PermissionUtil;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
			public Map<String, Serializable> getParameters(
				PortletDataContext portletDataContext) {

				return DateRangeUtil.getParameters(portletDataContext);
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

		return _toOAuthClientASLocalMetadata(
			serviceBuilderOAuthClientASLocalMetadata);
	}

	@Override
	public Page<OAuthClientASLocalMetadata> getOAuthClientASLocalMetadatasPage()
		throws Exception {

		return Page.of(
			transform(
				_oAuthClientASLocalMetadataService.
					getCompanyOAuthClientASLocalMetadata(
						contextCompany.getCompanyId()),
				this::_toOAuthClientASLocalMetadata));
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

		_updateResourcePermissions(
			oAuthClientASLocalMetadata,
			serviceBuilderOAuthClientASLocalMetadata);

		return _toOAuthClientASLocalMetadata(
			serviceBuilderOAuthClientASLocalMetadata);
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

			_updateResourcePermissions(
				oAuthClientASLocalMetadata,
				serviceBuilderOAuthClientASLocalMetadata);

			return _toOAuthClientASLocalMetadata(
				serviceBuilderOAuthClientASLocalMetadata);
		}

		return postOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	public Page<OAuthClientASLocalMetadata> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		Page<OAuthClientASLocalMetadata> page = super.read(
			filter, pagination, sorts, parameters, search);

		List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas =
			DateRangeUtil.filter(
				OAuthClientASLocalMetadata::getDateModified, page.getItems(),
				parameters);

		return Page.of(
			ListUtil.subList(
				oAuthClientASLocalMetadatas, pagination.getStartPosition(),
				pagination.getEndPosition()),
			pagination, oAuthClientASLocalMetadatas.size());
	}

	private OAuthClientASLocalMetadata _toOAuthClientASLocalMetadata(
		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			serviceBuilderOAuthClientASLocalMetadata) {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			OAuthClientASLocalMetadataUtil.toOAuthClientASLocalMetadata(
				_portal, serviceBuilderOAuthClientASLocalMetadata,
				_userLocalService.fetchUser(
					serviceBuilderOAuthClientASLocalMetadata.getUserId()));

		oAuthClientASLocalMetadata.setPermissions(
			() -> NestedFieldsSupplier.supply(
				"permissions",
				fieldName -> _toPermissions(
					serviceBuilderOAuthClientASLocalMetadata)));

		return oAuthClientASLocalMetadata;
	}

	private Permission[] _toPermissions(
			com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata
					serviceBuilderOAuthClientASLocalMetadata)
		throws Exception {

		String className =
			com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata.class.getName();

		_permissionService.checkPermission(
			0, className,
			serviceBuilderOAuthClientASLocalMetadata.
				getOAuthClientASLocalMetadataId());

		Collection<Permission> permissions = PermissionUtil.getPermissions(
			serviceBuilderOAuthClientASLocalMetadata.getCompanyId(),
			_resourceActionLocalService.getResourceActions(className),
			serviceBuilderOAuthClientASLocalMetadata.
				getOAuthClientASLocalMetadataId(),
			className, null);

		return permissions.toArray(new Permission[0]);
	}

	private void _updateResourcePermissions(
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata,
			com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata
					serviceBuilderOAuthClientASLocalMetadata)
		throws Exception {

		if (oAuthClientASLocalMetadata.getPermissions() == null) {
			return;
		}

		String className =
			com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata.class.getName();

		_permissionService.checkPermission(
			0, className,
			serviceBuilderOAuthClientASLocalMetadata.
				getOAuthClientASLocalMetadataId());

		ModelPermissions modelPermissions =
			ModelPermissionsUtil.toModelPermissions(
				serviceBuilderOAuthClientASLocalMetadata.getCompanyId(),
				oAuthClientASLocalMetadata.getPermissions(),
				serviceBuilderOAuthClientASLocalMetadata.
					getOAuthClientASLocalMetadataId(),
				className, _resourceActionLocalService,
				_resourcePermissionLocalService, _roleLocalService);

		String primKey = String.valueOf(
			serviceBuilderOAuthClientASLocalMetadata.
				getOAuthClientASLocalMetadataId());
		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(className);
		Collection<String> roleNames = modelPermissions.getRoleNames();

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getResourcePermissions(
					serviceBuilderOAuthClientASLocalMetadata.getCompanyId(),
					className, ResourceConstants.SCOPE_INDIVIDUAL, primKey)) {

			Role role = _roleLocalService.fetchRole(
				resourcePermission.getRoleId());

			if ((role == null) || roleNames.contains(role.getName())) {
				continue;
			}

			for (ResourceAction resourceAction : resourceActions) {
				_resourcePermissionLocalService.removeResourcePermission(
					serviceBuilderOAuthClientASLocalMetadata.getCompanyId(),
					className, ResourceConstants.SCOPE_INDIVIDUAL, primKey,
					role.getRoleId(), resourceAction.getActionId());
			}
		}

		_resourcePermissionLocalService.updateResourcePermissions(
			serviceBuilderOAuthClientASLocalMetadata.getCompanyId(), 0,
			className, primKey, modelPermissions);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuthClientASLocalMetadataService
		_oAuthClientASLocalMetadataService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}