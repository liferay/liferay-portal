/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.taglib.internal.servlet.ServletContextUtil;
import com.liferay.dynamic.data.mapping.taglib.servlet.taglib.base.BaseHTMLFieldTag;

import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Sergio González
 */
public class HTMLFieldTag extends BaseHTMLFieldTag {

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div>");

		return EVAL_PAGE;
	}

}