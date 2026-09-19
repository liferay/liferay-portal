/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.internal.document.library.exportimport.data.handler;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntrySerializer;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.adaptive.media.image.processor.AMImageAttribute;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.image.util.AMImageSerializer;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.document.library.constants.DLPortletDataHandlerConstants;
import com.liferay.document.library.exportimport.data.handler.DLPluggableContentDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.xml.Element;

import java.io.InputStream;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.repository.model.FileEntry",
	service = DLPluggableContentDataHandler.class
)
public class AMImageDLPluggableContentDataHandler
	implements DLPluggableContentDataHandler<FileEntry> {

	@Override
	public void exportContent(
			PortletDataContext portletDataContext, Element fileEntryElement,
			FileEntry fileEntry)
		throws Exception {

		if (!_isEnabled(portletDataContext)) {
			return;
		}

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				portletDataContext.getCompanyId());

		amImageConfigurationEntries.forEach(
			amImageConfigurationEntry -> _exportConfigurationEntry(
				portletDataContext, amImageConfigurationEntry));

		_exportMedia(portletDataContext, fileEntry);
	}

	@Override
	public void importContent(
			PortletDataContext portletDataContext, Element fileEntryElement,
			FileEntry fileEntry, FileEntry importedFileEntry)
		throws Exception {

		if (!_isEnabled(portletDataContext)) {
			return;
		}

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				portletDataContext.getCompanyId());

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			_importGeneratedMedia(
				portletDataContext, fileEntry, importedFileEntry,
				amImageConfigurationEntry);
		}
	}

	private void _exportConfigurationEntry(
		PortletDataContext portletDataContext,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		portletDataContext.addZipEntry(
			_getConfigurationEntryBinPath(amImageConfigurationEntry),
			_amImageConfigurationEntrySerializer.serialize(
				amImageConfigurationEntry));
	}

	private void _exportMedia(
			PortletDataContext portletDataContext, FileEntry fileEntry)
		throws Exception {

		FileVersion fileVersion = fileEntry.getFileVersion();

		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
			_amImageFinder.getAdaptiveMedias(
				amImageQueryBuilder -> amImageQueryBuilder.forFileVersion(
					fileVersion
				).done());

		for (AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia :
				adaptiveMedias) {

			_exportMedia(portletDataContext, fileEntry, adaptiveMedia);
		}
	}

	private void _exportMedia(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia)
		throws Exception {

		String configurationUuid = adaptiveMedia.getValue(
			AMAttribute.getConfigurationUuidAMAttribute());

		if (configurationUuid == null) {
			return;
		}

		String basePath = _getAMBasePath(fileEntry, configurationUuid);

		if (!portletDataContext.isPerformDirectBinaryImport()) {
			try (InputStream inputStream = adaptiveMedia.getInputStream()) {
				portletDataContext.addZipEntry(basePath + ".bin", inputStream);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to find adaptive media for file entry ",
							fileEntry.getFileEntryId(), " and configuration ",
							configurationUuid),
						exception);
				}

				return;
			}
		}

		portletDataContext.addZipEntry(
			basePath + ".json", _amImageSerializer.serialize(adaptiveMedia));
	}

	private String _getAMBasePath(FileEntry fileEntry, String uuid) {
		return String.format(
			"adaptive-media/%s/%s/%s", FileEntry.class.getSimpleName(),
			fileEntry.getUuid(), uuid);
	}

	private List<AdaptiveMedia<AMProcessor<FileVersion>>> _getAdaptiveMedias(
		FileEntry fileEntry,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		try {
			FileVersion fileVersion = fileEntry.getFileVersion();

			return _amImageFinder.getAdaptiveMedias(
				amImageQueryBuilder -> amImageQueryBuilder.forFileVersion(
					fileVersion
				).forConfiguration(
					amImageConfigurationEntry.getUUID()
				).done());
		}
		catch (PortalException portalException) {
			_log.error(
				StringBundler.concat(
					"Unable to find adaptive media for file entry ",
					fileEntry.getFileEntryId(), " and configuration ",
					amImageConfigurationEntry.getUUID()),
				portalException);
		}

		return Collections.emptyList();
	}

	private String _getConfigurationEntryBinPath(
		AMImageConfigurationEntry amImageConfigurationEntry) {

		return String.format(
			"adaptive-media/%s.cf", amImageConfigurationEntry.getUUID());
	}

	private AdaptiveMedia<AMProcessor<FileVersion>> _getExportedMedia(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		String basePath = _getAMBasePath(
			fileEntry, amImageConfigurationEntry.getUUID());

		String serializedAdaptiveMedia = portletDataContext.getZipEntryAsString(
			basePath + ".json");

		if (serializedAdaptiveMedia == null) {
			return null;
		}

		if (!portletDataContext.isPerformDirectBinaryImport()) {
			return _amImageSerializer.deserialize(
				serializedAdaptiveMedia,
				() -> portletDataContext.getZipEntryAsInputStream(
					basePath + ".bin"));
		}

		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
			_getAdaptiveMedias(fileEntry, amImageConfigurationEntry);

		if (adaptiveMedias.isEmpty()) {
			return null;
		}

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			adaptiveMedias.get(0);

		return _amImageSerializer.deserialize(
			serializedAdaptiveMedia, adaptiveMedia::getInputStream);
	}

	private void _importGeneratedMedia(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry,
			AMImageConfigurationEntry amImageConfigurationEntry)
		throws Exception {

		String configuration = portletDataContext.getZipEntryAsString(
			_getConfigurationEntryBinPath(amImageConfigurationEntry));

		if (configuration == null) {
			return;
		}

		AMImageConfigurationEntry importedAMImageConfigurationEntry =
			_amImageConfigurationEntrySerializer.deserialize(configuration);

		if (!importedAMImageConfigurationEntry.equals(
				amImageConfigurationEntry)) {

			return;
		}

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			_getExportedMedia(
				portletDataContext, fileEntry, amImageConfigurationEntry);

		if (adaptiveMedia == null) {
			return;
		}

		Long contentLength = adaptiveMedia.getValue(
			AMAttribute.getContentLengthAMAttribute());

		Integer width = adaptiveMedia.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

		Integer height = adaptiveMedia.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT);

		if ((contentLength == null) || (width == null) || (height == null)) {
			return;
		}

		FileVersion importedFileVersion = importedFileEntry.getFileVersion();

		AMImageEntry amImageEntry = _amImageEntryLocalService.fetchAMImageEntry(
			amImageConfigurationEntry.getUUID(),
			importedFileVersion.getFileVersionId());

		if (amImageEntry != null) {
			_amImageEntryLocalService.deleteAMImageEntryFileVersion(
				amImageConfigurationEntry.getUUID(),
				importedFileVersion.getFileVersionId());
		}

		try (InputStream inputStream = adaptiveMedia.getInputStream()) {
			_amImageEntryLocalService.addAMImageEntry(
				amImageConfigurationEntry, importedFileVersion, height, width,
				inputStream, contentLength);
		}
	}

	private boolean _isEnabled(PortletDataContext portletDataContext) {
		return portletDataContext.getBooleanParameter(
			DLPortletDataHandlerConstants.NAMESPACE, "previews-and-thumbnails");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageDLPluggableContentDataHandler.class);

	@Reference
	private AMImageConfigurationEntrySerializer
		_amImageConfigurationEntrySerializer;

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Reference
	private AMImageFinder _amImageFinder;

	@Reference
	private AMImageSerializer _amImageSerializer;

}