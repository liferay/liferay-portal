/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.audio.internal.processor;

import com.liferay.document.library.kernel.background.task.DLBackgroundTaskExecutorNames;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.AudioProcessor;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.util.AudioConverter;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.preview.processor.BasePreviewableDLProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.PortalException;
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

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Set;
import java.util.Vector;

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
	property = "type=" + DLProcessorConstants.AUDIO_PROCESSOR,
	service = DLProcessor.class
)
public class AudioPreviewableDLProcessor
	extends BasePreviewableDLProcessor implements AudioProcessor {

	@Override
	public void generateAudio(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion)
		throws Exception {

		_generateAudio(sourceFileVersion, destinationFileVersion);
	}

	@Override
	public void generatePreviews() {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				try {
					String jobName = "generateAudioPreviews-".concat(
						PortalUUIDUtil.generate());

					_backgroundTaskManager.addBackgroundTask(
						UserConstants.USER_ID_DEFAULT,
						BackgroundTaskConstants.GROUP_ID_DEFAULT, jobName,
						DLBackgroundTaskExecutorNames.
							AUDIO_PREVIEW_BACKGROUND_TASK_EXECUTOR,
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
	public Set<String> getAudioMimeTypes() {
		return _audioMimeTypes;
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
	public String getType() {
		return DLProcessorConstants.AUDIO_PROCESSOR;
	}

	@Override
	public boolean hasAudio(FileVersion fileVersion) {
		boolean hasAudio = false;

		try {
			hasAudio = hasPreviews(fileVersion);

			if (!hasAudio && isSupported(fileVersion)) {
				_queueGeneration(null, fileVersion);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return hasAudio;
	}

	@Override
	public boolean isAudioSupported(FileVersion fileVersion) {
		return isSupported(fileVersion);
	}

	@Override
	public boolean isAudioSupported(String mimeType) {
		return isSupported(mimeType);
	}

	@Override
	public boolean isEnabled() {
		return _audioConverter.isEnabled();
	}

	@Override
	public boolean isSupported(String mimeType) {
		if (_audioMimeTypes.contains(mimeType) && _audioConverter.isEnabled()) {
			return true;
		}

		return false;
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
				if (!previewType.equals("mp3") && !previewType.equals("ogg")) {
					valid = false;

					break;
				}
			}
		}

		if (!valid && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Liferay is incorrectly configured to generate video ",
					"previews using video containers other than MP3 or OGG. ",
					"Please change the property \"",
					PropsKeys.DL_FILE_ENTRY_PREVIEW_AUDIO_CONTAINERS,
					"\" in portal.properties."));
		}

		FileUtil.mkdirs(PREVIEW_TMP_PATH);
	}

	@Override
	protected void doExportGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement)
		throws Exception {

		exportPreviews(portletDataContext, fileEntry, fileEntryElement);
	}

	@Override
	protected void doImportGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement)
		throws Exception {

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
				if (previewType.equals("mp3") || previewType.equals("ogg")) {
					exportPreview(
						portletDataContext, fileEntry, fileEntryElement,
						"audio", previewType);
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
		return null;
	}

	protected void importPreviews(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement)
		throws Exception {

		if ((_PREVIEW_TYPES.length == 0) || (_PREVIEW_TYPES.length > 2)) {
			return;
		}

		for (String previewType : _PREVIEW_TYPES) {
			if (previewType.equals("mp3") || previewType.equals("ogg")) {
				importPreview(
					portletDataContext, fileEntry, importedFileEntry,
					fileEntryElement, "audio", previewType);
			}
		}
	}

	private void _generateAudio(
			FileVersion fileVersion, File srcFile, File destFile,
			String containerType)
		throws Exception {

		if (hasPreview(fileVersion, containerType)) {
			return;
		}

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			FileUtil.write(
				destFile,
				_audioConverter.generateAudioPreview(srcFile, containerType));
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to process ", fileVersion.getFileVersionId(), " ",
					fileVersion.getTitle()),
				exception);
		}

		addFileToStore(
			fileVersion.getCompanyId(), PREVIEW_PATH,
			getPreviewFilePath(fileVersion, containerType), destFile);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Generated a ", containerType, " preview audio for ",
					fileVersion.getFileVersionId(), " in ", stopWatch.getTime(),
					"ms"));
		}
	}

	private void _generateAudio(
		FileVersion fileVersion, File srcFile, File[] destFiles) {

		try {
			for (int i = 0; i < destFiles.length; i++) {
				_generateAudio(
					fileVersion, srcFile, destFiles[i], _PREVIEW_TYPES[i]);
			}
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to process ", fileVersion.getFileVersionId(), " ",
					fileVersion.getTitle()),
				exception);
		}
	}

	private void _generateAudio(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion)
		throws Exception {

		String tempFileId = DLUtil.getTempFileId(
			destinationFileVersion.getFileEntryId(),
			destinationFileVersion.getVersion());

		File[] previewTempFiles = new File[_PREVIEW_TYPES.length];

		for (int i = 0; i < _PREVIEW_TYPES.length; i++) {
			previewTempFiles[i] = getPreviewTempFile(
				tempFileId, _PREVIEW_TYPES[i]);
		}

		File audioTempFile = null;

		try {
			if (sourceFileVersion != null) {
				copy(sourceFileVersion, destinationFileVersion);

				return;
			}

			if (!_audioConverter.isEnabled() ||
				hasPreviews(destinationFileVersion)) {

				return;
			}

			audioTempFile = FileUtil.createTempFile(
				destinationFileVersion.getExtension());

			if (!hasPreviews(destinationFileVersion)) {
				try (InputStream inputStream =
						destinationFileVersion.getContentStream(false)) {

					FileUtil.write(audioTempFile, inputStream);
				}

				try {
					_generateAudio(
						destinationFileVersion, audioTempFile,
						previewTempFiles);

					_fileVersionPreviewEventListener.onSuccess(
						destinationFileVersion);
				}
				catch (Exception exception) {
					_fileVersionPreviewEventListener.onFailure(
						destinationFileVersion);

					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to process ",
								destinationFileVersion.getFileVersionId(), " ",
								destinationFileVersion.getTitle()));
					}

					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					throw exception;
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

			FileUtil.delete(audioTempFile);
		}
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
			DestinationNames.DOCUMENT_LIBRARY_AUDIO_PROCESSOR,
			sourceFileVersion, destinationFileVersion);
	}

	private static final String[] _PREVIEW_TYPES =
		PropsValues.DL_FILE_ENTRY_PREVIEW_AUDIO_CONTAINERS;

	private static final Log _log = LogFactoryUtil.getLog(
		AudioPreviewableDLProcessor.class);

	@Reference
	private AudioConverter _audioConverter;

	private final Set<String> _audioMimeTypes = SetUtil.fromArray(
		PropsValues.DL_FILE_ENTRY_PREVIEW_AUDIO_MIME_TYPES);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private CompanyLocalService _companyLocalService;

	private final List<Long> _fileVersionIds = new Vector<>();

	@Reference
	private FileVersionPreviewEventListener _fileVersionPreviewEventListener;

}