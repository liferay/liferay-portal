/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Luca Pellizzon
 */
public class CommerceInventoryWarehouseItemCommerceInventoryWarehouseIdException
	extends PortalException {

	public CommerceInventoryWarehouseItemCommerceInventoryWarehouseIdException() {
	}

	public CommerceInventoryWarehouseItemCommerceInventoryWarehouseIdException(
		String msg) {

		super(msg);
	}

	public CommerceInventoryWarehouseItemCommerceInventoryWarehouseIdException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public CommerceInventoryWarehouseItemCommerceInventoryWarehouseIdException(
		Throwable throwable) {

		super(throwable);
	}

}