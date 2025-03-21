/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.item.selector.web.internal.search;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.util.SetUtil;

import jakarta.portlet.PortletResponse;

import java.util.Set;

/**
 * @author Andrea Di Giorgi
 */
public class CommerceInventoryWarehouseChecker extends EmptyOnClickRowChecker {

	public CommerceInventoryWarehouseChecker(
		PortletResponse portletResponse,
		long[] checkedCommerceInventoryWarehouseIds,
		long[] disabledCommerceInventoryWarehouseIds) {

		super(portletResponse);

		_checkedCommerceInventoryWarehouseIds = SetUtil.fromArray(
			checkedCommerceInventoryWarehouseIds);
		_disabledCommerceInventoryWarehouseIds = SetUtil.fromArray(
			disabledCommerceInventoryWarehouseIds);
	}

	@Override
	public boolean isChecked(Object object) {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			(CommerceInventoryWarehouse)object;

		return _checkedCommerceInventoryWarehouseIds.contains(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId());
	}

	@Override
	public boolean isDisabled(Object object) {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			(CommerceInventoryWarehouse)object;

		return _disabledCommerceInventoryWarehouseIds.contains(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId());
	}

	private final Set<Long> _checkedCommerceInventoryWarehouseIds;
	private final Set<Long> _disabledCommerceInventoryWarehouseIds;

}