/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.FragmentConfigurationFieldValue;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentConfigurationFieldValueTypeUtil {

	public static FragmentConfigurationFieldValue.Type toExternalType(
		String internalType) {

		FragmentConfigurationFieldValue.Type fragmentConfigurationFieldType =
			_internalToExternalValuesMap.get(internalType);

		if (fragmentConfigurationFieldType != null) {
			return fragmentConfigurationFieldType;
		}

		throw new UnsupportedOperationException();
	}

	public static String toInternalType(
		FragmentConfigurationFieldValue.Type externalType) {

		for (Map.Entry<String, FragmentConfigurationFieldValue.Type> entry :
				_internalToExternalValuesMap.entrySet()) {

			if (Objects.equals(externalType, entry.getValue())) {
				return entry.getKey();
			}
		}

		throw new UnsupportedOperationException();
	}

	private static final Map<String, FragmentConfigurationFieldValue.Type>
		_internalToExternalValuesMap = HashMapBuilder.put(
			"categoryTreeNodeSelector",
			FragmentConfigurationFieldValue.Type.CATEGORY
		).put(
			"checkbox", FragmentConfigurationFieldValue.Type.CHECKBOX
		).put(
			"collectionSelector",
			FragmentConfigurationFieldValue.Type.COLLECTION
		).put(
			"colorPalette", FragmentConfigurationFieldValue.Type.COLOR_PALETTE
		).put(
			"colorPicker", FragmentConfigurationFieldValue.Type.COLOR_PICKER
		).put(
			"itemSelector", FragmentConfigurationFieldValue.Type.ITEM
		).put(
			"length", FragmentConfigurationFieldValue.Type.LENGTH
		).put(
			"navigationMenuSelector",
			FragmentConfigurationFieldValue.Type.NAVIGATION_MENU
		).put(
			"select", FragmentConfigurationFieldValue.Type.SELECT
		).put(
			"targetCollectionDisplay",
			FragmentConfigurationFieldValue.Type.TARGET_COLLECTION_DISPLAY
		).put(
			"text", FragmentConfigurationFieldValue.Type.TEXT
		).put(
			"url", FragmentConfigurationFieldValue.Type.URL
		).put(
			"videoSelector", FragmentConfigurationFieldValue.Type.VIDEO
		).build();

}