/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.data.source.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
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

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Fabio Monaco
 */
@RunWith(Arquillian.class)
public class AssetCategoriesAndTagsCPDataSourceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);
	}

	@Test
	public void testGetResultWithAccountGroupFilterEnabled() throws Exception {
		AccountEntry accountEntry1 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		AccountGroup accountGroup =
			CommerceAccountTestUtil.addAccountGroupAndAccountRel(
				RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
				accountEntry1.getAccountEntryId(), _serviceContext);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableAccountGroupFilter(accountGroup, cpDefinition1);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		CPTestUtil.addCategoryToCPDefinitions(
			_commerceCatalog.getGroupId(), cpDefinition1.getCPDefinitionId(),
			cpDefinition2.getCPDefinitionId());

		CommerceChannel commerceChannel = _addCommerceChannel();

		Assert.assertEquals(
			Collections.singletonList(cpDefinition1.getCPDefinitionId()),
			_getCPDefinitionIds(
				commerceChannel,
				_commerceContextFactory.create(
					accountEntry1.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId()),
				cpDefinition2));

		AccountEntry accountEntry2 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			accountEntry2.getAccountEntryId(), _serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				commerceChannel,
				_commerceContextFactory.create(
					accountEntry2.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId()),
				cpDefinition2));

		AccountEntry accountEntry3 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				commerceChannel,
				_commerceContextFactory.create(
					accountEntry3.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId()),
				cpDefinition2));

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				commerceChannel,
				_commerceContextFactory.create(
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId()),
				cpDefinition2));
		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(commerceChannel, null, cpDefinition2));
	}

	@Test
	public void testGetResultWithChannelFilterEnabled() throws Exception {
		CommerceChannel commerceChannel = _addCommerceChannel();

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableChannelFilter(commerceChannel, cpDefinition1);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		CPDefinition cpDefinition3 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableChannelFilter(_addCommerceChannel(), cpDefinition3);

		CPTestUtil.addCategoryToCPDefinitions(
			_commerceCatalog.getGroupId(), cpDefinition1.getCPDefinitionId(),
			cpDefinition2.getCPDefinitionId(),
			cpDefinition3.getCPDefinitionId());

		Assert.assertEquals(
			Collections.singletonList(cpDefinition1.getCPDefinitionId()),
			_getCPDefinitionIds(commerceChannel, null, cpDefinition2));

		AccountEntry accountEntry1 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		_addEligibleAccountEntry(accountEntry1, commerceChannel);

		Assert.assertEquals(
			Collections.singletonList(cpDefinition1.getCPDefinitionId()),
			_getCPDefinitionIds(
				commerceChannel,
				_commerceContextFactory.create(
					accountEntry1.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId()),
				cpDefinition2));

		AccountEntry accountEntry2 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getUserId()}, null,
				_serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				commerceChannel,
				_commerceContextFactory.create(
					accountEntry2.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId()),
				cpDefinition2));

		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(
				commerceChannel,
				_commerceContextFactory.create(
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
					commerceChannel.getGroupId(), null, 0,
					commerceChannel.getCompanyId()),
				cpDefinition2));
		Assert.assertEquals(
			Collections.emptyList(),
			_getCPDefinitionIds(commerceChannel, null, cpDefinition2));
	}

	private CommerceChannel _addCommerceChannel() throws Exception {
		Group group = GroupTestUtil.addGroup();

		return CommerceTestUtil.addCommerceChannel(group.getGroupId(), null);
	}

	private void _addEligibleAccountEntry(
			AccountEntry accountEntry, CommerceChannel commerceChannel)
		throws Exception {

		_commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				TestPropsValues.getUserId(), accountEntry.getAccountEntryId(),
				AccountEntry.class.getName(), accountEntry.getAccountEntryId(),
				commerceChannel.getCommerceChannelId(), true, 0,
				CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);
	}

	private void _enableAccountGroupFilter(
			AccountGroup accountGroup, CPDefinition cpDefinition)
		throws Exception {

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		cpDefinition.setAccountGroupFilterEnabled(true);

		_cpDefinitionLocalService.updateCPDefinition(cpDefinition);
	}

	private void _enableChannelFilter(
			CommerceChannel commerceChannel, CPDefinition cpDefinition)
		throws Exception {

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			commerceChannel.getCommerceChannelId(), _serviceContext);

		cpDefinition.setChannelFilterEnabled(true);

		_cpDefinitionLocalService.updateCPDefinition(cpDefinition);
	}

	private List<Long> _getCPDefinitionIds(
			CommerceChannel commerceChannel, CommerceContext commerceContext,
			CPDefinition cpDefinition)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT, commerceContext);
		mockHttpServletRequest.setAttribute(
			CPWebKeys.CP_CATALOG_ENTRY,
			_cpDefinitionHelper.getCPCatalogEntry(
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
				cpDefinition.getCPDefinitionId(), LocaleUtil.US, false));
		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, commerceChannel.getCompanyId());
		mockHttpServletRequest.setAttribute(
			WebKeys.LAYOUT,
			LayoutTestUtil.addTypePortletLayout(
				commerceChannel.getSiteGroupId()));

		CPDataSourceResult cpDataSourceResult = _cpDataSource.getResult(
			mockHttpServletRequest, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		return TransformUtil.transform(
			cpDataSourceResult.getCPCatalogEntries(),
			cpCatalogEntry -> cpCatalogEntry.getCPDefinitionId());
	}

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Inject
	private CommerceContextFactory _commerceContextFactory;

	@Inject(
		filter = "commerce.product.data.source.name=assetCategoriesAndTagsDataSource"
	)
	private CPDataSource _cpDataSource;

	@Inject
	private CPDefinitionHelper _cpDefinitionHelper;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private ServiceContext _serviceContext;

}