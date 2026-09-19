/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author Andrea Sbarra
 */
public class ProductDTOConverterContext extends DefaultDTOConverterContext {

	public ProductDTOConverterContext(
		CommerceContext commerceContext, CPCatalogEntry cpCatalogEntry,
		Object id, Locale locale) {

		super(false, new HashMap<>(), null, id, locale, null, null);

		_commerceContext = commerceContext;
		_cpCatalogEntry = cpCatalogEntry;
	}

	public ProductDTOConverterContext(
		CommerceContext commerceContext, CPDefinition cpDefinition, Object id,
		Locale locale) {

		super(false, new HashMap<>(), null, id, locale, null, null);

		_commerceContext = commerceContext;
		_cpDefinition = cpDefinition;
	}

	public CPCatalogEntry getCPCatalogEntry() {
		return _cpCatalogEntry;
	}

	public CPDefinition getCPDefinition() {
		return _cpDefinition;
	}

	public CommerceContext getCommerceContext() {
		return _commerceContext;
	}

	private final CommerceContext _commerceContext;
	private CPCatalogEntry _cpCatalogEntry;
	private CPDefinition _cpDefinition;

}