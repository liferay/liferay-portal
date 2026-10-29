/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.servlet.taglib;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
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

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "dynamic.include.key=/dynamic_include/top_head.jsp#analytics",
	service = DynamicInclude.class
)
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
				themeDisplay.getLocale(),
				segmentsExperienceManager.getSegmentsExperienceId(
					httpServletRequest),
				segmentsExperiment));

		sb.writeTo(httpServletResponse.getWriter());
	}

	private Map<String, String> _getValues(
		Locale locale, long segmentsExperienceId,
		SegmentsExperiment segmentsExperiment) {

		String segmentsExperienceKey = SegmentsExperienceConstants.KEY_DEFAULT;
		String segmentsExperienceName = LanguageUtil.get(locale, "default");
		String segmentsExperimentId = StringPool.BLANK;
		String segmentsVariantId = StringPool.BLANK;

		if (segmentsExperiment != null) {
			segmentsExperienceId = segmentsExperiment.getSegmentsExperienceId();
			segmentsExperimentId =
				segmentsExperiment.getSegmentsExperimentKey();
			segmentsVariantId = segmentsExperienceKey;
		}

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if (segmentsExperience != null) {
			segmentsExperienceKey =
				segmentsExperience.getSegmentsExperienceKey();
			segmentsExperienceName = segmentsExperience.getName(locale);
		}

		return HashMapBuilder.put(
			"experienceId", segmentsExperienceKey
		).put(
			"experienceName", segmentsExperienceName
		).put(
			"experimentId", segmentsExperimentId
		).put(
			"variantId", segmentsVariantId
		).build();
	}

	private static final String _TMPL_CONTENT = StringUtil.read(
		SegmentsExperimentAnalyticsTopHeadJSPDynamicInclude.class,
		"analytics.tmpl");

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}