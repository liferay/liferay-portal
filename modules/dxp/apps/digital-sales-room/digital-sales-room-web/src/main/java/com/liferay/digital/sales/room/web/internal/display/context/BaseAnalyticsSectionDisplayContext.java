/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.display.context;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.digital.sales.room.web.internal.constants.DSRWebKeys;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * @author Gianmarco Brunialti Masera
 */
public abstract class BaseAnalyticsSectionDisplayContext {

	public BaseAnalyticsSectionDisplayContext(
		AnalyticsSettingsManager analyticsSettingsManager,
		JSONObject configurationJSONObject,
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		this.analyticsSettingsManager = analyticsSettingsManager;
		this.configurationJSONObject = configurationJSONObject;
		this.fragmentEntryConfigurationParser =
			fragmentEntryConfigurationParser;
		this.fragmentEntryLink = fragmentEntryLink;
		this.httpServletRequest = httpServletRequest;
		this.objectDefinition = objectDefinition;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAnalyticsStoreFilters() {
		HttpSession httpSession = httpServletRequest.getSession();

		String filters = (String)httpSession.getAttribute(
			DSRWebKeys.DSR_ANALYTICS_STORE_FILTERS);

		if (Validator.isNotNull(filters)) {
			return filters;
		}

		return StringPool.BLANK;
	}

	public Object getConfigurationValue(String name) {
		if ((configurationJSONObject == null) ||
			(fragmentEntryConfigurationParser == null) ||
			(fragmentEntryLink == null)) {

			return null;
		}

		return fragmentEntryConfigurationParser.getFieldValue(
			configurationJSONObject,
			fragmentEntryLink.getEditableValuesJSONObject(),
			themeDisplay.getLocale(), name);
	}

	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>put(
			"isAnalyticsEnabled", _isAnalyticsEnabled()
		).build();
	}

	protected final AnalyticsSettingsManager analyticsSettingsManager;
	protected final JSONObject configurationJSONObject;
	protected final FragmentEntryConfigurationParser
		fragmentEntryConfigurationParser;
	protected final FragmentEntryLink fragmentEntryLink;
	protected final HttpServletRequest httpServletRequest;
	protected final ObjectDefinition objectDefinition;
	protected final ThemeDisplay themeDisplay;

	private boolean _isAnalyticsEnabled() {
		try {
			return analyticsSettingsManager.isAnalyticsEnabled(
				themeDisplay.getCompanyId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAnalyticsSectionDisplayContext.class);

}