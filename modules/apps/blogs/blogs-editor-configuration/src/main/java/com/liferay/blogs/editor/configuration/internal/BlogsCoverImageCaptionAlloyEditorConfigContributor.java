/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.editor.configuration.internal;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"editor.config.key=coverImageCaptionEditor", "editor.name=alloyeditor",
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_ADMIN
	},
	service = EditorConfigContributor.class
)
public class BlogsCoverImageCaptionAlloyEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"extraPlugins", "ae_placeholder,ae_selectionregion,ae_uicore"
		).put(
			"toolbars", _getToolbarsJSONObject()
		);
	}

	private JSONObject _getToolbarsJSONObject() {
		return JSONUtil.put("styles", _getToolbarsStylesJSONObject());
	}

	private JSONObject _getToolbarsStylesJSONObject() {
		return JSONUtil.put(
			"selections", _getToolbarStylesSelectionsJSONArray()
		).put(
			"tabIndex", 1
		);
	}

	private JSONArray _getToolbarStylesSelectionsJSONArray() {
		return JSONUtil.putAll(
			_getToolbarStylesSelectionsLinkJSONObject(),
			_getToolbarStylesSelectionsTextJSONObject());
	}

	private JSONObject _getToolbarStylesSelectionsLinkJSONObject() {
		return JSONUtil.put(
			"buttons", toJSONArray("['linkEdit']")
		).put(
			"name", "link"
		).put(
			"test", "AlloyEditor.SelectionTest.link"
		);
	}

	private JSONObject _getToolbarStylesSelectionsTextJSONObject() {
		return JSONUtil.put(
			"buttons", toJSONArray("['link']")
		).put(
			"name", "text"
		).put(
			"test", "AlloyEditor.SelectionTest.text"
		);
	}

}