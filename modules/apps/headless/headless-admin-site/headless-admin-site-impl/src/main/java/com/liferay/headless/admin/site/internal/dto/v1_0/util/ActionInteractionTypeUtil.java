/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.fragment.entry.processor.editable.element.constants.ActionEditableElementConstants;
import com.liferay.headless.admin.site.dto.v1_0.ActionInteraction;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class ActionInteractionTypeUtil {

	public static ActionInteraction.Type toExternalType(String internalType) {
		for (Map.Entry<ActionInteraction.Type, String> entry :
				_externalToInternalValuesMap.entrySet()) {

			if (Objects.equals(internalType, entry.getValue())) {
				return entry.getKey();
			}
		}

		throw new UnsupportedOperationException();
	}

	public static String toInternalType(ActionInteraction.Type externalType) {
		String internalType = _externalToInternalValuesMap.get(externalType);

		if (internalType != null) {
			return internalType;
		}

		throw new UnsupportedOperationException();
	}

	private static final Map<ActionInteraction.Type, String>
		_externalToInternalValuesMap = HashMapBuilder.put(
			ActionInteraction.Type.DISPLAY_PAGE,
			ActionEditableElementConstants.INTERACTION_DISPLAY_PAGE
		).put(
			ActionInteraction.Type.NONE,
			ActionEditableElementConstants.INTERACTION_NONE
		).put(
			ActionInteraction.Type.NOTIFICATION,
			ActionEditableElementConstants.INTERACTION_NOTIFICATION
		).put(
			ActionInteraction.Type.PAGE,
			ActionEditableElementConstants.INTERACTION_PAGE
		).put(
			ActionInteraction.Type.URL,
			ActionEditableElementConstants.INTERACTION_URL
		).build();

}