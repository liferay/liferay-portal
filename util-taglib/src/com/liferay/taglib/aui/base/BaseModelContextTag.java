/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseModelContextTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public Object getBean() {
		return _bean;
	}

	public String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public Class<?> getModel() {
		return _model;
	}

	public void setBean(Object bean) {
		_bean = bean;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setModel(Class<?> model) {
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

	private static final String _ATTRIBUTE_NAMESPACE = "aui:model-context:";

	private static final String _PAGE =
		"/html/taglib/aui/model_context/page.jsp";

	private Object _bean;
	private String _defaultLanguageId;
	private Class<?> _model;

}