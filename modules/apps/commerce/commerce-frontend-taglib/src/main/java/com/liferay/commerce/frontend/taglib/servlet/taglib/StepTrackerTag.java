/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.model.StepModel;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Fabio Diego Mastrorilli
 */
public class StepTrackerTag extends IncludeTag {

	public String getSpritemap() {
		return _spritemap;
	}

	public List<StepModel> getSteps() {
		return _steps;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setSpritemap(String spritemap) {
		_spritemap = spritemap;
	}

	public void setSteps(List<StepModel> steps) {
		_steps = steps;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_spritemap = null;
		_steps = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		if (Validator.isNull(_spritemap)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_spritemap = themeDisplay.getPathThemeSpritemap();
		}

		httpServletRequest.setAttribute(
			"liferay-commerce:step-tracker:spritemap", _spritemap);
		httpServletRequest.setAttribute(
			"liferay-commerce:step-tracker:steps", _steps);
	}

	private static final String _PAGE = "/step_tracker/page.jsp";

	private String _spritemap;
	private List<StepModel> _steps;

}