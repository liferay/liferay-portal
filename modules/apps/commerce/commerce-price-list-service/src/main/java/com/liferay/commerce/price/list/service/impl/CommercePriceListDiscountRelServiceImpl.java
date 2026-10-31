/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.commerce.price.list.service.base.CommercePriceListDiscountRelServiceBaseImpl;
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
 * @see CommercePriceListDiscountRelServiceBaseImpl
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePriceListDiscountRel"
	},
	service = AopService.class
)
public class CommercePriceListDiscountRelServiceImpl
	extends CommercePriceListDiscountRelServiceBaseImpl {

	@Override
	public CommercePriceListDiscountRel addCommercePriceListDiscountRel(
			long commercePriceListId, long commerceDiscountId, int order,
			ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		return commercePriceListDiscountRelLocalService.
			addCommercePriceListDiscountRel(
				getUserId(), commercePriceListId, commerceDiscountId, order,
				serviceContext);
	}

	@Override
	public void deleteCommercePriceListDiscountRel(
			long commercePriceListDiscountRelId)
		throws PortalException {

		CommercePriceListDiscountRel commercePriceListDiscountRel =
			commercePriceListDiscountRelPersistence.findByPrimaryKey(
				commercePriceListDiscountRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListDiscountRel.getCommercePriceListId(),
			ActionKeys.UPDATE);

		commercePriceListDiscountRelLocalService.
			deleteCommercePriceListDiscountRel(commercePriceListDiscountRel);
	}

	@Override
	public CommercePriceListDiscountRel fetchCommercePriceListDiscountRel(
			long commercePriceListId, long commerceDiscountId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListDiscountRelPersistence.fetchByCDI_CPI(
			commercePriceListId, commerceDiscountId);
	}

	@Override
	public CommercePriceListDiscountRel getCommercePriceListDiscountRel(
			long commercePriceListDiscountRelId)
		throws PortalException {

		CommercePriceListDiscountRel commercePriceListDiscountRel =
			commercePriceListDiscountRelPersistence.findByPrimaryKey(
				commercePriceListDiscountRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListDiscountRel.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commercePriceListDiscountRel;
	}

	@Override
	public List<CommercePriceListDiscountRel> getCommercePriceListDiscountRels(
			long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListDiscountRelPersistence.
			findByCommercePriceListId(commercePriceListId);
	}

	@Override
	public List<CommercePriceListDiscountRel> getCommercePriceListDiscountRels(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceListDiscountRel> orderByComparator)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListDiscountRelPersistence.
			findByCommercePriceListId(
				commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceListDiscountRelsCount(long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListDiscountRelPersistence.
			countByCommercePriceListId(commercePriceListId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}