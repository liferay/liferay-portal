/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.constants.CommercePaymentEntryActionKeys;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
import com.liferay.commerce.payment.service.CommercePaymentEntryService;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

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
public class CommercePaymentEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			TestPropsValues.getCompanyId());
		Group group1 = GroupTestUtil.addGroup();

		_commerceChannel1 = _addCommerceChannel(_commerceCurrency, group1);

		User user1 = UserTestUtil.addUser();

		_commerceOrder1 = CommerceTestUtil.addB2CCommerceOrder(
			user1.getUserId(), _commerceChannel1.getGroupId(),
			_commerceCurrency);

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		RoleTestUtil.addResourcePermission(
			role, CommercePaymentEntryConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_commerceChannel1.getGroupId()),
			CommercePaymentEntryActionKeys.ADD_PAYMENT);

		User user2 = UserTestUtil.addUser();

		Group group2 = GroupTestUtil.addGroup();

		_commerceChannel2 = _addCommerceChannel(_commerceCurrency, group2);

		_commerceOrder2 = CommerceTestUtil.addB2CCommerceOrder(
			user2.getUserId(), _commerceChannel2.getGroupId(),
			_commerceCurrency);

		_user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(_user.getUserId(), role);
	}

	@Test
	public void testAddCommercePaymentEntry() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertNotNull(
				_addCommercePaymentEntry(
					_commerceOrder1, _commerceChannel1.getGroupId()));

			try {
				_addCommercePaymentEntry(
					_commerceOrder2, _commerceChannel1.getGroupId());

				Assert.fail();
			}
			catch (PrincipalException principalException) {
				Assert.assertNotNull(principalException);
			}

			try {
				_addCommercePaymentEntry(
					_commerceOrder2, _commerceChannel2.getGroupId());

				Assert.fail();
			}
			catch (PrincipalException principalException) {
				Assert.assertNotNull(principalException);
			}
		}

		Assert.assertEquals(
			0,
			_commercePaymentEntryLocalService.getCommercePaymentEntriesCount(
				_commerceOrder2.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder2.getCommerceOrderId(),
				CommercePaymentEntryConstants.TYPE_PAYMENT));
	}

	@Test
	public void testAddOrUpdateCommercePaymentEntry() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertNotNull(
				_addOrUpdateCommercePaymentEntry(
					_commerceOrder1, RandomTestUtil.randomString(),
					_commerceChannel1.getGroupId()));

			try {
				_addOrUpdateCommercePaymentEntry(
					_commerceOrder2, RandomTestUtil.randomString(),
					_commerceChannel1.getGroupId());

				Assert.fail();
			}
			catch (PrincipalException principalException) {
				Assert.assertNotNull(principalException);
			}

			try {
				_addOrUpdateCommercePaymentEntry(
					_commerceOrder2, RandomTestUtil.randomString(),
					_commerceChannel2.getGroupId());

				Assert.fail();
			}
			catch (PrincipalException principalException) {
				Assert.assertNotNull(principalException);
			}
		}

		Assert.assertEquals(
			0,
			_commercePaymentEntryLocalService.getCommercePaymentEntriesCount(
				_commerceOrder2.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder2.getCommerceOrderId(),
				CommercePaymentEntryConstants.TYPE_PAYMENT));
	}

	private CommerceChannel _addCommerceChannel(
			CommerceCurrency commerceCurrency, Group group)
		throws Exception {

		return _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, group.getGroupId(),
			RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			commerceCurrency.getCode(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private CommercePaymentEntry _addCommercePaymentEntry(
			CommerceOrder commerceOrder, long groupId)
		throws Exception {

		return _commercePaymentEntryService.addCommercePaymentEntry(
			_classNameLocalService.getClassNameId(CommerceOrder.class),
			commerceOrder.getCommerceOrderId(),
			_commerceChannel1.getCommerceChannelId(), BigDecimal.TEN, null,
			null, _commerceCurrency.getCode(), _user.getLanguageId(), null,
			null, null, 0, null, RandomTestUtil.randomString(),
			CommercePaymentEntryConstants.TYPE_PAYMENT,
			ServiceContextTestUtil.getServiceContext(
				groupId, _user.getUserId()));
	}

	private CommercePaymentEntry _addOrUpdateCommercePaymentEntry(
			CommerceOrder commerceOrder, String externalReferenceCode,
			long groupId)
		throws Exception {

		return _commercePaymentEntryService.addOrUpdateCommercePaymentEntry(
			externalReferenceCode,
			_classNameLocalService.getClassNameId(CommerceOrder.class),
			commerceOrder.getCommerceOrderId(),
			_commerceChannel1.getCommerceChannelId(), BigDecimal.TEN, null,
			null, _commerceCurrency.getCode(), null, _user.getLanguageId(),
			null, null, null, 0, CommercePaymentEntryConstants.STATUS_PENDING,
			null, null, RandomTestUtil.randomString(),
			CommercePaymentEntryConstants.TYPE_PAYMENT,
			ServiceContextTestUtil.getServiceContext(
				groupId, _user.getUserId()));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CommerceChannel _commerceChannel1;
	private CommerceChannel _commerceChannel2;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;
	private CommerceOrder _commerceOrder1;
	private CommerceOrder _commerceOrder2;

	@Inject
	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

	@Inject
	private CommercePaymentEntryService _commercePaymentEntryService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}