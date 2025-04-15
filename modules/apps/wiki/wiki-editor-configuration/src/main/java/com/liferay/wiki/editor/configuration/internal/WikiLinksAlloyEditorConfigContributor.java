/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.editor.configuration.internal;

import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.wiki.constants.WikiPortletKeys;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(
	property = {
		"editor.config.key=contentEditor", "editor.name=alloyeditor_creole",
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY
	},
	service = EditorConfigContributor.class
)
public class WikiLinksAlloyEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		JSONObject toolbarsJSONObject = jsonObject.getJSONObject("toolbars");

		if (toolbarsJSONObject == null) {
			return;
		}

		JSONObject stylesToolbarJSONObject = toolbarsJSONObject.getJSONObject(
			"styles");

		if (stylesToolbarJSONObject == null) {
			return;
		}

		JSONArray selectionsJSONArray = stylesToolbarJSONObject.getJSONArray(
			"selections");

		if (selectionsJSONArray == null) {
			return;
		}

		for (int i = 0; i < selectionsJSONArray.length(); i++) {
			JSONObject selectionJSONObject = selectionsJSONArray.getJSONObject(
				i);

			JSONArray buttonsJSONArray = selectionJSONObject.getJSONArray(
				"buttons");

			selectionJSONObject.put(
				"buttons", _updateButtonsJSONArray(buttonsJSONArray));
		}
	}

	private JSONObject _getWikiLinkButtonJSONObject(String buttonName) {
		return JSONUtil.put(
			"cfg", JSONUtil.put("appendProtocol", false)
		).put(
			"name", buttonName
		);
	}

	private JSONArray _updateButtonsJSONArray(JSONArray oldButtonsJSONArray) {
		JSONArray newButtonsJSONArray = _jsonFactory.createJSONArray();

		for (int j = 0; j < oldButtonsJSONArray.length(); j++) {
			JSONObject buttonJSONObject = oldButtonsJSONArray.getJSONObject(j);

			if (buttonJSONObject == null) {
				String buttonName = oldButtonsJSONArray.getString(j);

				if (buttonName.equals("link") ||
					buttonName.equals("linkEdit")) {

					buttonJSONObject = _getWikiLinkButtonJSONObject(buttonName);

					newButtonsJSONArray.put(buttonJSONObject);
				}
				else {
					newButtonsJSONArray.put(buttonName);
				}
			}
			else {
				String buttonName = buttonJSONObject.getString("name");

				if (buttonName.equals("link") ||
					buttonName.equals("linkEdit")) {

					JSONObject cfgJSONObject = buttonJSONObject.getJSONObject(
						"cfg");

					if (cfgJSONObject == null) {
						cfgJSONObject = _jsonFactory.createJSONObject();

						buttonJSONObject.put("cfg", cfgJSONObject);
					}

					cfgJSONObject.put("appendProtocol", false);
				}

				newButtonsJSONArray.put(buttonJSONObject);
			}
		}

		return newButtonsJSONArray;
	}

	@Reference
	private JSONFactory _jsonFactory;

}