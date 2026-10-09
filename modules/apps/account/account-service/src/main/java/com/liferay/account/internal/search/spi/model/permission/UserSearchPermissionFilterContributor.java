/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.search.spi.model.permission;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Drew Brokke
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.User",
	service = SearchPermissionFilterContributor.class
)
public class UserSearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!className.equals(User.class.getName())) {
			return;
		}

		try {
			TermsFilter termsFilter = new TermsFilter("accountEntryIds");

			List<AccountEntryUserRel> accountEntryUserRels =
				_accountEntryUserRelLocalService.
					getAccountEntryUserRelsByAccountUserId(
						permissionChecker.getUserId());

			for (AccountEntryUserRel accountEntryUserRel :
					accountEntryUserRels) {

				if (!_accountEntryModelResourcePermission.contains(
						permissionChecker,
						accountEntryUserRel.getAccountEntryId(),
						AccountActionKeys.VIEW_USERS)) {

					continue;
				}

				termsFilter.addValue(
					String.valueOf(accountEntryUserRel.getAccountEntryId()));
			}

			UserBag userBag = permissionChecker.getUserBag();

			long[] userOrgIds = userBag.getUserOrgIds();

			for (long userOrgId : userOrgIds) {
				if (OrganizationPermissionUtil.contains(
						permissionChecker, userOrgId,
						AccountActionKeys.MANAGE_ACCOUNTS)) {

					List<AccountEntryOrganizationRel>
						accountEntryOrganizationRels =
							_accountEntryOrganizationRelLocalService.
								getAccountEntryOrganizationRelsByOrganizationId(
									userOrgId);

					for (AccountEntryOrganizationRel
							accountEntryOrganizationRel :
								accountEntryOrganizationRels) {

						termsFilter.addValue(
							String.valueOf(
								accountEntryOrganizationRel.
									getAccountEntryId()));
					}
				}

				if (!OrganizationPermissionUtil.contains(
						permissionChecker, userOrgId,
						AccountActionKeys.MANAGE_SUBORGANIZATIONS_ACCOUNTS)) {

					continue;
				}

				Set<Organization> organizations = new HashSet<>();

				List<Organization> suborganizations =
					_organizationLocalService.getSuborganizations(
						companyId, userOrgId);

				while (!suborganizations.isEmpty()) {
					organizations.addAll(suborganizations);

					suborganizations =
						_organizationLocalService.getSuborganizations(
							suborganizations);
				}

				for (Organization organization : organizations) {
					List<AccountEntryOrganizationRel>
						accountEntryOrganizationRels =
							_accountEntryOrganizationRelLocalService.
								getAccountEntryOrganizationRelsByOrganizationId(
									organization.getOrganizationId());

					for (AccountEntryOrganizationRel
							accountEntryOrganizationRel :
								accountEntryOrganizationRels) {

						termsFilter.addValue(
							String.valueOf(
								accountEntryOrganizationRel.
									getAccountEntryId()));
					}
				}
			}

			if (!termsFilter.isEmpty()) {
				booleanFilter.add(termsFilter);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	@Reference
	protected RoleLocalService roleLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		UserSearchPermissionFilterContributor.class);

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

}