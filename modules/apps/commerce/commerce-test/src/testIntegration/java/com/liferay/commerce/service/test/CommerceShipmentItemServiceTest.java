/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.shipment.test.util.CommerceShipmentTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
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
public class CommerceShipmentItemServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		_commerceShipmentItem1 = _addCommerceShipmentItem(commerceCurrency);
		_commerceShipmentItem2 = _addCommerceShipmentItem(commerceCurrency);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), CommerceShipment.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_commerceShipmentItem1.getCommerceShipmentId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		CommerceShipment commerceShipment =
			_commerceShipmentItem1.getCommerceShipment();

		AccountEntry accountEntry = commerceShipment.getAccountEntry();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			CommerceOrderConstants.RESOURCE_NAME, ResourceConstants.SCOPE_GROUP,
			String.valueOf(accountEntry.getAccountEntryGroupId()),
			_role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

		_user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(_user.getUserId(), _role);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_commerceShipmentItem1.getGroupId(), _user.getUserId());
	}

	@Test
	public void testAddCommerceShipmentItem() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentItemService.addCommerceShipmentItem(
				null, _commerceShipmentItem1.getCommerceShipmentId(),
				_commerceShipmentItem1.getCommerceOrderItemId(), 0,
				BigDecimal.ONE, null, false, _serviceContext);

			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceShipmentItemService.addCommerceShipmentItem(
					null, _commerceShipmentItem1.getCommerceShipmentId(),
					_commerceShipmentItem2.getCommerceOrderItemId(), 0,
					BigDecimal.ONE, null, false, _serviceContext));

			CommerceShipment commerceShipment2 =
				_commerceShipmentItem2.getCommerceShipment();

			CommerceShipment commerceShipment3 =
				_commerceShipmentLocalService.addCommerceShipment(
					null, commerceShipment2.getGroupId(),
					commerceShipment2.getCommerceAccountId(), 0, 0, null,
					_serviceContext);

			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceShipmentItemService.addCommerceShipmentItem(
					null, commerceShipment3.getCommerceShipmentId(),
					_commerceShipmentItem2.getCommerceOrderItemId(), 0,
					BigDecimal.ONE, null, false, _serviceContext));

			AccountEntry accountEntry = commerceShipment2.getAccountEntry();

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(),
				CommerceOrderConstants.RESOURCE_NAME,
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(accountEntry.getAccountEntryGroupId()),
				_role.getRoleId(),
				new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceShipmentItemService.addCommerceShipmentItem(
					null, _commerceShipmentItem1.getCommerceShipmentId(),
					_commerceShipmentItem2.getCommerceOrderItemId(), 0,
					BigDecimal.ONE, null, false, _serviceContext));

			_commerceShipmentItemService.addCommerceShipmentItem(
				null, commerceShipment3.getCommerceShipmentId(),
				_commerceShipmentItem2.getCommerceOrderItemId(), 0,
				BigDecimal.ONE, null, false, _serviceContext);
		}
	}

	@Test
	public void testAddOrUpdateCommerceShipmentItem() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentItemService.addOrUpdateCommerceShipmentItem(
				_commerceShipmentItem1.getExternalReferenceCode(),
				_commerceShipmentItem1.getCommerceShipmentId(),
				_commerceShipmentItem1.getCommerceOrderItemId(),
				_commerceShipmentItem1.getCommerceInventoryWarehouseId(),
				BigDecimal.valueOf(2), null, false, _serviceContext);

			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceShipmentItemService.
						addOrUpdateCommerceShipmentItem(
							RandomTestUtil.randomString(),
							_commerceShipmentItem1.getCommerceShipmentId(),
							_commerceShipmentItem2.getCommerceOrderItemId(), 0,
							BigDecimal.ONE, null, false, _serviceContext));
			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceShipmentItemService.
						addOrUpdateCommerceShipmentItem(
							_commerceShipmentItem2.getExternalReferenceCode(),
							_commerceShipmentItem1.getCommerceShipmentId(),
							_commerceShipmentItem1.getCommerceOrderItemId(),
							_commerceShipmentItem2.
								getCommerceInventoryWarehouseId(),
							BigDecimal.valueOf(2), null, false,
							_serviceContext));
		}
	}

	@Test
	public void testUpdateCommerceShipmentItem() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentItemService.updateCommerceShipmentItem(
				_commerceShipmentItem1.getCommerceShipmentId(),
				_commerceShipmentItem1.getCommerceShipmentItemId(),
				_commerceShipmentItem1.getCommerceInventoryWarehouseId(),
				BigDecimal.valueOf(2), false);

			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceShipmentItemService.updateCommerceShipmentItem(
					_commerceShipmentItem1.getCommerceShipmentId(),
					_commerceShipmentItem2.getCommerceShipmentItemId(),
					_commerceShipmentItem2.getCommerceInventoryWarehouseId(),
					BigDecimal.valueOf(2), false));
		}
	}

	@Test
	public void testUpdateExternalReferenceCode() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentItemService.updateExternalReferenceCode(
				_commerceShipmentItem1.getCommerceShipmentId(),
				_commerceShipmentItem1.getCommerceShipmentItemId(),
				RandomTestUtil.randomString());

			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceShipmentItemService.updateExternalReferenceCode(
					_commerceShipmentItem1.getCommerceShipmentId(),
					_commerceShipmentItem2.getCommerceShipmentItemId(),
					RandomTestUtil.randomString()));
		}
	}

	private CommerceShipmentItem _addCommerceShipmentItem(
			CommerceCurrency commerceCurrency)
		throws Exception {

		User user = UserTestUtil.addUser();

		Group group = GroupTestUtil.addGroup();

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			user.getUserId(), commerceChannel.getGroupId(),
			commerceCurrency.getCommerceCurrencyId());

		CommerceShipment commerceShipment =
			CommerceShipmentTestUtil.createEmptyOrderShipment(
				group.getGroupId(), commerceOrder.getCommerceOrderId());

		return CommerceShipmentTestUtil.addCommerceShipmentItem(
			new TestCommerceContext(
				commerceOrder.getAccountEntry(), commerceCurrency,
				commerceChannel, user, group, commerceOrder),
			CPTestUtil.addCPInstanceWithRandomSku(group.getGroupId()),
			group.getGroupId(), user.getUserId(),
			commerceOrder.getCommerceOrderId(),
			commerceShipment.getCommerceShipmentId(), 10, 1);
	}

	private CommerceShipmentItem _commerceShipmentItem1;
	private CommerceShipmentItem _commerceShipmentItem2;

	@Inject
	private CommerceShipmentItemService _commerceShipmentItemService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}