/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.editor.configuration.internal;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"editor.config.key=contentEditor", "editor.name=ckeditor",
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SEARCH,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SECTION
	},
	service = EditorConfigContributor.class
)
public class KBContentCKEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		String extraPlugins = jsonObject.getString("extraPlugins");

		if (Validator.isNotNull(extraPlugins)) {
			extraPlugins = extraPlugins + ",videoembed";
		}
		else {
			extraPlugins = "videoembed";
		}

		jsonObject.put(
			"extraPlugins", extraPlugins
		).put(
			"toolbar", "kb"
		).put(
			"toolbar_kb", _getToolbarKBJSONArray(inputEditorTaglibAttributes)
		);
	}

	private JSONArray _getToolbarKBJSONArray(
		Map<String, Object> inputEditorTaglibAttributes) {

		return JSONUtil.putAll(
			super.toJSONArray("['Bold', 'Italic', 'Underline']"),
			super.toJSONArray(
				"['JustifyLeft', 'JustifyCenter', 'JustifyRight']"),
			super.toJSONArray("['NumberedList', 'BulletedList']"),
			super.toJSONArray("['Styles']"),
			super.toJSONArray("['Link', 'Unlink']"),
			super.toJSONArray(
				"['Table', 'ImageSelector', 'VideoSelector', 'HorizontalRule']")
		).put(
			() -> {
				if (_isShowSource(inputEditorTaglibAttributes)) {
					return toJSONArray("['Source']");
				}

				return null;
			}
		).put(
			toJSONArray("['A11YBtn']")
		);
	}

	private boolean _isShowSource(
		Map<String, Object> inputEditorTaglibAttributes) {

		return GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:showSource"));
	}

}