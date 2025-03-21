/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.mvc.binding.ParamError;

/**
 * @author Neil Griffin
 */
public class ParamErrorImpl implements ParamError {

	public ParamErrorImpl(String message, String paramName) {
		_message = message;
		_paramName = paramName;
	}

	@Override
	public String getMessage() {
		return _message;
	}

	@Override
	public String getParamName() {
		return _paramName;
	}

	private final String _message;
	private final String _paramName;

}