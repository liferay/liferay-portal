/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.model;

import java.math.BigDecimal;

/**
 * @author Alessio Antonio Rendina
 * @author Alec Sloan
 */
public class ShipmentItem {

	public ShipmentItem(
		String externalReferenceCode, long orderId, BigDecimal orderedQuantity,
		long shipmentItemId, BigDecimal shippedQuantity, String sku,
		BigDecimal toSendQuantity, String unitOfMeasureKey, String warehouse) {

		_externalReferenceCode = externalReferenceCode;
		_orderId = orderId;
		_orderedQuantity = orderedQuantity;
		_shipmentItemId = shipmentItemId;
		_shippedQuantity = shippedQuantity;
		_sku = sku;
		_toSendQuantity = toSendQuantity;
		_unitOfMeasureKey = unitOfMeasureKey;
		_warehouse = warehouse;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public long getOrderId() {
		return _orderId;
	}

	public BigDecimal getOrderedQuantity() {
		return _orderedQuantity;
	}

	public long getShipmentItemId() {
		return _shipmentItemId;
	}

	public BigDecimal getShippedQuantity() {
		return _shippedQuantity;
	}

	public String getSku() {
		return _sku;
	}

	public BigDecimal getToSendQuantity() {
		return _toSendQuantity;
	}

	public String getUnitOfMeasureKey() {
		return _unitOfMeasureKey;
	}

	public String getWarehouse() {
		return _warehouse;
	}

	private final String _externalReferenceCode;
	private final long _orderId;
	private final BigDecimal _orderedQuantity;
	private final long _shipmentItemId;
	private final BigDecimal _shippedQuantity;
	private final String _sku;
	private final BigDecimal _toSendQuantity;
	private final String _unitOfMeasureKey;
	private final String _warehouse;

}