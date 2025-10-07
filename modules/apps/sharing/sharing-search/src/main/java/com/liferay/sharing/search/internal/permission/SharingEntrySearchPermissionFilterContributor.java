/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.search.internal.permission;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Adds a new permission filter so the search returns shared documents based on
 * the information indexed by {@link
 * SharingEntrySearchPermissionDocumentContributor}.
 *
 * @author Sergio González
 */
@Component(service = SearchPermissionFilterContributor.class)
public class SharingEntrySearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (userId == 0) {
			return;
		}

		TermsFilter sharedToUserGroupIdTermsFilter = new TermsFilter(
			"sharedToUserGroupId");

		sharedToUserGroupIdTermsFilter.addValues(
			TransformUtil.transformToArray(
				_userGroupLocalService.getUserUserGroups(userId),
				userGroup -> String.valueOf(userGroup.getUserGroupId()),
				String.class));

		if (ArrayUtil.isNotEmpty(sharedToUserGroupIdTermsFilter.getValues())) {
			booleanFilter.add(
				sharedToUserGroupIdTermsFilter, BooleanClauseOccur.SHOULD);
		}

		TermsFilter sharedToUserIdTermsFilter = new TermsFilter(
			"sharedToUserId");

		sharedToUserIdTermsFilter.addValue(String.valueOf(userId));

		booleanFilter.add(sharedToUserIdTermsFilter, BooleanClauseOccur.SHOULD);
	}

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}