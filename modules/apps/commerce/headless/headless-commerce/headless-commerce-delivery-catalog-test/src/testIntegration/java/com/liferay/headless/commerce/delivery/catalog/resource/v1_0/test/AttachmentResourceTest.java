/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Attachment;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class AttachmentResourceTest extends BaseAttachmentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), RandomTestUtil.randomString());
		_cpDefinition = CPTestUtil.addCPDefinition(
			testGroup.getGroupId(), "simple", true, false);
	}

	@Override
	protected Attachment randomAttachment() throws Exception {
		return new Attachment() {
			{
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				neverExpire = true;
				priority = RandomTestUtil.randomDouble();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = CPAttachmentFileEntryConstants.TYPE_OTHER;
			}
		};
	}

	@Override
	protected Attachment testGetChannelProductAttachmentsPage_addAttachment(
			Long channelId, Long productId, Attachment attachment)
		throws Exception {

		return _addCPAttachmentFileEntry(attachment);
	}

	@Override
	protected Long testGetChannelProductAttachmentsPage_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Long testGetChannelProductAttachmentsPage_getProductId()
		throws Exception {

		return _cpDefinition.getCProductId();
	}

	@Override
	protected Attachment testGetChannelProductImagesPage_addAttachment(
			Long channelId, Long productId, Attachment attachment)
		throws Exception {

		attachment.setType(CPAttachmentFileEntryConstants.TYPE_IMAGE);

		return _addCPAttachmentFileEntry(attachment);
	}

	@Override
	protected Long testGetChannelProductImagesPage_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected Long testGetChannelProductImagesPage_getProductId()
		throws Exception {

		return _cpDefinition.getCProductId();
	}

	private Attachment _addCPAttachmentFileEntry(Attachment attachment)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			testGroup.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.nextDate(), _serviceContext);

		Calendar displayDate = Calendar.getInstance();
		Calendar expirationDate = Calendar.getInstance();

		displayDate.setTime(attachment.getDisplayDate());
		expirationDate.setTime(attachment.getExpirationDate());

		CPAttachmentFileEntry attachmentFileEntry =
			_cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
				RandomTestUtil.randomString(), _user.getUserId(),
				testGroup.getGroupId(),
				_classNameLocalService.getClassNameId(CPDefinition.class),
				_cpDefinition.getCPDefinitionId(), fileEntry.getFileEntryId(),
				false, null, displayDate.get(Calendar.MONTH),
				displayDate.get(Calendar.DAY_OF_MONTH),
				displayDate.get(Calendar.YEAR), displayDate.get(Calendar.HOUR),
				displayDate.get(Calendar.MINUTE),
				expirationDate.get(Calendar.MONTH),
				expirationDate.get(Calendar.DAY_OF_MONTH),
				expirationDate.get(Calendar.YEAR),
				expirationDate.get(Calendar.HOUR),
				expirationDate.get(Calendar.MINUTE), true, true,
				RandomTestUtil.randomLocaleStringMap(), null,
				RandomTestUtil.nextDouble(), attachment.getType(),
				_serviceContext);

		_attachmentFileEntries.add(attachmentFileEntry);

		return new Attachment() {
			{
				displayDate = attachmentFileEntry.getDisplayDate();
				expirationDate = attachmentFileEntry.getExpirationDate();
				id = attachmentFileEntry.getCPAttachmentFileEntryId();
				priority = attachmentFileEntry.getPriority();
				title = attachmentFileEntry.getTitle();
				type = attachmentFileEntry.getType();
			}
		};
	}

	@DeleteAfterTestRun
	private final List<CPAttachmentFileEntry> _attachmentFileEntries =
		new ArrayList<>();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}