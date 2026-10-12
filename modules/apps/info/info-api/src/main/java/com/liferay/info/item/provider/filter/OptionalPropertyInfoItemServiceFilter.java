/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.provider.filter;

import com.liferay.petra.string.StringBundler;

/**
 * @author Jorge Ferrer
 */
public class OptionalPropertyInfoItemServiceFilter
	extends PropertyInfoItemServiceFilter {

	public OptionalPropertyInfoItemServiceFilter(
		String propertyName, String propertyValue) {

		super(propertyName, propertyValue);
	}

	@Override
	public String getFilterString() {
		return StringBundler.concat(
			"(|", super.getFilterString(), "(!(", getPropertyName(), "=*)))");
	}

	@Override
	public boolean matchPropertyValue(Object propertyValue) {
		if (propertyValue == null) {
			return true;
		}

		return super.matchPropertyValue(propertyValue);
	}

}