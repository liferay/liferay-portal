/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Diego Hu
 */
public class ResourceSelectorTag extends IncludeTag {

	public String getInputLabel() {
		return _inputLabel;
	}

	public String getInputName() {
		return _inputName;
	}

	public String getModalTitle() {
		return _modalTitle;
	}

	public String getResourceName() {
		return _resourceName;
	}

	public String getResourceNameKey() {
		return _resourceNameKey;
	}

	public String getResourceValue() {
		return _resourceValue;
	}

	public String getResourceValueKey() {
		return _resourceValueKey;
	}

	public String getSelectEventName() {
		return _selectEventName;
	}

	public String getSelectResourceURL() {
		return _selectResourceURL;
	}

	public String getWarningMessage() {
		return _warningMessage;
	}

	public boolean isShowRemoveButton() {
		return _showRemoveButton;
	}

	public void setInputLabel(String inputLabel) {
		_inputLabel = inputLabel;
	}

	public void setInputName(String inputName) {
		_inputName = inputName;
	}

	public void setModalTitle(String modalTitle) {
		_modalTitle = modalTitle;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setResourceName(String resourceName) {
		_resourceName = resourceName;
	}

	public void setResourceNameKey(String resourceNameKey) {
		_resourceNameKey = resourceNameKey;
	}

	public void setResourceValue(String resourceValue) {
		_resourceValue = resourceValue;
	}

	public void setResourceValueKey(String resourceValueKey) {
		_resourceValueKey = resourceValueKey;
	}

	public void setSelectEventName(String selectEventName) {
		_selectEventName = selectEventName;
	}

	public void setSelectResourceURL(String selectResourceURL) {
		_selectResourceURL = selectResourceURL;
	}

	public void setShowRemoveButton(boolean showRemoveButton) {
		_showRemoveButton = showRemoveButton;
	}

	public void setWarningMessage(String warningMessage) {
		_warningMessage = warningMessage;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_inputLabel = null;
		_inputName = null;
		_modalTitle = null;
		_resourceName = null;
		_resourceNameKey = null;
		_resourceValue = null;
		_resourceValueKey = null;
		_selectEventName = null;
		_selectResourceURL = null;
		_showRemoveButton = false;
		_warningMessage = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:inputLabel", _inputLabel);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:inputName", _inputName);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:modalTitle", _modalTitle);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:resourceName", _resourceName);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:resourceNameKey",
			_resourceNameKey);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:resourceValue", _resourceValue);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:resourceValueKey",
			_resourceValueKey);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:selectEventName",
			_selectEventName);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:selectResourceURL",
			_selectResourceURL);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:showRemoveButton",
			_showRemoveButton);
		httpServletRequest.setAttribute(
			"liferay-frontend:resource-selector:warningMessage",
			_warningMessage);
	}

	private static final String _PAGE = "/resource_selector/page.jsp";

	private String _inputLabel;
	private String _inputName;
	private String _modalTitle;
	private String _resourceName;
	private String _resourceNameKey;
	private String _resourceValue;
	private String _resourceValueKey;
	private String _selectEventName;
	private String _selectResourceURL;
	private boolean _showRemoveButton;
	private String _warningMessage;

}