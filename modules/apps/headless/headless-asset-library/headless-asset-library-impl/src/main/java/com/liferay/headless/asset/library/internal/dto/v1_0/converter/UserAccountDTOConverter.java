/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.dto.v1_0.converter;

import com.liferay.headless.asset.library.dto.v1_0.Role;
import com.liferay.headless.asset.library.dto.v1_0.UserAccount;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "dto.class.name=com.liferay.portal.kernel.model.User",
	service = DTOConverter.class
)
public class UserAccountDTOConverter
	implements DTOConverter<User, UserAccount> {

	@Override
	public String getContentType() {
		return UserAccount.class.getName();
	}

	@Override
	public UserAccount toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		// Use the local service. See LPD-62456.

		User user = _userLocalService.getUserById(
			(Long)dtoConverterContext.getId());

		return new UserAccount() {
			{
				setExternalReferenceCode(user::getExternalReferenceCode);
				setId(user::getUserId);
				setImage(
					() -> {
						if (user.getPortraitId() == 0) {
							return null;
						}

						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setPathImage(_portal.getPathImage());
							}
						};

						return user.getPortraitURL(themeDisplay);
					});
				setName(user::getFullName);
				setRoles(
					() -> NestedFieldsSupplier.supply(
						"roles",
						nestedFieldNames -> {

							// Use the local service. See LPD-62456.

							long assetLibraryId = GetterUtil.getLong(
								dtoConverterContext.getAttribute(
									"assetLibraryId"));

							return TransformUtil.transformToArray(
								_roleLocalService.getUserGroupRoles(
									user.getUserId(), assetLibraryId),
								role -> _toRole(role), Role.class);
						}));
			}
		};
	}

	private Role _toRole(com.liferay.portal.kernel.model.Role role)
		throws PortalException {

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
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}