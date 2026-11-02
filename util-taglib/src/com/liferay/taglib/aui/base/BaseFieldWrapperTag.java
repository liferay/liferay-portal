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
public abstract class BaseFieldWrapperTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getCssClass() {
		return _cssClass;
	}

	public Object getData() {
		return _data;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public boolean getFirst() {
		return _first;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public boolean getInlineField() {
		return _inlineField;
	}

	public String getInlineLabel() {
		return _inlineLabel;
	}

	public String getLabel() {
		return _label;
	}

	public boolean getLast() {
		return _last;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public String getName() {
		return _name;
	}

	public boolean getRequired() {
		return _required;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(Object data) {
		_data = data;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setFirst(boolean first) {
		_first = first;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setInlineField(boolean inlineField) {
		_inlineField = inlineField;
	}

	public void setInlineLabel(String inlineLabel) {
		_inlineLabel = inlineLabel;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setLast(boolean last) {
		_last = last;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_data = null;
		_disabled = false;
		_first = false;
		_helpMessage = null;
		_inlineField = false;
		_inlineLabel = null;
		_label = null;
		_last = false;
		_localizeLabel = true;
		_name = null;
		_required = false;
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
		setNamespacedAttribute(httpServletRequest, "data", _data);
		setNamespacedAttribute(httpServletRequest, "disabled", _disabled);
		setNamespacedAttribute(httpServletRequest, "first", _first);
		setNamespacedAttribute(httpServletRequest, "helpMessage", _helpMessage);
		setNamespacedAttribute(httpServletRequest, "inlineField", _inlineField);
		setNamespacedAttribute(httpServletRequest, "inlineLabel", _inlineLabel);
		setNamespacedAttribute(httpServletRequest, "label", _label);
		setNamespacedAttribute(httpServletRequest, "last", _last);
		setNamespacedAttribute(
			httpServletRequest, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(httpServletRequest, "name", _name);
		setNamespacedAttribute(httpServletRequest, "required", _required);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:field-wrapper:";

	private static final String _END_PAGE =
		"/html/taglib/aui/field_wrapper/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/field_wrapper/start.jsp";

	private String _cssClass;
	private Object _data;
	private boolean _disabled;
	private boolean _first;
	private String _helpMessage;
	private boolean _inlineField;
	private String _inlineLabel;
	private String _label;
	private boolean _last;
	private boolean _localizeLabel = true;
	private String _name;
	private boolean _required;

}