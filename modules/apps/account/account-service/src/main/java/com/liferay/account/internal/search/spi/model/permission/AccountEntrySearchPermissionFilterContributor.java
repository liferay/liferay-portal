/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.search.spi.model.permission;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = SearchPermissionFilterContributor.class)
public class AccountEntrySearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!className.equals(AccountEntry.class.getName())) {
			return;
		}

		_addAccountUserIdsFilters(booleanFilter, userId);
		_addOrganizationIdsFilter(
			booleanFilter, companyId, userId, permissionChecker);
	}

	private void _addAccountUserIdsFilters(
		BooleanFilter booleanFilter, long userId) {

		TermsFilter accountUserIdsTermsFilter = new TermsFilter(
			"accountUserIds");

		accountUserIdsTermsFilter.addValue(String.valueOf(userId));

		booleanFilter.add(accountUserIdsTermsFilter, BooleanClauseOccur.SHOULD);
	}

	private void _addOrganizationIdsFilter(
		BooleanFilter booleanFilter, long companyId, long userId,
		PermissionChecker permissionChecker) {

		TermsFilter organizationIdsTermsFilter = new TermsFilter(
			"organizationIds");

		try {
			Set<Organization> organizations = new HashSet<>();

			for (Organization organization :
					_organizationLocalService.getUserOrganizations(userId)) {

				boolean hasManageAvailableAccountsPermission =
					OrganizationPermissionUtil.contains(
						permissionChecker, organization.getOrganizationId(),
						AccountActionKeys.MANAGE_AVAILABLE_ACCOUNTS);

				if (hasManageAvailableAccountsPermission ||
					OrganizationPermissionUtil.contains(
						permissionChecker, organization,
						AccountActionKeys.MANAGE_ACCOUNTS)) {

					organizations.add(organization);
				}

				if (!hasManageAvailableAccountsPermission &&
					!OrganizationPermissionUtil.contains(
						permissionChecker, organization,
						AccountActionKeys.MANAGE_SUBORGANIZATIONS_ACCOUNTS) &&
					!OrganizationPermissionUtil.contains(
						permissionChecker, organization,
						AccountActionKeys.UPDATE_SUBORGANIZATIONS_ACCOUNTS)) {

					continue;
				}

				organizations.addAll(
					_organizationLocalService.getOrganizations(
						companyId, organization.getTreePath() + "%/"));
			}

			BaseModelSearchResult<Organization> baseModelSearchResult =
				_organizationLocalService.searchOrganizations(
					companyId, OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
					null,
					LinkedHashMapBuilder.<String, Object>put(
						"accountsOrgsTree",
						ListUtil.fromCollection(organizations)
					).build(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			for (Organization organization :
					baseModelSearchResult.getBaseModels()) {

				organizationIdsTermsFilter.addValue(
					String.valueOf(organization.getOrganizationId()));
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		if (!organizationIdsTermsFilter.isEmpty()) {
			booleanFilter.add(
				organizationIdsTermsFilter, BooleanClauseOccur.SHOULD);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntrySearchPermissionFilterContributor.class);

	@Reference
	private OrganizationLocalService _organizationLocalService;

}