/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.sample.web.internal.editor.configuration;

import com.liferay.frontend.editor.ckeditor.sample.web.internal.constants.CKEditorSamplePortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marko Cikos
 */
@Component(
	property = {
		"editor.config.key=sampleClassicEditor",
		"jakarta.portlet.name=" + CKEditorSamplePortletKeys.CKEDITOR_SAMPLE
	},
	service = EditorConfigContributor.class
)
public class ClassicEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"extraPlugins", "itemselector, maximize, stylescombo"
		).put(
			"toolbar_liferay",
			JSONUtil.putAll(
				toJSONArray("['Undo', 'Redo']"),
				toJSONArray("['Styles', 'Bold', 'Italic', 'Underline']"),
				toJSONArray("['NumberedList', 'BulletedList']"),
				toJSONArray("['Maximize']"), toJSONArray("['Link', Unlink]"),
				toJSONArray("['Table', 'ImageSelector']"))
		);
	}

}