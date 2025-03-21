/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.sample.web.internal.display.context;

import com.liferay.frontend.taglib.sample.web.internal.constants.SamplePortletKeys;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Ortega
 */
public class SearchIteratorDisplayContext {

	public SearchIteratorDisplayContext(
		Portal portal, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_portal = portal;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public SearchContainer<String> getSearchContainer() {
		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(_renderRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(_renderResponse);

		SearchContainer<String> searchContainer = new SearchContainer<>(
			_renderRequest,
			PortletURLUtil.getCurrent(
				liferayPortletRequest, liferayPortletResponse),
			null, "no-items-were-found");

		searchContainer.setId("stringItemSearchContainer");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				_renderRequest, SamplePortletKeys.SAMPLE_PORTLET,
				"order-by-col", "name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				_renderRequest, SamplePortletKeys.SAMPLE_PORTLET,
				"order-by-type", "asc"));

		List<String> results = new ArrayList<>();

		for (int i = 1; i < 500; i++) {
			results.add(String.valueOf(i));
		}

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			searchContainer.getStart(), searchContainer.getEnd(),
			results.size());

		searchContainer.setResultsAndTotal(
			() -> results.subList(startAndEnd[0], startAndEnd[1]),
			results.size());

		return searchContainer;
	}

	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}