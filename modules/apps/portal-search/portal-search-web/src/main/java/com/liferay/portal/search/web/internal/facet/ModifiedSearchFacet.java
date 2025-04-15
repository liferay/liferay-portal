/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.search.facet.util.FacetFactory;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.facet.modified.ModifiedFacetFactory;
import com.liferay.portal.search.web.facet.BaseJSPSearchFacet;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.internal.util.DateRangeFactoryUtil;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = SearchFacet.class)
public class ModifiedSearchFacet extends BaseJSPSearchFacet {

	@Override
	public String getConfigurationJspPath() {
		return "/facets/configuration/modified.jsp";
	}

	@Override
	public FacetConfiguration getDefaultConfiguration(long companyId) {
		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setClassName(getFacetClassName());

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (int i = 0; i < _LABELS.length; i++) {
			jsonArray.put(
				JSONUtil.put(
					"label", _LABELS[i]
				).put(
					"range", _RANGES[i]
				));
		}

		facetConfiguration.setDataJSONObject(
			JSONUtil.put(
				"frequencyThreshold", 0
			).put(
				"ranges", jsonArray
			));
		facetConfiguration.setFieldName(getFieldName());
		facetConfiguration.setLabel(getLabel());
		facetConfiguration.setOrder(getOrder());
		facetConfiguration.setStatic(false);
		facetConfiguration.setWeight(1.0);

		return facetConfiguration;
	}

	@Override
	public String getDisplayJspPath() {
		return "/facets/view/modified.jsp";
	}

	@Override
	public Facet getFacet() {
		Facet facet = super.getFacet();

		FacetConfiguration facetConfiguration = facet.getFacetConfiguration();

		JSONObject jsonObject = facetConfiguration.getData();

		jsonObject.put(
			"ranges", _replaceAliases(jsonObject.getJSONArray("ranges")));

		return facet;
	}

	@Override
	public String getFacetClassName() {
		return _modifiedFacetFactory.getFacetClassName();
	}

	@Override
	public String getFieldName() {
		Facet facet = _modifiedFacetFactory.newInstance(null);

		return facet.getFieldName();
	}

	@Override
	public JSONObject getJSONData(ActionRequest actionRequest) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		String[] rangesIndexes = StringUtil.split(
			ParamUtil.getString(
				actionRequest, getClassName() + "rangesIndexes"));

		for (String rangesIndex : rangesIndexes) {
			jsonArray.put(
				JSONUtil.put(
					"label",
					ParamUtil.getString(
						actionRequest, getClassName() + "label_" + rangesIndex)
				).put(
					"range",
					ParamUtil.getString(
						actionRequest, getClassName() + "range_" + rangesIndex)
				));
		}

		return JSONUtil.put(
			"frequencyThreshold",
			ParamUtil.getInteger(
				actionRequest, getClassName() + "frequencyThreshold", 1)
		).put(
			"ranges", jsonArray
		);
	}

	@Override
	public String getLabel() {
		return "any-time";
	}

	@Override
	public String getTitle() {
		return "modified-date";
	}

	@Override
	protected FacetFactory getFacetFactory() {
		return _modifiedFacetFactory;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private JSONArray _replaceAliases(JSONArray rangesJSONArray) {
		return DateRangeFactoryUtil.replaceAliases(
			rangesJSONArray, CalendarFactoryUtil.getCalendar());
	}

	private static final String[] _LABELS = {
		"past-hour", "past-24-hours", "past-week", "past-month", "past-year"
	};

	private static final String[] _RANGES = {
		"[past-hour TO *]", "[past-24-hours TO *]", "[past-week TO *]",
		"[past-month TO *]", "[past-year TO *]"
	};

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ModifiedFacetFactory _modifiedFacetFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.portal.search.web)")
	private ServletContext _servletContext;

}