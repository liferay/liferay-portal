/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.commerce.order.rule.service.COREntryRelLocalService;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleAccountGroup;
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
public class OrderRuleAccountGroupResourceTest
	extends BaseOrderRuleAccountGroupResourceTestCase {

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
	public void testDeleteOrderRuleAccountGroup() throws Exception {
		super.testDeleteOrderRuleAccountGroup();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteOrderRuleAccountGroupBatch() throws Exception {
		super.testDeleteOrderRuleAccountGroupBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOrderRuleAccountGroup() throws Exception {
		super.testGraphQLDeleteOrderRuleAccountGroup();
	}

	@Override
	protected OrderRuleAccountGroup randomOrderRuleAccountGroup()
		throws Exception {

		OrderRuleAccountGroup orderRuleAccountGroup =
			new OrderRuleAccountGroup() {
				{
					orderRuleAccountGroupId = RandomTestUtil.randomLong();
					orderRuleExternalReferenceCode =
						_corEntry.getExternalReferenceCode();
					orderRuleId = _corEntry.getCOREntryId();
				}
			};

		AccountGroup accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, _user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _serviceContext);

		orderRuleAccountGroup.setAccountGroupId(
			accountGroup.getAccountGroupId());
		orderRuleAccountGroup.setAccountGroupExternalReferenceCode(
			accountGroup.getExternalReferenceCode());

		return orderRuleAccountGroup;
	}

	@Override
	protected OrderRuleAccountGroup
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				String externalReferenceCode,
				OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		return _addCOREntryRel(orderRuleAccountGroup);
	}

	@Override
	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		return _corEntry.getExternalReferenceCode();
	}

	@Override
	protected OrderRuleAccountGroup
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				Long id, OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		return _addCOREntryRel(orderRuleAccountGroup);
	}

	@Override
	protected Long testGetOrderRuleIdOrderRuleAccountGroupsPage_getId()
		throws Exception {

		return _corEntry.getCOREntryId();
	}

	@Override
	protected OrderRuleAccountGroup
			testPostOrderRuleByExternalReferenceCodeOrderRuleAccountGroup_addOrderRuleAccountGroup(
				OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		return _addCOREntryRel(orderRuleAccountGroup);
	}

	@Override
	protected OrderRuleAccountGroup
			testPostOrderRuleIdOrderRuleAccountGroup_addOrderRuleAccountGroup(
				OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		return _addCOREntryRel(orderRuleAccountGroup);
	}

	private OrderRuleAccountGroup _addCOREntryRel(
			OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		COREntryRel corEntryRel = _corEntryRelLocalService.addCOREntryRel(
			_user.getUserId(), AccountGroup.class.getName(),
			orderRuleAccountGroup.getAccountGroupId(),
			orderRuleAccountGroup.getOrderRuleId());

		AccountGroup accountGroup1 = _accountGroupLocalService.getAccountGroup(
			corEntryRel.getClassPK());

		return new OrderRuleAccountGroup() {
			{
				accountGroupExternalReferenceCode =
					accountGroup1.getExternalReferenceCode();
				accountGroupId = accountGroup1.getAccountGroupId();
				orderRuleAccountGroupId = corEntryRel.getCOREntryRelId();
				orderRuleExternalReferenceCode =
					_corEntry.getExternalReferenceCode();
				orderRuleId = _corEntry.getCOREntryId();
			}
		};
	}

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	private COREntry _corEntry;

	@Inject
	private COREntryLocalService _corEntryLocalService;

	@Inject
	private COREntryRelLocalService _corEntryRelLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}