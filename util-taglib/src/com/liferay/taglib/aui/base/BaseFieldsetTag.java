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
public abstract class BaseFieldsetTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public boolean getCollapsed() {
		return _collapsed;
	}

	public boolean getCollapsible() {
		return _collapsible;
	}

	public boolean getColumn() {
		return _column;
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public java.lang.String getHelpMessage() {
		return _helpMessage;
	}

	public java.lang.String getId() {
		return _id;
	}

	public java.lang.String getLabel() {
		return _label;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public java.lang.String getMarkupView() {
		return _markupView;
	}

	public java.lang.String getPanelHeaderLinkCssClass() {
		return _panelHeaderLinkCssClass;
	}

	public void setCollapsed(boolean collapsed) {
		_collapsed = collapsed;
	}

	public void setCollapsible(boolean collapsible) {
		_collapsible = collapsible;
	}

	public void setColumn(boolean column) {
		_column = column;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setHelpMessage(java.lang.String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setLabel(java.lang.String label) {
		_label = label;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setMarkupView(java.lang.String markupView) {
		_markupView = markupView;
	}

	public void setPanelHeaderLinkCssClass(java.lang.String panelHeaderLinkCssClass) {
		_panelHeaderLinkCssClass = panelHeaderLinkCssClass;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_collapsed = false;
		_collapsible = false;
		_column = false;
		_cssClass = null;
		_helpMessage = null;
		_id = null;
		_label = null;
		_localizeLabel = true;
		_markupView = null;
		_panelHeaderLinkCssClass = null;
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
		setNamespacedAttribute(request, "collapsed", _collapsed);
		setNamespacedAttribute(request, "collapsible", _collapsible);
		setNamespacedAttribute(request, "column", _column);
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "helpMessage", _helpMessage);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "label", _label);
		setNamespacedAttribute(request, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(request, "markupView", _markupView);
		setNamespacedAttribute(request, "panelHeaderLinkCssClass", _panelHeaderLinkCssClass);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:fieldset:";

	private static final String _END_PAGE =
		"/html/taglib/aui/fieldset/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/fieldset/start.jsp";

	private boolean _collapsed = false;
	private boolean _collapsible = false;
	private boolean _column = false;
	private java.lang.String _cssClass = null;
	private java.lang.String _helpMessage = null;
	private java.lang.String _id = null;
	private java.lang.String _label = null;
	private boolean _localizeLabel = true;
	private java.lang.String _markupView = null;
	private java.lang.String _panelHeaderLinkCssClass = null;

}