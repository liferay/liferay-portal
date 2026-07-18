/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.impl;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.type.virtual.order.constants.CommerceVirtualOrderActionKeys;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.base.CommerceVirtualOrderItemFileEntryServiceBaseImpl;
import com.liferay.commerce.product.type.virtual.order.service.persistence.CommerceVirtualOrderItemPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceVirtualOrderItemFileEntry"
	},
	service = AopService.class
)
public class CommerceVirtualOrderItemFileEntryServiceImpl
	extends CommerceVirtualOrderItemFileEntryServiceBaseImpl {

	@Override
	public CommerceVirtualOrderItemFileEntry
			fetchCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId)
		throws PortalException {

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItemFileEntryLocalService.
				fetchCommerceVirtualOrderItemFileEntry(
					commerceVirtualOrderItemFileEntryId);

		if (commerceVirtualOrderItemFileEntry != null) {
			_commerceVirtualOrderItemFileEntryModelResourcePermission.check(
				getPermissionChecker(), commerceVirtualOrderItemFileEntry,
				CommerceVirtualOrderActionKeys.
					DOWNLOAD_COMMERCE_VIRTUAL_ORDER_ITEM);
		}

		return commerceVirtualOrderItemFileEntry;
	}

	@Override
	public List<CommerceVirtualOrderItemFileEntry>
			getCommerceVirtualOrderItemFileEntries(
				long commerceVirtualOrderItemId, int start, int end)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			_commerceVirtualOrderItemPersistence.findByPrimaryKey(
				commerceVirtualOrderItemId);

		CommerceOrderItem commerceOrderItem =
			commerceVirtualOrderItem.getCommerceOrderItem();

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.VIEW);

		return commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntries(
				commerceVirtualOrderItemId, start, end);
	}

	@Override
	public CommerceVirtualOrderItemFileEntry
			updateCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId, long fileEntryId,
				String url, int usages, String version)
		throws PortalException {

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItemFileEntryLocalService.
				getCommerceVirtualOrderItemFileEntry(
					commerceVirtualOrderItemFileEntryId);

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			_commerceVirtualOrderItemPersistence.findByPrimaryKey(
				commerceVirtualOrderItemFileEntry.
					getCommerceVirtualOrderItemId());

		CommerceOrderItem commerceOrderItem =
			commerceVirtualOrderItem.getCommerceOrderItem();

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceVirtualOrderItemFileEntryLocalService.
			updateCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntryId, fileEntryId, url, usages,
				version);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry)"
	)
	private ModelResourcePermission<CommerceVirtualOrderItemFileEntry>
		_commerceVirtualOrderItemFileEntryModelResourcePermission;

	@Reference
	private CommerceVirtualOrderItemPersistence
		_commerceVirtualOrderItemPersistence;

}