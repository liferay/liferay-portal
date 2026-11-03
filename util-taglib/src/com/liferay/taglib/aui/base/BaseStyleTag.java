/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.PositionTagSupport;

import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseStyleTag extends PositionTagSupport {

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public String getSenna() {
		return _senna;
	}

	public String getType() {
		return _type;
	}

	public void setSenna(String senna) {
		_senna = senna;
	}

	public void setType(String type) {
		_type = type;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_senna = null;
		_type = null;
	}

	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE = "/html/taglib/aui/style/page.jsp";

	private String _senna;
	private String _type;

}