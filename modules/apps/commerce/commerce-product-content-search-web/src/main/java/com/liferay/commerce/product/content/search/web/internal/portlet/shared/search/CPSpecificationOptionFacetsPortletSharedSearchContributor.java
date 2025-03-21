/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet.shared.search;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.util.CPSpecificationOptionFacetsUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.search.facet.SerializableFacet;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.SimpleFacet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTION_FACETS,
	service = PortletSharedSearchContributor.class
)
public class CPSpecificationOptionFacetsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		RenderRequest renderRequest =
			portletSharedSearchSettings.getRenderRequest();

		try {
			SearchContext searchContext =
				portletSharedSearchSettings.getSearchContext();

			SerializableFacet serializableFacet = new SerializableFacet(
				CPField.SPECIFICATION_NAMES, searchContext);

			String[] parameterValues =
				portletSharedSearchSettings.getParameterValues(
					CPField.SPECIFICATION_NAMES);

			if (ArrayUtil.isNotEmpty(parameterValues)) {
				serializableFacet.select(parameterValues);

				searchContext.setAttribute(
					CPField.SPECIFICATION_NAMES, parameterValues);
			}

			PortletPreferences portletPreferences =
				portletSharedSearchSettings.getPortletPreferences();

			int frequencyThreshold = 1;
			int maxSpecifications = 10;
			int maxTerms = 10;

			if (portletPreferences != null) {
				frequencyThreshold = GetterUtil.getInteger(
					portletPreferences.getValue("frequencyThreshold", null), 1);
				maxSpecifications = GetterUtil.getInteger(
					portletPreferences.getValue("maxSpecifications", null), 10);
				maxTerms = GetterUtil.getInteger(
					portletPreferences.getValue("maxTerms", null), 10);
			}

			serializableFacet.setFacetConfiguration(
				_buildFacetConfiguration(
					serializableFacet.getFieldName(), frequencyThreshold,
					maxSpecifications));

			portletSharedSearchSettings.addFacet(serializableFacet);

			for (Facet facet :
					getFacets(
						frequencyThreshold, maxSpecifications, renderRequest)) {

				String cpSpecificationOptionKey =
					CPSpecificationOptionFacetsUtil.
						getCPSpecificationOptionKeyFromIndexFieldName(
							facet.getFieldName());

				parameterValues =
					portletSharedSearchSettings.getParameterValues(
						cpSpecificationOptionKey);

				serializableFacet = new SerializableFacet(
					facet.getFieldName(), searchContext);

				serializableFacet.setFacetConfiguration(
					_buildFacetConfiguration(
						facet.getFieldName(), frequencyThreshold, maxTerms));

				if (ArrayUtil.isNotEmpty(parameterValues)) {
					serializableFacet.select(parameterValues);

					searchContext.setAttribute(
						facet.getFieldName(), parameterValues);
				}

				portletSharedSearchSettings.addFacet(serializableFacet);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	protected SearchContext buildSearchContext(RenderRequest renderRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setLayout(themeDisplay.getLayout());
		searchContext.setLocale(themeDisplay.getLocale());
		searchContext.setTimeZone(themeDisplay.getTimeZone());
		searchContext.setUserId(themeDisplay.getUserId());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setLocale(themeDisplay.getLocale());

		searchContext.setAttribute(CPField.PUBLISHED, Boolean.TRUE);

		if (FeatureFlagManagerUtil.isEnabled("LPD-10889")) {
			CommerceContext commerceContext =
				(CommerceContext)renderRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			searchContext.setAttribute(
				CPField.CP_CONFIGURATION_LIST_IDS,
				commerceContext.getCPConfigurationListIds());
		}
		else {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
					themeDisplay.getScopeGroupId());

			if (commerceChannel != null) {
				searchContext.setAttribute(
					"commerceChannelGroupId", commerceChannel.getGroupId());

				AccountEntry accountEntry =
					_commerceAccountHelper.getCurrentAccountEntry(
						commerceChannel.getGroupId(),
						_portal.getHttpServletRequest(renderRequest));

				if (accountEntry != null) {
					searchContext.setAttribute(
						"accountEntryId", accountEntry.getAccountEntryId());
					searchContext.setAttribute(
						"commerceAccountGroupIds",
						_accountGroupLocalService.getAccountGroupIds(
							accountEntry.getAccountEntryId()));
				}
			}

			searchContext.setAttribute("secure", Boolean.TRUE);
		}

		return searchContext;
	}

	protected List<Facet> getFacets(
			int frequencyThreshold, int maxTerms, RenderRequest renderRequest)
		throws Exception {

		List<Facet> facets = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		AssetCategory assetCategory = (AssetCategory)renderRequest.getAttribute(
			WebKeys.ASSET_CATEGORY);

		SearchContext searchContext = buildSearchContext(renderRequest);

		if (assetCategory != null) {
			searchContext.setAssetCategoryIds(
				new long[] {assetCategory.getCategoryId()});
		}

		Facet facet = new SimpleFacet(searchContext);

		String fieldName = CPField.SPECIFICATION_NAMES;

		facet.setFacetConfiguration(
			_buildFacetConfiguration(fieldName, frequencyThreshold, maxTerms));
		facet.setFieldName(fieldName);

		searchContext.addFacet(facet);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.addSelectedFieldNames(fieldName);
		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		indexer.search(searchContext);

		FacetCollector facetCollector = facet.getFacetCollector();

		for (TermCollector termCollector : facetCollector.getTermCollectors()) {
			CPSpecificationOption cpSpecificationOption =
				_cpSpecificationOptionLocalService.getCPSpecificationOption(
					searchContext.getCompanyId(), termCollector.getTerm());

			if (cpSpecificationOption.isFacetable()) {
				MultiValueFacet multiValueFacet = new MultiValueFacet(
					searchContext);

				multiValueFacet.setFieldName(
					CPSpecificationOptionFacetsUtil.getIndexFieldName(
						termCollector.getTerm(), themeDisplay.getLanguageId()));

				facets.add(multiValueFacet);
			}
		}

		return facets;
	}

	private FacetConfiguration _buildFacetConfiguration(
		String fieldName, int frequencyThreshold, int maxTerms) {

		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setFieldName(fieldName);
		facetConfiguration.setLabel("any-category");
		facetConfiguration.setOrder("OrderHitsDesc");
		facetConfiguration.setStatic(false);
		facetConfiguration.setWeight(1.6);

		JSONObject jsonObject = facetConfiguration.getData();

		jsonObject.put(
			"frequencyThreshold", frequencyThreshold
		).put(
			"maxTerms", maxTerms
		);

		return facetConfiguration;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionFacetsPortletSharedSearchContributor.class);

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	@Reference
	private Portal _portal;

}