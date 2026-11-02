/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.exception.CommerceOrderAttachmentTitleException;
import com.liferay.commerce.exception.CommerceOrderAttachmentTypeException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.service.base.CommerceOrderAttachmentLocalServiceBaseImpl;
import com.liferay.commerce.service.persistence.CommerceOrderPersistence;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceOrderAttachment",
	service = AopService.class
)
public class CommerceOrderAttachmentLocalServiceImpl
	extends CommerceOrderAttachmentLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderAttachment addCommerceOrderAttachment(
			String externalReferenceCode, long userId, long commerceOrderId,
			double priority, boolean restricted, String title, String type,
			String fileName, InputStream inputStream)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderPersistence.findByPrimaryKey(commerceOrderId);

		User user = _userLocalService.getUser(userId);

		_validate(user.getCompanyId(), title, type);

		FileEntry fileEntry = _addFileEntry(
			commerceOrder, fileName, inputStream, user);

		long commerceOrderAttachmentId = counterLocalService.increment();

		CommerceOrderAttachment commerceOrderAttachment =
			commerceOrderAttachmentPersistence.create(
				commerceOrderAttachmentId);

		commerceOrderAttachment.setExternalReferenceCode(externalReferenceCode);
		commerceOrderAttachment.setGroupId(commerceOrder.getGroupId());
		commerceOrderAttachment.setCompanyId(user.getCompanyId());
		commerceOrderAttachment.setUserId(user.getUserId());
		commerceOrderAttachment.setUserName(user.getFullName());
		commerceOrderAttachment.setCommerceOrderId(
			commerceOrder.getCommerceOrderId());
		commerceOrderAttachment.setFileEntryId(fileEntry.getFileEntryId());
		commerceOrderAttachment.setPriority(priority);
		commerceOrderAttachment.setRestricted(restricted);
		commerceOrderAttachment.setTitle(title);
		commerceOrderAttachment.setType(type);

		commerceOrderAttachment = commerceOrderAttachmentPersistence.update(
			commerceOrderAttachment);

		_resourceLocalService.addModelResources(
			commerceOrderAttachment.getCompanyId(),
			commerceOrderAttachment.getGroupId(),
			commerceOrderAttachment.getUserId(),
			CommerceOrderAttachment.class.getName(),
			commerceOrderAttachment.getCommerceOrderAttachmentId(), null, null);

		return commerceOrderAttachment;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public CommerceOrderAttachment deleteCommerceOrderAttachment(
			CommerceOrderAttachment commerceOrderAttachment)
		throws PortalException {

		commerceOrderAttachmentPersistence.remove(commerceOrderAttachment);

		CommerceOrder commerceOrder =
			_commerceOrderPersistence.findByPrimaryKey(
				commerceOrderAttachment.getCommerceOrderId());

		LocalRepository localRepository = commerceOrder.getLocalRepository();

		if (localRepository != null) {
			localRepository.deleteFileEntry(
				commerceOrderAttachment.getFileEntryId());
		}

		_resourceLocalService.deleteResource(
			commerceOrderAttachment.getCompanyId(),
			CommerceOrderAttachment.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			commerceOrderAttachment.getCommerceOrderAttachmentId());

		return commerceOrderAttachment;
	}

	@Override
	public CommerceOrderAttachment deleteCommerceOrderAttachment(
			long commerceOrderAttachmentId)
		throws PortalException {

		return commerceOrderAttachmentLocalService.
			deleteCommerceOrderAttachment(
				commerceOrderAttachmentPersistence.findByPrimaryKey(
					commerceOrderAttachmentId));
	}

	@Override
	public void deleteCommerceOrderAttachments(long commerceOrderId)
		throws PortalException {

		List<CommerceOrderAttachment> commerceOrderAttachments =
			commerceOrderAttachmentPersistence.findByCommerceOrderId(
				commerceOrderId);

		for (CommerceOrderAttachment commerceOrderAttachment :
				commerceOrderAttachments) {

			commerceOrderAttachmentLocalService.deleteCommerceOrderAttachment(
				commerceOrderAttachment);
		}
	}

	@Override
	public List<CommerceOrderAttachment> getCommerceOrderAttachments(
		long commerceOrderId, boolean restricted, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return commerceOrderAttachmentPersistence.findByC_R(
			commerceOrderId, restricted, start, end, orderByComparator);
	}

	@Override
	public List<CommerceOrderAttachment> getCommerceOrderAttachments(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return commerceOrderAttachmentPersistence.findByCommerceOrderId(
			commerceOrderId, start, end, orderByComparator);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceOrderAttachment updateCommerceOrderAttachment(
			long commerceOrderAttachmentId, double priority, boolean restricted,
			String title, String type)
		throws PortalException {

		CommerceOrderAttachment commerceOrderAttachment =
			commerceOrderAttachmentPersistence.findByPrimaryKey(
				commerceOrderAttachmentId);

		_validate(commerceOrderAttachment.getCompanyId(), title, type);

		commerceOrderAttachment.setPriority(priority);
		commerceOrderAttachment.setRestricted(restricted);
		commerceOrderAttachment.setTitle(title);
		commerceOrderAttachment.setType(type);

		return commerceOrderAttachmentPersistence.update(
			commerceOrderAttachment);
	}

	private FileEntry _addFileEntry(
			CommerceOrder commerceOrder, String fileName,
			InputStream inputStream, User user)
		throws PortalException {

		File file = null;

		try {
			DLAppHelperThreadLocal.setEnabled(false);

			LocalRepository localRepository =
				commerceOrder.getLocalRepository();

			Folder folder = commerceOrder.getFolder(localRepository);

			if (folder == null) {
				ServiceContext folderServiceContext = new ServiceContext();

				folderServiceContext.setAddGroupPermissions(true);
				folderServiceContext.setAddGuestPermissions(true);
				folderServiceContext.setCompanyId(commerceOrder.getCompanyId());
				folderServiceContext.setUserId(commerceOrder.getUserId());

				folder = localRepository.addFolder(
					"order-" + commerceOrder.getCommerceOrderId(),
					commerceOrder.getUserId(), 0,
					String.valueOf(commerceOrder.getCommerceOrderId()),
					StringPool.BLANK, folderServiceContext);
			}

			file = FileUtil.createTempFile(inputStream);
			fileName = DLUtil.getUniqueFileName(
				folder.getGroupId(), folder.getFolderId(), fileName, false);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
			serviceContext.setAttribute(
				"className", CommerceOrder.class.getName());
			serviceContext.setAttribute(
				"classPK", String.valueOf(commerceOrder.getCommerceOrderId()));
			serviceContext.setIndexingEnabled(false);

			return localRepository.addFileEntry(
				null, user.getUserId(), folder.getFolderId(), fileName,
				MimeTypesUtil.getContentType(file, fileName), fileName,
				fileName, StringPool.BLANK, StringPool.BLANK, file, null, null,
				null, serviceContext);
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to write temporary file", ioException);
		}
		finally {
			DLAppHelperThreadLocal.setEnabled(true);

			FileUtil.delete(file);
		}
	}

	private void _validate(long companyId, String title, String type)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new CommerceOrderAttachmentTitleException();
		}

		if (Validator.isNull(type)) {
			throw new CommerceOrderAttachmentTypeException();
		}

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER_ATTACHMENT_TYPES", companyId);

		if (listTypeDefinition == null) {
			throw new CommerceOrderAttachmentTypeException();
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				listTypeDefinition.getListTypeDefinitionId(), type);

		if (listTypeEntry == null) {
			throw new CommerceOrderAttachmentTypeException();
		}
	}

	@Reference
	private CommerceOrderPersistence _commerceOrderPersistence;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}