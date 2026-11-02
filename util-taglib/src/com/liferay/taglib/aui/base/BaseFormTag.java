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
public abstract class BaseFormTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getAction() {
		return _action;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public boolean getEscapeXml() {
		return _escapeXml;
	}

	public boolean getInlineLabels() {
		return _inlineLabels;
	}

	public String getMethod() {
		return _method;
	}

	public String getName() {
		return _name;
	}

	public String getOnSubmit() {
		return _onSubmit;
	}

	public String getPortletNamespace() {
		return _portletNamespace;
	}

	public boolean getUseNamespace() {
		return _useNamespace;
	}

	public boolean getValidateOnBlur() {
		return _validateOnBlur;
	}

	public void setAction(String action) {
		_action = action;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setEscapeXml(boolean escapeXml) {
		_escapeXml = escapeXml;
	}

	public void setInlineLabels(boolean inlineLabels) {
		_inlineLabels = inlineLabels;
	}

	public void setMethod(String method) {
		_method = method;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOnSubmit(String onSubmit) {
		_onSubmit = onSubmit;
	}

	public void setPortletNamespace(String portletNamespace) {
		_portletNamespace = portletNamespace;
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	public void setValidateOnBlur(boolean validateOnBlur) {
		_validateOnBlur = validateOnBlur;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_action = null;
		_cssClass = null;
		_escapeXml = true;
		_inlineLabels = false;
		_method = "post";
		_name = "fm";
		_onSubmit = null;
		_portletNamespace = null;
		_useNamespace = true;
		_validateOnBlur = true;
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
		setNamespacedAttribute(httpServletRequest, "action", _action);
		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(httpServletRequest, "escapeXml", _escapeXml);
		setNamespacedAttribute(
			httpServletRequest, "inlineLabels", _inlineLabels);
		setNamespacedAttribute(httpServletRequest, "method", _method);
		setNamespacedAttribute(httpServletRequest, "name", _name);
		setNamespacedAttribute(httpServletRequest, "onSubmit", _onSubmit);
		setNamespacedAttribute(
			httpServletRequest, "portletNamespace", _portletNamespace);
		setNamespacedAttribute(
			httpServletRequest, "useNamespace", _useNamespace);
		setNamespacedAttribute(
			httpServletRequest, "validateOnBlur", _validateOnBlur);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:form:";

	private static final String _END_PAGE = "/html/taglib/aui/form/end.jsp";

	private static final String _START_PAGE = "/html/taglib/aui/form/start.jsp";

	private String _action;
	private String _cssClass;
	private boolean _escapeXml = true;
	private boolean _inlineLabels;
	private String _method = "post";
	private String _name = "fm";
	private String _onSubmit;
	private String _portletNamespace;
	private boolean _useNamespace = true;
	private boolean _validateOnBlur = true;

}