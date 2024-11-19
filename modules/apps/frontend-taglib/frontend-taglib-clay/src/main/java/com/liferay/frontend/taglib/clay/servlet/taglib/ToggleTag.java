/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.TagResourceBundleUtil;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * @author Kevin Tan
 */
public class ToggleTag extends BaseContainerTag {

	@Override
	public int doEndTag() throws JspException {
		_updateFormCheckboxNames();

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public String getHelpText() {
		return _helpText;
	}

	@Override
	public String getId() {
		return _id;
	}

	public String getLabel() {
		return _label;
	}

	public String getName() {
		return _name;
	}

	public String getOffLabel() {
		return _offLabel;
	}

	public String getOffSymbol() {
		return _offSymbol;
	}

	public String getOnLabel() {
		return _onLabel;
	}

	public String getOnSymbol() {
		return _onSymbol;
	}

	public String getRole() {
		return _role;
	}

	public String getSizing() {
		return _sizing;
	}

	public boolean getToggled() {
		return _toggled;
	}

	public String getValue() {
		return _value;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setHelpText(String helpText) {
		_helpText = helpText;
	}

	@Override
	public void setId(String id) {
		_id = id;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOffLabel(String offLabel) {
		_offLabel = offLabel;
	}

	public void setOffSymbol(String offSymbol) {
		_offSymbol = offSymbol;
	}

	public void setOnLabel(String onLabel) {
		_onLabel = onLabel;
	}

	public void setOnSymbol(String onSymbol) {
		_onSymbol = onSymbol;
	}

	public void setRole(String role) {
		_role = role;
	}

	public void setSizing(String sizing) {
		_sizing = sizing;
	}

	public void setToggled(boolean toggled) {
		_toggled = toggled;
	}

	public void setValue(String value) {
		_value = value;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_disabled = false;
		_helpText = null;
		_id = null;
		_label = null;
		_name = null;
		_offLabel = null;
		_offSymbol = null;
		_onLabel = null;
		_onSymbol = null;
		_role = null;
		_sizing = null;
		_toggled = false;
		_type = null;
		_value = null;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{Toggle} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("disabled", _disabled);
		props.put(
			"helpText",
			LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext),
				_helpText));
		props.put("id", _id);
		props.put(
			"label",
			LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext), _label));
		props.put(
			"offLabel",
			LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext),
				_offLabel));
		props.put(
			"onLabel",
			LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext),
				_onLabel));
		props.put("name", _name);

		if (Validator.isNotNull(_role)) {
			props.put("role", _role);
		}

		props.put("sizing", _sizing);
		props.put("toggled", _toggled);

		if (Validator.isNotNull(_type)) {
			props.put("type", _type);
		}

		if (Validator.isNotNull(_value)) {
			props.put("value", _value);
		}

		if (Validator.isNotNull(_offSymbol) || Validator.isNotNull(_onSymbol)) {
			JSONObject symbolJSONObject = JSONFactoryUtil.createJSONObject();

			if (Validator.isNotNull(_offSymbol)) {
				symbolJSONObject.put("off", _offSymbol);
			}

			if (Validator.isNotNull(_onSymbol)) {
				symbolJSONObject.put("on", _onSymbol);
			}

			props.put("symbol", symbolJSONObject);
		}

		return super.prepareProps(props);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<label class=\"toggle-switch");

		if (_disabled) {
			jspWriter.write(" disabled");
		}

		if (Validator.isNotNull(_sizing)) {
			jspWriter.write(" simple-toggle-switch toggle-switch-" + _sizing);
		}

		jspWriter.write("\">");
		jspWriter.write("<span class=\"toggle-switch-check-bar\">");
		jspWriter.write("<input class=\"toggle-switch-check\"");

		if (_toggled) {
			jspWriter.write(" checked");
		}

		if (_disabled) {
			jspWriter.write(" disabled");
		}

		if (Validator.isNotNull(_id)) {
			jspWriter.write(" id=\"");
			jspWriter.write(_id);
			jspWriter.write("\"");
		}

		if (Validator.isNotNull(_name)) {
			jspWriter.write(" name=\"");
			jspWriter.write(_name);
			jspWriter.write("\"");
		}

		if (Validator.isNotNull(_role)) {
			jspWriter.write(" role=\"");
			jspWriter.write(_role);
			jspWriter.write("\"");
		}
		else {
			jspWriter.write(" role=\"switch\"");
		}

		if (Validator.isNotNull(_type)) {
			jspWriter.write(" type=\"");
			jspWriter.write(_type);
			jspWriter.write("\"");
		}
		else {
			jspWriter.write(" type=\"checkbox\"");
		}

		if (Validator.isNotNull(_value)) {
			jspWriter.write(" value=\"");
			jspWriter.write(_value);
			jspWriter.write("\"");
		}

		jspWriter.write("/>");

		jspWriter.write(
			"<span aria-hidden=\"true\" class=\"toggle-switch-bar\">");
		jspWriter.write("<span class=\"toggle-switch-handle\"></span>");
		jspWriter.write("</span>");

		if (Validator.isNotNull(_label) || Validator.isNotNull(_offLabel) ||
			Validator.isNotNull(_onLabel)) {

			jspWriter.write("<span class=\"toggle-switch-label\">");

			if (Validator.isNotNull(_offLabel) && !_toggled) {
				jspWriter.write(
					LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						_offLabel));
			}
			else if (Validator.isNotNull(_onLabel) && _toggled) {
				jspWriter.write(
					LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						_onLabel));
			}
			else if (Validator.isNotNull(_label)) {
				jspWriter.write(
					LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						_label));
			}

			jspWriter.write("</span>");
		}

		jspWriter.write("</label>");

		return SKIP_BODY;
	}

	private void _updateFormCheckboxNames() {
		HttpServletRequest httpServletRequest = getRequest();

		List<String> checkboxNames =
			(List<String>)httpServletRequest.getAttribute(
				"LIFERAY_SHARED_aui:form:checkboxNames");

		if (checkboxNames != null) {
			checkboxNames.add(_name);
		}
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:toggle:";

	private boolean _disabled;
	private String _helpText;
	private String _id;
	private String _label;
	private String _name;
	private String _offLabel;
	private String _offSymbol;
	private String _onLabel;
	private String _onSymbol;
	private String _role;
	private String _sizing;
	private boolean _toggled;
	private String _type;
	private String _value;

}