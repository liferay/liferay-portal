/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.ai.hub.web.internal.frontend.data.set.model.IssueReportFDSEntry;
import com.liferay.ai.hub.web.internal.frontend.data.set.provider.IssueReportFDSDataProvider;
import com.liferay.ai.hub.web.internal.frontend.data.set.sort.IssueReportFDSSorts;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Davyson Melo
 */
public class ViewIssueReportsDisplayContext {

	public ViewIssueReportsDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getCardsReactData() {
		List<IssueReportFDSEntry> entries =
			IssueReportFDSDataProvider.getSampleEntries();

		int total = entries.size();

		long positiveCount = entries.stream(
		).filter(
			entry -> "POSITIVE".equals(entry.getFeedbackType())
		).count();
		long negativeCount = entries.stream(
		).filter(
			entry -> "NEGATIVE".equals(entry.getFeedbackType())
		).count();
		long criticalCount = entries.stream(
		).filter(
			entry -> "CRITICAL".equals(entry.getLevel())
		).count();

		return HashMapBuilder.<String, Object>put(
			"criticalIssuesCount", criticalCount
		).put(
			"criticalIssuesLabel",
			LanguageUtil.get(_httpServletRequest, "critical-issues")
		).put(
			"dislikeRatingLabel",
			LanguageUtil.get(_httpServletRequest, "dislike-rating")
		).put(
			"dislikeRatingPercent", _percent(negativeCount, total)
		).put(
			"positiveRatingLabel",
			LanguageUtil.get(_httpServletRequest, "positive-rating")
		).put(
			"positiveRatingPercent", _percent(positiveCount, total)
		).put(
			"userActivityLabel",
			LanguageUtil.get(_httpServletRequest, "user-activity")
		).build();
	}

	public FDSSortItemList getFDSSortItems() {
		return IssueReportFDSSorts.getItems(_httpServletRequest);
	}

	public ThemeDisplay getThemeDisplay() {
		return _themeDisplay;
	}

	private int _percent(long count, int total) {
		if (total == 0) {
			return 0;
		}

		return (int)Math.round((count * 100.0) / total);
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}