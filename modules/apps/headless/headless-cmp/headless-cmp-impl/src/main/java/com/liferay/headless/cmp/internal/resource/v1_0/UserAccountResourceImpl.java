/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.resource.v1_0;

import com.liferay.headless.cmp.dto.v1_0.UserAccount;
import com.liferay.headless.cmp.resource.v1_0.UserAccountResource;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.users.provider.CMSUsersProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-account.properties",
	scope = ServiceScope.PROTOTYPE, service = UserAccountResource.class
)
public class UserAccountResourceImpl extends BaseUserAccountResourceImpl {

	@Override
	public Page<UserAccount> getProjectUserAccountsPage(
			Long projectId, String search, Pagination pagination)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(projectId);

		_groupModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			objectEntry.getGroupId(), ActionKeys.ASSIGN_MEMBERS);

		return Page.of(
			transform(
				_cmsUsersProvider.getUsers(
					search, pagination.getStartPosition(),
					pagination.getEndPosition()),
				this::_toUserAccount),
			pagination, _cmsUsersProvider.getUsersCount(search));
	}

	private UserAccount _toUserAccount(User user) {
		return new UserAccount() {
			{
				setEmailAddress(user::getEmailAddress);
				setExternalReferenceCode(user::getExternalReferenceCode);
				setId(user::getUserId);
				setImage(
					() -> {
						if (user.getPortraitId() == 0) {
							return null;
						}

						return user.getPortraitURL(
							new ThemeDisplay() {
								{
									setPathImage(_portal.getPathImage());
								}
							});
					});
				setImageId(user::getPortraitId);
				setName(user::getFullName);
			}
		};
	}

	@Reference
	private CMSUsersProvider _cmsUsersProvider;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Group)"
	)
	private ModelResourcePermission<Group> _groupModelResourcePermission;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

}