/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface RoleFinder {

	public int countByGroupRoleAndTeamRole(
		long companyId, String name, java.util.List<String> excludedNames,
		String title, String description, int[] types, String subtype,
		long excludedTeamRoleId, long teamGroupId);

	public int countByKeywords(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params);

	public int countByC_N_D_T(
		long companyId, String name, String description, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator);

	public int countByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator);

	public int countByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator);

	public int countByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator);

	public int filterCountByGroupRoleAndTeamRole(
		long companyId, String name, java.util.List<String> excludedNames,
		String title, String description, int[] types, String subtype,
		long excludedTeamRoleId, long teamGroupId);

	public int filterCountByKeywords(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params);

	public int filterCountByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator);

	public int filterCountByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator);

	public java.util.List<com.liferay.portal.kernel.model.Role>
		filterFindByGroupRoleAndTeamRole(
			long companyId, String name, java.util.List<String> excludedNames,
			String title, String description, int[] types, String subtype,
			long excludedTeamRoleId, long teamGroupId, int start, int end);

	public java.util.List<com.liferay.portal.kernel.model.Role>
		filterFindByKeywords(
			long companyId, String keywords, Integer[] types,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Role>
		filterFindByC_N_T_D_T(
			long companyId, String name, String title, String description,
			Integer[] types, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Role>
		filterFindByC_N_T_D_T(
			long companyId, String[] names, String[] titles,
			String[] descriptions, Integer[] types,
			java.util.LinkedHashMap<String, Object> params, boolean andOperator,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Role> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Role>
		findByGroupRoleAndTeamRole(
			long companyId, String name, java.util.List<String> excludedNames,
			String title, String description, int[] types, String subtype,
			long excludedTeamRoleId, long teamGroupId, int start, int end);

	public java.util.List<com.liferay.portal.kernel.model.Role> findByKeywords(
		long companyId, String keywords, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Role> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Role> findByC_N_D_T(
		long companyId, String name, String description, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Role> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Role> findByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Role> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Role> findByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Role> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Role> findByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Role> orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:417994989