/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.permission;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceAccountActionKeys;
import com.liferay.commerce.internal.util.AccountEntryUtil;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Crescenzo Rega
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

		try {
			_addCommerceChannelIdsFilter(booleanFilter, userId);

			if (_accountEntryModelResourcePermission.contains(
					permissionChecker, 0,
					CommerceAccountActionKeys.
						MANAGE_AVAILABLE_ACCOUNTS_VIA_USER_CHANNEL_REL)) {

				List<CommerceChannelAccountEntryRel>
					commerceChannelAccountEntryRels = new ArrayList<>();

				commerceChannelAccountEntryRels.addAll(
					_commerceChannelAccountEntryRelService.
						getCommerceChannelAccountEntryRels(
							User.class.getName(), userId, 0,
							CommerceChannelAccountEntryRelConstants.TYPE_USER));

				long commerceChannelId = AccountEntryUtil.getCommerceChannelId(
					CommerceContextThreadLocal.get(),
					CommerceGroupThreadLocal.get());

				if (commerceChannelId > 0) {
					commerceChannelAccountEntryRels.addAll(
						_commerceChannelAccountEntryRelService.
							getCommerceChannelAccountEntryRels(
								User.class.getName(), userId, commerceChannelId,
								CommerceChannelAccountEntryRelConstants.
									TYPE_USER));
				}

				TermsFilter termsFilter = new TermsFilter(Field.ENTRY_CLASS_PK);

				for (CommerceChannelAccountEntryRel
						commerceChannelAccountEntryRel :
							commerceChannelAccountEntryRels) {

					termsFilter.addValue(
						String.valueOf(
							commerceChannelAccountEntryRel.
								getAccountEntryId()));
				}

				if (!termsFilter.isEmpty()) {
					booleanFilter.add(termsFilter, BooleanClauseOccur.SHOULD);
				}
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _addCommerceChannelIdsFilter(
			BooleanFilter booleanFilter, long userId)
		throws PortalException {

		long commerceChannelId = AccountEntryUtil.getCommerceChannelId(
			CommerceContextThreadLocal.get(), CommerceGroupThreadLocal.get());

		int count =
			_commerceChannelAccountEntryRelService.
				getCommerceChannelAccountEntryRelsCount(
					commerceChannelId, null,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		if ((commerceChannelId > 0) && (count > 0)) {
			TermsFilter accountUserIdsTermsFilter = new TermsFilter(
				"accountUserIds");

			accountUserIdsTermsFilter.addValue(String.valueOf(userId));

			booleanFilter.add(
				accountUserIdsTermsFilter, BooleanClauseOccur.MUST);

			TermsFilter termsFilter = new TermsFilter("commerceChannelIds");

			termsFilter.addValues(
				ArrayUtil.toStringArray(new long[] {commerceChannelId}));

			booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntrySearchPermissionFilterContributor.class);

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;

}