/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.sharepoint;

import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.sharepoint.BaseSharepointStorageImpl;
import com.liferay.portal.sharepoint.SharepointRequest;
import com.liferay.portal.sharepoint.SharepointUtil;
import com.liferay.portal.sharepoint.Tree;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.InputStream;

import java.util.List;

/**
 * @author Bruno Farache
 */
public class DLSharepointStorageImpl extends BaseSharepointStorageImpl {

	@Override
	public void addDocumentElements(
			SharepointRequest sharepointRequest, Element element)
		throws Exception {

		String parentFolderPath = sharepointRequest.getRootPath();

		long groupId = SharepointUtil.getGroupId(parentFolderPath);

		long parentFolderId = getLastFolderId(
			groupId, parentFolderPath,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		if (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		List<FileEntry> fileEntries = DLAppServiceUtil.getFileEntries(
			groupId, parentFolderId);

		for (FileEntry fileEntry : fileEntries) {
			String documentPath = StringBundler.concat(
				parentFolderPath, StringPool.SLASH, fileEntry.getTitle());

			addDocumentElement(
				element, documentPath, fileEntry.getCreateDate(),
				fileEntry.getModifiedDate(), fileEntry.getUserName());
		}
	}

	@Override
	public void createFolder(SharepointRequest sharepointRequest)
		throws Exception {

		String folderPath = sharepointRequest.getRootPath();

		String parentFolderPath = getParentFolderPath(folderPath);

		long groupId = SharepointUtil.getGroupId(parentFolderPath);

		long parentFolderId = getLastFolderId(
			groupId, parentFolderPath,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String folderName = getResourceName(folderPath);
		String description = StringPool.BLANK;

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		DLAppServiceUtil.addFolder(
			null, groupId, parentFolderId, folderName, description,
			serviceContext);
	}

	@Override
	public InputStream getDocumentInputStream(
			SharepointRequest sharepointRequest)
		throws Exception {

		FileEntry fileEntry = getFileEntry(sharepointRequest);

		return fileEntry.getContentStream();
	}

	@Override
	public Tree getDocumentsTree(SharepointRequest sharepointRequest)
		throws Exception {

		Tree documentsTree = new Tree();

		String parentFolderPath = sharepointRequest.getRootPath();

		long groupId = SharepointUtil.getGroupId(parentFolderPath);

		long parentFolderId = getLastFolderId(
			groupId, parentFolderPath,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		List<FileEntry> fileEntries = DLAppServiceUtil.getFileEntries(
			groupId, parentFolderId);

		for (FileEntry fileEntry : fileEntries) {
			documentsTree.addChild(
				getFileEntryTree(fileEntry, parentFolderPath));
		}

		return documentsTree;
	}

	@Override
	public Tree getDocumentTree(SharepointRequest sharepointRequest)
		throws Exception {

		String documentPath = sharepointRequest.getRootPath();

		return getFileEntryTree(
			getFileEntry(sharepointRequest), getParentFolderPath(documentPath));
	}

	@Override
	public Tree getFoldersTree(SharepointRequest sharepointRequest)
		throws Exception {

		Tree foldersTree = new Tree();

		String parentFolderPath = sharepointRequest.getRootPath();

		long groupId = SharepointUtil.getGroupId(parentFolderPath);

		long parentFolderId = getLastFolderId(
			groupId, parentFolderPath,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		List<Folder> folders = DLAppServiceUtil.getFolders(
			groupId, parentFolderId, false);

		for (Folder folder : folders) {
			foldersTree.addChild(getFolderTree(folder, parentFolderPath));
		}

		foldersTree.addChild(getFolderTree(parentFolderPath));

		return foldersTree;
	}

	@Override
	public Tree getFolderTree(SharepointRequest sharepointRequest)
		throws Exception {

		String folderPath = sharepointRequest.getRootPath();

		String parentFolderPath = getParentFolderPath(folderPath);

		long folderId = getLastFolderId(
			SharepointUtil.getGroupId(folderPath), folderPath,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return getFolderTree(
			DLAppServiceUtil.getFolder(folderId), parentFolderPath);
	}

	@Override
	public void getParentFolderIds(
			long groupId, String path, List<Long> folderIds)
		throws Exception {

		String[] pathArray = SharepointUtil.getPathArray(path);

		if (pathArray.length == 0) {
			return;
		}

		long parentFolderId = folderIds.get(folderIds.size() - 1);

		Folder folder = DLAppServiceUtil.getFolder(
			groupId, parentFolderId,
			HttpComponentsUtil.decodePath(pathArray[0]));

		folderIds.add(folder.getFolderId());

		if (pathArray.length > 1) {
			path = removeFoldersFromPath(path, 1);

			getParentFolderIds(groupId, path, folderIds);
		}
	}

	@Override
	public Tree[] moveDocument(SharepointRequest sharepointRequest)
		throws Exception {

		String parentFolderPath = sharepointRequest.getRootPath();

		long groupId = SharepointUtil.getGroupId(parentFolderPath);

		Folder folder = null;
		FileEntry fileEntry = null;

		try {
			long parentFolderId = getLastFolderId(
				groupId, parentFolderPath,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			folder = DLAppServiceUtil.getFolder(parentFolderId);
		}
		catch (Exception exception1) {
			if (exception1 instanceof NoSuchFolderException) {
				try {
					fileEntry = getFileEntry(sharepointRequest);
				}
				catch (Exception exception2) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception2);
					}
				}
			}
		}

		Tree movedDocsTree = new Tree();
		Tree movedDirsTree = new Tree();

		String newPath = sharepointRequest.getParameterValue("newUrl");

		String newParentFolderPath = getParentFolderPath(newPath);

		long newGroupId = SharepointUtil.getGroupId(newParentFolderPath);

		long newParentFolderId = getLastFolderId(
			newGroupId, newParentFolderPath,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String newName = getResourceName(newPath);

		ServiceContext serviceContext = new ServiceContext();

		if (fileEntry != null) {
			File file = null;

			try {
				long fileEntryId = fileEntry.getFileEntryId();

				long folderId = fileEntry.getFolderId();
				String mimeType = fileEntry.getMimeType();
				String description = fileEntry.getDescription();
				String changeLog = StringPool.BLANK;

				InputStream inputStream = fileEntry.getContentStream();

				file = FileUtil.createTempFile(inputStream);

				serviceContext.setAssetTagNames(
					AssetTagLocalServiceUtil.getTagNames(
						DLFileEntryConstants.getClassName(),
						fileEntry.getFileEntryId()));

				fileEntry = DLAppServiceUtil.updateFileEntry(
					fileEntryId, newName, mimeType, newName, StringPool.BLANK,
					description, changeLog,
					DLVersionNumberIncrease.fromMajorVersion(false), file,
					fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
					fileEntry.getReviewDate(), serviceContext);

				if (folderId != newParentFolderId) {
					fileEntry = DLAppServiceUtil.moveFileEntry(
						fileEntryId, newParentFolderId, serviceContext);
				}

				Tree documentTree = getFileEntryTree(
					fileEntry, newParentFolderPath);

				movedDocsTree.addChild(documentTree);
			}
			finally {
				FileUtil.delete(file);
			}
		}
		else if (folder != null) {
			long folderId = folder.getFolderId();

			String description = folder.getDescription();

			if (newParentFolderId != folder.getParentFolderId()) {
				folder = DLAppServiceUtil.moveFolder(
					folderId, newParentFolderId, serviceContext);
			}

			if (!newName.equals(folder.getName())) {
				DLAppServiceUtil.updateFolder(
					folderId, newName, description, serviceContext);
			}

			movedDirsTree.addChild(getFolderTree(folder, newParentFolderPath));
		}

		return new Tree[] {movedDocsTree, movedDirsTree};
	}

	@Override
	public void putDocument(SharepointRequest sharepointRequest)
		throws Exception {

		HttpServletRequest httpServletRequest =
			sharepointRequest.getHttpServletRequest();

		String documentPath = sharepointRequest.getRootPath();

		String parentFolderPath = getParentFolderPath(documentPath);

		long groupId = SharepointUtil.getGroupId(parentFolderPath);

		long parentFolderId = getLastFolderId(
			groupId, parentFolderPath,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String title = getResourceName(documentPath);
		String description = StringPool.BLANK;
		String changeLog = StringPool.BLANK;

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		String contentType = GetterUtil.get(
			httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE),
			ContentTypes.APPLICATION_OCTET_STREAM);

		String extension = FileUtil.getExtension(title);

		File file = null;

		try {
			file = FileUtil.createTempFile(extension);

			FileUtil.write(file, sharepointRequest.getBytes());

			if (contentType.equals(ContentTypes.APPLICATION_OCTET_STREAM)) {
				contentType = MimeTypesUtil.getContentType(file, title);
			}

			try {
				FileEntry fileEntry = getFileEntry(sharepointRequest);

				description = fileEntry.getDescription();

				serviceContext.setAssetTagNames(
					AssetTagLocalServiceUtil.getTagNames(
						DLFileEntryConstants.getClassName(),
						fileEntry.getFileEntryId()));

				DLAppServiceUtil.updateFileEntry(
					fileEntry.getFileEntryId(), title, contentType, title,
					StringPool.BLANK, description, changeLog,
					DLVersionNumberIncrease.fromMajorVersion(false), file,
					fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
					fileEntry.getReviewDate(), serviceContext);
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileEntryException);
				}

				DLAppServiceUtil.addFileEntry(
					null, groupId, parentFolderId, title, contentType, null,
					null, description, changeLog, file, null, null, null,
					serviceContext);
			}
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public Tree[] removeDocument(SharepointRequest sharepointRequest) {
		String parentFolderPath = sharepointRequest.getRootPath();

		long groupId = SharepointUtil.getGroupId(parentFolderPath);

		Folder folder = null;
		FileEntry fileEntry = null;

		try {
			long parentFolderId = getLastFolderId(
				groupId, parentFolderPath,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			folder = DLAppServiceUtil.getFolder(parentFolderId);
		}
		catch (Exception exception1) {
			if (exception1 instanceof NoSuchFolderException) {
				try {
					fileEntry = getFileEntry(sharepointRequest);
				}
				catch (Exception exception2) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception2);
					}
				}
			}
		}

		Tree removedDocsTree = new Tree();
		Tree failedDocsTree = new Tree();

		Tree removedDirsTree = new Tree();
		Tree failedDirsTree = new Tree();

		if (fileEntry != null) {
			Tree documentTree = new Tree();

			try {
				documentTree = getFileEntryTree(fileEntry, parentFolderPath);

				DLAppServiceUtil.deleteFileEntry(fileEntry.getFileEntryId());

				removedDocsTree.addChild(documentTree);
			}
			catch (Exception exception1) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception1);
				}

				try {
					failedDocsTree.addChild(documentTree);
				}
				catch (Exception exception2) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception2);
					}
				}
			}
		}
		else if (folder != null) {
			Tree folderTree = new Tree();

			try {
				folderTree = getFolderTree(folder, parentFolderPath);

				DLAppServiceUtil.deleteFolder(folder.getFolderId());

				removedDirsTree.addChild(folderTree);
			}
			catch (Exception exception1) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception1);
				}

				try {
					failedDirsTree.addChild(folderTree);
				}
				catch (Exception exception2) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception2);
					}
				}
			}
		}

		return new Tree[] {
			removedDocsTree, removedDirsTree, failedDocsTree, failedDirsTree
		};
	}

	protected FileEntry getFileEntry(SharepointRequest sharepointRequest)
		throws Exception {

		String documentPath = sharepointRequest.getRootPath();

		String parentFolderPath = getParentFolderPath(documentPath);

		long groupId = SharepointUtil.getGroupId(parentFolderPath);

		long parentFolderId = getLastFolderId(
			groupId, parentFolderPath,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String title = getResourceName(documentPath);

		return DLAppServiceUtil.getFileEntry(groupId, parentFolderId, title);
	}

	protected Tree getFileEntryTree(
		FileEntry fileEntry, String parentFolderPath) {

		String documentPath = StringBundler.concat(
			parentFolderPath, StringPool.SLASH, fileEntry.getTitle());

		return getDocumentTree(
			documentPath, fileEntry.getCreateDate(),
			fileEntry.getModifiedDate(), fileEntry.getSize(),
			fileEntry.getUserName(), fileEntry.getVersion());
	}

	protected Tree getFolderTree(Folder folder, String parentFolderPath) {
		String folderPath = StringBundler.concat(
			parentFolderPath, StringPool.SLASH, folder.getName());

		return getFolderTree(
			folderPath, folder.getCreateDate(), folder.getModifiedDate(),
			folder.getLastPostDate());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLSharepointStorageImpl.class);

}