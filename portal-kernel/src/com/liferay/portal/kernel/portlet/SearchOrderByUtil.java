/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public class SearchOrderByUtil {

	public static String getOrderByCol(
		HttpServletRequest httpServletRequest, String portletName,
		String defaultValue) {

		return getOrderByCol(
			httpServletRequest, portletName, "order-by-col", defaultValue);
	}

	public static String getOrderByCol(
		HttpServletRequest httpServletRequest, String portletName, String key,
		String defaultValue) {

		String orderByCol = ParamUtil.getString(
			httpServletRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM);

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		if (Validator.isNull(orderByCol)) {
			orderByCol = portalPreferences.getValue(
				portletName, key, defaultValue);
		}
		else {
			portalPreferences.setValue(portletName, key, orderByCol);
		}

		return orderByCol;
	}

	public static String getOrderByCol(
		PortletRequest portletRequest, String portletName,
		String defaultValue) {

		return getOrderByCol(
			PortalUtil.getHttpServletRequest(portletRequest), portletName,
			defaultValue);
	}

	public static String getOrderByCol(
		PortletRequest portletRequest, String portletName, String key,
		String defaultValue) {

		return getOrderByCol(
			PortalUtil.getHttpServletRequest(portletRequest), portletName, key,
			defaultValue);
	}

	public static String getOrderByType(
		HttpServletRequest httpServletRequest, String portletName,
		String defaultValue) {

		return getOrderByType(
			httpServletRequest, portletName, "order-by-type", defaultValue);
	}

	public static String getOrderByType(
		HttpServletRequest httpServletRequest, String portletName, String key,
		String defaultValue) {

		String orderByType = ParamUtil.getString(
			httpServletRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM);

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		if (Validator.isNull(orderByType)) {
			orderByType = portalPreferences.getValue(
				portletName, key, defaultValue);
		}
		else {
			portalPreferences.setValue(portletName, key, orderByType);
		}

		return orderByType;
	}

	public static String getOrderByType(
		PortletRequest portletRequest, String portletName,
		String defaultValue) {

		return getOrderByType(
			PortalUtil.getHttpServletRequest(portletRequest), portletName,
			defaultValue);
	}

	public static String getOrderByType(
		PortletRequest portletRequest, String portletName, String key,
		String defaultValue) {

		return getOrderByType(
			PortalUtil.getHttpServletRequest(portletRequest), portletName, key,
			defaultValue);
	}

}