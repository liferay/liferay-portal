/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.taglib.servlet.taglib.base;

import com.liferay.data.engine.taglib.internal.servlet.ServletContextUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author Jeyvison Nascimento
 * @author Leonardo Barros
 * @generated
 */
public abstract class BaseDataLayoutRendererTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getContainerId() {
		return _containerId;
	}

	public java.lang.String getContentType() {
		return _contentType;
	}

	public java.lang.Long getDataDefinitionId() {
		return _dataDefinitionId;
	}

	public java.lang.Long getDataLayoutId() {
		return _dataLayoutId;
	}

	public java.lang.Long getDataRecordId() {
		return _dataRecordId;
	}

	public java.util.Map<java.lang.String, java.lang.Object> getDataRecordValues() {
		return _dataRecordValues;
	}

	public java.lang.String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public boolean getDisableFieldRepetition() {
		return _disableFieldRepetition;
	}

	public java.lang.String getDisplayType() {
		return _displayType;
	}

	public java.lang.String getLanguageId() {
		return _languageId;
	}

	public java.lang.String getNamespace() {
		return _namespace;
	}

	public boolean getPersistDefaultValues() {
		return _persistDefaultValues;
	}

	public boolean getPersisted() {
		return _persisted;
	}

	public boolean getReadOnly() {
		return _readOnly;
	}

	public boolean getSubmittable() {
		return _submittable;
	}

	public void setContainerId(java.lang.String containerId) {
		_containerId = containerId;
	}

	public void setContentType(java.lang.String contentType) {
		_contentType = contentType;
	}

	public void setDataDefinitionId(java.lang.Long dataDefinitionId) {
		_dataDefinitionId = dataDefinitionId;
	}

	public void setDataLayoutId(java.lang.Long dataLayoutId) {
		_dataLayoutId = dataLayoutId;
	}

	public void setDataRecordId(java.lang.Long dataRecordId) {
		_dataRecordId = dataRecordId;
	}

	public void setDataRecordValues(java.util.Map<java.lang.String, java.lang.Object> dataRecordValues) {
		_dataRecordValues = dataRecordValues;
	}

	public void setDefaultLanguageId(java.lang.String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setDisableFieldRepetition(boolean disableFieldRepetition) {
		_disableFieldRepetition = disableFieldRepetition;
	}

	public void setDisplayType(java.lang.String displayType) {
		_displayType = displayType;
	}

	public void setLanguageId(java.lang.String languageId) {
		_languageId = languageId;
	}

	public void setNamespace(java.lang.String namespace) {
		_namespace = namespace;
	}

	public void setPersistDefaultValues(boolean persistDefaultValues) {
		_persistDefaultValues = persistDefaultValues;
	}

	public void setPersisted(boolean persisted) {
		_persisted = persisted;
	}

	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	public void setSubmittable(boolean submittable) {
		_submittable = submittable;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_containerId = null;
		_contentType = null;
		_dataDefinitionId = null;
		_dataLayoutId = null;
		_dataRecordId = null;
		_dataRecordValues = null;
		_defaultLanguageId = null;
		_disableFieldRepetition = false;
		_displayType = null;
		_languageId = null;
		_namespace = null;
		_persistDefaultValues = false;
		_persisted = false;
		_readOnly = false;
		_submittable = true;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "containerId", _containerId);
		setNamespacedAttribute(request, "contentType", _contentType);
		setNamespacedAttribute(request, "dataDefinitionId", _dataDefinitionId);
		setNamespacedAttribute(request, "dataLayoutId", _dataLayoutId);
		setNamespacedAttribute(request, "dataRecordId", _dataRecordId);
		setNamespacedAttribute(request, "dataRecordValues", _dataRecordValues);
		setNamespacedAttribute(request, "defaultLanguageId", _defaultLanguageId);
		setNamespacedAttribute(request, "disableFieldRepetition", _disableFieldRepetition);
		setNamespacedAttribute(request, "displayType", _displayType);
		setNamespacedAttribute(request, "languageId", _languageId);
		setNamespacedAttribute(request, "namespace", _namespace);
		setNamespacedAttribute(request, "persistDefaultValues", _persistDefaultValues);
		setNamespacedAttribute(request, "persisted", _persisted);
		setNamespacedAttribute(request, "readOnly", _readOnly);
		setNamespacedAttribute(request, "submittable", _submittable);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "liferay-data-engine:data-layout-renderer:";

	private static final String _PAGE =
		"/data_layout_renderer/page.jsp";

	private java.lang.String _containerId = null;
	private java.lang.String _contentType = null;
	private java.lang.Long _dataDefinitionId = null;
	private java.lang.Long _dataLayoutId = null;
	private java.lang.Long _dataRecordId = null;
	private java.util.Map<java.lang.String, java.lang.Object> _dataRecordValues = null;
	private java.lang.String _defaultLanguageId = null;
	private boolean _disableFieldRepetition;
	private java.lang.String _displayType = null;
	private java.lang.String _languageId = null;
	private java.lang.String _namespace = null;
	private boolean _persistDefaultValues = false;
	private boolean _persisted = false;
	private boolean _readOnly = false;
	private boolean _submittable = true;

}