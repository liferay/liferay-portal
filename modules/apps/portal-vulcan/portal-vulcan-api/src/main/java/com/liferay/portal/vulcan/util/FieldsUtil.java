/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Daniel Raposo
 */
public class FieldsUtil {

	public static List<String> expand(String fieldName) {
		if (!fieldName.contains(".")) {
			return Collections.singletonList(fieldName);
		}

		List<String> expandedFieldNames = new ArrayList<>();

		String expandedFieldName = fieldName;

		while (!expandedFieldName.equals("")) {
			expandedFieldNames.add(expandedFieldName);

			if (expandedFieldName.contains(".")) {
				expandedFieldName = expandedFieldName.substring(
					0, expandedFieldName.lastIndexOf("."));
			}
			else {
				expandedFieldName = "";
			}
		}

		return expandedFieldNames;
	}

}