/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author     Fabio Diego Mastrorilli
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Deprecated
public class ModalTag extends IncludeTag {

	public String getId() {
		return _id;
	}

	public boolean getRefreshPageOnClose() {
		return _refreshPageOnClose;
	}

	public String getSize() {
		return _size;
	}

	public String getSpritemap() {
		return _spritemap;
	}

	public String getTitle() {
		return _title;
	}

	public String getUrl() {
		return _url;
	}

	public void setId(String id) {
		_id = id;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRefreshPageOnClose(boolean refreshPageOnClose) {
		_refreshPageOnClose = refreshPageOnClose;
	}

	public void setSize(String size) {
		_size = size;
	}

	public void setSpritemap(String spritemap) {
		_spritemap = spritemap;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setUrl(String url) {
		_url = url;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_id = StringPool.BLANK;
		_refreshPageOnClose = false;
		_size = StringPool.BLANK;
		_spritemap = StringPool.BLANK;
		_title = StringPool.BLANK;
		_url = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest = getRequest();

		if (Validator.isNull(_spritemap)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_spritemap = themeDisplay.getPathThemeSpritemap();
		}

		httpServletRequest.setAttribute("liferay-commerce:modal:id", _id);
		httpServletRequest.setAttribute(
			"liferay-commerce:modal:refreshPageOnClose", _refreshPageOnClose);
		httpServletRequest.setAttribute("liferay-commerce:modal:size", _size);
		httpServletRequest.setAttribute(
			"liferay-commerce:modal:spritemap", _spritemap);
		httpServletRequest.setAttribute("liferay-commerce:modal:title", _title);
		httpServletRequest.setAttribute("liferay-commerce:modal:url", _url);
	}

	private static final String _PAGE = "/modal/page.jsp";

	private String _id = StringPool.BLANK;
	private boolean _refreshPageOnClose;
	private String _size = StringPool.BLANK;
	private String _spritemap = StringPool.BLANK;
	private String _title = StringPool.BLANK;
	private String _url = StringPool.BLANK;

}