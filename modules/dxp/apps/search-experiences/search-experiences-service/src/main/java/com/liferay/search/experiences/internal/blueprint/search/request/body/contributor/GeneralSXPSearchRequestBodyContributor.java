/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.search.request.body.contributor;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.asset.AssetSubtypeIdentifier;
import com.liferay.portal.search.asset.AssetSubtypeIdentifierBuilder;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.search.experiences.internal.blueprint.parameter.SXPParameterData;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.GeneralConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author André de Oliveira
 */
public class GeneralSXPSearchRequestBodyContributor
	implements SXPSearchRequestBodyContributor {

	public GeneralSXPSearchRequestBodyContributor(
		AssetSubtypeIdentifierBuilder assetSubtypeIdentifierBuilder,
		ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory,
		Queries queries) {

		_assetSubtypeIdentifierBuilder = assetSubtypeIdentifierBuilder;
		_complexQueryPartBuilderFactory = complexQueryPartBuilderFactory;
		_queries = queries;
	}

	@Override
	public void contribute(
		Configuration configuration, SearchRequestBuilder searchRequestBuilder,
		SXPParameterData sxpParameterData) {

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		if (generalConfiguration == null) {
			return;
		}

		if (ArrayUtil.isNotEmpty(
				generalConfiguration.getClauseContributorsExcludes())) {

			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setAttribute(
					"search.full.query.clause.contributors.excludes",
					StringUtil.merge(
						generalConfiguration.getClauseContributorsExcludes())));
		}

		if (ArrayUtil.isNotEmpty(
				generalConfiguration.getClauseContributorsIncludes())) {

			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setAttribute(
					"search.full.query.clause.contributors.includes",
					StringUtil.merge(
						generalConfiguration.getClauseContributorsIncludes())));
		}

		if (generalConfiguration.getEmptySearchEnabled() != null) {
			searchRequestBuilder.emptySearchEnabled(
				generalConfiguration.getEmptySearchEnabled());
		}

		if (generalConfiguration.getExplain() != null) {
			searchRequestBuilder.explain(generalConfiguration.getExplain());
		}

		if (generalConfiguration.getIncludeResponseString() != null) {
			searchRequestBuilder.includeResponseString(
				generalConfiguration.getIncludeResponseString());
		}

		if (!Validator.isBlank(generalConfiguration.getQueryString())) {
			searchRequestBuilder.queryString(
				generalConfiguration.getQueryString());
		}

		if (ArrayUtil.isNotEmpty(
				generalConfiguration.getSearchableAssetTypes())) {

			String[] searchableAssetTypes =
				generalConfiguration.getSearchableAssetTypes();

			HashMap<String, List<AssetSubtypeIdentifier>>
				assetSubtypeIdentifiersMap = new HashMap<>();

			Set<String> classNamesSet = new HashSet<>();

			for (String searchableAssetType : searchableAssetTypes) {
				AssetSubtypeIdentifier assetSubtypeIdentifier =
					_assetSubtypeIdentifierBuilder.searchableAssetType(
						searchableAssetType
					).build();

				String className = assetSubtypeIdentifier.getClassName();

				classNamesSet.add(className);

				if (assetSubtypeIdentifier.getSubtypeExternalReferenceCode() ==
						null) {

					continue;
				}

				List<AssetSubtypeIdentifier> assetSubtypeIdentifiers =
					assetSubtypeIdentifiersMap.get(className);

				if (assetSubtypeIdentifiers == null) {
					assetSubtypeIdentifiers = new ArrayList<>();
				}

				assetSubtypeIdentifiers.add(assetSubtypeIdentifier);

				assetSubtypeIdentifiersMap.put(
					className, assetSubtypeIdentifiers);
			}

			String[] classNames = classNamesSet.toArray(new String[0]);

			searchRequestBuilder.entryClassNames(classNames);
			searchRequestBuilder.modelIndexerClassNames(classNames);

			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setAttribute(
					"assetSubtypeIdentifiersMap", assetSubtypeIdentifiersMap));
		}

		if (ArrayUtil.isNotEmpty(generalConfiguration.getScope())) {
			TermsQuery termsQuery = _queries.terms(
				"scopeGroupExternalReferenceCode");

			termsQuery.addValues((Object[])generalConfiguration.getScope());

			searchRequestBuilder.addComplexQueryPart(
				_complexQueryPartBuilderFactory.builder(
				).query(
					termsQuery
				).build());
		}

		if (!Validator.isBlank(generalConfiguration.getLanguageId())) {
			searchRequestBuilder.locale(
				LocaleUtil.fromLanguageId(
					generalConfiguration.getLanguageId()));
		}

		if (!Validator.isBlank(generalConfiguration.getTimeZoneId())) {
			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setTimeZone(
					TimeZoneUtil.getTimeZone(
						generalConfiguration.getTimeZoneId())));
		}
	}

	@Override
	public String getName() {
		return "generalConfiguration";
	}

	private final AssetSubtypeIdentifierBuilder _assetSubtypeIdentifierBuilder;
	private final ComplexQueryPartBuilderFactory
		_complexQueryPartBuilderFactory;
	private final Queries _queries;

}