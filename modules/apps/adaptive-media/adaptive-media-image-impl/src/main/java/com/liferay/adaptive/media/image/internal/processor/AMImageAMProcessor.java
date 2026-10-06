/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.processor;

import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.adaptive.media.image.scaler.AMImageScaledImage;
import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.adaptive.media.image.scaler.AMImageScalerRegistry;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.FileVersionWrapper;
import com.liferay.portal.kernel.util.ContentTypes;

import java.io.IOException;
import java.io.InputStream;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.repository.model.FileVersion",
	service = AMProcessor.class
)
public final class AMImageAMProcessor implements AMProcessor<FileVersion> {

	@Override
	public void cleanUp(FileVersion fileVersion) throws PortalException {
		if (!_amImageValidator.isValid(fileVersion)) {
			return;
		}

		_amImageEntryLocalService.deleteAMImageEntryFileVersion(fileVersion);
	}

	@Override
	public void process(FileVersion fileVersion) throws PortalException {
		if (!_amImageValidator.isProcessingSupported(fileVersion)) {
			return;
		}

		Iterable<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				fileVersion.getCompanyId());

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			process(fileVersion, amImageConfigurationEntry.getUUID());
		}
	}

	@Override
	public void process(FileVersion fileVersion, String configurationEntryUuid)
		throws PortalException {

		if (!_amImageValidator.isProcessingSupported(fileVersion) ||
			!_hasDLFileVersion(fileVersion)) {

			return;
		}

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				fileVersion.getCompanyId(), configurationEntryUuid);

		if (amImageConfigurationEntry == null) {
			return;
		}

		AMImageEntry amImageEntry = _amImageEntryLocalService.fetchAMImageEntry(
			amImageConfigurationEntry.getUUID(),
			fileVersion.getFileVersionId());

		try {
			if (!_isUpdateImageEntry(amImageEntry, fileVersion)) {
				return;
			}

			AMImageScaler amImageScaler =
				_amImageScalerRegistry.getAMImageScaler(
					fileVersion.getMimeType());

			if (amImageScaler == null) {
				return;
			}

			AMImageScaledImage amImageScaledImage = amImageScaler.scaleImage(
				fileVersion, amImageConfigurationEntry);

			try (InputStream inputStream =
					amImageScaledImage.getInputStream()) {

				FileVersion scaledFileVersion = _getScaledFileVersion(
					amImageScaledImage, fileVersion);

				if (amImageEntry != null) {
					_amImageEntryLocalService.deleteAMImageEntry(
						amImageEntry.getAmImageEntryId());
				}

				_amImageEntryLocalService.addAMImageEntry(
					amImageConfigurationEntry, scaledFileVersion,
					amImageScaledImage.getHeight(),
					amImageScaledImage.getWidth(), inputStream,
					amImageScaledImage.getSize());
			}

			if (!_hasDLFileVersion(fileVersion)) {
				_amImageEntryLocalService.deleteAMImageEntryFileVersion(
					fileVersion);
			}
		}
		catch (IOException ioException) {
			throw new AMRuntimeException.IOException(ioException);
		}
	}

	private FileVersion _getScaledFileVersion(
		AMImageScaledImage amImageScaledImage, FileVersion fileVersion) {

		String mimeType = amImageScaledImage.getMimeType();

		if ((mimeType == null) || !mimeType.equals(fileVersion.getMimeType()) ||
			mimeType.equals(ContentTypes.APPLICATION_OCTET_STREAM)) {

			return fileVersion;
		}

		return new FileVersionWrapper(fileVersion) {

			@Override
			public String getMimeType() {
				return mimeType;
			}

		};
	}

	private boolean _hasDLFileVersion(FileVersion fileVersion) {
		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.fetchDLFileVersion(
				fileVersion.getFileVersionId());

		if (dlFileVersion != null) {
			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"File version " + fileVersion.getFileVersionId() +
					" was deleted");
		}

		return false;
	}

	private boolean _isUpdateImageEntry(
			AMImageEntry amImageEntry, FileVersion fileVersion)
		throws PortalException {

		if ((amImageEntry == null) ||
			!_amImageEntryLocalService.hasAMImageEntryContent(
				amImageEntry.getConfigurationUuid(), fileVersion)) {

			return true;
		}

		FileEntry fileEntry = fileVersion.getFileEntry();

		Date amImageEntryCreationDate = amImageEntry.getCreateDate();

		if (fileEntry.isCheckedOut() ||
			amImageEntryCreationDate.before(fileVersion.getModifiedDate())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageAMProcessor.class);

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Reference
	private AMImageScalerRegistry _amImageScalerRegistry;

	@Reference
	private AMImageValidator _amImageValidator;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

}