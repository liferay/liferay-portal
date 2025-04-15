/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.category.facet.portlet;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.builder.AssetCategoriesSearchFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.builder.AssetCategoryPermissionCheckerImpl;
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
		"com.liferay.portlet.css-class-wrapper=portlet-category-facet",
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
		"jakarta.portlet.display-name=Category Facet",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/category/facet/view.jsp",
		"jakarta.portlet.name=" + CategoryFacetPortletKeys.CATEGORY_FACET,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CategoryFacetPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext = _buildDisplayContext(
				portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			assetCategoriesSearchFacetDisplayContext);

		if (assetCategoriesSearchFacetDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected AssetCategoryLocalService assetCategoryLocalService;

	@Reference
	protected AssetVocabularyLocalService assetVocabularyLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private AssetCategoriesSearchFacetDisplayContext _buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		AssetCategoriesSearchFacetDisplayContextBuilder
			assetCategoriesSearchFacetDisplayContextBuilder =
				new AssetCategoriesSearchFacetDisplayContextBuilder(
					renderRequest);

		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetCategoryLocalService(assetCategoryLocalService);

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetCategoryPermissionChecker(
				new AssetCategoryPermissionCheckerImpl(
					themeDisplay.getPermissionChecker()));

		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetVocabularyLocalService(assetVocabularyLocalService);

		CategoryFacetPortletPreferences categoryFacetPortletPreferences =
			new CategoryFacetPortletPreferencesImpl(
				_assetVocabularyLocalService, _groupLocalService,
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		assetCategoriesSearchFacetDisplayContextBuilder.setDisplayStyle(
			categoryFacetPortletPreferences.getDisplayStyle());

		Group group = themeDisplay.getScopeGroup();

		Group stagingGroup = group.getStagingGroup();

		if (stagingGroup != null) {
			assetCategoriesSearchFacetDisplayContextBuilder.setExcludedGroupId(
				stagingGroup.getGroupId());
		}

		assetCategoriesSearchFacetDisplayContextBuilder.setFacet(
			portletSharedSearchResponse.getFacet(
				_getAggregationName(renderRequest)));
		assetCategoriesSearchFacetDisplayContextBuilder.setFrequenciesVisible(
			categoryFacetPortletPreferences.isFrequenciesVisible());
		assetCategoriesSearchFacetDisplayContextBuilder.setFrequencyThreshold(
			categoryFacetPortletPreferences.getFrequencyThreshold());
		assetCategoriesSearchFacetDisplayContextBuilder.setLocale(
			themeDisplay.getLocale());
		assetCategoriesSearchFacetDisplayContextBuilder.setMaxTerms(
			categoryFacetPortletPreferences.getMaxTerms());
		assetCategoriesSearchFacetDisplayContextBuilder.setOrder(
			categoryFacetPortletPreferences.getOrder());
		assetCategoriesSearchFacetDisplayContextBuilder.
			setPaginationStartParameterName(
				_getPaginationStartParameterName(portletSharedSearchResponse));

		String parameterName =
			categoryFacetPortletPreferences.getParameterName();

		assetCategoriesSearchFacetDisplayContextBuilder.setParameterName(
			parameterName);
		assetCategoriesSearchFacetDisplayContextBuilder.setParameterValues(
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest));

		assetCategoriesSearchFacetDisplayContextBuilder.setPortal(portal);

		return assetCategoriesSearchFacetDisplayContextBuilder.build();
	}

	private String _getAggregationName(RenderRequest renderRequest) {
		return portal.getPortletId(renderRequest);
	}

	private String _getPaginationStartParameterName(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}