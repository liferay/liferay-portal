/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.ActionRequest;

import java.math.BigDecimal;

import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public interface CommerceOrderItemQuantityFormatter {

	public String format(
			CommerceOrderItem commerceOrderItem,
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure, Locale locale)
		throws PortalException;

	public String format(CommerceOrderItem commerceOrderItem, Locale locale)
		throws PortalException;

	public BigDecimal parse(
			ActionRequest actionRequest, String className, String param)
		throws Exception;

	public BigDecimal parse(String className, String quantity, Locale locale)
		throws Exception;

}