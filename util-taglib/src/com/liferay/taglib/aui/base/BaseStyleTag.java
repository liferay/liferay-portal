/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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
public abstract class BaseStyleTag extends com.liferay.taglib.util.PositionTagSupport {

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public java.lang.String getSenna() {
		return _senna;
	}

	public void setSenna(java.lang.String senna) {
		_senna = senna;
	}

	public java.lang.String getType() {
		return _type;
	}

	public void setType(java.lang.String type) {
		_type = type;
	}

	protected void cleanUp() {
		_senna = null;
		_type = null;
	}

	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE =
		"/html/taglib/aui/style/page.jsp";

	private java.lang.String _senna = null;
	private java.lang.String _type = null;

}