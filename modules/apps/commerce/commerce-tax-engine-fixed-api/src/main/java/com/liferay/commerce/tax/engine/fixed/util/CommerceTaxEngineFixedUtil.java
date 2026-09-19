/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.util;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.util.comparator.CPTaxCategoryNameComparator;
import com.liferay.commerce.tax.engine.fixed.util.comparator.CommerceTaxFixedRateAddressRelCreateDateComparator;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceTaxEngineFixedUtil {

	public static OrderByComparator<CPTaxCategory>
		getCPTaxCategoryOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<CPTaxCategory> orderByComparator = null;

		if (orderByCol.equals("name")) {
			orderByComparator = CPTaxCategoryNameComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

	public static OrderByComparator<CommerceTaxFixedRateAddressRel>
		getCommerceTaxFixedRateAddressRelOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator =
			null;

		if (orderByCol.equals("create-date")) {
			orderByComparator =
				CommerceTaxFixedRateAddressRelCreateDateComparator.getInstance(
					orderByAsc);
		}

		return orderByComparator;
	}

}