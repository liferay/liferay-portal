/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.search.spi.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import java.util.Collections;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class SXPBlueprintSearchRequestContributorFederatedTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		_addSXPBlueprint();

		SearchRequestBuilder searchRequestBuilder1 = _getSearchRequestBuilder();
		SearchRequestBuilder searchRequestBuilder2 = _getSearchRequestBuilder();
		SearchRequestBuilder searchRequestBuilder3 = _getSearchRequestBuilder();

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder1.withSearchContext(
				searchContext -> searchContext.setAttribute(
					"search.experiences.blueprint.external.reference.code",
					_sxpBlueprint.getExternalReferenceCode())
			).addFederatedSearchRequest(
				searchRequestBuilder2.federatedSearchKey(
					"federatedSearchKey1"
				).withSearchContext(
					searchContext -> searchContext.setAttribute(
						"search.experiences.blueprint.external.reference.code",
						_sxpBlueprint.getExternalReferenceCode())
				).build()
			).addFederatedSearchRequest(
				searchRequestBuilder3.federatedSearchKey(
					"federatedSearchKey2"
				).withSearchContext(
					searchContext -> searchContext.setAttribute(
						"search.experiences.blueprint.external.reference.code",
						_sxpBlueprint.getExternalReferenceCode())
				).build()
			).build());

		String query = "jedi";

		_assert(searchResponse, query);
		_assert(
			searchResponse.getFederatedSearchResponse("federatedSearchKey1"),
			query);
		_assert(
			searchResponse.getFederatedSearchResponse("federatedSearchKey2"),
			query);
	}

	@Rule
	public TestName testName = new TestName();

	private void _addSXPBlueprint() throws Exception {
		_group = GroupTestUtil.addGroup();

		Class<?> clazz = getClass();

		_sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			null, TestPropsValues.getUserId(),
			StringUtil.read(
				clazz,
				StringBundler.concat(
					"dependencies/", clazz.getSimpleName(), StringPool.PERIOD,
					testName.getMethodName(), ".json")),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			"", "",
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private void _assert(SearchResponse searchResponse, String value) {
		Assert.assertThat(
			searchResponse.getRequestString(),
			CoreMatchers.containsString(value));
	}

	private SearchRequestBuilder _getSearchRequestBuilder() throws Exception {
		return _searchRequestBuilderFactory.builder(
		).companyId(
			TestPropsValues.getCompanyId()
		).emptySearchEnabled(
			true
		);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	@DeleteAfterTestRun
	private SXPBlueprint _sxpBlueprint;

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

}