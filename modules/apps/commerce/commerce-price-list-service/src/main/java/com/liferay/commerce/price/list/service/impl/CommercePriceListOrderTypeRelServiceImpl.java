/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.service.base.CommercePriceListOrderTypeRelServiceBaseImpl;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePriceListOrderTypeRel"
	},
	service = AopService.class
)
public class CommercePriceListOrderTypeRelServiceImpl
	extends CommercePriceListOrderTypeRelServiceBaseImpl {

	@Override
	public CommercePriceListOrderTypeRel addCommercePriceListOrderTypeRel(
			long commercePriceListId, long commerceOrderTypeId, int priority,
			ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		return commercePriceListOrderTypeRelLocalService.
			addCommercePriceListOrderTypeRel(
				getUserId(), commercePriceListId, commerceOrderTypeId, priority,
				serviceContext);
	}

	@Override
	public void deleteCommercePriceListOrderTypeRel(
			long commercePriceListOrderTypeRelId)
		throws PortalException {

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			commercePriceListOrderTypeRelLocalService.
				getCommercePriceListOrderTypeRel(
					commercePriceListOrderTypeRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListOrderTypeRel.getCommercePriceListId(),
			ActionKeys.UPDATE);

		commercePriceListOrderTypeRelLocalService.
			deleteCommercePriceListOrderTypeRel(commercePriceListOrderTypeRel);
	}

	@Override
	public CommercePriceListOrderTypeRel fetchCommercePriceListOrderTypeRel(
			long commercePriceListId, long commerceOrderTypeId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListOrderTypeRelPersistence.fetchByCPI_COTI(
			commercePriceListId, commerceOrderTypeId);
	}

	@Override
	public CommercePriceListOrderTypeRel getCommercePriceListOrderTypeRel(
			long commercePriceListOrderTypeRelId)
		throws PortalException {

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			commercePriceListOrderTypeRelLocalService.
				getCommercePriceListOrderTypeRel(
					commercePriceListOrderTypeRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListOrderTypeRel.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commercePriceListOrderTypeRel;
	}

	@Override
	public List<CommercePriceListOrderTypeRel>
			getCommercePriceListOrderTypeRels(
				long commercePriceListId, String name, int start, int end,
				OrderByComparator<CommercePriceListOrderTypeRel>
					orderByComparator)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRels(
				commercePriceListId, name, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceListOrderTypeRelsCount(
			long commercePriceListId, String name)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRelsCount(commercePriceListId, name);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}