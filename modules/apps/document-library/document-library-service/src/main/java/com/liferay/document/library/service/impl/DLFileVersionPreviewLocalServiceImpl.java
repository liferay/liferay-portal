/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.impl;

import com.liferay.document.library.model.DLFileVersionPreview;
import com.liferay.document.library.service.base.DLFileVersionPreviewLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.document.library.model.DLFileVersionPreview",
	service = AopService.class
)
public class DLFileVersionPreviewLocalServiceImpl
	extends DLFileVersionPreviewLocalServiceBaseImpl {

	@Override
	public void addDLFileVersionPreview(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws PortalException {

		DLFileVersionPreview dlFileVersionPreview =
			dlFileVersionPreviewPersistence.create(
				counterLocalService.increment());

		dlFileVersionPreview.setFileEntryId(fileEntryId);
		dlFileVersionPreview.setFileVersionId(fileVersionId);
		dlFileVersionPreview.setPreviewStatus(previewStatus);

		dlFileVersionPreviewPersistence.update(dlFileVersionPreview);
	}

	@Override
	public void deleteDLFileEntryFileVersionPreviews(long fileEntryId) {
		dlFileVersionPreviewPersistence.removeByFileEntryId(fileEntryId);
	}

	@Override
	public DLFileVersionPreview fetchDLFileVersionPreview(
		long fileEntryId, long fileVersionId) {

		return dlFileVersionPreviewPersistence.fetchByF_F(
			fileEntryId, fileVersionId);
	}

	@Override
	public boolean hasDLFileVersionPreview(
		long fileEntryId, long fileVersionId, int previewStatus) {

		DLFileVersionPreview dlFileVersionPreview =
			dlFileVersionPreviewPersistence.fetchByF_F_P(
				fileEntryId, fileVersionId, previewStatus);

		if (dlFileVersionPreview == null) {
			return false;
		}

		return true;
	}

	@Override
	public void updateDLFileVersionPreview(
			long dlFileVersionPreviewId, int previewStatus)
		throws PortalException {

		DLFileVersionPreview dlFileVersionPreview =
			dlFileVersionPreviewPersistence.findByPrimaryKey(
				dlFileVersionPreviewId);

		dlFileVersionPreview.setPreviewStatus(previewStatus);

		dlFileVersionPreviewPersistence.update(dlFileVersionPreview);
	}

}