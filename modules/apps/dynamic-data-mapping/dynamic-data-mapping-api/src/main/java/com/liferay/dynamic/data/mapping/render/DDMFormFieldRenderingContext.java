/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.render;

import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Pablo Carvalho
 */
public class DDMFormFieldRenderingContext {

	public DDMFormFieldRenderingContext() {
		setReturnFullContext(true);
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	public String getChildElementsHTML() {
		return _childElementsHTML;
	}

	public long getDDMFormInstanceId() {
		return _ddmFormInstanceId;
	}

	public Fields getFields() {
		return _fields;
	}

	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	public String getLabel() {
		return MapUtil.getString(_properties, "label");
	}

	public Locale getLocale() {
		return _locale;
	}

	public String getMode() {
		return _mode;
	}

	public String getName() {
		return MapUtil.getString(_properties, "name");
	}

	public String getNamespace() {
		return _namespace;
	}

	public String getPortletNamespace() {
		return _portletNamespace;
	}

	public Map<String, Object> getProperties() {
		return _properties;
	}

	public Object getProperty(String name) {
		return _properties.get(name);
	}

	public String getTip() {
		return MapUtil.getString(_properties, "tip");
	}

	public String getValue() {
		return MapUtil.getString(_properties, "value");
	}

	public boolean isReadOnly() {
		return MapUtil.getBoolean(_properties, "readOnly");
	}

	public boolean isRequired() {
		return MapUtil.getBoolean(_properties, "required");
	}

	public boolean isReturnFullContext() {
		return MapUtil.getBoolean(_properties, "returnFullContext");
	}

	public boolean isShowEmptyFieldLabel() {
		return _showEmptyFieldLabel;
	}

	public boolean isViewMode() {
		return MapUtil.getBoolean(_properties, "viewMode");
	}

	public boolean isVisible() {
		return MapUtil.getBoolean(_properties, "visible");
	}

	public void setDDMFormInstanceId(long ddmFormInstanceId) {
		_ddmFormInstanceId = ddmFormInstanceId;
	}

	public void setField(Field field) {
		Fields fields = new Fields();

		fields.put(field);

		_fields = fields;
	}

	public void setFields(Fields fields) {
		_fields = fields;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void setHttpServletResponse(
		HttpServletResponse httpServletResponse) {

		_httpServletResponse = httpServletResponse;
	}

	public void setLabel(String label) {
		_properties.put("label", label);
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setMode(String mode) {
		_mode = mode;
	}

	public void setName(String name) {
		_properties.put("name", name);
	}

	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	public void setPortletNamespace(String portletNamespace) {
		_portletNamespace = portletNamespace;
	}

	public void setProperties(Map<String, Object> properties) {
		_properties.putAll(properties);
	}

	public void setProperty(String name, Object value) {
		_properties.put(name, value);
	}

	public void setReadOnly(boolean readOnly) {
		_properties.put("readOnly", readOnly);
	}

	public void setRequired(boolean required) {
		_properties.put("required", required);
	}

	public void setReturnFullContext(boolean fullContext) {
		_properties.put("returnFullContext", fullContext);
	}

	public void setShowEmptyFieldLabel(boolean showEmptyFieldLabel) {
		_showEmptyFieldLabel = showEmptyFieldLabel;
	}

	public void setTip(String tip) {
		_properties.put("tip", tip);
	}

	public void setValue(String value) {
		_properties.put("value", value);
	}

	public void setViewMode(boolean viewMode) {
		_properties.put("viewMode", viewMode);
	}

	public void setVisible(boolean visible) {
		_properties.put("visible", visible);
	}

	private String _childElementsHTML;
	private long _ddmFormInstanceId;
	private Fields _fields;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private Locale _locale;
	private String _mode;
	private String _namespace;
	private String _portletNamespace;
	private final Map<String, Object> _properties = new HashMap<>();
	private boolean _showEmptyFieldLabel;

}