/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib.base;

import com.liferay.dynamic.data.mapping.form.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Pedro Queiroz
 * @author Rafael Praxedes
 * @generated
 */
public abstract class BaseDDMFormBuilderTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.Long getDdmStructureId() {
		return _ddmStructureId;
	}

	public java.lang.Long getDdmStructureVersionId() {
		return _ddmStructureVersionId;
	}

	public java.lang.String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public java.lang.String getEditingLanguageId() {
		return _editingLanguageId;
	}

	public long getFieldSetClassNameId() {
		return _fieldSetClassNameId;
	}

	public java.lang.String getRefererPortletNamespace() {
		return _refererPortletNamespace;
	}

	public boolean getShowPagination() {
		return _showPagination;
	}

	public void setDdmStructureId(java.lang.Long ddmStructureId) {
		_ddmStructureId = ddmStructureId;
	}

	public void setDdmStructureVersionId(java.lang.Long ddmStructureVersionId) {
		_ddmStructureVersionId = ddmStructureVersionId;
	}

	public void setDefaultLanguageId(java.lang.String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setEditingLanguageId(java.lang.String editingLanguageId) {
		_editingLanguageId = editingLanguageId;
	}

	public void setFieldSetClassNameId(long fieldSetClassNameId) {
		_fieldSetClassNameId = fieldSetClassNameId;
	}

	public void setRefererPortletNamespace(java.lang.String refererPortletNamespace) {
		_refererPortletNamespace = refererPortletNamespace;
	}

	public void setShowPagination(boolean showPagination) {
		_showPagination = showPagination;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_ddmStructureId = null;
		_ddmStructureVersionId = null;
		_defaultLanguageId = null;
		_editingLanguageId = null;
		_fieldSetClassNameId = 0;
		_refererPortletNamespace = null;
		_showPagination = true;
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
		setNamespacedAttribute(request, "ddmStructureId", _ddmStructureId);
		setNamespacedAttribute(request, "ddmStructureVersionId", _ddmStructureVersionId);
		setNamespacedAttribute(request, "defaultLanguageId", _defaultLanguageId);
		setNamespacedAttribute(request, "editingLanguageId", _editingLanguageId);
		setNamespacedAttribute(request, "fieldSetClassNameId", _fieldSetClassNameId);
		setNamespacedAttribute(request, "refererPortletNamespace", _refererPortletNamespace);
		setNamespacedAttribute(request, "showPagination", _showPagination);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "liferay-form:ddm-form-builder:";

	private static final String _END_PAGE =
		"/ddm_form_builder/end.jsp";

	private static final String _START_PAGE =
		"/ddm_form_builder/start.jsp";

	private java.lang.Long _ddmStructureId = null;
	private java.lang.Long _ddmStructureVersionId = null;
	private java.lang.String _defaultLanguageId = null;
	private java.lang.String _editingLanguageId = null;
	private long _fieldSetClassNameId = 0;
	private java.lang.String _refererPortletNamespace = null;
	private boolean _showPagination = true;

}