/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.fields.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * @author Ivica Cardic
 */
public class NestedFieldsHttpServletRequestWrapper
	extends HttpServletRequestWrapper {

	public NestedFieldsHttpServletRequestWrapper(
		String fieldName, HttpServletRequest httpServletRequest) {

		super(httpServletRequest);

		_fieldName = fieldName;
	}

	@Override
	public String getParameter(String name) {
		return super.getParameter(_fieldName + "." + name);
	}

	private final String _fieldName;

}