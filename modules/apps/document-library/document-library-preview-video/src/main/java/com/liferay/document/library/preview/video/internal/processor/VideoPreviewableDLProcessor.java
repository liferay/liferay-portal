/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.video.internal.processor;

import com.liferay.document.library.kernel.background.task.DLBackgroundTaskExecutorNames;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.VideoProcessor;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.VideoConverter;
import com.liferay.document.library.preview.processor.BasePreviewableDLProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.repository.event.FileVersionPreviewEventListener;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.xml.Element;

import java.awt.image.RenderedImage;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CancellationException;

import org.apache.commons.lang.time.StopWatch;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan González
 * @author Sergio González
 * @author Mika Koivisto
 * @author Ivica Cardic
 */
@Component(
	property = "type=" + DLProcessorConstants.VIDEO_PROCESSOR,
	service = DLProcessor.class
)
public class VideoPreviewableDLProcessor
	extends BasePreviewableDLProcessor implements VideoProcessor {

	@Override
	public void generatePreviews() {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				try {
					String jobName = "generateVideoPreviews-".concat(
						PortalUUIDUtil.generate());

					_backgroundTaskManager.addBackgroundTask(
						UserConstants.USER_ID_DEFAULT,
						BackgroundTaskConstants.GROUP_ID_DEFAULT, jobName,
						DLBackgroundTaskExecutorNames.
							VIDEO_PREVIEW_BACKGROUND_TASK_EXECUTOR,
						HashMapBuilder.<String, Serializable>put(
							BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS,
							true
						).build(),
						new ServiceContext());
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}
				}
			});
	}

	@Override
	public void generateVideo(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion)
		throws Exception {

		_generateVideo(sourceFileVersion, destinationFileVersion);
	}

	@Override
	public InputStream getPreviewAsStream(FileVersion fileVersion, String type)
		throws Exception {

		return doGetPreviewAsStream(fileVersion, type);
	}

	@Override
	public long getPreviewFileSize(FileVersion fileVersion, String type)
		throws Exception {

		return doGetPreviewFileSize(fileVersion, type);
	}

	@Override
	public InputStream getThumbnailAsStream(FileVersion fileVersion, int index)
		throws Exception {

		return doGetThumbnailAsStream(fileVersion, index);
	}

	@Override
	public long getThumbnailFileSize(FileVersion fileVersion, int index)
		throws Exception {

		return doGetThumbnailFileSize(fileVersion, index);
	}

	@Override
	public String getType() {
		return DLProcessorConstants.VIDEO_PROCESSOR;
	}

	@Override
	public Set<String> getVideoMimeTypes() {
		return _videoMimeTypes;
	}

	@Override
	public boolean hasVideo(FileVersion fileVersion) {
		boolean hasVideo = false;

		try {
			hasVideo = _hasVideo(fileVersion);

			if (!hasVideo && isSupported(fileVersion)) {
				_queueGeneration(null, fileVersion);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return hasVideo;
	}

	@Override
	public boolean isAvailable() {
		return _videoConverter.isEnabled();
	}

	@Override
	public boolean isSupported(String mimeType) {
		if (_videoMimeTypes.contains(mimeType) && _videoConverter.isEnabled()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isVideoSupported(FileVersion fileVersion) {
		return isSupported(fileVersion);
	}

	@Override
	public boolean isVideoSupported(String mimeType) {
		return isSupported(mimeType);
	}

	@Override
	public void trigger(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

		super.trigger(sourceFileVersion, destinationFileVersion);

		_queueGeneration(sourceFileVersion, destinationFileVersion);
	}

	@Activate
	protected void activate() {
		boolean valid = true;

		if ((_PREVIEW_TYPES.length == 0) || (_PREVIEW_TYPES.length > 2)) {
			valid = false;
		}
		else {
			for (String previewType : _PREVIEW_TYPES) {
				if (!previewType.equals("mp4") && !previewType.equals("ogv")) {
					valid = false;

					break;
				}
			}
		}

		if (!valid && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Liferay is incorrectly configured to generate video ",
					"previews using video containers other than MP4 or OGV. ",
					"Please change the property \"",
					PropsKeys.DL_FILE_ENTRY_PREVIEW_VIDEO_CONTAINERS,
					"\" in portal.properties."));
		}

		FileUtil.mkdirs(PREVIEW_TMP_PATH);
		FileUtil.mkdirs(THUMBNAIL_TMP_PATH);
	}

	@Override
	protected void doExportGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement)
		throws Exception {

		exportThumbnails(
			portletDataContext, fileEntry, fileEntryElement, "video");

		exportPreviews(portletDataContext, fileEntry, fileEntryElement);
	}

	@Override
	protected void doImportGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement)
		throws Exception {

		importThumbnails(
			portletDataContext, fileEntry, importedFileEntry, fileEntryElement,
			"video");

		importPreviews(
			portletDataContext, fileEntry, importedFileEntry, fileEntryElement);
	}

	protected void exportPreviews(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement)
		throws Exception {

		FileVersion fileVersion = fileEntry.getFileVersion();

		if (!isSupported(fileVersion) || !hasPreviews(fileVersion)) {
			return;
		}

		if (!portletDataContext.isPerformDirectBinaryImport()) {
			if ((_PREVIEW_TYPES.length == 0) || (_PREVIEW_TYPES.length > 2)) {
				return;
			}

			for (String previewType : _PREVIEW_TYPES) {
				if (previewType.equals("mp4") || previewType.equals("ogv")) {
					exportPreview(
						portletDataContext, fileEntry, fileEntryElement,
						"video", previewType);
				}
			}
		}
	}

	@Override
	protected List<Long> getFileVersionIds() {
		return _fileVersionIds;
	}

	@Override
	protected String getPreviewType(FileVersion fileVersion) {
		return _PREVIEW_TYPES[0];
	}

	@Override
	protected String[] getPreviewTypes() {
		return _PREVIEW_TYPES;
	}

	@Override
	protected String getThumbnailType(FileVersion fileVersion) {
		return THUMBNAIL_TYPE;
	}

	protected void importPreviews(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement)
		throws Exception {

		if ((_PREVIEW_TYPES.length == 0) || (_PREVIEW_TYPES.length > 2)) {
			return;
		}

		for (String previewType : _PREVIEW_TYPES) {
			if (previewType.equals("mp4") || previewType.equals("ogv")) {
				importPreview(
					portletDataContext, fileEntry, importedFileEntry,
					fileEntryElement, "video", previewType);
			}
		}
	}

	@Override
	protected void storeThumbnailImages(FileVersion fileVersion, File file)
		throws Exception {

		if (!hasThumbnail(fileVersion, THUMBNAIL_INDEX_DEFAULT)) {
			addFileToStore(
				fileVersion.getCompanyId(), THUMBNAIL_PATH,
				getThumbnailFilePath(fileVersion, THUMBNAIL_INDEX_DEFAULT),
				file);
		}

		if (isThumbnailEnabled(THUMBNAIL_INDEX_CUSTOM_1) ||
			isThumbnailEnabled(THUMBNAIL_INDEX_CUSTOM_2)) {

			ImageBag imageBag = ImageToolUtil.read(file);

			RenderedImage renderedImage = imageBag.getRenderedImage();

			storeThumbnailImage(
				fileVersion, renderedImage, THUMBNAIL_INDEX_CUSTOM_1);
			storeThumbnailImage(
				fileVersion, renderedImage, THUMBNAIL_INDEX_CUSTOM_2);
		}
	}

	private void _generateThumbnail(FileVersion fileVersion, File file) {
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		File thumbnailTempFile = getThumbnailTempFile(
			DLUtil.getTempFileId(
				fileVersion.getFileEntryId(), fileVersion.getVersion()));

		try {
			try {
				FileUtil.write(
					thumbnailTempFile,
					_videoConverter.generateVideoThumbnail(
						file, THUMBNAIL_TYPE));
			}
			catch (CancellationException cancellationException) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Cancellation received for ",
							fileVersion.getFileVersionId(), " ",
							fileVersion.getTitle()),
						cancellationException);
				}
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to process ", fileVersion.getFileVersionId(),
						" ", fileVersion.getTitle()),
					exception);
			}

			storeThumbnailImages(fileVersion, thumbnailTempFile);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Generated a thumbnail for ", fileVersion.getTitle(),
						" in ", stopWatch.getTime(), " ms"));
			}
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			FileUtil.delete(thumbnailTempFile);
		}
	}

	private void _generateVideo(
			FileVersion fileVersion, File sourceFile, File destinationFile,
			String containerType)
		throws Exception {

		if (hasPreview(fileVersion, containerType)) {
			return;
		}

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			FileUtil.write(
				destinationFile,
				_videoConverter.generateVideoPreview(
					sourceFile, containerType));

			_fileVersionPreviewEventListener.onSuccess(fileVersion);
		}
		catch (Exception exception) {
			_fileVersionPreviewEventListener.onFailure(fileVersion);

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to process ", fileVersion.getFileVersionId(),
						" ", fileVersion.getTitle()));
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw exception;
		}

		addFileToStore(
			fileVersion.getCompanyId(), PREVIEW_PATH,
			getPreviewFilePath(fileVersion, containerType), destinationFile);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Generated a ", containerType, " preview video for ",
					fileVersion.getTitle(), " in ", stopWatch.getTime(),
					" ms"));
		}
	}

	private void _generateVideo(
		FileVersion fileVersion, File sourceFile, File[] destinationFiles) {

		try {
			for (int i = 0; i < destinationFiles.length; i++) {
				_generateVideo(
					fileVersion, sourceFile, destinationFiles[i],
					_PREVIEW_TYPES[i]);
			}
		}
		catch (CancellationException cancellationException) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Cancellation received for ",
						fileVersion.getFileVersionId(), " ",
						fileVersion.getTitle()),
					cancellationException);
			}

			_fileVersionPreviewEventListener.onFailure(fileVersion);
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to process ", fileVersion.getFileVersionId(), " ",
					fileVersion.getTitle()),
				exception);

			_fileVersionPreviewEventListener.onFailure(fileVersion);
		}
	}

	private void _generateVideo(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion)
		throws Exception {

		if (!_videoConverter.isEnabled() || _hasVideo(destinationFileVersion)) {
			return;
		}

		File[] previewTempFiles = new File[_PREVIEW_TYPES.length];

		File videoTempFile = null;

		try {
			if (sourceFileVersion != null) {
				copy(sourceFileVersion, destinationFileVersion);

				return;
			}

			if (!hasPreviews(destinationFileVersion) ||
				!hasThumbnails(destinationFileVersion)) {

				try (InputStream inputStream =
						destinationFileVersion.getContentStream(false)) {

					videoTempFile = FileUtil.createTempFile(
						destinationFileVersion.getExtension());

					FileUtil.write(videoTempFile, inputStream);
				}
			}

			if (!hasPreviews(destinationFileVersion)) {
				String tempFileId = DLUtil.getTempFileId(
					destinationFileVersion.getFileEntryId(),
					destinationFileVersion.getVersion());

				for (int i = 0; i < _PREVIEW_TYPES.length; i++) {
					previewTempFiles[i] = getPreviewTempFile(
						tempFileId, _PREVIEW_TYPES[i]);
				}

				try {
					_generateVideo(
						destinationFileVersion, videoTempFile,
						previewTempFiles);
				}
				catch (Exception exception) {
					_fileVersionPreviewEventListener.onFailure(
						destinationFileVersion);

					_log.error(exception);
				}
			}

			if (!hasThumbnails(destinationFileVersion)) {
				try {
					_generateThumbnail(destinationFileVersion, videoTempFile);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileEntryException);
			}

			_fileVersionPreviewEventListener.onFailure(destinationFileVersion);
		}
		finally {
			_fileVersionIds.remove(destinationFileVersion.getFileVersionId());

			for (File previewTempFile : previewTempFiles) {
				FileUtil.delete(previewTempFile);
			}

			FileUtil.delete(videoTempFile);
		}
	}

	private boolean _hasVideo(FileVersion fileVersion) throws Exception {
		if (hasPreviews(fileVersion) && hasThumbnails(fileVersion)) {
			return true;
		}

		return false;
	}

	private void _queueGeneration(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

		if (_fileVersionIds.contains(
				destinationFileVersion.getFileVersionId()) ||
			!isSupported(destinationFileVersion)) {

			return;
		}

		_fileVersionIds.add(destinationFileVersion.getFileVersionId());

		sendGenerationMessage(
			DestinationNames.DOCUMENT_LIBRARY_VIDEO_PROCESSOR,
			sourceFileVersion, destinationFileVersion);
	}

	private static final String[] _PREVIEW_TYPES =
		PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_CONTAINERS;

	private static final Log _log = LogFactoryUtil.getLog(
		VideoPreviewableDLProcessor.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private CompanyLocalService _companyLocalService;

	private final List<Long> _fileVersionIds = new Vector<>();

	@Reference
	private FileVersionPreviewEventListener _fileVersionPreviewEventListener;

	@Reference
	private VideoConverter _videoConverter;

	private final Set<String> _videoMimeTypes = SetUtil.fromArray(
		PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_MIME_TYPES);

}