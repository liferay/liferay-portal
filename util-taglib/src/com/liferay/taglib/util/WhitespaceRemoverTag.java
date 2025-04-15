/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author Brian Wing Shun Chan
 */
public class WhitespaceRemoverTag extends BodyTagSupport {

	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(getBodyContentString());
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}

		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	protected String getBodyContentString() {
		BodyContent bodyContent = getBodyContent();

		String bodyContentString = StringUtil.trim(bodyContent.getString());

		return StringUtil.removeChars(
			bodyContentString, CharPool.NEW_LINE, CharPool.TAB);
	}

}