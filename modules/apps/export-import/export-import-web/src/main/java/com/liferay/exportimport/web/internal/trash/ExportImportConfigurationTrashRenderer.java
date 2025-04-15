/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.trash;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.web.internal.constants.ExportImportWebKeys;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.trash.BaseJSPTrashRenderer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Levente Hudák
 */
public class ExportImportConfigurationTrashRenderer
	extends BaseJSPTrashRenderer {

	public static final String TYPE = "export_import_configuration";

	public ExportImportConfigurationTrashRenderer(
		ExportImportConfiguration exportImportConfiguration) {

		_exportImportConfiguration = exportImportConfiguration;
	}

	@Override
	public String getClassName() {
		return ExportImportConfiguration.class.getName();
	}

	@Override
	public long getClassPK() {
		return _exportImportConfiguration.getPrimaryKey();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		return "/view_configuration.jsp";
	}

	@Override
	public String getPortletId() {
		return PortletKeys.EXPORT_IMPORT;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _exportImportConfiguration.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		return _exportImportConfiguration.getName();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			ExportImportWebKeys.EXPORT_IMPORT_CONFIGURATION_ID,
			_exportImportConfiguration.getExportImportConfigurationId());

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private final ExportImportConfiguration _exportImportConfiguration;

}