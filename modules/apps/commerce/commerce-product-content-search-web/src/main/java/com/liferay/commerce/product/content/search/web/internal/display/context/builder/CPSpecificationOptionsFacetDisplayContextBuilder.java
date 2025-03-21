/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.display.context.builder;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPSpecificationOptionFacetsPortletInstanceConfiguration;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPSpecificationOptionFacetsDisplayContext;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPSpecificationOptionsSearchFacetDisplayContext;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPSpecificationOptionsSearchFacetTermDisplayContext;
import com.liferay.commerce.product.content.search.web.internal.util.CPSpecificationOptionFacetsUtil;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @author Crescenzo Rega
 */
public class CPSpecificationOptionsFacetDisplayContextBuilder
	implements Serializable {

	public CPSpecificationOptionFacetsDisplayContext build()
		throws PortalException {

		CPSpecificationOptionFacetsDisplayContext
			cpSpecificationOptionFacetsDisplayContext =
				new CPSpecificationOptionFacetsDisplayContext(
					_configurationProvider, _groupLocalService,
					_portal.getHttpServletRequest(_renderRequest));

		_cpSpecificationOptionFacetsPortletInstanceConfiguration =
			cpSpecificationOptionFacetsDisplayContext.
				getCPSpecificationOptionFacetsPortletInstanceConfiguration();

		cpSpecificationOptionFacetsDisplayContext.
			setCPSpecificationOptionsSearchFacetDisplayContexts(
				_buildCPSpecificationOptionsSearchFacetDisplayContexts());

		return cpSpecificationOptionFacetsDisplayContext;
	}

	public void configurationProvider(
		ConfigurationProvider configurationProvider) {

		_configurationProvider = configurationProvider;
	}

	public void cpSpecificationOptionLocalService(
		CPSpecificationOptionLocalService cpSpecificationOptionLocalService) {

		_cpSpecificationOptionLocalService = cpSpecificationOptionLocalService;
	}

	public void groupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	public void parameterValues(String... parameterValues) {
		_parameterValues = parameterValues;
	}

	public void portal(Portal portal) {
		_portal = portal;
	}

	public void portletSharedSearchRequest(
		PortletSharedSearchRequest portletSharedSearchRequest) {

		_portletSharedSearchRequest = portletSharedSearchRequest;
	}

	public void renderRequest(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	private CPSpecificationOptionsSearchFacetDisplayContext
			_buildCPSpecificationOptionsSearchFacetDisplayContext()
		throws PortalException {

		_tuples = _getTuples(_facet.getFacetCollector());

		CPSpecificationOptionsSearchFacetDisplayContext
			cpSpecificationOptionsSearchFacetDisplayContext =
				new CPSpecificationOptionsSearchFacetDisplayContext();

		cpSpecificationOptionsSearchFacetDisplayContext.setFacet(_facet);
		cpSpecificationOptionsSearchFacetDisplayContext.setLocale(_locale);
		cpSpecificationOptionsSearchFacetDisplayContext.setParameterName(
			CPSpecificationOptionFacetsUtil.
				getCPSpecificationOptionKeyFromIndexFieldName(
					_facet.getFieldName()));
		cpSpecificationOptionsSearchFacetDisplayContext.
			setPaginationStartParameterName(_paginationStartParameterName);
		cpSpecificationOptionsSearchFacetDisplayContext.setParameterValue(
			_getFirstParameterValueString());
		cpSpecificationOptionsSearchFacetDisplayContext.setRenderRequest(
			_renderRequest);

		CPSpecificationOption cpSpecificationOption = _getCPSpecificationOption(
			_facet.getFieldName());

		cpSpecificationOptionsSearchFacetDisplayContext.setPriority(
			_getCPSpecificationOptionsSearchFacetDisplayContextPriority(
				cpSpecificationOption.getCPOptionCategory(),
				cpSpecificationOption,
				_portletSharedSearchResponse.getPortletPreferences(
					_renderRequest)));

		cpSpecificationOptionsSearchFacetDisplayContext.setTermDisplayContexts(
			_buildTermDisplayContexts());
		cpSpecificationOptionsSearchFacetDisplayContext.
			setCpSpecificationOptionLocalService(
				_cpSpecificationOptionLocalService);

		return cpSpecificationOptionsSearchFacetDisplayContext;
	}

	private CPSpecificationOptionsSearchFacetDisplayContext
			_buildCPSpecificationOptionsSearchFacetDisplayContext(
				Facet facet,
				PortletSharedSearchResponse portletSharedSearchResponse,
				RenderRequest renderRequest)
		throws PortalException {

		_facet = facet;

		parameterValues(
			portletSharedSearchResponse.getParameterValues(
				facet.getFieldName(), renderRequest));

		return _buildCPSpecificationOptionsSearchFacetDisplayContext();
	}

	private List<CPSpecificationOptionsSearchFacetDisplayContext>
			_buildCPSpecificationOptionsSearchFacetDisplayContexts()
		throws PortalException {

		_portletSharedSearchResponse = _portletSharedSearchRequest.search(
			_renderRequest);

		Facet facet = _portletSharedSearchResponse.getFacet(
			CPField.SPECIFICATION_NAMES);

		if (facet == null) {
			return Collections.emptyList();
		}

		List<Facet> filledFacets = new ArrayList<>();

		FacetCollector facetCollector = facet.getFacetCollector();

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (TermCollector termCollector : facetCollector.getTermCollectors()) {
			CPSpecificationOption cpSpecificationOption =
				_cpSpecificationOptionLocalService.getCPSpecificationOption(
					themeDisplay.getCompanyId(), termCollector.getTerm());

			if (cpSpecificationOption.isFacetable()) {
				Facet cpSpecificationOptionFacet =
					_portletSharedSearchResponse.getFacet(
						CPSpecificationOptionFacetsUtil.getIndexFieldName(
							termCollector.getTerm(),
							themeDisplay.getLanguageId()));

				if (cpSpecificationOptionFacet == null) {
					continue;
				}

				filledFacets.add(cpSpecificationOptionFacet);
			}
		}

		List<CPSpecificationOptionsSearchFacetDisplayContext>
			cpSpecificationOptionsSearchFacetDisplayContexts =
				new ArrayList<>();

		_paginationStartParameterName = _getPaginationStartParameterName(
			_portletSharedSearchResponse);

		_locale = themeDisplay.getLocale();

		for (Facet filledFacet : filledFacets) {
			CPSpecificationOptionsSearchFacetDisplayContext
				cpSpecificationOptionsSearchFacetDisplayContext =
					_buildCPSpecificationOptionsSearchFacetDisplayContext(
						filledFacet, _portletSharedSearchResponse,
						_renderRequest);

			List<CPSpecificationOptionsSearchFacetTermDisplayContext>
				cpSpecificationOptionsSearchFacetTermDisplayContexts =
					cpSpecificationOptionsSearchFacetDisplayContext.
						getTermDisplayContexts();

			if (!cpSpecificationOptionsSearchFacetTermDisplayContexts.
					isEmpty()) {

				cpSpecificationOptionsSearchFacetDisplayContexts.add(
					cpSpecificationOptionsSearchFacetDisplayContext);
			}
		}

		String specificationsOrder =
			_cpSpecificationOptionFacetsPortletInstanceConfiguration.
				specificationsOrder();

		if (specificationsOrder != null) {
			Comparator<CPSpecificationOptionsSearchFacetDisplayContext>
				comparator = Comparator.comparing(
					CPSpecificationOptionsSearchFacetDisplayContext::
						getPriority);

			if (StringUtil.equals(specificationsOrder, "label-priority:desc") ||
				StringUtil.equals(specificationsOrder, "priority:desc")) {

				comparator = comparator.reversed();
			}

			cpSpecificationOptionsSearchFacetDisplayContexts.sort(comparator);
		}

		return cpSpecificationOptionsSearchFacetDisplayContexts;
	}

	private CPSpecificationOptionsSearchFacetTermDisplayContext
		_buildTermDisplayContext(
			int frequency, boolean selected, int popularity, String term) {

		CPSpecificationOptionsSearchFacetTermDisplayContext
			cpSpecificationOptionsSearchFacetTermDisplayContext =
				new CPSpecificationOptionsSearchFacetTermDisplayContext();

		cpSpecificationOptionsSearchFacetTermDisplayContext.setDisplayName(
			term);
		cpSpecificationOptionsSearchFacetTermDisplayContext.setFrequency(
			frequency);
		cpSpecificationOptionsSearchFacetTermDisplayContext.setFrequencyVisible(
			_cpSpecificationOptionFacetsPortletInstanceConfiguration.
				frequenciesVisible());
		cpSpecificationOptionsSearchFacetTermDisplayContext.setPopularity(
			popularity);
		cpSpecificationOptionsSearchFacetTermDisplayContext.setSelected(
			selected);

		return cpSpecificationOptionsSearchFacetTermDisplayContext;
	}

	private List<CPSpecificationOptionsSearchFacetTermDisplayContext>
		_buildTermDisplayContexts() {

		if (_tuples.isEmpty()) {
			return Collections.emptyList();
		}

		List<CPSpecificationOptionsSearchFacetTermDisplayContext>
			cpSpecificationOptionsSearchFacetTermDisplayContexts =
				new ArrayList<>(_tuples.size());

		int maxCount = 1;
		int minCount = 1;

		int frequencyThreshold =
			_cpSpecificationOptionFacetsPortletInstanceConfiguration.
				frequencyThreshold();
		int maxTerms =
			_cpSpecificationOptionFacetsPortletInstanceConfiguration.maxTerms();

		if (_cpSpecificationOptionFacetsPortletInstanceConfiguration.
				frequenciesVisible() &&
			StringUtil.equals(
				_cpSpecificationOptionFacetsPortletInstanceConfiguration.
					displayStyle(),
				"ddmTemplate_CP-SPECIFICATION-OPTION-FACET-CLOUD-FTL")) {

			// The cloud style may not list tags in the order of frequency.
			// Keep looking through the results until we reach the maximum
			// number of terms or we run out of terms.

			for (int i = 0, j = 0; i < _tuples.size(); i++, j++) {
				if (j >= maxTerms) {
					break;
				}

				Tuple tuple = _tuples.get(i);

				Integer frequency = (Integer)tuple.getObject(1);

				if (frequencyThreshold > frequency) {
					j--;

					continue;
				}

				maxCount = Math.max(maxCount, frequency);
				minCount = Math.min(minCount, frequency);
			}
		}

		double multiplier = 1;

		if (maxCount != minCount) {
			multiplier = (double)5 / (maxCount - minCount);
		}

		for (int i = 0, j = 0; i < _tuples.size(); i++, j++) {
			if ((maxTerms > 0) && (j >= maxTerms)) {
				break;
			}

			Tuple tuple = _tuples.get(i);

			Integer frequency = (Integer)tuple.getObject(1);

			if (frequencyThreshold > frequency) {
				j--;

				continue;
			}

			int popularity = (int)_getPopularity(
				frequency, maxCount, minCount, multiplier);

			String fieldName = (String)tuple.getObject(0);

			String fieldValue = (String)tuple.getObject(2);

			cpSpecificationOptionsSearchFacetTermDisplayContexts.add(
				_buildTermDisplayContext(
					frequency,
					_isCPDefinitionSpecificationOptionValueSelected(
						fieldName, fieldValue),
					popularity, fieldValue));
		}

		return cpSpecificationOptionsSearchFacetTermDisplayContexts;
	}

	private CPSpecificationOption _getCPSpecificationOption(String fieldName) {
		return _cpSpecificationOptionLocalService.fetchCPSpecificationOption(
			PortalUtil.getCompanyId(_renderRequest),
			CPSpecificationOptionFacetsUtil.
				getCPSpecificationOptionKeyFromIndexFieldName(fieldName));
	}

	private double _getCPSpecificationOptionsSearchFacetDisplayContextPriority(
		CPOptionCategory cpOptionCategory,
		CPSpecificationOption cpSpecificationOption,
		PortletPreferences portletPreferences) {

		double priority = GetterUtil.DEFAULT_DOUBLE;

		if (portletPreferences != null) {
			String specificationsOrder = portletPreferences.getValue(
				"specificationsOrder",
				_cpSpecificationOptionFacetsPortletInstanceConfiguration.
					specificationsOrder());

			if (specificationsOrder.equals("label-priority:asc") ||
				specificationsOrder.equals("label-priority:desc")) {

				priority = cpSpecificationOption.getPriority();
			}
			else if (cpOptionCategory != null) {
				priority = cpOptionCategory.getPriority();
			}
		}
		else if (cpOptionCategory != null) {
			priority = cpOptionCategory.getPriority();
		}

		return priority;
	}

	private String _getFirstParameterValueString() {
		if (_parameterValues != null) {
			for (String parameterValue : _parameterValues) {
				if (GetterUtil.getLong(parameterValue) > 0) {
					return parameterValue;
				}
			}
		}

		return StringPool.BLANK;
	}

	private String _getPaginationStartParameterName(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

	private double _getPopularity(
		int frequency, int maxCount, int minCount, double multiplier) {

		double popularity = maxCount - (maxCount - (frequency - minCount));

		return 1 + (popularity * multiplier);
	}

	private List<Tuple> _getTuples(FacetCollector facetCollector) {
		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		List<Tuple> tuples = new ArrayList<>(termCollectors.size());

		for (TermCollector termCollector : termCollectors) {
			tuples.add(
				new Tuple(
					facetCollector.getFieldName(), termCollector.getFrequency(),
					termCollector.getTerm()));
		}

		return tuples;
	}

	private boolean _isCPDefinitionSpecificationOptionValueSelected(
		String fieldName, String fieldValue) {

		CPSpecificationOption cpSpecificationOption = _getCPSpecificationOption(
			fieldName);

		return ArrayUtil.contains(
			_portletSharedSearchResponse.getParameterValues(
				cpSpecificationOption.getKey(), _renderRequest),
			fieldValue);
	}

	private ConfigurationProvider _configurationProvider;
	private CPSpecificationOptionFacetsPortletInstanceConfiguration
		_cpSpecificationOptionFacetsPortletInstanceConfiguration;
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;
	private Facet _facet;
	private GroupLocalService _groupLocalService;
	private Locale _locale;
	private String _paginationStartParameterName;
	private String[] _parameterValues;
	private Portal _portal;
	private PortletSharedSearchRequest _portletSharedSearchRequest;
	private PortletSharedSearchResponse _portletSharedSearchResponse;
	private RenderRequest _renderRequest;
	private List<Tuple> _tuples;

}