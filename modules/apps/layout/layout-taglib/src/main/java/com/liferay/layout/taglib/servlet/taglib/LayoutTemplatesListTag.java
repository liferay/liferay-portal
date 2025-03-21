/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.servlet.taglib;

import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutTemplatesListTag extends IncludeTag {

	public String getLayoutTemplateId() {
		return _layoutTemplateId;
	}

	public String getLayoutTemplateIdPrefix() {
		return _layoutTemplateIdPrefix;
	}

	public List<LayoutTemplate> getLayoutTemplates() {
		return _layoutTemplates;
	}

	public void setLayoutTemplateId(String layoutTemplateId) {
		_layoutTemplateId = layoutTemplateId;
	}

	public void setLayoutTemplateIdPrefix(String layoutTemplateIdPrefix) {
		_layoutTemplateIdPrefix = layoutTemplateIdPrefix;
	}

	public void setLayoutTemplates(List<LayoutTemplate> layoutTemplates) {
		_layoutTemplates = layoutTemplates;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_layoutTemplateId = null;
		_layoutTemplateIdPrefix = null;
		_layoutTemplates = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-layout:layout-templates-list:layoutTemplateId",
			_layoutTemplateId);
		httpServletRequest.setAttribute(
			"liferay-layout:layout-templates-list:layoutTemplateIdPrefix",
			_layoutTemplateIdPrefix);
		httpServletRequest.setAttribute(
			"liferay-layout:layout-templates-list:layoutTemplates",
			_layoutTemplates);
	}

	private static final String _PAGE = "/layout_templates_list/page.jsp";

	private String _layoutTemplateId;
	private String _layoutTemplateIdPrefix;
	private List<LayoutTemplate> _layoutTemplates;

}