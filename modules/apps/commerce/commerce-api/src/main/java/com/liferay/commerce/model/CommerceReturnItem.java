/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model;

import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceReturnItem {

	public CommerceReturnItem(ObjectEntry objectEntry) {
		Map<String, Serializable> objectEntryValues = objectEntry.getValues();

		_objectEntry = objectEntry;

		_amount = new BigDecimal(
			String.valueOf(objectEntryValues.get("amount")));
		_authorized = new BigDecimal(
			String.valueOf(objectEntryValues.get("authorized")));
		_authorizeReturnWithoutReturningProducts = GetterUtil.getBoolean(
			String.valueOf(
				objectEntryValues.get(
					"authorizeReturnWithoutReturningProducts")));
		_commerceOrderItemId = (long)objectEntryValues.get(
			"r_commerceOrderItemToCommerceReturnItems_commerceOrderItemId");
		_createDate = objectEntry.getCreateDate();
		_currencyCode = (String)objectEntryValues.get("currencyCode");
		_currencySymbol = (String)objectEntryValues.get("currencySymbol");
		_externalReferenceCode = objectEntry.getExternalReferenceCode();
		_id = objectEntry.getPrimaryKey();
		_quantity = new BigDecimal(
			String.valueOf(objectEntryValues.get("quantity")));
		_received = new BigDecimal(
			String.valueOf(objectEntryValues.get("received")));
		_returnItemStatus = (String)objectEntryValues.get("returnItemStatus");
		_returnReason = (String)objectEntryValues.get("returnReason");
		_returnResolutionMethod = (String)objectEntryValues.get(
			"returnResolutionMethod");
		_status = objectEntry.getStatus();
	}

	public BigDecimal getAmount() {
		return _amount;
	}

	public BigDecimal getAuthorized() {
		return _authorized;
	}

	public long getCommerceOrderItemId() {
		return _commerceOrderItemId;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public String getCurrencyCode() {
		return _currencyCode;
	}

	public String getCurrencySymbol() {
		return _currencySymbol;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public long getId() {
		return _id;
	}

	public ObjectEntry getObjectEntry() {
		return _objectEntry;
	}

	public BigDecimal getQuantity() {
		return _quantity;
	}

	public BigDecimal getReceived() {
		return _received;
	}

	public String getReturnItemStatus() {
		return _returnItemStatus;
	}

	public String getReturnReason() {
		return _returnReason;
	}

	public String getReturnResolutionMethod() {
		return _returnResolutionMethod;
	}

	public int getStatus() {
		return _status;
	}

	public Boolean isAuthorizeReturnWithoutReturningProducts() {
		return _authorizeReturnWithoutReturningProducts;
	}

	private final BigDecimal _amount;
	private final Boolean _authorizeReturnWithoutReturningProducts;
	private final BigDecimal _authorized;
	private final long _commerceOrderItemId;
	private final Date _createDate;
	private final String _currencyCode;
	private final String _currencySymbol;
	private final String _externalReferenceCode;
	private final long _id;
	private final ObjectEntry _objectEntry;
	private final BigDecimal _quantity;
	private final BigDecimal _received;
	private final String _returnItemStatus;
	private final String _returnReason;
	private final String _returnResolutionMethod;
	private final int _status;

}