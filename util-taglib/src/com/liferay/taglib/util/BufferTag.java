/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author Brian Wing Shun Chan
 */
public class BufferTag extends BodyTagSupport {

	@Override
	public int doEndTag() {
		try {
			pageContext.setAttribute(
				_var, StringUtil.trim(getBodyContent().getString()));

			return EVAL_PAGE;
		}
		finally {
			_var = null;
		}
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public void setVar(String var) {
		_var = var;
	}

	private String _var;

}