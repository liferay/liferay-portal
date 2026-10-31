/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.commerce.service.base.CommerceShippingOptionAccountEntryRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceShippingOptionAccountEntryRel"
	},
	service = AopService.class
)
public class CommerceShippingOptionAccountEntryRelServiceImpl
	extends CommerceShippingOptionAccountEntryRelServiceBaseImpl {

	@Override
	public CommerceShippingOptionAccountEntryRel
			addCommerceShippingOptionAccountEntryRel(
				long accountEntryId, long commerceChannelId,
				String commerceShippingMethodKey,
				String commerceShippingOptionKey)
		throws PortalException {

		_checkAccountEntry(accountEntryId, ActionKeys.UPDATE);

		return commerceShippingOptionAccountEntryRelLocalService.
			addCommerceShippingOptionAccountEntryRel(
				getUserId(), accountEntryId, commerceChannelId,
				commerceShippingMethodKey, commerceShippingOptionKey);
	}

	@Override
	public void deleteCommerceShippingOptionAccountEntryRel(
			long commerceShippingOptionAccountEntryRelId)
		throws PortalException {

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel =
				commerceShippingOptionAccountEntryRelPersistence.
					findByPrimaryKey(commerceShippingOptionAccountEntryRelId);

		_checkAccountEntry(
			commerceShippingOptionAccountEntryRel.getAccountEntryId(),
			ActionKeys.UPDATE);

		commerceShippingOptionAccountEntryRelLocalService.
			deleteCommerceShippingOptionAccountEntryRel(
				commerceShippingOptionAccountEntryRel);
	}

	@Override
	public CommerceShippingOptionAccountEntryRel
			fetchCommerceShippingOptionAccountEntryRel(
				long accountEntryId, long commerceChannelId)
		throws PortalException {

		_checkAccountEntry(accountEntryId, ActionKeys.VIEW);

		return commerceShippingOptionAccountEntryRelPersistence.fetchByA_C(
			accountEntryId, commerceChannelId);
	}

	@Override
	public CommerceShippingOptionAccountEntryRel
			getCommerceShippingOptionAccountEntryRel(
				long commerceShippingOptionAccountEntryRelId)
		throws PortalException {

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel =
				commerceShippingOptionAccountEntryRelPersistence.
					findByPrimaryKey(commerceShippingOptionAccountEntryRelId);

		_checkAccountEntry(
			commerceShippingOptionAccountEntryRel.getAccountEntryId(),
			ActionKeys.VIEW);

		return commerceShippingOptionAccountEntryRel;
	}

	@Override
	public List<CommerceShippingOptionAccountEntryRel>
			getCommerceShippingOptionAccountEntryRels(long accountEntryId)
		throws Exception {

		_checkAccountEntry(accountEntryId, ActionKeys.VIEW);

		return commerceShippingOptionAccountEntryRelPersistence.
			findByAccountEntryId(accountEntryId);
	}

	@Override
	public int getCommerceShippingOptionAccountEntryRelsCount(
			long accountEntryId)
		throws Exception {

		_checkAccountEntry(accountEntryId, ActionKeys.VIEW);

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

		_checkAccountEntry(
			commerceShippingOptionAccountEntryRel.getAccountEntryId(),
			ActionKeys.UPDATE);

		return commerceShippingOptionAccountEntryRelLocalService.
			updateCommerceShippingOptionAccountEntryRel(
				commerceShippingOptionAccountEntryRelId,
				commerceShippingMethodKey, commerceShippingOptionKey);
	}

	private void _checkAccountEntry(long accountEntryId, String actionId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId, actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

}