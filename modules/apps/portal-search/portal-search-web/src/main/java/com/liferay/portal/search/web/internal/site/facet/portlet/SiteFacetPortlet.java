/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.site.facet.portlet;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.facet.display.context.ScopeSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.builder.ScopeSearchFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.site.facet.constants.SiteFacetPortletKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-site-facet",
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
		"jakarta.portlet.display-name=Site Facet",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/site/facet/view.jsp",
		"jakarta.portlet.name=" + SiteFacetPortletKeys.SITE_FACET,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SiteFacetPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		ScopeSearchFacetDisplayContext siteFacetPortletDisplayContext =
			_buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, siteFacetPortletDisplayContext);

		if (siteFacetPortletDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private ScopeSearchFacetDisplayContext _buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		ScopeSearchFacetDisplayContextBuilder
			scopeSearchFacetDisplayContextBuilder =
				_createScopeSearchFacetDisplayContextBuilder(renderRequest);

		scopeSearchFacetDisplayContextBuilder.setFacet(
			portletSharedSearchResponse.getFacet(
				_getAggregationName(renderRequest)));

		long[] filteredGroupIds = _getFilteredGroupIds(
			portletSharedSearchResponse);

		if (filteredGroupIds != null) {
			scopeSearchFacetDisplayContextBuilder.setFilteredGroupIds(
				filteredGroupIds);
		}

		SiteFacetPortletPreferences siteFacetPortletPreferences =
			new SiteFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		scopeSearchFacetDisplayContextBuilder.setFrequenciesVisible(
			siteFacetPortletPreferences.isFrequenciesVisible());
		scopeSearchFacetDisplayContextBuilder.setFrequencyThreshold(
			siteFacetPortletPreferences.getFrequencyThreshold());

		scopeSearchFacetDisplayContextBuilder.setGroupLocalService(
			groupLocalService);
		scopeSearchFacetDisplayContextBuilder.setLanguage(language);
		scopeSearchFacetDisplayContextBuilder.setLocale(
			_getLocale(portletSharedSearchResponse, renderRequest));
		scopeSearchFacetDisplayContextBuilder.setMaxTerms(
			siteFacetPortletPreferences.getMaxTerms());
		scopeSearchFacetDisplayContextBuilder.setOrder(
			siteFacetPortletPreferences.getOrder());
		scopeSearchFacetDisplayContextBuilder.setPaginationStartParameterName(
			_getPaginationStartParameterName(portletSharedSearchResponse));

		String parameterName = siteFacetPortletPreferences.getParameterName();

		scopeSearchFacetDisplayContextBuilder.setParameterName(parameterName);

		scopeSearchFacetDisplayContextBuilder.setParameterValues(
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest));

		scopeSearchFacetDisplayContextBuilder.setRequest(
			_getHttpServletRequest(renderRequest));

		return scopeSearchFacetDisplayContextBuilder.build();
	}

	private ScopeSearchFacetDisplayContextBuilder
		_createScopeSearchFacetDisplayContextBuilder(
			RenderRequest renderRequest) {

		try {
			return new ScopeSearchFacetDisplayContextBuilder(renderRequest);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private String _getAggregationName(RenderRequest renderRequest) {
		return portal.getPortletId(renderRequest);
	}

	private long[] _getFilteredGroupIds(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchSettings searchSettings =
			portletSharedSearchResponse.getSearchSettings();

		SearchContext searchContext = searchSettings.getSearchContext();

		return searchContext.getGroupIds();
	}

	private HttpServletRequest _getHttpServletRequest(
		RenderRequest renderRequest) {

		LiferayPortletRequest liferayPortletRequest =
			portal.getLiferayPortletRequest(renderRequest);

		return liferayPortletRequest.getHttpServletRequest();
	}

	private Locale _getLocale(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		return themeDisplay.getLocale();
	}

	private String _getPaginationStartParameterName(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

}