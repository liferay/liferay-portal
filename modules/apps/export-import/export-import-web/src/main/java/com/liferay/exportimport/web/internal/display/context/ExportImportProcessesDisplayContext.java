/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.exportimport.web.internal.constants.ExportImportFDSNames;
import com.liferay.exportimport.web.internal.util.ScopeUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Raposo
 */
public class ExportImportProcessesDisplayContext {

	public ExportImportProcessesDisplayContext(
		Group group, long groupId, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, boolean privateLayout) {

		_group = group;
		_groupId = groupId;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_privateLayout = privateLayout;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public CreationMenu getExportCreationMenu() {
		return _getCreationMenu(
			Constants.EXPORT, "mvcRenderCommandName",
			"/export_import/view_new_export");
	}

	public List<FDSActionDropdownItem> getExportFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				ScopeUtil.getAPIURL("/export-processes/{id}/relaunch"),
				"reload", "relaunch",
				LanguageUtil.get(_httpServletRequest, "relaunch"), "post", null,
				"async"),
			new FDSActionDropdownItem(
				ScopeUtil.getAPIURL("/export-processes/{id}/content?p_auth=") +
					AuthTokenUtil.getToken(_httpServletRequest),
				"download", "download",
				LanguageUtil.get(_httpServletRequest, "download"), "get", null,
				"link",
				HashMapBuilder.<String, Object>put(
					"status.code", BackgroundTaskConstants.STATUS_SUCCESSFUL
				).build()),
			_getDeleteFDSActionDropdownItem("/export-processes/{id}"),
			_getClearFDSActionDropdownItem("/export-processes/{id}"));
	}

	public String getExportFDSName() {
		return _getFDSName(
			ExportImportFDSNames.COMPANY_EXPORT_PROCESSES,
			ExportImportFDSNames.EXPORT_PROCESSES);
	}

	public String getExportProcessesAPIURL() {
		if (_exportProcessesAPIURL != null) {
			return _exportProcessesAPIURL;
		}

		_exportProcessesAPIURL = _getAPIURL("/export-processes");

		return _exportProcessesAPIURL;
	}

	public String getExportTitle() {
		return _getTitle("export");
	}

	public CreationMenu getImportCreationMenu() {
		return _getCreationMenu(
			Constants.IMPORT, "mvcPath", "/revamp/import/new_import.jsp");
	}

	public List<FDSActionDropdownItem> getImportFDSActionDropdownItems() {
		Map<String, Object> visibilityFilters =
			HashMapBuilder.<String, Object>put(
				"status.code",
				Arrays.asList(
					BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
					BackgroundTaskConstants.STATUS_FAILED)
			).build();

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/export_import/view_import_report_entries"
				).setBackURL(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"backgroundTaskId", "{id}"
				).buildString(),
				"list-ul", "view-report-entries",
				LanguageUtil.get(_httpServletRequest, "view-report-entries"),
				"get", null, "link", visibilityFilters),
			new FDSActionDropdownItem(
				null, "download", "exportReportEntries",
				LanguageUtil.get(_httpServletRequest, "export-report-entries"),
				"get", null, null, visibilityFilters),
			_getDeleteFDSActionDropdownItem("/import-processes/{id}"),
			_getClearFDSActionDropdownItem("/import-processes/{id}"));
	}

	public String getImportFDSName() {
		return _getFDSName(
			ExportImportFDSNames.COMPANY_IMPORT_PROCESSES,
			ExportImportFDSNames.IMPORT_PROCESSES);
	}

	public String getImportProcessesAPIURL() {
		if (_importProcessesAPIURL != null) {
			return _importProcessesAPIURL;
		}

		_importProcessesAPIURL = _getAPIURL("/import-processes");

		return _importProcessesAPIURL;
	}

	public String getImportTitle() {
		return _getTitle("import");
	}

	private String _getAPIURL(String endpoint) {
		String portletId = _getPortletId();

		if (Validator.isBlank(portletId)) {
			return ScopeUtil.getAPIURL(_group, endpoint);
		}

		return StringBundler.concat(
			ScopeUtil.getAPIURL(_group, endpoint), "?portletId=",
			URLEncoder.encode(portletId, StandardCharsets.UTF_8));
	}

	private FDSActionDropdownItem _getClearFDSActionDropdownItem(
		String endpoint) {

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			ScopeUtil.getAPIURL(endpoint), "trash", "clear",
			LanguageUtil.get(_httpServletRequest, "clear"), "delete", null,
			"async",
			HashMapBuilder.<String, Object>put(
				"status.code",
				Arrays.asList(
					BackgroundTaskConstants.STATUS_CANCELLED,
					BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
					BackgroundTaskConstants.STATUS_FAILED,
					BackgroundTaskConstants.STATUS_SUCCESSFUL)
			).build());

		fdsActionDropdownItem.setConfirmationMessage(
			LanguageUtil.get(
				_httpServletRequest, "are-you-sure-you-want-to-delete-this"));

		return fdsActionDropdownItem;
	}

	private CreationMenu _getCreationMenu(
		String cmd, String mvcCommandName, String mvcCommandValue) {

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_liferayPortletResponse.createRenderURL(), mvcCommandName,
					mvcCommandValue, Constants.CMD, cmd, "groupId",
					String.valueOf(_groupId), "liveGroupId",
					String.valueOf(_groupId), "privateLayout",
					String.valueOf(_privateLayout), "plid",
					String.valueOf(_themeDisplay.getPlid()), "portletId",
					_getPortletId(), "backURL", _themeDisplay.getURLCurrent());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new"));
			}
		).build();
	}

	private FDSActionDropdownItem _getDeleteFDSActionDropdownItem(
		String endpoint) {

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			ScopeUtil.getAPIURL(endpoint), "trash", "delete",
			LanguageUtil.get(_httpServletRequest, "delete"), "delete", null,
			"async",
			HashMapBuilder.<String, Object>put(
				"status.code",
				Arrays.asList(
					BackgroundTaskConstants.STATUS_IN_PROGRESS,
					BackgroundTaskConstants.STATUS_NEW,
					BackgroundTaskConstants.STATUS_QUEUED)
			).build());

		fdsActionDropdownItem.setConfirmationMessage(
			LanguageUtil.get(
				_httpServletRequest, "are-you-sure-you-want-to-delete-this"));

		return fdsActionDropdownItem;
	}

	private String _getFDSName(String companyFDSName, String fdsName) {
		if (ScopeUtil.isInstanceScoped(_group)) {
			return companyFDSName;
		}

		return fdsName;
	}

	private String _getPortletId() {
		return ParamUtil.getString(
			_httpServletRequest, "portletId",
			ParamUtil.getString(_httpServletRequest, "portletResource"));
	}

	private String _getTitle(String key) {
		String portletId = _getPortletId();

		if (Validator.isBlank(portletId)) {
			return null;
		}

		return StringBundler.concat(
			LanguageUtil.get(_httpServletRequest, key), " ",
			PortalUtil.getPortletTitle(portletId, _themeDisplay.getLocale()));
	}

	private String _exportProcessesAPIURL;
	private final Group _group;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private String _importProcessesAPIURL;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final boolean _privateLayout;
	private final ThemeDisplay _themeDisplay;

}