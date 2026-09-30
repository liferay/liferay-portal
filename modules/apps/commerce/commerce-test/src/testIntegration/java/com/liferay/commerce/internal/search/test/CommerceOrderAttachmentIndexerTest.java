/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.test;

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
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

import java.util.Arrays;
import java.util.HashSet;

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
@Sync
public class CommerceOrderAttachmentIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CommerceOrderAttachmentTestUtil.initialize(getClass());

		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_user = UserTestUtil.addUser();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		_accountEntry1 = CommerceAccountTestUtil.addPersonAccountEntry(
			_user.getUserId(), serviceContext);

		_commerceOrder1 = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry1.getAccountEntryId(), _commerceCurrency.getCode(), 0);

		_attachment1 = _addCommerceOrderAttachment(_commerceOrder1, false);
		_attachment2 = _addCommerceOrderAttachment(_commerceOrder1, true);

		_accountEntry2 = CommerceAccountTestUtil.addPersonAccountEntry(
			_user.getUserId(), serviceContext);

		_commerceOrder2 = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry2.getAccountEntryId(), _commerceCurrency.getCode(), 0);

		_attachment3 = _addCommerceOrderAttachment(_commerceOrder2, false);
		_attachment4 = _addCommerceOrderAttachment(_commerceOrder2, true);

		_indexer = _indexerRegistry.getIndexer(
			CommerceOrderAttachment.class.getName());

		_role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null, serviceContext);

		_roleLocalService.addUserRole(_user.getUserId(), _role);
	}

	@Test
	public void testSearch() throws Exception {
		_assertSearch(0L, TestPropsValues.getUserId());
		_assertSearch(
			_commerceOrder1.getCommerceOrderId(), TestPropsValues.getUserId(),
			_attachment1, _attachment2);
		_assertSearch(
			_commerceOrder2.getCommerceOrderId(), TestPropsValues.getUserId(),
			_attachment3, _attachment4);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_assertSearch(
				_commerceOrder1.getCommerceOrderId(), _user.getUserId());
			_assertSearch(
				_commerceOrder2.getCommerceOrderId(), _user.getUserId());
		}

		_setResourcePermissions(
			_accountEntry1, CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_assertSearch(
				_commerceOrder1.getCommerceOrderId(), _user.getUserId(),
				_attachment1);
			_assertSearch(
				_commerceOrder2.getCommerceOrderId(), _user.getUserId());
		}

		_setResourcePermissions(
			_accountEntry1, CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_assertSearch(
				_commerceOrder1.getCommerceOrderId(), _user.getUserId(),
				_attachment1, _attachment2);
			_assertSearch(
				_commerceOrder2.getCommerceOrderId(), _user.getUserId());
		}

		_setResourcePermissions(
			_accountEntry2, CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_assertSearch(
				_commerceOrder1.getCommerceOrderId(), _user.getUserId(),
				_attachment1, _attachment2);
			_assertSearch(
				_commerceOrder2.getCommerceOrderId(), _user.getUserId(),
				_attachment3, _attachment4);
		}
	}

	private CommerceOrderAttachment _addCommerceOrderAttachment(
			CommerceOrder commerceOrder, boolean restricted)
		throws Exception {

		return _commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			commerceOrder.getCommerceOrderId(), RandomTestUtil.nextDouble(),
			restricted, RandomTestUtil.randomString(), "invoice",
			RandomTestUtil.randomString(),
			new ByteArrayInputStream("Liferay".getBytes()));
	}

	private void _assertSearch(
			long commerceOrderId, long userId,
			CommerceOrderAttachment... commerceOrderAttachments)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(_group.getCompanyId());
		searchContext.setUserId(userId);

		if (commerceOrderId > 0) {
			searchContext.setAttribute("commerceOrderId", commerceOrderId);
		}

		Hits hits = _indexer.search(searchContext);

		Assert.assertEquals(
			Arrays.toString(hits.getDocs()),
			new HashSet<>(
				TransformUtil.transformToList(
					commerceOrderAttachments,
					CommerceOrderAttachment::getCommerceOrderAttachmentId)),
			new HashSet<>(
				TransformUtil.transformToList(
					hits.getDocs(),
					document -> Long.valueOf(
						document.get(Field.ENTRY_CLASS_PK)))));
	}

	private void _setResourcePermissions(
			AccountEntry accountEntry, String... actionIds)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(accountEntry.getAccountEntryGroupId()),
			_role.getRoleId(), actionIds);
	}

	private AccountEntry _accountEntry1;
	private AccountEntry _accountEntry2;
	private CommerceOrderAttachment _attachment1;
	private CommerceOrderAttachment _attachment2;
	private CommerceOrderAttachment _attachment3;
	private CommerceOrderAttachment _attachment4;
	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;
	private CommerceOrder _commerceOrder1;
	private CommerceOrder _commerceOrder2;

	@Inject
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Group _group;
	private Indexer<CommerceOrderAttachment> _indexer;

	@Inject
	private IndexerRegistry _indexerRegistry;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}