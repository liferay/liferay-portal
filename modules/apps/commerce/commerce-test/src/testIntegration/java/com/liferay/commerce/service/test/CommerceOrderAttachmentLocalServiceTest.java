/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.exception.CommerceOrderAttachmentTitleException;
import com.liferay.commerce.exception.CommerceOrderAttachmentTypeException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceOrderAttachmentTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

import java.util.List;

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
public class CommerceOrderAttachmentLocalServiceTest {

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

		_user = UserTestUtil.addUser();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_accountEntry = CommerceAccountTestUtil.addPersonAccountEntry(
			_user.getUserId(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(), 0);
	}

	@Test
	public void testAddCommerceOrderAttachment() throws Exception {
		try {
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				null, _user.getUserId(), _commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false, null, _TYPE_KEY,
				RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));

			Assert.fail();
		}
		catch (CommerceOrderAttachmentTitleException
					commerceOrderAttachmentTitleException) {

			Assert.assertNotNull(commerceOrderAttachmentTitleException);
		}

		try {
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				null, _user.getUserId(), _commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(),
				getClass().getResourceAsStream("dependencies/attachment.txt"));

			Assert.fail();
		}
		catch (CommerceOrderAttachmentTypeException
					commerceOrderAttachmentTypeException) {

			Assert.assertNotNull(commerceOrderAttachmentTypeException);
		}

		try {
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				null, _user.getUserId(), _commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));

			Assert.fail();
		}
		catch (CommerceOrderAttachmentTypeException
					commerceOrderAttachmentTypeException) {

			Assert.assertNotNull(commerceOrderAttachmentTypeException);
		}

		String externalReferenceCode = RandomTestUtil.randomString();
		double priority = RandomTestUtil.nextDouble();
		String title = RandomTestUtil.randomString();

		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				externalReferenceCode, _user.getUserId(),
				_commerceOrder.getCommerceOrderId(), priority, false, title,
				_TYPE_KEY, RandomTestUtil.randomString(),
				new ByteArrayInputStream("Liferay".getBytes()));

		Assert.assertEquals(
			externalReferenceCode,
			commerceOrderAttachment.getExternalReferenceCode());
		Assert.assertEquals(
			_commerceOrder.getGroupId(), commerceOrderAttachment.getGroupId());
		Assert.assertEquals(
			_user.getUserId(), commerceOrderAttachment.getUserId());
		Assert.assertEquals(
			_commerceOrder.getCommerceOrderId(),
			commerceOrderAttachment.getCommerceOrderId());
		Assert.assertTrue(commerceOrderAttachment.getFileEntryId() > 0);
		Assert.assertEquals(priority, commerceOrderAttachment.getPriority(), 0);
		Assert.assertFalse(commerceOrderAttachment.isRestricted());
		Assert.assertEquals(title, commerceOrderAttachment.getTitle());
		Assert.assertEquals(_TYPE_KEY, commerceOrderAttachment.getType());

		LocalRepository localRepository = _commerceOrder.getLocalRepository();

		Folder folder = _commerceOrder.getFolder(localRepository);

		FileEntry fileEntry = localRepository.getFileEntry(
			commerceOrderAttachment.getFileEntryId());

		Assert.assertEquals(folder.getFolderId(), fileEntry.getFolderId());
	}

	@Test
	public void testDeleteCommerceOrderAttachment() throws Exception {
		CommerceOrderAttachment commerceOrderAttachment =
			_addCommerceOrderAttachment(false);

		_commerceOrderAttachmentLocalService.deleteCommerceOrderAttachment(
			commerceOrderAttachment.getCommerceOrderAttachmentId());

		Assert.assertNull(
			_commerceOrderAttachmentLocalService.fetchCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId()));

		LocalRepository localRepository = _commerceOrder.getLocalRepository();

		Assert.assertNull(
			localRepository.fetchFileEntry(
				commerceOrderAttachment.getFileEntryId()));
	}

	@Test
	public void testDeleteCommerceOrderAttachments() throws Exception {
		CommerceOrderAttachment commerceOrderAttachment1 =
			_addCommerceOrderAttachment(false);
		CommerceOrderAttachment commerceOrderAttachment2 =
			_addCommerceOrderAttachment(true);

		_commerceOrderAttachmentLocalService.deleteCommerceOrderAttachments(
			_commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			0,
			_commerceOrderAttachmentLocalService.
				getCommerceOrderAttachmentsCount(
					_commerceOrder.getCommerceOrderId()));

		LocalRepository localRepository = _commerceOrder.getLocalRepository();

		Assert.assertNull(
			localRepository.fetchFileEntry(
				commerceOrderAttachment1.getFileEntryId()));
		Assert.assertNull(
			localRepository.fetchFileEntry(
				commerceOrderAttachment2.getFileEntryId()));
	}

	@Test
	public void testGetCommerceOrderAttachments() throws Exception {
		_addCommerceOrderAttachment(false);
		_addCommerceOrderAttachment(false);
		_addCommerceOrderAttachment(true);

		List<CommerceOrderAttachment> commerceOrderAttachments =
			_commerceOrderAttachmentLocalService.getCommerceOrderAttachments(
				_commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commerceOrderAttachments.toString(), 3,
			commerceOrderAttachments.size());

		commerceOrderAttachments =
			_commerceOrderAttachmentLocalService.getCommerceOrderAttachments(
				_commerceOrder.getCommerceOrderId(), false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commerceOrderAttachments.toString(), 2,
			commerceOrderAttachments.size());

		commerceOrderAttachments =
			_commerceOrderAttachmentLocalService.getCommerceOrderAttachments(
				_commerceOrder.getCommerceOrderId(), true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commerceOrderAttachments.toString(), 1,
			commerceOrderAttachments.size());
	}

	@Test
	public void testGetCommerceOrderAttachmentsCount() throws Exception {
		_addCommerceOrderAttachment(false);
		_addCommerceOrderAttachment(true);

		Assert.assertEquals(
			2,
			_commerceOrderAttachmentLocalService.
				getCommerceOrderAttachmentsCount(
					_commerceOrder.getCommerceOrderId()));
		Assert.assertEquals(
			1,
			_commerceOrderAttachmentLocalService.
				getCommerceOrderAttachmentsCount(
					_commerceOrder.getCommerceOrderId(), false));
		Assert.assertEquals(
			1,
			_commerceOrderAttachmentLocalService.
				getCommerceOrderAttachmentsCount(
					_commerceOrder.getCommerceOrderId(), true));
	}

	@Test
	public void testUpdateCommerceOrderAttachment() throws Exception {
		CommerceOrderAttachment commerceOrderAttachment =
			_addCommerceOrderAttachment(false);

		try {
			_commerceOrderAttachmentLocalService.updateCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				RandomTestUtil.nextDouble(), false, null, _TYPE_KEY);

			Assert.fail();
		}
		catch (CommerceOrderAttachmentTitleException
					commerceOrderAttachmentTitleException) {

			Assert.assertNotNull(commerceOrderAttachmentTitleException);
		}

		try {
			_commerceOrderAttachmentLocalService.updateCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), null);

			Assert.fail();
		}
		catch (CommerceOrderAttachmentTypeException
					commerceOrderAttachmentTypeException) {

			Assert.assertNotNull(commerceOrderAttachmentTypeException);
		}

		try {
			_commerceOrderAttachmentLocalService.updateCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (CommerceOrderAttachmentTypeException
					commerceOrderAttachmentTypeException) {

			Assert.assertNotNull(commerceOrderAttachmentTypeException);
		}

		double priority = RandomTestUtil.nextDouble();
		String title = RandomTestUtil.randomString();

		commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.updateCommerceOrderAttachment(
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				priority, true, title, _TYPE_KEY);

		Assert.assertEquals(priority, commerceOrderAttachment.getPriority(), 0);
		Assert.assertTrue(commerceOrderAttachment.isRestricted());
		Assert.assertEquals(title, commerceOrderAttachment.getTitle());
		Assert.assertEquals(_TYPE_KEY, commerceOrderAttachment.getType());
	}

	private CommerceOrderAttachment _addCommerceOrderAttachment(
			boolean restricted)
		throws Exception {

		return _commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
			RandomTestUtil.randomString(), _user.getUserId(),
			_commerceOrder.getCommerceOrderId(), RandomTestUtil.nextDouble(),
			restricted, RandomTestUtil.randomString(), _TYPE_KEY,
			RandomTestUtil.randomString(),
			new ByteArrayInputStream("Liferay".getBytes()));
	}

	private static final String _TYPE_KEY = "invoice";

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
	private User _user;

}