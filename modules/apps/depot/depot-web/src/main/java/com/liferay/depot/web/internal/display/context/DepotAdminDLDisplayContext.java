/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class DepotAdminDLDisplayContext {

	public DepotAdminDLDisplayContext(
		DepotEntry depotEntry,
		DLSizeLimitConfigurationProvider dlSizeLimitConfigurationProvider,
		HttpServletRequest httpServletRequest) {

		_depotEntry = depotEntry;
		_dlSizeLimitConfigurationProvider = dlSizeLimitConfigurationProvider;
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public DepotEntry getDepotEntry() {
		return _depotEntry;
	}

	public long getFileMaxSize() {
		return _dlSizeLimitConfigurationProvider.getGroupFileMaxSize(
			_depotEntry.getGroupId());
	}

	public Map<String, Object> getFileSizePerMimeTypeData() {
		List<Map<String, Object>> sizeList = new ArrayList<>();

		Map<String, Long> groupMimeTypeSizeLimit =
			_dlSizeLimitConfigurationProvider.getGroupMimeTypeSizeLimit(
				_depotEntry.getGroupId());

		groupMimeTypeSizeLimit.forEach(
			(mimeType, size) -> sizeList.add(
				HashMapBuilder.<String, Object>put(
					"mimeType", mimeType
				).put(
					"size", size
				).build()));

		return HashMapBuilder.<String, Object>put(
			"sizeList", sizeList
		).build();
	}

	public String getGroupDLFriendlyURL() throws PortalException {
		Group group = _depotEntry.getGroup();

		return _themeDisplay.getPortalURL() + "/documents" +
			group.getFriendlyURL();
	}

	public String getGroupName() throws PortalException {
		Group group = _depotEntry.getGroup();

		return HtmlUtil.escape(
			group.getDescriptiveName(_themeDisplay.getLocale()));
	}

	public boolean isDirectoryIndexingEnabled() throws PortalException {
		Group group = _depotEntry.getGroup();

		UnicodeProperties unicodeProperties = group.getTypeSettingsProperties();

		return PropertiesParamUtil.getBoolean(
			unicodeProperties, _httpServletRequest, "directoryIndexingEnabled");
	}

	private final DepotEntry _depotEntry;
	private final DLSizeLimitConfigurationProvider
		_dlSizeLimitConfigurationProvider;
	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}