/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.portlet.action;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.experiment.web.internal.util.SegmentsExperimentUtil;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.segments.service.SegmentsExperimentService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sarai Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
		"mvc.command.name=/segments_experiment/run_segments_experiment"
	},
	service = MVCActionCommand.class
)
public class RunSegmentsExperimentMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONObject jsonObject = null;

		try {
			jsonObject = _runSegmentsExperiment(actionRequest);
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

	private JSONObject _runSegmentsExperiment(ActionRequest actionRequest)
		throws PortalException {

		long segmentsExperimentId = ParamUtil.getLong(
			actionRequest, "segmentsExperimentId");

		String segmentsExperimentRels = ParamUtil.getString(
			actionRequest, "segmentsExperimentRels");

		JSONObject segmentsExperimentRelsJSONObject =
			_jsonFactory.createJSONObject(segmentsExperimentRels);

		Iterator<String> iterator = segmentsExperimentRelsJSONObject.keys();

		Map<Long, Double> segmentsExperienceIdSplitMap = new HashMap<>();

		while (iterator.hasNext()) {
			String key = iterator.next();

			SegmentsExperimentRel segmentsExperimentRel =
				_segmentsExperimentRelLocalService.getSegmentsExperimentRel(
					GetterUtil.getLong(key));

			segmentsExperienceIdSplitMap.put(
				segmentsExperimentRel.getSegmentsExperienceId(),
				segmentsExperimentRelsJSONObject.getDouble(key));
		}

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentService.runSegmentsExperiment(
				segmentsExperimentId,
				ParamUtil.getDouble(actionRequest, "confidenceLevel"),
				segmentsExperienceIdSplitMap,
				ParamUtil.getString(actionRequest, "segmentsExperimentType"));

		return JSONUtil.put(
			"segmentsExperiment",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				Layout layout = themeDisplay.getLayout();

				return SegmentsExperimentUtil.toSegmentsExperimentJSONObject(
					_analyticsSettingsManager.getAnalyticsConfiguration(
						themeDisplay.getCompanyId()),
					layout.getGroup(), themeDisplay.getLocale(),
					segmentsExperiment);
			}
		).put(
			"segmentsExperimentRels", segmentsExperimentRelsJSONObject
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RunSegmentsExperimentMVCActionCommand.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

	@Reference
	private SegmentsExperimentService _segmentsExperimentService;

}