/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.ContextualMenuNavigationMenuValue;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class ContextualMenuTypeUtil {

	public static ContextualMenuNavigationMenuValue.ContextualMenuType
		toExternalType(String internalType) {

		if (_internalToExternalValuesMap.containsKey(internalType)) {
			return _internalToExternalValuesMap.get(internalType);
		}

		throw new UnsupportedOperationException();
	}

	public static String toInternalType(
		ContextualMenuNavigationMenuValue.ContextualMenuType externalType) {

		for (Map.Entry
				<String, ContextualMenuNavigationMenuValue.ContextualMenuType>
					entry : _internalToExternalValuesMap.entrySet()) {

			if (Objects.equals(externalType, entry.getValue())) {
				return entry.getKey();
			}
		}

		throw new UnsupportedOperationException();
	}

	private static final Map
		<String, ContextualMenuNavigationMenuValue.ContextualMenuType>
			_internalToExternalValuesMap = HashMapBuilder.put(
				"children",
				ContextualMenuNavigationMenuValue.ContextualMenuType.CHILDREN
			).put(
				"parent-and-its-children",
				ContextualMenuNavigationMenuValue.ContextualMenuType.
					PARENT_AND_ITS_SIBLINGS
			).put(
				"self-and-siblings",
				ContextualMenuNavigationMenuValue.ContextualMenuType.
					SELF_AND_SIBLINGS
			).build();

}