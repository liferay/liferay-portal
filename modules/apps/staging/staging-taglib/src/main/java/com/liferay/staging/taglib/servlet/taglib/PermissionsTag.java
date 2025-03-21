/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Máté Thurzó
 */
public class PermissionsTag extends IncludeTag {

	public String getAction() {
		return _action;
	}

	public String getDescriptionCSSClass() {
		return _descriptionCSSClass;
	}

	public long getExportImportConfigurationId() {
		return _exportImportConfigurationId;
	}

	public String getLabelCSSClass() {
		return _labelCSSClass;
	}

	public boolean isDisableInputs() {
		return _disableInputs;
	}

	public boolean isGlobal() {
		return _global;
	}

	public void setAction(String action) {
		if (action == null) {
			_action = StringPool.BLANK;
		}
		else {
			_action = action;
		}
	}

	public void setDescriptionCSSClass(String descriptionCSSClass) {
		if (descriptionCSSClass == null) {
			_descriptionCSSClass = StringPool.BLANK;
		}
		else {
			_descriptionCSSClass = descriptionCSSClass;
		}
	}

	public void setDisableInputs(boolean disableInputs) {
		_disableInputs = disableInputs;
	}

	public void setExportImportConfigurationId(
		long exportImportConfigurationId) {

		_exportImportConfigurationId = exportImportConfigurationId;
	}

	public void setGlobal(boolean global) {
		_global = global;
	}

	public void setLabelCSSClass(String labelCSSClass) {
		if (labelCSSClass == null) {
			_labelCSSClass = StringPool.BLANK;
		}
		else {
			_labelCSSClass = labelCSSClass;
		}
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_action = StringPool.BLANK;
		_descriptionCSSClass = StringPool.BLANK;
		_disableInputs = false;
		_exportImportConfigurationId = 0;
		_global = false;
		_labelCSSClass = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:deletions:global", _global);
		httpServletRequest.setAttribute(
			"liferay-staging:permissions:action", _action);
		httpServletRequest.setAttribute(
			"liferay-staging:permissions:descriptionCSSClass",
			_descriptionCSSClass);
		httpServletRequest.setAttribute(
			"liferay-staging:permissions:disableInputs", _disableInputs);
		httpServletRequest.setAttribute(
			"liferay-staging:permissions:exportImportConfigurationId",
			_exportImportConfigurationId);
		httpServletRequest.setAttribute(
			"liferay-staging:permissions:labelCSSClass", _labelCSSClass);
	}

	private static final String _PAGE = "/permissions/page.jsp";

	private String _action = StringPool.BLANK;
	private String _descriptionCSSClass = StringPool.BLANK;
	private boolean _disableInputs;
	private long _exportImportConfigurationId;
	private boolean _global;
	private String _labelCSSClass = StringPool.BLANK;

}