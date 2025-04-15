/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.portlet.action;

import com.liferay.analytics.reports.web.internal.constants.AnalyticsReportsPortletKeys;
import com.liferay.analytics.reports.web.internal.data.provider.AnalyticsReportsDataProvider;
import com.liferay.analytics.reports.web.internal.model.ReferringSocialMedia;
import com.liferay.analytics.reports.web.internal.model.TimeRange;
import com.liferay.analytics.reports.web.internal.model.TimeSpan;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AnalyticsReportsPortletKeys.ANALYTICS_REPORTS,
		"mvc.command.name=/analytics_reports/get_social_traffic_sources"
	},
	service = MVCResourceCommand.class
)
public class GetSocialTrafficSourcesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			themeDisplay.getLocale(), getClass());

		try {
			AnalyticsReportsDataProvider analyticsReportsDataProvider =
				new AnalyticsReportsDataProvider(
					_analyticsSettingsManager, _http);

			String canonicalURL = ParamUtil.getString(
				resourceRequest, "canonicalURL");

			String timeSpanKey = ParamUtil.getString(
				resourceRequest, "timeSpanKey", TimeSpan.defaultTimeSpanKey());

			TimeSpan timeSpan = TimeSpan.of(timeSpanKey);

			int timeSpanOffset = ParamUtil.getInteger(
				resourceRequest, "timeSpanOffset");

			JSONObject jsonObject = JSONUtil.put(
				"referringSocialMedia",
				_getReferringSocialMediaJSONArray(
					_getReferringSocialMediaList(
						analyticsReportsDataProvider, canonicalURL,
						themeDisplay.getCompanyId(),
						timeSpan.toTimeRange(timeSpanOffset)),
					resourceBundle));

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					ResourceBundleUtil.getString(
						resourceBundle, "an-unexpected-error-occurred")));
		}
	}

	private JSONArray _getReferringSocialMediaJSONArray(
		List<ReferringSocialMedia> referringSocialMediaList,
		ResourceBundle resourceBundle) {

		if (ListUtil.isEmpty(referringSocialMediaList)) {
			return _jsonFactory.createJSONArray();
		}

		Comparator<ReferringSocialMedia> comparator = Comparator.comparingInt(
			ReferringSocialMedia::getTrafficAmount);

		referringSocialMediaList = ListUtil.filter(
			referringSocialMediaList,
			referringSocialMedia ->
				referringSocialMedia.getTrafficAmount() > 0);

		referringSocialMediaList.sort(comparator.reversed());

		return JSONUtil.toJSONArray(
			referringSocialMediaList,
			referringSocialMedia -> referringSocialMedia.toJSONObject(
				resourceBundle),
			_log);
	}

	private List<ReferringSocialMedia> _getReferringSocialMediaList(
			AnalyticsReportsDataProvider analyticsReportsDataProvider,
			String canonicalURL, long companyId, TimeRange timeRange)
		throws Exception {

		if (!analyticsReportsDataProvider.isValidAnalyticsConnection(
				companyId)) {

			throw new PortalException("Unable to get social media ");
		}

		return analyticsReportsDataProvider.getReferringSocialMediaList(
			companyId, timeRange, canonicalURL);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetSocialTrafficSourcesMVCResourceCommand.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}