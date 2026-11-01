/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.constants.CommerceAccountActionKeys;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.base.CommerceChannelAccountEntryRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceChannelAccountEntryRel"
	},
	service = AopService.class
)
public class CommerceChannelAccountEntryRelServiceImpl
	extends CommerceChannelAccountEntryRelServiceBaseImpl {

	@Override
	public CommerceChannelAccountEntryRel addCommerceChannelAccountEntryRel(
			long accountEntryId, String className, long classPK,
			long commerceChannelId, boolean overrideEligibility,
			double priority, int type)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS);

		return commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				getUserId(), accountEntryId, className, classPK,
				commerceChannelId, overrideEligibility, priority, type);
	}

	@Override
	public void deleteCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.findByPrimaryKey(
				commerceChannelAccountEntryRelId);

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(),
			commerceChannelAccountEntryRel.getAccountEntryId(),
			CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS);

		commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRel);
	}

	@Override
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.fetchByPrimaryKey(
				commerceChannelAccountEntryRelId);

		if (commerceChannelAccountEntryRel != null) {
			_accountEntryModelResourcePermission.check(
				getPermissionChecker(),
				commerceChannelAccountEntryRel.getAccountEntryId(),
				CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS);
		}

		return commerceChannelAccountEntryRel;
	}

	@Override
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
			long accountEntryId, long commerceChannelId, int type)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					accountEntryId, commerceChannelId, type);

		if (commerceChannelAccountEntryRel != null) {
			_accountEntryModelResourcePermission.check(
				getPermissionChecker(),
				commerceChannelAccountEntryRel.getAccountEntryId(),
				CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS);
		}

		return commerceChannelAccountEntryRel;
	}

	@Override
	public CommerceChannelAccountEntryRel getCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.findByPrimaryKey(
				commerceChannelAccountEntryRelId);

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(),
			commerceChannelAccountEntryRel.getAccountEntryId(),
			CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS);

		return commerceChannelAccountEntryRel;
	}

	@Override
	public List<CommerceChannelAccountEntryRel>
			getCommerceChannelAccountEntryRels(
				long accountEntryId, int type, int start, int end,
				OrderByComparator<CommerceChannelAccountEntryRel>
					orderByComparator)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS);

		return commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRels(
				accountEntryId, type, start, end, orderByComparator);
	}

	@Override
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long commerceChannelId, String name, int type, int start, int end) {

		return commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRels(
				commerceChannelId, name, type, start, end);
	}

	@Override
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			String className, long classPK, long commerceChannelId, int type) {

		return commerceChannelAccountEntryRelPersistence.findByC_C_C_T(
			_classNameLocalService.getClassNameId(className), classPK,
			commerceChannelId, type);
	}

	@Override
	public int getCommerceChannelAccountEntryRelsCount(
			long accountEntryId, int type)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS);

		return commerceChannelAccountEntryRelPersistence.countByA_T(
			accountEntryId, type);
	}

	@Override
	public int getCommerceChannelAccountEntryRelsCount(
		long commerceChannelId, String name, int type) {

		return commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRelsCount(
				commerceChannelId, name, type);
	}

	@Override
	public CommerceChannelAccountEntryRel updateCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId, long commerceChannelId,
			long classPK, boolean overrideEligibility, double priority)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.findByPrimaryKey(
				commerceChannelAccountEntryRelId);

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(),
			commerceChannelAccountEntryRel.getAccountEntryId(),
			CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS);

		return commerceChannelAccountEntryRelLocalService.
			updateCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRel.
					getCommerceChannelAccountEntryRelId(),
				commerceChannelId, classPK, overrideEligibility, priority);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<CommerceCatalog>
		_accountEntryModelResourcePermission;

	@Reference
	private ClassNameLocalService _classNameLocalService;

}