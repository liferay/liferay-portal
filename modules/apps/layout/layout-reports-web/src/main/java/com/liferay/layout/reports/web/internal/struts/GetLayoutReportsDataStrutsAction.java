/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.reports.web.internal.struts;

import com.liferay.layout.reports.web.internal.configuration.provider.LayoutReportsGooglePageSpeedConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "path=/layout_reports/get_layout_reports_data",
	service = StrutsAction.class
)
public class GetLayoutReportsDataStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeContent() || layout.isTypeAssetDisplay()) {
			jsonArray.put(
				JSONUtil.put(
					"id", "performance"
				).put(
					"name",
					_language.get(themeDisplay.getLocale(), "performance")
				).put(
					"url",
					() -> {
						String url = HttpComponentsUtil.addParameter(
							themeDisplay.getPortalURL() +
								themeDisplay.getPathMain() +
									"/layout_reports/get_layout_item_data",
							"p_l_id", themeDisplay.getPlid());

						long segmentsExperienceId = ParamUtil.getLong(
							_portal.getOriginalServletRequest(
								httpServletRequest),
							"segmentsExperienceId", -1);

						if (segmentsExperienceId == -1) {
							return url;
						}

						return HttpComponentsUtil.addParameter(
							url, "segmentsExperienceId", segmentsExperienceId);
					}
				));
		}

		if (_layoutReportsGooglePageSpeedConfigurationProvider.isEnabled(
				themeDisplay.getScopeGroup())) {

			jsonArray.put(
				JSONUtil.put(
					"id", "page-speed-insights"
				).put(
					"name",
					_language.get(
						themeDisplay.getLocale(), "page-speed-insights")
				).put(
					"url",
					HttpComponentsUtil.addParameter(
						themeDisplay.getPortalURL() +
							themeDisplay.getPathMain() +
								"/layout_reports/get_google_page_speed_data",
						"p_l_id", themeDisplay.getPlid())
				));
		}

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.put(
				"segmentsExperienceSelectorData",
				JSONUtil.put(
					"segmentsExperiences",
					_getSegmentsExperiencesJSONArray(themeDisplay)
				).put(
					"selectedSegmentsExperience",
					_getSegmentsExperienceSelectedJSONObject(
						httpServletRequest, themeDisplay)
				)
			).put(
				"tabsData", jsonArray
			).toString());

		return null;
	}

	private SegmentsExperience _fetchSegmentsExperience(
		HttpServletRequest httpServletRequest) {

		long segmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId", -1);

		if (segmentsExperienceId == -1) {
			SegmentsExperienceManager segmentsExperienceManager =
				new SegmentsExperienceManager(_segmentsExperienceLocalService);

			segmentsExperienceId =
				segmentsExperienceManager.getSegmentsExperienceId(
					httpServletRequest);
		}

		return _segmentsExperienceLocalService.fetchSegmentsExperience(
			segmentsExperienceId);
	}

	private SegmentsExperience _getParentSegmentsExperience(
		SegmentsExperience segmentsExperience, ThemeDisplay themeDisplay) {

		List<SegmentsExperimentRel> segmentsExperimentRels =
			_segmentsExperimentRelLocalService.
				getSegmentsExperimentRelsBySegmentsExperienceKey(
					segmentsExperience.getSegmentsExperienceKey(),
					themeDisplay.getPlid());

		if (segmentsExperimentRels.isEmpty()) {
			return null;
		}

		SegmentsExperimentRel segmentsExperimentRel =
			segmentsExperimentRels.get(0);

		try {
			SegmentsExperiment segmentsExperiment =
				_segmentsExperimentLocalService.getSegmentsExperiment(
					segmentsExperimentRel.getSegmentsExperimentId());

			return _segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperiment.getSegmentsExperienceId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private JSONObject _getSegmentsExperienceJSONObject(
			long segmentsExperienceId, ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray segmentsExperiencesJSONArray =
			_getSegmentsExperiencesJSONArray(themeDisplay);

		for (int i = 0; i < segmentsExperiencesJSONArray.length(); i++) {
			JSONObject segmentsExperiencesJSONObject =
				segmentsExperiencesJSONArray.getJSONObject(i);

			if (segmentsExperienceId == segmentsExperiencesJSONObject.getLong(
					"segmentsExperienceId")) {

				return segmentsExperiencesJSONObject;
			}
		}

		return _jsonFactory.createJSONObject();
	}

	private JSONObject _getSegmentsExperienceJSONObject(
			SegmentsExperience segmentsExperience,
			List<SegmentsExperience> segmentsExperiences,
			ThemeDisplay themeDisplay)
		throws Exception {

		boolean segmentsExperienceIsActive = _isActive(
			segmentsExperience, segmentsExperiences);

		return JSONUtil.put(
			"active", segmentsExperienceIsActive
		).put(
			"segmentsEntryId", segmentsExperience.getSegmentsEntryId()
		).put(
			"segmentsEntryName",
			() -> {
				SegmentsEntry segmentsEntry =
					_segmentsEntryLocalService.fetchSegmentsEntry(
						segmentsExperience.getSegmentsEntryId());

				if (segmentsEntry != null) {
					return segmentsEntry.getName(themeDisplay.getLocale());
				}

				return SegmentsEntryConstants.getDefaultSegmentsEntryName(
					themeDisplay.getLocale());
			}
		).put(
			"segmentsExperienceId", segmentsExperience.getSegmentsExperienceId()
		).put(
			"segmentsExperienceName",
			segmentsExperience.getName(themeDisplay.getLocale())
		).put(
			"statusLabel",
			() -> {
				String statusLabelKey = "inactive";

				if (segmentsExperienceIsActive) {
					statusLabelKey = "active";
				}

				return _language.get(themeDisplay.getLocale(), statusLabelKey);
			}
		).put(
			"url",
			HttpComponentsUtil.setParameter(
				_portal.getLayoutURL(themeDisplay), "segmentsExperienceId",
				segmentsExperience.getSegmentsExperienceId())
		);
	}

	private JSONObject _getSegmentsExperienceSelectedJSONObject(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		SegmentsExperience segmentsExperience = _fetchSegmentsExperience(
			httpServletRequest);

		long plid = themeDisplay.getPlid();

		Layout layout = themeDisplay.getLayout();

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		if ((segmentsExperience == null) ||
			(segmentsExperience.getPlid() != plid)) {

			long defaultSegmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(plid);

			segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					defaultSegmentsExperienceId);
		}

		if (segmentsExperience != null) {
			JSONObject segmentsExperienceSelectedJSONObject =
				_getSegmentsExperienceJSONObject(
					segmentsExperience.getSegmentsExperienceId(), themeDisplay);

			segmentsExperienceSelectedJSONObject.put(
				"segmentsExperienceName",
				_getSelectedSegmentsExperienceName(
					segmentsExperience, themeDisplay));

			return segmentsExperienceSelectedJSONObject;
		}

		return _jsonFactory.createJSONObject();
	}

	private JSONArray _getSegmentsExperiencesJSONArray(
			ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray segmentsExperiencesJSONArray = _jsonFactory.createJSONArray();

		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(), true);

		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			segmentsExperiencesJSONArray.put(
				_getSegmentsExperienceJSONObject(
					segmentsExperience, segmentsExperiences, themeDisplay));
		}

		return segmentsExperiencesJSONArray;
	}

	private String _getSelectedSegmentsExperienceName(
		SegmentsExperience segmentsExperience, ThemeDisplay themeDisplay) {

		SegmentsExperience parentSegmentsExperience =
			_getParentSegmentsExperience(segmentsExperience, themeDisplay);

		if (parentSegmentsExperience != null) {
			segmentsExperience = parentSegmentsExperience;
		}

		if (segmentsExperience != null) {
			return segmentsExperience.getName(themeDisplay.getLocale());
		}

		return SegmentsEntryConstants.getDefaultSegmentsEntryName(
			themeDisplay.getLocale());
	}

	private boolean _isActive(
		SegmentsExperience segmentsExperience,
		List<SegmentsExperience> segmentsExperiences) {

		for (SegmentsExperience curSegmentsExperience : segmentsExperiences) {
			if ((curSegmentsExperience.getSegmentsEntryId() ==
					segmentsExperience.getSegmentsEntryId()) ||
				(curSegmentsExperience.getSegmentsEntryId() ==
					SegmentsEntryConstants.ID_DEFAULT)) {

				if (curSegmentsExperience.getSegmentsExperienceId() ==
						segmentsExperience.getSegmentsExperienceId()) {

					return true;
				}

				return false;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetLayoutReportsDataStrutsAction.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutReportsGooglePageSpeedConfigurationProvider
		_layoutReportsGooglePageSpeedConfigurationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Reference
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

}