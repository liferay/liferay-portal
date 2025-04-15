/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.sample.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
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
 * @author Daniel Sanz
 */
public class SearchPaginatorDisplayContext {

	public SearchPaginatorDisplayContext(
		Portal portal, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_portal = portal;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public SearchContainer<DropdownItem> getSearchContainer() {
		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(_renderRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(_renderResponse);

		SearchContainer<DropdownItem> searchContainer = new SearchContainer<>(
			_renderRequest,
			PortletURLUtil.getCurrent(
				liferayPortletRequest, liferayPortletResponse),
			null, "no-items-were-found");

		searchContainer.setId("DropdownItemSearchContainer");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				_renderRequest, SamplePortletKeys.SAMPLE_PORTLET,
				"order-by-col", "name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				_renderRequest, SamplePortletKeys.SAMPLE_PORTLET,
				"order-by-type", "asc"));

		List<DropdownItem> dropdownItems = new ArrayList<>();

		for (int i = 1; i < 500; i++) {
			dropdownItems.add(
				DropdownItemBuilder.setLabel(
					String.valueOf(i)
				).build());
		}

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			searchContainer.getStart(), searchContainer.getEnd(),
			dropdownItems.size());

		searchContainer.setResultsAndTotal(
			() -> dropdownItems.subList(startAndEnd[0], startAndEnd[1]),
			dropdownItems.size());

		return searchContainer;
	}

	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}