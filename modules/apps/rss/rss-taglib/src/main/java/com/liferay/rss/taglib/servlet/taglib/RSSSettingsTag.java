/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.taglib.servlet.taglib;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.rss.taglib.internal.servlet.ServletContextUtil;
import com.liferay.rss.util.RSSUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Eduardo García
 */
public class RSSSettingsTag extends IncludeTag {

	public int getDelta() {
		return _delta;
	}

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public String[] getDisplayStyles() {
		return _displayStyles;
	}

	public String getFeedType() {
		return _feedType;
	}

	public String getName() {
		return _name;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public boolean isNameEnabled() {
		return _nameEnabled;
	}

	public void setDelta(int delta) {
		_delta = delta;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setDisplayStyles(String[] displayStyles) {
		_displayStyles = displayStyles;
	}

	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	public void setFeedType(String feedType) {
		_feedType = feedType;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setNameEnabled(boolean nameEnabled) {
		_nameEnabled = nameEnabled;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_delta = SearchContainer.DEFAULT_DELTA;
		_displayStyle = RSSUtil.DISPLAY_STYLE_DEFAULT;
		_displayStyles = RSSUtil.DISPLAY_STYLES;
		_enabled = false;
		_feedType = RSSUtil.FEED_TYPE_DEFAULT;
		_name = null;
		_nameEnabled = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-rss:rss-settings:delta", String.valueOf(_delta));
		httpServletRequest.setAttribute(
			"liferay-rss:rss-settings:displayStyle", _displayStyle);
		httpServletRequest.setAttribute(
			"liferay-rss:rss-settings:displayStyles", _displayStyles);
		httpServletRequest.setAttribute(
			"liferay-rss:rss-settings:enabled", String.valueOf(_enabled));
		httpServletRequest.setAttribute(
			"liferay-rss:rss-settings:feedType", _feedType);
		httpServletRequest.setAttribute("liferay-rss:rss-settings:name", _name);
		httpServletRequest.setAttribute(
			"liferay-rss:rss-settings:nameEnabled",
			String.valueOf(_nameEnabled));
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _PAGE = "/rss_settings/page.jsp";

	private int _delta = SearchContainer.DEFAULT_DELTA;
	private String _displayStyle = RSSUtil.DISPLAY_STYLE_DEFAULT;
	private String[] _displayStyles = RSSUtil.DISPLAY_STYLES;
	private boolean _enabled;
	private String _feedType = RSSUtil.FEED_TYPE_DEFAULT;
	private String _name;
	private boolean _nameEnabled;

}