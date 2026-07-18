/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.impl;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.type.virtual.order.constants.CommerceVirtualOrderActionKeys;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.order.service.base.CommerceVirtualOrderItemServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.io.File;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceVirtualOrderItem"
	},
	service = AopService.class
)
public class CommerceVirtualOrderItemServiceImpl
	extends CommerceVirtualOrderItemServiceBaseImpl {

	@Override
	public CommerceVirtualOrderItem fetchCommerceVirtualOrderItem(
			long commerceVirtualOrderItemId)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.fetchByPrimaryKey(
				commerceVirtualOrderItemId);

		if (commerceVirtualOrderItem != null) {
			CommerceOrderItem commerceOrderItem =
				commerceVirtualOrderItem.getCommerceOrderItem();

			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
				ActionKeys.VIEW);
		}

		return commerceVirtualOrderItem;
	}

	@Override
	public CommerceVirtualOrderItem
			fetchCommerceVirtualOrderItemByCommerceOrderItemId(
				long commerceOrderItemId)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemLocalService.
				fetchCommerceVirtualOrderItemByCommerceOrderItemId(
					commerceOrderItemId);

		if (commerceVirtualOrderItem != null) {
			CommerceOrderItem commerceOrderItem =
				commerceVirtualOrderItem.getCommerceOrderItem();

			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
				ActionKeys.VIEW);
		}

		return commerceVirtualOrderItem;
	}

	@Override
	public File getFile(
			long commerceVirtualOrderItemId,
			long commerceVirtualOrderItemFileEntryId)
		throws Exception {

		PermissionChecker permissionChecker = getPermissionChecker();

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.findByPrimaryKey(
				commerceVirtualOrderItemId);

		_commerceVirtualOrderItemFileEntryModelResourcePermission.check(
			permissionChecker, commerceVirtualOrderItemFileEntryId,
			CommerceVirtualOrderActionKeys.
				DOWNLOAD_COMMERCE_VIRTUAL_ORDER_ITEM);

		File file = commerceVirtualOrderItemLocalService.getFile(
			commerceVirtualOrderItemId, commerceVirtualOrderItemFileEntryId);

		if (!permissionChecker.isCompanyAdmin() ||
			!permissionChecker.isGroupAdmin(
				commerceVirtualOrderItem.getGroupId())) {

			_commerceVirtualOrderItemFileEntryLocalService.incrementUsages(
				commerceVirtualOrderItemFileEntryId);
		}

		return file;
	}

	@Override
	public CommerceVirtualOrderItem updateCommerceVirtualOrderItem(
			long commerceVirtualOrderItemId, int activationStatus,
			long duration, int maxUsages, boolean active)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.findByPrimaryKey(
				commerceVirtualOrderItemId);

		CommerceOrderItem commerceOrderItem =
			commerceVirtualOrderItem.getCommerceOrderItem();

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceVirtualOrderItemLocalService.
			updateCommerceVirtualOrderItem(
				commerceVirtualOrderItemId, activationStatus, duration,
				maxUsages, active);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceVirtualOrderItemFileEntryLocalService
		_commerceVirtualOrderItemFileEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry)"
	)
	private ModelResourcePermission<CommerceVirtualOrderItemFileEntry>
		_commerceVirtualOrderItemFileEntryModelResourcePermission;

}