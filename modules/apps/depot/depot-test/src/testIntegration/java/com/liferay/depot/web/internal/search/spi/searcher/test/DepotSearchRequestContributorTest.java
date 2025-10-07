/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.search.spi.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.spi.searcher.SearchRequestContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gustavo Lima
 */
@RunWith(Arquillian.class)
public class DepotSearchRequestContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
	}

	@Test
	public void testContributeWithConnectedGroupId() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group1.getGroupId());

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.groupIds(_group1.getGroupId());

		SearchRequest searchRequest = searchRequestBuilder.build();

		_depotSearchRequestContributor.contribute(searchRequest);

		searchRequestBuilder = _searchRequestBuilderFactory.builder(
			searchRequest);

		SearchContext searchContext = searchRequestBuilder.withSearchContextGet(
			Function.identity());

		Assert.assertEquals(
			Arrays.toString(searchContext.getGroupIds()),
			Arrays.toString(
				new long[] {_group1.getGroupId(), depotEntry.getGroupId()}),
			Arrays.toString(searchContext.getGroupIds()));
	}

	@Test
	public void testContributeWithConnectedGroupIdWithSeveralDepotEntries()
		throws Exception {

		DepotEntry depotEntry1 = _addDepotEntry();
		DepotEntry depotEntry2 = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry1.getDepotEntryId(), _group1.getGroupId());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry2.getDepotEntryId(), _group1.getGroupId());

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.groupIds(_group1.getGroupId());

		SearchRequest searchRequest = searchRequestBuilder.build();

		_depotSearchRequestContributor.contribute(searchRequest);

		searchRequestBuilder = _searchRequestBuilderFactory.builder(
			searchRequest);

		SearchContext searchContext = searchRequestBuilder.withSearchContextGet(
			Function.identity());

		Assert.assertEquals(
			Arrays.toString(searchContext.getGroupIds()),
			Arrays.toString(
				new long[] {
					_group1.getGroupId(), depotEntry1.getGroupId(),
					depotEntry2.getGroupId()
				}),
			Arrays.toString(searchContext.getGroupIds()));
	}

	@Test
	public void testContributeWithMultipleConnectedGroupIds() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry();
		DepotEntry depotEntry2 = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry1.getDepotEntryId(), _group1.getGroupId());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry2.getDepotEntryId(), _group2.getGroupId());

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.groupIds(
			_group1.getGroupId(), _group2.getGroupId());

		SearchRequest searchRequest = searchRequestBuilder.build();

		_depotSearchRequestContributor.contribute(searchRequest);

		searchRequestBuilder = _searchRequestBuilderFactory.builder(
			searchRequest);

		SearchContext searchContext = searchRequestBuilder.withSearchContextGet(
			Function.identity());

		Assert.assertEquals(
			Arrays.toString(searchContext.getGroupIds()),
			Arrays.toString(
				new long[] {
					_group1.getGroupId(), _group2.getGroupId(),
					depotEntry1.getGroupId(), depotEntry2.getGroupId()
				}),
			Arrays.toString(searchContext.getGroupIds()));
	}

	@Test
	public void testContributeWithNoGroupIds() throws Exception {
		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		SearchRequest searchRequest = searchRequestBuilder.build();

		_depotSearchRequestContributor.contribute(searchRequest);

		searchRequestBuilder = _searchRequestBuilderFactory.builder(
			searchRequest);

		SearchContext searchContext = searchRequestBuilder.withSearchContextGet(
			Function.identity());

		Assert.assertNull(searchContext.getGroupIds());
	}

	@Test
	public void testContributeWithNotConnectedGroupId() throws Exception {
		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.groupIds(_group1.getGroupId());

		SearchRequest searchRequest = searchRequestBuilder.build();

		_depotSearchRequestContributor.contribute(searchRequest);

		searchRequestBuilder = _searchRequestBuilderFactory.builder(
			searchRequest);

		SearchContext searchContext = searchRequestBuilder.withSearchContextGet(
			Function.identity());

		Assert.assertEquals(
			Arrays.toString(searchContext.getGroupIds()),
			Arrays.toString(new long[] {_group1.getGroupId()}),
			Arrays.toString(searchContext.getGroupIds()));
	}

	@Test
	public void testContributeWithNotSearchableConnectedGroupId()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group1.getGroupId(), false);

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.groupIds(_group2.getGroupId());

		SearchRequest searchRequest = searchRequestBuilder.build();

		_depotSearchRequestContributor.contribute(searchRequest);

		searchRequestBuilder = _searchRequestBuilderFactory.builder(
			searchRequest);

		SearchContext searchContext = searchRequestBuilder.withSearchContextGet(
			Function.identity());

		Assert.assertEquals(
			Arrays.toString(searchContext.getGroupIds()),
			Arrays.toString(new long[] {_group2.getGroupId()}),
			Arrays.toString(searchContext.getGroupIds()));
	}

	private DepotEntry _addDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(filter = "search.request.contributor.id=com.liferay.depot")
	private SearchRequestContributor _depotSearchRequestContributor;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}