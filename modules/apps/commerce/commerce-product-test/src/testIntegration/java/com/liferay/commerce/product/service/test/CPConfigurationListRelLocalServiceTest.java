/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class CPConfigurationListRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {_user.getUserId()}, null,
			_serviceContext);

		_accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, _serviceContext.getUserId(), null,
			RandomTestUtil.randomString(), _serviceContext);
		_commerceCatalog = _commerceCatalogService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US", _serviceContext);

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		_commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), 1, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), true, _serviceContext);

		int displayDateHour = calendar.get(Calendar.HOUR);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		_cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(), 0, false,
				RandomTestUtil.randomString(), 0D, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), displayDateHour,
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				new ServiceContext());
	}

	@After
	public void tearDown() throws Exception {
		_cpConfigurationListRelLocalService.deleteCPConfigurationListRels(
			_cpConfigurationList.getCPConfigurationListId());
	}

	@Test
	public void testAddAccountEntryCPConfigurationRelList() throws Exception {
		frutillaRule.scenario(
			"Add a Product Configuration List Relationship for Account"
		).given(
			"There is a Product Configuration List and Account"
		).when(
			"A Configuration List Relationship is added"
		).then(
			"The Configuration List Relationship is created"
		);

		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId(),
			_cpConfigurationList.getCPConfigurationListId());

		Assert.assertEquals(
			1,
			_cpConfigurationListRelLocalService.
				getAccountEntryCPConfigurationListRelsCount(
					_cpConfigurationList.getCPConfigurationListId(), null));
	}

	@Test
	public void testAddAccountGroupCPConfigurationRelList() throws Exception {
		frutillaRule.scenario(
			"Add a Product Configuration List Relationship for Account Group"
		).given(
			"There is a Product Configuration List and Account Group"
		).when(
			"A Configuration List Relationship is added"
		).then(
			"The Configuration List Relationship is created"
		);

		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountGroup.class.getName(),
			_accountGroup.getAccountGroupId(),
			_cpConfigurationList.getCPConfigurationListId());

		Assert.assertEquals(
			1,
			_cpConfigurationListRelLocalService.
				getAccountGroupCPConfigurationListRelsCount(
					_cpConfigurationList.getCPConfigurationListId(), null));
	}

	@Test
	public void testAddCommerceOrderTypeCPConfigurationRelList()
		throws Exception {

		frutillaRule.scenario(
			"Add a Product Configuration List Relationship for Order Type"
		).given(
			"There is a Product Configuration List and Order Type"
		).when(
			"A Configuration List Relationship is added"
		).then(
			"The Configuration List Relationship is created"
		);

		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), CommerceOrderType.class.getName(),
			_commerceOrderType.getCommerceOrderTypeId(),
			_cpConfigurationList.getCPConfigurationListId());

		Assert.assertEquals(
			1,
			_cpConfigurationListRelLocalService.
				getCommerceOrderTypeCPConfigurationListRelsCount(
					_cpConfigurationList.getCPConfigurationListId(), null));
	}

	@Test
	public void testDeleteCPConfigurationListRels() throws Exception {
		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountGroup.class.getName(),
			_accountGroup.getAccountGroupId(),
			_cpConfigurationList.getCPConfigurationListId());

		_assertSearchCommerceAccountGroupIds(
			ListUtil.fromArray(_accountGroup.getAccountGroupId()));

		_cpConfigurationListRelLocalService.deleteCPConfigurationListRels(
			AccountGroup.class.getName(),
			_cpConfigurationList.getCPConfigurationListId());

		_assertSearchCommerceAccountGroupIds(Collections.emptyList());

		_cpConfigurationListRelLocalService.addCPConfigurationListRel(
			_user.getUserId(), AccountGroup.class.getName(),
			_accountGroup.getAccountGroupId(),
			_cpConfigurationList.getCPConfigurationListId());

		_assertSearchCommerceAccountGroupIds(
			ListUtil.fromArray(_accountGroup.getAccountGroupId()));

		_cpConfigurationListRelLocalService.deleteCPConfigurationListRels(
			_cpConfigurationList.getCPConfigurationListId());

		_assertSearchCommerceAccountGroupIds(Collections.emptyList());
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertSearchCommerceAccountGroupIds(
		List<Long> expectedCommerceAccountGroupIds) {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_cpConfigurationList.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				"commerceAccountGroupIds"
			).modelIndexerClasses(
				CPConfigurationList.class
			).query(
				QueriesUtil.term(
					CPField.CP_CONFIGURATION_LIST_ID,
					_cpConfigurationList.getCPConfigurationListId())
			).build());

		List<Document> documents = searchResponse.getDocuments();

		Assert.assertEquals(documents.toString(), 1, documents.size());

		Document document = documents.get(0);

		Assert.assertEquals(
			expectedCommerceAccountGroupIds,
			document.getLongs("commerceAccountGroupIds"));
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private AccountGroup _accountGroup;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogService _commerceCatalogService;

	@DeleteAfterTestRun
	private CommerceOrderType _commerceOrderType;

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPConfigurationListRelLocalService
		_cpConfigurationListRelLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	private ServiceContext _serviceContext;
	private User _user;

}