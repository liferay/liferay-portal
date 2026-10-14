/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.impl;

import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountUsageEntry;
import com.liferay.commerce.discount.service.base.CommerceDiscountUsageEntryLocalServiceBaseImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.discount.model.CommerceDiscountUsageEntry",
	service = AopService.class
)
public class CommerceDiscountUsageEntryLocalServiceImpl
	extends CommerceDiscountUsageEntryLocalServiceBaseImpl {

	@Override
	public CommerceDiscountUsageEntry addCommerceDiscountUsageEntry(
			long commerceAccountId, long commerceOrderId,
			long commerceDiscountId, ServiceContext serviceContext)
		throws PortalException {

		long userId = serviceContext.getUserId();

		User user = _userLocalService.getUser(userId);

		if (user.isGuestUser()) {
			userId = 0;
		}

		long commerceDiscountUsageEntryId = counterLocalService.increment();

		CommerceDiscountUsageEntry commerceDiscountUsageEntry =
			commerceDiscountUsageEntryPersistence.create(
				commerceDiscountUsageEntryId);

		commerceDiscountUsageEntry.setCompanyId(user.getCompanyId());
		commerceDiscountUsageEntry.setUserId(userId);
		commerceDiscountUsageEntry.setUserName(user.getFullName());
		commerceDiscountUsageEntry.setCommerceAccountId(commerceAccountId);
		commerceDiscountUsageEntry.setCommerceOrderId(commerceOrderId);
		commerceDiscountUsageEntry.setCommerceDiscountId(commerceDiscountId);

		return commerceDiscountUsageEntryPersistence.update(
			commerceDiscountUsageEntry);
	}

	@Override
	public void deleteCommerceUsageEntry(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId) {

		CommerceDiscountUsageEntry commerceDiscountUsageEntry =
			commerceDiscountUsageEntryPersistence.fetchByCAI_COI_CDI_First(
				commerceAccountId, commerceOrderId, commerceDiscountId, null);

		if (commerceDiscountUsageEntry != null) {
			commerceDiscountUsageEntryPersistence.remove(
				commerceDiscountUsageEntry);
		}
	}

	@Override
	public void deleteCommerceUsageEntryByDiscountId(long commerceDiscountId) {
		commerceDiscountUsageEntryPersistence.removeByCommerceDiscountId(
			commerceDiscountId);
	}

	@Override
	public int getCommerceDiscountUsageEntriesCount(long commerceDiscountId) {
		return commerceDiscountUsageEntryPersistence.countByCommerceDiscountId(
			commerceDiscountId);
	}

	@Override
	public int getCommerceDiscountUsageEntriesCount(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId) {

		return commerceDiscountUsageEntryPersistence.countByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId);
	}

	@Override
	public int getCommerceDiscountUsageEntriesCountByAccountId(
		long commerceAccountId, long commerceDiscountId) {

		return commerceDiscountUsageEntryPersistence.countByCAI_CDI(
			commerceAccountId, commerceDiscountId);
	}

	@Override
	public boolean validateDiscountLimitationUsage(
			long commerceAccountId, long commerceDiscountId)
		throws PortalException {

		CommerceDiscount commerceDiscount =
			_commerceDiscountPersistence.findByPrimaryKey(commerceDiscountId);

		if (Objects.equals(
				commerceDiscount.getLimitationType(),
				CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED)) {

			return true;
		}

		int limitationTimes = commerceDiscount.getLimitationTimes();

		if (Objects.equals(
				commerceDiscount.getLimitationType(),
				CommerceDiscountConstants.LIMITATION_TYPE_LIMITED)) {

			int commerceDiscountUsageEntriesCount =
				getCommerceDiscountUsageEntriesCount(commerceDiscountId);

			if (commerceDiscountUsageEntriesCount < limitationTimes) {
				return true;
			}

			return false;
		}

		int limitationTimesPerAccount =
			commerceDiscount.getLimitationTimesPerAccount();

		if (Objects.equals(
				commerceDiscount.getLimitationType(),
				CommerceDiscountConstants.
					LIMITATION_TYPE_LIMITED_FOR_ACCOUNTS)) {

			int commerceDiscountUsageEntriesCount =
				getCommerceDiscountUsageEntriesCountByAccountId(
					commerceAccountId, commerceDiscountId);

			if (commerceDiscountUsageEntriesCount < limitationTimesPerAccount) {
				return true;
			}

			return false;
		}

		int commerceDiscountUsageEntriesTotalCount =
			getCommerceDiscountUsageEntriesCount(commerceDiscountId);

		if (commerceDiscountUsageEntriesTotalCount >= limitationTimes) {
			return false;
		}

		int commerceDiscountUsageEntriesUserCount =
			getCommerceDiscountUsageEntriesCountByAccountId(
				commerceAccountId, commerceDiscountId);

		if (commerceDiscountUsageEntriesUserCount >=
				limitationTimesPerAccount) {

			return false;
		}

		return true;
	}

	@Reference
	private CommerceDiscountPersistence _commerceDiscountPersistence;

	@Reference
	private UserLocalService _userLocalService;

}