/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.taglib.servlet.taglib;

import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Alejandro Tardín
 */
public class JournalArticleDisplayTag extends IncludeTag {

	public JournalArticleDisplay getArticleDisplay() {
		return _articleDisplay;
	}

	public PortletURL getPaginationURL() {
		return _paginationURL;
	}

	public String getWrapperCssClass() {
		return _wrapperCssClass;
	}

	public boolean isDataAnalyticsTrackingEnabled() {
		return _dataAnalyticsTrackingEnabled;
	}

	public boolean isShowTitle() {
		return _showTitle;
	}

	public void setArticleDisplay(JournalArticleDisplay articleDisplay) {
		_articleDisplay = articleDisplay;
	}

	public void setDataAnalyticsTrackingEnabled(
		boolean dataAnalyticsTrackingEnabled) {

		_dataAnalyticsTrackingEnabled = dataAnalyticsTrackingEnabled;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPaginationURL(PortletURL paginationURL) {
		_paginationURL = paginationURL;
	}

	public void setShowTitle(boolean showTitle) {
		_showTitle = showTitle;
	}

	public void setWrapperCssClass(String wrapperCssClass) {
		_wrapperCssClass = wrapperCssClass;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_articleDisplay = null;
		_dataAnalyticsTrackingEnabled = true;
		_paginationURL = null;
		_showTitle = false;
		_wrapperCssClass = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:articleDisplay", _articleDisplay);
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:dataAnalyticsTrackingEnabled",
			String.valueOf(_dataAnalyticsTrackingEnabled));
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:paginationURL", _paginationURL);
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:showTitle",
			String.valueOf(_showTitle));
		httpServletRequest.setAttribute(
			"liferay-journal:journal-article:wrapperCssClass",
			_wrapperCssClass);
	}

	private static final String _PAGE = "/journal_article/page.jsp";

	private JournalArticleDisplay _articleDisplay;
	private boolean _dataAnalyticsTrackingEnabled = true;
	private PortletURL _paginationURL;
	private boolean _showTitle;
	private String _wrapperCssClass;

}