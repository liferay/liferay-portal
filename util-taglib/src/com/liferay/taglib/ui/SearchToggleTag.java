/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class SearchToggleTag extends IncludeTag {

	public String getButtonLabel() {
		return _buttonLabel;
	}

	public DisplayTerms getDisplayTerms() {
		return _displayTerms;
	}

	public String getId() {
		return _id;
	}

	public String getMarkupView() {
		return null;
	}

	public boolean isAutoFocus() {
		return _autoFocus;
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setButtonLabel(String buttonLabel) {
		_buttonLabel = buttonLabel;
	}

	public void setDisplayTerms(DisplayTerms displayTerms) {
		_displayTerms = displayTerms;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setMarkupView(String markupView) {
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_autoFocus = false;
		_buttonLabel = null;
		_displayTerms = null;
		_id = null;
	}

	@Override
	protected String getEndPage() {
		return "/html/taglib/ui/search_toggle/end.jsp";
	}

	@Override
	protected String getStartPage() {
		return "/html/taglib/ui/search_toggle/start.jsp";
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:search-toggle:autoFocus", String.valueOf(_autoFocus));
		httpServletRequest.setAttribute(
			"liferay-ui:search-toggle:buttonLabel", _buttonLabel);
		httpServletRequest.setAttribute(
			"liferay-ui:search-toggle:displayTerms", _displayTerms);
		httpServletRequest.setAttribute("liferay-ui:search-toggle:id", _id);
	}

	private boolean _autoFocus;
	private String _buttonLabel;
	private DisplayTerms _displayTerms;
	private String _id;

}