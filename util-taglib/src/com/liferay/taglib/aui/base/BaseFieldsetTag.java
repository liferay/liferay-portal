/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseFieldsetTag extends IncludeTag {

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

	public String getCssClass() {
		return _cssClass;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public String getId() {
		return _id;
	}

	public String getLabel() {
		return _label;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public String getMarkupView() {
		return _markupView;
	}

	public String getPanelHeaderLinkCssClass() {
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

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setMarkupView(String markupView) {
		_markupView = markupView;
	}

	public void setPanelHeaderLinkCssClass(String panelHeaderLinkCssClass) {
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
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(httpServletRequest, "collapsed", _collapsed);
		setNamespacedAttribute(httpServletRequest, "collapsible", _collapsible);
		setNamespacedAttribute(httpServletRequest, "column", _column);
		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(httpServletRequest, "helpMessage", _helpMessage);
		setNamespacedAttribute(httpServletRequest, "id", _id);
		setNamespacedAttribute(httpServletRequest, "label", _label);
		setNamespacedAttribute(
			httpServletRequest, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(httpServletRequest, "markupView", _markupView);
		setNamespacedAttribute(
			httpServletRequest, "panelHeaderLinkCssClass",
			_panelHeaderLinkCssClass);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:fieldset:";

	private static final String _END_PAGE = "/html/taglib/aui/fieldset/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/fieldset/start.jsp";

	private boolean _collapsed;
	private boolean _collapsible;
	private boolean _column;
	private String _cssClass;
	private String _helpMessage;
	private String _id;
	private String _label;
	private boolean _localizeLabel = true;
	private String _markupView;
	private String _panelHeaderLinkCssClass;

}