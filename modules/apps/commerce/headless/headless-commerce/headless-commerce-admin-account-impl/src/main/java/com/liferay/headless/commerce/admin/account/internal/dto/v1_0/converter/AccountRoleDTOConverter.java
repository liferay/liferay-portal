/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.dto.v1_0.converter;

import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountRole;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.portal.kernel.model.UserGroupRole"
	},
	service = DTOConverter.class
)
public class AccountRoleDTOConverter
	implements DTOConverter<UserGroupRole, AccountRole> {

	@Override
	public String getContentType() {
		return AccountRole.class.getSimpleName();
	}

	@Override
	public AccountRole toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		UserGroupRole userGroupRole =
			_userGroupRoleLocalService.getUserGroupRole(
				(long)dtoConverterContext.getId());

		Role role = userGroupRole.getRole();

		return new AccountRole() {
			{
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						role.getDescriptionMap()));
				setName(role::getName);
				setRoleId(role::getRoleId);
				setTitle(
					() -> LanguageUtils.getLanguageIdMap(role.getTitleMap()));
			}
		};
	}

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}