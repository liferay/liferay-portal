/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.servlet.taglib;

import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.jsp.PageContext;

/**
 * @author Eudaldo Alonso
 */
public class RenderStateMaxLayoutStructureTag extends IncludeTag {

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE =
		"/render_state_max_layout_structure/page.jsp";

}