/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.display.context.builder;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPOptionFacetsPortletInstanceConfiguration;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPOptionsSearchFacetDisplayContext;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPOptionsSearchFacetTermDisplayContext;
import com.liferay.commerce.product.content.search.web.internal.util.CPOptionFacetsUtil;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.RenderRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andrea Sbarra
 */
public class CPOptionsSearchFacetDisplayContextBuilder implements Serializable {

	public CPOptionsSearchFacetDisplayContextBuilder(
		RenderRequest renderRequest) {

		_renderRequest = renderRequest;
	}

	public CPOptionsSearchFacetDisplayContext build() {
		CPOptionsSearchFacetDisplayContext cpOptionsSearchFacetDisplayContext =
			_createCPOptionsSearchFacetDisplayContext();

		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(_renderRequest);

		_cpOptionFacetsPortletInstanceConfiguration =
			cpOptionsSearchFacetDisplayContext.
				getCPOptionFacetsPortletInstanceConfiguration();

		cpOptionsSearchFacetDisplayContext.setFacets(_getFacets());
		cpOptionsSearchFacetDisplayContext.setCPOptionLocalService(
			_cpOptionLocalService);
		cpOptionsSearchFacetDisplayContext.setPortletSharedSearchResponse(
			portletSharedSearchResponse);
		cpOptionsSearchFacetDisplayContext.setPaginationStartParameterName(
			_getPaginationStartParameterName());
		cpOptionsSearchFacetDisplayContext.setTermDisplayContexts(
			buildTermDisplayContexts());

		return cpOptionsSearchFacetDisplayContext;
	}

	public void configurationProvider(
		ConfigurationProvider configurationProvider) {

		_configurationProvider = configurationProvider;
	}

	public void cpOptionLocalService(
		CPOptionLocalService cpOptionLocalService) {

		_cpOptionLocalService = cpOptionLocalService;
	}

	public void groupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	public void portal(Portal portal) {
		_portal = portal;
	}

	public void portletSharedSearchRequest(
		PortletSharedSearchRequest portletSharedSearchRequest) {

		_portletSharedSearchRequest = portletSharedSearchRequest;
	}

	protected CPOptionsSearchFacetTermDisplayContext buildTermDisplayContext(
		int frequency, boolean frequencyVisible, int popularity,
		boolean selected, String term) {

		CPOptionsSearchFacetTermDisplayContext
			cpOptionsSearchFacetTermDisplayContext =
				new CPOptionsSearchFacetTermDisplayContext();

		cpOptionsSearchFacetTermDisplayContext.setFrequency(frequency);
		cpOptionsSearchFacetTermDisplayContext.setFrequencyVisible(
			frequencyVisible);
		cpOptionsSearchFacetTermDisplayContext.setPopularity(popularity);
		cpOptionsSearchFacetTermDisplayContext.setSelected(selected);
		cpOptionsSearchFacetTermDisplayContext.setTerm(term);

		return cpOptionsSearchFacetTermDisplayContext;
	}

	protected List<CPOptionsSearchFacetTermDisplayContext>
		buildTermDisplayContexts() {

		if (ListUtil.isEmpty(_tuples)) {
			return Collections.emptyList();
		}

		List<CPOptionsSearchFacetTermDisplayContext>
			cpOptionsSearchFacetTermDisplayContexts = new ArrayList<>(
				_tuples.size());

		int maxCount = 1;
		int minCount = 1;

		int frequencyThreshold =
			_cpOptionFacetsPortletInstanceConfiguration.frequencyThreshold();
		int maxTerms = _cpOptionFacetsPortletInstanceConfiguration.maxTerms();

		if (_cpOptionFacetsPortletInstanceConfiguration.frequenciesVisible() &&
			StringUtil.equals(
				_cpOptionFacetsPortletInstanceConfiguration.displayStyle(),
				"ddmTemplate_CP-SPECIFICATION-OPTION-FACET-CLOUD-FTL")) {

			// The cloud style may not list tags in the order of frequency.
			// Keep looking through the results until we reach the maximum
			// number of terms or we run out of terms.

			for (int i = 0, j = 0; i < _tuples.size(); i++, j++) {
				if (j >= maxTerms) {
					break;
				}

				Tuple tuple = _tuples.get(i);

				Integer frequency = (Integer)tuple.getObject(2);

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

			Integer frequency = (Integer)tuple.getObject(2);

			if (frequencyThreshold > frequency) {
				j--;

				continue;
			}

			int popularity = (int)getPopularity(
				frequency, maxCount, minCount, multiplier);

			CPOption cpOption = (CPOption)tuple.getObject(0);

			String term = (String)tuple.getObject(1);

			cpOptionsSearchFacetTermDisplayContexts.add(
				buildTermDisplayContext(
					frequency,
					_cpOptionFacetsPortletInstanceConfiguration.
						frequenciesVisible(),
					popularity, isSelected(cpOption, term), term));
		}

		return cpOptionsSearchFacetTermDisplayContexts;
	}

	protected double getPopularity(
		int frequency, int maxCount, int minCount, double multiplier) {

		double popularity = maxCount - (maxCount - (frequency - minCount));

		return 1 + (popularity * multiplier);
	}

	protected boolean isSelected(CPOption cpOption, String fieldValue) {
		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(_renderRequest);

		return ArrayUtil.contains(
			portletSharedSearchResponse.getParameterValues(
				cpOption.getKey(), _renderRequest),
			fieldValue);
	}

	private CPOptionsSearchFacetDisplayContext
		_createCPOptionsSearchFacetDisplayContext() {

		try {
			return new CPOptionsSearchFacetDisplayContext(
				_configurationProvider, _groupLocalService,
				_portal.getHttpServletRequest(_renderRequest));
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private List<Facet> _getFacets() {
		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(_renderRequest);

		Facet facet = portletSharedSearchResponse.getFacet(
			CPField.OPTION_NAMES);

		if (facet == null) {
			return Collections.emptyList();
		}

		List<Facet> filledFacets = new ArrayList<>();

		FacetCollector facetCollector = facet.getFacetCollector();

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			_renderRequest);

		for (TermCollector termCollector : facetCollector.getTermCollectors()) {
			CPOption cpOption = _cpOptionLocalService.fetchCPOption(
				themeDisplay.getCompanyId(), termCollector.getTerm());

			if ((cpOption != null) && cpOption.isFacetable()) {
				Facet cpOptionFacet = portletSharedSearchResponse.getFacet(
					CPOptionFacetsUtil.getIndexFieldName(
						termCollector.getTerm(), themeDisplay.getLanguageId()));

				if (cpOptionFacet == null) {
					continue;
				}

				filledFacets.add(cpOptionFacet);
				_tuples = _getTuples(
					cpOption, cpOptionFacet.getFacetCollector());
			}
		}

		return filledFacets;
	}

	private String _getPaginationStartParameterName() {
		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(_renderRequest);

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

	private List<Tuple> _getTuples(
		CPOption cpOption, FacetCollector facetCollector) {

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		List<Tuple> tuples = new ArrayList<>(termCollectors.size());

		for (TermCollector termCollector : termCollectors) {
			tuples.add(
				new Tuple(
					cpOption, termCollector.getTerm(),
					termCollector.getFrequency()));
		}

		return tuples;
	}

	private ConfigurationProvider _configurationProvider;
	private CPOptionFacetsPortletInstanceConfiguration
		_cpOptionFacetsPortletInstanceConfiguration;
	private CPOptionLocalService _cpOptionLocalService;
	private GroupLocalService _groupLocalService;
	private Portal _portal;
	private PortletSharedSearchRequest _portletSharedSearchRequest;
	private final RenderRequest _renderRequest;
	private List<Tuple> _tuples;

}