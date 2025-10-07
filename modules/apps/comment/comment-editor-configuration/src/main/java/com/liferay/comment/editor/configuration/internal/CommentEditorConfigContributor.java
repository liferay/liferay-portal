/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.editor.configuration.internal;

import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = "editor.config.key=commentEditor",
	service = EditorConfigContributor.class
)
public class CommentEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"allowedContent", PropsValues.DISCUSSION_COMMENTS_ALLOWED_CONTENT
		).put(
			"toolbar", _jsonFactory.createJSONObject()
		);

		if (PropsValues.DISCUSSION_COMMENTS_FORMAT.equals("bbcode")) {
			String extraPlugins = jsonObject.getString("extraPlugins");

			if (Validator.isNull(extraPlugins)) {
				extraPlugins = "bbcode";
			}
			else if (!extraPlugins.contains("bbcode")) {
				extraPlugins = extraPlugins + ",bbcode";
			}

			jsonObject.put("extraPlugins", extraPlugins);
		}
	}

	@Reference
	private JSONFactory _jsonFactory;

}