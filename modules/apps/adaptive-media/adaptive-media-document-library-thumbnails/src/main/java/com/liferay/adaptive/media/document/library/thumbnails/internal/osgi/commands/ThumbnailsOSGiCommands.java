/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.osgi.commands;

import com.liferay.adaptive.media.document.library.thumbnails.internal.osgi.commands.configuration.ThumbnailConfiguration;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.preview.processor.BasePreviewableDLProcessor;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;

import java.awt.image.RenderedImage;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.command.function=check", "osgi.command.function=cleanUp",
		"osgi.command.function=migrate", "osgi.command.scope=thumbnails"
	},
	service = OSGiCommands.class
)
public class ThumbnailsOSGiCommands implements OSGiCommands {

	public void check(String... companyIds) {
		System.out.println("Company ID\t# of thumbnails pending migration");
		System.out.println("-------------------------------------------------");

		AtomicInteger count = new AtomicInteger();

		_companyLocalService.forEachCompanyId(
			companyId -> _countPendingThumbnails(companyId, count));

		System.out.printf("%nTOTAL: %d%n", count.get());
	}

	public void cleanUp(String... companyIds) {
		_companyLocalService.forEachCompanyId(
			companyId -> _cleanUp(companyId), _getCompanyIds(companyIds));
	}

	public void migrate(String... companyIds) throws PortalException {
		_companyLocalService.forEachCompanyId(
			companyId -> _migrate(companyId), _getCompanyIds(companyIds));
	}

	private void _cleanUp(long companyId) {
		String[] fileNames = _store.getFileNames(
			companyId, BasePreviewableDLProcessor.REPOSITORY_ID,
			BasePreviewableDLProcessor.THUMBNAIL_PATH);

		for (String fileName : fileNames) {

			// See LPS-70788

			String actualFileName = StringUtil.replace(
				fileName, "//", StringPool.SLASH);

			for (ThumbnailConfiguration thumbnailConfiguration :
					_getThumbnailConfigurations()) {

				FileVersion fileVersion = _getFileVersion(
					thumbnailConfiguration.getFileVersionId(actualFileName));

				if (fileVersion == null) {
					continue;
				}

				_store.deleteDirectory(
					companyId, BasePreviewableDLProcessor.REPOSITORY_ID,
					actualFileName);
			}
		}
	}

	private void _countPendingThumbnails(Long companyId, AtomicInteger count) {
		String[] fileNames = _store.getFileNames(
			companyId, BasePreviewableDLProcessor.REPOSITORY_ID,
			BasePreviewableDLProcessor.THUMBNAIL_PATH);

		int companyTotal = 0;

		for (String fileName : fileNames) {

			// See LPS-70788

			String actualFileName = StringUtil.replace(
				fileName, StringPool.DOUBLE_SLASH, StringPool.SLASH);

			for (ThumbnailConfiguration thumbnailConfiguration :
					_getThumbnailConfigurations()) {

				FileVersion fileVersion = _getFileVersion(
					thumbnailConfiguration.getFileVersionId(actualFileName));

				if (fileVersion == null) {
					continue;
				}

				companyTotal = +1;
			}
		}

		System.out.printf("%d\t\t%d%n", companyId, companyTotal);

		count.addAndGet(companyTotal);
	}

	private long[] _getCompanyIds(String... companyIds) {
		if (companyIds.length == 0) {
			return ListUtil.toLongArray(
				_companyLocalService.getCompanies(), Company::getCompanyId);
		}

		return ListUtil.toLongArray(Arrays.asList(companyIds), Long::parseLong);
	}

	private FileVersion _getFileVersion(long fileVersionId) {
		try {
			if (fileVersionId == 0) {
				return null;
			}

			FileVersion fileVersion = _dlAppLocalService.getFileVersion(
				fileVersionId);

			if (!_isMimeTypeSupported(fileVersion)) {
				return null;
			}

			return fileVersion;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private ThumbnailConfiguration[] _getThumbnailConfigurations() {
		return new ThumbnailConfiguration[] {
			new ThumbnailConfiguration(
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_WIDTH),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_HEIGHT),
				Pattern.compile(
					BasePreviewableDLProcessor.THUMBNAIL_PATH +
						"\\d+/\\d+(?:/\\d+)+/(\\d+)(?:\\..+)?$")),
			new ThumbnailConfiguration(
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_WIDTH),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_HEIGHT),
				Pattern.compile(
					BasePreviewableDLProcessor.THUMBNAIL_PATH +
						"\\d+/\\d+(?:/\\d+)+/(\\d+)-1(?:\\..+)?$")),
			new ThumbnailConfiguration(
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_WIDTH),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_HEIGHT),
				Pattern.compile(
					BasePreviewableDLProcessor.THUMBNAIL_PATH +
						"\\d+/\\d+(?:/\\d+)+/(\\d+)-2(?:\\..+)?$"))
		};
	}

	private boolean _isMimeTypeSupported(FileVersion fileVersion) {
		return ArrayUtil.contains(
			_amImageMimeTypeProvider.getSupportedMimeTypes(),
			fileVersion.getMimeType());
	}

	private boolean _isValidConfigurationEntries(
		Collection<AMImageConfigurationEntry> amImageConfigurationEntries) {

		for (ThumbnailConfiguration thumbnailConfiguration :
				_getThumbnailConfigurations()) {

			for (AMImageConfigurationEntry amImageConfigurationEntry :
					amImageConfigurationEntries) {

				if (thumbnailConfiguration.matches(amImageConfigurationEntry)) {
					return true;
				}
			}
		}

		return false;
	}

	private void _migrate(Long companyId) throws PortalException {
		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				companyId);

		if (!_isValidConfigurationEntries(amImageConfigurationEntries)) {
			throw new PortalException(
				"No valid Adaptive Media configuration found. Please refer " +
					"to the upgrade documentation for the details.");
		}

		String[] fileNames = _store.getFileNames(
			companyId, BasePreviewableDLProcessor.REPOSITORY_ID,
			BasePreviewableDLProcessor.THUMBNAIL_PATH);

		for (String fileName : fileNames) {

			// See LPS-70788

			String actualFileName = StringUtil.replace(
				fileName, "//", StringPool.SLASH);

			for (ThumbnailConfiguration thumbnailConfiguration :
					_getThumbnailConfigurations()) {

				AMImageConfigurationEntry amImageConfigurationEntry =
					thumbnailConfiguration.selectMatchingConfigurationEntry(
						amImageConfigurationEntries);

				if (amImageConfigurationEntry == null) {
					continue;
				}

				_migrate(
					actualFileName, amImageConfigurationEntry,
					thumbnailConfiguration);
			}
		}
	}

	private void _migrate(
		String fileName, AMImageConfigurationEntry amImageConfigurationEntry,
		ThumbnailConfiguration thumbnailConfiguration) {

		try {
			FileVersion fileVersion = _getFileVersion(
				thumbnailConfiguration.getFileVersionId(fileName));

			if (fileVersion == null) {
				return;
			}

			AMImageEntry amImageEntry =
				_amImageEntryLocalService.fetchAMImageEntry(
					amImageConfigurationEntry.getUUID(),
					fileVersion.getFileVersionId());

			if (amImageEntry != null) {
				return;
			}

			byte[] bytes = StreamUtil.toByteArray(
				_store.getFileAsStream(
					fileVersion.getCompanyId(),
					BasePreviewableDLProcessor.REPOSITORY_ID, fileName,
					StringPool.BLANK));

			ImageBag imageBag = ImageToolUtil.read(bytes);

			RenderedImage renderedImage = imageBag.getRenderedImage();

			_amImageEntryLocalService.addAMImageEntry(
				amImageConfigurationEntry, fileVersion,
				renderedImage.getHeight(), renderedImage.getWidth(),
				new UnsyncByteArrayInputStream(bytes), bytes.length);
		}
		catch (IOException | PortalException exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ThumbnailsOSGiCommands.class);

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Reference
	private AMImageMimeTypeProvider _amImageMimeTypeProvider;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference(target = "(default=true)")
	private Store _store;

}