/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.GroupDescriptiveNameComparator;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.portal.kernel.util.comparator.GroupTypeComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class GroupSearch extends SearchContainer<Group> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-sites-were-found";

	public GroupSearch(PortletRequest portletRequest, PortletURL iteratorURL) {
		super(portletRequest, iteratorURL, null, EMPTY_RESULTS_MESSAGE);

		try {
			String portletId = PortletProviderUtil.getPortletId(
				User.class.getName(), PortletProvider.Action.VIEW);

			String orderByCol = SearchOrderByUtil.getOrderByCol(
				portletRequest, portletId, "groups-order-by-col", "name");

			setOrderByCol(orderByCol);

			String orderByType = SearchOrderByUtil.getOrderByType(
				portletRequest, portletId, "groups-order-by-type", "asc");

			Locale locale = LocaleUtil.getDefault();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (themeDisplay != null) {
				locale = themeDisplay.getLocale();
			}

			setOrderByComparator(
				_getGroupOrderByComparator(orderByCol, orderByType, locale));
			setOrderByType(orderByType);
		}
		catch (Exception exception) {
			_log.error("Unable to initialize group search", exception);
		}
	}

	private OrderByComparator<Group> _getGroupOrderByComparator(
		String orderByCol, String orderByType, Locale locale) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		if (orderByCol.equals("descriptive-name")) {
			return new GroupDescriptiveNameComparator(orderByAsc, locale);
		}
		else if (orderByCol.equals("name")) {
			return new GroupNameComparator(orderByAsc, locale);
		}
		else if (orderByCol.equals("type")) {
			return GroupTypeComparator.getInstance(orderByAsc);
		}

		return new GroupNameComparator(orderByAsc, locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(GroupSearch.class);

}