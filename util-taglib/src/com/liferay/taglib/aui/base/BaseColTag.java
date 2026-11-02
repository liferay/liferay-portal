/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseColTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getId() {
		return _id;
	}

	public String getLg() {
		return _lg;
	}

	public String getMd() {
		return _md;
	}

	public String getSm() {
		return _sm;
	}

	public int getSpan() {
		return _span;
	}

	public int getWidth() {
		return _width;
	}

	public String getXs() {
		return _xs;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setLg(String lg) {
		_lg = lg;
	}

	public void setMd(String md) {
		_md = md;
	}

	public void setSm(String sm) {
		_sm = sm;
	}

	public void setSpan(int span) {
		_span = span;
	}

	public void setWidth(int width) {
		_width = width;
	}

	public void setXs(String xs) {
		_xs = xs;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_id = null;
		_lg = null;
		_md = null;
		_sm = null;
		_span = 12;
		_width = 0;
		_xs = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(httpServletRequest, "id", _id);
		setNamespacedAttribute(httpServletRequest, "lg", _lg);
		setNamespacedAttribute(httpServletRequest, "md", _md);
		setNamespacedAttribute(httpServletRequest, "sm", _sm);
		setNamespacedAttribute(httpServletRequest, "span", _span);
		setNamespacedAttribute(httpServletRequest, "width", _width);
		setNamespacedAttribute(httpServletRequest, "xs", _xs);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:col:";

	private static final String _END_PAGE = "/html/taglib/aui/col/end.jsp";

	private static final String _START_PAGE = "/html/taglib/aui/col/start.jsp";

	private String _cssClass;
	private String _id;
	private String _lg;
	private String _md;
	private String _sm;
	private int _span = 12;
	private int _width;
	private String _xs;

}