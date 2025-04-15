/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.editor.configuration;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fortunato Maldonado
 */
@Component(
	property = {
		"editor.config.key=rich_text", "editor.name=ckeditor_classic",
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL
	},
	service = EditorConfigContributor.class
)
public class JournalArticleContentEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"bodyClass", jsonObject.getString("bodyClass") + " min-vh-100");

		JSONArray contentsCSSJSONArray = jsonObject.getJSONArray("contentsCss");

		contentsCSSJSONArray.put(
			HtmlUtil.escape(
				_portal.getStaticResourceURL(
					themeDisplay.getRequest(),
					_portal.getPathContext() +
						"/o/journal-web/css/ckeditor.css")));

		jsonObject.put(
			"contentsCss", contentsCSSJSONArray
		).put(
			"resize_enabled", true
		);

		String removePlugins = jsonObject.getString("removePlugins");

		if (Validator.isNotNull(removePlugins)) {
			removePlugins = removePlugins + ",autogrow";
		}
		else {
			removePlugins = "autogrow";
		}

		jsonObject.put("removePlugins", removePlugins);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (Validator.isNotNull(portletDisplay.getId())) {
			jsonObject.put(
				"uploadUrl",
				PortletURLBuilder.create(
					requestBackedPortletURLFactory.createActionURL(
						JournalPortletKeys.JOURNAL)
				).setActionName(
					"/journal/upload_image"
				).buildString());
		}
	}

	@Reference
	private Portal _portal;

}