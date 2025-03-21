/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.web.internal.search;

import com.liferay.password.policies.admin.constants.PasswordPoliciesAdminPortletKeys;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.comparator.PasswordPolicyDescriptionComparator;
import com.liferay.portal.kernel.util.comparator.PasswordPolicyNameComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Scott Lee
 */
public class PasswordPolicySearch extends SearchContainer<PasswordPolicy> {

	public static final String EMPTY_RESULTS_MESSAGE =
		"no-password-policies-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("name");
			add("description");
		}
	};
	public static Map<String, String> orderableHeaders = HashMapBuilder.put(
		"description", "description"
	).put(
		"name", "name"
	).build();

	public PasswordPolicySearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new PasswordPolicyDisplayTerms(portletRequest),
			new PasswordPolicyDisplayTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		PasswordPolicyDisplayTerms displayTerms =
			(PasswordPolicyDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			PasswordPolicyDisplayTerms.NAME, displayTerms.getName());

		try {
			setOrderableHeaders(orderableHeaders);

			String orderByCol = SearchOrderByUtil.getOrderByCol(
				portletRequest,
				PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN,
				"password-policies-order-by-col", "name");

			setOrderByCol(orderByCol);

			String orderByType = SearchOrderByUtil.getOrderByType(
				portletRequest,
				PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN,
				"password-policies-order-by-type", "asc");

			setOrderByComparator(
				_getOrderByComparator(orderByCol, orderByType));
			setOrderByType(orderByType);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize password policy search", exception);
		}
	}

	private OrderByComparator<PasswordPolicy> _getOrderByComparator(
		String orderByCol, String orderByType) {

		OrderByComparator<PasswordPolicy> orderByComparator = null;

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		if (orderByCol.equals("name")) {
			orderByComparator = PasswordPolicyNameComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("description")) {
			orderByComparator = PasswordPolicyDescriptionComparator.getInstance(
				orderByAsc);
		}
		else {
			orderByComparator = PasswordPolicyNameComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordPolicySearch.class);

}