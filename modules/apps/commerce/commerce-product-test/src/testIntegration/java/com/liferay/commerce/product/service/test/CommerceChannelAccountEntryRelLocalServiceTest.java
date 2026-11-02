/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.exception.DuplicateCommerceChannelAccountEntryRelException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CommerceChannelAccountEntryRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_user.getCompanyId());
		_group = GroupTestUtil.addGroup(
			_user.getCompanyId(), _user.getUserId(), 0);

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_user.getCompanyId(), _group.getGroupId(), _user.getUserId());
	}

	@Test
	public void testAddCommerceChannelAccountEntryRel() throws Exception {
		AccountEntry accountEntry = _addBusinessAccountEntry();
		User user = UserTestUtil.addUser(
			RandomTestUtil.randomString(), _group.getGroupId());

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_addCommerceChannelAccountEntryRel(accountEntry, user);

		Assert.assertEquals(
			accountEntry.getAccountEntryId(),
			commerceChannelAccountEntryRel.getAccountEntryId());
		Assert.assertEquals(
			user.getUserId(), commerceChannelAccountEntryRel.getClassPK());
		Assert.assertEquals(
			CommerceChannelAccountEntryRelConstants.TYPE_USER,
			commerceChannelAccountEntryRel.getType());

		Assert.assertThrows(
			DuplicateCommerceChannelAccountEntryRelException.class,
			() -> _addCommerceChannelAccountEntryRel(accountEntry, user));
	}

	@Test
	public void testDeleteCommerceChannelAccountEntryRel() throws Exception {
		AccountEntry accountEntry = _addBusinessAccountEntry();
		User user = UserTestUtil.addUser(
			RandomTestUtil.randomString(), _group.getGroupId());

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_addCommerceChannelAccountEntryRel(accountEntry, user);

		List<Long> accountEntryIds = _getAccountEntryIds(user);

		Assert.assertTrue(
			accountEntryIds.contains(accountEntry.getAccountEntryId()));

		_commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRel.
					getCommerceChannelAccountEntryRelId());

		accountEntryIds = _getAccountEntryIds(user);

		Assert.assertTrue(accountEntryIds.isEmpty());
	}

	@Test
	public void testGetCommerceChannelAccountEntryRels() throws Exception {
		AccountEntry account1 = _addBusinessAccountEntry();
		User user1 = UserTestUtil.addUser(
			RandomTestUtil.randomString(), _group.getGroupId());

		_addCommerceChannelAccountEntryRel(account1, user1);

		User user2 = UserTestUtil.addUser(
			RandomTestUtil.randomString(), _group.getGroupId());

		_addCommerceChannelAccountEntryRel(account1, user2);

		AccountEntry account2 = _addBusinessAccountEntry();

		_addCommerceChannelAccountEntryRel(account2, user1);
		_addCommerceChannelAccountEntryRel(account2, user2);

		AccountEntry account3 = _addBusinessAccountEntry();

		List<Long> accountEntryIds = _getAccountEntryIds(user1);

		Assert.assertEquals(
			accountEntryIds.toString(), 2, accountEntryIds.size());

		Assert.assertTrue(
			accountEntryIds.contains(account1.getAccountEntryId()));
		Assert.assertTrue(
			accountEntryIds.contains(account2.getAccountEntryId()));
		Assert.assertFalse(
			accountEntryIds.contains(account3.getAccountEntryId()));

		accountEntryIds = _getAccountEntryIds(user2);

		Assert.assertEquals(
			accountEntryIds.toString(), 2, accountEntryIds.size());

		Assert.assertTrue(
			accountEntryIds.contains(account1.getAccountEntryId()));
		Assert.assertTrue(
			accountEntryIds.contains(account2.getAccountEntryId()));
		Assert.assertFalse(
			accountEntryIds.contains(account3.getAccountEntryId()));
	}

	@Test
	public void testUpdateCommerceChannelAccountEntryRel() throws Exception {
		AccountEntry accountEntry = _addBusinessAccountEntry();
		User user1 = UserTestUtil.addUser(
			RandomTestUtil.randomString(), _group.getGroupId());

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_addCommerceChannelAccountEntryRel(accountEntry, user1);

		List<Long> accountEntryIds = _getAccountEntryIds(user1);

		Assert.assertTrue(
			accountEntryIds.contains(accountEntry.getAccountEntryId()));

		User user2 = UserTestUtil.addUser(
			RandomTestUtil.randomString(), _group.getGroupId());

		accountEntryIds = _getAccountEntryIds(user2);

		Assert.assertTrue(accountEntryIds.isEmpty());

		_commerceChannelAccountEntryRelLocalService.
			updateCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRel.
					getCommerceChannelAccountEntryRelId(),
				commerceChannelAccountEntryRel.getCommerceChannelId(),
				user2.getUserId(), false,
				commerceChannelAccountEntryRel.getPriority());

		accountEntryIds = _getAccountEntryIds(user1);

		Assert.assertTrue(accountEntryIds.isEmpty());

		accountEntryIds = _getAccountEntryIds(user2);

		Assert.assertTrue(
			accountEntryIds.contains(accountEntry.getAccountEntryId()));
	}

	private AccountEntry _addBusinessAccountEntry() throws Exception {
		return CommerceAccountTestUtil.addBusinessAccountEntry(
			_serviceContext.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com", _serviceContext);
	}

	private CommerceChannelAccountEntryRel _addCommerceChannelAccountEntryRel(
			AccountEntry accountEntry, User user)
		throws Exception {

		return _commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				_user.getUserId(), accountEntry.getAccountEntryId(),
				User.class.getName(), user.getUserId(),
				_commerceChannel.getCommerceChannelId(), false, 0,
				CommerceChannelAccountEntryRelConstants.TYPE_USER);
	}

	private List<Long> _getAccountEntryIds(User user) throws Exception {
		return TransformUtil.transform(
			CPTestUtil.getCommerceChannelAccountEntryRels(
				User.class.getName(), user.getUserId(),
				_commerceChannel.getCommerceChannelId(),
				CommerceChannelAccountEntryRelConstants.TYPE_USER),
			CommerceChannelAccountEntryRel::getAccountEntryId);
	}

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _user;

}