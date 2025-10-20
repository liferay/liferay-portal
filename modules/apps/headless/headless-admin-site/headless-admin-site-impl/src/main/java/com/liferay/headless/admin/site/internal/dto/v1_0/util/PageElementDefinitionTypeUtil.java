/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Lourdes Fernández Besada
 */
public class PageElementDefinitionTypeUtil {

	public static PageElementDefinition.Type toExternalType(
		String internalType) {

		Set<PageElementDefinition.Type> externalTypes =
			_externalToInternalValuesMap.keySet();

		for (PageElementDefinition.Type externalType : externalTypes) {
			if (Objects.equals(
					internalType,
					_externalToInternalValuesMap.get(externalType))) {

				return externalType;
			}
		}

		throw new UnsupportedOperationException();
	}

	public static String toInternalType(
		PageElementDefinition.Type externalType) {

		if (_externalToInternalValuesMap.containsKey(externalType)) {
			return _externalToInternalValuesMap.get(externalType);
		}

		throw new UnsupportedOperationException();
	}

	private static final Map<PageElementDefinition.Type, String>
		_externalToInternalValuesMap = HashMapBuilder.put(
			PageElementDefinition.Type.COLLECTION,
			LayoutDataItemTypeConstants.TYPE_COLLECTION
		).put(
			PageElementDefinition.Type.COLLECTION_ITEM,
			LayoutDataItemTypeConstants.TYPE_COLLECTION_ITEM
		).put(
			PageElementDefinition.Type.CONTAINER,
			LayoutDataItemTypeConstants.TYPE_CONTAINER
		).put(
			PageElementDefinition.Type.DROP_ZONE,
			LayoutDataItemTypeConstants.TYPE_DROP_ZONE
		).put(
			PageElementDefinition.Type.FORM,
			LayoutDataItemTypeConstants.TYPE_FORM
		).put(
			PageElementDefinition.Type.FORM_STEP,
			LayoutDataItemTypeConstants.TYPE_FORM_STEP
		).put(
			PageElementDefinition.Type.FORM_STEP_CONTAINER,
			LayoutDataItemTypeConstants.TYPE_FORM_STEP_CONTAINER
		).put(
			PageElementDefinition.Type.FRAGMENT,
			LayoutDataItemTypeConstants.TYPE_FRAGMENT
		).put(
			PageElementDefinition.Type.FRAGMENT_DROP_ZONE,
			LayoutDataItemTypeConstants.TYPE_FRAGMENT_DROP_ZONE
		).put(
			PageElementDefinition.Type.GRID,
			LayoutDataItemTypeConstants.TYPE_ROW
		).put(
			PageElementDefinition.Type.MODULE,
			LayoutDataItemTypeConstants.TYPE_COLUMN
		).build();

}