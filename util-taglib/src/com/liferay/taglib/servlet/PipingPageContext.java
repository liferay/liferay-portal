/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.servlet;

import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.io.Writer;

/**
 * @author Shuyang Zhou
 */
public class PipingPageContext extends PageContextWrapper {

	public PipingPageContext(PageContext pageContext, Writer writer) {
		super(pageContext);

		_jspWriter = new PipingJspWriter(writer);
	}

	@Override
	public JspWriter getOut() {
		return _jspWriter;
	}

	private final JspWriter _jspWriter;

}