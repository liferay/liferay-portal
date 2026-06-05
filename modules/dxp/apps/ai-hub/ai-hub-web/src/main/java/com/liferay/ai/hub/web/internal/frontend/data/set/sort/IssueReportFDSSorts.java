/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.sort;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
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

	@Override
	public List<FDSSortItem> getFDSSortItems(
		HttpServletRequest httpServletRequest) {

		return FDSSortItemListBuilder.add(
			_createFDSSortItem(
				httpServletRequest, "dateCreated", "date", "desc", true)
		).add(
			_createFDSSortItem(
				httpServletRequest, "surface", "surface", "asc", false)
		).add(
			_createFDSSortItem(
				httpServletRequest, "feedback", "feedback-type", "asc", false)
		).add(
			_createFDSSortItem(
				httpServletRequest, "reason", "issue-type", "asc", false)
		).add(
			_createFDSSortItem(
				httpServletRequest, "level", "level", "asc", false)
		).add(
			_createFDSSortItem(
				httpServletRequest, "userMessage", "user-message", "asc", false)
		).build();
	}

	private FDSSortItem _createFDSSortItem(
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