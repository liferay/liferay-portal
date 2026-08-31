/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceFragmentRendererKeys;
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
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

import java.util.Collections;
import java.util.Map;

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
public class InfoBoxFragmentRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_user = UserTestUtil.addUser();

		_accountEntry = CommerceAccountTestUtil.addPersonAccountEntry(
			_user.getUserId(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));
	}

	@Test
	public void testGetPurchaseOrderDocumentAdditionalProps() throws Exception {
		_testGetPurchaseOrderDocumentAdditionalProps();
		_testGetPurchaseOrderDocumentAdditionalPropsRestricted();
	}

	private CommerceOrder _addCommerceOrder() throws Exception {
		return _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);
	}

	private Map<String, Object> _getPurchaseOrderDocumentAdditionalProps(
			PermissionChecker permissionChecker)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_infoBoxFragmentRenderer,
			"_getPurchaseOrderDocumentAdditionalProps",
			new Class<?>[] {CommerceOrder.class, PermissionChecker.class},
			_commerceOrder, permissionChecker);
	}

	private Object _getPurchaseOrderDocumentFileEntry(
			PermissionChecker permissionChecker)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_infoBoxFragmentRenderer, "_getPurchaseOrderDocumentFileEntry",
			new Class<?>[] {CommerceOrder.class, PermissionChecker.class},
			_commerceOrder, permissionChecker);
	}

	private void _testGetPurchaseOrderDocumentAdditionalProps()
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-6252"),
					Boolean.TRUE.toString())) {

			_commerceOrder = _addCommerceOrder();

			CommerceOrderAttachmentTestUtil.initialize(getClass());

			Map<String, Object> additionalProps =
				_getPurchaseOrderDocumentAdditionalProps(
					PermissionThreadLocal.getPermissionChecker());

			Assert.assertFalse(additionalProps.containsKey("downloadURL"));
			Assert.assertEquals(
				CommerceFragmentRendererKeys.ORDER_ATTACHMENTS_DATA_SET +
					"-pendingOrderAttachments",
				additionalProps.get("fdsId"));
			Assert.assertFalse(additionalProps.containsKey("isOwner"));
			Assert.assertFalse(additionalProps.containsKey("value"));

			CommerceOrderAttachment commerceOrderAttachment =
				_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					_commerceOrder.getCommerceOrderId(),
					RandomTestUtil.nextDouble(), false,
					RandomTestUtil.randomString(), "purchaseOrderDocument",
					RandomTestUtil.randomString(),
					new ByteArrayInputStream("Liferay".getBytes()));

			additionalProps = _getPurchaseOrderDocumentAdditionalProps(
				PermissionThreadLocal.getPermissionChecker());

			Assert.assertEquals(
				CommerceFragmentRendererKeys.ORDER_ATTACHMENTS_DATA_SET +
					"-pendingOrderAttachments",
				additionalProps.get("fdsId"));
			Assert.assertNotNull(additionalProps.get("downloadURL"));
			Assert.assertTrue(
				GetterUtil.getBoolean(additionalProps.get("isOwner")));
			Assert.assertEquals(
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				additionalProps.get("value"));

			Role role = _roleLocalService.addRole(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				null, 0, RandomTestUtil.randomString(), null, null,
				RoleConstants.TYPE_REGULAR, null,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			_resourcePermissionLocalService.setResourcePermissions(
				_commerceOrder.getCompanyId(),
				CommerceOrderConstants.RESOURCE_NAME,
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_accountEntry.getAccountEntryGroupId()),
				role.getRoleId(),
				new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

			_resourcePermissionLocalService.setResourcePermissions(
				_commerceOrder.getCompanyId(),
				CommerceOrderAttachment.class.getName(),
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_commerceOrder.getGroupId()), role.getRoleId(),
				new String[] {ActionKeys.VIEW});

			User user = UserTestUtil.addUser();

			_roleLocalService.addUserRole(user.getUserId(), role);

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				additionalProps = _getPurchaseOrderDocumentAdditionalProps(
					PermissionThreadLocal.getPermissionChecker());

				Assert.assertNotNull(additionalProps.get("downloadURL"));
				Assert.assertEquals(
					CommerceFragmentRendererKeys.ORDER_ATTACHMENTS_DATA_SET +
						"-pendingOrderAttachments",
					additionalProps.get("fdsId"));
				Assert.assertFalse(
					GetterUtil.getBoolean(additionalProps.get("isOwner")));
				Assert.assertEquals(
					commerceOrderAttachment.getCommerceOrderAttachmentId(),
					additionalProps.get("value"));

				Assert.assertNotNull(
					_getPurchaseOrderDocumentFileEntry(
						PermissionThreadLocal.getPermissionChecker()));
			}
		}

		_commerceOrder = _addCommerceOrder();

		Assert.assertEquals(
			Collections.emptyMap(),
			_getPurchaseOrderDocumentAdditionalProps(
				PermissionThreadLocal.getPermissionChecker()));

		_commerceOrderLocalService.addAttachmentFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_commerceOrder.getCommerceOrderId(), RandomTestUtil.randomString(),
			new ByteArrayInputStream("Liferay".getBytes()));

		Map<String, Object> additionalProps =
			_getPurchaseOrderDocumentAdditionalProps(
				PermissionThreadLocal.getPermissionChecker());

		Assert.assertNotNull(additionalProps.get("downloadURL"));
		Assert.assertFalse(additionalProps.containsKey("fdsId"));
		Assert.assertFalse(additionalProps.containsKey("isOwner"));
		Assert.assertNotNull(additionalProps.get("value"));
	}

	private void _testGetPurchaseOrderDocumentAdditionalPropsRestricted()
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-6252"),
					Boolean.TRUE.toString())) {

			_commerceOrder = _addCommerceOrder();

			CommerceOrderAttachmentTestUtil.initialize(getClass());

			CommerceOrderAttachment commerceOrderAttachment =
				_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					_commerceOrder.getCommerceOrderId(),
					RandomTestUtil.nextDouble(), true,
					RandomTestUtil.randomString(), "purchaseOrderDocument",
					RandomTestUtil.randomString(),
					new ByteArrayInputStream("Liferay".getBytes()));

			Map<String, Object> additionalProps =
				_getPurchaseOrderDocumentAdditionalProps(
					PermissionThreadLocal.getPermissionChecker());

			Assert.assertNotNull(additionalProps.get("downloadURL"));
			Assert.assertEquals(
				CommerceFragmentRendererKeys.ORDER_ATTACHMENTS_DATA_SET +
					"-pendingOrderAttachments",
				additionalProps.get("fdsId"));
			Assert.assertTrue(
				GetterUtil.getBoolean(additionalProps.get("isOwner")));
			Assert.assertEquals(
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				additionalProps.get("value"));

			Role role = _roleLocalService.addRole(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				null, 0, RandomTestUtil.randomString(), null, null,
				RoleConstants.TYPE_REGULAR, null,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			_resourcePermissionLocalService.setResourcePermissions(
				_commerceOrder.getCompanyId(),
				CommerceOrderConstants.RESOURCE_NAME,
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_accountEntry.getAccountEntryGroupId()),
				role.getRoleId(),
				new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

			_resourcePermissionLocalService.setResourcePermissions(
				_commerceOrder.getCompanyId(),
				CommerceOrderAttachment.class.getName(),
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_commerceOrder.getGroupId()), role.getRoleId(),
				new String[] {ActionKeys.VIEW});

			User user = UserTestUtil.addUser();

			_roleLocalService.addUserRole(user.getUserId(), role);

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				additionalProps = _getPurchaseOrderDocumentAdditionalProps(
					PermissionThreadLocal.getPermissionChecker());

				Assert.assertFalse(additionalProps.containsKey("downloadURL"));
				Assert.assertEquals(
					CommerceFragmentRendererKeys.ORDER_ATTACHMENTS_DATA_SET +
						"-pendingOrderAttachments",
					additionalProps.get("fdsId"));
				Assert.assertFalse(additionalProps.containsKey("isOwner"));
				Assert.assertFalse(additionalProps.containsKey("value"));

				Assert.assertNull(
					_getPurchaseOrderDocumentFileEntry(
						PermissionThreadLocal.getPermissionChecker()));
			}

			_resourcePermissionLocalService.setResourcePermissions(
				_commerceOrder.getCompanyId(),
				CommerceOrderConstants.RESOURCE_NAME,
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_accountEntry.getAccountEntryGroupId()),
				role.getRoleId(),
				new String[] {
					CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
					CommerceOrderActionKeys.
						VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS
				});

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				additionalProps = _getPurchaseOrderDocumentAdditionalProps(
					PermissionThreadLocal.getPermissionChecker());

				Assert.assertNotNull(additionalProps.get("downloadURL"));
				Assert.assertEquals(
					commerceOrderAttachment.getCommerceOrderAttachmentId(),
					additionalProps.get("value"));

				Assert.assertNotNull(
					_getPurchaseOrderDocumentFileEntry(
						PermissionThreadLocal.getPermissionChecker()));
			}
		}
	}

	private AccountEntry _accountEntry;
	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.commerce.order.content.web.internal.fragment.renderer.InfoBoxFragmentRenderer"
	)
	private FragmentRenderer _infoBoxFragmentRenderer;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}