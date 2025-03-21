/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringPool;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class SearchPaginatorTag<R> extends SearchFormTag<R> {

	public String getId() {
		return _id;
	}

	public String getMarkupView() {
		return _markupView;
	}

	public String getType() {
		return _type;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setMarkupView(String markupView) {
		_markupView = markupView;
	}

	public void setType(String type) {
		_type = type;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_id = null;
		_markupView = StringPool.BLANK;
		_type = "regular";
	}

	@Override
	protected String getPage() {
		return "/html/taglib/ui/search_paginator/page.jsp";
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		httpServletRequest.setAttribute("liferay-ui:search:id", _id);
		httpServletRequest.setAttribute(
			"liferay-ui:search:markupView", _markupView);
		httpServletRequest.setAttribute("liferay-ui:search:type", _type);
	}

	private String _id;
	private String _markupView = StringPool.BLANK;
	private String _type = "regular";

}