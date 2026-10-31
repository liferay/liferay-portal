/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.impl;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountAccountRel;
import com.liferay.commerce.discount.service.base.CommerceDiscountAccountRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 * @see CommerceDiscountAccountRelServiceBaseImpl
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceDiscountAccountRel"
	},
	service = AopService.class
)
public class CommerceDiscountAccountRelServiceImpl
	extends CommerceDiscountAccountRelServiceBaseImpl {

	@Override
	public CommerceDiscountAccountRel addCommerceDiscountAccountRel(
			long commerceDiscountId, long commerceAccountId,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountAccountRelLocalService.
			addCommerceDiscountAccountRel(
				getUserId(), commerceDiscountId, commerceAccountId,
				serviceContext);
	}

	@Override
	public void deleteCommerceDiscountAccountRel(
			long commerceDiscountAccountRelId)
		throws PortalException {

		CommerceDiscountAccountRel commerceDiscountAccountRel =
			commerceDiscountAccountRelPersistence.findByPrimaryKey(
				commerceDiscountAccountRelId);

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountAccountRel.getCommerceDiscountId(),
			ActionKeys.UPDATE);

		commerceDiscountAccountRelLocalService.deleteCommerceDiscountAccountRel(
			commerceDiscountAccountRel);
	}

	@Override
	public void deleteCommerceDiscountAccountRelsByCommerceDiscountId(
			long commerceDiscountId)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		commerceDiscountAccountRelLocalService.
			deleteCommerceDiscountAccountRelsByCommerceDiscountId(
				commerceDiscountId);
	}

	@Override
	public CommerceDiscountAccountRel fetchCommerceDiscountAccountRel(
			long commerceAccountId, long commerceDiscountId)
		throws PortalException {

		CommerceDiscountAccountRel commerceDiscountAccountRel =
			commerceDiscountAccountRelPersistence.fetchByCAI_CDI(
				commerceAccountId, commerceDiscountId);

		if (commerceDiscountAccountRel != null) {
			_commerceDiscountResourcePermission.check(
				getPermissionChecker(),
				commerceDiscountAccountRel.getCommerceDiscountId(),
				ActionKeys.UPDATE);
		}

		return commerceDiscountAccountRel;
	}

	@Override
	public CommerceDiscountAccountRel getCommerceDiscountAccountRel(
			long commerceDiscountAccountRelId)
		throws PortalException {

		CommerceDiscountAccountRel commerceDiscountAccountRel =
			commerceDiscountAccountRelPersistence.findByPrimaryKey(
				commerceDiscountAccountRelId);

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountAccountRel.getCommerceDiscountId(),
			ActionKeys.UPDATE);

		return commerceDiscountAccountRel;
	}

	@Override
	public List<CommerceDiscountAccountRel> getCommerceDiscountAccountRels(
			long commerceDiscountId, int start, int end,
			OrderByComparator<CommerceDiscountAccountRel> orderByComparator)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountAccountRelPersistence.findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceDiscountAccountRel> getCommerceDiscountAccountRels(
		long commerceDiscountId, String name, int start, int end) {

		return commerceDiscountAccountRelFinder.findByCommerceDiscountId(
			commerceDiscountId, name, start, end, true);
	}

	@Override
	public int getCommerceDiscountAccountRelsCount(long commerceDiscountId)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountAccountRelPersistence.countByCommerceDiscountId(
			commerceDiscountId);
	}

	@Override
	public int getCommerceDiscountAccountRelsCount(
		long commerceDiscountId, String name) {

		return commerceDiscountAccountRelFinder.countByCommerceDiscountId(
			commerceDiscountId, name, true);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscount)"
	)
	private ModelResourcePermission<CommerceDiscount>
		_commerceDiscountResourcePermission;

}