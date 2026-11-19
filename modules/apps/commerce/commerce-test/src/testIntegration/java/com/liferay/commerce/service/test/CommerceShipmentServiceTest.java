/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.exception.NoSuchShipmentException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.service.CommerceShipmentService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
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

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author João Cordeiro
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommerceShipmentServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId(), TestPropsValues.getUserId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, group.getGroupId(),
			"Test Channel", CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			group.getGroupId());

		_commerceOrder = CommerceTestUtil.createCommerceOrderForShipping(
			TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId(),
			cpInstance.getCPInstanceId(),
			BigDecimal.valueOf(RandomTestUtil.nextDouble()), BigDecimal.ONE, 1);

		_commerceShipment = _commerceShipmentService.addCommerceShipment(
			_commerceOrder.getCommerceOrderId(), _serviceContext);

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse();

		_commerceShipmentItemLocalService.addCommerceShipmentItem(
			null, _commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceOrderItem.getQuantity(), null, false, _serviceContext);

		_commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			_commerceOrder.getCommerceOrderId());

		_role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null, _serviceContext);

		_user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(_user.getUserId(), _role);
	}

	@After
	public void tearDown() throws Exception {
		for (CommerceShipment commerceShipment :
				_commerceShipmentLocalService.getCommerceShipments(
					new long[] {_commerceChannel.getGroupId()},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			_commerceShipmentLocalService.deleteCommerceShipment(
				commerceShipment, false);
		}

		_commerceOrderLocalService.deleteCommerceOrders(
			_commerceChannel.getGroupId());
	}

	@Test
	public void testAddCommerceShipment() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.addCommerceShipment(
				_commerceOrder.getCommerceOrderId(), _serviceContext);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);
		RoleTestUtil.addResourcePermission(
			_role, CommerceConstants.RESOURCE_NAME_COMMERCE_SHIPMENT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceActionKeys.ADD_COMMERCE_SHIPMENT);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.addCommerceShipment(
				_commerceOrder.getCommerceOrderId(), _serviceContext);
		}
	}

	@Test
	public void testDeleteCommerceShipment() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.deleteCommerceShipment(
				_commerceShipment.getCommerceShipmentId(), false);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.DELETE, exception.getMessage(), _user.getUserId());
		}

		_commerceOrder.setUserId(_user.getUserId());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.deleteCommerceShipment(
				_commerceShipment.getCommerceShipmentId(), false);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.DELETE, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceShipment.getCompanyId(), CommerceShipment.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_commerceShipment.getCommerceShipmentId()),
			_role.getRoleId(), new String[] {ActionKeys.DELETE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.deleteCommerceShipment(
				_commerceShipment.getCommerceShipmentId(), false);
		}
	}

	@Test
	public void testGetCommerceShipment() throws Exception {
		_testGetCommerceShipment();
		_testGetCommerceShipmentWithGroupId();
	}

	@Test
	public void testGetCommerceShipments() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipments(
				_commerceShipment.getCompanyId(), 0, 0, null);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceConstants.RESOURCE_NAME_COMMERCE_SHIPMENT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipments(
				_commerceShipment.getCompanyId(), 0, 0, null);
		}
	}

	@Test
	public void testGetCommerceShipmentsCount() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipmentsCount(
				_commerceShipment.getCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceConstants.RESOURCE_NAME_COMMERCE_SHIPMENT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipmentsCount(
				_commerceShipment.getCompanyId());
		}
	}

	@Test
	public void testUpdateCommerceShipment() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.updateStatus(
				_commerceShipment.getCommerceShipmentId(), 1);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.UPDATE, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceShipment.getCompanyId(), CommerceShipment.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_commerceShipment.getCommerceShipmentId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.updateStatus(
				_commerceShipment.getCommerceShipmentId(), 1);
		}
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	private void _testGetCommerceShipment() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipment(
				_commerceShipment.getCommerceShipmentId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceConstants.RESOURCE_NAME_COMMERCE_SHIPMENT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceActionKeys.ADD_COMMERCE_SHIPMENT);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipment(
				_commerceShipment.getCommerceShipmentId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_commerceOrder.setUserId(_user.getUserId());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipment(
				_commerceShipment.getCommerceShipmentId());
		}

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_commerceChannel.getGroupId());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse();

		_commerceShipmentItemLocalService.addCommerceShipmentItem(
			null, _commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceOrderItem.getQuantity(), null, false, _serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipment(
				_commerceShipment.getCommerceShipmentId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceShipmentService.getCommerceShipment(
				_commerceShipment.getCommerceShipmentId());
		}
	}

	private void _testGetCommerceShipmentWithGroupId() throws Exception {
		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		CommerceShipment commerceShipment =
			_commerceShipmentService.addCommerceShipment(
				commerceOrder.getCommerceOrderId(), _serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_commerceChannel.getSiteGroupId());

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse();

		_commerceShipmentItemLocalService.addCommerceShipmentItem(
			null, commerceShipment.getCommerceShipmentId(),
			commerceOrderItem.getCommerceOrderItemId(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceOrderItem.getQuantity(), null, false, _serviceContext);

		User user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_commerceShipmentService.getCommerceShipment(
				_commerceChannel.getGroupId(),
				commerceShipment.getCommerceShipmentId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			_assertMessage(
				ActionKeys.VIEW, principalException.getMessage(),
				user.getUserId());
		}

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder.setUserId(user.getUserId());

		_commerceOrderLocalService.updateCommerceOrder(commerceOrder);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Assert.assertEquals(
				commerceShipment,
				_commerceShipmentService.getCommerceShipment(
					_commerceChannel.getGroupId(),
					commerceShipment.getCommerceShipmentId()));

			_commerceShipmentService.getCommerceShipment(
				RandomTestUtil.randomLong(),
				commerceShipment.getCommerceShipmentId());

			Assert.fail();
		}
		catch (NoSuchShipmentException noSuchShipmentException) {
			Assert.assertNotNull(noSuchShipmentException);
		}
	}

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private CommerceShipment _commerceShipment;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Inject
	private CommerceShipmentService _commerceShipmentService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}