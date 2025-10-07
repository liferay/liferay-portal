/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.editor.config;

import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marko Čikoš
 */
@Component(
	property = "editor.config.key=richTextFragmentCKEditor5",
	service = EditorConfigContributor.class
)
public class RichTextFragmentCKEditor5EditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (!scopeGroup.isCMS()) {
			return;
		}

		jsonObject.put(
			"toolbar",
			JSONUtil.put(
				"items",
				new String[] {
					"undo", "redo", "|", "style", "|", "heading", "|", "bold",
					"italic", "underline", "strikethrough", "|", "fontColor",
					"fontBackgroundColor", "|", "removeFormat", "|",
					"numberedList", "bulletedList", "|", "indent", "outdent",
					"|", "blockQuote", "|", "link", "insertTable",
					"headlessImageSelector", "headlessVideoSelector", "|",
					"alignment", "|", "aiCreator", "|", "sourceEditing"
				}
			).put(
				"shouldNotGroupWhenFull", true
			));
	}

}