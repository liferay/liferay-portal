/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.Map;

/**
 * @author Fabio Diego Mastrorilli
 */
public class InfoBoxTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest = getRequest();

		httpServletRequest.setAttribute(
			"liferay-commerce:info-box:actionContext", getActionContext());
		httpServletRequest.setAttribute(
			"liferay-commerce:info-box:actionLabel", getActionLabel());
		httpServletRequest.setAttribute(
			"liferay-commerce:info-box:actionTargetId", getActionTargetId());
		httpServletRequest.setAttribute(
			"liferay-commerce:info-box:actionUrl", getActionUrl());
		httpServletRequest.setAttribute(
			"liferay-commerce:info-box:elementClasses", getElementClasses());
		httpServletRequest.setAttribute(
			"liferay-commerce:info-box:title", getTitle());

		super.doStartTag();

		return EVAL_BODY_INCLUDE;
	}

	public Map<String, Object> getActionContext() {
		return _actionContext;
	}

	public String getActionLabel() {
		return _actionLabel;
	}

	public String getActionTargetId() {
		return _actionTargetId;
	}

	public String getActionUrl() {
		return _actionUrl;
	}

	public String getElementClasses() {
		return _elementClasses;
	}

	public String getTitle() {
		return _title;
	}

	public void setActionContext(Map<String, Object> actionContext) {
		_actionContext = actionContext;
	}

	public void setActionLabel(String actionLabel) {
		_actionLabel = actionLabel;
	}

	public void setActionTargetId(String actionTargetId) {
		_actionTargetId = actionTargetId;
	}

	public void setActionUrl(String actionUrl) {
		_actionUrl = actionUrl;
	}

	public void setElementClasses(String elementClasses) {
		_elementClasses = elementClasses;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setTitle(String title) {
		_title = title;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_actionContext = null;
		_actionLabel = null;
		_actionTargetId = null;
		_actionUrl = null;
		_elementClasses = null;
		_title = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	private static final String _END_PAGE = "/info_box/end.jsp";

	private static final String _START_PAGE = "/info_box/start.jsp";

	private Map<String, Object> _actionContext;
	private String _actionLabel;
	private String _actionTargetId;
	private String _actionUrl;
	private String _elementClasses;
	private String _title;

}