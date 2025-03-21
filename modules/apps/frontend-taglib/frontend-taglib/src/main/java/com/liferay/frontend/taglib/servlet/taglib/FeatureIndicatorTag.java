/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Patrick Yeo
 */
public class FeatureIndicatorTag extends IncludeTag {

	public boolean getDark() {
		return _dark;
	}

	public boolean getInteractive() {
		return _interactive;
	}

	public String getTooltipAlign() {
		return _tooltipAlign;
	}

	public String getType() {
		return _type;
	}

	public void setDark(boolean dark) {
		_dark = dark;
	}

	public void setInteractive(boolean interactive) {
		_interactive = interactive;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setTooltipAlign(String tooltipAlign) {
		_tooltipAlign = tooltipAlign;
	}

	public void setType(String type) {
		_type = type;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_dark = false;
		_interactive = false;
		_tooltipAlign = null;
		_type = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-frontend:feature-indicator:dark", _dark);
		httpServletRequest.setAttribute(
			"liferay-frontend:feature-indicator:interactive", _interactive);
		httpServletRequest.setAttribute(
			"liferay-frontend:feature-indicator:tooltipAlign", _tooltipAlign);
		httpServletRequest.setAttribute(
			"liferay-frontend:feature-indicator:type", _type);
	}

	private static final String _PAGE = "/feature_indicator/page.jsp";

	private boolean _dark;
	private boolean _interactive;
	private String _tooltipAlign;
	private String _type;

}