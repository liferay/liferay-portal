/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.model.impl;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryLocalServiceUtil;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryLocalServiceUtil;
import com.liferay.portal.kernel.service.RegionLocalServiceUtil;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceTaxFixedRateAddressRelImpl
	extends CommerceTaxFixedRateAddressRelBaseImpl {

	@Override
	public CPTaxCategory getCPTaxCategory() throws PortalException {
		return CPTaxCategoryLocalServiceUtil.getCPTaxCategory(
			getCPTaxCategoryId());
	}

	@Override
	public CommerceTaxMethod getCommerceTaxMethod() throws PortalException {
		if (getCommerceTaxMethodId() <= 0) {
			return null;
		}

		return CommerceTaxMethodLocalServiceUtil.getCommerceTaxMethod(
			getCommerceTaxMethodId());
	}

	@Override
	public Country getCountry() throws PortalException {
		if (getCountryId() > 0) {
			return CountryLocalServiceUtil.getCountry(getCountryId());
		}

		return null;
	}

	@Override
	public Region getRegion() throws PortalException {
		if (getRegionId() > 0) {
			return RegionLocalServiceUtil.getRegion(getRegionId());
		}

		return null;
	}

}