/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

/**
 * @author Tancredi Covioli
 */
public class ProductStickerDisplayContext {

	public ProductStickerDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public Map<String, Object> getProps() {
		Object infoItem = _httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (!(infoItem instanceof ObjectEntry)) {
			return Collections.emptyMap();
		}

		ObjectEntry objectEntry = (ObjectEntry)infoItem;

		return HashMapBuilder.<String, Object>put(
			"objectEntryId", String.valueOf(objectEntry.getObjectEntryId())
		).put(
			"restContextPath",
			() -> {
				ObjectDefinition objectDefinition =
					objectEntry.getObjectDefinition();

				return "/o" + objectDefinition.getRESTContextPath();
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;

}