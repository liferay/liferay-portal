/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.portlet.action;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClient;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClientImpl;
import com.liferay.segments.asah.connector.internal.client.model.util.ExperimentSettingsUtil;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
		"mvc.command.name=/calculate_segments_experiment_estimated_duration"
	},
	service = MVCActionCommand.class
)
public class CalculateSegmentsExperimentEstimatedDurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Activate
	protected void activate() {
		_asahFaroBackendClient = new AsahFaroBackendClientImpl(
			_analyticsSettingsManager, _http);
	}

	@Deactivate
	protected void deactivate() {
		_asahFaroBackendClient = null;
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONObject jsonObject = null;

		try {
			jsonObject =
				_calculateSegmentsExperimentEstimatedDaysDurationJSONObject(
					actionRequest);
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			jsonObject = JSONUtil.put(
				"error",
				_language.get(
					themeDisplay.getRequest(), "an-unexpected-error-occurred"));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);

		hideDefaultSuccessMessage(actionRequest);
	}

	private Long _calculateSegmentsExperimentEstimatedDaysDuration(
			double confidenceLevel, SegmentsExperiment segmentsExperiment,
			Map<String, Double> segmentsExperienceKeySplitMap)
		throws Exception {

		if (!_analyticsSettingsManager.isAnalyticsEnabled(
				segmentsExperiment.getCompanyId())) {

			return null;
		}

		return _asahFaroBackendClient.calculateExperimentEstimatedDaysDuration(
			segmentsExperiment.getCompanyId(),
			segmentsExperiment.getSegmentsExperimentKey(),
			ExperimentSettingsUtil.toExperimentSettings(
				confidenceLevel, segmentsExperienceKeySplitMap,
				segmentsExperiment));
	}

	private JSONObject
			_calculateSegmentsExperimentEstimatedDaysDurationJSONObject(
				ActionRequest actionRequest)
		throws PortalException {

		return JSONUtil.put(
			"segmentsExperimentEstimatedDaysDuration",
			() -> {
				long segmentsExperimentId = ParamUtil.getLong(
					actionRequest, "segmentsExperimentId");

				SegmentsExperiment segmentsExperiment =
					_segmentsExperimentLocalService.getSegmentsExperiment(
						segmentsExperimentId);

				String segmentsExperimentRels = ParamUtil.getString(
					actionRequest, "segmentsExperimentRels");

				JSONObject segmentsExperimentRelsJSONObject =
					_jsonFactory.createJSONObject(segmentsExperimentRels);

				Iterator<String> iterator =
					segmentsExperimentRelsJSONObject.keys();

				Map<String, Double> segmentsExperienceKeySplitMap =
					new HashMap<>();

				while (iterator.hasNext()) {
					String key = iterator.next();

					SegmentsExperimentRel segmentsExperimentRel =
						_segmentsExperimentRelLocalService.
							getSegmentsExperimentRel(GetterUtil.getLong(key));

					segmentsExperienceKeySplitMap.put(
						segmentsExperimentRel.getSegmentsExperienceKey(),
						segmentsExperimentRelsJSONObject.getDouble(key));
				}

				return _calculateSegmentsExperimentEstimatedDaysDuration(
					ParamUtil.getDouble(actionRequest, "confidenceLevel"),
					segmentsExperiment, segmentsExperienceKeySplitMap);
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalculateSegmentsExperimentEstimatedDurationMVCActionCommand.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	private AsahFaroBackendClient _asahFaroBackendClient;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Reference
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

}