/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet.shared.search;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPPriceRangeFacetsPortletInstanceConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.range.RangeFacetFactory;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_PRICE_RANGE_FACETS,
	service = PortletSharedSearchContributor.class
)
public class CPPriceRangeFacetsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		RenderRequest renderRequest =
			portletSharedSearchSettings.getRenderRequest();

		try {
			SearchContext searchContext =
				portletSharedSearchSettings.getSearchContext();

			Facet facet = _getFacet(
				portletSharedSearchSettings, renderRequest, searchContext);

			String[] parameterValues =
				portletSharedSearchSettings.getParameterValues(
					facet.getFieldName());

			if (ArrayUtil.isNotEmpty(parameterValues)) {
				facet.select(parameterValues);
			}

			portletSharedSearchSettings.addFacet(facet);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private Facet _getFacet(
			PortletSharedSearchSettings portletSharedSearchSettings,
			RenderRequest renderRequest, SearchContext searchContext)
		throws PortalException {

		CPPriceRangeFacetsPortletInstanceConfiguration
			cpPriceRangeFacetsPortletInstanceConfiguration =
				_configurationProvider.getPortletInstanceConfiguration(
					CPPriceRangeFacetsPortletInstanceConfiguration.class,
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY));

		Facet facet = _rangeFacetFactory.newInstance(searchContext);

		FacetConfiguration facetConfiguration = new FacetConfiguration();

		JSONObject jsonObject = new JSONObjectImpl();

		String rangesJSONArrayString =
			cpPriceRangeFacetsPortletInstanceConfiguration.
				rangesJSONArrayString();

		PortletPreferences portletPreferences =
			portletSharedSearchSettings.getPortletPreferences();

		if (portletPreferences != null) {
			rangesJSONArrayString = portletPreferences.getValue(
				"rangesJSONArrayString", rangesJSONArrayString);
		}

		rangesJSONArrayString = StringUtil.replace(
			rangesJSONArrayString, new String[] {"\\,", StringPool.STAR},
			new String[] {StringPool.COMMA, String.valueOf(Double.MAX_VALUE)});

		jsonObject.put(
			"ranges", _jsonFactory.createJSONArray(rangesJSONArrayString));

		facetConfiguration.setDataJSONObject(jsonObject);

		facet.setFacetConfiguration(facetConfiguration);

		facet.setFieldName(CPField.BASE_PRICE);

		return facet;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPPriceRangeFacetsPortletSharedSearchContributor.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RangeFacetFactory _rangeFacetFactory;

}