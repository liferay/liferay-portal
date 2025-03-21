/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.web.internal.servlet.taglib;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.web.internal.constants.AnalyticsWebKeys;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = DynamicInclude.class)
public class AnalyticsTopHeadJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		AnalyticsConfiguration analyticsConfiguration = null;

		try {
			analyticsConfiguration =
				_analyticsSettingsManager.getAnalyticsConfiguration(
					themeDisplay.getCompanyId());
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		if (!_isAnalyticsTrackingEnabled(
				analyticsConfiguration, httpServletRequest, themeDisplay)) {

			return;
		}

		httpServletRequest.setAttribute(
			AnalyticsWebKeys.ANALYTICS_CLIENT_CHANNEL_ID,
			_getLiferayAnalyticsChannelId(httpServletRequest, themeDisplay));
		httpServletRequest.setAttribute(
			AnalyticsWebKeys.ANALYTICS_CLIENT_CONFIG,
			_serialize(_getAnalyticsCloudClientConfig(analyticsConfiguration)));
		httpServletRequest.setAttribute(
			AnalyticsWebKeys.ANALYTICS_CLIENT_GROUP_IDS,
			_serialize(analyticsConfiguration.syncedGroupIds()));
		httpServletRequest.setAttribute(
			AnalyticsWebKeys.ANALYTICS_COOKIES_EXPLICIT_CONSENT_MODE,
			_isCookiesExplicitConsentMode(themeDisplay));

		Layout layout = themeDisplay.getLayout();

		httpServletRequest.setAttribute(
			AnalyticsWebKeys.ANALYTICS_CLIENT_READABLE_CONTENT,
			Boolean.toString(layout.isTypeAssetDisplay()));

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/top_head.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private Map<String, String> _getAnalyticsCloudClientConfig(
		AnalyticsConfiguration analyticsConfiguration) {

		if (GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.ANALYTICS_CLOUD_MOCK_ENABLED))) {

			return HashMapBuilder.put(
				"endpointUrl", "/o/mock/osb-asah-publisher"
			).build();
		}

		return HashMapBuilder.put(
			"dataSourceId",
			analyticsConfiguration.liferayAnalyticsDataSourceId()
		).put(
			"endpointUrl", analyticsConfiguration.liferayAnalyticsEndpointURL()
		).put(
			"projectId", analyticsConfiguration.liferayAnalyticsProjectId()
		).build();
	}

	private String _getLiferayAnalyticsChannelId(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		if (Objects.equals(group.getGroupKey(), "Forms")) {
			Group refererGroup = _groupLocalService.fetchGroup(
				GetterUtil.getLong(
					httpServletRequest.getAttribute("refererGroupId")));

			if (refererGroup != null) {
				return refererGroup.getTypeSettingsProperty(
					"analyticsChannelId");
			}
		}

		return group.getTypeSettingsProperty("analyticsChannelId");
	}

	private boolean _isAnalyticsTrackingEnabled(
		AnalyticsConfiguration analyticsConfiguration,
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		if (GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.ANALYTICS_CLOUD_MOCK_ENABLED))) {

			return true;
		}

		Layout layout = themeDisplay.getLayout();

		if ((analyticsConfiguration == null) || (layout == null) ||
			layout.isTypeControlPanel() ||
			Validator.isNull(
				analyticsConfiguration.liferayAnalyticsDataSourceId()) ||
			Validator.isNull(
				analyticsConfiguration.liferayAnalyticsEndpointURL()) ||
			Objects.equals(
				httpServletRequest.getRequestURI(), "/c/portal/api/jsonws")) {

			return false;
		}

		String[] syncedGroupIds = analyticsConfiguration.syncedGroupIds();

		if (_isSharedFormEnabled(
				syncedGroupIds, layout.getGroup(), httpServletRequest) ||
			analyticsConfiguration.liferayAnalyticsEnableAllGroupIds() ||
			ArrayUtil.contains(
				syncedGroupIds, String.valueOf(layout.getGroupId()))) {

			return true;
		}

		return false;
	}

	private boolean _isCookiesExplicitConsentMode(ThemeDisplay themeDisplay) {
		try {
			CookiesPreferenceHandlingConfiguration
				cookiesPreferenceHandlingConfiguration =
					_cookiesConfigurationProvider.
						getCookiesPreferenceHandlingConfiguration(themeDisplay);

			if (cookiesPreferenceHandlingConfiguration.enabled() &&
				cookiesPreferenceHandlingConfiguration.explicitConsentMode()) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private boolean _isSharedFormEnabled(
		String[] liferayAnalyticsGroupIds, Group group,
		HttpServletRequest httpServletRequest) {

		if (Objects.equals(group.getGroupKey(), "Forms")) {
			return ArrayUtil.contains(
				liferayAnalyticsGroupIds,
				String.valueOf(
					httpServletRequest.getAttribute("refererGroupId")));
		}

		return false;
	}

	private String _serialize(Map<String, String> map) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}

		return jsonObject.toString();
	}

	private String _serialize(Object[] array) {
		JSONArray jsonArray = _jsonFactory.createJSONArray(array);

		return jsonArray.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsTopHeadJSPDynamicInclude.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.analytics.web)")
	private ServletContext _servletContext;

}