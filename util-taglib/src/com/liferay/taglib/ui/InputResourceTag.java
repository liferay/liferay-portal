/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class InputResourceTag extends IncludeTag {

	public String getCssClass() {
		return _cssClass;
	}

	public String getId() {
		return _id;
	}

	public String getTitle() {
		return _title;
	}

	public String getUrl() {
		return _url;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setId(String id) {
		_id = id;
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

		_cssClass = null;
		_id = null;
		_title = null;
		_url = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:input-resource:cssClass", _cssClass);
		httpServletRequest.setAttribute("liferay-ui:input-resource:id", _id);
		httpServletRequest.setAttribute(
			"liferay-ui:input-resource:title", _title);
		httpServletRequest.setAttribute("liferay-ui:input-resource:url", _url);
	}

	private static final String _PAGE =
		"/html/taglib/ui/input_resource/page.jsp";

	private String _cssClass;
	private String _id;
	private String _title;
	private String _url;

}