/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class ErrorMarkerTag extends IncludeTag {

	public String getKey() {
		return _key;
	}

	public String getValue() {
		return _value;
	}

	public void setKey(String key) {
		_key = key;
	}

	public void setValue(String value) {
		_value = value;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_key = null;
		_value = null;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		if (Validator.isNotNull(_key) && Validator.isNotNull(_value)) {
			httpServletRequest.setAttribute(
				"liferay-ui:error-marker:key", _key);
			httpServletRequest.setAttribute(
				"liferay-ui:error-marker:value", _value);
		}
		else {
			httpServletRequest.removeAttribute("liferay-ui:error-marker:key");
			httpServletRequest.removeAttribute("liferay-ui:error-marker:value");
		}
	}

	private String _key;
	private String _value;

}