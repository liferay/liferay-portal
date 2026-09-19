/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.highlight.FieldConfigBuilderFactory;
import com.liferay.portal.search.highlight.HighlightBuilder;
import com.liferay.portal.search.highlight.HighlightBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Joshua Cords
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
public abstract class BaseSearchRequestBuilderHighlightTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		serviceContext = ServiceContextTestUtil.getServiceContext(
			group, TestPropsValues.getUserId());
	}

	@Test
	public void testSearchWithHighlight() throws Exception {
		addModels(
			"alpha", "alpha beta", "alpha beta alpha",
			"alpha beta gamma alpha eta theta alpha zeta eta alpha iota",
			"alpha beta gamma delta epsilon zeta eta theta iota alpha");

		List<String> fieldNames = Arrays.asList(getFieldNames());

		HighlightBuilder highlightBuilder = _highlightBuilderFactory.builder();

		fieldNames.forEach(
			fieldName -> highlightBuilder.addFieldConfig(
				_fieldConfigBuilderFactory.builder(
					fieldName
				).fragmentSize(
					20
				).postTags(
					"[/H]"
				).preTags(
					"[H]"
				).build()));

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				group.getCompanyId()
			).groupIds(
				group.getGroupId()
			).queryString(
				"alpha"
			).highlight(
				highlightBuilder.build()
			);

		_assertSearch(
			Arrays.asList(
				"[H]alpha[/H]", "[H]alpha[/H] beta",
				"[H]alpha[/H] beta [H]alpha[/H]",
				"[H]alpha[/H] beta gamma [H]alpha[/H]...eta theta [H]alpha" +
					"[/H] zeta...eta [H]alpha[/H] iota",
				"[H]alpha[/H] beta gamma delta...zeta eta theta iota [H]alpha" +
					"[/H]"),
			getFieldNames(), searchRequestBuilder);
	}

	@Test
	public void testSearchWithHighlightEnabled() throws Exception {
		addModels("alpha beta", "alpha beta alpha");

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				group.getCompanyId()
			).groupIds(
				group.getGroupId()
			).queryString(
				"alpha"
			).highlightEnabled(
				true
			);

		_assertSearch(
			Arrays.asList(
				"<liferay-hl>alpha</liferay-hl> beta",
				"<liferay-hl>alpha</liferay-hl> beta " +
					"<liferay-hl>alpha</liferay-hl>"),
			getFieldNames(), searchRequestBuilder);
	}

	@Test
	public void testSingleEntryClassSearchWithHighlightEnabled()
		throws Exception {

		addModels("alpha beta");

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				group.getCompanyId()
			).entryClassNames(
				getBaseModelClassName()
			).groupIds(
				group.getGroupId()
			).queryString(
				"alpha"
			).highlightEnabled(
				true
			);

		_assertSearch(
			Arrays.asList("<liferay-hl>alpha</liferay-hl> beta"),
			getFieldNames(), searchRequestBuilder);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected abstract void addModels(String... titles) throws Exception;

	protected abstract Class<?> getBaseModelClass();

	protected String getBaseModelClassName() {
		Class<?> clazz = getBaseModelClass();

		return clazz.getName();
	}

	protected abstract String[] getFieldNames();

	@DeleteAfterTestRun
	protected Group group;

	protected ServiceContext serviceContext;

	private void _assertSearch(
		List<String> expected, String[] fieldNames,
		SearchRequestBuilder searchRequestBuilder) {

		if (!_isElasticsearch()) {
			return;
		}

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		Hits hits = searchResponse.withHitsGet(Function.identity());

		for (String fieldName : fieldNames) {
			DocumentsAssert.assertValuesIgnoreRelevance(
				searchResponse.getRequestString(), hits.getDocs(),
				"snippet_" + fieldName, expected);
		}
	}

	private boolean _isElasticsearch() {
		return Objects.equals(_searchEngine.getVendor(), "Elasticsearch");
	}

	@Inject
	private FieldConfigBuilderFactory _fieldConfigBuilderFactory;

	@Inject
	private HighlightBuilderFactory _highlightBuilderFactory;

	@Inject
	private SearchEngine _searchEngine;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}