/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.util;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.ActionRequest;

import java.math.BigDecimal;

import java.util.Locale;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public interface CommercePriceFormatter {

	public String format(BigDecimal price, Locale locale)
		throws PortalException;

	public String format(
			CommerceCurrency commerceCurrency, BigDecimal price, Locale locale)
		throws PortalException;

	public String formatAsRelative(
		CommerceCurrency commerceCurrency, BigDecimal price, Locale locale);

	public BigDecimal parse(
			ActionRequest actionRequest, boolean allowNegativeValue,
			String className, String param)
		throws Exception;

	public String parse(
			boolean allowNegativeValue, String className, String price,
			Locale locale)
		throws Exception;

}