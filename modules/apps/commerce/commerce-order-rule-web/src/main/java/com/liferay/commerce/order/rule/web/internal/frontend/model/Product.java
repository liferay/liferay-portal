/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.web.internal.frontend.model;

/**
 * @author Alessio Antonio Rendina
 */
public class Product {

	public Product(String externalReferenceCode, String name) {
		_externalReferenceCode = externalReferenceCode;
		_name = name;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public String getName() {
		return _name;
	}

	private final String _externalReferenceCode;
	private final String _name;

}