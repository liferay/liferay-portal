/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.mvc.binding.BindingError;

/**
 * @author Neil Griffin
 */
public class BindingErrorImpl extends ParamErrorImpl implements BindingError {

	public BindingErrorImpl(
		String message, String paramName, String submittedValue) {

		super(message, paramName);

		_submittedValue = submittedValue;
	}

	@Override
	public String getSubmittedValue() {
		return _submittedValue;
	}

	private final String _submittedValue;

}