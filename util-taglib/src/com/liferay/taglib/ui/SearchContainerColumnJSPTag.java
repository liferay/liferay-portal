/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.taglib.search.JSPSearchEntry;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;

import java.util.List;

/**
 * @author Raymond Augé
 */
public class SearchContainerColumnJSPTag<R> extends SearchContainerColumnTag {

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

			JSPSearchEntry jspSearchEntry = new JSPSearchEntry();

			jspSearchEntry.setAlign(getAlign());
			jspSearchEntry.setColspan(getColspan());
			jspSearchEntry.setCssClass(getCssClass());
			jspSearchEntry.setHref(String.valueOf(getHref()));
			jspSearchEntry.setPath(getPath());
			jspSearchEntry.setRequest(
				(HttpServletRequest)pageContext.getRequest());
			jspSearchEntry.setResponse(
				(HttpServletResponse)pageContext.getResponse());
			jspSearchEntry.setServletContext(pageContext.getServletContext());
			jspSearchEntry.setTruncate(getTruncate());
			jspSearchEntry.setValign(getValign());

			resultRow.addSearchEntry(index, jspSearchEntry);

			return EVAL_PAGE;
		}
		finally {
			index = -1;

			align = SearchEntry.DEFAULT_ALIGN;
			colspan = SearchEntry.DEFAULT_COLSPAN;
			cssClass = SearchEntry.DEFAULT_CSS_CLASS;
			name = StringPool.BLANK;
			_path = null;
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
			List<String> headerNames = searchContainerRowTag.getHeaderNames();

			headerNames.add(name);
		}

		return EVAL_BODY_INCLUDE;
	}

	public Object getHref() {
		if (_href instanceof PortletURL) {
			_href = _href.toString();
		}

		return _href;
	}

	public String getPath() {
		return _path;
	}

	public void setHref(Object href) {
		_href = href;
	}

	public void setPath(String path) {
		_path = path;
	}

	private Object _href;
	private String _path;

}