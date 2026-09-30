/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.security.permission.resource.test;

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
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceOrderAttachmentTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class CommerceOrderAttachmentModelResourcePermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

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
	public void testContains() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(_user);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			_testContains(permissionChecker);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private void _testContains(PermissionChecker permissionChecker)
		throws Exception {

		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker, RandomTestUtil.randomInt(),
				ActionKeys.VIEW));

		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));

		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			_role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));

		commerceOrderAttachment.setUserId(TestPropsValues.getUserId());

		commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.updateCommerceOrderAttachment(
				commerceOrderAttachment);

		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));

		_resourcePermissionLocalService.setResourcePermissions(
			commerceOrderAttachment.getCompanyId(),
			CommerceOrderAttachment.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				commerceOrderAttachment.getCommerceOrderAttachmentId()),
			_role.getRoleId(),
			new String[] {
				ActionKeys.DELETE, ActionKeys.UPDATE, ActionKeys.VIEW
			});

		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));

		commerceOrderAttachment.setRestricted(true);

		commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.updateCommerceOrderAttachment(
				commerceOrderAttachment);

		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			_role.getRoleId(),
			new String[] {
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
				CommerceOrderActionKeys.
					VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS
			});

		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			_role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

		_resourcePermissionLocalService.setResourcePermissions(
			commerceOrderAttachment.getCompanyId(),
			CommerceOrderAttachment.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				commerceOrderAttachment.getCommerceOrderAttachmentId()),
			_role.getRoleId(), new String[0]);

		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(),
			CommerceOrderAttachment.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_commerceOrder.getCompanyId()), _role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertFalse(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(),
			CommerceOrderAttachment.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_commerceOrder.getCompanyId()), _role.getRoleId(),
			new String[] {
				ActionKeys.DELETE, ActionKeys.UPDATE, ActionKeys.VIEW
			});

		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.DELETE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.UPDATE));
		Assert.assertTrue(
			_commerceOrderAttachmentModelResourcePermission.contains(
				permissionChecker,
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				ActionKeys.VIEW));
	}

	private AccountEntry _accountEntry;
	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.commerce.model.CommerceOrderAttachment"
	)
	private volatile ModelResourcePermission<CommerceOrderAttachment>
		_commerceOrderAttachmentModelResourcePermission;

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