/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.link;

import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.IncludeExcludeClause;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.util.CMSOutboundLinksUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class BrokenLinkAssetSearcherTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-108685")
	public void testGetBrokenLinkTargetsMap() {
		List<String> outboundLinks = TransformUtil.transform(
			Arrays.asList(
				"a0-deleted", "a1-deleted", "a2-deleted", "a3-deleted",
				"b0-existing", "c0-deleted", "CUSTOM-deleted"),
			CMSOutboundLinksUtil::getObjectEntryExternalReferenceCodeToken);

		List<IncludeExcludeClause> includeExcludeClauses = new ArrayList<>();

		BrokenLinkAssetSearcher brokenLinkAssetSearcher =
			new BrokenLinkAssetSearcher(
				_getAggregations(includeExcludeClauses),
				_getObjectEntryLocalService(),
				_getSearcher(includeExcludeClauses, outboundLinks),
				_getSearchRequestBuilderFactory(
					Mockito.mock(
						SearchRequestBuilder.class, Mockito.RETURNS_SELF)));

		Long[] spaceGroupIds = {RandomTestUtil.randomLong()};

		Map<String, BrokenLinkTarget> brokenLinkTargetsMap =
			brokenLinkAssetSearcher.getBrokenLinkTargetsMap(
				RandomTestUtil.randomLong(),
				new Long[] {RandomTestUtil.randomLong()}, spaceGroupIds,
				spaceGroupIds);

		Assert.assertEquals(
			new TreeSet<>(
				TransformUtil.transform(
					Arrays.asList(
						"a0-deleted", "a1-deleted", "a2-deleted", "a3-deleted",
						"c0-deleted", "CUSTOM-deleted"),
					CMSOutboundLinksUtil::
						getObjectEntryExternalReferenceCodeToken)),
			new TreeSet<>(brokenLinkTargetsMap.keySet()));

		Assert.assertEquals(
			includeExcludeClauses.toString(), 35, includeExcludeClauses.size());
	}

	@Test
	@TestInfo("LPD-109248")
	public void testSearch() {
		_setUpLocalizationUtil();

		SearchRequestBuilder searchRequestBuilder = Mockito.mock(
			SearchRequestBuilder.class, Mockito.RETURNS_SELF);

		BrokenLinkAssetSearcher brokenLinkAssetSearcher =
			new BrokenLinkAssetSearcher(
				Mockito.mock(Aggregations.class),
				Mockito.mock(ObjectEntryLocalService.class),
				Mockito.mock(Searcher.class),
				_getSearchRequestBuilderFactory(searchRequestBuilder));

		brokenLinkAssetSearcher.search(
			RandomTestUtil.randomLong(),
			new Long[] {RandomTestUtil.randomLong()},
			RandomTestUtil.randomString(),
			Collections.singleton(RandomTestUtil.randomString()),
			Pagination.of(501, 20), null, null);

		Mockito.verify(
			searchRequestBuilder
		).from(
			10000
		);

		Mockito.verify(
			searchRequestBuilder
		).size(
			20
		);
	}

	private Aggregations _getAggregations(
		List<IncludeExcludeClause> includeExcludeClauses) {

		Aggregations aggregations = Mockito.mock(Aggregations.class);

		Mockito.when(
			aggregations.terms(Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			invocation -> {
				TermsAggregation termsAggregation = Mockito.mock(
					TermsAggregation.class);

				Mockito.doAnswer(
					setIncludeExcludeClauseInvocation -> {
						includeExcludeClauses.add(
							setIncludeExcludeClauseInvocation.getArgument(0));

						return null;
					}
				).when(
					termsAggregation
				).setIncludeExcludeClause(
					Mockito.any()
				);

				return termsAggregation;
			}
		);

		return aggregations;
	}

	private ObjectEntryLocalService _getObjectEntryLocalService() {
		ObjectEntryLocalService objectEntryLocalService = Mockito.mock(
			ObjectEntryLocalService.class);

		Mockito.when(
			objectEntryLocalService.dslQuery(Mockito.any())
		).thenReturn(
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList(), Collections.singletonList("b0-existing")
		);

		return objectEntryLocalService;
	}

	private SearchRequestBuilderFactory _getSearchRequestBuilderFactory(
		SearchRequestBuilder searchRequestBuilder) {

		SearchRequestBuilderFactory searchRequestBuilderFactory = Mockito.mock(
			SearchRequestBuilderFactory.class);

		Mockito.when(
			searchRequestBuilderFactory.builder()
		).thenReturn(
			searchRequestBuilder
		);

		return searchRequestBuilderFactory;
	}

	private Searcher _getSearcher(
		List<IncludeExcludeClause> includeExcludeClauses,
		List<String> outboundLinks) {

		Searcher searcher = Mockito.mock(Searcher.class);

		Mockito.when(
			searcher.search(Mockito.any())
		).thenAnswer(
			invocation -> {
				IncludeExcludeClause includeExcludeClause =
					includeExcludeClauses.get(includeExcludeClauses.size() - 1);

				List<Bucket> buckets = new ArrayList<>();

				for (String outboundLink : outboundLinks) {
					if (outboundLink.matches(
							includeExcludeClause.getIncludeRegex()) &&
						((includeExcludeClause.getExcludeRegex() == null) ||
						 !outboundLink.matches(
							 includeExcludeClause.getExcludeRegex()))) {

						buckets.add(
							new Bucket(
								outboundLink, RandomTestUtil.randomLong()));
					}
				}

				TermsAggregationResult termsAggregationResult = Mockito.mock(
					TermsAggregationResult.class);

				Mockito.when(
					termsAggregationResult.getBuckets()
				).thenReturn(
					buckets.subList(0, Math.min(buckets.size(), _SIZE))
				);

				Mockito.when(
					termsAggregationResult.getOtherDocCounts()
				).thenReturn(
					(long)Math.max(buckets.size() - _SIZE, 0)
				);

				SearchResponse searchResponse = Mockito.mock(
					SearchResponse.class);

				Mockito.when(
					searchResponse.getAggregationResult("outboundLinks")
				).thenReturn(
					termsAggregationResult
				);

				return searchResponse;
			}
		);

		return searcher;
	}

	private void _setUpLocalizationUtil() {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(Mockito.mock(Localization.class));
	}

	private static final int _SIZE = 3;

}