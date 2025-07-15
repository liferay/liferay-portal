/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Alicia García
 */
public class ViewSharedWithMeSectionDisplayContext {

	public ViewSharedWithMeSectionDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public String getAPIURL() {
		return "/o/headless-admin-user/v1.0/my-user-account/shared-assets" +
			"/shared-with-me";
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest,
				"items-shared-with-you-by-other-users-will-appear-here")
		).put(
			"image", "/states/empty-state-shared-with-me.svg"
		).put(
			"title",
			LanguageUtil.get(
				_httpServletRequest, "no-items-shared-with-you-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"link/to/view/item", "pencil", "actionLink", "view", "get",
				"view", null));
	}

	private final HttpServletRequest _httpServletRequest;

}