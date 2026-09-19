/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.exception.DuplicateCommerceShippingOptionAccountEntryRelException;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.commerce.service.base.CommerceShippingOptionAccountEntryRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel",
	service = AopService.class
)
public class CommerceShippingOptionAccountEntryRelLocalServiceImpl
	extends CommerceShippingOptionAccountEntryRelLocalServiceBaseImpl {

	@Override
	public CommerceShippingOptionAccountEntryRel
			addCommerceShippingOptionAccountEntryRel(
				long userId, long accountEntryId, long commerceChannelId,
				String commerceShippingMethodKey,
				String commerceShippingOptionKey)
		throws PortalException {

		_validate(accountEntryId, commerceChannelId);

		long commerceShippingOptionAccountEntryRelId =
			counterLocalService.increment();

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel =
				commerceShippingOptionAccountEntryRelPersistence.create(
					commerceShippingOptionAccountEntryRelId);

		User user = _userLocalService.getUser(userId);

		commerceShippingOptionAccountEntryRel.setCompanyId(user.getCompanyId());
		commerceShippingOptionAccountEntryRel.setUserId(user.getUserId());
		commerceShippingOptionAccountEntryRel.setUserName(user.getFullName());

		commerceShippingOptionAccountEntryRel.setAccountEntryId(accountEntryId);
		commerceShippingOptionAccountEntryRel.setCommerceChannelId(
			commerceChannelId);
		commerceShippingOptionAccountEntryRel.setCommerceShippingMethodKey(
			commerceShippingMethodKey);
		commerceShippingOptionAccountEntryRel.setCommerceShippingOptionKey(
			commerceShippingOptionKey);

		return commerceShippingOptionAccountEntryRelPersistence.update(
			commerceShippingOptionAccountEntryRel);
	}

	@Override
	public void deleteCommerceShippingOptionAccountEntryRelsByAccountEntryId(
		long accountEntryId) {

		commerceShippingOptionAccountEntryRelPersistence.removeByAccountEntryId(
			accountEntryId);
	}

	@Override
	public void deleteCommerceShippingOptionAccountEntryRelsByCSFixedOptionKey(
		String commerceShippingFixedOptionKey) {

		commerceShippingOptionAccountEntryRelPersistence.
			removeByCommerceShippingOptionKey(commerceShippingFixedOptionKey);
	}

	@Override
	public void deleteCommerceShippingOptionAccountEntryRelsByCommerceChannelId(
		long commerceChannelId) {

		commerceShippingOptionAccountEntryRelPersistence.
			removeByCommerceChannelId(commerceChannelId);
	}

	@Override
	public CommerceShippingOptionAccountEntryRel
		fetchCommerceShippingOptionAccountEntryRel(
			long accountEntryId, long commerceChannelId) {

		return commerceShippingOptionAccountEntryRelPersistence.fetchByA_C(
			accountEntryId, commerceChannelId);
	}

	@Override
	public List<CommerceShippingOptionAccountEntryRel>
		getCommerceShippingOptionAccountEntryRels(long accountEntryId) {

		return commerceShippingOptionAccountEntryRelPersistence.
			findByAccountEntryId(accountEntryId);
	}

	@Override
	public int getCommerceShippingOptionAccountEntryRelsCount(
		long accountEntryId) {

		return commerceShippingOptionAccountEntryRelPersistence.
			countByAccountEntryId(accountEntryId);
	}

	@Override
	public CommerceShippingOptionAccountEntryRel
			updateCommerceShippingOptionAccountEntryRel(
				long commerceShippingOptionAccountEntryRelId,
				String commerceShippingMethodKey,
				String commerceShippingOptionKey)
		throws PortalException {

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel =
				commerceShippingOptionAccountEntryRelPersistence.
					findByPrimaryKey(commerceShippingOptionAccountEntryRelId);

		commerceShippingOptionAccountEntryRel.setCommerceShippingMethodKey(
			commerceShippingMethodKey);
		commerceShippingOptionAccountEntryRel.setCommerceShippingOptionKey(
			commerceShippingOptionKey);

		return commerceShippingOptionAccountEntryRelPersistence.update(
			commerceShippingOptionAccountEntryRel);
	}

	private void _validate(long accountEntryId, long commerceChannelId)
		throws PortalException {

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel =
				commerceShippingOptionAccountEntryRelPersistence.fetchByA_C(
					accountEntryId, commerceChannelId);

		if (commerceShippingOptionAccountEntryRel != null) {
			throw new DuplicateCommerceShippingOptionAccountEntryRelException();
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}