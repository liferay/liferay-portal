/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Péter Borkuti
 */
public class PopoverTag extends BaseCssTag {

	public String getId() {
		return _id;
	}

	@Override
	public String getTagNameForCssPath() {
		return "popover";
	}

	public String getText() {
		return _text;
	}

	public String getTitle() {
		return _title;
	}

	public void setId(String id) {
		_id = id;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setText(String text) {
		_text = text;
	}

	public void setTitle(String title) {
		_title = title;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_id = StringPool.BLANK;
		_text = StringPool.BLANK;
		_title = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute("liferay-staging:popover:id", _id);
		httpServletRequest.setAttribute("liferay-staging:popover:text", _text);
		httpServletRequest.setAttribute(
			"liferay-staging:popover:title", _title);
	}

	private static final String _PAGE = "/popover/page.jsp";

	private String _id = StringPool.BLANK;
	private String _text = StringPool.BLANK;
	private String _title = StringPool.BLANK;

}