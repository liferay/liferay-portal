/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.exportimport.util.ScopeUtil;
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
public class ReportEntriesDisplayContext {

	public ReportEntriesDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				_getReportEntryDetailsURL(), "view", "view",
				LanguageUtil.get(_httpServletRequest, "view"), "get", null,
				"link"));
	}

	public String getImportProcessReportEntriesAPIURL(String importProcessId) {
		return ScopeUtil.getAPIURL(
			StringBundler.concat(
				"/import-processes/", importProcessId, "/report-entries"));
	}

	public String getPublishProcessReportEntriesAPIURL(
		String publishProcessId) {

		return ScopeUtil.getAPIURL(
			StringBundler.concat(
				"/publish-processes/", publishProcessId, "/report-entries"));
	}

	public String getReportEntryAPIURL(String reportEntryId) {
		return ScopeUtil.getAPIURL(
			StringBundler.concat(
				"/report-entry/", reportEntryId,
				"?nestedFields=errorStacktrace,scope.label"));
	}

	private String _getReportEntryDetailsURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/export_import/view_import_report_entry_detail"
		).setBackURL(
			ParamUtil.getString(_httpServletRequest, "backURL")
		).setParameter(
			"reportEntryId", "{id}"
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}