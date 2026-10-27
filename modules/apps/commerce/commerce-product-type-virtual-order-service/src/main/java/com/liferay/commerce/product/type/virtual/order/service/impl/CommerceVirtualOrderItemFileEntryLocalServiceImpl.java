/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.impl;

import com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemFileEntryIdException;
import com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemUrlException;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.base.CommerceVirtualOrderItemFileEntryLocalServiceBaseImpl;
import com.liferay.commerce.product.type.virtual.util.VirtualCPTypeURLValidatorUtil;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry",
	service = AopService.class
)
public class CommerceVirtualOrderItemFileEntryLocalServiceImpl
	extends CommerceVirtualOrderItemFileEntryLocalServiceBaseImpl {

	@Override
	public CommerceVirtualOrderItemFileEntry
			addCommerceVirtualOrderItemFileEntry(
				long userId, long groupId, long commerceOrderItemId,
				long fileEntryId, String url, int usages, String version)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long commerceVirtualOrderItemFileEntryId =
			counterLocalService.increment();

		if (Validator.isNotNull(url)) {
			fileEntryId = 0;
		}

		_validate(fileEntryId, url);

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItemFileEntryPersistence.create(
				commerceVirtualOrderItemFileEntryId);

		commerceVirtualOrderItemFileEntry.setGroupId(groupId);
		commerceVirtualOrderItemFileEntry.setCompanyId(user.getCompanyId());
		commerceVirtualOrderItemFileEntry.setUserId(user.getUserId());
		commerceVirtualOrderItemFileEntry.setUserName(user.getFullName());
		commerceVirtualOrderItemFileEntry.setCommerceVirtualOrderItemId(
			commerceOrderItemId);
		commerceVirtualOrderItemFileEntry.setFileEntryId(fileEntryId);
		commerceVirtualOrderItemFileEntry.setUrl(url);
		commerceVirtualOrderItemFileEntry.setUsages(usages);
		commerceVirtualOrderItemFileEntry.setVersion(version);

		return commerceVirtualOrderItemFileEntryPersistence.update(
			commerceVirtualOrderItemFileEntry);
	}

	@Override
	public CommerceVirtualOrderItemFileEntry
		fetchCommerceVirtualOrderItemFileEntry(
			long commerceVirtualOrderItemId, long fileEntryId) {

		return commerceVirtualOrderItemFileEntryPersistence.fetchByC_F_First(
			commerceVirtualOrderItemId, fileEntryId, null);
	}

	@Override
	public List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntries(
			long commerceVirtualOrderItemId) {

		return commerceVirtualOrderItemFileEntryPersistence.
			findByCommerceVirtualOrderItemId(commerceVirtualOrderItemId);
	}

	@Override
	public List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntries(
			long commerceVirtualOrderItemId, int start, int end) {

		return commerceVirtualOrderItemFileEntryPersistence.
			findByCommerceVirtualOrderItemId(
				commerceVirtualOrderItemId, start, end);
	}

	@Override
	public int getCommerceVirtualOrderItemFileEntriesCount(
		long commerceVirtualOrderItemId) {

		return commerceVirtualOrderItemFileEntryPersistence.
			countByCommerceVirtualOrderItemId(commerceVirtualOrderItemId);
	}

	@Override
	public CommerceVirtualOrderItemFileEntry incrementUsages(
			long commerceVirtualOrderItemFileEntryId)
		throws PortalException {

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItemFileEntryPersistence.findByPrimaryKey(
				commerceVirtualOrderItemFileEntryId);

		commerceVirtualOrderItemFileEntry.setUsages(
			commerceVirtualOrderItemFileEntry.getUsages() + 1);

		return commerceVirtualOrderItemFileEntryPersistence.update(
			commerceVirtualOrderItemFileEntry);
	}

	@Override
	public CommerceVirtualOrderItemFileEntry
			updateCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId, long fileEntryId,
				String url, int usages, String version)
		throws PortalException {

		if (Validator.isNotNull(url)) {
			fileEntryId = 0;
		}

		_validate(fileEntryId, url);

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItemFileEntryPersistence.findByPrimaryKey(
				commerceVirtualOrderItemFileEntryId);

		if (Validator.isNotNull(url) &&
			!VirtualCPTypeURLValidatorUtil.isValidURL(
				commerceVirtualOrderItemFileEntry.getCompanyId(), url)) {

			throw new CommerceVirtualOrderItemUrlException();
		}

		commerceVirtualOrderItemFileEntry.setFileEntryId(fileEntryId);
		commerceVirtualOrderItemFileEntry.setUrl(url);
		commerceVirtualOrderItemFileEntry.setUsages(usages);
		commerceVirtualOrderItemFileEntry.setVersion(version);

		return commerceVirtualOrderItemFileEntryPersistence.update(
			commerceVirtualOrderItemFileEntry);
	}

	private void _validate(long fileEntryId, String url)
		throws PortalException {

		if (fileEntryId > 0) {
			try {
				_dlAppLocalService.getFileEntry(fileEntryId);
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				throw new CommerceVirtualOrderItemFileEntryIdException(
					noSuchFileEntryException);
			}
		}
		else if ((fileEntryId < 0) && Validator.isNull(url)) {
			throw new CommerceVirtualOrderItemUrlException();
		}
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private UserLocalService _userLocalService;

}