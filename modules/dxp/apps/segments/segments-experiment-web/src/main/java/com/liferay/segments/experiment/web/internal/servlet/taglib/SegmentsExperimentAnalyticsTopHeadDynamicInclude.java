/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.servlet.taglib;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.experiment.web.internal.constants.SegmentsExperimentWebKeys;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = DynamicInclude.class)
public class SegmentsExperimentAnalyticsTopHeadDynamicInclude
	implements DynamicInclude {

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

		SegmentsExperiment segmentsExperiment =
			(SegmentsExperiment)httpServletRequest.getAttribute(
				SegmentsExperimentWebKeys.SEGMENTS_EXPERIMENT);

		SegmentsExperienceManager segmentsExperienceManager =
			new SegmentsExperienceManager(_segmentsExperienceLocalService);

		StringBundler sb = StringUtil.replaceToStringBundler(
			_TMPL_CONTENT, "${", "}",
			_getValues(
				segmentsExperiment,
				_getSegmentsExperienceKey(
					segmentsExperienceManager.getSegmentsExperienceId(
						httpServletRequest))));

		sb.writeTo(httpServletResponse.getWriter());
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/dynamic_include/top_head.jsp#analytics");
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

	private Map<String, String> _getValues(
		SegmentsExperiment segmentsExperiment,
		String segmentsExperimentSegmentsExperienceKey) {

		Map<String, String> analyticsClientContextMap = new HashMap<>();

		if (segmentsExperiment == null) {
			analyticsClientContextMap.put(
				"experienceId", segmentsExperimentSegmentsExperienceKey);
			analyticsClientContextMap.put("experimentId", StringPool.BLANK);
			analyticsClientContextMap.put("variantId", StringPool.BLANK);

			return analyticsClientContextMap;
		}

		analyticsClientContextMap.put(
			"experienceId", segmentsExperiment.getSegmentsExperienceKey());
		analyticsClientContextMap.put(
			"experimentId", segmentsExperiment.getSegmentsExperimentKey());
		analyticsClientContextMap.put(
			"variantId", segmentsExperimentSegmentsExperienceKey);

		return analyticsClientContextMap;
	}

	private static final String _TMPL_CONTENT = StringUtil.read(
		SegmentsExperimentAnalyticsTopHeadJSPDynamicInclude.class,
		"analytics.tmpl");

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}