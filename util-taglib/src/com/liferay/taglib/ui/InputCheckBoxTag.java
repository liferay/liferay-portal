/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class InputCheckBoxTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		_updateFormCheckboxNames();

		return super.doEndTag();
	}

	public String getAutoComplete() {
		return _autoComplete;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getFormName() {
		return _formName;
	}

	public String getId() {
		return _id;
	}

	public String getOnClick() {
		return _onClick;
	}

	public String getParam() {
		return _param;
	}

	public boolean isDefaultValue() {
		return _defaultValue;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public void setAutoComplete(String autoComplete) {
		_autoComplete = autoComplete;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDefaultValue(boolean defaultValue) {
		_defaultValue = defaultValue;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setOnClick(String onClick) {
		_onClick = onClick;
	}

	public void setParam(String param) {
		_param = param;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_autoComplete = null;
		_cssClass = null;
		_defaultValue = false;
		_disabled = false;
		_formName = "fm";
		_id = null;
		_onClick = null;
		_param = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:input-checkbox:autoComplete", _autoComplete);
		httpServletRequest.setAttribute(
			"liferay-ui:input-checkbox:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:input-checkbox:defaultValue", _defaultValue);
		httpServletRequest.setAttribute(
			"liferay-ui:input-checkbox:disabled", String.valueOf(_disabled));
		httpServletRequest.setAttribute(
			"liferay-ui:input-checkbox:formName", _formName);
		httpServletRequest.setAttribute("liferay-ui:input-checkbox:id", _id);
		httpServletRequest.setAttribute(
			"liferay-ui:input-checkbox:onClick", _onClick);
		httpServletRequest.setAttribute(
			"liferay-ui:input-checkbox:param", _param);
	}

	private void _updateFormCheckboxNames() {
		HttpServletRequest httpServletRequest = getRequest();

		List<String> checkboxNames =
			(List<String>)httpServletRequest.getAttribute(
				"aui:form:checkboxNames");

		if (checkboxNames != null) {
			checkboxNames.add(_param);
		}
	}

	private static final String _PAGE =
		"/html/taglib/ui/input_checkbox/page.jsp";

	private String _autoComplete;
	private String _cssClass;
	private boolean _defaultValue;
	private boolean _disabled;
	private String _formName = "fm";
	private String _id;
	private String _onClick;
	private String _param;

}