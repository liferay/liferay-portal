/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.taglib.servlet.taglib.base;

import com.liferay.dynamic.data.mapping.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Bruno Basto
 * @generated
 */
public abstract class BaseHTMLTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public boolean getCheckRequired() {
		return _checkRequired;
	}

	public long getClassNameId() {
		return _classNameId;
	}

	public long getClassPK() {
		return _classPK;
	}

	public com.liferay.dynamic.data.mapping.storage.DDMFormValues getDdmFormValues() {
		return _ddmFormValues;
	}

	public java.util.Locale getDefaultEditLocale() {
		return _defaultEditLocale;
	}

	public java.util.Locale getDefaultLocale() {
		return _defaultLocale;
	}

	public java.lang.String getDocumentLibrarySelectorURL() {
		return _documentLibrarySelectorURL;
	}

	public java.lang.String getFieldsNamespace() {
		return _fieldsNamespace;
	}

	public long getGroupId() {
		return _groupId;
	}

	public boolean getIgnoreRequestValue() {
		return _ignoreRequestValue;
	}

	public java.lang.String getImageSelectorURL() {
		return _imageSelectorURL;
	}

	public java.lang.String getLayoutSelectorURL() {
		return _layoutSelectorURL;
	}

	public boolean getLocalizable() {
		return _localizable;
	}

	public boolean getReadOnly() {
		return _readOnly;
	}

	public boolean getRepeatable() {
		return _repeatable;
	}

	public java.util.Locale getRequestedLocale() {
		return _requestedLocale;
	}

	public boolean getShowEmptyFieldLabel() {
		return _showEmptyFieldLabel;
	}

	public boolean getShowLanguageSelector() {
		return _showLanguageSelector;
	}

	public boolean getSynchronousFormSubmission() {
		return _synchronousFormSubmission;
	}

	public void setCheckRequired(boolean checkRequired) {
		_checkRequired = checkRequired;
	}

	public void setClassNameId(long classNameId) {
		_classNameId = classNameId;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setDdmFormValues(com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues) {
		_ddmFormValues = ddmFormValues;
	}

	public void setDefaultEditLocale(java.util.Locale defaultEditLocale) {
		_defaultEditLocale = defaultEditLocale;
	}

	public void setDefaultLocale(java.util.Locale defaultLocale) {
		_defaultLocale = defaultLocale;
	}

	public void setDocumentLibrarySelectorURL(java.lang.String documentLibrarySelectorURL) {
		_documentLibrarySelectorURL = documentLibrarySelectorURL;
	}

	public void setFieldsNamespace(java.lang.String fieldsNamespace) {
		_fieldsNamespace = fieldsNamespace;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	public void setImageSelectorURL(java.lang.String imageSelectorURL) {
		_imageSelectorURL = imageSelectorURL;
	}

	public void setLayoutSelectorURL(java.lang.String layoutSelectorURL) {
		_layoutSelectorURL = layoutSelectorURL;
	}

	public void setLocalizable(boolean localizable) {
		_localizable = localizable;
	}

	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	public void setRepeatable(boolean repeatable) {
		_repeatable = repeatable;
	}

	public void setRequestedLocale(java.util.Locale requestedLocale) {
		_requestedLocale = requestedLocale;
	}

	public void setShowEmptyFieldLabel(boolean showEmptyFieldLabel) {
		_showEmptyFieldLabel = showEmptyFieldLabel;
	}

	public void setShowLanguageSelector(boolean showLanguageSelector) {
		_showLanguageSelector = showLanguageSelector;
	}

	public void setSynchronousFormSubmission(boolean synchronousFormSubmission) {
		_synchronousFormSubmission = synchronousFormSubmission;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_checkRequired = true;
		_classNameId = 0;
		_classPK = 0;
		_ddmFormValues = null;
		_defaultEditLocale = null;
		_defaultLocale = null;
		_documentLibrarySelectorURL = null;
		_fieldsNamespace = null;
		_groupId = 0;
		_ignoreRequestValue = false;
		_imageSelectorURL = null;
		_layoutSelectorURL = null;
		_localizable = true;
		_readOnly = false;
		_repeatable = true;
		_requestedLocale = null;
		_showEmptyFieldLabel = true;
		_showLanguageSelector = true;
		_synchronousFormSubmission = true;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "checkRequired", _checkRequired);
		setNamespacedAttribute(request, "classNameId", _classNameId);
		setNamespacedAttribute(request, "classPK", _classPK);
		setNamespacedAttribute(request, "ddmFormValues", _ddmFormValues);
		setNamespacedAttribute(request, "defaultEditLocale", _defaultEditLocale);
		setNamespacedAttribute(request, "defaultLocale", _defaultLocale);
		setNamespacedAttribute(request, "documentLibrarySelectorURL", _documentLibrarySelectorURL);
		setNamespacedAttribute(request, "fieldsNamespace", _fieldsNamespace);
		setNamespacedAttribute(request, "groupId", _groupId);
		setNamespacedAttribute(request, "ignoreRequestValue", _ignoreRequestValue);
		setNamespacedAttribute(request, "imageSelectorURL", _imageSelectorURL);
		setNamespacedAttribute(request, "layoutSelectorURL", _layoutSelectorURL);
		setNamespacedAttribute(request, "localizable", _localizable);
		setNamespacedAttribute(request, "readOnly", _readOnly);
		setNamespacedAttribute(request, "repeatable", _repeatable);
		setNamespacedAttribute(request, "requestedLocale", _requestedLocale);
		setNamespacedAttribute(request, "showEmptyFieldLabel", _showEmptyFieldLabel);
		setNamespacedAttribute(request, "showLanguageSelector", _showLanguageSelector);
		setNamespacedAttribute(request, "synchronousFormSubmission", _synchronousFormSubmission);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "liferay-ddm:html:";

	private static final String _START_PAGE =
		"/html/start.jsp";

	private boolean _checkRequired = true;
	private long _classNameId = 0;
	private long _classPK = 0;
	private com.liferay.dynamic.data.mapping.storage.DDMFormValues _ddmFormValues = null;
	private java.util.Locale _defaultEditLocale = null;
	private java.util.Locale _defaultLocale = null;
	private java.lang.String _documentLibrarySelectorURL = null;
	private java.lang.String _fieldsNamespace = null;
	private long _groupId = 0;
	private boolean _ignoreRequestValue = false;
	private java.lang.String _imageSelectorURL = null;
	private java.lang.String _layoutSelectorURL = null;
	private boolean _localizable = true;
	private boolean _readOnly = false;
	private boolean _repeatable = true;
	private java.util.Locale _requestedLocale = null;
	private boolean _showEmptyFieldLabel = true;
	private boolean _showLanguageSelector = true;
	private boolean _synchronousFormSubmission = true;

}