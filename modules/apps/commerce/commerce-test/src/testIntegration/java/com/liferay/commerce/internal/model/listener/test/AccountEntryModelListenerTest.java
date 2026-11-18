/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.model.listener.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.exception.CommerceAccountOrdersException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
public class AccountEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany(true);

		_user = UserTestUtil.addCompanyAdminUser(_company);
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		AccountEntry accountEntry = null;
		CommerceOrder commerceOrder = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user);
			SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			Group group = GroupTestUtil.addGroup(
				_company.getCompanyId(), _user.getUserId(),
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_company.getCompanyId(), group.getGroupId(),
					_user.getUserId());

			accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
				_user.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com", serviceContext);

			CommerceCurrency commerceCurrency =
				CommerceCurrencyTestUtil.addCommerceCurrency(
					_company.getCompanyId());

			CommerceChannel commerceChannel =
				_commerceChannelLocalService.addCommerceChannel(
					StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
					group.getGroupId(), RandomTestUtil.randomString(),
					CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
					commerceCurrency.getCode(), serviceContext);

			commerceOrder = _commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), commerceCurrency.getCode(),
				0);

			try {
				_accountEntryLocalService.deleteAccountEntry(accountEntry);

				Assert.fail();
			}
			catch (CommerceAccountOrdersException
						commerceAccountOrdersException) {

				Assert.assertNotNull(commerceAccountOrdersException);
			}
		}

		_companyLocalService.deleteCompany(_company);

		Assert.assertNull(
			_accountEntryLocalService.fetchAccountEntry(
				accountEntry.getAccountEntryId()));
		Assert.assertNull(
			_commerceOrderLocalService.fetchCommerceOrder(
				commerceOrder.getCommerceOrderId()));
		Assert.assertNull(
			_companyLocalService.fetchCompany(_company.getCompanyId()));
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private User _user;

}