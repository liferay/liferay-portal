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
public interface UserFinder {

	public int countByKeywords(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params);

	public int countByOrganizationsAndUserGroups(
		long[] organizationIds, long[] userGroupIds);

	public int countBySocialUsers(
		long companyId, long userId, int socialRelationType,
		String socialRelationTypeComparator, int status);

	public int countByUser(
		long userId, java.util.LinkedHashMap<String, Object> params);

	public int countByC_FN_MN_LN_SN_EA_S(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator);

	public int countByC_FN_MN_LN_SN_EA_S(
		long companyId, String[] firstNames, String[] middleNames,
		String[] lastNames, String[] screenNames, String[] emailAddresses,
		int status, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator);

	public java.util.List<com.liferay.portal.kernel.model.User> findByKeywords(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.User> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.User>
		findBySocialUsers(
			long companyId, long userId, int socialRelationType,
			String socialRelationTypeComparator, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.User> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.User>
		findByUsersOrgsGtUserId(
			long companyId, long organizationId, long gtUserId, int size);

	public java.util.List<com.liferay.portal.kernel.model.User>
		findByUsersUserGroupsGtUserId(
			long companyId, long userGroupId, long gtUserId, int size);

	public java.util.List<com.liferay.portal.kernel.model.User>
		findByC_FN_MN_LN_SN_EA_S(
			long companyId, String firstName, String middleName,
			String lastName, String screenName, String emailAddress, int status,
			java.util.LinkedHashMap<String, Object> params, boolean andOperator,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.User> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.User>
		findByC_FN_MN_LN_SN_EA_S(
			long companyId, String[] firstNames, String[] middleNames,
			String[] lastNames, String[] screenNames, String[] emailAddresses,
			int status, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.User> orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:1288985768