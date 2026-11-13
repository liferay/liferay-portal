/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.dao.search;

import com.liferay.depot.search.DepotEntrySearch;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

/**
 * @author Roberto Díaz
 */
public class GroupSearchContainerFactory {

	public static GroupSearch create(
			DepotEntryService depotEntryService,
			GroupItemSelectorCriterion groupItemSelectorCriterion,
			Language language, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		DepotEntrySearch depotEntrySearch =
			DepotEntrySearchContainerFactory.create(
				depotEntryService, groupItemSelectorCriterion, language,
				portletRequest, portletResponse, portletURL);

		GroupSearch groupSearch = new GroupSearch(portletRequest, portletURL);

		groupSearch.setEmptyResultsMessage(
			depotEntrySearch.getEmptyResultsMessage());
		groupSearch.setResultsAndTotal(
			() -> TransformUtil.transform(
				depotEntrySearch.getResults(),
				depotEntry -> depotEntry.getGroup()),
			depotEntrySearch.getTotal());

		return groupSearch;
	}

}