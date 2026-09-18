/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.impl;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.service.base.CommerceDiscountOrderTypeRelServiceBaseImpl;
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
 * @author Marco Leo
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceDiscountOrderTypeRel"
	},
	service = AopService.class
)
public class CommerceDiscountOrderTypeRelServiceImpl
	extends CommerceDiscountOrderTypeRelServiceBaseImpl {

	@Override
	public CommerceDiscountOrderTypeRel addCommerceDiscountOrderTypeRel(
			long commerceDiscountId, long commerceOrderTypeId, int priority,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceDiscountModelResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountOrderTypeRelLocalService.
			addCommerceDiscountOrderTypeRel(
				getUserId(), commerceDiscountId, commerceOrderTypeId, priority,
				serviceContext);
	}

	@Override
	public void deleteCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelLocalService.
				getCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRelId);

		_commerceDiscountModelResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountOrderTypeRel.getCommerceDiscountId(),
			ActionKeys.UPDATE);

		commerceDiscountOrderTypeRelLocalService.
			deleteCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRel);
	}

	@Override
	public CommerceDiscountOrderTypeRel fetchCommerceDiscountOrderTypeRel(
			long commerceDiscountId, long commerceOrderTypeId)
		throws PortalException {

		_commerceDiscountModelResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.VIEW);

		return commerceDiscountOrderTypeRelLocalService.
			fetchCommerceDiscountOrderTypeRel(
				commerceDiscountId, commerceOrderTypeId);
	}

	@Override
	public CommerceDiscountOrderTypeRel getCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelLocalService.
				getCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRelId);

		_commerceDiscountModelResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountOrderTypeRel.getCommerceDiscountId(),
			ActionKeys.VIEW);

		return commerceDiscountOrderTypeRel;
	}

	@Override
	public List<CommerceDiscountOrderTypeRel> getCommerceDiscountOrderTypeRels(
			long commerceDiscountId, String name, int start, int end,
			OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator)
		throws PortalException {

		_commerceDiscountModelResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.VIEW);

		return commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRels(
				commerceDiscountId, name, start, end, orderByComparator);
	}

	@Override
	public int getCommerceDiscountOrderTypeRelsCount(
			long commerceDiscountId, String name)
		throws PortalException {

		_commerceDiscountModelResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.VIEW);

		return commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRelsCount(commerceDiscountId, name);
	}

	@Override
	public CommerceDiscountOrderTypeRel updatePriority(
			long commerceDiscountOrderTypeRelId, int priority)
		throws PortalException {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelLocalService.
				getCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRelId);

		_commerceDiscountModelResourcePermission.check(
			getPermissionChecker(),
			commerceDiscountOrderTypeRel.getCommerceDiscountId(),
			ActionKeys.UPDATE);

		return commerceDiscountOrderTypeRelLocalService.updatePriority(
			commerceDiscountOrderTypeRelId, priority);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscount)"
	)
	private ModelResourcePermission<CommerceDiscount>
		_commerceDiscountModelResourcePermission;

}