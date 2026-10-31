/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.base.CommerceSubscriptionEntryServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceSubscriptionEntry"
	},
	service = AopService.class
)
public class CommerceSubscriptionEntryServiceImpl
	extends CommerceSubscriptionEntryServiceBaseImpl {

	@Override
	public void deleteCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId)
		throws PortalException {

		_checkPortletResourcePermission(commerceSubscriptionEntryId);

		commerceSubscriptionEntryLocalService.deleteCommerceSubscriptionEntry(
			commerceSubscriptionEntryId);
	}

	@Override
	public CommerceSubscriptionEntry fetchCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId)
		throws PortalException {

		_checkPortletResourcePermission(commerceSubscriptionEntryId);

		return commerceSubscriptionEntryPersistence.fetchByPrimaryKey(
			commerceSubscriptionEntryId);
	}

	@Override
	public List<CommerceSubscriptionEntry> getCommerceSubscriptionEntries(
			long companyId, long groupId, long userId, int start, int end,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator)
		throws PortalException {

		_checkCompanyId(companyId);

		if (userId != getUserId()) {
			_portletResourcePermission.check(
				getPermissionChecker(), null,
				CommerceActionKeys.MANAGE_COMMERCE_SUBSCRIPTIONS);
		}

		return commerceSubscriptionEntryPersistence.findByG_C_U(
			groupId, companyId, userId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceSubscriptionEntriesCount(
			long companyId, long groupId, long userId)
		throws PortalException {

		_checkCompanyId(companyId);

		if (userId != getUserId()) {
			_portletResourcePermission.check(
				getPermissionChecker(), null,
				CommerceActionKeys.MANAGE_COMMERCE_SUBSCRIPTIONS);
		}

		return commerceSubscriptionEntryPersistence.countByG_C_U(
			groupId, companyId, userId);
	}

	@Override
	public BaseModelSearchResult<CommerceSubscriptionEntry>
			searchCommerceSubscriptionEntries(
				long companyId, Long maxSubscriptionCycles,
				Integer subscriptionStatus, String keywords, int start, int end,
				Sort sort)
		throws PortalException {

		_checkCompanyId(companyId);

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceActionKeys.MANAGE_COMMERCE_SUBSCRIPTIONS);

		return commerceSubscriptionEntryLocalService.
			searchCommerceSubscriptionEntries(
				companyId,
				TransformUtil.transformToLongArray(
					_commerceChannelLocalService.search(companyId),
					CommerceChannel::getGroupId),
				maxSubscriptionCycles, subscriptionStatus, keywords, start, end,
				sort);
	}

	@Override
	public CommerceSubscriptionEntry updateCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId, int subscriptionLength,
			String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, int subscriptionStatus,
			int nextIterationDateMonth, int nextIterationDateDay,
			int nextIterationDateYear, int nextIterationDateHour,
			int nextIterationDateMinute, int deliverySubscriptionLength,
			String deliverySubscriptionType,
			UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, int deliverySubscriptionStatus,
			int deliveryNextIterationDateMonth,
			int deliveryNextIterationDateDay, int deliveryNextIterationDateYear,
			int deliveryNextIterationDateHour,
			int deliveryNextIterationDateMinute)
		throws PortalException {

		_checkPortletResourcePermission(commerceSubscriptionEntryId);

		return commerceSubscriptionEntryLocalService.
			updateCommerceSubscriptionEntry(
				commerceSubscriptionEntryId, subscriptionLength,
				subscriptionType, subscriptionTypeSettingsUnicodeProperties,
				maxSubscriptionCycles, subscriptionStatus,
				nextIterationDateMonth, nextIterationDateDay,
				nextIterationDateYear, nextIterationDateHour,
				nextIterationDateMinute, deliverySubscriptionLength,
				deliverySubscriptionType,
				deliverySubscriptionTypeSettingsUnicodeProperties,
				deliveryMaxSubscriptionCycles, deliverySubscriptionStatus,
				deliveryNextIterationDateMonth, deliveryNextIterationDateDay,
				deliveryNextIterationDateYear, deliveryNextIterationDateHour,
				deliveryNextIterationDateMinute);
	}

	private void _checkCompanyId(long companyId) throws PortalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		if (companyId != permissionChecker.getCompanyId()) {
			throw new PrincipalException();
		}
	}

	private void _checkPortletResourcePermission(
			long commerceSubscriptionEntryId)
		throws PortalException {

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			commerceSubscriptionEntryPersistence.fetchByPrimaryKey(
				commerceSubscriptionEntryId);

		if (commerceSubscriptionEntry == null) {
			return;
		}

		_portletResourcePermission.check(
			getPermissionChecker(), commerceSubscriptionEntry.getGroupId(),
			CommerceActionKeys.MANAGE_COMMERCE_SUBSCRIPTIONS);
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(resource.name=" + CommerceConstants.RESOURCE_NAME_COMMERCE_SUBSCRIPTION + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}