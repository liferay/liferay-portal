/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.taglib.servlet.taglib;

import com.liferay.portal.workflow.taglib.internal.constants.WorkflowStatusConstants;
import com.liferay.portal.workflow.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Feliphe Marinho
 */
public class WorkflowStatusTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(WorkflowStatusConstants.ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public Object getBean() {
		return _bean;
	}

	public String getId() {
		return _id;
	}

	public Class<?> getModelClass() {
		return _modelClass;
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

	public boolean isShowInstanceTracker() {
		return _showInstanceTracker;
	}

	public boolean isShowStatusLabel() {
		return _showStatusLabel;
	}

	public void setBean(Object bean) {
		_bean = bean;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setModelClass(Class<?> modelClass) {
		_modelClass = modelClass;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setShowInstanceTracker(boolean showInstanceTracker) {
		_showInstanceTracker = showInstanceTracker;
	}

	public void setShowStatusLabel(boolean showStatusLabel) {
		_showStatusLabel = showStatusLabel;
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
		_id = null;
		_modelClass = null;
		_showInstanceTracker = false;
		_showStatusLabel = true;
		_status = null;
		_statusMessage = null;
		_version = null;
	}

	@Override
	protected String getPage() {
		return "/workflow_status/page.jsp";
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return true;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		Object bean = getBean();

		if (bean == null) {
			bean = pageContext.getAttribute("aui:model-context:bean");
		}

		setNamespacedAttribute(httpServletRequest, "bean", bean);
		setNamespacedAttribute(httpServletRequest, "id", _id);

		Class<?> modelClass = getModelClass();

		if (modelClass == null) {
			modelClass = (Class<?>)pageContext.getAttribute(
				"aui:model-context:model");
		}

		setNamespacedAttribute(httpServletRequest, "modelClass", modelClass);
		setNamespacedAttribute(
			httpServletRequest, "showInstanceTracker", _showInstanceTracker);
		setNamespacedAttribute(
			httpServletRequest, "showStatusLabel", _showStatusLabel);
		setNamespacedAttribute(httpServletRequest, "status", _status);
		setNamespacedAttribute(
			httpServletRequest, "statusMessage", _statusMessage);
		setNamespacedAttribute(httpServletRequest, "version", _version);
	}

	private Object _bean;
	private String _id;
	private Class<?> _modelClass;
	private boolean _showInstanceTracker;
	private boolean _showStatusLabel = true;
	private Integer _status;
	private String _statusMessage;
	private String _version;

}