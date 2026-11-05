/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.security.permission.resource.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CommerceReturnItemDiscussionPermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Assume.assumeTrue(
			FeatureFlagManagerUtil.isEnabled(
				TestPropsValues.getCompanyId(), "LPD-10562"));

		_group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.addCommerceChannel(
				null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				_group.getGroupId(), RandomTestUtil.randomString(),
				CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
				commerceCurrency.getCode(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(),
			commerceCurrency.getUserId(), commerceCurrency.getCode());

		CPInstance cpInstance =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				commerceCatalog.getGroupId());

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			cpInstance.getCPDefinition());

		_user = UserTestUtil.addUser();

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(), commerceCurrency);

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder.setOrderStatus(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED);

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		ObjectDefinition commerceReturnObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "CommerceReturn");

		ObjectEntry commerceReturnObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, _user.getUserId(),
				commerceReturnObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"channelGroupId", commerceChannel.getSiteGroupId()
				).put(
					"channelId", commerceChannel.getCommerceChannelId()
				).put(
					"r_accountToCommerceReturns_accountEntryId",
					commerceOrder.getCommerceAccountId()
				).put(
					"r_commerceOrderToCommerceReturns_commerceOrderId",
					commerceOrder.getCommerceOrderId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_commerceReturnItemObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "CommerceReturnItem");

		_commerceReturnItemObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, _user.getUserId(),
				_commerceReturnItemObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"quantity", commerceOrderItem.getQuantity()
				).put(
					"r_accountToCommerceReturnItems_accountEntryId",
					commerceOrder.getCommerceAccountId()
				).put(
					"r_commerceOrderItemToCommerceReturnItems_" +
						"commerceOrderItemId",
					commerceOrderItem.getCommerceOrderItemId()
				).put(
					"r_commerceReturnToCommerceReturnItems_l_commerceReturnId",
					commerceReturnObjectEntry.getObjectEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testHasPermission() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		Assert.assertTrue(
			_discussionPermission.hasAddPermission(
				permissionChecker, _group.getCompanyId(), _group.getGroupId(),
				_commerceReturnItemObjectDefinition.getClassName(),
				_commerceReturnItemObjectEntry.getObjectEntryId()));

		long administratorCommentId = _addComment(TestPropsValues.getUserId());

		Assert.assertFalse(
			_discussionPermission.hasDeletePermission(
				permissionChecker, administratorCommentId));
		Assert.assertFalse(
			_discussionPermission.hasUpdatePermission(
				permissionChecker, administratorCommentId));

		Assert.assertTrue(
			_discussionPermission.hasViewPermission(
				permissionChecker, _group.getCompanyId(), _group.getGroupId(),
				_commerceReturnItemObjectDefinition.getClassName(),
				_commerceReturnItemObjectEntry.getObjectEntryId()));

		permissionChecker = PermissionCheckerFactoryUtil.create(
			UserTestUtil.addUser());

		Assert.assertFalse(
			_discussionPermission.hasAddPermission(
				permissionChecker, _group.getCompanyId(), _group.getGroupId(),
				_commerceReturnItemObjectDefinition.getClassName(),
				_commerceReturnItemObjectEntry.getObjectEntryId()));

		long userCommentId = _addComment(_user.getUserId());

		Assert.assertFalse(
			_discussionPermission.hasDeletePermission(
				permissionChecker, userCommentId));
		Assert.assertFalse(
			_discussionPermission.hasUpdatePermission(
				permissionChecker, userCommentId));

		Assert.assertFalse(
			_discussionPermission.hasViewPermission(
				permissionChecker, _group.getCompanyId(), _group.getGroupId(),
				_commerceReturnItemObjectDefinition.getClassName(),
				_commerceReturnItemObjectEntry.getObjectEntryId()));
	}

	private long _addComment(long userId) throws Exception {
		return _commentManager.addComment(
			userId, _group.getGroupId(),
			_commerceReturnItemObjectDefinition.getClassName(),
			_commerceReturnItemObjectEntry.getObjectEntryId(),
			RandomTestUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), userId)));
	}

	@Inject
	private CommentManager _commentManager;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private ObjectDefinition _commerceReturnItemObjectDefinition;
	private ObjectEntry _commerceReturnItemObjectEntry;

	@Inject
	private DiscussionPermission _discussionPermission;

	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private User _user;

}