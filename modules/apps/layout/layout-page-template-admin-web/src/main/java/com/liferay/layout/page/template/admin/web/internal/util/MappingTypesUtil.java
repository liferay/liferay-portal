/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.util;

import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.layout.page.template.info.item.capability.DisplayPageInfoItemCapability;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Locale;

/**
 * @author Javier Moral
 */
public class MappingTypesUtil {

	public static JSONArray getMappingTypesJSONArray(
		long groupId, InfoItemServiceRegistry infoItemServiceRegistry,
		Locale locale, PermissionChecker permissionChecker) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (InfoItemClassDetails infoItemClassDetails :
				infoItemServiceRegistry.getInfoItemClassDetails(
					groupId, DisplayPageInfoItemCapability.KEY,
					permissionChecker)) {

			jsonArray.put(
				JSONUtil.put(
					"id",
					String.valueOf(
						PortalUtil.getClassNameId(
							infoItemClassDetails.getClassName()))
				).put(
					"label", infoItemClassDetails.getLabel(locale)
				).put(
					"subtypes",
					_getMappingFormVariationsJSONArray(
						groupId, infoItemClassDetails, infoItemServiceRegistry,
						locale, permissionChecker)
				));
		}

		return jsonArray;
	}

	private static JSONArray _getMappingFormVariationsJSONArray(
		long groupId, InfoItemClassDetails infoItemClassDetails,
		InfoItemServiceRegistry infoItemServiceRegistry, Locale locale,
		PermissionChecker permissionChecker) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				infoItemClassDetails.getClassName());

		if (infoItemFormVariationsProvider == null) {
			return jsonArray;
		}

		InfoPermissionProvider infoPermissionProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoPermissionProvider.class,
				infoItemClassDetails.getClassName());

		for (InfoItemFormVariation infoItemFormVariation :
				infoItemFormVariationsProvider.getInfoItemFormVariations(
					groupId)) {

			if ((infoPermissionProvider != null) &&
				!infoPermissionProvider.hasViewPermission(
					infoItemFormVariation.getKey(), groupId,
					permissionChecker)) {

				continue;
			}

			jsonArray.put(
				JSONUtil.put(
					"id", infoItemFormVariation.getKey()
				).put(
					"label",
					() -> {
						InfoLocalizedValue<String> labelInfoLocalizedValue =
							infoItemFormVariation.getLabelInfoLocalizedValue();

						return labelInfoLocalizedValue.getValue(locale);
					}
				));
		}

		return jsonArray;
	}

}