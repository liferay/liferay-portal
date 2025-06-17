/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPConfigurationListLocalServiceUtil;
import com.liferay.commerce.product.service.CPTaxCategoryLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.Arrays;

/**
 * @author Marco Leo
 */
public class CPConfigurationEntryImpl extends CPConfigurationEntryBaseImpl {

	@Override
	public BigDecimal[] getAllowedOrderQuantitiesArray() {
		String allowedOrderQuantitiesString = getAllowedOrderQuantities();

		if (Validator.isNull(allowedOrderQuantitiesString)) {
			return new BigDecimal[0];
		}

		allowedOrderQuantitiesString = allowedOrderQuantitiesString.replaceAll(
			" *(, *)|(\\. *)|( +)", StringPool.COMMA);

		int[] allowedOrderQuantities = StringUtil.split(
			allowedOrderQuantitiesString, 0);

		Arrays.sort(allowedOrderQuantities);

		return TransformUtil.transform(
			allowedOrderQuantities, BigDecimal::valueOf, BigDecimal.class);
	}

	@Override
	public CPTaxCategory getCPTaxCategory() throws PortalException {
		if (getCPTaxCategoryId() <= 0) {
			return null;
		}

		return CPTaxCategoryLocalServiceUtil.getCPTaxCategory(
			getCPTaxCategoryId());
	}

	@Override
	public CPConfigurationList getParentCPConfigurationList()
		throws PortalException {

		CPConfigurationList cpConfigurationList =
			CPConfigurationListLocalServiceUtil.getCPConfigurationList(
				getCPConfigurationListId());

		return cpConfigurationList.getParentCPConfigurationList();
	}

	@Override
	public boolean isMaster() throws PortalException {
		CPConfigurationList cpConfigurationList =
			CPConfigurationListLocalServiceUtil.getCPConfigurationList(
				getCPConfigurationListId());

		return cpConfigurationList.isMaster();
	}

}