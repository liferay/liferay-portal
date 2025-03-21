/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsEntryServiceUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class RateEntryAction extends JSONAction {

	@Override
	public String getJSON(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String className = getClassName(httpServletRequest);
		long classPK = getClassPK(httpServletRequest);
		double score = ParamUtil.getDouble(httpServletRequest, "score");

		if (score == -1) {
			RatingsEntryServiceUtil.deleteEntry(className, classPK);
		}
		else {
			RatingsEntryServiceUtil.updateEntry(className, classPK, score);
		}

		RatingsStats stats = RatingsStatsLocalServiceUtil.fetchStats(
			className, classPK);

		double averageScore = 0.0;
		int totalEntries = 0;
		double totalScore = 0.0;

		if (stats != null) {
			averageScore = stats.getAverageScore();
			totalEntries = stats.getTotalEntries();
			totalScore = stats.getTotalScore();
		}

		return JSONUtil.put(
			"averageScore", averageScore
		).put(
			"score", score
		).put(
			"totalEntries", totalEntries
		).put(
			"totalScore", totalScore
		).toString();
	}

	protected String getClassName(HttpServletRequest httpServletRequest) {
		return ParamUtil.getString(httpServletRequest, "className");
	}

	protected long getClassPK(HttpServletRequest httpServletRequest) {
		return ParamUtil.getLong(httpServletRequest, "classPK");
	}

}