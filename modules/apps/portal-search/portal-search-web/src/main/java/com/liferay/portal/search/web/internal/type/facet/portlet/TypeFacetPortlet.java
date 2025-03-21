/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.type.facet.portlet;

import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.asset.SearchableAssetClassNamesProvider;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.facet.display.context.AssetEntriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.builder.AssetEntriesSearchFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.type.facet.constants.TypeFacetPortletKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-type-facet",
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
		"jakarta.portlet.display-name=Type Facet",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/type/facet/view.jsp",
		"jakarta.portlet.name=" + TypeFacetPortletKeys.TYPE_FACET,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class TypeFacetPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		AssetEntriesSearchFacetDisplayContext
			assetEntriesSearchFacetDisplayContext = _buildDisplayContext(
				portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			assetEntriesSearchFacetDisplayContext);

		if (assetEntriesSearchFacetDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Reference
	protected SearchableAssetClassNamesProvider
		searchableAssetClassNamesProvider;

	private AssetEntriesSearchFacetDisplayContext _buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		AssetEntriesSearchFacetDisplayContextBuilder
			assetEntriesSearchFacetDisplayContextBuilder =
				_createAssetEntriesSearchFacetDisplayContextBuilder(
					renderRequest);

		TypeFacetPortletPreferences typeFacetPortletPreferences =
			new TypeFacetPortletPreferencesImpl(
				objectDefinitionLocalService,
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest),
				searchableAssetClassNamesProvider);

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		assetEntriesSearchFacetDisplayContextBuilder.setClassNames(
			_getAssetTypesClassNames(
				typeFacetPortletPreferences, themeDisplay));

		assetEntriesSearchFacetDisplayContextBuilder.setFacet(
			portletSharedSearchResponse.getFacet(
				_getAggregationName(renderRequest)));
		assetEntriesSearchFacetDisplayContextBuilder.setFrequenciesVisible(
			typeFacetPortletPreferences.isFrequenciesVisible());
		assetEntriesSearchFacetDisplayContextBuilder.setFrequencyThreshold(
			typeFacetPortletPreferences.getFrequencyThreshold());
		assetEntriesSearchFacetDisplayContextBuilder.setLocale(
			themeDisplay.getLocale());
		assetEntriesSearchFacetDisplayContextBuilder.setOrder(
			typeFacetPortletPreferences.getOrder());
		assetEntriesSearchFacetDisplayContextBuilder.
			setPaginationStartParameterName(
				_getPaginationStartParameterName(portletSharedSearchResponse));

		String parameterName = typeFacetPortletPreferences.getParameterName();

		assetEntriesSearchFacetDisplayContextBuilder.setParameterName(
			parameterName);
		assetEntriesSearchFacetDisplayContextBuilder.setParameterValues(
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest));

		return assetEntriesSearchFacetDisplayContextBuilder.build();
	}

	private AssetEntriesSearchFacetDisplayContextBuilder
		_createAssetEntriesSearchFacetDisplayContextBuilder(
			RenderRequest renderRequest) {

		try {
			return new AssetEntriesSearchFacetDisplayContextBuilder(
				renderRequest);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private String _getAggregationName(RenderRequest renderRequest) {
		return portal.getPortletId(renderRequest);
	}

	private String[] _getAssetTypesClassNames(
		TypeFacetPortletPreferences typeFacetPortletPreferences,
		ThemeDisplay themeDisplay) {

		return typeFacetPortletPreferences.getCurrentAssetTypesArray(
			themeDisplay.getCompanyId());
	}

	private String _getPaginationStartParameterName(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

}