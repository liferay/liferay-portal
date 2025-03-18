/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.dto.v1_0.converter;

import com.liferay.headless.asset.library.dto.v1_0.Role;
import com.liferay.headless.asset.library.dto.v1_0.UserGroup;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "dto.class.name=com.liferay.portal.kernel.model.UserGroup",
	service = DTOConverter.class
)
public class UserGroupDTOConverter
	implements DTOConverter
		<com.liferay.portal.kernel.model.UserGroup, UserGroup> {

	@Override
	public String getContentType() {
		return UserGroup.class.getSimpleName();
	}

	@Override
	public UserGroup toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.portal.kernel.model.UserGroup userGroup)
		throws Exception {

		return new UserGroup() {
			{
				setExternalReferenceCode(userGroup::getExternalReferenceCode);
				setId(userGroup::getUserGroupId);
				setName(userGroup::getName);
				setName_i18n(
					() -> {
						if (dtoConverterContext == null) {
							return null;
						}

						Group group = userGroup.getGroup();

						return LocalizedMapUtil.getI18nMap(
							dtoConverterContext.isAcceptAllLanguages(),
							group.getNameMap());
					});
				setRoles(
					() -> NestedFieldsSupplier.supply(
						"roles",
						fieldName -> TransformUtil.transformToArray(
							_roleService.getGroupRoles(userGroup.getGroupId()),
							role -> _toRole(dtoConverterContext, role),
							Role.class)));
			}
		};
	}

	private Role _toRole(
		DTOConverterContext dtoConverterContext,
		com.liferay.portal.kernel.model.Role role) {

		return new Role() {
			{
				setExternalReferenceCode(role::getExternalReferenceCode);
				setId(role::getRoleId);
				setName(
					() -> {
						if (dtoConverterContext == null) {
							return role.getName();
						}

						return role.getTitle(dtoConverterContext.getLocale());
					});
				setName_i18n(
					() -> {
						if (dtoConverterContext == null) {
							return null;
						}

						return LocalizedMapUtil.getI18nMap(
							dtoConverterContext.isAcceptAllLanguages(),
							role.getTitleMap());
					});
				setRoleType(role::getType);
			}
		};
	}

	@Reference
	private RoleService _roleService;

}