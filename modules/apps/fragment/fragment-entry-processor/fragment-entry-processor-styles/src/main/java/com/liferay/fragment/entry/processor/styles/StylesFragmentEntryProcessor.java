/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.styles;

import com.liferay.fragment.processor.FragmentEntryProcessor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Víctor Galán
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=7",
	service = FragmentEntryProcessor.class
)
public class StylesFragmentEntryProcessor implements FragmentEntryProcessor {

	@Override
	public JSONArray getDataAttributesJSONArray() {
		return JSONUtil.put("lfr-styles");
	}

	@Override
	public JSONObject getDefaultEditableValuesJSONObject(
		String html, JSONObject configurationJSONObject) {

		return JSONUtil.put(
			"hasCommonStyles",
			() -> {
				if (!html.contains("data-lfr-styles")) {
					return null;
				}

				return true;
			});
	}

}