/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.commerce.order.rule.service.COREntryRelLocalService;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleAccount;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 * @author Stefano Motta
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class OrderRuleAccountResourceTest
	extends BaseOrderRuleAccountResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_corEntry = _corEntryLocalService.addCOREntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(), 1, 1,
			2022, 12, 0, 0, 0, 0, 0, 0, true, RandomTestUtil.randomString(), 0,
			RandomTestUtil.randomString(), StringPool.BLANK, _serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteOrderRuleAccount() throws Exception {
		super.testDeleteOrderRuleAccount();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteOrderRuleAccountBatch() throws Exception {
		super.testDeleteOrderRuleAccountBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOrderRuleAccount() throws Exception {
		super.testGraphQLDeleteOrderRuleAccount();
	}

	@Override
	protected OrderRuleAccount randomOrderRuleAccount() throws Exception {
		User guestUser = testCompany.getGuestUser();

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, guestUser.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(), "business", 1, _serviceContext);

		return new OrderRuleAccount() {
			{
				accountExternalReferenceCode =
					accountEntry.getExternalReferenceCode();
				accountId = accountEntry.getAccountEntryId();
				orderRuleAccountId = RandomTestUtil.randomLong();
				orderRuleExternalReferenceCode =
					_corEntry.getExternalReferenceCode();
				orderRuleId = _corEntry.getCOREntryId();
			}
		};
	}

	@Override
	protected OrderRuleAccount
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_addOrderRuleAccount(
				String externalReferenceCode, OrderRuleAccount orderRuleAccount)
		throws Exception {

		return _addCOREntryRel(orderRuleAccount);
	}

	@Override
	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getExternalReferenceCode()
		throws Exception {

		return _corEntry.getExternalReferenceCode();
	}

	@Override
	protected OrderRuleAccount
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				Long id, OrderRuleAccount orderRuleAccount)
		throws Exception {

		return _addCOREntryRel(orderRuleAccount);
	}

	@Override
	protected Long testGetOrderRuleIdOrderRuleAccountsPage_getId()
		throws Exception {

		return _corEntry.getCOREntryId();
	}

	@Override
	protected OrderRuleAccount
			testPostOrderRuleByExternalReferenceCodeOrderRuleAccount_addOrderRuleAccount(
				OrderRuleAccount orderRuleAccount)
		throws Exception {

		return _addCOREntryRel(orderRuleAccount);
	}

	@Override
	protected OrderRuleAccount
			testPostOrderRuleIdOrderRuleAccount_addOrderRuleAccount(
				OrderRuleAccount orderRuleAccount)
		throws Exception {

		return _addCOREntryRel(orderRuleAccount);
	}

	private OrderRuleAccount _addCOREntryRel(OrderRuleAccount orderRuleAccount)
		throws Exception {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			orderRuleAccount.getAccountId());
		COREntryRel corEntryRel = _corEntryRelLocalService.addCOREntryRel(
			_user.getUserId(), AccountEntry.class.getName(),
			orderRuleAccount.getAccountId(), orderRuleAccount.getOrderRuleId());

		return new OrderRuleAccount() {
			{
				accountExternalReferenceCode =
					accountEntry.getExternalReferenceCode();
				accountId = accountEntry.getAccountEntryId();
				orderRuleAccountId = corEntryRel.getCOREntryRelId();
				orderRuleExternalReferenceCode =
					_corEntry.getExternalReferenceCode();
				orderRuleId = _corEntry.getCOREntryId();
			}
		};
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private COREntry _corEntry;

	@Inject
	private COREntryLocalService _corEntryLocalService;

	@Inject
	private COREntryRelLocalService _corEntryRelLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}