/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Levente Hudák
 */
public class RemoteOptionsTag extends IncludeTag {

	public long getExportImportConfigurationId() {
		return _exportImportConfigurationId;
	}

	public boolean isDisableInputs() {
		return _disableInputs;
	}

	public boolean isPrivateLayout() {
		return _privateLayout;
	}

	public void setDisableInputs(boolean disableInputs) {
		_disableInputs = disableInputs;
	}

	public void setExportImportConfigurationId(
		long exportImportConfigurationId) {

		_exportImportConfigurationId = exportImportConfigurationId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPrivateLayout(boolean privateLayout) {
		_privateLayout = privateLayout;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_disableInputs = false;
		_exportImportConfigurationId = 0;
		_privateLayout = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:remote-options:disableInputs", _disableInputs);
		httpServletRequest.setAttribute(
			"liferay-staging:remote-options:exportImportConfigurationId",
			_exportImportConfigurationId);
		httpServletRequest.setAttribute(
			"liferay-staging:remote-options:privateLayout", _privateLayout);
	}

	private static final String _PAGE = "/remote_options/page.jsp";

	private boolean _disableInputs;
	private long _exportImportConfigurationId;
	private boolean _privateLayout;

}