/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RoleFinderUtil {

	public static int countByGroupRoleAndTeamRole(
		long companyId, String name, java.util.List<String> excludedNames,
		String title, String description, int[] types, String subtype,
		long excludedTeamRoleId, long teamGroupId) {

		return getFinder().countByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types, subtype,
			excludedTeamRoleId, teamGroupId);
	}

	public static int countByKeywords(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params) {

		return getFinder().countByKeywords(companyId, keywords, types, params);
	}

	public static int countByC_N_D_T(
		long companyId, String name, String description, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return getFinder().countByC_N_D_T(
			companyId, name, description, types, params, andOperator);
	}

	public static int countByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return getFinder().countByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator);
	}

	public static int countByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return getFinder().countByC_N_T_D_T(
			companyId, name, title, description, types, params, andOperator);
	}

	public static int countByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return getFinder().countByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator);
	}

	public static int filterCountByGroupRoleAndTeamRole(
		long companyId, String name, java.util.List<String> excludedNames,
		String title, String description, int[] types, String subtype,
		long excludedTeamRoleId, long teamGroupId) {

		return getFinder().filterCountByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types, subtype,
			excludedTeamRoleId, teamGroupId);
	}

	public static int filterCountByKeywords(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params) {

		return getFinder().filterCountByKeywords(
			companyId, keywords, types, params);
	}

	public static int filterCountByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return getFinder().filterCountByC_N_T_D_T(
			companyId, name, title, description, types, params, andOperator);
	}

	public static int filterCountByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return getFinder().filterCountByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		filterFindByGroupRoleAndTeamRole(
			long companyId, String name, java.util.List<String> excludedNames,
			String title, String description, int[] types, String subtype,
			long excludedTeamRoleId, long teamGroupId, int start, int end) {

		return getFinder().filterFindByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types, subtype,
			excludedTeamRoleId, teamGroupId, start, end);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		filterFindByKeywords(
			long companyId, String keywords, Integer[] types,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator) {

		return getFinder().filterFindByKeywords(
			companyId, keywords, types, params, start, end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		filterFindByC_N_T_D_T(
			long companyId, String name, String title, String description,
			Integer[] types, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator) {

		return getFinder().filterFindByC_N_T_D_T(
			companyId, name, title, description, types, params, andOperator,
			start, end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		filterFindByC_N_T_D_T(
			long companyId, String[] names, String[] titles,
			String[] descriptions, Integer[] types,
			java.util.LinkedHashMap<String, Object> params, boolean andOperator,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator) {

		return getFinder().filterFindByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			start, end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		findByGroupRoleAndTeamRole(
			long companyId, String name, java.util.List<String> excludedNames,
			String title, String description, int[] types, String subtype,
			long excludedTeamRoleId, long teamGroupId, int start, int end) {

		return getFinder().findByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types, subtype,
			excludedTeamRoleId, teamGroupId, start, end);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		findByKeywords(
			long companyId, String keywords, Integer[] types,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator) {

		return getFinder().findByKeywords(
			companyId, keywords, types, params, start, end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		findByC_N_D_T(
			long companyId, String name, String description, Integer[] types,
			java.util.LinkedHashMap<String, Object> params, boolean andOperator,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator) {

		return getFinder().findByC_N_D_T(
			companyId, name, description, types, params, andOperator, start,
			end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		findByC_N_D_T(
			long companyId, String[] names, String[] descriptions,
			Integer[] types, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator) {

		return getFinder().findByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator, start,
			end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		findByC_N_T_D_T(
			long companyId, String name, String title, String description,
			Integer[] types, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator) {

		return getFinder().findByC_N_T_D_T(
			companyId, name, title, description, types, params, andOperator,
			start, end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Role>
		findByC_N_T_D_T(
			long companyId, String[] names, String[] titles,
			String[] descriptions, Integer[] types,
			java.util.LinkedHashMap<String, Object> params, boolean andOperator,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator) {

		return getFinder().findByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			start, end, orderByComparator);
	}

	public static RoleFinder getFinder() {
		if (_finder == null) {
			_finder = (RoleFinder)PortalBeanLocatorUtil.locate(
				RoleFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(RoleFinder finder) {
		_finder = finder;
	}

	private static RoleFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:1788791669