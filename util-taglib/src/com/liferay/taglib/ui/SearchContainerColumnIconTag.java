/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.search.IconSearchEntry;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class SearchContainerColumnIconTag<R> extends SearchContainerColumnTag {

	@Override
	public int doEndTag() {
		try {
			SearchContainerRowTag<R> searchContainerRowTag =
				(SearchContainerRowTag<R>)findAncestorWithClass(
					this, SearchContainerRowTag.class);

			ResultRow resultRow = searchContainerRowTag.getRow();

			if (index <= -1) {
				List<SearchEntry> searchEntries = resultRow.getEntries();

				index = searchEntries.size();
			}

			if (resultRow.isRestricted()) {
				_href = null;
			}

			IconSearchEntry iconSearchEntry = new IconSearchEntry();

			iconSearchEntry.setAlign(getAlign());
			iconSearchEntry.setColspan(getColspan());
			iconSearchEntry.setCssClass(getCssClass());
			iconSearchEntry.setIcon(_icon);
			iconSearchEntry.setRequest(
				(HttpServletRequest)pageContext.getRequest());
			iconSearchEntry.setResponse(
				(HttpServletResponse)pageContext.getResponse());
			iconSearchEntry.setServletContext(
				ServletContextPool.get(PortalUtil.getServletContextName()));
			iconSearchEntry.setToggleRowChecker(isToggleRowChecker());
			iconSearchEntry.setValign(getValign());

			resultRow.addSearchEntry(index, iconSearchEntry);

			return EVAL_PAGE;
		}
		finally {
			index = -1;
			_icon = null;

			align = SearchEntry.DEFAULT_ALIGN;
			colspan = SearchEntry.DEFAULT_COLSPAN;
			cssClass = SearchEntry.DEFAULT_CSS_CLASS;
			_href = null;
			name = null;
			_toggleRowChecker = false;
			valign = SearchEntry.DEFAULT_VALIGN;
		}
	}

	@Override
	public int doStartTag() throws JspException {
		SearchContainerRowTag<R> searchContainerRowTag =
			(SearchContainerRowTag<R>)findAncestorWithClass(
				this, SearchContainerRowTag.class);

		if (searchContainerRowTag == null) {
			throw new JspTagException(
				"Requires liferay-ui:search-container-row");
		}

		if (!searchContainerRowTag.isHeaderNamesAssigned()) {
			String name = getName();

			if (Validator.isNotNull(name)) {
				List<String> headerNames =
					searchContainerRowTag.getHeaderNames();

				headerNames.add(name);
			}
		}

		return EVAL_BODY_INCLUDE;
	}

	public Object getHref() {
		if (_href instanceof PortletURL) {
			_href = _href.toString();
		}

		return _href;
	}

	public String getIcon() {
		return _icon;
	}

	public boolean isToggleRowChecker() {
		return _toggleRowChecker;
	}

	public void setHref(Object href) {
		_href = href;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setToggleRowChecker(boolean toggleRowChecker) {
		_toggleRowChecker = toggleRowChecker;
	}

	private Object _href;
	private String _icon;
	private boolean _toggleRowChecker;

}