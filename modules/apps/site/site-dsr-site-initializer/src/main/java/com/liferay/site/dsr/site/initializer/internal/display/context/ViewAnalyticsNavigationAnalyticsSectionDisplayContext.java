/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.display.context;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Gianmarco Brunialti Masera
 */
public class ViewAnalyticsNavigationAnalyticsSectionDisplayContext
	extends BaseAnalyticsSectionDisplayContext {

	public ViewAnalyticsNavigationAnalyticsSectionDisplayContext(
		AnalyticsSettingsManager analyticsSettingsManager,
		JSONObject configurationJSONObject,
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		super(
			analyticsSettingsManager, configurationJSONObject,
			fragmentEntryConfigurationParser, fragmentEntryLink,
			httpServletRequest, objectDefinition);
	}

	public String getActiveTab() {
		String currentURL = PortalUtil.getCurrentURL(httpServletRequest);

		if (currentURL.contains("view-overview")) {
			return "overview";
		}
		else if (currentURL.contains("view-timeline")) {
			return "timeline";
		}

		return StringPool.BLANK;
	}

	public JSONObject getFilterSettingsJSONObject() {
		return JSONUtil.put(
			"disabled",
			GetterUtil.getBoolean(getConfigurationValue("disableFilters"))
		).put(
			"interactable",
			GetterUtil.getBoolean(
				getConfigurationValue("userControlledFilters"))
		).put(
			"persisted",
			GetterUtil.getBoolean(getConfigurationValue("persistFilters"))
		);
	}

	@Override
	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>putAll(
			super.getProps()
		).put(
			"activeTab", getActiveTab()
		).put(
			"filterSettings", getFilterSettingsJSONObject()
		).put(
			"filtersJSONString", getAnalyticsStoreFilters()
		).put(
			"groupIds", _getGroupIdsJSONArray()
		).build();
	}

	private JSONArray _getGroupIdsJSONArray() {
		try {
			return JSONUtil.putAll(
				(Object[])DSRRoomUtil.getGroupIds(
					null, PermissionThreadLocal.getPermissionChecker()));
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return JSONFactoryUtil.createJSONArray();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAnalyticsNavigationAnalyticsSectionDisplayContext.class);

}