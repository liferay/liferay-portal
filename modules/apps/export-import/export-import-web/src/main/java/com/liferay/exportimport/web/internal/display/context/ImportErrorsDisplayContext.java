/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jorge González
 */
public class ImportErrorsDisplayContext {

	public ImportErrorsDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public String getAPIURL(String importProcessId) {
		return StringBundler.concat(
			"/o/export-import/v1.0/import-processes/", importProcessId,
			"/report-entries");
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				getImportErrorDetailsURL(), "view", "view",
				LanguageUtil.get(_httpServletRequest, "view"), "get", null,
				"link"));
	}

	public String getImportErrorDetailsURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/export_import/view_import_error_detail"
		).setBackURL(
			ParamUtil.getString(_httpServletRequest, "redirect")
		).setParameter(
			"errorId", "{id}"
		).buildString();
	}

	public String getReportEntryAPIURL(String errorId) {
		return StringBundler.concat(
			"/o/export-import/v1.0/report-entry/", errorId,
			"?nestedFields=errorStacktrace,scope.label");
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}