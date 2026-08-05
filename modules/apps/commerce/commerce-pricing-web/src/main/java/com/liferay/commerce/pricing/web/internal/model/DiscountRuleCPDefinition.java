/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.model;

import com.liferay.commerce.frontend.model.ImageField;

/**
 * @author Riccardo Alberti
 */
public class DiscountRuleCPDefinition {

	public DiscountRuleCPDefinition(
		String cProductExternalReferenceCode, long discountRuleId,
		ImageField image, String name, String sku) {

		_cProductExternalReferenceCode = cProductExternalReferenceCode;
		_discountRuleId = discountRuleId;
		_image = image;
		_name = name;
		_sku = sku;
	}

	public String getCProductExternalReferenceCode() {
		return _cProductExternalReferenceCode;
	}

	public long getDiscountRuleId() {
		return _discountRuleId;
	}

	public ImageField getImage() {
		return _image;
	}

	public String getName() {
		return _name;
	}

	public String getSku() {
		return _sku;
	}

	private final String _cProductExternalReferenceCode;
	private final long _discountRuleId;
	private final ImageField _image;
	private final String _name;
	private final String _sku;

}