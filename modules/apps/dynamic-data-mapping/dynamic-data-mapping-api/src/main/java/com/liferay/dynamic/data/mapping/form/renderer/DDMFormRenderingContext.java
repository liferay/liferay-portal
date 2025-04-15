/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer;

import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Marcellus Tavares
 */
public class DDMFormRenderingContext {

	public DDMFormRenderingContext() {
		setContainerId(_getDefaultContainerId());
		setEditOnlyInDefaultLanguage(false);
		setReturnFullContext(true);
	}

	public void addProperty(String key, Object value) {
		_properties.put(key, value);
	}

	public String getCancelLabel() {
		return _cancelLabel;
	}

	public String getContainerId() {
		return _containerId;
	}

	public long getDDMFormInstanceId() {
		return _ddmFormInstanceId;
	}

	public DDMFormValues getDDMFormValues() {
		return _ddmFormValues;
	}

	public long getDDMStructureLayoutId() {
		return _ddmStructureLayoutId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	public Locale getLocale() {
		return _locale;
	}

	public String getPortletNamespace() {
		return _portletNamespace;
	}

	public <T> T getProperty(String key) {
		return (T)_properties.get(key);
	}

	public String getRedirectURL() {
		return _redirectURL;
	}

	public String getSubmitLabel() {
		return _submitLabel;
	}

	public boolean isDisableFieldRepetition() {
		return _disableFieldRepetition;
	}

	public boolean isEditOnlyInDefaultLanguage() {
		return MapUtil.getBoolean(_properties, "editOnlyInDefaultLanguage");
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public boolean isReturnFullContext() {
		return MapUtil.getBoolean(_properties, "returnFullContext");
	}

	public boolean isSharedURL() {
		return MapUtil.getBoolean(_properties, "sharedURL");
	}

	public boolean isShowCancelButton() {
		return _showCancelButton;
	}

	public boolean isShowRequiredFieldsWarning() {
		return _showRequiredFieldsWarning;
	}

	public boolean isShowSubmitButton() {
		return _showSubmitButton;
	}

	public boolean isSubmittable() {
		return _submittable;
	}

	public boolean isViewMode() {
		return MapUtil.getBoolean(_properties, "viewMode");
	}

	public void setCancelLabel(String cancelLabel) {
		_cancelLabel = cancelLabel;
	}

	public void setContainerId(String containerId) {
		_containerId = containerId;
	}

	public void setDDMFormInstanceId(long ddmFormInstanceId) {
		_ddmFormInstanceId = ddmFormInstanceId;
	}

	public void setDDMFormValues(DDMFormValues ddmFormValues) {
		_ddmFormValues = ddmFormValues;
	}

	public void setDDMStructureLayoutId(long ddmStructureLayoutId) {
		_ddmStructureLayoutId = ddmStructureLayoutId;
	}

	public void setDisableFieldRepetition(boolean disableFieldRepetition) {
		_disableFieldRepetition = disableFieldRepetition;
	}

	public void setEditOnlyInDefaultLanguage(
		boolean editOnlyInDefaultLanguage) {

		_properties.put("editOnlyInDefaultLanguage", editOnlyInDefaultLanguage);
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void setHttpServletResponse(
		HttpServletResponse httpServletResponse) {

		_httpServletResponse = httpServletResponse;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setPortletNamespace(String portletNamespace) {
		_portletNamespace = portletNamespace;
	}

	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	public void setRedirectURL(String redirectURL) {
		_redirectURL = redirectURL;
	}

	public void setReturnFullContext(boolean fullContext) {
		_properties.put("returnFullContext", fullContext);
	}

	public void setSharedURL(boolean sharedURL) {
		_properties.put("sharedURL", sharedURL);
	}

	public void setShowCancelButton(boolean showCancelButton) {
		_showCancelButton = showCancelButton;
	}

	public void setShowRequiredFieldsWarning(
		boolean showRequiredFieldsWarning) {

		_showRequiredFieldsWarning = showRequiredFieldsWarning;
	}

	public void setShowSubmitButton(boolean showSubmitButton) {
		_showSubmitButton = showSubmitButton;
	}

	public void setSubmitLabel(String submitLabel) {
		_submitLabel = submitLabel;
	}

	public void setSubmittable(boolean submittable) {
		_submittable = submittable;
	}

	public void setViewMode(boolean viewMode) {
		_properties.put("viewMode", viewMode);
	}

	private String _getDefaultContainerId() {
		return "ddmForm".concat(StringUtil.randomString());
	}

	private String _cancelLabel;
	private String _containerId;
	private long _ddmFormInstanceId;
	private DDMFormValues _ddmFormValues;
	private long _ddmStructureLayoutId;
	private boolean _disableFieldRepetition;
	private long _groupId;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private Locale _locale;
	private String _portletNamespace;
	private final Map<String, Object> _properties = new HashMap<>();
	private boolean _readOnly;
	private String _redirectURL;
	private boolean _showCancelButton = true;
	private boolean _showRequiredFieldsWarning = true;
	private boolean _showSubmitButton = true;
	private String _submitLabel;
	private boolean _submittable = true;

}