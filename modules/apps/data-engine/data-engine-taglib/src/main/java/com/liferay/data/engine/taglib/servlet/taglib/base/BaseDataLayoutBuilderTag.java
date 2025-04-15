/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.taglib.servlet.taglib.base;

import com.liferay.data.engine.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Jeyvison Nascimento
 * @author Leonardo Barros
 * @generated
 */
public abstract class BaseDataLayoutBuilderTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.util.List<java.util.Map<java.lang.String, java.lang.Object>> getAdditionalPanels() {
		return _additionalPanels;
	}

	public java.lang.String getComponentId() {
		return _componentId;
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

	public boolean getDisplayFieldName() {
		return _displayFieldName;
	}

	public java.lang.String getFieldSetContentType() {
		return _fieldSetContentType;
	}

	public java.lang.Long getGroupId() {
		return _groupId;
	}

	public boolean getLocalizable() {
		return _localizable;
	}

	public jakarta.servlet.ServletContext getModuleServletContext() {
		return _moduleServletContext;
	}

	public java.lang.String getNamespace() {
		return _namespace;
	}

	public java.util.Set getScopes() {
		return _scopes;
	}

	public boolean getSearchableFieldsDisabled() {
		return _searchableFieldsDisabled;
	}

	public java.lang.String getSubmitButtonId() {
		return _submitButtonId;
	}

	public void setAdditionalPanels(java.util.List<java.util.Map<java.lang.String, java.lang.Object>> additionalPanels) {
		_additionalPanels = additionalPanels;
	}

	public void setComponentId(java.lang.String componentId) {
		_componentId = componentId;
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

	public void setDisplayFieldName(boolean displayFieldName) {
		_displayFieldName = displayFieldName;
	}

	public void setFieldSetContentType(java.lang.String fieldSetContentType) {
		_fieldSetContentType = fieldSetContentType;
	}

	public void setGroupId(java.lang.Long groupId) {
		_groupId = groupId;
	}

	public void setLocalizable(boolean localizable) {
		_localizable = localizable;
	}

	public void setModuleServletContext(jakarta.servlet.ServletContext moduleServletContext) {
		_moduleServletContext = moduleServletContext;
	}

	public void setNamespace(java.lang.String namespace) {
		_namespace = namespace;
	}

	public void setScopes(java.util.Set scopes) {
		_scopes = scopes;
	}

	public void setSearchableFieldsDisabled(boolean searchableFieldsDisabled) {
		_searchableFieldsDisabled = searchableFieldsDisabled;
	}

	public void setSubmitButtonId(java.lang.String submitButtonId) {
		_submitButtonId = submitButtonId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_additionalPanels = null;
		_componentId = null;
		_contentType = null;
		_dataDefinitionId = null;
		_dataLayoutId = null;
		_displayFieldName = false;
		_fieldSetContentType = null;
		_groupId = null;
		_localizable = false;
		_moduleServletContext = null;
		_namespace = null;
		_scopes = null;
		_searchableFieldsDisabled = false;
		_submitButtonId = null;
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
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "additionalPanels", _additionalPanels);
		setNamespacedAttribute(request, "componentId", _componentId);
		setNamespacedAttribute(request, "contentType", _contentType);
		setNamespacedAttribute(request, "dataDefinitionId", _dataDefinitionId);
		setNamespacedAttribute(request, "dataLayoutId", _dataLayoutId);
		setNamespacedAttribute(request, "displayFieldName", _displayFieldName);
		setNamespacedAttribute(request, "fieldSetContentType", _fieldSetContentType);
		setNamespacedAttribute(request, "groupId", _groupId);
		setNamespacedAttribute(request, "localizable", _localizable);
		setNamespacedAttribute(request, "moduleServletContext", _moduleServletContext);
		setNamespacedAttribute(request, "namespace", _namespace);
		setNamespacedAttribute(request, "scopes", _scopes);
		setNamespacedAttribute(request, "searchableFieldsDisabled", _searchableFieldsDisabled);
		setNamespacedAttribute(request, "submitButtonId", _submitButtonId);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "liferay-data-engine:data-layout-builder:";

	private static final String _END_PAGE =
		"/data_layout_builder/end.jsp";

	private static final String _START_PAGE =
		"/data_layout_builder/start.jsp";

	private java.util.List<java.util.Map<java.lang.String, java.lang.Object>> _additionalPanels = null;
	private java.lang.String _componentId = null;
	private java.lang.String _contentType = null;
	private java.lang.Long _dataDefinitionId = null;
	private java.lang.Long _dataLayoutId = null;
	private boolean _displayFieldName = false;
	private java.lang.String _fieldSetContentType = null;
	private java.lang.Long _groupId = null;
	private boolean _localizable = false;
	private jakarta.servlet.ServletContext _moduleServletContext = null;
	private java.lang.String _namespace = null;
	private java.util.Set _scopes = null;
	private boolean _searchableFieldsDisabled = false;
	private java.lang.String _submitButtonId = null;

}