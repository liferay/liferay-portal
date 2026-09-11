/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.service;

import com.liferay.document.library.configuration.DLFileEntryFriendlyURLConfiguration;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceWrapper;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.InputStream;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLFileEntryFriendlyURLConfiguration",
	service = ServiceWrapper.class
)
public class FriendlyURLDLFileEntryLocalServiceWrapper
	extends DLFileEntryLocalServiceWrapper {

	@Override
	public DLFileEntry addFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, long folderId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, long fileEntryTypeId,
			Map<String, DDMFormValues> ddmFormValuesMap, File file,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry dlFileEntry = super.addFileEntry(
			externalReferenceCode, userId, groupId, repositoryId, folderId,
			sourceFileName, mimeType, title, urlTitle, description, changeLog,
			fileEntryTypeId, ddmFormValuesMap, file, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);

		if (!ExportImportThreadLocal.isImportInProcess()) {
			_addFriendlyURLEntry(
				dlFileEntry,
				_getNormalizedURLTitle(
					dlFileEntry, _getUrlTitle(title, urlTitle)));
		}

		return dlFileEntry;
	}

	@Override
	public DLFileEntry deleteFileEntry(DLFileEntry dlFileEntry)
		throws PortalException {

		dlFileEntry = super.deleteFileEntry(dlFileEntry);

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			dlFileEntry.getGroupId(),
			_classNameLocalService.getClassNameId(FileEntry.class),
			dlFileEntry.getFileEntryId());

		return dlFileEntry;
	}

	@Override
	public DLFileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			long fileEntryTypeId, Map<String, DDMFormValues> ddmFormValuesMap,
			File file, InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry dlFileEntry = super.updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, fileEntryTypeId,
			ddmFormValuesMap, file, inputStream, size, displayDate,
			expirationDate, reviewDate, serviceContext);

		if (!ExportImportThreadLocal.isImportInProcess()) {
			_updateFriendlyURL(dlFileEntry, title, urlTitle);
		}

		return dlFileEntry;
	}

	private void _addFriendlyURLEntry(DLFileEntry dlFileEntry, String urlTitle)
		throws PortalException {

		String uniqueUrlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
			dlFileEntry.getGroupId(),
			_classNameLocalService.getClassNameId(FileEntry.class),
			dlFileEntry.getFileEntryId(), urlTitle);

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			dlFileEntry.getGroupId(),
			_classNameLocalService.getClassNameId(FileEntry.class),
			dlFileEntry.getFileEntryId(), uniqueUrlTitle,
			ServiceContextThreadLocal.getServiceContext());
	}

	private String _getNormalizedURLTitle(
			DLFileEntry dlFileEntry, String urlTitle)
		throws PortalException {

		DLFileEntryFriendlyURLConfiguration
			dlFileEntryFriendlyURLConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					DLFileEntryFriendlyURLConfiguration.class,
					dlFileEntry.getCompanyId());

		if (dlFileEntryFriendlyURLConfiguration.
				enableFriendlyURLWithExtension() &&
			Validator.isNotNull(FileUtil.getExtension(urlTitle))) {

			String normalizedURLTitle =
				_friendlyURLNormalizer.normalizeWithPeriodsAndSlashes(
					FileUtil.stripExtension(urlTitle));

			return normalizedURLTitle + StringPool.PERIOD +
				dlFileEntry.getExtension();
		}

		return _friendlyURLNormalizer.normalizeWithPeriodsAndSlashes(urlTitle);
	}

	private String _getUrlTitle(String title, String urlTitle) {
		if (!Validator.isBlank(urlTitle)) {
			return urlTitle;
		}

		return title;
	}

	private void _updateFriendlyURL(
			DLFileEntry dlFileEntry, String title, String urlTitle)
		throws PortalException {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				_classNameLocalService.getClassNameId(FileEntry.class),
				dlFileEntry.getFileEntryId());

		if (friendlyURLEntry == null) {
			_addFriendlyURLEntry(
				dlFileEntry,
				_getNormalizedURLTitle(
					dlFileEntry, _getUrlTitle(title, urlTitle)));

			return;
		}

		String normalizedUrlTitle = _getNormalizedURLTitle(
			dlFileEntry, urlTitle);

		if (Validator.isNotNull(urlTitle) &&
			!Objects.equals(
				friendlyURLEntry.getUrlTitle(), normalizedUrlTitle)) {

			_addFriendlyURLEntry(dlFileEntry, normalizedUrlTitle);
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

}