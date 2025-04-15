/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.search.web.constants.SearchPortletKeys;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.internal.facet.util.SearchFacetRegistryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 */
@Component(
	property = "jakarta.portlet.name=" + SearchPortletKeys.SEARCH,
	service = ConfigurationAction.class
)
public class SearchConfigurationAction extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		JSONArray facetsJSONArray = _jsonFactory.createJSONArray();

		for (SearchFacet searchFacet :
				SearchFacetRegistryUtil.getSearchFacets()) {

			JSONObject facetJSONObject = JSONUtil.put(
				"className", searchFacet.getFacetClassName()
			).put(
				"data", searchFacet.getJSONData(actionRequest)
			).put(
				"fieldName", searchFacet.getFieldName()
			).put(
				"id", searchFacet.getId()
			).put(
				"label", searchFacet.getLabel()
			).put(
				"order", searchFacet.getOrder()
			);

			boolean displayFacet = ParamUtil.getBoolean(
				actionRequest, searchFacet.getClassName() + "displayFacet");

			facetJSONObject.put("static", !displayFacet);

			double weight = ParamUtil.getDouble(
				actionRequest, searchFacet.getClassName() + "weight");

			facetJSONObject.put("weight", weight);

			facetsJSONArray.put(facetJSONObject);
		}

		JSONObject jsonObject = JSONUtil.put("facets", facetsJSONArray);

		setPreference(
			actionRequest, "searchConfiguration", jsonObject.toString());

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Reference
	private JSONFactory _jsonFactory;

}