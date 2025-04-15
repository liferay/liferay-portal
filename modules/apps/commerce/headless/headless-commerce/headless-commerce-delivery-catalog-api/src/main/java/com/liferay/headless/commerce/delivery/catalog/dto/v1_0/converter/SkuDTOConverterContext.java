/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.dto.v1_0.converter;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.ws.rs.core.UriInfo;

import java.math.BigDecimal;

import java.util.Locale;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
public class SkuDTOConverterContext extends DefaultDTOConverterContext {

	public SkuDTOConverterContext(
		CommerceContext commerceContext, long companyId,
		CPDefinition cpDefinition, Locale locale, BigDecimal quantity,
		long resourcePrimKey, JSONArray skuOptionsJSONArray,
		String unitOfMeasureKey, UriInfo uriInfo, User user) {

		super(resourcePrimKey, locale, uriInfo, user);

		_commerceContext = commerceContext;
		_companyId = companyId;
		_cpDefinition = cpDefinition;
		_quantity = quantity;
		_skuOptionsJSONArray = skuOptionsJSONArray;
		_unitOfMeasureKey = unitOfMeasureKey;
	}

	public CommerceContext getCommerceContext() {
		return _commerceContext;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public CPDefinition getCPDefinition() {
		return _cpDefinition;
	}

	public BigDecimal getQuantity() {
		return _quantity;
	}

	public JSONArray getSkuOptionsJSONArray() {
		return _skuOptionsJSONArray;
	}

	public String getUnitOfMeasureKey() {
		return _unitOfMeasureKey;
	}

	private final CommerceContext _commerceContext;
	private final long _companyId;
	private final CPDefinition _cpDefinition;
	private final BigDecimal _quantity;
	private final JSONArray _skuOptionsJSONArray;
	private final String _unitOfMeasureKey;

}