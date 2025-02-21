/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.renderer;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Leonardo Barros
 */
public class DataLayoutRendererContext {

	public String getContainerId() {
		return _containerId;
	}

	public String getContentType() {
		return _contentType;
	}

	public Map<String, Object> getDataRecordValues() {
		return _dataRecordValues;
	}

	public String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public String getDisplayType() {
		return _displayType;
	}

	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	public String getLanguageId() {
		return _languageId;
	}

	public String getPortletNamespace() {
		return _portletNamespace;
	}

	public boolean isDisableFieldRepetition() {
		return _disableFieldRepetition;
	}

	public boolean isPersistDefaultValues() {
		return _persistDefaultValues;
	}

	public boolean isPersisted() {
		return _persisted;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public boolean isSubmittable() {
		return _submittable;
	}

	public void setContainerId(String containerId) {
		_containerId = containerId;
	}

	public void setContentType(String contentType) {
		_contentType = contentType;
	}

	public void setDataRecordValues(Map<String, Object> dataRecordValues) {
		_dataRecordValues = dataRecordValues;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setDisableFieldRepetition(boolean disableFieldRepetition) {
		_disableFieldRepetition = disableFieldRepetition;
	}

	public void setDisplayType(String displayType) {
		_displayType = displayType;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void setHttpServletResponse(
		HttpServletResponse httpServletResponse) {

		_httpServletResponse = httpServletResponse;
	}

	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	public void setPersistDefaultValues(boolean persistDefaultValues) {
		_persistDefaultValues = persistDefaultValues;
	}

	public void setPersisted(boolean persisted) {
		_persisted = persisted;
	}

	public void setPortletNamespace(String portletNamespace) {
		_portletNamespace = portletNamespace;
	}

	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	public void setSubmittable(boolean submittable) {
		_submittable = submittable;
	}

	private String _containerId;
	private String _contentType;
	private Map<String, Object> _dataRecordValues;
	private String _defaultLanguageId;
	private boolean _disableFieldRepetition;
	private String _displayType;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private String _languageId;
	private boolean _persistDefaultValues;
	private boolean _persisted;
	private String _portletNamespace;
	private boolean _readOnly;
	private boolean _submittable;

}