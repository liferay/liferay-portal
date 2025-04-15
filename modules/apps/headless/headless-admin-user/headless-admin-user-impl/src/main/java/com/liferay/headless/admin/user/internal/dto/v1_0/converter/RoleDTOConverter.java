/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.converter;

import com.liferay.headless.admin.user.dto.v1_0.Role;
import com.liferay.headless.admin.user.dto.v1_0.RolePermission;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PermissionUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Admin.User",
		"dto.class.name=com.liferay.portal.kernel.model.Role", "version=v1.0"
	},
	service = DTOConverter.class
)
public class RoleDTOConverter
	implements DTOConverter<com.liferay.portal.kernel.model.Role, Role> {

	@Override
	public String getContentType() {
		return Role.class.getSimpleName();
	}

	@Override
	public Role toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.portal.kernel.model.Role role)
		throws Exception {

		return new Role() {
			{
				setActions(dtoConverterContext::getActions);
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						role.getAvailableLanguageIds()));
				setCreator(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(role.getUserId())));
				setDateCreated(role::getCreateDate);
				setDateModified(role::getModifiedDate);
				setDescription(
					() -> role.getDescription(dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						role.getDescriptionMap()));
				setExternalReferenceCode(role::getExternalReferenceCode);
				setId(role::getRoleId);
				setName(role::getName);
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(role.getTitleMap()));
				setPermissions(
					() -> NestedFieldsSupplier.supply(
						"permissions",
						nestedFieldNames -> {
							User user = _userLocalService.fetchUser(
								role.getUserId());

							return PermissionUtil.toPermissions(
								user.getCompanyId(), user.getGroupId(),
								role.getRoleId(),
								com.liferay.portal.kernel.model.Role.class.
									getName(),
								_permissionService,
								_resourceActionLocalService);
						}));
				setRolePermissions(
					() -> {
						UriInfo uriInfo = dtoConverterContext.getUriInfo();

						if (uriInfo != null) {
							MultivaluedMap<String, String> queryParameters =
								uriInfo.getQueryParameters();

							if (StringUtil.contains(
									queryParameters.getFirst("restrictFields"),
									"rolePermissions")) {

								return null;
							}
						}

						return TransformUtil.transformToArray(
							_resourcePermissionLocalService.
								getRoleResourcePermissions(role.getRoleId()),
							resourcePermission -> _toRolePermission(
								dtoConverterContext, resourcePermission),
							RolePermission.class);
					});
				setRoleType(role::getTypeLabel);
			}
		};
	}

	private RolePermission _toRolePermission(
		DTOConverterContext dtoConverterContext,
		ResourcePermission resourcePermission) {

		return new RolePermission() {
			{
				setActionIds(
					() -> {
						List<ResourceAction> resourceActions =
							_resourceActionLocalService.getResourceActions(
								resourcePermission.getName());

						Set<String> actionIdsSet = new HashSet<>();

						long actionIds = resourcePermission.getActionIds();

						for (ResourceAction resourceAction : resourceActions) {
							long bitwiseValue =
								actionIds & resourceAction.getBitwiseValue();

							if (bitwiseValue ==
									resourceAction.getBitwiseValue()) {

								actionIdsSet.add(resourceAction.getActionId());
							}
						}

						return actionIdsSet.toArray(new String[0]);
					});
				setId(resourcePermission::getRoleId);
				setLabel(
					() -> {
						String resourceName = getResourceName();

						if (Validator.isBlank(resourceName)) {
							return null;
						}

						if (resourceName.contains("model")) {
							return _language.get(
								dtoConverterContext.getLocale(),
								"model.resource." + resourceName);
						}

						if (resourceName.contains("portlet")) {
							return _language.get(
								dtoConverterContext.getLocale(),
								"jakarta.portlet.title." + resourceName);
						}

						return resourceName;
					});
				setPrimaryKey(resourcePermission::getPrimKey);
				setResourceName(resourcePermission::getName);
				setRoleId(resourcePermission::getRoleId);
				setScope(() -> (long)resourcePermission.getScope());
			}
		};
	}

	@Reference
	private Language _language;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}