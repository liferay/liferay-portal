/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.internal.resource.v1_0;

import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataService;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryService;
import com.liferay.oauth.client.rest.dto.v1_0.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.rest.dto.v1_0.OAuthClientEntry;
import com.liferay.oauth.client.rest.internal.dto.v1_0.util.OAuthClientASLocalMetadataUtil;
import com.liferay.oauth.client.rest.internal.resource.v1_0.util.DateRangeUtil;
import com.liferay.oauth.client.rest.resource.v1_0.OAuthClientEntryResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
import com.liferay.portal.kernel.util.Validator;
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
	properties = "OSGI-INF/liferay/rest/v1_0/o-auth-client-entry.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = OAuthClientEntryResource.class
)
public class OAuthClientEntryResourceImpl
	extends BaseOAuthClientEntryResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<OAuthClientEntry> {

	@Override
	public void deleteOAuthClientEntryByExternalReferenceCode(
			String oauthClientEntryExternalReferenceCode)
		throws Exception {

		com.liferay.oauth.client.persistence.model.OAuthClientEntry
			oAuthClientEntry =
				_oAuthClientEntryService.
					getOAuthClientEntryByExternalReferenceCode(
						oauthClientEntryExternalReferenceCode,
						contextCompany.getCompanyId());

		_oAuthClientEntryService.deleteOAuthClientEntry(
			oAuthClientEntry.getOAuthClientEntryId());
	}

	@Override
	public ExportImportDescriptor
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public String getKey() {
				return OAuthClientEntryResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "oauth-client-entries";
			}

			@Override
			public Class
				<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
					getModelClass() {

				return com.liferay.oauth.client.persistence.model.
					OAuthClientEntry.class;
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
	public Page<OAuthClientEntry> getOAuthClientEntriesPage() throws Exception {
		return Page.of(
			transform(
				_oAuthClientEntryService.getCompanyOAuthClientEntries(
					contextCompany.getCompanyId()),
				this::_toOAuthClientEntry));
	}

	@Override
	public OAuthClientEntry getOAuthClientEntryByExternalReferenceCode(
			String oauthClientEntryExternalReferenceCode)
		throws Exception {

		return _toOAuthClientEntry(
			_oAuthClientEntryService.getOAuthClientEntryByExternalReferenceCode(
				oauthClientEntryExternalReferenceCode,
				contextCompany.getCompanyId()));
	}

	@Override
	public OAuthClientEntry postOAuthClientEntry(
			OAuthClientEntry oAuthClientEntry)
		throws Exception {

		String authServerWellKnownURI =
			oAuthClientEntry.getAuthServerWellKnownURI();

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientEntry.getOAuthClientASLocalMetadata();

		if (Validator.isNotNull(authServerWellKnownURI) &&
			authServerWellKnownURI.endsWith("local") &&
			(oAuthClientASLocalMetadata != null)) {

			com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata
					serviceBuilderOAuthClientASLocalMetadata =
						_oAuthClientASLocalMetadataService.
							fetchOAuthClientASLocalMetadata(
								contextCompany.getCompanyId(),
								oAuthClientASLocalMetadata.getIssuer());

			if (serviceBuilderOAuthClientASLocalMetadata == null) {
				OAuthClientASLocalMetadataUtil.addOAuthClientASLocalMetadata(
					oAuthClientASLocalMetadata,
					_oAuthClientASLocalMetadataService);
			}
		}

		com.liferay.oauth.client.persistence.model.OAuthClientEntry
			serviceBuilderOAuthClientEntry =
				_oAuthClientEntryService.addOAuthClientEntry(
					oAuthClientEntry.getExternalReferenceCode(),
					contextUser.getUserId(),
					oAuthClientEntry.getAuthRequestParametersJSON(),
					oAuthClientEntry.getAuthServerWellKnownURI(),
					oAuthClientEntry.getCustomClaims(),
					oAuthClientEntry.getInfoJSON(),
					oAuthClientEntry.getMatcherField(),
					oAuthClientEntry.getMetadataCacheTime(),
					oAuthClientEntry.getOidcUserInfoMapperJSON(),
					oAuthClientEntry.getTokenConnectionTimeout(),
					oAuthClientEntry.getTokenRequestParametersJSON());

		_updateResourcePermissions(
			oAuthClientEntry, serviceBuilderOAuthClientEntry);

		return _toOAuthClientEntry(serviceBuilderOAuthClientEntry);
	}

	@Override
	public OAuthClientEntry putOAuthClientEntryByExternalReferenceCode(
			String oauthClientEntryExternalReferenceCode,
			OAuthClientEntry oAuthClientEntry)
		throws Exception {

		oAuthClientEntry.setExternalReferenceCode(
			() -> oauthClientEntryExternalReferenceCode);

		com.liferay.oauth.client.persistence.model.OAuthClientEntry
			serviceBuilderOAuthClientEntry =
				_oAuthClientEntryService.
					fetchOAuthClientEntryByExternalReferenceCode(
						oauthClientEntryExternalReferenceCode,
						contextCompany.getCompanyId());

		if (serviceBuilderOAuthClientEntry != null) {
			serviceBuilderOAuthClientEntry =
				_oAuthClientEntryService.updateOAuthClientEntry(
					serviceBuilderOAuthClientEntry.getOAuthClientEntryId(),
					oAuthClientEntry.getAuthRequestParametersJSON(),
					oAuthClientEntry.getAuthServerWellKnownURI(),
					oAuthClientEntry.getCustomClaims(),
					oAuthClientEntry.getInfoJSON(),
					oAuthClientEntry.getMatcherField(),
					oAuthClientEntry.getMetadataCacheTime(),
					oAuthClientEntry.getOidcUserInfoMapperJSON(),
					oAuthClientEntry.getTokenConnectionTimeout(),
					oAuthClientEntry.getTokenRequestParametersJSON());

			_updateResourcePermissions(
				oAuthClientEntry, serviceBuilderOAuthClientEntry);

			return _toOAuthClientEntry(serviceBuilderOAuthClientEntry);
		}

		return postOAuthClientEntry(oAuthClientEntry);
	}

	@Override
	public Page<OAuthClientEntry> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		Page<OAuthClientEntry> page = super.read(
			filter, pagination, sorts, parameters, search);

		List<OAuthClientEntry> oAuthClientEntries = DateRangeUtil.filter(
			OAuthClientEntry::getDateModified, page.getItems(), parameters);

		return Page.of(
			ListUtil.subList(
				oAuthClientEntries, pagination.getStartPosition(),
				pagination.getEndPosition()),
			pagination, oAuthClientEntries.size());
	}

	private OAuthClientASLocalMetadata _toOAuthClientASLocalMetadata(
		String localWellKnownURI) {

		if (Validator.isNotNull(localWellKnownURI) &&
			!localWellKnownURI.endsWith("local")) {

			return null;
		}

		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			serviceBuilderOAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataLocalService.
					fetchOAuthClientASLocalMetadataByLocalWellKnownURI(
						contextCompany.getCompanyId(), localWellKnownURI);

		if (serviceBuilderOAuthClientASLocalMetadata == null) {
			return null;
		}

		return OAuthClientASLocalMetadataUtil.toOAuthClientASLocalMetadata(
			_portal, serviceBuilderOAuthClientASLocalMetadata,
			_userLocalService.fetchUser(
				serviceBuilderOAuthClientASLocalMetadata.getUserId()));
	}

	private OAuthClientEntry _toOAuthClientEntry(
		com.liferay.oauth.client.persistence.model.OAuthClientEntry
			serviceBuilderOAuthClientEntry) {

		if (serviceBuilderOAuthClientEntry == null) {
			return null;
		}

		return new OAuthClientEntry() {
			{
				setAuthRequestParametersJSON(
					serviceBuilderOAuthClientEntry::
						getAuthRequestParametersJSON);
				setAuthServerWellKnownURI(
					serviceBuilderOAuthClientEntry::getAuthServerWellKnownURI);
				setClientId(serviceBuilderOAuthClientEntry::getClientId);
				setCreator(
					() -> CreatorUtil.toCreator(
						null, _portal,
						_userLocalService.fetchUser(
							serviceBuilderOAuthClientEntry.getUserId())));
				setCustomClaims(
					serviceBuilderOAuthClientEntry::getCustomClaimsJSON);
				setDateCreated(serviceBuilderOAuthClientEntry::getCreateDate);
				setDateModified(
					serviceBuilderOAuthClientEntry::getModifiedDate);
				setExternalReferenceCode(
					serviceBuilderOAuthClientEntry::getExternalReferenceCode);
				setInfoJSON(serviceBuilderOAuthClientEntry::getInfoJSON);
				setMatcherField(
					serviceBuilderOAuthClientEntry::getMatcherField);
				setMetadataCacheTime(
					serviceBuilderOAuthClientEntry::getMetadataCacheTime);
				setOAuthClientASLocalMetadata(
					() -> _toOAuthClientASLocalMetadata(
						serviceBuilderOAuthClientEntry.
							getAuthServerWellKnownURI()));
				setOidcUserInfoMapperJSON(
					serviceBuilderOAuthClientEntry::getOIDCUserInfoMapperJSON);
				setPermissions(
					() -> NestedFieldsSupplier.supply(
						"permissions",
						fieldName -> _toPermissions(
							serviceBuilderOAuthClientEntry)));
				setTokenConnectionTimeout(
					serviceBuilderOAuthClientEntry::getTokenConnectionTimeout);
				setTokenRequestParametersJSON(
					serviceBuilderOAuthClientEntry::
						getTokenRequestParametersJSON);
			}
		};
	}

	private Permission[] _toPermissions(
			com.liferay.oauth.client.persistence.model.OAuthClientEntry
				serviceBuilderOAuthClientEntry)
		throws Exception {

		String className =
			com.liferay.oauth.client.persistence.model.OAuthClientEntry.class.
				getName();

		_permissionService.checkPermission(
			0, className,
			serviceBuilderOAuthClientEntry.getOAuthClientEntryId());

		Collection<Permission> permissions = PermissionUtil.getPermissions(
			serviceBuilderOAuthClientEntry.getCompanyId(),
			_resourceActionLocalService.getResourceActions(className),
			serviceBuilderOAuthClientEntry.getOAuthClientEntryId(), className,
			null);

		return permissions.toArray(new Permission[0]);
	}

	private void _updateResourcePermissions(
			OAuthClientEntry oAuthClientEntry,
			com.liferay.oauth.client.persistence.model.OAuthClientEntry
				serviceBuilderOAuthClientEntry)
		throws Exception {

		if (oAuthClientEntry.getPermissions() == null) {
			return;
		}

		String className =
			com.liferay.oauth.client.persistence.model.OAuthClientEntry.class.
				getName();

		_permissionService.checkPermission(
			0, className,
			serviceBuilderOAuthClientEntry.getOAuthClientEntryId());

		ModelPermissions modelPermissions =
			ModelPermissionsUtil.toModelPermissions(
				serviceBuilderOAuthClientEntry.getCompanyId(),
				oAuthClientEntry.getPermissions(),
				serviceBuilderOAuthClientEntry.getOAuthClientEntryId(),
				className, _resourceActionLocalService,
				_resourcePermissionLocalService, _roleLocalService);

		String primKey = String.valueOf(
			serviceBuilderOAuthClientEntry.getOAuthClientEntryId());
		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(className);
		Collection<String> roleNames = modelPermissions.getRoleNames();

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getResourcePermissions(
					serviceBuilderOAuthClientEntry.getCompanyId(), className,
					ResourceConstants.SCOPE_INDIVIDUAL, primKey)) {

			Role role = _roleLocalService.fetchRole(
				resourcePermission.getRoleId());

			if ((role == null) || roleNames.contains(role.getName())) {
				continue;
			}

			for (ResourceAction resourceAction : resourceActions) {
				_resourcePermissionLocalService.removeResourcePermission(
					serviceBuilderOAuthClientEntry.getCompanyId(), className,
					ResourceConstants.SCOPE_INDIVIDUAL, primKey,
					role.getRoleId(), resourceAction.getActionId());
			}
		}

		_resourcePermissionLocalService.updateResourcePermissions(
			serviceBuilderOAuthClientEntry.getCompanyId(), 0, className,
			primKey, modelPermissions);
	}

	@Reference
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Reference
	private OAuthClientASLocalMetadataService
		_oAuthClientASLocalMetadataService;

	@Reference
	private OAuthClientEntryService _oAuthClientEntryService;

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