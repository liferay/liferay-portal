/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.base.BaseButtonRowTag;
import com.liferay.taglib.util.InlineUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspWriter;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class ButtonRowTag extends BaseButtonRowTag {

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div>");

		return EVAL_PAGE;
	}

	@Override
	protected int processStartTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<div class=\"button-holder ");

		String cssClass = getCssClass();

		if (cssClass != null) {
			jspWriter.write(cssClass);
		}

		jspWriter.write("\" ");

		String id = getId();

		if (id != null) {
			jspWriter.write("id=\"");
			jspWriter.write(id);
			jspWriter.write("\" ");
		}

		jspWriter.write(
			InlineUtil.buildDynamicAttributes(getDynamicAttributes()));

		jspWriter.write(">");

		return EVAL_BODY_INCLUDE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isStatePopUp()) {
			String cssClass = "dialog-footer";

			if (getCssClass() != null) {
				cssClass = cssClass + StringPool.SPACE + getCssClass();
			}

			setCssClass(cssClass);
		}

		super.setAttributes(httpServletRequest);
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

}