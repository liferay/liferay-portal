/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelLocalService;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommercePriceListCommerceAccountGroupRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commercePriceList = CommercePriceListTestUtil.addCommercePriceList(
			_group.getGroupId(), false,
			CommercePriceListConstants.TYPE_PRICE_LIST, 1.0);
	}

	@Test
	public void testDeleteCommercePriceListAccountGroupRelsByCommercePriceListId()
		throws Exception {

		long commerceAccountGroupId = RandomTestUtil.randomLong();

		_commercePriceListCommerceAccountGroupRelLocalService.
			addCommercePriceListCommerceAccountGroupRel(
				TestPropsValues.getUserId(),
				_commercePriceList.getCommercePriceListId(),
				commerceAccountGroupId, 0,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertSearchCommerceAccountGroupIds(
			ListUtil.fromArray(commerceAccountGroupId));

		_commercePriceListCommerceAccountGroupRelLocalService.
			deleteCommercePriceListAccountGroupRelsByCommercePriceListId(
				_commercePriceList.getCommercePriceListId());

		_assertSearchCommerceAccountGroupIds(Collections.emptyList());

		Assert.assertEquals(
			Collections.emptyList(),
			_commercePriceListCommerceAccountGroupRelLocalService.
				getCommercePriceListCommerceAccountGroupRels(
					_commercePriceList.getCommercePriceListId()));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertSearchCommerceAccountGroupIds(
		List<Long> expectedCommerceAccountGroupIds) {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_commercePriceList.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				"commerceAccountGroupIds",
				"commerceAccountGroupIds_required_matches"
			).modelIndexerClasses(
				CommercePriceList.class
			).query(
				QueriesUtil.term(
					Field.ENTRY_CLASS_PK,
					_commercePriceList.getCommercePriceListId())
			).build());

		List<Document> documents = searchResponse.getDocuments();

		Assert.assertEquals(documents.toString(), 1, documents.size());

		Document document = documents.get(0);

		Assert.assertEquals(
			expectedCommerceAccountGroupIds,
			document.getLongs("commerceAccountGroupIds"));
		Assert.assertEquals(
			Long.valueOf(expectedCommerceAccountGroupIds.size()),
			document.getLong("commerceAccountGroupIds_required_matches"));
	}

	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListCommerceAccountGroupRelLocalService
		_commercePriceListCommerceAccountGroupRelLocalService;

	private Group _group;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}