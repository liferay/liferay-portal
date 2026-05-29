/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.sort;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.data.set.sort.FDSSorts;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Davyson Melo
 */
@Component(
	property = "frontend.data.set.name=" + AIHubFDSNames.ISSUE_REPORTS,
	service = FDSSorts.class
)
public class IssueReportFDSSorts implements FDSSorts {

	public static FDSSortItemList getItems(
		HttpServletRequest httpServletRequest) {

		return FDSSortItemListBuilder.add(
			_sortItem(httpServletRequest, "date", "date", "desc", true)
		).add(
			_sortItem(
				httpServletRequest, "agentName", "agent-name", "asc", false)
		).add(
			_sortItem(httpServletRequest, "surface", "surface", "asc", false)
		).add(
			_sortItem(
				httpServletRequest, "feedbackType", "feedback-type", "asc",
				false)
		).add(
			_sortItem(
				httpServletRequest, "issueType", "issue-type", "asc", false)
		).add(
			_sortItem(httpServletRequest, "level", "level", "asc", false)
		).add(
			_sortItem(
				httpServletRequest, "userMessage", "user-message", "asc", false)
		).add(
			_sortItem(
				httpServletRequest, "userEmail", "user-email", "asc", false)
		).build();
	}

	@Override
	public List<FDSSortItem> getFDSSortItems(
		HttpServletRequest httpServletRequest) {

		return getItems(httpServletRequest);
	}

	private static FDSSortItem _sortItem(
		HttpServletRequest httpServletRequest, String key, String labelKey,
		String direction, boolean active) {

		return FDSSortItemBuilder.setActive(
			active
		).setDirection(
			direction
		).setKey(
			key
		).setLabel(
			LanguageUtil.get(httpServletRequest, labelKey)
		).build();
	}

}