/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.importer.item;

import com.liferay.commerce.price.CommerceOrderItemPrice;

import java.math.BigDecimal;

import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public interface CommerceOrderImporterItem {

	public long getCPDefinitionId();

	public long getCPInstanceId();

	public CommerceOrderItemPrice getCommerceOrderItemPrice();

	public String[] getErrorMessages();

	public String getJSON();

	public String getName(Locale locale);

	public long getParentCommerceOrderItemCPDefinitionId();

	public BigDecimal getQuantity();

	public String getReplacingSKU();

	public String getRequestedDeliveryDateString();

	public String getSKU();

	public String getUnitOfMeasureKey();

	public boolean hasParentCommerceOrderItem();

	public boolean isValid();

}