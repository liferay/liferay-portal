/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.commerce.price.list.service.base.CommercePriceListChannelRelServiceBaseImpl;
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
 * @see CommercePriceListChannelRelServiceBaseImpl
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePriceListChannelRel"
	},
	service = AopService.class
)
public class CommercePriceListChannelRelServiceImpl
	extends CommercePriceListChannelRelServiceBaseImpl {

	@Override
	public CommercePriceListChannelRel addCommercePriceListChannelRel(
			long commercePriceListId, long commerceChannelId, int order,
			ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		return commercePriceListChannelRelLocalService.
			addCommercePriceListChannelRel(
				getUserId(), commercePriceListId, commerceChannelId, order,
				serviceContext);
	}

	@Override
	public void deleteCommercePriceListChannelRel(
			long commercePriceListChannelRelId)
		throws PortalException {

		CommercePriceListChannelRel commercePriceListChannelRel =
			commercePriceListChannelRelPersistence.findByPrimaryKey(
				commercePriceListChannelRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListChannelRel.getCommercePriceListId(),
			ActionKeys.UPDATE);

		commercePriceListChannelRelLocalService.
			deleteCommercePriceListChannelRel(commercePriceListChannelRel);
	}

	@Override
	public void deleteCommercePriceListChannelRelsByCommercePriceListId(
			long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		commercePriceListChannelRelLocalService.
			deleteCommercePriceListChannelRels(commercePriceListId);
	}

	@Override
	public CommercePriceListChannelRel fetchCommercePriceListChannelRel(
			long commerceChannelId, long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListChannelRelPersistence.fetchByCCI_CPI(
			commerceChannelId, commercePriceListId);
	}

	@Override
	public CommercePriceListChannelRel getCommercePriceListChannelRel(
			long commercePriceListChannelRelId)
		throws PortalException {

		CommercePriceListChannelRel commercePriceListChannelRel =
			commercePriceListChannelRelPersistence.findByPrimaryKey(
				commercePriceListChannelRelId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceListChannelRel.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commercePriceListChannelRel;
	}

	@Override
	public List<CommercePriceListChannelRel> getCommercePriceListChannelRels(
			long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListChannelRelPersistence.findByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public List<CommercePriceListChannelRel> getCommercePriceListChannelRels(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceListChannelRel> orderByComparator)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRels(
				commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public List<CommercePriceListChannelRel> getCommercePriceListChannelRels(
		long commercePriceListId, String name, int start, int end) {

		return commercePriceListChannelRelFinder.findByCommercePriceListId(
			commercePriceListId, name, start, end, true);
	}

	@Override
	public int getCommercePriceListChannelRelsCount(long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceListChannelRelPersistence.
			countByCommercePriceListId(commercePriceListId);
	}

	@Override
	public int getCommercePriceListChannelRelsCount(
		long commercePriceListId, String name) {

		return commercePriceListChannelRelFinder.countByCommercePriceListId(
			commercePriceListId, name, true);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}