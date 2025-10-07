/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.display.context;

import com.liferay.frontend.data.set.sample.web.internal.display.context.helper.FDSRequestHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Javier Gamarra
 * @author Javier de Arcos
 */
public class FDSSampleDisplayContext {

	public FDSSampleDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_renderResponse = renderResponse;

		_fdsRequestHelper = new FDSRequestHelper(httpServletRequest);
	}

	public String getAPIURL() {
		return "/o/c/fdssamples?sort=title:asc";
	}

	public CreationMenu getCreationMenu() throws Exception {
		return new CreationMenu();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"filtered",
			JSONUtil.put(
				"search",
				JSONUtil.put(
					"description",
					LanguageUtil.get(
						_fdsRequestHelper.getRequest(), "custom-description")
				).put(
					"image", "/states/empty_state.svg"
				).put(
					"imageReducedMotion",
					"/states/empty_state_reduced_motion.svg"
				).put(
					"title",
					LanguageUtil.get(
						_fdsRequestHelper.getRequest(), "custom-title")
				))
		).build();
	}

	private final FDSRequestHelper _fdsRequestHelper;
	private final RenderResponse _renderResponse;

}