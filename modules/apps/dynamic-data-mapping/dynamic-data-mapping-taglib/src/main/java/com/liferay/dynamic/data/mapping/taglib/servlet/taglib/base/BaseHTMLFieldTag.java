/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.taglib.servlet.taglib.base;

import com.liferay.dynamic.data.mapping.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Bruno Basto
 * @generated
 */
public abstract class BaseHTMLFieldTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public long getClassNameId() {
		return _classNameId;
	}

	public long getClassPK() {
		return _classPK;
	}

	public com.liferay.dynamic.data.mapping.storage.Field getField() {
		return _field;
	}

	public java.lang.String getFieldsNamespace() {
		return _fieldsNamespace;
	}

	public boolean getReadOnly() {
		return _readOnly;
	}

	public boolean getRepeatable() {
		return _repeatable;
	}

	public java.util.Locale getRequestedLocale() {
		return _requestedLocale;
	}

	public boolean getShowEmptyFieldLabel() {
		return _showEmptyFieldLabel;
	}

	public void setClassNameId(long classNameId) {
		_classNameId = classNameId;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setField(com.liferay.dynamic.data.mapping.storage.Field field) {
		_field = field;
	}

	public void setFieldsNamespace(java.lang.String fieldsNamespace) {
		_fieldsNamespace = fieldsNamespace;
	}

	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	public void setRepeatable(boolean repeatable) {
		_repeatable = repeatable;
	}

	public void setRequestedLocale(java.util.Locale requestedLocale) {
		_requestedLocale = requestedLocale;
	}

	public void setShowEmptyFieldLabel(boolean showEmptyFieldLabel) {
		_showEmptyFieldLabel = showEmptyFieldLabel;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_classNameId = 0;
		_classPK = 0;
		_field = null;
		_fieldsNamespace = null;
		_readOnly = false;
		_repeatable = true;
		_requestedLocale = null;
		_showEmptyFieldLabel = true;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "classNameId", _classNameId);
		setNamespacedAttribute(request, "classPK", _classPK);
		setNamespacedAttribute(request, "field", _field);
		setNamespacedAttribute(request, "fieldsNamespace", _fieldsNamespace);
		setNamespacedAttribute(request, "readOnly", _readOnly);
		setNamespacedAttribute(request, "repeatable", _repeatable);
		setNamespacedAttribute(request, "requestedLocale", _requestedLocale);
		setNamespacedAttribute(request, "showEmptyFieldLabel", _showEmptyFieldLabel);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "liferay-ddm:html-field:";

	private static final String _START_PAGE =
		"/html_field/start.jsp";

	private long _classNameId = 0;
	private long _classPK = 0;
	private com.liferay.dynamic.data.mapping.storage.Field _field = null;
	private java.lang.String _fieldsNamespace = null;
	private boolean _readOnly = false;
	private boolean _repeatable = true;
	private java.util.Locale _requestedLocale = null;
	private boolean _showEmptyFieldLabel = true;

}