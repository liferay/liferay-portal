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
public class DeletionsTag extends IncludeTag {

	public String getCmd() {
		return _cmd;
	}

	public long getExportImportConfigurationId() {
		return _exportImportConfigurationId;
	}

	public boolean isDisableInputs() {
		return _disableInputs;
	}

	public void setCmd(String cmd) {
		_cmd = cmd;
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

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cmd = null;
		_disableInputs = false;
		_exportImportConfigurationId = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute("liferay-staging:deletions:cmd", _cmd);
		httpServletRequest.setAttribute(
			"liferay-staging:deletions:disableInputs", _disableInputs);
		httpServletRequest.setAttribute(
			"liferay-staging:deletions:exportImportConfigurationId",
			_exportImportConfigurationId);
	}

	private static final String _PAGE = "/deletions/page.jsp";

	private String _cmd;
	private boolean _disableInputs;
	private long _exportImportConfigurationId;

}