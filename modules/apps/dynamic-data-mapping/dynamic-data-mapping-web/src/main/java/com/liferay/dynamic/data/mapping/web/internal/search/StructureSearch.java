/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.search;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

/**
 * @author Eduardo Lundgren
 */
public class StructureSearch extends SearchContainer<DDMStructure> {

	public static final String EMPTY_RESULTS_MESSAGE = "there-are-no-results";

	public StructureSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new StructureDisplayTerms(portletRequest),
			new StructureSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, null, EMPTY_RESULTS_MESSAGE);

		StructureDisplayTerms displayTerms =
			(StructureDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			StructureDisplayTerms.DESCRIPTION, displayTerms.getDescription());
		iteratorURL.setParameter(
			StructureDisplayTerms.NAME, displayTerms.getName());
		iteratorURL.setParameter(
			StructureDisplayTerms.SEARCH_RESTRICTION,
			String.valueOf(displayTerms.isSearchRestriction()));
		iteratorURL.setParameter(
			StructureDisplayTerms.STORAGE_TYPE, displayTerms.getStorageType());
	}

	public StructureSearch(
		PortletRequest portletRequest, PortletURL iteratorURL, int status) {

		this(portletRequest, iteratorURL);

		StructureSearchTerms searchTerms =
			(StructureSearchTerms)getSearchTerms();

		searchTerms.setStatus(status);
	}

}