/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.display.context;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
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

import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class SegmentsExperienceSelectorDisplayContext {

	public SegmentsExperienceSelectorDisplayContext(
		HttpServletRequest httpServletRequest, JSONFactory jsonFactory,
		Language language, Portal portal,
		SegmentsEntryLocalService segmentsEntryLocalService,
		SegmentsExperienceManager segmentsExperienceManager,
		SegmentsExperienceLocalService segmentsExperienceLocalService,
		SegmentsExperimentLocalService segmentsExperimentLocalService,
		SegmentsExperimentRelLocalService segmentsExperimentRelLocalService) {

		_httpServletRequest = httpServletRequest;
		_jsonFactory = jsonFactory;
		_language = language;
		_portal = portal;
		_segmentsEntryLocalService = segmentsEntryLocalService;
		_segmentsExperienceManager = segmentsExperienceManager;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;
		_segmentsExperimentLocalService = segmentsExperimentLocalService;
		_segmentsExperimentRelLocalService = segmentsExperimentRelLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getData() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"segmentsExperiences", _getSegmentsExperiencesJSONArray()
		).put(
			"selectedSegmentsExperience",
			_getSegmentsExperienceSelectedJSONObject()
		).build();
	}

	private SegmentsExperience _fetchSegmentsExperienceFromRequest() {
		long segmentsExperienceId = ParamUtil.getLong(
			_httpServletRequest, "segmentsExperienceId", -1);

		if (segmentsExperienceId == -1) {
			segmentsExperienceId =
				_segmentsExperienceManager.getSegmentsExperienceId(
					_httpServletRequest);
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
			long segmentsExperienceId)
		throws PortalException {

		JSONArray segmentsExperiencesJSONArray =
			_getSegmentsExperiencesJSONArray();

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
		List<SegmentsExperience> segmentsExperiences) {

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
					return segmentsEntry.getName(_themeDisplay.getLocale());
				}

				return SegmentsEntryConstants.getDefaultSegmentsEntryName(
					_themeDisplay.getLocale());
			}
		).put(
			"segmentsExperienceId", segmentsExperience.getSegmentsExperienceId()
		).put(
			"segmentsExperienceName",
			segmentsExperience.getName(_themeDisplay.getLocale())
		).put(
			"statusLabel",
			() -> {
				String statusLabelKey = "inactive";

				if (segmentsExperienceIsActive) {
					statusLabelKey = "active";
				}

				return _language.get(_httpServletRequest, statusLabelKey);
			}
		).put(
			"url",
			HttpComponentsUtil.setParameter(
				_portal.getCurrentURL(_httpServletRequest),
				"segmentsExperienceId",
				segmentsExperience.getSegmentsExperienceId())
		);
	}

	private JSONObject _getSegmentsExperienceSelectedJSONObject()
		throws PortalException {

		SegmentsExperience segmentsExperience =
			_fetchSegmentsExperienceFromRequest();

		if ((segmentsExperience == null) ||
			(segmentsExperience.getPlid() != _themeDisplay.getPlid())) {

			long defaultSegmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_themeDisplay.getPlid());

			segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					defaultSegmentsExperienceId);
		}

		if (segmentsExperience != null) {
			SegmentsExperience parentSegmentsExperience =
				_getParentSegmentsExperience(segmentsExperience, _themeDisplay);

			if (parentSegmentsExperience != null) {
				segmentsExperience = parentSegmentsExperience;
			}

			JSONObject segmentsExperienceSelectedJSONObject =
				_getSegmentsExperienceJSONObject(
					segmentsExperience.getSegmentsExperienceId());

			segmentsExperienceSelectedJSONObject.put(
				"segmentsExperienceName",
				segmentsExperience.getName(_themeDisplay.getLocale()));

			return segmentsExperienceSelectedJSONObject;
		}

		return _jsonFactory.createJSONObject();
	}

	private JSONArray _getSegmentsExperiencesJSONArray()
		throws PortalException {

		if (_segmentsExperiencesJSONArray != null) {
			return _segmentsExperiencesJSONArray;
		}

		JSONArray segmentsExperiencesJSONArray = _jsonFactory.createJSONArray();

		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				_themeDisplay.getScopeGroupId(), _themeDisplay.getPlid(), true);

		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			segmentsExperiencesJSONArray.put(
				_getSegmentsExperienceJSONObject(
					segmentsExperience, segmentsExperiences));
		}

		_segmentsExperiencesJSONArray = segmentsExperiencesJSONArray;

		return _segmentsExperiencesJSONArray;
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
		SegmentsExperienceSelectorDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final JSONFactory _jsonFactory;
	private final Language _language;
	private final Portal _portal;
	private final SegmentsEntryLocalService _segmentsEntryLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;
	private final SegmentsExperienceManager _segmentsExperienceManager;
	private JSONArray _segmentsExperiencesJSONArray;
	private final SegmentsExperimentLocalService
		_segmentsExperimentLocalService;
	private final SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;
	private final ThemeDisplay _themeDisplay;

}