/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.impl;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel;
import com.liferay.commerce.inventory.service.base.CommerceInventoryWarehouseRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceInventoryWarehouseRel"
	},
	service = AopService.class
)
public class CommerceInventoryWarehouseRelServiceImpl
	extends CommerceInventoryWarehouseRelServiceBaseImpl {

	@Override
	public CommerceInventoryWarehouseRel addCommerceInventoryWarehouseRel(
			String className, long classPK, long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.UPDATE);

		return commerceInventoryWarehouseRelLocalService.
			addCommerceInventoryWarehouseRel(
				getUserId(), className, classPK, commerceInventoryWarehouseId);
	}

	@Override
	public void deleteCommerceInventoryWarehouseRel(
			long commerceInventoryWarehouseRelId)
		throws PortalException {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			commerceInventoryWarehouseRelPersistence.findByPrimaryKey(
				commerceInventoryWarehouseRelId);

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(),
			commerceInventoryWarehouseRel.getCommerceInventoryWarehouseId(),
			ActionKeys.UPDATE);

		commerceInventoryWarehouseRelLocalService.
			deleteCommerceInventoryWarehouseRel(commerceInventoryWarehouseRel);
	}

	@Override
	public void deleteCommerceInventoryWarehouseRels(
			String className, long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.UPDATE);

		commerceInventoryWarehouseRelLocalService.
			deleteCommerceInventoryWarehouseRels(
				className, commerceInventoryWarehouseId);
	}

	@Override
	public void
			deleteCommerceInventoryWarehouseRelsByCommerceInventoryWarehouseId(
				long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.UPDATE);

		commerceInventoryWarehouseRelLocalService.
			deleteCommerceInventoryWarehouseRels(commerceInventoryWarehouseId);
	}

	@Override
	public CommerceInventoryWarehouseRel fetchCommerceInventoryWarehouseRel(
			String className, long classPK, long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelPersistence.fetchByC_C_CIWI(
			_classNameLocalService.getClassNameId(className), classPK,
			commerceInventoryWarehouseId);
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
			getAccountEntryCommerceInventoryWarehouseRels(
				long commerceInventoryWarehouseId, String keywords, int start,
				int end)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelLocalService.
			getAccountEntryCommerceInventoryWarehouseRels(
				commerceInventoryWarehouseId, keywords, start, end);
	}

	@Override
	public int getAccountEntryCommerceInventoryWarehouseRelsCount(
			long commerceInventoryWarehouseId, String keywords)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelLocalService.
			getAccountEntryCommerceInventoryWarehouseRelsCount(
				commerceInventoryWarehouseId, keywords);
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
			getAccountGroupCommerceInventoryWarehouseRels(
				long commerceInventoryWarehouseId, String keywords, int start,
				int end)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelLocalService.
			getAccountGroupCommerceInventoryWarehouseRels(
				commerceInventoryWarehouseId, keywords, start, end);
	}

	@Override
	public int getAccountGroupCommerceInventoryWarehouseRelsCount(
			long commerceInventoryWarehouseId, String keywords)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelLocalService.
			getAccountGroupCommerceInventoryWarehouseRelsCount(
				commerceInventoryWarehouseId, keywords);
	}

	@Override
	public CommerceInventoryWarehouseRel getCommerceInventoryWarehouseRel(
			long commerceInventoryWarehouseRelId)
		throws PortalException {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			commerceInventoryWarehouseRelPersistence.findByPrimaryKey(
				commerceInventoryWarehouseRelId);

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(),
			commerceInventoryWarehouseRel.getCommerceInventoryWarehouseId(),
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRel;
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
			getCommerceInventoryWarehouseRels(long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelPersistence.
			findByCommerceInventoryWarehouseId(commerceInventoryWarehouseId);
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
			getCommerceInventoryWarehouseRels(
				long commerceInventoryWarehouseId, int start, int end,
				OrderByComparator<CommerceInventoryWarehouseRel>
					orderByComparator)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelPersistence.
			findByCommerceInventoryWarehouseId(
				commerceInventoryWarehouseId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
			getCommerceInventoryWarehouseRels(
				String className, long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelLocalService.
			getCommerceInventoryWarehouseRels(
				className, commerceInventoryWarehouseId);
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
			getCommerceInventoryWarehouseRels(
				String className, long commerceInventoryWarehouseId, int start,
				int end,
				OrderByComparator<CommerceInventoryWarehouseRel>
					orderByComparator)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className),
			commerceInventoryWarehouseId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceInventoryWarehouseRelsCount(
			long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelPersistence.
			countByCommerceInventoryWarehouseId(commerceInventoryWarehouseId);
	}

	@Override
	public int getCommerceInventoryWarehouseRelsCount(
			String className, long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelPersistence.countByC_C(
			_classNameLocalService.getClassNameId(className),
			commerceInventoryWarehouseId);
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
			getCommerceOrderTypeCommerceInventoryWarehouseRels(
				long commerceInventoryWarehouseId, String keywords, int start,
				int end)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelLocalService.
			getCommerceOrderTypeCommerceInventoryWarehouseRels(
				commerceInventoryWarehouseId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommerceInventoryWarehouseRelsCount(
			long commerceInventoryWarehouseId, String keywords)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehouseRelLocalService.
			getCommerceOrderTypeCommerceInventoryWarehouseRelsCount(
				commerceInventoryWarehouseId, keywords);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse)"
	)
	private ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;

}