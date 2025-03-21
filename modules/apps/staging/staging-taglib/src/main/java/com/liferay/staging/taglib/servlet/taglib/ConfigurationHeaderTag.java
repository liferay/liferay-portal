/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Levente Hudák
 */
public class ConfigurationHeaderTag extends IncludeTag {

	public ExportImportConfiguration getExportImportConfiguration() {
		return _exportImportConfiguration;
	}

	public String getLabel() {
		return _label;
	}

	public void setExportImportConfiguration(
		ExportImportConfiguration exportImportConfiguration) {

		_exportImportConfiguration = exportImportConfiguration;
	}

	public void setLabel(String label) {
		_label = label;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_exportImportConfiguration = null;
		_label = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:configuration-header:exportImportConfiguration",
			_exportImportConfiguration);
		httpServletRequest.setAttribute(
			"liferay-staging:configuration-header:label", _label);
	}

	private static final String _PAGE = "/configuration_header/page.jsp";

	private ExportImportConfiguration _exportImportConfiguration;
	private String _label;

}