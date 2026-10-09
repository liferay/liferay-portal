/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.permission;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = SearchPermissionFilterContributor.class)
public class CommerceOrderAttachmentSearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!className.equals(CommerceOrderAttachment.class.getName())) {
			return;
		}

		try {
			List<AccountEntry> accountEntries =
				_accountEntryLocalService.getUserAccountEntries(
					userId, AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
					StringPool.BLANK,
					new String[] {
						AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
						AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON
					},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			if (!_portletResourcePermission.contains(
					permissionChecker, 0L,
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS)) {

				TermsFilter termsFilter = new TermsFilter(
					"accountEntryGroupId");

				for (AccountEntry accountEntry : accountEntries) {
					if (!_portletResourcePermission.contains(
							permissionChecker,
							accountEntry.getAccountEntryGroupId(),
							CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS) &&
						!_portletResourcePermission.contains(
							permissionChecker,
							accountEntry.getAccountEntryGroupId(),
							CommerceOrderActionKeys.VIEW_COMMERCE_ORDERS) &&
						!_portletResourcePermission.contains(
							permissionChecker,
							accountEntry.getAccountEntryGroupId(),
							CommerceOrderActionKeys.
								VIEW_OPEN_COMMERCE_ORDERS)) {

						continue;
					}

					termsFilter.addValue(
						String.valueOf(accountEntry.getAccountEntryGroupId()));
				}

				if (termsFilter.isEmpty()) {
					booleanFilter.add(
						new TermFilter("accountEntryGroupId", "-1"),
						BooleanClauseOccur.MUST);

					return;
				}

				booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
			}

			if (_portletResourcePermission.contains(
					permissionChecker, 0L,
					CommerceOrderActionKeys.
						VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS)) {

				return;
			}

			BooleanFilter restrictedBooleanFilter = new BooleanFilter();

			restrictedBooleanFilter.add(
				new TermFilter("restricted", "false"),
				BooleanClauseOccur.SHOULD);

			TermsFilter termsFilter = new TermsFilter("accountEntryGroupId");

			for (AccountEntry accountEntry : accountEntries) {
				if (!_portletResourcePermission.contains(
						permissionChecker,
						accountEntry.getAccountEntryGroupId(),
						CommerceOrderActionKeys.
							VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS)) {

					continue;
				}

				termsFilter.addValue(
					String.valueOf(accountEntry.getAccountEntryGroupId()));
			}

			if (!termsFilter.isEmpty()) {
				restrictedBooleanFilter.add(
					termsFilter, BooleanClauseOccur.SHOULD);
			}

			booleanFilter.add(restrictedBooleanFilter, BooleanClauseOccur.MUST);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderAttachmentSearchPermissionFilterContributor.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}