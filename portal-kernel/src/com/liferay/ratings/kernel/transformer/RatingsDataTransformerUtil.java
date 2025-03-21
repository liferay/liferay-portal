/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.kernel.transformer;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.ratings.kernel.RatingsType;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionValues;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

/**
 * @author Roberto Díaz
 * @author Sergio González
 */
public class RatingsDataTransformerUtil {

	public static String getPropertyKey(String className) {
		return className + "_RatingsType";
	}

	public static void transformCompanyRatingsData(
			long companyId, PortletPreferences oldPortletPreferences,
			UnicodeProperties unicodeProperties)
		throws PortalException {

		RatingsDataTransformer ratingsDataTransformer =
			_ratingsDataTransformerSnapshot.get();

		if (ratingsDataTransformer == null) {
			return;
		}

		Map<String, PortletRatingsDefinitionValues>
			portletRatingsDefinitionValuesMap =
				PortletRatingsDefinitionUtil.
					getPortletRatingsDefinitionValuesMap();

		for (Map.Entry<String, PortletRatingsDefinitionValues> entry :
				portletRatingsDefinitionValuesMap.entrySet()) {

			String className = entry.getKey();

			String propertyKey = getPropertyKey(className);

			RatingsType fromRatingsType = RatingsType.parse(
				oldPortletPreferences.getValue(propertyKey, StringPool.BLANK));

			if (fromRatingsType == null) {
				PortletRatingsDefinitionValues portletRatingsDefinitionValues =
					entry.getValue();

				fromRatingsType =
					portletRatingsDefinitionValues.getDefaultRatingsType();
			}

			RatingsType toRatingsType = RatingsType.parse(
				unicodeProperties.getProperty(propertyKey));

			_transformRatingsData(
				"companyId", companyId, className, fromRatingsType,
				toRatingsType, ratingsDataTransformer);
		}
	}

	public static void transformGroupRatingsData(
			long groupId, UnicodeProperties oldUnicodeProperties,
			UnicodeProperties unicodeProperties)
		throws PortalException {

		RatingsDataTransformer ratingsDataTransformer =
			_ratingsDataTransformerSnapshot.get();

		if (ratingsDataTransformer == null) {
			return;
		}

		Map<String, PortletRatingsDefinitionValues>
			portletRatingsDefinitionValuesMap =
				PortletRatingsDefinitionUtil.
					getPortletRatingsDefinitionValuesMap();

		for (Map.Entry<String, PortletRatingsDefinitionValues> entry :
				portletRatingsDefinitionValuesMap.entrySet()) {

			String className = entry.getKey();

			String propertyKey = getPropertyKey(className);

			RatingsType fromRatingsType = RatingsType.parse(
				oldUnicodeProperties.getProperty(propertyKey));

			if (fromRatingsType == null) {
				PortletRatingsDefinitionValues portletRatingsDefinitionValues =
					entry.getValue();

				fromRatingsType =
					portletRatingsDefinitionValues.getDefaultRatingsType();
			}

			RatingsType toRatingsType = RatingsType.parse(
				unicodeProperties.getProperty(propertyKey));

			_transformRatingsData(
				"groupId", groupId, className, fromRatingsType, toRatingsType,
				ratingsDataTransformer);
		}
	}

	private static void _transformRatingsData(
			String classPKFieldName, long classPKFieldValue, String className,
			RatingsType fromRatingsType, RatingsType toRatingsType,
			RatingsDataTransformer ratingsDataTransformer)
		throws PortalException {

		if ((toRatingsType == null) || fromRatingsType.equals(toRatingsType)) {
			return;
		}

		ActionableDynamicQuery.PerformActionMethod<RatingsEntry>
			performActionMethod = ratingsDataTransformer.transformRatingsData(
				fromRatingsType, toRatingsType);

		if (performActionMethod == null) {
			return;
		}

		ActionableDynamicQuery ratingsEntryActionableDynamicQuery =
			RatingsEntryLocalServiceUtil.getActionableDynamicQuery();

		ratingsEntryActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property property = PropertyFactoryUtil.forName(
					classPKFieldName);

				dynamicQuery.add(property.eq(classPKFieldValue));

				property = PropertyFactoryUtil.forName("className");

				dynamicQuery.add(property.eq(className));
			});
		ratingsEntryActionableDynamicQuery.setPerformActionMethod(
			performActionMethod);

		ratingsEntryActionableDynamicQuery.performActions();
	}

	private RatingsDataTransformerUtil() {
	}

	private static final Snapshot<RatingsDataTransformer>
		_ratingsDataTransformerSnapshot = new Snapshot<>(
			RatingsDataTransformerUtil.class, RatingsDataTransformer.class,
			null, true);

}