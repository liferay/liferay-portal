/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.asset.SearchableAssetClassNamesProvider;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.FacetFactory;
import com.liferay.portal.search.facet.type.AssetEntriesFacetFactory;
import com.liferay.portal.search.web.facet.BaseJSPSearchFacet;
import com.liferay.portal.search.web.facet.SearchFacet;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.ServletContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = SearchFacet.class)
public class AssetEntriesSearchFacet extends BaseJSPSearchFacet {

	public List<AssetRendererFactory<?>> getAssetRendererFactories(
		long companyId) {

		return AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
			companyId);
	}

	@Override
	public String getConfigurationJspPath() {
		return "/facets/configuration/asset_entries.jsp";
	}

	@Override
	public FacetConfiguration getDefaultConfiguration(long companyId) {
		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setClassName(getFacetClassName());
		facetConfiguration.setDataJSONObject(
			JSONUtil.put(
				"frequencyThreshold", 1
			).put(
				"values",
				() -> {
					JSONArray jsonArray = _jsonFactory.createJSONArray();

					for (String assetType : getAssetTypes(companyId)) {
						jsonArray.put(assetType);
					}

					return jsonArray;
				}
			));
		facetConfiguration.setFieldName(getFieldName());
		facetConfiguration.setLabel(getLabel());
		facetConfiguration.setOrder(getOrder());
		facetConfiguration.setStatic(false);
		facetConfiguration.setWeight(1.5);

		return facetConfiguration;
	}

	@Override
	public String getDisplayJspPath() {
		return "/facets/view/asset_entries.jsp";
	}

	@Override
	public String getFacetClassName() {
		return _assetEntriesFacetFactory.getFacetClassName();
	}

	@Override
	public String getFieldName() {
		Facet facet = _assetEntriesFacetFactory.newInstance(null);

		return facet.getFieldName();
	}

	@Override
	public JSONObject getJSONData(ActionRequest actionRequest) {
		return JSONUtil.put(
			"frequencyThreshold",
			ParamUtil.getInteger(
				actionRequest, getClassName() + "frequencyThreshold", 1)
		).put(
			"values",
			() -> {
				String[] assetTypes = StringUtil.split(
					ParamUtil.getString(
						actionRequest, getClassName() + "assetTypes"));

				JSONArray jsonArray = _jsonFactory.createJSONArray();

				if (ArrayUtil.isEmpty(assetTypes)) {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)actionRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					assetTypes = getAssetTypes(themeDisplay.getCompanyId());
				}

				for (String assetType : assetTypes) {
					jsonArray.put(assetType);
				}

				return jsonArray;
			}
		);
	}

	@Override
	public String getLabel() {
		return "any-asset";
	}

	@Override
	public String getTitle() {
		return "asset-type";
	}

	protected String[] getAssetTypes(long companyId) {
		return _searchableAssetClassNamesProvider.getClassNames(companyId);
	}

	@Override
	protected FacetFactory getFacetFactory() {
		return _assetEntriesFacetFactory;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	private AssetEntriesFacetFactory _assetEntriesFacetFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SearchableAssetClassNamesProvider
		_searchableAssetClassNamesProvider;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.portal.search.web)")
	private ServletContext _servletContext;

}