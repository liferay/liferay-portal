/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.portlet.action;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClient;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClientImpl;
import com.liferay.segments.asah.connector.internal.client.model.Experiment;
import com.liferay.segments.asah.connector.internal.client.model.ExperimentStatus;
import com.liferay.segments.asah.connector.internal.configuration.SegmentsExperimentConfiguration;
import com.liferay.segments.asah.connector.internal.util.SegmentsExperimentUtil;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperienceService;
import com.liferay.segments.service.SegmentsExperimentRelService;
import com.liferay.segments.service.SegmentsExperimentService;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	configurationPid = "com.liferay.segments.asah.connector.internal.configuration.SegmentsExperimentConfiguration",
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
		"mvc.command.name=/segments_experiment/get_data"
	},
	service = MVCResourceCommand.class
)
public class GetDataMVCResourceCommand extends BaseMVCResourceCommand {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_segmentsExperimentConfiguration = ConfigurableUtil.createConfigurable(
			SegmentsExperimentConfiguration.class, properties);

		_asahFaroBackendClient = new AsahFaroBackendClientImpl(
			_analyticsSettingsManager, _http);
	}

	@Deactivate
	protected void deactivate() {
		_asahFaroBackendClient = null;
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		try {
			String backURL = ParamUtil.getString(resourceRequest, "backURL");
			String backURLTitle = ParamUtil.getString(
				resourceRequest, "backURLTitle");
			String redirect = ParamUtil.getString(resourceRequest, "redirect");

			long plid = ParamUtil.getLong(resourceRequest, "plid");

			Layout layout = _layoutLocalService.getLayout(plid);

			long segmentsExperienceId = ParamUtil.getLong(
				resourceRequest, "segmentsExperienceId");

			Experiment experiment = _fetchAndSyncExperiment(
				layout.getGroupId(), plid, segmentsExperienceId);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"context",
					_getContextJSONObject(
						backURL, backURLTitle, layout, httpServletRequest,
						redirect, segmentsExperienceId)
				).put(
					"props",
					_getPropsJSONObject(
						experiment, httpServletRequest, layout,
						_portal.getLocale(httpServletRequest), redirect,
						segmentsExperienceId)
				));
		}
		catch (Exception exception) {
			_log.error(exception);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					_language.get(
						httpServletRequest, "an-unexpected-error-occurred")));
		}
	}

	private Experiment _fetchAndSyncExperiment(
			long groupId, long plid, long segmentsExperienceId)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentService.fetchSegmentsExperiment(
				groupId, segmentsExperience.getSegmentsExperienceKey(), plid);

		if (segmentsExperiment == null) {
			return null;
		}

		Experiment experiment = _asahFaroBackendClient.getExperiment(
			segmentsExperiment.getCompanyId(),
			segmentsExperiment.getSegmentsExperimentKey());

		ExperimentStatus experimentStatus = experiment.getExperimentStatus();

		SegmentsExperimentConstants.Status status =
			SegmentsExperimentConstants.Status.parse(experimentStatus.name());

		if (status == null) {
			return null;
		}

		if (!Objects.equals(
				segmentsExperiment.getStatus(), status.getValue())) {

			if (experiment.getWinnerDXPVariantId() != null) {
				segmentsExperience =
					_segmentsExperienceService.getSegmentsExperience(
						segmentsExperiment.getGroupId(),
						experiment.getWinnerDXPVariantId(),
						segmentsExperiment.getPlid());

				_segmentsExperimentService.updateSegmentsExperimentStatus(
					segmentsExperiment.getSegmentsExperimentId(),
					segmentsExperience.getSegmentsExperienceId(),
					status.getValue());
			}
			else {
				_segmentsExperimentService.updateSegmentsExperimentStatus(
					segmentsExperiment.getSegmentsExperimentId(),
					status.getValue());
			}
		}

		return experiment;
	}

	private SegmentsExperiment _fetchSegmentsExperiment(
			Layout layout, long segmentsExperienceId)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		return _segmentsExperimentService.fetchSegmentsExperiment(
			layout.getGroupId(), segmentsExperience.getSegmentsExperienceKey(),
			layout.getPlid());
	}

	private String _getContentPageEditorPortletNamespace() {
		return _portal.getPortletNamespace(
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET);
	}

	private JSONObject _getContextJSONObject(
			String backURL, String backURLTitle, Layout layout,
			HttpServletRequest httpServletRequest, String redirect,
			long segmentsExperienceId)
		throws Exception {

		return JSONUtil.put(
			"contentPageEditorNamespace",
			_getContentPageEditorPortletNamespace()
		).put(
			"endpoints",
			JSONUtil.put(
				"calculateSegmentsExperimentEstimatedDurationURL",
				_getSegmentsExperimentActionURL(
					"/calculate_segments_experiment_estimated_duration",
					httpServletRequest)
			).put(
				"createSegmentsExperimentURL",
				_getSegmentsExperimentActionURL(
					"/segments_experiment/add_segments_experiment",
					httpServletRequest)
			).put(
				"createSegmentsVariantURL",
				() -> PortletURLBuilder.create(
					_portletURLFactory.create(
						httpServletRequest,
						ContentPageEditorPortletKeys.
							CONTENT_PAGE_EDITOR_PORTLET,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/layout_content_page_editor/add_segments_experience"
				).buildString()
			).put(
				"deleteSegmentsExperimentURL",
				_getSegmentsExperimentActionURL(
					"/segments_experiment/delete_segments_experiment",
					httpServletRequest)
			).put(
				"deleteSegmentsVariantURL",
				_getSegmentsExperimentActionURL(
					"/segments_experiment/delete_segments_experiment_rel",
					httpServletRequest)
			).put(
				"editSegmentsExperimentStatusURL",
				_getSegmentsExperimentActionURL(
					"/segments_experiment/edit_segments_experiment_status",
					httpServletRequest)
			).put(
				"editSegmentsExperimentURL",
				_getSegmentsExperimentActionURL(
					"/segments_experiment/edit_segments_experiment",
					httpServletRequest)
			).put(
				"editSegmentsVariantLayoutURL",
				_getEditSegmentsVariantLayoutURL(
					backURL, backURLTitle, layout, redirect,
					segmentsExperienceId)
			).put(
				"editSegmentsVariantURL",
				_getSegmentsExperimentActionURL(
					"/segments_experiment/edit_segments_experiment_rel",
					httpServletRequest)
			).put(
				"runSegmentsExperimentURL",
				_getSegmentsExperimentActionURL(
					"/segments_experiment/run_segments_experiment",
					httpServletRequest)
			)
		).put(
			"imagesPath", _portal.getPathContext(httpServletRequest) + "/images"
		).put(
			"namespace",
			_portal.getPortletNamespace(SegmentsPortletKeys.SEGMENTS_EXPERIMENT)
		).put(
			"page",
			JSONUtil.put(
				"plid", layout.getPlid()
			).put(
				"type", layout.getType()
			)
		);
	}

	private String _getEditSegmentsVariantLayoutURL(
		String backURL, String backURLTitle, Layout layout, String redirect,
		long segmentsExperienceId) {

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layout.getPlid());

		if (draftLayout == null) {
			return StringPool.BLANK;
		}

		if (segmentsExperienceId != -1) {
			backURL = HttpComponentsUtil.setParameter(
				backURL, "segmentsExperienceId", segmentsExperienceId);
		}

		return HttpComponentsUtil.addParameters(
			redirect, "p_l_back_url", backURL, "p_l_back_url_title",
			backURLTitle, "p_l_mode", Constants.EDIT, "redirect", redirect);
	}

	private long _getLiveGroupId(long groupId) throws Exception {
		Group group = _stagingGroupHelper.fetchLiveGroup(groupId);

		if (group != null) {
			return group.getGroupId();
		}

		return groupId;
	}

	private JSONObject _getPropsJSONObject(
			Experiment experiment, HttpServletRequest httpServletRequest,
			Layout layout, Locale locale, String redirect,
			long segmentsExperienceId)
		throws Exception {

		Group group = layout.getGroup();

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				group.getCompanyId());

		return JSONUtil.put(
			"analyticsData",
			JSONUtil.put(
				"cloudTrialURL",
				SegmentsExperimentUtil.ANALYTICS_CLOUD_TRIAL_URL
			).put(
				"isConnected",
				_analyticsSettingsManager.isAnalyticsEnabled(
					group.getCompanyId())
			).put(
				"isSynced",
				_analyticsSettingsManager.isSiteIdSynced(
					group.getCompanyId(), _getLiveGroupId(group.getGroupId()))
			).put(
				"url", analyticsConfiguration.liferayAnalyticsURL()
			)
		).put(
			"hideSegmentsExperimentPanelURL",
			PortletURLBuilder.create(
				_portletURLFactory.create(
					httpServletRequest, SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"/segments_experiment/hide_segments_experiment_panel"
			).setRedirect(
				redirect
			).setParameter(
				"p_l_mode", Constants.VIEW
			).buildString()
		).put(
			"initialSegmentsVariants",
			() -> {
				JSONArray segmentsExperimentRelsJSONArray =
					_jsonFactory.createJSONArray();

				SegmentsExperiment segmentsExperiment =
					_fetchSegmentsExperiment(layout, segmentsExperienceId);

				if (segmentsExperiment == null) {
					return segmentsExperimentRelsJSONArray;
				}

				return JSONUtil.toJSONArray(
					_segmentsExperimentRelService.getSegmentsExperimentRels(
						segmentsExperiment.getSegmentsExperimentId()),
					segmentsExperimentRel ->
						SegmentsExperimentUtil.
							toSegmentsExperimentRelJSONObject(
								experiment, locale, segmentsExperimentRel));
			}
		).put(
			"pathToAssets", _portal.getPathContext(httpServletRequest)
		).put(
			"segmentsExperiences",
			JSONUtil.toJSONArray(
				_segmentsExperienceService.getSegmentsExperiences(
					layout.getGroupId(), layout.getPlid(), true),
				segmentsExperience -> JSONUtil.put(
					"name", segmentsExperience.getName(locale)
				).put(
					"segmentsExperienceId",
					String.valueOf(segmentsExperience.getSegmentsExperienceId())
				).put(
					"segmentsExperiment",
					SegmentsExperimentUtil.toSegmentsExperimentJSONObject(
						analyticsConfiguration, group, locale,
						_fetchSegmentsExperiment(
							layout,
							segmentsExperience.getSegmentsExperienceId()))
				))
		).put(
			"segmentsExperiment",
			SegmentsExperimentUtil.toSegmentsExperimentJSONObject(
				analyticsConfiguration, group, locale,
				_fetchSegmentsExperiment(layout, segmentsExperienceId))
		).put(
			"segmentsExperimentGoals",
			JSONUtil.toJSONArray(
				SegmentsExperimentConstants.Goal.values(),
				goal -> {
					if (!ArrayUtil.contains(
							_segmentsExperimentConfiguration.goalsEnabled(),
							goal.name())) {

						return null;
					}

					return JSONUtil.put(
						"label", _language.get(locale, goal.getLabel())
					).put(
						"value", goal.getLabel()
					);
				})
		).put(
			"selectedSegmentsExperienceId", segmentsExperienceId
		).put(
			"winnerSegmentsVariantId",
			() -> {
				SegmentsExperiment segmentsExperiment =
					_fetchSegmentsExperiment(layout, segmentsExperienceId);

				if (segmentsExperiment == null) {
					return StringPool.BLANK;
				}

				long winnerSegmentsExperienceId =
					segmentsExperiment.getWinnerSegmentsExperienceId();

				if (winnerSegmentsExperienceId == -1) {
					return StringPool.BLANK;
				}

				return String.valueOf(winnerSegmentsExperienceId);
			}
		);
	}

	private String _getSegmentsExperimentActionURL(
		String action, HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portletURLFactory.create(
				httpServletRequest, SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			action
		).setParameter(
			"p_l_mode", Constants.VIEW
		).buildString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetDataMVCResourceCommand.class);

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
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperienceService _segmentsExperienceService;

	private volatile SegmentsExperimentConfiguration
		_segmentsExperimentConfiguration;

	@Reference
	private SegmentsExperimentRelService _segmentsExperimentRelService;

	@Reference
	private SegmentsExperimentService _segmentsExperimentService;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}