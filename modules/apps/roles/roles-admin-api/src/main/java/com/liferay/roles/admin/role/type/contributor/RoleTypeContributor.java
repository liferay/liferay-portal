/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.role.type.contributor;

import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Represents a role type entry to contribute in the Roles Admin portlet. A tab
 * is created for each role type.
 *
 * @author Drew Brokke
 */
public interface RoleTypeContributor {

	/**
	 * Optionally returns a class name to be used when creating a new role of
	 * this type. If {@code null} is returned, use the class name of the role.
	 *
	 * @return a class name to be used when creating a new role of
	 * this types
	 */
	public default String getClassName() {
		return null;
	}

	/**
	 * Returns a list of role names whose permissions cannot be manually defined
	 * by users.
	 *
	 * @return a list of role names whose permissions cannot be manually defined
	 *         by users
	 */
	public default String[] getExcludedRoleNames() {
		return new String[0];
	}

	/**
	 * Returns the CSS class of the role type icon.
	 *
	 * @return the CSS class of the role type icon
	 */
	public String getIcon();

	/**
	 * Returns the role type name.
	 *
	 * <p>
	 * Example: <code>"regular"</code>
	 * </p>
	 *
	 * @return the role type name
	 */
	public String getName();

	/**
	 * Optionally returns an array of subtypes for this role type.
	 *
	 * @return an array of subtypes for this role type
	 */
	public default String[] getSubtypes() {
		return new String[0];
	}

	/**
	 * Returns the title to display for this role type's tab.
	 *
	 * <p>
	 * Example: <code>"Regular Roles"</code>
	 * </p>
	 *
	 * @param  locale the locale to apply
	 * @return the title to display for this role type's tab
	 */
	public String getTabTitle(Locale locale);

	/**
	 * Returns the title to display for this role type in the creation menu.
	 *
	 * <p>
	 * Example: <code>"Regular Roles"</code>
	 * </p>
	 *
	 * @param  locale the locale to apply
	 * @return the title to display for this role type in the creation menu
	 */
	public String getTitle(Locale locale);

	/**
	 * Returns an integer that represents the role type. It is used as a key to
	 * retrieve the {@code
	 * com.liferay.roles.admin.internal.role.type.contributor.RoleTypeContributor}.
	 *
	 * @return an integer that represents the role type
	 */
	public int getType();

	public default String getTypeLabel() {
		return RoleConstants.getTypeLabel(getType());
	}

	/**
	 * Returns <code>true</code> if users are allowed to assign members to the
	 * role; <code>false</code> otherwise.
	 *
	 * @param  role a role
	 * @return <code>true</code> if users are allowed to assign members to the
	 *         role; <code>false</code> otherwise
	 */
	public boolean isAllowAssignMembers(Role role);

	/**
	 * Returns <code>true</code> if users are allowed to define permissions
	 * granted by the role; <code>false</code> otherwise.
	 *
	 * @param  role a role
	 * @return <code>true</code> if users are allowed to define permissions
	 *         granted by the role; <code>false</code> otherwise
	 */
	public default boolean isAllowDefinePermissions(Role role) {
		return !ArrayUtil.contains(getExcludedRoleNames(), role.getName());
	}

	/**
	 * Returns <code>true</code> if users are allowed to delete the role;
	 * <code>false</code> otherwise.
	 *
	 * @param  role a role
	 * @return <code>true</code> if users are allowed to delete the role;
	 *         <code>false</code> otherwise
	 */
	public boolean isAllowDelete(Role role);

	/**
	 * Returns <code>true</code> if the role is automatically assgned;
	 * <code>false</code> otherwise.
	 *
	 * @param  role a role
	 * @return <code>true</code> if the role is automatically assgned;
	 *         <code>false</code> otherwise
	 */
	public default boolean isAutomaticallyAssigned(Role role) {
		return false;
	}

	public default BaseModelSearchResult<Role> searchRoles(
		long companyId, String keywords, String subtype, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		if (Validator.isNotNull(getClassName())) {
			params.put(
				"classNameId", PortalUtil.getClassNameId(getClassName()));
		}

		if (Validator.isNotNull(subtype)) {
			params.put("subtype", subtype);
		}

		int total = RoleServiceUtil.searchCount(
			companyId, keywords, new Integer[] {getType()}, params);

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			start, end, total);

		return new BaseModelSearchResult<>(
			RoleServiceUtil.search(
				companyId, keywords, new Integer[] {getType()}, params,
				startAndEnd[0], startAndEnd[1], orderByComparator),
			total);
	}

}