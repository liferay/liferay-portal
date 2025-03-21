/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.editor.configuration.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;

import jakarta.portlet.ResourceURL;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"editor.name=alloyeditor_creole", "editor.name=ckeditor_creole",
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"service.ranking:Integer=100"
	},
	service = EditorConfigContributor.class
)
public class WikiCreoleAutoCompleteEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"autocomplete",
			JSONUtil.put(
				"requestTemplate", "query={query}"
			).put(
				"trigger",
				JSONUtil.put(
					JSONUtil.put(
						"resultFilters",
						"function(query, results) {return results;}"
					).put(
						"resultTextLocator", "title"
					).put(
						"source",
						() -> {
							PortletDisplay portletDisplay =
								themeDisplay.getPortletDisplay();

							ResourceURL autoCompletePageTitleURL =
								(ResourceURL)
									requestBackedPortletURLFactory.
										createResourceURL(
											portletDisplay.getId());

							Map<String, String> fileBrowserParams =
								(Map<String, String>)
									inputEditorTaglibAttributes.get(
										"liferay-ui:input-editor:" +
											"fileBrowserParams");

							autoCompletePageTitleURL.setParameter(
								"nodeId", fileBrowserParams.get("nodeId"));

							autoCompletePageTitleURL.setResourceID(
								"/wiki/autocomplete_page_title");

							return StringBundler.concat(
								autoCompletePageTitleURL, "&",
								_portal.getPortletNamespace(
									portletDisplay.getId()));
						}
					).put(
						"term", "["
					).put(
						"tplReplace", "<a href=\"{title}\">{title}</a>"
					).put(
						"tplResults",
						"<span class=\"h5 text-truncate\">{title}</span>"
					))
			));

		String extraPlugins = jsonObject.getString("extraPlugins");

		if (Validator.isNotNull(extraPlugins)) {
			extraPlugins += ",autocomplete";
		}
		else {
			extraPlugins =
				"autocomplete,ae_placeholder,ae_selectionregion,ae_uicore";
		}

		jsonObject.put("extraPlugins", extraPlugins);
	}

	@Reference
	private Portal _portal;

}