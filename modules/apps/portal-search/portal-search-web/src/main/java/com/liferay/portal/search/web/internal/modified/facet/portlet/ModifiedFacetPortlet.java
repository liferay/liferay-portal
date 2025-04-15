/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.modified.facet.portlet;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.facet.modified.ModifiedFacetFactory;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.modified.facet.constants.ModifiedFacetPortletKeys;
import com.liferay.portal.search.web.internal.modified.facet.display.context.ModifiedFacetDisplayContext;
import com.liferay.portal.search.web.internal.modified.facet.display.context.builder.ModifiedFacetDisplayContextBuilder;
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
 * @author Adam Brandizzi
 * @author André de Oliveira
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-modified-facet",
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
		"jakarta.portlet.display-name=Modified Facet",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/modified/facet/view.jsp",
		"jakarta.portlet.name=" + ModifiedFacetPortletKeys.MODIFIED_FACET,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ModifiedFacetPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(renderRequest);

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			_buildDisplayContext(portletSharedSearchResponse, renderRequest);

		if (modifiedFacetDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, modifiedFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	private ModifiedFacetDisplayContext _buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			_createModifiedFacetDisplayContextBuilder(renderRequest);

		modifiedFacetDisplayContextBuilder.setCurrentURL(
			_portal.getCurrentURL(renderRequest));
		modifiedFacetDisplayContextBuilder.setFacet(
			portletSharedSearchResponse.getFacet(_getFieldName()));

		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences =
			new ModifiedFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		modifiedFacetDisplayContextBuilder.setFrequenciesVisible(
			modifiedFacetPortletPreferences.isFrequenciesVisible());
		modifiedFacetDisplayContextBuilder.setFrequencyThreshold(
			modifiedFacetPortletPreferences.getFrequencyThreshold());

		String parameterName =
			modifiedFacetPortletPreferences.getParameterName();

		modifiedFacetDisplayContextBuilder.setFromParameterValue(
			portletSharedSearchResponse.getParameter(
				parameterName + "From", renderRequest));

		ThemeDisplay themeDisplay = _getThemeDisplay(renderRequest);

		modifiedFacetDisplayContextBuilder.setLocale(themeDisplay.getLocale());

		modifiedFacetDisplayContextBuilder.setOrder(
			modifiedFacetPortletPreferences.getOrder());
		modifiedFacetDisplayContextBuilder.setPaginationStartParameterName(
			_getPaginationStartParameterName(portletSharedSearchResponse));
		modifiedFacetDisplayContextBuilder.setParameterName(parameterName);
		modifiedFacetDisplayContextBuilder.setParameterValues(
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest));
		modifiedFacetDisplayContextBuilder.setTimeZone(
			themeDisplay.getTimeZone());
		modifiedFacetDisplayContextBuilder.setToParameterValue(
			portletSharedSearchResponse.getParameter(
				parameterName + "To", renderRequest));

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		modifiedFacetDisplayContextBuilder.setTotalHits(
			searchResponse.getTotalHits());

		return modifiedFacetDisplayContextBuilder.build();
	}

	private ModifiedFacetDisplayContextBuilder
		_createModifiedFacetDisplayContextBuilder(RenderRequest renderRequest) {

		try {
			return new ModifiedFacetDisplayContextBuilder(renderRequest);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private String _getFieldName() {
		Facet facet = _modifiedFacetFactory.newInstance(null);

		return facet.getFieldName();
	}

	private String _getPaginationStartParameterName(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

	private ThemeDisplay _getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	@Reference
	private ModifiedFacetFactory _modifiedFacetFactory;

	@Reference
	private Portal _portal;

	@Reference
	private PortletSharedSearchRequest _portletSharedSearchRequest;

}