/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.processor;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.document.library.thumbnails.internal.configuration.AMSystemImagesConfiguration;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.processor.AMImageAttribute;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.adaptive.media.processor.AMAsyncProcessor;
import com.liferay.adaptive.media.processor.AMAsyncProcessorLocator;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.ImageProcessor;
import com.liferay.document.library.security.io.InputStreamSanitizer;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ImageConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileEntryWrapper;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.FileVersionWrapper;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.adaptive.media.document.library.thumbnails.internal.configuration.AMSystemImagesConfiguration",
	property = "type=" + DLProcessorConstants.IMAGE_PROCESSOR,
	service = DLProcessor.class
)
public class AMImageEntryProcessor implements DLProcessor, ImageProcessor {

	@Override
	public void cleanUp(FileEntry fileEntry) {
	}

	@Override
	public void cleanUp(FileVersion fileVersion) {
		if (fileVersion.getModel() instanceof DLFileVersion) {
			return;
		}

		try {
			_amImageEntryLocalService.deleteAMImageEntryFileVersion(
				fileVersion);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to delete adaptive media for file version " +
					fileVersion.getFileVersionId(),
				portalException);
		}
	}

	@Override
	public void copy(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {
	}

	@Override
	public void exportGeneratedFiles(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		Element fileEntryElement) {
	}

	@Override
	public void generateImages(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {
	}

	@Override
	public Set<String> getImageMimeTypes() {
		return new HashSet<>(
			Arrays.asList(_amImageMimeTypeProvider.getSupportedMimeTypes()));
	}

	@Override
	public InputStream getPreviewAsStream(FileVersion fileVersion)
		throws Exception {

		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
			_getAdaptiveMedias(
				fileVersion,
				_amSystemImagesConfiguration.previewAMConfiguration(),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_PREVIEW_DOCUMENT_MAX_WIDTH),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_PREVIEW_DOCUMENT_MAX_HEIGHT));

		if (_isProcessingRequired(adaptiveMedias, fileVersion)) {
			_processAMImage(fileVersion);

			return fileVersion.getContentStream(false);
		}

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			adaptiveMedias.get(0);

		return adaptiveMedia.getInputStream();
	}

	@Override
	public long getPreviewFileSize(FileVersion fileVersion) throws Exception {
		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
			_getAdaptiveMedias(
				fileVersion,
				_amSystemImagesConfiguration.previewAMConfiguration(),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_PREVIEW_DOCUMENT_MAX_WIDTH),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_PREVIEW_DOCUMENT_MAX_HEIGHT));

		if (_isProcessingRequired(adaptiveMedias, fileVersion)) {
			_processAMImage(fileVersion);

			return fileVersion.getSize();
		}

		return _getValue(adaptiveMedias.get(0));
	}

	@Override
	public String getPreviewType(FileVersion fileVersion) {
		return _getType(fileVersion);
	}

	@Override
	public InputStream getThumbnailAsStream(FileVersion fileVersion, int index)
		throws Exception {

		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
			_getThumbnailAdaptiveMedia(fileVersion, index);

		if (_isProcessingRequired(adaptiveMedias, fileVersion)) {
			_processAMImage(fileVersion);
		}

		if (adaptiveMedias.isEmpty()) {
			return new ByteArrayInputStream(new byte[0]);
		}

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			adaptiveMedias.get(0);

		return adaptiveMedia.getInputStream();
	}

	@Override
	public long getThumbnailFileSize(FileVersion fileVersion, int index)
		throws Exception {

		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
			_getThumbnailAdaptiveMedia(fileVersion, index);

		if (_isProcessingRequired(adaptiveMedias, fileVersion)) {
			_processAMImage(fileVersion);
		}

		if (adaptiveMedias.isEmpty()) {
			return 0L;
		}

		return _getValue(adaptiveMedias.get(0));
	}

	@Override
	public String getThumbnailType(FileVersion fileVersion) {
		return _getType(fileVersion);
	}

	@Override
	public String getType() {
		return DLProcessorConstants.IMAGE_PROCESSOR;
	}

	@Override
	public boolean hasImages(FileVersion fileVersion) {
		try {
			List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
				_getThumbnailAdaptiveMedia(fileVersion);

			if (!_isProcessingRequired(adaptiveMedias, fileVersion)) {
				return true;
			}

			_processAMImage(fileVersion);

			return false;
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return false;
		}
	}

	@Override
	public void importGeneratedFiles(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		FileEntry importedFileEntry, Element fileEntryElement) {
	}

	@Override
	public boolean isImageSupported(FileVersion fileVersion) {
		return _amImageValidator.isValid(fileVersion);
	}

	@Override
	public boolean isImageSupported(String mimeType) {
		return _isMimeTypeSupported(mimeType);
	}

	@Override
	public boolean isSupported(FileVersion fileVersion) {
		return _amImageValidator.isValid(fileVersion);
	}

	@Override
	public boolean isSupported(String mimeType) {
		return _isMimeTypeSupported(mimeType);
	}

	@Override
	public void storeThumbnail(
		long companyId, long groupId, long fileEntryId, long fileVersionId,
		long custom1ImageId, long custom2ImageId, InputStream inputStream,
		String type) {
	}

	@Override
	public void trigger(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_amSystemImagesConfiguration = ConfigurableUtil.createConfigurable(
			AMSystemImagesConfiguration.class, properties);
	}

	private List<AdaptiveMedia<AMProcessor<FileVersion>>> _getAdaptiveMedias(
			FileVersion fileVersion, String configurationUuid, int defaultWidth,
			int defaultHeight)
		throws PortalException {

		if (Validator.isNotNull(configurationUuid)) {
			return _amImageFinder.getAdaptiveMedias(
				amImageQueryBuilder -> amImageQueryBuilder.forFileVersion(
					fileVersion
				).forConfiguration(
					configurationUuid
				).done());
		}

		return _amImageFinder.getAdaptiveMedias(
			amImageQueryBuilder -> amImageQueryBuilder.forFileVersion(
				fileVersion
			).with(
				AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH, defaultWidth
			).with(
				AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT, defaultHeight
			).done());
	}

	private List<AdaptiveMedia<AMProcessor<FileVersion>>>
			_getThumbnailAdaptiveMedia(FileVersion fileVersion)
		throws PortalException {

		return _getThumbnailAdaptiveMedia(fileVersion, 0);
	}

	private List<AdaptiveMedia<AMProcessor<FileVersion>>>
			_getThumbnailAdaptiveMedia(FileVersion fileVersion, int index)
		throws PortalException {

		if (index == _THUMBNAIL_INDEX_CUSTOM_1) {
			return _getAdaptiveMedias(
				fileVersion,
				_amSystemImagesConfiguration.thumbnailCustom1AMConfiguration(),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_WIDTH),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_HEIGHT));
		}

		if (index == _THUMBNAIL_INDEX_CUSTOM_2) {
			return _getAdaptiveMedias(
				fileVersion,
				_amSystemImagesConfiguration.thumbnailCustom2AMConfiguration(),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_WIDTH),
				PrefsPropsUtil.getInteger(
					PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_HEIGHT));
		}

		return _getAdaptiveMedias(
			fileVersion,
			_amSystemImagesConfiguration.thumbnailAMConfiguration(),
			PrefsPropsUtil.getInteger(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_WIDTH),
			PrefsPropsUtil.getInteger(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_HEIGHT));
	}

	private String _getType(FileVersion fileVersion) {
		String type = "png";

		if (fileVersion == null) {
			return type;
		}

		String mimeType = fileVersion.getMimeType();

		if (mimeType.equals(ContentTypes.IMAGE_BMP)) {
			type = ImageConstants.TYPE_BMP;
		}
		else if (mimeType.equals(ContentTypes.IMAGE_GIF)) {
			type = ImageConstants.TYPE_GIF;
		}
		else if (mimeType.equals(ContentTypes.IMAGE_JPEG)) {
			type = ImageConstants.TYPE_JPEG;
		}
		else if (mimeType.equals(ContentTypes.IMAGE_PNG)) {
			type = ImageConstants.TYPE_PNG;
		}
		else if (!_previewGenerationRequired(fileVersion)) {
			type = fileVersion.getExtension();
		}

		return type;
	}

	private Long _getValue(
		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia) {

		Long value = adaptiveMedia.getValue(
			AMAttribute.getContentLengthAMAttribute());

		if (value == null) {
			return 0L;
		}

		return value;
	}

	private boolean _isMimeTypeSupported(String mimeType) {
		return _amImageMimeTypeProvider.isMimeTypeSupported(mimeType);
	}

	private boolean _isProcessingRequired(
		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias,
		FileVersion fileVersion) {

		if (adaptiveMedias.isEmpty()) {
			return true;
		}

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			adaptiveMedias.get(0);

		return _amImageValidator.isProcessingRequired(
			adaptiveMedia, fileVersion);
	}

	private boolean _previewGenerationRequired(FileVersion fileVersion) {
		String mimeType = fileVersion.getMimeType();

		if (mimeType.contains("tiff") || mimeType.contains("tif")) {
			return true;
		}

		return false;
	}

	private void _processAMImage(FileVersion fileVersion) {
		if (!_amImageValidator.isValid(fileVersion)) {
			return;
		}

		try {
			AMAsyncProcessor<FileVersion, ?> amAsyncProcessor =
				_amAsyncProcessorLocator.locateForClass(FileVersion.class);

			amAsyncProcessor.triggerProcess(
				new SafeFileVersion(fileVersion),
				String.valueOf(fileVersion.getFileVersionId()));
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to create lazy adaptive media for file version " +
					fileVersion.getFileVersionId(),
				portalException);
		}
	}

	private static final int _THUMBNAIL_INDEX_CUSTOM_1 = 1;

	private static final int _THUMBNAIL_INDEX_CUSTOM_2 = 2;

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageEntryProcessor.class);

	@Reference
	private AMAsyncProcessorLocator _amAsyncProcessorLocator;

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Reference
	private AMImageFinder _amImageFinder;

	@Reference
	private AMImageMimeTypeProvider _amImageMimeTypeProvider;

	@Reference
	private AMImageValidator _amImageValidator;

	private volatile AMSystemImagesConfiguration _amSystemImagesConfiguration;

	@Reference
	private InputStreamSanitizer _inputStreamSanitizer;

	private class SafeFileEntry extends FileEntryWrapper {

		public SafeFileEntry(FileEntry fileEntry) {
			super(fileEntry);
		}

		@Override
		public InputStream getContentStream() throws PortalException {
			return _inputStreamSanitizer.sanitize(super.getContentStream());
		}

		@Override
		public InputStream getContentStream(String version)
			throws PortalException {

			return _inputStreamSanitizer.sanitize(
				super.getContentStream(version));
		}

		@Override
		public FileVersion getFileVersion() throws PortalException {
			return new SafeFileVersion(super.getFileVersion());
		}

		@Override
		public FileVersion getFileVersion(String version)
			throws PortalException {

			return new SafeFileVersion(super.getFileVersion(version));
		}

		@Override
		public FileVersion getLatestFileVersion() throws PortalException {
			return new SafeFileVersion(super.getLatestFileVersion());
		}

		@Override
		public FileVersion getLatestFileVersion(boolean trusted)
			throws PortalException {

			return new SafeFileVersion(super.getLatestFileVersion(trusted));
		}

	}

	private class SafeFileVersion extends FileVersionWrapper {

		public SafeFileVersion(FileVersion fileVersion) {
			super(fileVersion);
		}

		@Override
		public InputStream getContentStream(boolean incrementCounter)
			throws PortalException {

			return _inputStreamSanitizer.sanitize(
				super.getContentStream(incrementCounter));
		}

		@Override
		public FileEntry getFileEntry() throws PortalException {
			return new SafeFileEntry(super.getFileEntry());
		}

	}

}