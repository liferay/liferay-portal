/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseValidatorTagImpl extends com.liferay.taglib.BaseBodyTagSupport {

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public java.lang.String getErrorMessage() {
		return _errorMessage;
	}

	public java.lang.String getName() {
		return _name;
	}

	public void setErrorMessage(java.lang.String errorMessage) {
		_errorMessage = errorMessage;
	}

	public void setName(java.lang.String name) {
		_name = name;
	}

	protected void cleanUp() {
		_errorMessage = null;
		_name = null;
	}

	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE =
		"/html/taglib/aui/validator/page.jsp";

	private java.lang.String _errorMessage = null;
	private java.lang.String _name = null;

}