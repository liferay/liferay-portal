/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.petra.string.StringPool;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eduardo Lundgren
 * @author Shuyang Zhou
 */
public class AttributesTagSupport
	extends ParamAndPropertyAncestorTagImpl implements DynamicAttributes {

	public void clearDynamicAttributes() {
		_dynamicAttributes.clear();
	}

	public String getAttributeNamespace() {
		return _attributeNamespace;
	}

	@Override
	public void release() {
		super.release();

		_attributeNamespace = null;
		_dynamicAttributes = null;
	}

	public void setAttributeNamespace(String attributeNamespace) {
		_attributeNamespace = attributeNamespace;
	}

	@Override
	public void setDynamicAttribute(
		String uri, String localName, Object value) {

		_dynamicAttributes.put(localName, value);
	}

	public void setNamespacedAttribute(
		HttpServletRequest httpServletRequest, String key, Object value) {

		if (value instanceof Boolean) {
			value = String.valueOf(value);
		}
		else if (value instanceof Number) {
			value = String.valueOf(value);
		}

		httpServletRequest.setAttribute(_encodeKey(key), value);
	}

	protected Map<String, Object> getDynamicAttributes() {
		return _dynamicAttributes;
	}

	private String _encodeKey(String key) {
		if (_attributeNamespace.length() == 0) {
			return key;
		}

		return _attributeNamespace.concat(key);
	}

	private String _attributeNamespace = StringPool.BLANK;
	private Map<String, Object> _dynamicAttributes = new HashMap<>();

}