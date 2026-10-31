/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRel;
import com.liferay.commerce.price.list.service.base.CommercePriceListCommerceAccountGroupRelServiceBaseImpl;
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
		"json.web.service.context.path=CommercePriceListCommerceAccountGroupRel"
	},
	service = AopService.class
)
public class CommercePriceListCommerceAccountGroupRelServiceImpl
	extends CommercePriceListCommerceAccountGroupRelServiceBaseImpl {

	@Override
	public CommercePriceListCommerceAccountGroupRel
			addCommercePriceListCommerceAccountGroupRel(
				long commercePriceListId, long commerceAccountGroupId,
				int order, ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		return commercePriceListCommerceAccountGroupRelLocalService.
			addCommercePriceListCommerceAccountGroupRel(
				getUserId(), commercePriceListId, commerceAccountGroupId, order,
				serviceContext);
	}

	@Override
	public void deleteCommercePriceListAccountGroupRelsByCommercePriceListId(
			long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		commercePriceListCommerceAccountGroupRelLocalService.
			deleteCommercePriceListAccountGroupRelsByCommercePriceListId(
				commercePriceListId);
	}

	@Override
	public void deleteCommercePriceListCommerceAccountGroupRel(
			long commercePriceListCommerceAccountGroupRelId)
		throws PortalException {

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel =
				commercePriceListCommerceAccountGroupRelPersistence.
					findByPrimaryKey(
						commercePriceListCommerceAccountGroupRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListCommerceAccountGroupRel.getCommercePriceListId(),
			ActionKeys.UPDATE);

		commercePriceListCommerceAccountGroupRelLocalService.
			deleteCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRel);
	}

	@Override
	public CommercePriceListCommerceAccountGroupRel
			fetchCommercePriceListCommerceAccountGroupRel(
				long commercePriceListId, long commerceAccountGroupId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListCommerceAccountGroupRelPersistence.
			fetchByCAGI_CPI(commercePriceListId, commerceAccountGroupId);
	}

	@Override
	public CommercePriceListCommerceAccountGroupRel
			getCommercePriceListCommerceAccountGroupRel(
				long commercePriceListCommerceAccoungGroupRelId)
		throws PortalException {

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel =
				commercePriceListCommerceAccountGroupRelPersistence.
					findByPrimaryKey(
						commercePriceListCommerceAccoungGroupRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListCommerceAccountGroupRel.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commercePriceListCommerceAccountGroupRel;
	}

	@Override
	public List<CommercePriceListCommerceAccountGroupRel>
			getCommercePriceListCommerceAccountGroupRels(
				long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListCommerceAccountGroupRelPersistence.
			findByCommercePriceListId(commercePriceListId);
	}

	@Override
	public List<CommercePriceListCommerceAccountGroupRel>
			getCommercePriceListCommerceAccountGroupRels(
				long commercePriceListId, int start, int end,
				OrderByComparator<CommercePriceListCommerceAccountGroupRel>
					orderByComparator)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRels(
				commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public List<CommercePriceListCommerceAccountGroupRel>
		getCommercePriceListCommerceAccountGroupRels(
			long commercePriceListId, String name, int start, int end) {

		return commercePriceListCommerceAccountGroupRelFinder.
			findByCommercePriceListId(commercePriceListId, name, start, end);
	}

	@Override
	public int getCommercePriceListCommerceAccountGroupRelsCount(
			long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListCommerceAccountGroupRelPersistence.
			countByCommercePriceListId(commercePriceListId);
	}

	@Override
	public int getCommercePriceListCommerceAccountGroupRelsCount(
		long commercePriceListId, String name) {

		return commercePriceListCommerceAccountGroupRelFinder.
			countByCommercePriceListId(commercePriceListId, name);
	}

	@Override
	public CommercePriceListCommerceAccountGroupRel
			updateCommercePriceListCommerceAccountGroupRel(
				long commercePriceListCommerceAccountGroupRelId, int order,
				ServiceContext serviceContext)
		throws PortalException {

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel =
				commercePriceListCommerceAccountGroupRelPersistence.
					findByPrimaryKey(
						commercePriceListCommerceAccountGroupRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListCommerceAccountGroupRel.getCommercePriceListId(),
			ActionKeys.UPDATE);

		return commercePriceListCommerceAccountGroupRelLocalService.
			updateCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRelId, order,
				serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}