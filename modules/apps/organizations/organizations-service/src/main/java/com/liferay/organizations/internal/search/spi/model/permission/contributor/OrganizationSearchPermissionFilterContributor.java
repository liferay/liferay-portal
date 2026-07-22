/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.search.spi.model.permission.contributor;

import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yuri Monteiro
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.Organization",
	service = SearchPermissionFilterContributor.class
)
public class OrganizationSearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!Objects.equals(Organization.class.getName(), className) ||
			(userId == 0)) {

			return;
		}

		long[] organizationIds = _userLocalService.getOrganizationPrimaryKeys(
			userId);

		if (ArrayUtil.isEmpty(organizationIds)) {
			return;
		}

		TermsFilter termsFilter = new TermsFilter(Field.ENTRY_CLASS_PK);

		termsFilter.addValues(ArrayUtil.toStringArray(organizationIds));

		booleanFilter.add(termsFilter, BooleanClauseOccur.SHOULD);
	}

	@Reference
	private UserLocalService _userLocalService;

}