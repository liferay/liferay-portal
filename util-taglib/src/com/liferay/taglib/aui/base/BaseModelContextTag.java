/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseModelContextTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.Object getBean() {
		return _bean;
	}

	public java.lang.String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public java.lang.Class<?> getModel() {
		return _model;
	}

	public void setBean(java.lang.Object bean) {
		_bean = bean;
	}

	public void setDefaultLanguageId(java.lang.String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setModel(java.lang.Class<?> model) {
		_model = model;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_bean = null;
		_defaultLanguageId = null;
		_model = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:model-context:";

	private static final String _PAGE =
		"/html/taglib/aui/model_context/page.jsp";

	private java.lang.Object _bean = null;
	private java.lang.String _defaultLanguageId = null;
	private java.lang.Class<?> _model = null;

}