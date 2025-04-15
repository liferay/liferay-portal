/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.folder.facet.portlet;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearcher;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookupImpl;
import com.liferay.portal.search.web.internal.facet.display.context.builder.FolderSearchFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.folder.facet.constants.FolderFacetPortletKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-folder-facet",
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
		"jakarta.portlet.display-name=Folder Facet",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/folder/facet/view.jsp",
		"jakarta.portlet.name=" + FolderFacetPortletKeys.FOLDER_FACET,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class FolderFacetPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		FolderSearchFacetDisplayContext folderSearchFacetDisplayContext =
			_buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, folderSearchFacetDisplayContext);

		if (folderSearchFacetDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private FolderSearchFacetDisplayContext _buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		FolderSearchFacetDisplayContextBuilder
			folderSearchFacetDisplayContextBuilder =
				_createFolderSearchFacetDisplayContextBuilder(renderRequest);

		folderSearchFacetDisplayContextBuilder.setFacet(
			portletSharedSearchResponse.getFacet(
				_getAggregationName(renderRequest)));
		folderSearchFacetDisplayContextBuilder.setFolderTitleLookup(
			new FolderTitleLookupImpl(
				new FolderSearcher(),
				portal.getHttpServletRequest(renderRequest)));

		FolderFacetPortletPreferences folderFacetPortletPreferences =
			new FolderFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		folderSearchFacetDisplayContextBuilder.setFrequenciesVisible(
			folderFacetPortletPreferences.isFrequenciesVisible());
		folderSearchFacetDisplayContextBuilder.setFrequencyThreshold(
			folderFacetPortletPreferences.getFrequencyThreshold());

		folderSearchFacetDisplayContextBuilder.setLocale(
			_getLocale(portletSharedSearchResponse, renderRequest));
		folderSearchFacetDisplayContextBuilder.setMaxTerms(
			folderFacetPortletPreferences.getMaxTerms());
		folderSearchFacetDisplayContextBuilder.setOrder(
			folderFacetPortletPreferences.getOrder());
		folderSearchFacetDisplayContextBuilder.setPaginationStartParameterName(
			_getPaginationStartParameterName(portletSharedSearchResponse));

		String parameterName = folderFacetPortletPreferences.getParameterName();

		folderSearchFacetDisplayContextBuilder.setParameterName(parameterName);
		folderSearchFacetDisplayContextBuilder.setParameterValues(
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest));

		return folderSearchFacetDisplayContextBuilder.build();
	}

	private FolderSearchFacetDisplayContextBuilder
		_createFolderSearchFacetDisplayContextBuilder(
			RenderRequest renderRequest) {

		try {
			return new FolderSearchFacetDisplayContextBuilder(renderRequest);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private String _getAggregationName(RenderRequest renderRequest) {
		return portal.getPortletId(renderRequest);
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