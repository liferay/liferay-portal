/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.permission;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.search.configuration.SearchPermissionCheckerConfiguration;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 * @author Gabriel Albuquerque
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.SearchPermissionCheckerConfiguration",
	service = SearchPermissionFilterContributor.class
)
public class ObjectEntrySearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!className.startsWith(ObjectDefinition.class.getName())) {
			return;
		}

		List<Long> accountEntryIds = new ArrayList<>();

		for (AccountEntryUserRel accountEntryUserRel :
				_accountEntryUserRelLocalService.
					getAccountEntryUserRelsByAccountUserId(
						permissionChecker.getUserId())) {

			try {
				AccountEntry accountEntry =
					accountEntryUserRel.getAccountEntry();

				if (accountEntry.isInactive()) {
					continue;
				}

				accountEntryIds.add(accountEntry.getAccountEntryId());
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		List<Organization> organizations =
			_organizationLocalService.getUserOrganizations(
				permissionChecker.getUserId());

		int termsCount = accountEntryIds.size() + organizations.size();

		int permissionTermsLimit =
			_searchPermissionCheckerConfiguration.permissionTermsLimit();

		if (termsCount > permissionTermsLimit) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skipping presearch account restriction checking due ",
						"to too many account entries and organizations: ",
						termsCount, " > ", permissionTermsLimit));
			}

			return;
		}

		_contributeAccountEntryRestrictedObjectFieldValue(
			accountEntryIds, booleanFilter);
		_contributeAccountEntryRestrictedOrganizationIds(
			booleanFilter, organizations);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_searchPermissionCheckerConfiguration =
			ConfigurableUtil.createConfigurable(
				SearchPermissionCheckerConfiguration.class, properties);
	}

	private void _contributeAccountEntryRestrictedObjectFieldValue(
		List<Long> accountEntryIds, BooleanFilter booleanFilter) {

		if (accountEntryIds.isEmpty()) {
			return;
		}

		TermsFilter termsFilter = new TermsFilter(
			"accountEntryRestrictedObjectFieldValue");

		for (long accountEntryId : accountEntryIds) {
			termsFilter.addValue(String.valueOf(accountEntryId));
		}

		booleanFilter.add(termsFilter, BooleanClauseOccur.SHOULD);
	}

	private void _contributeAccountEntryRestrictedOrganizationIds(
		BooleanFilter booleanFilter, List<Organization> organizations) {

		if (organizations.isEmpty()) {
			return;
		}

		TermsFilter termsFilter = new TermsFilter(
			"accountEntryRestrictedOrganizationIds");

		for (Organization organization : organizations) {
			termsFilter.addValue(
				String.valueOf(organization.getOrganizationId()));

			for (Organization descendantOrganization :
					organization.getDescendants()) {

				termsFilter.addValue(
					String.valueOf(descendantOrganization.getOrganizationId()));
			}
		}

		booleanFilter.add(termsFilter, BooleanClauseOccur.SHOULD);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntrySearchPermissionFilterContributor.class);

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	private volatile SearchPermissionCheckerConfiguration
		_searchPermissionCheckerConfiguration;

}