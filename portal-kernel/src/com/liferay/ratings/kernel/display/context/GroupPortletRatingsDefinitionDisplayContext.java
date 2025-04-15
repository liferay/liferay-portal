/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.kernel.display.context;

import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.ratings.kernel.RatingsType;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionValues;
import com.liferay.ratings.kernel.transformer.RatingsDataTransformerUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class GroupPortletRatingsDefinitionDisplayContext {

	public GroupPortletRatingsDefinitionDisplayContext(
		UnicodeProperties groupTypeSettingsUnicodeProperties,
		HttpServletRequest httpServletRequest) {

		_populateRatingsTypeMaps(
			groupTypeSettingsUnicodeProperties, httpServletRequest);
	}

	public Map<String, Map<String, RatingsType>> getGroupRatingsTypeMaps() {
		return Collections.unmodifiableMap(_groupRatingsTypeMaps);
	}

	private void _populateRatingsTypeMaps(
		UnicodeProperties groupTypeSettingsUnicodeProperties,
		HttpServletRequest httpServletRequest) {

		Map<String, PortletRatingsDefinitionValues>
			portletRatingsDefinitionValuesMap =
				PortletRatingsDefinitionUtil.
					getPortletRatingsDefinitionValuesMap();

		for (Map.Entry<String, PortletRatingsDefinitionValues> entry :
				portletRatingsDefinitionValuesMap.entrySet()) {

			PortletRatingsDefinitionValues portletRatingsDefinitionValues =
				entry.getValue();

			if (portletRatingsDefinitionValues == null) {
				continue;
			}

			String portletId = portletRatingsDefinitionValues.getPortletId();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (!PortletLocalServiceUtil.hasPortlet(
					themeDisplay.getCompanyId(), portletId)) {

				continue;
			}

			String className = entry.getKey();

			String groupRatingsTypeString = PropertiesParamUtil.getString(
				groupTypeSettingsUnicodeProperties, httpServletRequest,
				RatingsDataTransformerUtil.getPropertyKey(className));

			RatingsType ratingsType = null;

			if (Validator.isNotNull(groupRatingsTypeString)) {
				ratingsType = RatingsType.parse(groupRatingsTypeString);
			}

			Map<String, RatingsType> ratingsTypeMap = new HashMap<>();

			ratingsTypeMap.put(className, ratingsType);

			_groupRatingsTypeMaps.put(portletId, ratingsTypeMap);
		}
	}

	private final Map<String, Map<String, RatingsType>> _groupRatingsTypeMaps =
		new HashMap<>();

}