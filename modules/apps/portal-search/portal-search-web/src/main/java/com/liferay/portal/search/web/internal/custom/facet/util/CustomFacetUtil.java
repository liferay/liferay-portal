/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.facet.util;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortletPreferences;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Petteri Karttunen
 */
public class CustomFacetUtil {

	public static HttpServletRequest getHttpServletRequest(
		RenderRequest renderRequest) {

		LiferayPortletRequest liferayPortletRequest =
			PortalUtil.getLiferayPortletRequest(renderRequest);

		return liferayPortletRequest.getHttpServletRequest();
	}

	public static String getParameterName(
		CustomFacetPortletPreferences customFacetPortletPreferences) {

		String parameterName = customFacetPortletPreferences.getParameterName();

		if (Validator.isNotNull(parameterName)) {
			return parameterName;
		}

		String aggregationField =
			customFacetPortletPreferences.getAggregationField();

		if (Validator.isNotNull(aggregationField)) {
			return aggregationField;
		}

		return "customfield";
	}

	public static boolean isRangeAggregation(String aggregationType) {
		if (!Validator.isBlank(aggregationType) &&
			(aggregationType.equals("dateRange") ||
			 aggregationType.equals("range"))) {

			return true;
		}

		return false;
	}

}