/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.dsr.site.initializer.internal.display.context.ViewAnalyticsNavigationAnalyticsSectionDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(service = FragmentRenderer.class)
public class ViewAnalyticsNavigationJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewAnalyticsNavigationAnalyticsSectionDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "digital-sales-room-analytics";
	}

	@Override
	public JSONObject getConfigurationJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(),
					"view_analytics_navigation/dependencies" +
						"/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject,
				ResourceBundleUtil.getBundle("content.Language", getClass()));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	@Override
	public String getKey() {
		return "dsr-view-analytics-navigation";
	}

	@Override
	public String getLabelKey() {
		return "navigation";
	}

	@Override
	protected ViewAnalyticsNavigationAnalyticsSectionDisplayContext
		getDisplayContext(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return new ViewAnalyticsNavigationAnalyticsSectionDisplayContext(
			analyticsSettingsManager,
			getConfigurationJSONObject(fragmentRendererContext),
			_fragmentEntryConfigurationParser,
			fragmentRendererContext.getFragmentEntryLink(), httpServletRequest,
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", themeDisplay.getCompanyId()));
	}

	@Override
	protected String getJSPPath() {
		return "/view_analytics_navigation.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAnalyticsNavigationJSPSectionFragmentRenderer.class);

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}