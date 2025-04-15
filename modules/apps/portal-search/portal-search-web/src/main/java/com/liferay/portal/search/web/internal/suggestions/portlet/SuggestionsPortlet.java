/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.suggestions.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.suggestions.constants.SuggestionsPortletKeys;
import com.liferay.portal.search.web.internal.suggestions.display.context.SuggestionsPortletDisplayContext;
import com.liferay.portal.search.web.internal.suggestions.display.context.builder.SuggestionsPortletDisplayContextBuilder;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-suggestions",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Suggestions",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/suggestions/view.jsp",
		"jakarta.portlet.name=" + SuggestionsPortletKeys.SUGGESTIONS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SuggestionsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		SuggestionsPortletPreferences suggestionsPortletPreferences =
			new SuggestionsPortletPreferencesImpl(
				renderRequest.getPreferences());

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_buildDisplayContext(
				suggestionsPortletPreferences, portletSharedSearchResponse,
				renderRequest);

		if (!suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions() &&
			!suggestionsPortletDisplayContext.hasSpellCheckSuggestion()) {

			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, suggestionsPortletDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private SuggestionsPortletDisplayContext _buildDisplayContext(
		SuggestionsPortletPreferences suggestionsPortletPreferences,
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		SuggestionsPortletDisplayContextBuilder
			suggestionsPortletDisplayContextBuilder =
				new SuggestionsPortletDisplayContextBuilder();

		String keywords = portletSharedSearchResponse.getKeywords();

		if (keywords != null) {
			suggestionsPortletDisplayContextBuilder.setKeywords(keywords);
		}

		SearchSettings searchSettings =
			portletSharedSearchResponse.getSearchSettings();

		String keywordsParameterName =
			searchSettings.getKeywordsParameterName();

		if (keywordsParameterName != null) {
			suggestionsPortletDisplayContextBuilder.setKeywordsParameterName(
				keywordsParameterName);
		}

		suggestionsPortletDisplayContextBuilder.setRelatedQueriesSuggestions(
			portletSharedSearchResponse.getRelatedQueriesSuggestions());
		suggestionsPortletDisplayContextBuilder.
			setRelatedQueriesSuggestionsEnabled(
				suggestionsPortletPreferences.
					isRelatedQueriesSuggestionsEnabled());
		suggestionsPortletDisplayContextBuilder.setSearchURL(
			portal.getCurrentURL(renderRequest));

		String spellCheckSuggestion =
			portletSharedSearchResponse.getSpellCheckSuggestion();

		if (spellCheckSuggestion != null) {
			suggestionsPortletDisplayContextBuilder.setSpellCheckSuggestion(
				spellCheckSuggestion);
		}

		suggestionsPortletDisplayContextBuilder.setSpellCheckSuggestionEnabled(
			suggestionsPortletPreferences.isSpellCheckSuggestionEnabled());

		return suggestionsPortletDisplayContextBuilder.build();
	}

}