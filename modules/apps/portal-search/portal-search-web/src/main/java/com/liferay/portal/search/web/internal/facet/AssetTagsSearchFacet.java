/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.FacetFactory;
import com.liferay.portal.search.facet.tag.AssetTagNamesFacetFactory;
import com.liferay.portal.search.web.facet.BaseJSPSearchFacet;
import com.liferay.portal.search.web.facet.SearchFacet;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = SearchFacet.class)
public class AssetTagsSearchFacet extends BaseJSPSearchFacet {

	@Override
	public String getConfigurationJspPath() {
		return "/facets/configuration/asset_tags.jsp";
	}

	@Override
	public FacetConfiguration getDefaultConfiguration(long companyId) {
		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setClassName(getFacetClassName());
		facetConfiguration.setDataJSONObject(
			JSONUtil.put(
				"displayStyle", "list"
			).put(
				"frequencyThreshold", 1
			).put(
				"maxTerms", 10
			).put(
				"showAssetCount", true
			));
		facetConfiguration.setFieldName(getFieldName());
		facetConfiguration.setLabel(getLabel());
		facetConfiguration.setOrder(getOrder());
		facetConfiguration.setStatic(false);
		facetConfiguration.setWeight(1.4);

		return facetConfiguration;
	}

	@Override
	public String getDisplayJspPath() {
		return "/facets/view/asset_tags.jsp";
	}

	@Override
	public String getFacetClassName() {
		return assetTagNamesFacetFactory.getFacetClassName();
	}

	@Override
	public String getFieldName() {
		Facet facet = assetTagNamesFacetFactory.newInstance(null);

		return facet.getFieldName();
	}

	@Override
	public JSONObject getJSONData(ActionRequest actionRequest) {
		return JSONUtil.put(
			"displayStyle",
			ParamUtil.getString(
				actionRequest, getClassName() + "displayStyleFacet", "list")
		).put(
			"frequencyThreshold",
			ParamUtil.getInteger(
				actionRequest, getClassName() + "frequencyThreshold", 1)
		).put(
			"maxTerms",
			ParamUtil.getInteger(actionRequest, getClassName() + "maxTerms", 10)
		).put(
			"showAssetCount",
			ParamUtil.getBoolean(
				actionRequest, getClassName() + "showAssetCount", true)
		);
	}

	@Override
	public String getLabel() {
		return "any-tag";
	}

	@Override
	public String getTitle() {
		return "tag";
	}

	@Override
	protected FacetFactory getFacetFactory() {
		return assetTagNamesFacetFactory;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	protected AssetTagNamesFacetFactory assetTagNamesFacetFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.portal.search.web)")
	private ServletContext _servletContext;

}