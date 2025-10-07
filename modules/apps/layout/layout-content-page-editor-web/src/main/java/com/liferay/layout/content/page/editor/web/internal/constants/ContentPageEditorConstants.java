/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.constants;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Map;

/**
 * @author Pavel Savinov
 */
public class ContentPageEditorConstants {

	public static final String TYPE_COMPOSITION = "composition";

	public static final Map<String, List<Map<String, Object>>>
		layoutElementMapsListMap =
			LinkedHashMapBuilder.<String, List<Map<String, Object>>>put(
				"layout-elements",
				() -> ListUtil.fromArray(
					HashMapBuilder.<String, Object>put(
						"fragmentEntryKey", "container"
					).put(
						"icon", "container"
					).put(
						"itemType", "container"
					).put(
						"languageKey", "container"
					).build(),
					HashMapBuilder.<String, Object>put(
						"fragmentEntryKey", "row"
					).put(
						"icon", "table"
					).put(
						"itemType", "row"
					).put(
						"languageKey", "grid"
					).build())
			).put(
				"INPUTS",
				ListUtil.fromArray(
					HashMapBuilder.<String, Object>put(
						"fragmentEntryKey", "form"
					).put(
						"icon", "forms"
					).put(
						"itemType", "form"
					).put(
						"languageKey", "form-container"
					).build())
			).put(
				"content-display",
				ListUtil.fromArray(
					HashMapBuilder.<String, Object>put(
						"fragmentEntryKey", "collection-display"
					).put(
						"icon", "list"
					).put(
						"itemType", "collection"
					).put(
						"languageKey", "collection-display"
					).build())
			).build();

}