/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.processor;

import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.DLProcessorHelper;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import java.awt.image.RenderedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 * @author Ivica Cardic
 */
public abstract class BasePreviewableDLProcessor implements DLProcessor {

	public static final String DECRYPT_PATH = "document_decrypt/";

	public static final String DECRYPT_TMP_PATH;

	public static final String PREVIEW_PATH = "document_preview/";

	public static final String PREVIEW_TMP_PATH;

	public static final long REPOSITORY_ID = CompanyConstants.SYSTEM;

	public static final int THUMBNAIL_INDEX_CUSTOM_1 = 1;

	public static final int THUMBNAIL_INDEX_CUSTOM_2 = 2;

	public static final int THUMBNAIL_INDEX_DEFAULT = 0;

	public static final String THUMBNAIL_PATH = "document_thumbnail/";

	public static final String THUMBNAIL_TMP_PATH;

	public static final String TMP_PATH;

	static {
		TMP_PATH = SystemProperties.get(SystemProperties.TMP_DIR) + "/liferay/";

		DECRYPT_TMP_PATH = TMP_PATH.concat(DECRYPT_PATH);
		PREVIEW_TMP_PATH = TMP_PATH.concat(PREVIEW_PATH);
		THUMBNAIL_TMP_PATH = TMP_PATH.concat(THUMBNAIL_PATH);
	}

	@Override
	public void cleanUp(FileEntry fileEntry) {
		deleteFiles(fileEntry, getThumbnailType());
	}

	@Override
	public void cleanUp(FileVersion fileVersion) {
		deleteFiles(fileVersion, getThumbnailType());
	}

	@Override
	public void copy(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

		if (sourceFileVersion.getFileVersionId() ==
				destinationFileVersion.getFileVersionId()) {

			return;
		}

		copyPreviews(sourceFileVersion, destinationFileVersion);
		copyThumbnails(sourceFileVersion, destinationFileVersion);
	}

	public void deleteFiles(FileEntry fileEntry, String thumbnailType) {
		deleteFiles(
			fileEntry.getCompanyId(), fileEntry.getGroupId(),
			fileEntry.getFileEntryId(), -1, thumbnailType);
	}

	public void deleteFiles(FileVersion fileVersion, String thumbnailType) {
		deleteFiles(
			fileVersion.getCompanyId(), fileVersion.getGroupId(),
			fileVersion.getFileEntryId(), fileVersion.getFileVersionId(),
			thumbnailType);
	}

	@Override
	public void exportGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement)
		throws Exception {

		doExportGeneratedFiles(portletDataContext, fileEntry, fileEntryElement);
	}

	@Override
	public void importGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement)
		throws Exception {

		cleanUp(importedFileEntry.getFileVersion());

		doImportGeneratedFiles(
			portletDataContext, fileEntry, importedFileEntry, fileEntryElement);
	}

	@Override
	public boolean isSupported(FileVersion fileVersion) {
		if ((fileVersion == null) || (fileVersion.getSize() == 0) ||
			!dlProcessorHelper.isPreviewableSize(fileVersion)) {

			return false;
		}

		return isSupported(fileVersion.getMimeType());
	}

	@Override
	public void trigger(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

		if (getFileVersionIds().contains(
				destinationFileVersion.getFileVersionId())) {

			String processIdentity = String.valueOf(
				destinationFileVersion.getFileVersionId());

			destroyProcess(processIdentity);

			getFileVersionIds().remove(
				destinationFileVersion.getFileVersionId());
		}
	}

	protected void addFileToStore(
			long companyId, String dirName, String filePath, File srcFile)
		throws PortalException {

		try (InputStream inputStream = new FileInputStream(srcFile)) {
			store.addFile(
				companyId, REPOSITORY_ID, filePath, Store.VERSION_DEFAULT,
				inputStream);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	protected void addFileToStore(
			long companyId, String dirName, String filePath,
			InputStream inputStream)
		throws PortalException {

		store.addFile(
			companyId, REPOSITORY_ID, filePath, Store.VERSION_DEFAULT,
			inputStream);
	}

	protected void copyPreviews(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

		try {
			String[] previewTypes = getPreviewTypes();

			for (String previewType : previewTypes) {
				if (!hasPreview(sourceFileVersion, previewType) ||
					hasPreview(destinationFileVersion, previewType)) {

					continue;
				}

				String previewFilePath = getPreviewFilePath(
					destinationFileVersion, previewType);

				InputStream inputStream = doGetPreviewAsStream(
					sourceFileVersion, previewType);

				addFileToStore(
					destinationFileVersion.getCompanyId(), PREVIEW_PATH,
					previewFilePath, inputStream);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	protected void copyThumbnail(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion,
		int index) {

		try {
			if (isThumbnailEnabled(index) &&
				hasThumbnail(sourceFileVersion, index) &&
				!hasThumbnail(destinationFileVersion, index)) {

				InputStream inputStream = doGetThumbnailAsStream(
					sourceFileVersion, index);

				String thumbnailFilePath = getThumbnailFilePath(
					destinationFileVersion,
					getThumbnailType(destinationFileVersion), index);

				addFileToStore(
					destinationFileVersion.getCompanyId(), THUMBNAIL_PATH,
					thumbnailFilePath, inputStream);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	protected void copyThumbnails(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

		copyThumbnail(
			sourceFileVersion, destinationFileVersion, THUMBNAIL_INDEX_DEFAULT);
		copyThumbnail(
			sourceFileVersion, destinationFileVersion,
			THUMBNAIL_INDEX_CUSTOM_1);
		copyThumbnail(
			sourceFileVersion, destinationFileVersion,
			THUMBNAIL_INDEX_CUSTOM_2);
	}

	protected void deleteFiles(
		long companyId, long groupId, long fileEntryId, long fileVersionId,
		String thumbnailType) {

		deletePreviews(companyId, groupId, fileEntryId, fileVersionId);
		deleteThumbnails(
			companyId, groupId, fileEntryId, fileVersionId, thumbnailType);
	}

	protected void deletePreviews(
		long companyId, long groupId, long fileEntryId, long fileVersionId) {

		for (String fileName :
				store.getFileNames(
					companyId, REPOSITORY_ID,
					getPreviewFilePath(
						groupId, fileEntryId, fileVersionId, null))) {

			store.deleteFile(
				companyId, REPOSITORY_ID, fileName, StringPool.BLANK);
		}
	}

	protected void deleteThumbnail(
		long companyId, long groupId, long fileEntryId, long fileVersionId,
		String thumbnailType, int index) {

		try {
			for (String fileName :
					store.getFileNames(
						companyId, REPOSITORY_ID,
						getThumbnailFilePath(
							groupId, fileEntryId, fileVersionId, thumbnailType,
							index))) {

				store.deleteFile(
					companyId, REPOSITORY_ID, fileName, StringPool.BLANK);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	protected void deleteThumbnails(
		long companyId, long groupId, long fileEntryId, long fileVersionId,
		String thumbnailType) {

		deleteThumbnail(
			companyId, groupId, fileEntryId, fileVersionId, thumbnailType,
			THUMBNAIL_INDEX_DEFAULT);
		deleteThumbnail(
			companyId, groupId, fileEntryId, fileVersionId, thumbnailType,
			THUMBNAIL_INDEX_CUSTOM_1);
		deleteThumbnail(
			companyId, groupId, fileEntryId, fileVersionId, thumbnailType,
			THUMBNAIL_INDEX_CUSTOM_2);
	}

	protected void destroyProcess(String processIdentity) {
		Future<?> future = futures.remove(processIdentity);

		if (future != null) {
			future.cancel(true);

			if (_log.isInfoEnabled()) {
				_log.info("Cancellation requested for " + processIdentity);
			}
		}
	}

	protected abstract void doExportGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement)
		throws Exception;

	protected InputStream doGetPreviewAsStream(
			FileVersion fileVersion, int index, String type)
		throws PortalException {

		return store.getFileAsStream(
			fileVersion.getCompanyId(), CompanyConstants.SYSTEM,
			getPreviewFilePath(fileVersion, index, type), StringPool.BLANK);
	}

	protected InputStream doGetPreviewAsStream(
			FileVersion fileVersion, String type)
		throws PortalException {

		return doGetPreviewAsStream(fileVersion, 0, type);
	}

	protected int doGetPreviewFileCount(FileVersion fileVersion)
		throws Exception {

		try {
			String[] fileNames = store.getFileNames(
				fileVersion.getCompanyId(), REPOSITORY_ID,
				getPathSegment(fileVersion, true));

			return fileNames.length;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return 0;
	}

	protected long doGetPreviewFileSize(FileVersion fileVersion, int index)
		throws PortalException {

		return doGetPreviewFileSize(fileVersion, index, getPreviewType());
	}

	protected long doGetPreviewFileSize(
			FileVersion fileVersion, int index, String type)
		throws PortalException {

		return store.getFileSize(
			fileVersion.getCompanyId(), CompanyConstants.SYSTEM,
			getPreviewFilePath(fileVersion, index, type), StringPool.BLANK);
	}

	protected long doGetPreviewFileSize(FileVersion fileVersion, String type)
		throws PortalException {

		return doGetPreviewFileSize(fileVersion, 0, type);
	}

	protected InputStream doGetThumbnailAsStream(
			FileVersion fileVersion, int index)
		throws PortalException {

		String type = getThumbnailType(fileVersion);

		return store.getFileAsStream(
			fileVersion.getCompanyId(), CompanyConstants.SYSTEM,
			getThumbnailFilePath(fileVersion, type, index), StringPool.BLANK);
	}

	protected long doGetThumbnailFileSize(FileVersion fileVersion, int index)
		throws PortalException {

		String type = getThumbnailType(fileVersion);

		return store.getFileSize(
			fileVersion.getCompanyId(), CompanyConstants.SYSTEM,
			getThumbnailFilePath(fileVersion, type, index), StringPool.BLANK);
	}

	protected abstract void doImportGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement)
		throws Exception;

	protected void exportBinary(
		PortletDataContext portletDataContext, Element fileEntryElement,
		FileVersion fileVersion, InputStream inputStream, String binPath,
		String binPathName) {

		fileEntryElement.addAttribute(binPathName, binPath);

		if (inputStream == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No input stream found for file entry " +
						fileVersion.getFileEntryId());
			}

			fileEntryElement.detach();

			return;
		}

		portletDataContext.addZipEntry(binPath, inputStream);
	}

	protected void exportPreview(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement, String binPathSuffix, String previewType)
		throws Exception {

		exportPreview(
			portletDataContext, fileEntry, fileEntryElement, binPathSuffix,
			previewType, -1);
	}

	protected void exportPreview(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement, String binPathSuffix, String previewType,
			int fileIndex)
		throws Exception {

		if (portletDataContext.isPerformDirectBinaryImport()) {
			return;
		}

		FileVersion fileVersion = fileEntry.getFileVersion();

		if (!hasPreview(fileVersion, previewType)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No preview found for file entry " +
						fileEntry.getFileEntryId());
			}

			return;
		}

		String binPathSegment = null;

		if (fileIndex < 0) {
			binPathSegment = previewType;
		}
		else {
			binPathSegment = String.valueOf(fileIndex + 1);
		}

		String binPath = getBinPath(
			portletDataContext, fileEntry, binPathSegment);

		String binPathName = StringBundler.concat(
			"bin-path-preview-", binPathSegment, "-", binPathSuffix);

		fileEntryElement.addAttribute(binPathName, binPath);

		if (fileIndex < 0) {
			fileIndex = 0;
		}
		else {
			fileIndex++;
		}

		try (InputStream inputStream = doGetPreviewAsStream(
				fileVersion, fileIndex, previewType)) {

			exportBinary(
				portletDataContext, fileEntryElement, fileVersion, inputStream,
				binPath, binPathName);
		}
	}

	protected void exportThumbnail(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement, String binPathName, int index)
		throws PortalException {

		if (!isThumbnailEnabled(index)) {
			return;
		}

		FileVersion fileVersion = fileEntry.getFileVersion();

		if (!hasThumbnail(fileVersion, index)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No thumbnail found for file entry " +
						fileEntry.getFileEntryId());
			}

			return;
		}

		try (InputStream inputStream = doGetThumbnailAsStream(
				fileVersion, index)) {

			String binPath = getBinPath(portletDataContext, fileEntry, index);

			fileEntryElement.addAttribute(binPathName, binPath);

			exportBinary(
				portletDataContext, fileEntryElement, fileVersion, inputStream,
				binPath, binPathName);
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}
	}

	protected void exportThumbnails(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement, String binPathSuffix)
		throws PortalException {

		if (!isSupported(fileEntry.getFileVersion())) {
			return;
		}

		if (!portletDataContext.isPerformDirectBinaryImport()) {
			exportThumbnail(
				portletDataContext, fileEntry, fileEntryElement,
				"bin-path-thumbnail-default-" + binPathSuffix,
				THUMBNAIL_INDEX_DEFAULT);
			exportThumbnail(
				portletDataContext, fileEntry, fileEntryElement,
				"bin-path-thumbnail-custom-1-" + binPathSuffix,
				THUMBNAIL_INDEX_CUSTOM_1);
			exportThumbnail(
				portletDataContext, fileEntry, fileEntryElement,
				"bin-path-thumbnail-custom-2-" + binPathSuffix,
				THUMBNAIL_INDEX_CUSTOM_2);
		}
	}

	protected String getBinPath(
		PortletDataContext portletDataContext, FileEntry fileEntry, int index) {

		return StringBundler.concat(
			ExportImportPathUtil.getPortletPath(
				portletDataContext, PortletKeys.DOCUMENT_LIBRARY),
			"/bin/", fileEntry.getFileEntryId(), StringPool.SLASH,
			THUMBNAIL_PATH, fileEntry.getVersion(), StringPool.SLASH, index);
	}

	protected String getBinPath(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		String type) {

		return StringBundler.concat(
			ExportImportPathUtil.getPortletPath(
				portletDataContext, PortletKeys.DOCUMENT_LIBRARY),
			"/bin/", fileEntry.getFileEntryId(), StringPool.SLASH, PREVIEW_PATH,
			fileEntry.getVersion(), StringPool.SLASH, type);
	}

	protected File getDecryptedTempFile(String id) {
		return new File(DECRYPT_TMP_PATH + id);
	}

	protected abstract List<Long> getFileVersionIds();

	protected String getPathSegment(FileVersion fileVersion, boolean preview) {
		return getPathSegment(
			fileVersion.getGroupId(), fileVersion.getFileEntryId(),
			fileVersion.getFileVersionId(), preview);
	}

	protected String getPathSegment(
		long groupId, long fileEntryId, long fileVersionId, boolean preview) {

		StringBundler sb = null;

		if (fileVersionId > 0) {
			sb = new StringBundler(5);
		}
		else {
			sb = new StringBundler(3);
		}

		if (preview) {
			sb.append(PREVIEW_PATH);
		}
		else {
			sb.append(THUMBNAIL_PATH);
		}

		sb.append(groupId);
		sb.append(DLUtil.getDividedPath(fileEntryId));

		if (fileVersionId > 0) {
			sb.append(StringPool.SLASH);
			sb.append(fileVersionId);
		}

		return sb.toString();
	}

	protected String getPreviewFilePath(FileVersion fileVersion) {
		return getPreviewFilePath(fileVersion, 0);
	}

	protected String getPreviewFilePath(FileVersion fileVersion, int index) {
		return getPreviewFilePath(fileVersion, index, getPreviewType());
	}

	protected String getPreviewFilePath(
		FileVersion fileVersion, int index, String type) {

		return getPreviewFilePath(
			fileVersion.getGroupId(), fileVersion.getFileEntryId(),
			fileVersion.getFileVersionId(), index, type);
	}

	protected String getPreviewFilePath(FileVersion fileVersion, String type) {
		return getPreviewFilePath(fileVersion, 0, type);
	}

	protected String getPreviewFilePath(
		long groupId, long fileEntryId, long fileVersionId, int index,
		String type) {

		StringBundler sb = null;

		if (index > 0) {
			sb = new StringBundler(5);
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(getPathSegment(groupId, fileEntryId, fileVersionId, true));

		if (index > 0) {
			sb.append(StringPool.SLASH);
			sb.append(index - 1);
		}

		if (Validator.isNotNull(type)) {
			sb.append(StringPool.PERIOD);
			sb.append(type);
		}

		return sb.toString();
	}

	protected String getPreviewFilePath(
		long groupId, long fileEntryId, long fileVersionId, String type) {

		return getPreviewFilePath(groupId, fileEntryId, fileVersionId, 0, type);
	}

	protected File getPreviewTempFile(String id) {
		return getPreviewTempFile(id, 0);
	}

	protected File getPreviewTempFile(String id, int index) {
		return getPreviewTempFile(id, index, getPreviewType());
	}

	protected File getPreviewTempFile(String id, int index, String type) {
		return new File(getPreviewTempFilePath(id, index, type));
	}

	protected File getPreviewTempFile(String id, String type) {
		return getPreviewTempFile(id, 0, type);
	}

	protected int getPreviewTempFileCount(FileVersion fileVersion) {
		return getPreviewTempFileCount(fileVersion, getPreviewType());
	}

	protected int getPreviewTempFileCount(
		FileVersion fileVersion, String type) {

		String tempFileId = DLUtil.getTempFileId(
			fileVersion.getFileEntryId(), fileVersion.getVersion());

		String prefix = tempFileId + StringPool.DASH;

		Predicate<File> filePredicate = File::isFile;

		filePredicate = filePredicate.and(
			file -> {
				String fileName = file.getName();

				return fileName.startsWith(prefix);
			});

		if (Validator.isNotNull(type)) {
			String suffix = StringPool.PERIOD + type;

			filePredicate = filePredicate.and(
				file -> {
					String fileName = file.getName();

					return fileName.endsWith(suffix);
				});
		}

		File dir = new File(PREVIEW_TMP_PATH);

		File[] files = dir.listFiles(filePredicate::test);

		if (_log.isDebugEnabled()) {
			for (File file : files) {
				_log.debug(
					StringBundler.concat(
						"Preview page for ", tempFileId, " ", file));
			}
		}

		return files.length;
	}

	protected String getPreviewTempFilePath(String id) {
		return getPreviewTempFilePath(id, 0);
	}

	protected String getPreviewTempFilePath(String id, int index) {
		return getPreviewTempFilePath(id, index, getPreviewType());
	}

	protected String getPreviewTempFilePath(String id, int index, String type) {
		StringBundler sb = null;

		if (index > 0) {
			sb = new StringBundler(6);
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(PREVIEW_TMP_PATH);
		sb.append(id);

		if (index > 0) {
			sb.append(StringPool.DASH);
			sb.append(index - 1);
		}
		else if (index == -1) {
			sb.append("-%d");
		}

		if (Validator.isNotNull(type)) {
			sb.append(StringPool.PERIOD);
			sb.append(type);
		}

		return sb.toString();
	}

	protected String getPreviewTempFilePath(String id, String type) {
		return getPreviewTempFilePath(id, 0, type);
	}

	protected String getPreviewType() {
		return getPreviewType(null);
	}

	protected abstract String getPreviewType(FileVersion fileVersion);

	protected String getPreviewType(int index) {
		String[] previewTypes = getPreviewTypes();

		if ((previewTypes != null) && (previewTypes.length > index)) {
			return previewTypes[index];
		}

		return getPreviewType();
	}

	protected String[] getPreviewTypes() {
		return new String[] {getPreviewType()};
	}

	protected String getThumbnailFilePath(FileVersion fileVersion, int index) {
		return getThumbnailFilePath(fileVersion, getThumbnailType(), index);
	}

	protected String getThumbnailFilePath(
		FileVersion fileVersion, String type, int index) {

		return getThumbnailFilePath(
			fileVersion.getGroupId(), fileVersion.getFileEntryId(),
			fileVersion.getFileVersionId(), type, index);
	}

	protected String getThumbnailFilePath(
		long groupId, long fileEntryId, long fileVersionId,
		String thumbnailType, int index) {

		StringBundler sb = new StringBundler(5);

		sb.append(getPathSegment(groupId, fileEntryId, fileVersionId, false));

		if (index != THUMBNAIL_INDEX_DEFAULT) {
			sb.append(StringPool.DASH);
			sb.append(index);
		}

		if ((fileVersionId > 0) && Validator.isNotNull(thumbnailType)) {
			sb.append(StringPool.PERIOD);
			sb.append(thumbnailType);
		}

		return sb.toString();
	}

	protected File getThumbnailTempFile(String id) {
		return getThumbnailTempFile(id, getThumbnailType());
	}

	protected File getThumbnailTempFile(String id, String type) {
		return new File(getThumbnailTempFilePath(id, type));
	}

	protected String getThumbnailTempFilePath(String id) {
		return getThumbnailTempFilePath(id, getThumbnailType());
	}

	protected String getThumbnailTempFilePath(String id, String type) {
		StringBundler sb = new StringBundler(4);

		sb.append(THUMBNAIL_TMP_PATH);
		sb.append(id);

		if (Validator.isNotNull(type)) {
			sb.append(StringPool.PERIOD);
			sb.append(type);
		}

		return sb.toString();
	}

	protected String getThumbnailType() {
		return getThumbnailType(null);
	}

	protected abstract String getThumbnailType(FileVersion fileVersion);

	protected boolean hasPreview(FileVersion fileVersion, String type)
		throws Exception {

		return store.hasFile(
			fileVersion.getCompanyId(), REPOSITORY_ID,
			getPreviewFilePath(fileVersion, type), Store.VERSION_DEFAULT);
	}

	protected boolean hasPreviews(FileVersion fileVersion) throws Exception {
		int count = 0;

		String[] previewTypes = getPreviewTypes();

		for (String previewType : previewTypes) {
			if (!hasPreview(fileVersion, previewType)) {
				continue;
			}

			count++;
		}

		if (count == previewTypes.length) {
			return true;
		}

		return false;
	}

	protected boolean hasThumbnail(FileVersion fileVersion, int index) {
		try {
			String imageType = getThumbnailType(fileVersion);

			return store.hasFile(
				fileVersion.getCompanyId(), REPOSITORY_ID,
				getThumbnailFilePath(fileVersion, imageType, index),
				Store.VERSION_DEFAULT);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	protected boolean hasThumbnails(FileVersion fileVersion) {
		if ((isThumbnailEnabled(THUMBNAIL_INDEX_DEFAULT) &&
			 !hasThumbnail(fileVersion, THUMBNAIL_INDEX_DEFAULT)) ||
			(isThumbnailEnabled(THUMBNAIL_INDEX_CUSTOM_1) &&
			 !hasThumbnail(fileVersion, THUMBNAIL_INDEX_CUSTOM_1)) ||
			(isThumbnailEnabled(THUMBNAIL_INDEX_CUSTOM_2) &&
			 !hasThumbnail(fileVersion, THUMBNAIL_INDEX_CUSTOM_2))) {

			return false;
		}

		return true;
	}

	protected void importPreview(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement,
			String binPathSuffix, String previewType)
		throws Exception {

		importPreview(
			portletDataContext, fileEntry, importedFileEntry, fileEntryElement,
			binPathSuffix, previewType, -1);
	}

	protected void importPreview(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement,
			String binPathSuffix, String previewType, int fileIndex)
		throws Exception {

		if (!portletDataContext.isPerformDirectBinaryImport()) {
			importPreviewFromLAR(
				portletDataContext, importedFileEntry, fileEntryElement,
				binPathSuffix, previewType, fileIndex);
		}
		else {
			FileVersion importedFileVersion =
				importedFileEntry.getFileVersion();

			String previewFilePath = getPreviewFilePath(
				importedFileVersion, previewType);

			FileVersion fileVersion = fileEntry.getFileVersion();

			if (!hasPreview(fileVersion, previewType)) {
				return;
			}

			if (fileIndex < 0) {
				fileIndex = 0;
			}
			else {
				fileIndex++;
			}

			try (InputStream inputStream = doGetPreviewAsStream(
					fileVersion, fileIndex, previewType)) {

				addFileToStore(
					portletDataContext.getCompanyId(), PREVIEW_PATH,
					previewFilePath, inputStream);
			}
		}
	}

	protected void importPreviewFromLAR(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement, String binPathSuffix, String previewType,
			int fileIndex)
		throws Exception {

		FileVersion fileVersion = fileEntry.getFileVersion();

		String binPathSegment = null;

		if (fileIndex < 0) {
			binPathSegment = previewType;
		}
		else {
			binPathSegment = String.valueOf(fileIndex + 1);
		}

		String binPathName = StringBundler.concat(
			"bin-path-preview-", binPathSegment, "-", binPathSuffix);

		String binPath = fileEntryElement.attributeValue(binPathName);

		try (InputStream inputStream =
				portletDataContext.getZipEntryAsInputStream(binPath)) {

			if (inputStream == null) {
				return;
			}

			String previewFilePath = null;

			if (fileIndex < 0) {
				previewFilePath = getPreviewFilePath(fileVersion, previewType);
			}
			else {
				previewFilePath = getPreviewFilePath(
					fileVersion, fileIndex + 1);
			}

			addFileToStore(
				portletDataContext.getCompanyId(), PREVIEW_PATH,
				previewFilePath, inputStream);
		}
	}

	protected void importThumbnail(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement,
			String binPathName, int index)
		throws Exception {

		if (!isThumbnailEnabled(index)) {
			return;
		}

		if (!portletDataContext.isPerformDirectBinaryImport()) {
			importThumbnailFromLAR(
				portletDataContext, importedFileEntry, fileEntryElement,
				binPathName, index);
		}
		else {
			FileVersion fileVersion = fileEntry.getFileVersion();

			if (!hasThumbnail(fileVersion, index)) {
				return;
			}

			try (InputStream inputStream = doGetThumbnailAsStream(
					fileVersion, index)) {

				FileVersion importedFileVersion =
					importedFileEntry.getFileVersion();

				addFileToStore(
					portletDataContext.getCompanyId(), THUMBNAIL_PATH,
					getThumbnailFilePath(
						importedFileVersion,
						getThumbnailType(importedFileVersion), index),
					inputStream);
			}
		}
	}

	protected void importThumbnailFromLAR(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement, String binPathName, int index)
		throws Exception {

		FileVersion fileVersion = fileEntry.getFileVersion();

		String binPath = fileEntryElement.attributeValue(binPathName);

		try (InputStream inputStream =
				portletDataContext.getZipEntryAsInputStream(binPath)) {

			if (inputStream == null) {
				return;
			}

			addFileToStore(
				portletDataContext.getCompanyId(), THUMBNAIL_PATH,
				getThumbnailFilePath(
					fileVersion, getThumbnailType(fileVersion), index),
				inputStream);
		}
	}

	protected void importThumbnails(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement,
			String binPathSuffix)
		throws Exception {

		importThumbnail(
			portletDataContext, fileEntry, importedFileEntry, fileEntryElement,
			"bin-path-thumbnail-default-" + binPathSuffix,
			THUMBNAIL_INDEX_DEFAULT);
		importThumbnail(
			portletDataContext, fileEntry, importedFileEntry, fileEntryElement,
			"bin-path-thumbnail-custom-1-" + binPathSuffix,
			THUMBNAIL_INDEX_CUSTOM_1);
		importThumbnail(
			portletDataContext, fileEntry, importedFileEntry, fileEntryElement,
			"bin-path-thumbnail-custom-2-" + binPathSuffix,
			THUMBNAIL_INDEX_CUSTOM_2);
	}

	protected boolean isThumbnailEnabled(int index) {
		if ((index == THUMBNAIL_INDEX_DEFAULT) &&
			GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.DL_FILE_ENTRY_THUMBNAIL_ENABLED))) {

			return true;
		}
		else if (index == THUMBNAIL_INDEX_CUSTOM_1) {
			int maxHeight = PrefsPropsUtil.getInteger(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_HEIGHT);
			int maxWidth = PrefsPropsUtil.getInteger(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_WIDTH);

			if ((maxHeight > 0) || (maxWidth > 0)) {
				return true;
			}
		}
		else if (index == THUMBNAIL_INDEX_CUSTOM_2) {
			int maxHeight = PrefsPropsUtil.getInteger(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_HEIGHT);
			int maxWidth = PrefsPropsUtil.getInteger(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_WIDTH);

			if ((maxHeight > 0) || (maxWidth > 0)) {
				return true;
			}
		}

		return false;
	}

	protected void sendGenerationMessage(
		String destinationName, FileVersion sourceFileVersion,
		FileVersion destinationFileVersion) {

		Message message = new Message();

		message.setPayload(
			new Object[] {sourceFileVersion, destinationFileVersion});

		messageBus.sendMessage(destinationName, message);
	}

	protected void storeThumbnailImage(
			FileVersion fileVersion, RenderedImage renderedImage, int index)
		throws Exception {

		if (!isThumbnailEnabled(index) || hasThumbnail(fileVersion, index)) {
			return;
		}

		String type = getThumbnailType(fileVersion);

		String maxHeightPropsKey = PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_HEIGHT;
		String maxWidthPropsKey = PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_WIDTH;

		if (index == THUMBNAIL_INDEX_CUSTOM_1) {
			maxHeightPropsKey =
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_HEIGHT;
			maxWidthPropsKey =
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_WIDTH;
		}
		else if (index == THUMBNAIL_INDEX_CUSTOM_2) {
			maxHeightPropsKey =
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_HEIGHT;
			maxWidthPropsKey =
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_WIDTH;
		}

		RenderedImage thumbnailRenderedImage = ImageToolUtil.scale(
			renderedImage, PrefsPropsUtil.getInteger(maxHeightPropsKey),
			PrefsPropsUtil.getInteger(maxWidthPropsKey));

		byte[] bytes = ImageToolUtil.getBytes(thumbnailRenderedImage, type);

		File file = null;

		try {
			file = FileUtil.createTempFile(bytes);

			addFileToStore(
				fileVersion.getCompanyId(), THUMBNAIL_PATH,
				getThumbnailFilePath(fileVersion, type, index), file);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	protected void storeThumbnailImages(FileVersion fileVersion, File file)
		throws Exception {

		ImageBag imageBag = ImageToolUtil.read(file);

		storeThumbnailImages(fileVersion, imageBag.getRenderedImage());
	}

	protected void storeThumbnailImages(
			FileVersion fileVersion, RenderedImage renderedImage)
		throws Exception {

		storeThumbnailImage(
			fileVersion, renderedImage, THUMBNAIL_INDEX_DEFAULT);
		storeThumbnailImage(
			fileVersion, renderedImage, THUMBNAIL_INDEX_CUSTOM_1);
		storeThumbnailImage(
			fileVersion, renderedImage, THUMBNAIL_INDEX_CUSTOM_2);
	}

	@Reference
	protected DLProcessorHelper dlProcessorHelper;

	protected Map<String, Future<?>> futures = new ConcurrentHashMap<>();

	@Reference
	protected MessageBus messageBus;

	@Reference(target = "(default=true)")
	protected Store store;

	private static final Log _log = LogFactoryUtil.getLog(
		BasePreviewableDLProcessor.class);

}