/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.commerce.service.CommerceOrderAttachmentService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceOrderAttachmentTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

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
public class CommerceOrderAttachmentServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CommerceOrderAttachmentTestUtil.initialize(getClass());

		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_accountEntry = CommerceAccountTestUtil.addPersonAccountEntry(
			TestPropsValues.getUserId(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);

		_commerceOrderAttachment = _addCommerceOrderAttachment(false);

		_role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(_user.getUserId(), _role);
	}

	@Test
	public void testAddCommerceOrderAttachment() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.addCommerceOrderAttachment(
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), _TYPE_KEY,
				RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT,
				exception.getMessage(), _user.getUserId());
		}

		_setResourcePermissions(
			CommerceOrderConstants.RESOURCE_NAME,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			ResourceConstants.SCOPE_GROUP,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.addCommerceOrderAttachment(
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), _TYPE_KEY,
				RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));
		}
	}

	@Test
	public void testDeleteCommerceOrderAttachment() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.deleteCommerceOrderAttachment(
				_commerceOrderAttachment.getCommerceOrderAttachmentId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.DELETE, exception.getMessage(), _user.getUserId());
		}

		_setResourcePermissions(
			CommerceOrderConstants.RESOURCE_NAME,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			ResourceConstants.SCOPE_GROUP,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);
		_setResourcePermissions(
			CommerceOrderAttachment.class.getName(),
			String.valueOf(
				_commerceOrderAttachment.getCommerceOrderAttachmentId()),
			ResourceConstants.SCOPE_INDIVIDUAL, ActionKeys.DELETE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.deleteCommerceOrderAttachment(
				_commerceOrderAttachment.getCommerceOrderAttachmentId());
		}
	}

	@Test
	public void testGetCommerceOrderAttachment1() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.getCommerceOrderAttachment(
				_commerceOrderAttachment.getCommerceOrderAttachmentId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_setResourcePermissions(
			CommerceOrderConstants.RESOURCE_NAME,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			ResourceConstants.SCOPE_GROUP,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);
		_setResourcePermissions(
			CommerceOrderAttachment.class.getName(),
			String.valueOf(
				_commerceOrderAttachment.getCommerceOrderAttachmentId()),
			ResourceConstants.SCOPE_INDIVIDUAL, ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			CommerceOrderAttachment commerceOrderAttachment =
				_commerceOrderAttachmentService.getCommerceOrderAttachment(
					_commerceOrderAttachment.getCommerceOrderAttachmentId());

			Assert.assertEquals(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(),
				commerceOrderAttachment.getCommerceOrderAttachmentId());
		}
	}

	@Test
	public void testGetCommerceOrderAttachment2() throws Exception {
		CommerceOrderAttachment commerceOrderAttachment =
			_addCommerceOrderAttachment(true);

		_setResourcePermissions(
			CommerceOrderConstants.RESOURCE_NAME,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			ResourceConstants.SCOPE_GROUP,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.getCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_setResourcePermissions(
			CommerceOrderConstants.RESOURCE_NAME,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			ResourceConstants.SCOPE_GROUP,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);
		_setResourcePermissions(
			CommerceOrderAttachment.class.getName(),
			String.valueOf(
				commerceOrderAttachment.getCommerceOrderAttachmentId()),
			ResourceConstants.SCOPE_INDIVIDUAL, ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.getCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId());
		}
	}

	@Test
	public void testGetCommerceOrderAttachments1() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.getCommerceOrderAttachments(
				_commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_setResourcePermissions(
			CommerceOrderConstants.RESOURCE_NAME,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			ResourceConstants.SCOPE_GROUP,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.getCommerceOrderAttachments(
				_commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);
		}
	}

	@Test
	public void testGetCommerceOrderAttachments2() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.getCommerceOrderAttachments(
				_commerceOrder.getCommerceOrderId(), true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_setResourcePermissions(
			CommerceOrderConstants.RESOURCE_NAME,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			ResourceConstants.SCOPE_GROUP,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.getCommerceOrderAttachments(
				_commerceOrder.getCommerceOrderId(), true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);
		}
	}

	@Test
	public void testUpdateCommerceOrderAttachment() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.updateCommerceOrderAttachment(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), _TYPE_KEY);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.UPDATE, exception.getMessage(), _user.getUserId());
		}

		_setResourcePermissions(
			CommerceOrderConstants.RESOURCE_NAME,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			ResourceConstants.SCOPE_GROUP,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);
		_setResourcePermissions(
			CommerceOrderAttachment.class.getName(),
			String.valueOf(
				_commerceOrderAttachment.getCommerceOrderAttachmentId()),
			ResourceConstants.SCOPE_INDIVIDUAL, ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceOrderAttachmentService.updateCommerceOrderAttachment(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), _TYPE_KEY);
		}
	}

	private CommerceOrderAttachment _addCommerceOrderAttachment(
			boolean restricted)
		throws Exception {

		return _commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_commerceOrder.getCommerceOrderId(), RandomTestUtil.nextDouble(),
			restricted, RandomTestUtil.randomString(), _TYPE_KEY,
			RandomTestUtil.randomString(),
			new ByteArrayInputStream("Liferay".getBytes()));
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message,
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	private void _setResourcePermissions(
			String className, String primKey, int scope, String... actionIds)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(), className, scope, primKey,
			_role.getRoleId(), actionIds);
	}

	private static final String _TYPE_KEY = "invoice";

	private AccountEntry _accountEntry;
	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;
	private CommerceOrder _commerceOrder;
	private CommerceOrderAttachment _commerceOrderAttachment;

	@Inject
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Inject
	private CommerceOrderAttachmentService _commerceOrderAttachmentService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Group _group;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}