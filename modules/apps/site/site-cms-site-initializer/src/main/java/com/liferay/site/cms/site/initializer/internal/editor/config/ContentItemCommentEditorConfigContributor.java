/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.editor.config;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Víctor Galán
 */
@Component(
	property = "editor.config.key=contentItemCommentEditor",
	service = EditorConfigContributor.class
)
public class ContentItemCommentEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				StringPool.BLANK, StringPool.BLANK, "ckeditor5_balloon",
				Collections.emptyMap(), themeDisplay,
				requestBackedPortletURLFactory);

		JSONObject configJSONObject = editorConfiguration.getConfigJSONObject();

		jsonObject.put(
			"editorType", configJSONObject.getString("editorType")
		).put(
			"licenseKey", configJSONObject.getString("licenseKey")
		).put(
			"preset", "basic"
		).put(
			"removePlugins", new String[] {"Image"}
		).put(
			"toolbar",
			new String[] {
				"bold", "italic", "underline", "|", "numberedList",
				"bulletedList", "|", "link"
			}
		);
	}

}