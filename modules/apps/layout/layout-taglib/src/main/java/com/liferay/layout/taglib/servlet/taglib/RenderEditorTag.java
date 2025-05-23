/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.servlet.taglib;

import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.taglib.util.IncludeTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * @author Verónica González
 */
public class RenderEditorTag extends IncludeTag {

	public String getContent() {
		return _content;
	}

	public String getLabel() {
		return _label;
	}

	public String getLayoutMode() {
		return _layoutMode;
	}

	public String getName() {
		return _name;
	}

	public boolean isRequired() {
		return _required;
	}

	public void setContent(String content) {
		_content = content;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setLayoutMode(String layoutMode) {
		_layoutMode = layoutMode;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_content = null;
		_label = null;
		_layoutMode = null;
		_name = null;
		_required = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		try {
			httpServletRequest.setAttribute(
				"liferay-layout:render-editor:content", _content);
			httpServletRequest.setAttribute(
				"liferay-layout:render-editor:label", _label);
			httpServletRequest.setAttribute(
				"liferay-layout:render-editor:layoutMode", _layoutMode);
			httpServletRequest.setAttribute(
				"liferay-layout:render-editor:name", _name);
			httpServletRequest.setAttribute(
				"liferay-layout:render-editor:required", _required);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final String _PAGE = "/render_editor/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		RenderEditorTag.class);

	private String _content;
	private String _label;
	private String _layoutMode;
	private String _name;
	private boolean _required;

}