/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseTranslationManagerTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.util.Locale[] getAvailableLocales() {
		return _availableLocales;
	}

	public boolean getChangeableDefaultLanguage() {
		return _changeableDefaultLanguage;
	}

	public java.lang.String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public java.lang.String getEditingLanguageId() {
		return _editingLanguageId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public java.lang.String getId() {
		return _id;
	}

	public boolean getInitialize() {
		return _initialize;
	}

	public boolean getReadOnly() {
		return _readOnly;
	}

	public void setAvailableLocales(java.util.Locale[] availableLocales) {
		_availableLocales = availableLocales;
	}

	public void setChangeableDefaultLanguage(boolean changeableDefaultLanguage) {
		_changeableDefaultLanguage = changeableDefaultLanguage;
	}

	public void setDefaultLanguageId(java.lang.String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setEditingLanguageId(java.lang.String editingLanguageId) {
		_editingLanguageId = editingLanguageId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setInitialize(boolean initialize) {
		_initialize = initialize;
	}

	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_availableLocales = null;
		_changeableDefaultLanguage = true;
		_defaultLanguageId = null;
		_editingLanguageId = null;
		_groupId = 0;
		_id = null;
		_initialize = true;
		_readOnly = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "availableLocales", _availableLocales);
		setNamespacedAttribute(request, "changeableDefaultLanguage", _changeableDefaultLanguage);
		setNamespacedAttribute(request, "defaultLanguageId", _defaultLanguageId);
		setNamespacedAttribute(request, "editingLanguageId", _editingLanguageId);
		setNamespacedAttribute(request, "groupId", _groupId);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "initialize", _initialize);
		setNamespacedAttribute(request, "readOnly", _readOnly);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:translation-manager:";

	private static final String _PAGE =
		"/html/taglib/aui/translation_manager/page.jsp";

	private java.util.Locale[] _availableLocales = null;
	private boolean _changeableDefaultLanguage = true;
	private java.lang.String _defaultLanguageId = null;
	private java.lang.String _editingLanguageId = null;
	private long _groupId = 0;
	private java.lang.String _id = null;
	private boolean _initialize = true;
	private boolean _readOnly = false;

}