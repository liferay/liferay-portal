/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_5.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceOrderAttachmentTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.ByteArrayInputStream;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class CommerceOrderAttachmentUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		CommerceOrderAttachmentTestUtil.initialize(
			CommerceOrderAttachmentUpgradeProcessTest.class);

		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		_accountEntry = CommerceAccountTestUtil.addPersonAccountEntry(
			_user.getUserId(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);
	}

	@Test
	public void testUpgrade() throws Exception {
		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceOrder.getCommerceOrderId(), 0, true,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));
		FileEntry fileEntry1 =
			_commerceOrderLocalService.addAttachmentFileEntry(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));
		FileEntry fileEntry2 =
			_commerceOrderLocalService.addAttachmentFileEntry(
				null, _user.getUserId(), _commerceOrder.getCommerceOrderId(),
				RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));

		int count =
			_commerceOrderAttachmentLocalService.
				getCommerceOrderAttachmentsCount(
					_commerceOrder.getCommerceOrderId());

		_runUpgrade();

		List<CommerceOrderAttachment> commerceOrderAttachments =
			_commerceOrderAttachmentLocalService.getCommerceOrderAttachments(
				_commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commerceOrderAttachments.toString(), count + 2,
			commerceOrderAttachments.size());

		_assertCommerceOrderAttachment(
			_fetchCommerceOrderAttachment(
				commerceOrderAttachments, fileEntry1.getFileEntryId()),
			fileEntry1);
		_assertCommerceOrderAttachment(
			_fetchCommerceOrderAttachment(
				commerceOrderAttachments, fileEntry2.getFileEntryId()),
			fileEntry2);

		commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.getCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId());

		Assert.assertEquals("invoice", commerceOrderAttachment.getType());
		Assert.assertTrue(commerceOrderAttachment.isRestricted());

		_runUpgrade();

		Assert.assertEquals(
			count + 2,
			_commerceOrderAttachmentLocalService.
				getCommerceOrderAttachmentsCount(
					_commerceOrder.getCommerceOrderId()));
	}

	private void _assertCommerceOrderAttachment(
		CommerceOrderAttachment commerceOrderAttachment, FileEntry fileEntry) {

		Assert.assertEquals(
			_commerceOrder.getCommerceOrderId(),
			commerceOrderAttachment.getCommerceOrderId());
		Assert.assertEquals(
			fileEntry.getCompanyId(), commerceOrderAttachment.getCompanyId());
		Assert.assertEquals(
			commerceOrderAttachment.getUuid(),
			commerceOrderAttachment.getExternalReferenceCode());
		Assert.assertEquals(
			_commerceOrder.getGroupId(), commerceOrderAttachment.getGroupId());
		Assert.assertEquals(0, commerceOrderAttachment.getPriority(), 0);
		Assert.assertFalse(commerceOrderAttachment.isRestricted());
		Assert.assertEquals(
			fileEntry.getTitle(), commerceOrderAttachment.getTitle());
		Assert.assertEquals(
			"purchaseOrderDocument", commerceOrderAttachment.getType());
		Assert.assertEquals(
			fileEntry.getUserId(), commerceOrderAttachment.getUserId());

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				commerceOrderAttachment.getCompanyId(),
				CommerceOrderAttachment.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(
					commerceOrderAttachment.getCommerceOrderAttachmentId()));

		Assert.assertFalse(resourcePermissions.isEmpty());
	}

	private CommerceOrderAttachment _fetchCommerceOrderAttachment(
		List<CommerceOrderAttachment> commerceOrderAttachments,
		long fileEntryId) {

		for (CommerceOrderAttachment commerceOrderAttachment :
				commerceOrderAttachments) {

			if (commerceOrderAttachment.getFileEntryId() == fileEntryId) {
				return commerceOrderAttachment;
			}
		}

		return null;
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
		FinderCacheUtil.clearCache();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.internal.upgrade.v15_1_5." +
			"CommerceOrderAttachmentUpgradeProcess";

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

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.internal.upgrade.registry.CommerceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	private User _user;

}