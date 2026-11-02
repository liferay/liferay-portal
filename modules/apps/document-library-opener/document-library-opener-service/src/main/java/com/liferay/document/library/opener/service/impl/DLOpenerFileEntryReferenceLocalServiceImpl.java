/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.service.impl;

import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.service.base.DLOpenerFileEntryReferenceLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.document.library.opener.model.DLOpenerFileEntryReference",
	service = AopService.class
)
@CTAware
public class DLOpenerFileEntryReferenceLocalServiceImpl
	extends DLOpenerFileEntryReferenceLocalServiceBaseImpl {

	@Override
	public DLOpenerFileEntryReference addPlaceholderDLOpenerFileEntryReference(
			long userId, String referenceType, FileEntry fileEntry, int type)
		throws PortalException {

		return _addDLOpenerFileEntryReference(
			userId, null, referenceType, fileEntry, type);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 *             #deleteDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	@Override
	public void deleteDLOpenerFileEntryReference(FileEntry fileEntry)
		throws PortalException {

		deleteDLOpenerFileEntryReference(_GOOGLE_REFERENCE_TYPE, fileEntry);
	}

	@Override
	public void deleteDLOpenerFileEntryReference(
			String referenceType, FileEntry fileEntry)
		throws PortalException {

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			dlOpenerFileEntryReferencePersistence.findByR_F(
				referenceType, fileEntry.getFileEntryId());

		dlOpenerFileEntryReferenceLocalService.deleteDLOpenerFileEntryReference(
			dlOpenerFileEntryReference);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 *             #fetchDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	@Override
	public DLOpenerFileEntryReference fetchDLOpenerFileEntryReference(
		FileEntry fileEntry) {

		return fetchDLOpenerFileEntryReference(
			_GOOGLE_REFERENCE_TYPE, fileEntry);
	}

	@Override
	public DLOpenerFileEntryReference fetchDLOpenerFileEntryReference(
		String referenceKey, FileEntry fileEntry) {

		return dlOpenerFileEntryReferencePersistence.fetchByR_F(
			referenceKey, fileEntry.getFileEntryId());
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 *             #getDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	@Override
	public DLOpenerFileEntryReference getDLOpenerFileEntryReference(
			FileEntry fileEntry)
		throws PortalException {

		return getDLOpenerFileEntryReference(_GOOGLE_REFERENCE_TYPE, fileEntry);
	}

	@Override
	public DLOpenerFileEntryReference getDLOpenerFileEntryReference(
			String referenceType, FileEntry fileEntry)
		throws PortalException {

		return dlOpenerFileEntryReferencePersistence.findByR_F(
			referenceType, fileEntry.getFileEntryId());
	}

	@Override
	public DLOpenerFileEntryReference updateDLOpenerFileEntryReference(
		String referenceKey, String referenceType, FileEntry fileEntry) {

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			dlOpenerFileEntryReferencePersistence.fetchByR_F(
				referenceType, fileEntry.getFileEntryId());

		dlOpenerFileEntryReference.setReferenceKey(referenceKey);

		return dlOpenerFileEntryReferencePersistence.update(
			dlOpenerFileEntryReference);
	}

	private DLOpenerFileEntryReference _addDLOpenerFileEntryReference(
			long userId, String referenceKey, String referenceType,
			FileEntry fileEntry, int type)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long dlOpenerFileEntryReferenceId = counterLocalService.increment();

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			dlOpenerFileEntryReferenceLocalService.
				createDLOpenerFileEntryReference(dlOpenerFileEntryReferenceId);

		dlOpenerFileEntryReference.setGroupId(fileEntry.getGroupId());
		dlOpenerFileEntryReference.setCompanyId(fileEntry.getCompanyId());
		dlOpenerFileEntryReference.setUserId(user.getUserId());
		dlOpenerFileEntryReference.setUserName(user.getFullName());
		dlOpenerFileEntryReference.setReferenceKey(referenceKey);
		dlOpenerFileEntryReference.setReferenceType(referenceType);
		dlOpenerFileEntryReference.setFileEntryId(fileEntry.getFileEntryId());
		dlOpenerFileEntryReference.setType(type);

		return dlOpenerFileEntryReferencePersistence.update(
			dlOpenerFileEntryReference);
	}

	private static final String _GOOGLE_REFERENCE_TYPE = "GoogleDrive";

	@Reference
	private UserLocalService _userLocalService;

}