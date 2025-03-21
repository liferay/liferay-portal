/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.editor.configuration;

import com.liferay.mentions.constants.MentionsPortletKeys;
import com.liferay.mentions.matcher.MentionsMatcherUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import java.util.Map;

/**
 * @author Sergio González
 */
public class BaseMentionsEditorConfigContributor
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
						"regExp",
						StringBundler.concat(
							"(?:\\strigger|^trigger)(",
							MentionsMatcherUtil.
								getScreenNameRegularExpression(),
							")")
					).put(
						"resultFilters",
						"function(query, results) {return results;}"
					).put(
						"resultTextLocator", "screenName"
					).put(
						"source",
						() -> {
							LiferayPortletURL portletURL =
								(LiferayPortletURL)getPortletURL(
									themeDisplay,
									requestBackedPortletURLFactory);

							portletURL.setAnchor(false);

							return StringBundler.concat(
								portletURL, "&",
								PortalUtil.getPortletNamespace(
									MentionsPortletKeys.MENTIONS));
						}
					).put(
						"term", "@"
					).put(
						"tplReplace", "{mention}"
					).put(
						"tplResults",
						StringBundler.concat(
							"<div id=\"",
							PortalUtil.getPortletNamespace(
								MentionsPortletKeys.MENTIONS),
							"mentionsResult\">",
							"<div class=\"p-1 autofit-row ",
							"autofit-row-center\"><div class=\"autofit-col ",
							"inline-item-before\">{portraitHTML}</div><div ",
							"class=\"autofit-col autofit-col-expand\">",
							"<strong class=\"text-truncate\">{fullName}",
							"</strong><div class=\"autofit-col-expand\">",
							"<small class=\"text-truncate\">@{screenName}",
							"</small></div></div></div></div>")
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

	protected PortletURL getPortletURL(
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		String discussionPortletId = themeDisplay.getPpid();

		if (Validator.isBlank(discussionPortletId)) {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			discussionPortletId = portletDisplay.getId();
		}

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createResourceURL(
				MentionsPortletKeys.MENTIONS)
		).setParameter(
			"discussionPortletId", discussionPortletId
		).buildPortletURL();
	}

}