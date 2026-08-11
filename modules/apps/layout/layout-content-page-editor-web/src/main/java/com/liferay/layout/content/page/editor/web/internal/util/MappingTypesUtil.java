/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class MappingTypesUtil {

	public static JSONObject getMappingTypeJSONObject(
		String className, InfoItemServiceRegistry infoItemServiceRegistry,
		String itemCapabilityKey, ThemeDisplay themeDisplay) {

		for (InfoItemClassDetails infoItemClassDetails :
				infoItemServiceRegistry.getInfoItemClassDetails(
					itemCapabilityKey)) {

			if (Objects.equals(
					infoItemClassDetails.getClassName(), className)) {

				return _getMappingTypeJSONObject(
					infoItemClassDetails, infoItemServiceRegistry,
					themeDisplay);
			}
		}

		return null;
	}

	public static JSONArray getMappingTypesJSONArray(
		InfoItemServiceRegistry infoItemServiceRegistry,
		String itemCapabilityKey, ThemeDisplay themeDisplay) {

		JSONArray mappingTypesJSONArray = JSONFactoryUtil.createJSONArray();

		Group scopeGroup = themeDisplay.getScopeGroup();

		for (InfoItemClassDetails infoItemClassDetails :
				infoItemServiceRegistry.getInfoItemClassDetails(
					itemCapabilityKey)) {

			if (!scopeGroup.isCMS() &&
				_isCMS(
					infoItemClassDetails.getClassName(),
					themeDisplay.getCompanyId())) {

				continue;
			}

			mappingTypesJSONArray.put(
				_getMappingTypeJSONObject(
					infoItemClassDetails, infoItemServiceRegistry,
					themeDisplay));
		}

		return mappingTypesJSONArray;
	}

	public static boolean hasInfoItemCapability(
		String className, InfoItemServiceRegistry infoItemServiceRegistry,
		String itemCapabilityKey) {

		for (InfoItemClassDetails infoItemClassDetails :
				infoItemServiceRegistry.getInfoItemClassDetails(
					itemCapabilityKey)) {

			if (Objects.equals(
					infoItemClassDetails.getClassName(), className)) {

				return true;
			}
		}

		return false;
	}

	private static JSONArray _getMappingFormVariationsJSONArray(
		InfoItemClassDetails infoItemClassDetails,
		InfoItemServiceRegistry infoItemServiceRegistry,
		ThemeDisplay themeDisplay) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				infoItemClassDetails.getClassName());

		if (infoItemFormVariationsProvider == null) {
			return jsonArray;
		}

		Collection<InfoItemFormVariation> infoItemFormVariations =
			infoItemFormVariationsProvider.getInfoItemFormVariations(
				themeDisplay.getScopeGroupId());

		InfoPermissionProvider infoPermissionProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoPermissionProvider.class,
				infoItemClassDetails.getClassName());

		for (InfoItemFormVariation infoItemFormVariation :
				infoItemFormVariations) {

			jsonArray.put(
				JSONUtil.put(
					"isRestricted",
					() -> {
						if ((infoPermissionProvider == null) ||
							infoPermissionProvider.hasViewPermission(
								infoItemFormVariation.getKey(),
								themeDisplay.getScopeGroupId(),
								themeDisplay.getPermissionChecker())) {

							return false;
						}

						return true;
					}
				).put(
					"label",
					() -> {
						InfoLocalizedValue<String> labelInfoLocalizedValue =
							infoItemFormVariation.getLabelInfoLocalizedValue();

						return labelInfoLocalizedValue.getValue(
							themeDisplay.getLocale());
					}
				).put(
					"value", String.valueOf(infoItemFormVariation.getKey())
				));
		}

		return jsonArray;
	}

	private static JSONObject _getMappingTypeJSONObject(
		InfoItemClassDetails infoItemClassDetails,
		InfoItemServiceRegistry infoItemServiceRegistry,
		ThemeDisplay themeDisplay) {

		return JSONUtil.put(
			"className", infoItemClassDetails.getClassName()
		).put(
			"isRestricted",
			() -> {
				InfoPermissionProvider infoPermissionProvider =
					infoItemServiceRegistry.getFirstInfoItemService(
						InfoPermissionProvider.class,
						infoItemClassDetails.getClassName());

				if ((infoPermissionProvider == null) ||
					infoPermissionProvider.hasViewPermission(
						null, themeDisplay.getScopeGroupId(),
						themeDisplay.getPermissionChecker())) {

					return false;
				}

				return true;
			}
		).put(
			"label", infoItemClassDetails.getLabel(themeDisplay.getLocale())
		).put(
			"subtypes",
			_getMappingFormVariationsJSONArray(
				infoItemClassDetails, infoItemServiceRegistry, themeDisplay)
		).put(
			"value",
			String.valueOf(
				PortalUtil.getClassNameId(infoItemClassDetails.getClassName()))
		);
	}

	private static boolean _isCMS(String className, long companyId) {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinitionByClassName(
				companyId, className);

		if (objectDefinition == null) {
			return false;
		}

		return objectDefinition.isCMS();
	}

}