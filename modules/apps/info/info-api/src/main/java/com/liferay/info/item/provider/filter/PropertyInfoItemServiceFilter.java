/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.provider.filter;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Objects;

import org.osgi.framework.ServiceReference;

/**
 * @author Jorge Ferrer
 */
public class PropertyInfoItemServiceFilter implements InfoItemServiceFilter {

	public PropertyInfoItemServiceFilter(
		String propertyName, String propertyValue) {

		_propertyName = propertyName;
		_propertyValue = propertyValue;
	}

	@Override
	public String getFilterString() {
		return StringBundler.concat(
			"(", _propertyName, StringPool.EQUAL, _propertyValue, ")");
	}

	public String getPropertyName() {
		return _propertyName;
	}

	public String getPropertyValue() {
		return _propertyValue;
	}

	public boolean match(ServiceReference<?> serviceReference) {
		return Objects.equals(
			_propertyValue,
			GetterUtil.getString(
				serviceReference.getProperty(_propertyName), null));
	}

	private final String _propertyName;
	private final String _propertyValue;

}