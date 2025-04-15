/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.portlet.action;

import com.liferay.analytics.reports.web.internal.constants.AnalyticsReportsPortletKeys;
import com.liferay.analytics.reports.web.internal.data.provider.AnalyticsReportsDataProvider;
import com.liferay.analytics.reports.web.internal.model.TimeRange;
import com.liferay.analytics.reports.web.internal.model.TimeSpan;
import com.liferay.analytics.reports.web.internal.model.TrafficChannel;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AnalyticsReportsPortletKeys.ANALYTICS_REPORTS,
		"mvc.command.name=/analytics_reports/get_traffic_sources"
	},
	service = MVCResourceCommand.class
)
public class GetTrafficSourcesMVCResourceCommand
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
				"trafficSources",
				_getTrafficSourcesJSONArray(
					analyticsReportsDataProvider, canonicalURL,
					themeDisplay.getCompanyId(),
					_portal.getLiferayPortletRequest(resourceRequest),
					_portal.getLiferayPortletResponse(resourceResponse),
					timeSpan.toTimeRange(timeSpanOffset), resourceBundle));

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (Exception exception) {
			_log.error(exception);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					ResourceBundleUtil.getString(
						resourceBundle, "an-unexpected-error-occurred")));
		}
	}

	private List<TrafficChannel> _getTrafficChannels(
			AnalyticsReportsDataProvider analyticsReportsDataProvider,
			String canonicalURL, long companyId, TimeRange timeRange)
		throws Exception {

		Map<TrafficChannel.Type, TrafficChannel> emptyMap = HashMapBuilder.put(
			TrafficChannel.Type.DIRECT,
			TrafficChannel.newInstance(0, 0.0, TrafficChannel.Type.DIRECT)
		).put(
			TrafficChannel.Type.ORGANIC,
			TrafficChannel.newInstance(0, 0.0, TrafficChannel.Type.ORGANIC)
		).put(
			TrafficChannel.Type.PAID,
			TrafficChannel.newInstance(0, 0.0, TrafficChannel.Type.PAID)
		).put(
			TrafficChannel.Type.REFERRAL,
			TrafficChannel.newInstance(0, 0.0, TrafficChannel.Type.REFERRAL)
		).put(
			TrafficChannel.Type.SOCIAL,
			TrafficChannel.newInstance(0, 0.0, TrafficChannel.Type.SOCIAL)
		).build();

		if (!analyticsReportsDataProvider.isValidAnalyticsConnection(
				companyId)) {

			PortalException portalException = new PortalException(
				"Invalid Analytics Connection");

			return Arrays.asList(
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.DIRECT),
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.ORGANIC),
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.PAID),
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.REFERRAL),
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.SOCIAL));
		}

		try {
			Map<TrafficChannel.Type, TrafficChannel> trafficChannels =
				analyticsReportsDataProvider.getTrafficChannels(
					companyId, timeRange, canonicalURL);

			emptyMap.forEach(
				(type, trafficChannel) -> trafficChannels.merge(
					type, trafficChannel,
					(trafficChannel1, trafficChannel2) -> trafficChannel1));

			return new ArrayList<>(trafficChannels.values());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return Arrays.asList(
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.DIRECT),
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.ORGANIC),
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.PAID),
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.REFERRAL),
				TrafficChannel.newInstance(
					portalException, TrafficChannel.Type.SOCIAL));
		}
	}

	private JSONArray _getTrafficSourcesJSONArray(
			AnalyticsReportsDataProvider analyticsReportsDataProvider,
			String canonicalURL, long companyId,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, TimeRange timeRange,
			ResourceBundle resourceBundle)
		throws Exception {

		List<TrafficChannel> trafficChannels = _getTrafficChannels(
			analyticsReportsDataProvider, canonicalURL, companyId, timeRange);

		Comparator<TrafficChannel> comparator = Comparator.comparing(
			TrafficChannel::getTrafficShare);

		trafficChannels = ListUtil.copy(trafficChannels);

		trafficChannels.sort(comparator.reversed());

		return JSONUtil.toJSONArray(
			trafficChannels,
			trafficChannel -> trafficChannel.toJSONObject(
				liferayPortletRequest, liferayPortletResponse, resourceBundle),
			_log);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetTrafficSourcesMVCResourceCommand.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Http _http;

	@Reference
	private Portal _portal;

}