/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.simulation.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryServiceUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Eduardo García
 */
public class SegmentsSimulationDisplayContext {

	public SegmentsSimulationDisplayContext(
		HttpServletRequest httpServletRequest, Language language,
		SegmentsConfigurationProvider segmentsConfigurationProvider,
		SegmentsEntryLocalService segmentsEntryLocalService,
		SegmentsExperienceLocalService segmentsExperienceLocalService) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_segmentsConfigurationProvider = segmentsConfigurationProvider;
		_segmentsEntryLocalService = segmentsEntryLocalService;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"namespace", _getPortletNamespace()
		).put(
			"segmentationEnabled", _isSegmentationEnabled()
		).put(
			"segmentsCompanyConfigurationURL",
			_getSegmentsCompanyConfigurationURL()
		).put(
			"segmentsEntries", _getSegmentsEntriesJSONArray()
		).put(
			"segmentsExperiences", _getSegmentsExperiencesJSONArray()
		).build();
	}

	private String _getPortletNamespace() {
		return PortalUtil.getPortletNamespace(
			SegmentsPortletKeys.SEGMENTS_SIMULATION);
	}

	private String _getSegmentsCompanyConfigurationURL() {
		try {
			return _segmentsConfigurationProvider.getCompanyConfigurationURL(
				_httpServletRequest);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	private List<SegmentsEntry> _getSegmentsEntries() {
		if (_segmentsEntries != null) {
			return _segmentsEntries;
		}

		_segmentsEntries = SegmentsEntryServiceUtil.getSegmentsEntries(
			_getStagingAwareGroupId());

		return _segmentsEntries;
	}

	private JSONArray _getSegmentsEntriesJSONArray() {
		if (_segmentsEntriesJSONArray != null) {
			return _segmentsEntriesJSONArray;
		}

		_segmentsEntriesJSONArray = JSONUtil.put(
			JSONUtil.put(
				"id", SegmentsEntryConstants.ID_DEFAULT
			).put(
				"name",
				SegmentsEntryConstants.getDefaultSegmentsEntryName(
					_themeDisplay.getLocale())
			));

		for (SegmentsEntry segmentsEntry : _getSegmentsEntries()) {
			_segmentsEntriesJSONArray.put(
				JSONUtil.put(
					"id", segmentsEntry.getSegmentsEntryId()
				).put(
					"name", segmentsEntry.getName(_themeDisplay.getLocale())
				));
		}

		return _segmentsEntriesJSONArray;
	}

	private JSONArray _getSegmentsExperiencesJSONArray() throws Exception {
		if (_segmentsExperiencesJSONArray != null) {
			return _segmentsExperiencesJSONArray;
		}

		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				_themeDisplay.getScopeGroupId(), _themeDisplay.getPlid(), true);

		_segmentsExperiencesJSONArray = JSONUtil.toJSONArray(
			segmentsExperiences,
			segmentsExperience -> JSONUtil.put(
				"active", _isActive(segmentsExperience, segmentsExperiences)
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
				"segmentsExperienceId",
				segmentsExperience.getSegmentsExperienceId()
			).put(
				"segmentsExperienceName",
				segmentsExperience.getName(_themeDisplay.getLocale())
			).put(
				"statusLabel",
				() -> {
					String statusLabelKey = "inactive";

					if (_isActive(segmentsExperience, segmentsExperiences)) {
						statusLabelKey = "active";
					}

					return _language.get(_httpServletRequest, statusLabelKey);
				}
			));

		return _segmentsExperiencesJSONArray;
	}

	private long _getStagingAwareGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		_groupId = stagingGroupHelper.getStagedPortletGroupId(
			_themeDisplay.getScopeGroupId(), SegmentsPortletKeys.SEGMENTS);

		return _groupId;
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

	private boolean _isSegmentationEnabled() {
		try {
			return _segmentsConfigurationProvider.isSegmentationEnabled(
				_themeDisplay.getCompanyId());
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsSimulationDisplayContext.class);

	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final SegmentsConfigurationProvider _segmentsConfigurationProvider;
	private List<SegmentsEntry> _segmentsEntries;
	private JSONArray _segmentsEntriesJSONArray;
	private final SegmentsEntryLocalService _segmentsEntryLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;
	private JSONArray _segmentsExperiencesJSONArray;
	private final ThemeDisplay _themeDisplay;

}