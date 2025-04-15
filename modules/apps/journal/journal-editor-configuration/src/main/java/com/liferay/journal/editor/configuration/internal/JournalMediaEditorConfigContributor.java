/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.editor.configuration.internal;

import com.liferay.document.library.kernel.processor.AudioProcessorUtil;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Antonio Pol
 */
@Component(
	property = {
		"editor.name=alloyeditor",
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL
	},
	service = EditorConfigContributor.class
)
public class JournalMediaEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		if (!AudioProcessorUtil.isEnabled()) {
			return;
		}

		JSONObject toolbarsJSONObject = jsonObject.getJSONObject("toolbars");

		if (toolbarsJSONObject == null) {
			toolbarsJSONObject = _jsonFactory.createJSONObject();
		}

		JSONObject addJSONObject = toolbarsJSONObject.getJSONObject("add");

		if (addJSONObject == null) {
			addJSONObject = _jsonFactory.createJSONObject();
		}

		JSONArray buttonsJSONArray = _jsonFactory.createJSONArray();

		JSONArray currentButtonsJSONArray = addJSONObject.getJSONArray(
			"buttons");

		if (currentButtonsJSONArray != null) {
			currentButtonsJSONArray.forEach(
				button -> {
					if (!Objects.equals(button, "embedVideo")) {
						buttonsJSONArray.put(button);
					}
				});
		}

		buttonsJSONArray.put(
			"video"
		).put(
			"audio"
		);

		addJSONObject.put("buttons", buttonsJSONArray);

		toolbarsJSONObject.put("add", addJSONObject);

		jsonObject.put("toolbars", toolbarsJSONObject);
	}

	@Reference
	private JSONFactory _jsonFactory;

}