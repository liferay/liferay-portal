/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.frontend.data.set.filter;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.site.cms.site.initializer.users.provider.CMSUsersProvider;
import com.liferay.site.cms.site.initializer.util.CMSUserUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Fábio Alves
 */
public class AssigneeSelectionFDSFilter extends BaseSelectionFDSFilter {

	public AssigneeSelectionFDSFilter(
		ClassNameLocalService classNameLocalService,
		CMSUsersProvider cmsUsersProvider, long companyId,
		RoleService roleService) {

		_classNameLocalService = classNameLocalService;
		_cmsUsersProvider = cmsUsersProvider;
		_companyId = companyId;
		_roleService = roleService;
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.STRING;
	}

	@Override
	public String getId() {
		return "cmpAssignTo";
	}

	@Override
	public String getLabel() {
		return "assignee";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		long roleClassNameId = _classNameLocalService.getClassNameId(
			Role.class.getName());
		long userClassNameId = _classNameLocalService.getClassNameId(
			User.class.getName());

		return ListUtil.concat(
			TransformUtil.transform(
				DepotRoleUtil.filter(
					_roleService.getRoles(
						_companyId, new int[] {RoleConstants.TYPE_DEPOT}),
					DepotRolesConstants.SUBTYPE_PROJECT),
				role -> {
					if (StringUtil.equals(
							DepotRolesConstants.
								ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
							role.getName())) {

						return null;
					}

					return new SelectionFDSFilterItem(
						role.getName(),
						_getValue(roleClassNameId, role.getRoleId()));
				}),
			TransformUtil.transform(
				CMSUserUtil.getUsers(_cmsUsersProvider),
				user -> new SelectionFDSFilterItem(
					user.getFullName(),
					_getValue(userClassNameId, user.getUserId()))));
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	private String _getValue(long classNameId, long classPK) {
		return StringBundler.concat(classNameId, StringPool.UNDERLINE, classPK);
	}

	private final ClassNameLocalService _classNameLocalService;
	private final CMSUsersProvider _cmsUsersProvider;
	private final long _companyId;
	private final RoleService _roleService;

}