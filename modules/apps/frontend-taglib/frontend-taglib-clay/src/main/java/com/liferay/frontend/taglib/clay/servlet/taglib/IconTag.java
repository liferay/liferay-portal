/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Set;

/**
 * @author Chema Balsas
 */
public class IconTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);
		setContainerElement("svg");
		setDynamicAttribute(StringPool.BLANK, "role", "presentation");

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_spritemap = themeDisplay.getPathThemeSpritemap();

		return super.doStartTag();
	}

	public String getSymbol() {
		return _symbol;
	}

	public void setSymbol(String symbol) {
		_symbol = symbol;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_spritemap = null;
		_symbol = null;
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("lexicon-icon");
		cssClasses.add("lexicon-icon-" + _symbol);

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<use xlink:href=\"");
		jspWriter.write(_spritemap);
		jspWriter.write("#");
		jspWriter.write(_symbol);
		jspWriter.write("\" />");

		return SKIP_BODY;
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:icon:";

	private String _spritemap;
	private String _symbol;

}