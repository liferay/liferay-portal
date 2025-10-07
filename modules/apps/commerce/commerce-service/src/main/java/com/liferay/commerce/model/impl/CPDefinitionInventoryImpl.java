/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model.impl;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.Arrays;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionInventoryImpl extends CPDefinitionInventoryBaseImpl {

	@Override
	public BigDecimal[] getAllowedOrderQuantitiesArray() {
		String allowedOrderQuantitiesString = getAllowedOrderQuantities();

		if (Validator.isNull(allowedOrderQuantitiesString)) {
			return new BigDecimal[0];
		}

		allowedOrderQuantitiesString = allowedOrderQuantitiesString.replaceAll(
			StringPool.COMMA, StringPool.BLANK);

		BigDecimal[] allowedOrderQuantities = TransformUtil.transform(
			StringUtil.split(allowedOrderQuantitiesString, StringPool.SPACE),
			BigDecimal::new, BigDecimal.class);

		Arrays.sort(allowedOrderQuantities);

		return allowedOrderQuantities;
	}

}