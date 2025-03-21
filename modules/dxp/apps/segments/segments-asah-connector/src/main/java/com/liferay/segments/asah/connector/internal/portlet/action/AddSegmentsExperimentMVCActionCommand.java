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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
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
import com.liferay.segments.exception.DuplicateSegmentsExperimentException;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentRelService;
import com.liferay.segments.service.SegmentsExperimentService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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
		"mvc.command.name=/segments_experiment/add_segments_experiment"
	},
	service = MVCActionCommand.class
)
public class AddSegmentsExperimentMVCActionCommand
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

		Callable<JSONObject> callable = new AddSegmentsExperimentCallable(
			actionRequest);

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

	private JSONObject _addSegmentsExperiment(ActionRequest actionRequest)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				serviceContext.getCompanyId());

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		long plid = ParamUtil.getLong(actionRequest, "plid");

		SegmentsExperiment segmentsExperiment = null;

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if (segmentsExperience != null) {
			segmentsExperiment =
				_segmentsExperimentService.fetchSegmentsExperiment(
					serviceContext.getScopeGroupId(),
					segmentsExperience.getSegmentsExperienceKey(), plid);
		}

		if (segmentsExperiment != null) {
			if (segmentsExperiment.getStatus() ==
					SegmentsExperimentConstants.STATUS_TERMINATED) {

				Experiment experiment = ExperimentUtil.toExperiment(
					_companyLocalService,
					analyticsConfiguration.liferayAnalyticsDataSourceId(),
					_groupLocalService, _layoutLocalService,
					LocaleUtil.getSiteDefault(), _portal,
					_segmentsEntryLocalService, _segmentsExperienceLocalService,
					segmentsExperiment);

				experiment.setPublishable(false);

				_asahFaroBackendClient.updateExperiment(
					segmentsExperiment.getCompanyId(), experiment);

				segmentsExperiment.setStatus(
					SegmentsExperimentConstants.STATUS_DELETED_ON_DXP_ONLY);

				_segmentsExperimentService.deleteSegmentsExperiment(
					segmentsExperiment, false);

				segmentsExperienceId = _getActiveSegmentsExperienceId(
					serviceContext.getScopeGroupId(), plid,
					segmentsExperienceId);
			}
			else {
				throw new DuplicateSegmentsExperimentException();
			}
		}
		else {
			segmentsExperienceId = _getActiveSegmentsExperienceId(
				serviceContext.getScopeGroupId(), plid, segmentsExperienceId);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		segmentsExperiment = _segmentsExperimentService.addSegmentsExperiment(
			segmentsExperienceId, plid,
			ParamUtil.getString(actionRequest, "name"),
			ParamUtil.getString(actionRequest, "description"),
			ParamUtil.getString(actionRequest, "goal"),
			ParamUtil.getString(actionRequest, "goalTarget"), serviceContext);

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelService.getSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperiment.getSegmentsExperienceKey());

		return JSONUtil.put(
			"segmentsExperiment",
			SegmentsExperimentUtil.toSegmentsExperimentJSONObject(
				analyticsConfiguration, layout.getGroup(),
				themeDisplay.getLocale(), segmentsExperiment)
		).put(
			"segmentsExperimentRel",
			SegmentsExperimentUtil.toSegmentsExperimentRelJSONObject(
				themeDisplay.getLocale(), segmentsExperimentRel)
		);
	}

	private long _getActiveSegmentsExperienceId(
			long groupId, long plid, long segmentsExperienceId)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if (segmentsExperience != null) {
			return segmentsExperienceId;
		}

		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				groupId, plid, true, 0, 1, null);

		segmentsExperience = segmentsExperiences.get(0);

		return segmentsExperience.getSegmentsExperienceId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddSegmentsExperimentMVCActionCommand.class);

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
	private SegmentsExperimentRelService _segmentsExperimentRelService;

	@Reference
	private SegmentsExperimentService _segmentsExperimentService;

	private class AddSegmentsExperimentCallable
		implements Callable<JSONObject> {

		@Override
		public JSONObject call() throws Exception {
			return _addSegmentsExperiment(_actionRequest);
		}

		private AddSegmentsExperimentCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}