/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseFormTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getAction() {
		return _action;
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public boolean getEscapeXml() {
		return _escapeXml;
	}

	public boolean getInlineLabels() {
		return _inlineLabels;
	}

	public java.lang.String getMethod() {
		return _method;
	}

	public java.lang.String getName() {
		return _name;
	}

	public java.lang.String getOnSubmit() {
		return _onSubmit;
	}

	public java.lang.String getPortletNamespace() {
		return _portletNamespace;
	}

	public boolean getUseNamespace() {
		return _useNamespace;
	}

	public boolean getValidateOnBlur() {
		return _validateOnBlur;
	}

	public void setAction(java.lang.String action) {
		_action = action;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setEscapeXml(boolean escapeXml) {
		_escapeXml = escapeXml;
	}

	public void setInlineLabels(boolean inlineLabels) {
		_inlineLabels = inlineLabels;
	}

	public void setMethod(java.lang.String method) {
		_method = method;
	}

	public void setName(java.lang.String name) {
		_name = name;
	}

	public void setOnSubmit(java.lang.String onSubmit) {
		_onSubmit = onSubmit;
	}

	public void setPortletNamespace(java.lang.String portletNamespace) {
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
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "action", _action);
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "escapeXml", _escapeXml);
		setNamespacedAttribute(request, "inlineLabels", _inlineLabels);
		setNamespacedAttribute(request, "method", _method);
		setNamespacedAttribute(request, "name", _name);
		setNamespacedAttribute(request, "onSubmit", _onSubmit);
		setNamespacedAttribute(request, "portletNamespace", _portletNamespace);
		setNamespacedAttribute(request, "useNamespace", _useNamespace);
		setNamespacedAttribute(request, "validateOnBlur", _validateOnBlur);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:form:";

	private static final String _END_PAGE =
		"/html/taglib/aui/form/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/form/start.jsp";

	private java.lang.String _action = null;
	private java.lang.String _cssClass = null;
	private boolean _escapeXml = true;
	private boolean _inlineLabels = false;
	private java.lang.String _method = "post";
	private java.lang.String _name = "fm";
	private java.lang.String _onSubmit = null;
	private java.lang.String _portletNamespace = null;
	private boolean _useNamespace = true;
	private boolean _validateOnBlur = true;

}