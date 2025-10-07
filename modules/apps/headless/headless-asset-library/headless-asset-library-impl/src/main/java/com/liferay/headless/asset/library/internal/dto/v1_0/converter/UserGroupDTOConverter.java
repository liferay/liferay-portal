/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.dto.v1_0.converter;

import com.liferay.headless.asset.library.dto.v1_0.Role;
import com.liferay.headless.asset.library.dto.v1_0.UserGroup;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
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
		return UserGroup.class.getName();
	}

	@Override
	public UserGroup toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		com.liferay.portal.kernel.model.UserGroup userGroup =
			_userGroupLocalService.getUserGroup(
				(Long)dtoConverterContext.getId());

		return new UserGroup() {
			{
				setExternalReferenceCode(userGroup::getExternalReferenceCode);
				setId(userGroup::getUserGroupId);
				setName(userGroup::getName);
				setNumberOfUserAccounts(
					() -> NestedFieldsSupplier.supply(
						"numberOfUserAccounts",
						nestedField -> _userLocalService.getUserGroupUsersCount(
							userGroup.getUserGroupId())));
				setRoles(
					() -> NestedFieldsSupplier.supply(
						"roles",
						nestedFieldNames -> TransformUtil.transformToArray(
							_userGroupGroupRoleLocalService.
								getUserGroupGroupRoles(
									userGroup.getUserGroupId()),
							userGroupGroupRole -> _toRole(userGroupGroupRole),
							Role.class)));
			}
		};
	}

	private Role _toRole(UserGroupGroupRole userGroupGroupRole)
		throws PortalException {

		com.liferay.portal.kernel.model.Role role =
			userGroupGroupRole.getRole();

		return new Role() {
			{
				setExternalReferenceCode(role::getExternalReferenceCode);
				setId(role::getRoleId);
				setName(role::getName);
				setRoleType(role::getType);
			}
		};
	}

	@Reference
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}