/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.importer.item;

import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceOrderImporterItemImpl
	implements CommerceOrderImporterItem {

	@Override
	public long getCPDefinitionId() {
		return _cpDefinitionId;
	}

	@Override
	public long getCPInstanceId() {
		return _cpInstanceId;
	}

	@Override
	public CommerceOrderItemPrice getCommerceOrderItemPrice() {
		return _commerceOrderItemPrice;
	}

	@Override
	public String[] getErrorMessages() {
		return _errorMessages;
	}

	@Override
	public String getJSON() {
		return _json;
	}

	@Override
	public String getName(Locale locale) {
		if (_nameMap == null) {
			return null;
		}

		return _nameMap.get(locale);
	}

	@Override
	public long getParentCommerceOrderItemCPDefinitionId() {
		return _parentCommerceOrderItemCPDefinitionId;
	}

	@Override
	public BigDecimal getQuantity() {
		return _quantity;
	}

	@Override
	public String getReplacingSKU() {
		return _replacingSKU;
	}

	@Override
	public String getRequestedDeliveryDateString() {
		return _requestedDeliveryDateString;
	}

	@Override
	public String getSKU() {
		return _sku;
	}

	@Override
	public String getUnitOfMeasureKey() {
		return _unitOfMeasureKey;
	}

	@Override
	public boolean hasParentCommerceOrderItem() {
		if (getParentCommerceOrderItemCPDefinitionId() > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isValid() {
		return ArrayUtil.isEmpty(getErrorMessages());
	}

	public void setCPDefinitionId(long cpDefinitionId) {
		_cpDefinitionId = cpDefinitionId;
	}

	public void setCPInstanceId(long cpInstanceId) {
		_cpInstanceId = cpInstanceId;
	}

	public void setCommerceOrderItemPrice(
		CommerceOrderItemPrice commerceOrderItemPrice) {

		_commerceOrderItemPrice = commerceOrderItemPrice;
	}

	public void setErrorMessages(String[] errorMessages) {
		_errorMessages = errorMessages;
	}

	public void setJSON(String json) {
		_json = json;
	}

	public void setNameMap(Map<Locale, String> nameMap) {
		_nameMap = nameMap;
	}

	public void setParentCommerceOrderItemCPDefinitionId(
		long parentCommerceOrderItemCPDefinitionId) {

		_parentCommerceOrderItemCPDefinitionId =
			parentCommerceOrderItemCPDefinitionId;
	}

	public void setQuantity(BigDecimal quantity) {
		_quantity = quantity;
	}

	public void setReplacingSKU(String replacingSKU) {
		_replacingSKU = replacingSKU;
	}

	public void setRequestedDeliveryDateString(
		String requestedDeliveryDateString) {

		_requestedDeliveryDateString = requestedDeliveryDateString;
	}

	public void setSku(String sku) {
		_sku = sku;
	}

	public void setUnitOfMeasureKey(String unitOfMeasureKey) {
		_unitOfMeasureKey = unitOfMeasureKey;
	}

	private CommerceOrderItemPrice _commerceOrderItemPrice;
	private long _cpDefinitionId;
	private long _cpInstanceId;
	private String[] _errorMessages;
	private String _json;
	private Map<Locale, String> _nameMap;
	private long _parentCommerceOrderItemCPDefinitionId;
	private BigDecimal _quantity;
	private String _replacingSKU;
	private String _requestedDeliveryDateString;
	private String _sku;
	private String _unitOfMeasureKey;

}