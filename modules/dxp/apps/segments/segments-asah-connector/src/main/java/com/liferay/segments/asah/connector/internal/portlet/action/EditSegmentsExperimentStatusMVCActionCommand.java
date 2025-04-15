/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.portlet.action;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClient;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClientImpl;
import com.liferay.segments.asah.connector.internal.client.model.Experiment;
import com.liferay.segments.asah.connector.internal.client.model.util.ExperimentUtil;
import com.liferay.segments.asah.connector.internal.util.SegmentsExperimentUtil;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sarai Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
		"mvc.command.name=/segments_experiment/edit_segments_experiment_status"
	},
	service = MVCActionCommand.class
)
public class EditSegmentsExperimentStatusMVCActionCommand
	extends BaseMVCActionCommand {

	@Activate
	protected void activate(Map<String, Object> properties) {
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

		Callable<JSONObject> callable =
			new EditSegmentsExperimentStatusCallable(actionRequest);

		JSONObject jsonObject = null;

		try {
			jsonObject = TransactionInvokerUtil.invoke(
				_transactionConfig, callable);
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

	private JSONObject _editSegmentsExperimentStatus(
			ActionRequest actionRequest)
		throws Exception {

		return JSONUtil.put(
			"segmentsExperiment",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				Layout layout = themeDisplay.getLayout();

				long winnerSegmentsExperienceId = ParamUtil.getLong(
					actionRequest, "winnerSegmentsExperienceId", -1);

				SegmentsExperiment segmentsExperiment =
					_segmentsExperimentService.updateSegmentsExperimentStatus(
						ParamUtil.getLong(
							actionRequest, "segmentsExperimentId"),
						winnerSegmentsExperienceId,
						ParamUtil.getInteger(actionRequest, "status"));

				AnalyticsConfiguration analyticsConfiguration =
					_analyticsSettingsManager.getAnalyticsConfiguration(
						themeDisplay.getCompanyId());

				if (((segmentsExperiment.getStatus() ==
						SegmentsExperimentConstants.STATUS_COMPLETED) ||
					 (segmentsExperiment.getStatus() ==
						 SegmentsExperimentConstants.STATUS_TERMINATED)) &&
					(winnerSegmentsExperienceId != -1)) {

					Experiment experiment = ExperimentUtil.toExperiment(
						_companyLocalService,
						analyticsConfiguration.liferayAnalyticsDataSourceId(),
						_groupLocalService, _layoutLocalService,
						LocaleUtil.getSiteDefault(), _portal,
						_segmentsEntryLocalService,
						_segmentsExperienceLocalService, segmentsExperiment);

					experiment.setPublishable(false);

					_asahFaroBackendClient.updateExperiment(
						segmentsExperiment.getCompanyId(), experiment);

					segmentsExperiment.setStatus(
						SegmentsExperimentConstants.STATUS_DELETED_ON_DXP_ONLY);

					_segmentsExperimentService.deleteSegmentsExperiment(
						segmentsExperiment, false);

					segmentsExperiment = null;
				}

				return SegmentsExperimentUtil.toSegmentsExperimentJSONObject(
					analyticsConfiguration, layout.getGroup(),
					themeDisplay.getLocale(), segmentsExperiment);
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditSegmentsExperimentStatusMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	private AsahFaroBackendClient _asahFaroBackendClient;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Http _http;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperimentService _segmentsExperimentService;

	private class EditSegmentsExperimentStatusCallable
		implements Callable<JSONObject> {

		@Override
		public JSONObject call() throws Exception {
			return _editSegmentsExperimentStatus(_actionRequest);
		}

		private EditSegmentsExperimentStatusCallable(
			ActionRequest actionRequest) {

			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}