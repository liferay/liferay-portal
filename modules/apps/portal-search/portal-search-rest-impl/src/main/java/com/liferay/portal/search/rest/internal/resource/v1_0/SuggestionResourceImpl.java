/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.resource.v1_0;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.rest.configuration.SearchSuggestionsCompanyConfiguration;
import com.liferay.portal.search.rest.dto.v1_0.Suggestion;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorConfiguration;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorResults;
import com.liferay.portal.search.rest.internal.util.ScopeUtil;
import com.liferay.portal.search.rest.resource.v1_0.SuggestionResource;
import com.liferay.portal.search.suggestions.SuggestionsRetriever;
import com.liferay.portal.search.web.constants.SearchBarPortletKeys;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portlet.RenderRequestFactory;
import com.liferay.portlet.RenderResponseFactory;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import jakarta.servlet.ServletContext;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "com.liferay.portal.search.rest.configuration.SearchSuggestionsCompanyConfiguration",
	properties = "OSGI-INF/liferay/rest/v1_0/suggestion.properties",
	scope = ServiceScope.PROTOTYPE, service = SuggestionResource.class
)
public class SuggestionResourceImpl extends BaseSuggestionResourceImpl {

	@Override
	public Page<SuggestionsContributorResults> postSuggestionsPage(
			String currentURL, String destinationFriendlyURL, Long groupId,
			String keywordsParameterName, Long plid, String scope,
			String search,
			SuggestionsContributorConfiguration[]
				suggestionsContributorConfigurations)
		throws Exception {

		if (!_searchSuggestionsCompanyConfiguration.
				enableSuggestionsEndpoint() ||
			(suggestionsContributorConfigurations == null)) {

			return Page.of(Collections.emptyList());
		}

		Layout layout = _layoutLocalService.getLayout(plid);

		ThemeDisplay themeDisplay = _createThemeDisplay(currentURL, layout);

		LiferayRenderRequest liferayRenderRequest = _createLiferayRenderRequest(
			layout, themeDisplay);

		if (!StringUtil.startsWith(destinationFriendlyURL, CharPool.SLASH)) {
			destinationFriendlyURL = StringPool.SLASH.concat(
				destinationFriendlyURL);
		}

		return Page.of(
			transform(
				_suggestionsRetriever.getSuggestionsContributorResults(
					liferayRenderRequest,
					RenderResponseFactory.create(
						contextHttpServletResponse, liferayRenderRequest),
					_createSearchContext(
						destinationFriendlyURL, groupId, keywordsParameterName,
						scope, search, suggestionsContributorConfigurations,
						themeDisplay)),
				suggestionsContributorResult ->
					new SuggestionsContributorResults() {
						{
							setAttributes(
								() ->
									suggestionsContributorResult.
										getAttributes());
							setDisplayGroupName(
								() -> _language.get(
									contextAcceptLanguage.getPreferredLocale(),
									suggestionsContributorResult.
										getDisplayGroupName()));
							setSuggestions(
								() -> (Suggestion[])transformToArray(
									suggestionsContributorResult.
										getSuggestions(),
									suggestion -> new Suggestion() {
										{
											setAttributes(
												suggestion::getAttributes);
											setScore(suggestion::getScore);
											setText(suggestion::getText);
										}
									},
									Suggestion.class));
						}
					}));
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_searchSuggestionsCompanyConfiguration =
			ConfigurableUtil.createConfigurable(
				SearchSuggestionsCompanyConfiguration.class, properties);
	}

	private LiferayRenderRequest _createLiferayRenderRequest(
			Layout layout, ThemeDisplay themeDisplay)
		throws Exception {

		contextHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Portlet portlet = _portletLocalService.getPortletById(
			SearchBarPortletKeys.SEARCH_BAR);
		ServletContext servletContext =
			(ServletContext)contextHttpServletRequest.getAttribute(WebKeys.CTX);

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		LiferayRenderRequest liferayRenderRequest = RenderRequestFactory.create(
			contextHttpServletRequest, portlet,
			PortletInstanceFactoryUtil.create(portlet, servletContext),
			portletConfig.getPortletContext(), WindowState.NORMAL,
			PortletMode.VIEW, null, layout.getPlid());

		liferayRenderRequest.setPortletRequestDispatcherRequest(
			contextHttpServletRequest);

		liferayRenderRequest.defineObjects(
			portletConfig,
			RenderResponseFactory.create(
				contextHttpServletResponse, liferayRenderRequest));

		return liferayRenderRequest;
	}

	private SearchContext _createSearchContext(
			String destinationFriendlyURL, Long groupId,
			String keywordsParameterName, String scope, String search,
			SuggestionsContributorConfiguration[]
				suggestionsContributorConfigurations,
			ThemeDisplay themeDisplay)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH, Boolean.TRUE);
		searchContext.setAttribute(
			"search.experiences.ip.address",
			contextHttpServletRequest.getRemoteAddr());

		if (themeDisplay != null) {
			searchContext.setAttribute(
				"search.experiences.scope.group.id",
				themeDisplay.getScopeGroupId());
		}

		searchContext.setAttribute(
			"search.suggestions.contributor.configurations",
			suggestionsContributorConfigurations);
		searchContext.setAttribute(
			"search.suggestions.destination.friendly.url",
			destinationFriendlyURL);
		searchContext.setAttribute(
			"search.suggestions.keywords.parameter.name",
			keywordsParameterName);

		searchContext.setCompanyId(contextCompany.getCompanyId());

		if (StringUtil.equals(scope, "everything") ||
			(Validator.isBlank(scope) && (groupId == null))) {
		}
		else if (StringUtil.equals(scope, "this-site") && (groupId != null)) {
			searchContext.setGroupIds(new long[] {groupId});
		}
		else {
			searchContext.setGroupIds(
				ScopeUtil.toGroupIds(contextCompany.getCompanyId(), scope));
		}

		searchContext.setKeywords(search);
		searchContext.setLocale(contextAcceptLanguage.getPreferredLocale());
		searchContext.setTimeZone(contextUser.getTimeZone());
		searchContext.setUserId(contextUser.getUserId());

		return searchContext;
	}

	private ThemeDisplay _createThemeDisplay(String currentURL, Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(contextCompany);
		themeDisplay.setLayout(layout);
		themeDisplay.setLocale(contextAcceptLanguage.getPreferredLocale());
		themeDisplay.setPathMain(_portal.getPathMain());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(layout.getPlid());

		String portalURL = _portal.getPortalURL(contextHttpServletRequest);

		themeDisplay.setPortalDomain(HttpComponentsUtil.getDomain(portalURL));
		themeDisplay.setPortalURL(portalURL);

		themeDisplay.setRequest(contextHttpServletRequest);
		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setSiteGroupId(layout.getGroupId());
		themeDisplay.setURLCurrent(currentURL);
		themeDisplay.setUser(contextUser);

		return themeDisplay;
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	private volatile SearchSuggestionsCompanyConfiguration
		_searchSuggestionsCompanyConfiguration;

	@Reference
	private SuggestionsRetriever _suggestionsRetriever;

}