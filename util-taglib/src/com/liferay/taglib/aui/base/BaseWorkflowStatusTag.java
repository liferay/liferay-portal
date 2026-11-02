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
public abstract class BaseWorkflowStatusTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public Object getBean() {
		return _bean;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public String getId() {
		return _id;
	}

	public String getMarkupView() {
		return _markupView;
	}

	public Class<?> getModel() {
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

	public Integer getStatus() {
		return _status;
	}

	public String getStatusMessage() {
		return _statusMessage;
	}

	public String getVersion() {
		return _version;
	}

	public void setBean(Object bean) {
		_bean = bean;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setMarkupView(String markupView) {
		_markupView = markupView;
	}

	public void setModel(Class<?> model) {
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

	public void setStatus(Integer status) {
		_status = status;
	}

	public void setStatusMessage(String statusMessage) {
		_statusMessage = statusMessage;
	}

	public void setVersion(String version) {
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
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(httpServletRequest, "bean", _bean);
		setNamespacedAttribute(httpServletRequest, "helpMessage", _helpMessage);
		setNamespacedAttribute(httpServletRequest, "id", _id);
		setNamespacedAttribute(httpServletRequest, "markupView", _markupView);
		setNamespacedAttribute(httpServletRequest, "model", _model);
		setNamespacedAttribute(
			httpServletRequest, "showHelpMessage", _showHelpMessage);
		setNamespacedAttribute(httpServletRequest, "showIcon", _showIcon);
		setNamespacedAttribute(httpServletRequest, "showLabel", _showLabel);
		setNamespacedAttribute(httpServletRequest, "status", _status);
		setNamespacedAttribute(
			httpServletRequest, "statusMessage", _statusMessage);
		setNamespacedAttribute(httpServletRequest, "version", _version);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:workflow-status:";

	private static final String _PAGE =
		"/html/taglib/aui/workflow_status/page.jsp";

	private Object _bean;
	private String _helpMessage;
	private String _id;
	private String _markupView;
	private Class<?> _model;
	private boolean _showHelpMessage = true;
	private boolean _showIcon = true;
	private boolean _showLabel = true;
	private Integer _status;
	private String _statusMessage;
	private String _version;

}