/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.servlet.taglib;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.experiment.web.internal.constants.SegmentsExperimentWebKeys;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = DynamicInclude.class)
public class SegmentsExperimentAnalyticsTopHeadJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (!_analyticsSettingsManager.isSiteIdSynced(
					themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId())) {

				return;
			}
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}

		SegmentsExperienceManager segmentsExperienceManager =
			new SegmentsExperienceManager(_segmentsExperienceLocalService);

		httpServletRequest.setAttribute(
			SegmentsExperimentWebKeys.
				SEGMENTS_EXPERIMENT_SEGMENTS_EXPERIENCE_KEY,
			_getSegmentsExperienceKey(
				segmentsExperienceManager.getSegmentsExperienceId(
					httpServletRequest)));

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/top_head.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private String _getSegmentsExperienceKey(long segmentsExperienceId) {
		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if (segmentsExperience != null) {
			return segmentsExperience.getSegmentsExperienceKey();
		}

		return SegmentsExperienceConstants.KEY_DEFAULT;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperimentAnalyticsTopHeadJSPDynamicInclude.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.segments.experiment.web)"
	)
	private ServletContext _servletContext;

}