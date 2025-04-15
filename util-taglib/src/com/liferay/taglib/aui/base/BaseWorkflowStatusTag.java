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
public abstract class BaseWorkflowStatusTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.Object getBean() {
		return _bean;
	}

	public java.lang.String getHelpMessage() {
		return _helpMessage;
	}

	public java.lang.String getId() {
		return _id;
	}

	public java.lang.String getMarkupView() {
		return _markupView;
	}

	public java.lang.Class<?> getModel() {
		return _model;
	}

	public boolean getShowHelpMessage() {
		return _showHelpMessage;
	}

	public boolean getShowIcon() {
		return _showIcon;
	}

	public boolean getShowLabel() {
		return _showLabel;
	}

	public java.lang.Integer getStatus() {
		return _status;
	}

	public java.lang.String getStatusMessage() {
		return _statusMessage;
	}

	public java.lang.String getVersion() {
		return _version;
	}

	public void setBean(java.lang.Object bean) {
		_bean = bean;
	}

	public void setHelpMessage(java.lang.String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setMarkupView(java.lang.String markupView) {
		_markupView = markupView;
	}

	public void setModel(java.lang.Class<?> model) {
		_model = model;
	}

	public void setShowHelpMessage(boolean showHelpMessage) {
		_showHelpMessage = showHelpMessage;
	}

	public void setShowIcon(boolean showIcon) {
		_showIcon = showIcon;
	}

	public void setShowLabel(boolean showLabel) {
		_showLabel = showLabel;
	}

	public void setStatus(java.lang.Integer status) {
		_status = status;
	}

	public void setStatusMessage(java.lang.String statusMessage) {
		_statusMessage = statusMessage;
	}

	public void setVersion(java.lang.String version) {
		_version = version;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_bean = null;
		_helpMessage = null;
		_id = null;
		_markupView = null;
		_model = null;
		_showHelpMessage = true;
		_showIcon = true;
		_showLabel = true;
		_status = null;
		_statusMessage = null;
		_version = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "bean", _bean);
		setNamespacedAttribute(request, "helpMessage", _helpMessage);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "markupView", _markupView);
		setNamespacedAttribute(request, "model", _model);
		setNamespacedAttribute(request, "showHelpMessage", _showHelpMessage);
		setNamespacedAttribute(request, "showIcon", _showIcon);
		setNamespacedAttribute(request, "showLabel", _showLabel);
		setNamespacedAttribute(request, "status", _status);
		setNamespacedAttribute(request, "statusMessage", _statusMessage);
		setNamespacedAttribute(request, "version", _version);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:workflow-status:";

	private static final String _PAGE =
		"/html/taglib/aui/workflow_status/page.jsp";

	private java.lang.Object _bean = null;
	private java.lang.String _helpMessage = null;
	private java.lang.String _id = null;
	private java.lang.String _markupView = null;
	private java.lang.Class<?> _model = null;
	private boolean _showHelpMessage = true;
	private boolean _showIcon = true;
	private boolean _showLabel = true;
	private java.lang.Integer _status = null;
	private java.lang.String _statusMessage = null;
	private java.lang.String _version = null;

}