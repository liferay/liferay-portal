/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.search;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonardo Barros
 */
public class FieldSetSearch extends SearchContainer<DDMStructure> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-entries-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("id");
			add("name");
			add("description");
			add("modified-date");
		}
	};

	public FieldSetSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new FieldSetDisplayTerms(portletRequest),
			new FieldSetSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		FieldSetDisplayTerms displayTerms =
			(FieldSetDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			FieldSetDisplayTerms.DESCRIPTION, displayTerms.getDescription());
		iteratorURL.setParameter(
			FieldSetDisplayTerms.NAME, String.valueOf(displayTerms.getName()));
	}

}