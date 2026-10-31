/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.commerce.price.list.service.base.CommercePriceListAccountRelServiceBaseImpl;
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
 * @author Ethan Bustad
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePriceListAccountRel"
	},
	service = AopService.class
)
public class CommercePriceListAccountRelServiceImpl
	extends CommercePriceListAccountRelServiceBaseImpl {

	@Override
	public CommercePriceListAccountRel addCommercePriceListAccountRel(
			long commercePriceListId, long commerceAccountId, int order,
			ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		return commercePriceListAccountRelLocalService.
			addCommercePriceListAccountRel(
				getUserId(), commercePriceListId, commerceAccountId, order,
				serviceContext);
	}

	@Override
	public void deleteCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws PortalException {

		CommercePriceListAccountRel commercePriceListAccountRel =
			commercePriceListAccountRelPersistence.findByPrimaryKey(
				commercePriceListAccountRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListAccountRel.getCommercePriceListId(),
			ActionKeys.UPDATE);

		commercePriceListAccountRelLocalService.
			deleteCommercePriceListAccountRel(commercePriceListAccountRel);
	}

	@Override
	public void deleteCommercePriceListAccountRelsByCommercePriceListId(
			long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		commercePriceListAccountRelLocalService.
			deleteCommercePriceListAccountRelsByCommercePriceListId(
				commercePriceListId);
	}

	@Override
	public CommercePriceListAccountRel fetchCommercePriceListAccountRel(
			long commercePriceListId, long commerceAccountId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListAccountRelPersistence.fetchByCAI_CPI(
			commerceAccountId, commercePriceListId);
	}

	@Override
	public CommercePriceListAccountRel getCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws PortalException {

		CommercePriceListAccountRel commercePriceListAccountRel =
			commercePriceListAccountRelPersistence.findByPrimaryKey(
				commercePriceListAccountRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListAccountRel.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commercePriceListAccountRel;
	}

	@Override
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
			long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListAccountRelPersistence.findByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceListAccountRel> orderByComparator)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRels(
				commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
		long commercePriceListId, String name, int start, int end) {

		return commercePriceListAccountRelFinder.findByCommercePriceListId(
			commercePriceListId, name, start, end, true);
	}

	@Override
	public int getCommercePriceListAccountRelsCount(long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListAccountRelPersistence.
			countByCommercePriceListId(commercePriceListId);
	}

	@Override
	public int getCommercePriceListAccountRelsCount(
		long commercePriceListId, String name) {

		return commercePriceListAccountRelFinder.countByCommercePriceListId(
			commercePriceListId, name, true);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}