/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Set;

/**
 * @author Chema Balsas
 */
public class RadioTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public boolean getChecked() {
		return _checked;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public boolean getInline() {
		return _inline;
	}

	public String getLabel() {
		return _label;
	}

	public String getName() {
		return _name;
	}

	public boolean getShowLabel() {
		return _showLabel;
	}

	public String getValue() {
		return _value;
	}

	public void setChecked(boolean checked) {
		_checked = checked;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setInline(boolean inline) {
		_inline = inline;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setShowLabel(boolean showLabel) {
		_showLabel = showLabel;
	}

	public void setValue(String value) {
		_value = value;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_checked = false;
		_disabled = false;
		_inline = false;
		_label = null;
		_name = null;
		_showLabel = true;
		_value = null;
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("custom-control");
		cssClasses.add("custom-radio");

		if (_inline) {
			cssClasses.add("custom-control-inline");
		}

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<label><input");

		if (_checked) {
			jspWriter.write(" checked");
		}

		jspWriter.write(" class=\"custom-control-input\"");

		if (_disabled) {
			jspWriter.write(" disabled");
		}

		if (Validator.isNotNull(_name)) {
			jspWriter.write(" name=\"");
			jspWriter.write(_name);
			jspWriter.write("\"");
		}

		jspWriter.write(" role=\"radio\" type=\"radio\"");

		if (Validator.isNotNull(_value)) {
			jspWriter.write(" value=\"");
			jspWriter.write(_value);
			jspWriter.write("\"");
		}

		jspWriter.write("/><span class=\"custom-control-label\"><span");
		jspWriter.write(" class=\"custom-control-label-text");

		if (!_showLabel) {
			jspWriter.write(" sr-only");
		}

		jspWriter.write("\">");
		jspWriter.write(_label);
		jspWriter.write("</span></span></label>");

		return SKIP_BODY;
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:radio:";

	private boolean _checked;
	private boolean _disabled;
	private boolean _inline;
	private String _label;
	private String _name;
	private boolean _showLabel = true;
	private String _value;

}