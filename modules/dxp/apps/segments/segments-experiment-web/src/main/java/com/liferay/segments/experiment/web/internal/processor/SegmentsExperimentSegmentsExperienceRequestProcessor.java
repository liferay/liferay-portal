/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.processor;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.experiment.web.internal.constants.SegmentsExperimentWebKeys;
import com.liferay.segments.experiment.web.internal.util.SegmentsCookieManagerUtil;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.processor.SegmentsExperienceRequestProcessor;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "segments.experience.request.processor.priority:Integer=50",
	service = SegmentsExperienceRequestProcessor.class
)
public class SegmentsExperimentSegmentsExperienceRequestProcessor
	implements SegmentsExperienceRequestProcessor {

	@Override
	public long[] getSegmentsExperienceIds(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long groupId, long plid,
			long[] segmentsExperienceIds)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (!_analyticsSettingsManager.isSiteIdSynced(
					themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId())) {

				return segmentsExperienceIds;
			}
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		long segmentsExperienceId = _getSelectedSegmentsExperienceId(
			httpServletRequest, themeDisplay);

		if (segmentsExperienceId != -1) {
			return new long[] {segmentsExperienceId};
		}

		String segmentsExperimentKey = _getSelectedSegmentsExperimentKey(
			httpServletRequest);

		if (Validator.isNotNull(segmentsExperimentKey)) {
			SegmentsExperiment segmentsExperiment =
				_segmentsExperimentLocalService.fetchSegmentsExperiment(
					themeDisplay.getScopeGroupId(), segmentsExperimentKey);

			if ((segmentsExperiment != null) &&
				(segmentsExperiment.getPlid() == themeDisplay.getPlid())) {

				return new long[] {
					segmentsExperiment.getSegmentsExperienceId()
				};
			}
		}

		segmentsExperienceId = _getCurrentSegmentsExperienceId(
			groupId, plid, httpServletRequest);

		if (segmentsExperienceId != -1) {
			SegmentsExperiment segmentsExperiment =
				_segmentsExperimentLocalService.fetchSegmentsExperiment(
					themeDisplay.getScopeGroupId(),
					_getSegmentsExperienceKey(segmentsExperienceId), plid);

			if (segmentsExperiment != null) {
				httpServletRequest.setAttribute(
					SegmentsExperimentWebKeys.SEGMENTS_EXPERIMENT,
					segmentsExperiment);

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Serving previous experience ",
							segmentsExperienceId, " as its experiment ",
							segmentsExperiment.getSegmentsExperimentId(),
							" is still running"));
				}

				return new long[] {segmentsExperienceId};
			}
		}

		SegmentsCookieManagerUtil.unsetCookie(
			httpServletRequest, httpServletResponse, plid);

		if (ArrayUtil.isEmpty(segmentsExperienceIds)) {
			segmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(plid);
		}
		else {
			segmentsExperienceId = segmentsExperienceIds[0];
		}

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.fetchSegmentsExperiment(
				themeDisplay.getScopeGroupId(),
				_getSegmentsExperienceKey(segmentsExperienceId), plid);

		if (segmentsExperiment == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No experiment running for the user experiences " +
						StringUtil.merge(segmentsExperienceIds));
			}

			return segmentsExperienceIds;
		}

		List<SegmentsExperimentRel> segmentsExperimentRels =
			_segmentsExperimentRelLocalService.getSegmentsExperimentRels(
				segmentsExperiment.getSegmentsExperimentId());

		if (segmentsExperimentRels.isEmpty()) {
			return segmentsExperienceIds;
		}

		segmentsExperienceId = _getSegmentsExperimentSegmentsExperienceId(
			segmentsExperiment.getSegmentsExperienceId(),
			segmentsExperimentRels);

		SegmentsCookieManagerUtil.setCookie(
			httpServletRequest, httpServletResponse, plid,
			_getSegmentsExperienceKey(segmentsExperienceId));

		httpServletRequest.setAttribute(
			SegmentsExperimentWebKeys.SEGMENTS_EXPERIMENT, segmentsExperiment);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Serving experience ", segmentsExperienceId,
					" for running experiment ",
					segmentsExperiment.getSegmentsExperimentId()));
		}

		return new long[] {segmentsExperienceId};
	}

	@Override
	public long[] getSegmentsExperienceIds(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long groupId, long plid,
			long[] segmentsEntryIds, long[] segmentsExperienceIds)
		throws PortalException {

		return getSegmentsExperienceIds(
			httpServletRequest, httpServletResponse, groupId, plid,
			segmentsExperienceIds);
	}

	private long _getCurrentSegmentsExperienceId(
		long groupId, long plid, HttpServletRequest httpServletRequest) {

		Cookie cookie = SegmentsCookieManagerUtil.getCookie(
			httpServletRequest, plid);

		if (cookie == null) {
			return -1;
		}

		return _getSegmentsExperienceId(groupId, cookie.getValue(), plid);
	}

	private long _getSegmentsExperienceId(
		long groupId, String segmentsExperienceKey, long plid) {

		if (Validator.isNotNull(segmentsExperienceKey)) {
			SegmentsExperience segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					groupId, segmentsExperienceKey, plid);

			if (segmentsExperience != null) {
				return segmentsExperience.getSegmentsExperienceId();
			}
		}

		return -1;
	}

	private String _getSegmentsExperienceKey(long segmentsExperienceId) {
		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		return segmentsExperience.getSegmentsExperienceKey();
	}

	private long _getSegmentsExperimentSegmentsExperienceId(
		long controlSegmentsExperienceId,
		List<SegmentsExperimentRel> segmentsExperimentRels) {

		double random = Math.random();

		for (SegmentsExperimentRel segmentsExperimentRel :
				segmentsExperimentRels) {

			random -= segmentsExperimentRel.getSplit();

			if (random <= 0.0D) {
				return segmentsExperimentRel.getSegmentsExperienceId();
			}
		}

		return controlSegmentsExperienceId;
	}

	private long _getSelectedSegmentsExperienceId(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		if (!themeDisplay.isSignedIn()) {
			return -1;
		}

		long selectedSegmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId", -1);

		if (selectedSegmentsExperienceId != -1) {
			SegmentsExperience segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					selectedSegmentsExperienceId);

			if ((segmentsExperience != null) &&
				(segmentsExperience.getPlid() == themeDisplay.getPlid())) {

				return selectedSegmentsExperienceId;
			}
		}

		String selectedSegmentsExperienceKey = ParamUtil.getString(
			httpServletRequest, "segmentsExperienceKey");

		return _getSegmentsExperienceId(
			themeDisplay.getScopeGroupId(), selectedSegmentsExperienceKey,
			themeDisplay.getPlid());
	}

	private String _getSelectedSegmentsExperimentKey(
		HttpServletRequest httpServletRequest) {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		return ParamUtil.getString(
			originalHttpServletRequest, "segmentsExperimentKey");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperimentSegmentsExperienceRequestProcessor.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Reference
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

}