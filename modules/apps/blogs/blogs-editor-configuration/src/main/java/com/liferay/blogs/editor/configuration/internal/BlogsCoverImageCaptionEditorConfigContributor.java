/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.editor.configuration.internal;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"editor.config.key=coverImageCaptionEditor",
		"editor.name=ballooneditor",
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_ADMIN
	},
	service = EditorConfigContributor.class
)
public class BlogsCoverImageCaptionEditorConfigContributor
	extends BaseEditorConfigContributor {

	public static final String DEFAULT_REMOVE_PLUGINS =
		"magicline,stylescombo,videoembed,video,image,contextmenu," +
			"tabletools,liststyle,insertbutton";

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"allowedContent", "a[*](*)"
		).put(
			"balloonEditorEnabled", true
		).put(
			"disallowedContent", "br"
		);

		String removePlugins = jsonObject.getString("removePlugins");

		if (Validator.isNotNull(removePlugins)) {
			removePlugins = removePlugins + "," + DEFAULT_REMOVE_PLUGINS;
		}
		else {
			removePlugins = DEFAULT_REMOVE_PLUGINS;
		}

		jsonObject.put(
			"removePlugins", removePlugins
		).put(
			"toolbarImage", ""
		).put(
			"toolbarTable", ""
		).put(
			"toolbarText", "TextLink"
		).put(
			"toolbarVideo", ""
		);
	}

}