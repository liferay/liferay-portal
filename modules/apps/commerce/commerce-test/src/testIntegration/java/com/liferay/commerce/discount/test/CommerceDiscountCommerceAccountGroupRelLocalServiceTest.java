/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.test;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
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

import java.math.BigDecimal;

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
public class CommerceDiscountCommerceAccountGroupRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_accountGroup = _accountGroupLocalService.addAccountGroup(
			null, TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
		_commerceDiscount = _commerceDiscountLocalService.addCommerceDiscount(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			CommerceDiscountConstants.TARGET_PRODUCTS, false, null, true,
			BigDecimal.ONE, CommerceDiscountConstants.LEVEL_L1, BigDecimal.ONE,
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
			CommerceDiscountConstants.TYPE_PERCENTAGE, 0, false, true, 1, 1,
			2020, 1, 1, 0, 0, 0, 0, 0, true,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testDeleteAccountGroup() throws Exception {
		_commerceDiscountCommerceAccountGroupRelLocalService.
			addCommerceDiscountCommerceAccountGroupRel(
				TestPropsValues.getUserId(),
				_commerceDiscount.getCommerceDiscountId(),
				_accountGroup.getAccountGroupId(),
				ServiceContextTestUtil.getServiceContext());

		_assertSearchCommerceAccountGroupIds(
			ListUtil.fromArray(_accountGroup.getAccountGroupId()));

		_accountGroupLocalService.deleteAccountGroup(_accountGroup);

		_assertSearchCommerceAccountGroupIds(Collections.emptyList());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertSearchCommerceAccountGroupIds(
		List<Long> expectedCommerceAccountGroupIds) {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_commerceDiscount.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				"commerceAccountGroupIds",
				"commerceAccountGroupIds_required_matches"
			).modelIndexerClasses(
				CommerceDiscount.class
			).query(
				QueriesUtil.term(
					Field.ENTRY_CLASS_PK,
					_commerceDiscount.getCommerceDiscountId())
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					"skipCommerceAccountGroupValidation", Boolean.TRUE)
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

	private AccountGroup _accountGroup;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	private CommerceDiscount _commerceDiscount;

	@Inject
	private CommerceDiscountCommerceAccountGroupRelLocalService
		_commerceDiscountCommerceAccountGroupRelLocalService;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}