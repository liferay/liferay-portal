/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.cmis.internal;

import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelCreateDateComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelModifiedDateComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelSizeComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelTitleComparator;
import com.liferay.document.library.repository.cmis.BaseCmisRepository;
import com.liferay.document.library.repository.cmis.CMISRepositoryHandler;
import com.liferay.document.library.repository.cmis.configuration.CMISRepositoryConfiguration;
import com.liferay.document.library.repository.cmis.internal.model.CMISFileEntry;
import com.liferay.document.library.repository.cmis.internal.model.CMISFileVersion;
import com.liferay.document.library.repository.cmis.internal.model.CMISFolder;
import com.liferay.document.library.repository.cmis.search.CMISSearchQueryBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchRepositoryEntryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.DocumentHelper;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RepositoryEntryLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TransientValue;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpSession;

import java.io.InputStream;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.Base64;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

/**
 * CMIS does not provide vendor neutral support for workflow, metadata, tags,
 * categories, etc. They will be ignored in this implementation.
 *
 * @author Alexander Chow
 * @see    <a href="http://wiki.oasis-open.org/cmis/Candidate%20v2%20topics">
 *         Candidate v2 topics</a>
 * @see    <a href="http://wiki.oasis-open.org/cmis/Mixin_Proposal">Mixin /
 *         Aspect Support</a>
 * @see    <a
 *         href="http://www.oasis-open.org/committees/document.php?document_id=39631">
 *         CMIS Type Mutability proposal</a>
 */
public class CMISRepository extends BaseCmisRepository {

	public CMISRepository(
		CMISRepositoryConfiguration cmisRepositoryConfiguration,
		CMISRepositoryHandler cmisRepositoryHandler,
		CMISSearchQueryBuilder cmisSearchQueryBuilder,
		LockManager lockManager) {

		_cmisRepositoryConfiguration = cmisRepositoryConfiguration;
		_cmisRepositoryHandler = cmisRepositoryHandler;
		_cmisSearchQueryBuilder = cmisSearchQueryBuilder;
		_lockManager = lockManager;
	}

	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(title)) {
			if (size == 0) {
				throw new FileNameException("Title is null");
			}

			title = sourceFileName;
		}

		try {
			Session session = getSession();

			_validateTitle(session, folderId, title);

			org.apache.chemistry.opencmis.client.api.Folder cmisFolder =
				_getCmisFolder(session, folderId);

			Map<String, Object> properties = HashMapBuilder.<String, Object>put(
				PropertyIds.NAME, title
			).put(
				PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value()
			).build();

			ContentStream contentStream = new ContentStreamImpl(
				title, BigInteger.valueOf(size), mimeType, inputStream);

			Document document = null;

			if (_cmisRepositoryDetector.isNuxeo5_5OrHigher()) {
				document = cmisFolder.createDocument(
					properties, contentStream, VersioningState.NONE);

				document.checkIn(
					true, Collections.<String, Object>emptyMap(), null,
					StringPool.BLANK);
			}
			else {
				document = cmisFolder.createDocument(
					properties, contentStream, null);
			}

			return toFileEntry(document);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public FileShortcut addFileShortcut(
		String externalReferenceCode, long userId, long folderId,
		long toFileEntryId, ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Folder addFolder(
			String externalReferenceCode, long userId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		try {
			Session session = getSession();

			_validateTitle(session, parentFolderId, name);

			org.apache.chemistry.opencmis.client.api.Folder cmisFolder =
				_getCmisFolder(session, parentFolderId);

			return toFolder(
				cmisFolder.createFolder(
					HashMapBuilder.<String, Object>put(
						PropertyIds.NAME, name
					).put(
						PropertyIds.OBJECT_TYPE_ID,
						BaseTypeId.CMIS_FOLDER.value()
					).build()));
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public FileVersion cancelCheckOut(long fileEntryId) throws PortalException {
		Document draftDocument = null;

		try {
			Session session = getSession();

			String versionSeriesId = toFileEntryId(fileEntryId);

			Document document = (Document)session.getObject(versionSeriesId);

			document.refresh();

			String versionSeriesCheckedOutId =
				document.getVersionSeriesCheckedOutId();

			if (Validator.isNotNull(versionSeriesCheckedOutId)) {
				draftDocument = (Document)session.getObject(
					versionSeriesCheckedOutId);

				draftDocument.cancelCheckOut();

				document = (Document)session.getObject(versionSeriesId);

				document.refresh();
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to cancel checkout for file entry with {fileEntryId=" +
					fileEntryId + "}",
				exception);
		}

		if (draftDocument != null) {
			return toFileVersion(null, draftDocument);
		}

		return null;
	}

	@Override
	public void checkInFileEntry(
		long userId, long fileEntryId,
		DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
		ServiceContext serviceContext) {

		try {
			clearManualCheckInRequired(fileEntryId, serviceContext);

			Session session = getSession();

			String versionSeriesId = toFileEntryId(fileEntryId);

			Document document = (Document)session.getObject(versionSeriesId);

			document.refresh();

			String versionSeriesCheckedOutId =
				document.getVersionSeriesCheckedOutId();

			if (Validator.isNotNull(versionSeriesCheckedOutId)) {
				boolean major = false;

				if (!isSupportsMinorVersions() ||
					(dlVersionNumberIncrease ==
						DLVersionNumberIncrease.MAJOR)) {

					major = true;
				}

				document = (Document)session.getObject(
					versionSeriesCheckedOutId);

				document.checkIn(major, null, null, changeLog);

				document = (Document)session.getObject(versionSeriesId);

				document.refresh();
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to check in file entry with {fileEntryId=" +
					fileEntryId + "}",
				exception);
		}
	}

	@Override
	public void checkInFileEntry(
		long userId, long fileEntryId, String lockUuid,
		ServiceContext serviceContext) {

		checkInFileEntry(
			userId, fileEntryId, DLVersionNumberIncrease.MINOR,
			StringPool.BLANK, serviceContext);
	}

	@Override
	public FileEntry checkOutFileEntry(
			long fileEntryId, ServiceContext serviceContext)
		throws PortalException {

		try {
			setManualCheckInRequired(fileEntryId, serviceContext);

			Session session = getSession();

			String versionSeriesId = toFileEntryId(fileEntryId);

			Document document = (Document)session.getObject(versionSeriesId);

			document.refresh();

			document.checkOut();

			document = (Document)session.getObject(versionSeriesId);

			document.refresh();
		}
		catch (Exception exception) {
			_log.error(
				"Unable checkout file entry with {fileEntryId=" + fileEntryId +
					"}",
				exception);
		}

		return getFileEntry(fileEntryId);
	}

	@Override
	public FileEntry checkOutFileEntry(
		long fileEntryId, String owner, long expirationTime,
		ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FileEntry copyFileEntry(
			long userId, long groupId, long fileEntryId, long destFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			Session session = getSession();

			Document document = _getDocument(session, fileEntryId);

			_validateTitle(session, destFolderId, document.getName());

			String destFolderObjectId = _toFolderId(session, destFolderId);

			Document newDocument = document.copy(
				new ObjectIdImpl(destFolderObjectId));

			return toFileEntry(newDocument);
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFolderException(
				"No CMIS folder with {folderId=" + destFolderId + "}",
				cmisObjectNotFoundException);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		try {
			Document document = _getDocument(getSession(), fileEntryId);

			_deleteMappedFileEntry(document);

			document.deleteAllVersions();
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		try {
			org.apache.chemistry.opencmis.client.api.Folder cmisFolder =
				_getCmisFolder(getSession(), folderId);

			_deleteMappedFolder(cmisFolder);

			cmisFolder.deleteTree(true, UnfileObject.DELETE, false);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	public CMISRepositoryDetector getCMISRepositoryDetector() {
		return _cmisRepositoryDetector;
	}

	@Override
	public List<FileEntry> getFileEntries(
		long folderId, int status, int start, int end,
		OrderByComparator<FileEntry> orderByComparator) {

		return getFileEntries(folderId, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getFileEntries(
		long folderId, int start, int end,
		OrderByComparator<FileEntry> orderByComparator) {

		return _subList(
			getFileEntries(folderId), start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getFileEntries(
		long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<FileEntry> orderByComparator) {

		return new ArrayList<>();
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, String[] mimeTypes, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		List<FileEntry> fileEntries = _cmisModelCache.getFileEntries(folderId);

		if ((fileEntries == null) || (mimeTypes != null)) {
			fileEntries = new ArrayList<>();

			List<String> documentIds = _getDocumentIds(
				getSession(), folderId, mimeTypes);

			for (String documentId : documentIds) {
				FileEntry fileEntry = toFileEntry(documentId);

				fileEntries.add(fileEntry);
			}

			if (mimeTypes == null) {
				_cmisModelCache.putFileEntries(folderId, fileEntries);
			}
		}

		return _subList(fileEntries, start, end, orderByComparator);
	}

	@Override
	public int getFileEntriesCount(long folderId) {
		List<FileEntry> fileEntries = getFileEntries(folderId);

		return fileEntries.size();
	}

	@Override
	public int getFileEntriesCount(long folderId, int status) {
		List<FileEntry> fileEntries = getFileEntries(folderId);

		return fileEntries.size();
	}

	@Override
	public int getFileEntriesCount(long folderId, long fileEntryTypeId) {
		List<FileEntry> fileEntries = getFileEntries(folderId, fileEntryTypeId);

		return fileEntries.size();
	}

	@Override
	public int getFileEntriesCount(long folderId, String[] mimeTypes)
		throws PortalException {

		List<String> documentIds = _getDocumentIds(
			getSession(), folderId, mimeTypes);

		return documentIds.size();
	}

	@Override
	public FileEntry getFileEntry(long fileEntryId) throws PortalException {
		try {
			FileEntry fileEntry = _cmisModelCache.getFileEntry(fileEntryId);

			if (fileEntry == null) {
				fileEntry = toFileEntry(
					_getDocument(getSession(), fileEntryId));

				_cmisModelCache.putFileEntry(fileEntry);
			}

			return fileEntry;
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public FileEntry getFileEntry(long folderId, String title)
		throws PortalException {

		try {
			Session session = getSession();

			String objectId = _getObjectId(session, folderId, true, title);

			if (objectId != null) {
				CmisObject cmisObject = session.getObject(objectId);

				Document document = (Document)cmisObject;

				return toFileEntry(document);
			}
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFileEntryException(
				StringBundler.concat(
					"No CMIS file entry with {folderId=", folderId, ", title=",
					title, "}"),
				cmisObjectNotFoundException);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}

		throw new NoSuchFileEntryException(
			StringBundler.concat(
				"No CMIS file entry with {folderId=", folderId, ", title=",
				title, "}"));
	}

	@Override
	public FileEntry getFileEntryByUuid(String uuid) throws PortalException {
		try {
			Session session = getSession();

			RepositoryEntry repositoryEntry =
				repositoryEntryLocalService.getRepositoryEntry(
					uuid, getGroupId());

			String objectId = repositoryEntry.getMappedId();

			return toFileEntry((Document)session.getObject(objectId));
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFileEntryException(
				"No CMIS file entry with {uuid=" + uuid + "}",
				cmisObjectNotFoundException);
		}
		catch (NoSuchRepositoryEntryException noSuchRepositoryEntryException) {
			throw new NoSuchFileEntryException(noSuchRepositoryEntryException);
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public FileShortcut getFileShortcut(long fileShortcutId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		try {
			return getFileVersion(getSession(), null, fileVersionId);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public Folder getFolder(long folderId) throws PortalException {
		try {
			return getFolder(getSession(), folderId);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public Folder getFolder(long parentFolderId, String name)
		throws PortalException {

		try {
			Session session = getSession();

			String objectId = _getObjectId(
				session, parentFolderId, false, name);

			if (objectId != null) {
				CmisObject cmisObject = session.getObject(objectId);

				return toFolder(
					(org.apache.chemistry.opencmis.client.api.Folder)
						cmisObject);
			}
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFolderException(
				StringBundler.concat(
					"No CMIS folder with {parentFolderId=", parentFolderId,
					", name=", name, "}"),
				cmisObjectNotFoundException);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}

		throw new NoSuchFolderException(
			StringBundler.concat(
				"No CMIS folder with {parentFolderId=", parentFolderId,
				", name=", name, "}"));
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, boolean includeMountfolders, int start,
			int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return _subList(
			getFolders(parentFolderId), start, end, orderByComparator);
	}

	@Override
	public List<Object> getFoldersAndFileEntries(
		long folderId, int start, int end,
		OrderByComparator<?> orderByComparator) {

		return _subList(
			getFoldersAndFileEntries(folderId), start, end,
			(OrderByComparator<Object>)orderByComparator);
	}

	@Override
	public List<Object> getFoldersAndFileEntries(
			long folderId, String[] mimeTypes, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		List<Object> foldersAndFileEntries =
			_cmisModelCache.getFoldersAndFileEntries(folderId);

		if ((foldersAndFileEntries == null) || (mimeTypes != null)) {
			foldersAndFileEntries = new ArrayList<>();

			foldersAndFileEntries.addAll(getFolders(folderId));
			foldersAndFileEntries.addAll(
				getFileEntries(
					folderId, mimeTypes, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null));

			if (mimeTypes == null) {
				_cmisModelCache.putFoldersAndFileEntries(
					folderId, foldersAndFileEntries);
			}
		}

		return _subList(
			foldersAndFileEntries, start, end,
			(OrderByComparator<Object>)orderByComparator);
	}

	@Override
	public int getFoldersAndFileEntriesCount(long folderId) {
		List<Object> foldersAndFileEntries = getFoldersAndFileEntries(folderId);

		return foldersAndFileEntries.size();
	}

	@Override
	public int getFoldersAndFileEntriesCount(long folderId, String[] mimeTypes)
		throws PortalException {

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			List<Folder> folders = getFolders(folderId);

			List<String> documentIds = _getDocumentIds(
				getSession(), folderId, mimeTypes);

			return folders.size() + documentIds.size();
		}

		List<Object> foldersAndFileEntries = getFoldersAndFileEntries(folderId);

		return foldersAndFileEntries.size();
	}

	@Override
	public int getFoldersCount(long parentFolderId, boolean includeMountfolders)
		throws PortalException {

		List<Folder> folders = getFolders(parentFolderId);

		return folders.size();
	}

	@Override
	public int getFoldersFileEntriesCount(List<Long> folderIds, int status) {
		int count = 0;

		for (long folderId : folderIds) {
			List<FileEntry> fileEntries = getFileEntries(folderId);

			count += fileEntries.size();
		}

		return count;
	}

	@Override
	public String getLatestVersionId(String objectId) {
		try {
			Session session = getSession();

			Document document = (Document)session.getObject(objectId);

			List<Document> documentVersions = document.getAllVersions();

			document = documentVersions.get(0);

			return document.getId();
		}
		catch (Exception exception) {
			throw new RepositoryException(exception);
		}
	}

	@Override
	public List<Folder> getMountFolders(
		long parentFolderId, int start, int end,
		OrderByComparator<Folder> orderByComparator) {

		return new ArrayList<>();
	}

	@Override
	public int getMountFoldersCount(long parentFolderId) {
		return 0;
	}

	@Override
	public String getObjectName(String objectId) throws PortalException {
		Session session = getSession();

		CmisObject cmisObject = session.getObject(objectId);

		return cmisObject.getName();
	}

	@Override
	public List<String> getObjectPaths(String objectId) throws PortalException {
		Session session = getSession();

		CmisObject cmisObject = session.getObject(objectId);

		if (cmisObject instanceof FileableCmisObject) {
			FileableCmisObject fileableCmisObject =
				(FileableCmisObject)cmisObject;

			return fileableCmisObject.getPaths();
		}

		throw new RepositoryException(
			"CMIS object is unfileable for id " + objectId);
	}

	@Override
	public List<FileShortcut> getRepositoryFileShortcuts(long groupId)
		throws PortalException {

		return new ArrayList<>();
	}

	public Session getSession() throws PortalException {
		Session session = null;

		HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

		if (httpSession != null) {
			TransientValue<Session> transientValue =
				(TransientValue<Session>)httpSession.getAttribute(_sessionKey);

			if (transientValue != null) {
				Object value = transientValue.getValue();

				if (value instanceof Session) {
					session = (Session)value;
				}
				else {
					httpSession.removeAttribute(_sessionKey);
				}
			}
		}

		if (session == null) {
			SessionImpl sessionImpl =
				(SessionImpl)_cmisRepositoryHandler.getSession();

			session = sessionImpl.getSession();
		}

		if (_cmisRepositoryDetector == null) {
			_cmisRepositoryDetector = new CMISRepositoryDetector(
				session.getRepositoryInfo());
		}

		return session;
	}

	@Override
	public void getSubfolderIds(List<Long> folderIds, long folderId) {
		try {
			List<Folder> subfolders = getFolders(
				folderId, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			getSubfolderIds(folderIds, subfolders, true);
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			throw new RepositoryException(exception);
		}
	}

	@Override
	public List<Long> getSubfolderIds(long folderId, boolean recurse) {
		try {
			List<Long> subfolderIds = new ArrayList<>();

			List<Folder> subfolders = getFolders(
				folderId, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			getSubfolderIds(subfolderIds, subfolders, recurse);

			return subfolderIds;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			throw new RepositoryException(exception);
		}
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Override
	public String[][] getSupportedParameters() {
		return _cmisRepositoryHandler.getSupportedParameters();
	}

	@Override
	public void initRepository() throws PortalException {
		try {
			_sessionKey = StringBundler.concat(
				Session.class.getName(), StringPool.POUND, getRepositoryId());

			Session session = getSession();

			session.getRepositoryInfo();
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(
				StringBundler.concat(
					"Unable to initialize CMIS session for repository with ",
					"{repositoryId=", getRepositoryId(), "}"),
				exception);
		}
	}

	@Override
	public boolean isCancelCheckOutAllowable(String objectId)
		throws PortalException {

		return _isActionAllowable(objectId, Action.CAN_CANCEL_CHECK_OUT);
	}

	@Override
	public boolean isCheckInAllowable(String objectId) throws PortalException {
		return _isActionAllowable(objectId, Action.CAN_CHECK_IN);
	}

	@Override
	public boolean isCheckOutAllowable(String objectId) throws PortalException {
		return _isActionAllowable(objectId, Action.CAN_CHECK_OUT);
	}

	public boolean isDocumentRetrievableByVersionSeriesId() {
		return _cmisRepositoryHandler.isDocumentRetrievableByVersionSeriesId();
	}

	public boolean isRefreshBeforePermissionCheck() {
		return _cmisRepositoryHandler.isRefreshBeforePermissionCheck();
	}

	@Override
	public boolean isSupportsMinorVersions() throws PortalException {
		try {
			Session session = getSession();

			RepositoryInfo repositoryInfo = session.getRepositoryInfo();

			return _cmisRepositoryHandler.isSupportsMinorVersions(
				repositoryInfo.getProductName());
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public Lock lockFolder(long folderId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Lock lockFolder(
		long folderId, String owner, boolean inheritable, long expirationTime) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			Session session = getSession();

			String newFolderObjectId = _toFolderId(session, newFolderId);

			Document document = _getDocument(session, fileEntryId);

			_validateTitle(session, newFolderId, document.getName());

			List<org.apache.chemistry.opencmis.client.api.Folder>
				parentFolders = document.getParents();

			org.apache.chemistry.opencmis.client.api.Folder oldFolder =
				parentFolders.get(0);

			String oldFolderObjectId = oldFolder.getId();

			if (oldFolderObjectId.equals(newFolderObjectId)) {
				return toFileEntry(document);
			}

			document = (Document)document.move(
				new ObjectIdImpl(oldFolderObjectId),
				new ObjectIdImpl(newFolderObjectId));

			String versionSeriesId = toFileEntryId(fileEntryId);

			String newObjectId = document.getVersionSeriesId();

			if (!versionSeriesId.equals(newObjectId)) {
				document = (Document)session.getObject(newObjectId);

				_updateMappedId(fileEntryId, document.getVersionSeriesId());
			}

			return toFileEntry(document);
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFolderException(
				"No CMIS folder with {folderId=" + newFolderId + "}",
				cmisObjectNotFoundException);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public Folder moveFolder(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			Session session = getSession();

			org.apache.chemistry.opencmis.client.api.Folder cmisFolder =
				_getCmisFolder(session, folderId);

			_validateTitle(session, parentFolderId, cmisFolder.getName());

			org.apache.chemistry.opencmis.client.api.Folder parentCmisFolder =
				cmisFolder.getFolderParent();

			if (parentCmisFolder == null) {
				throw new RepositoryException(
					"Unable to move CMIS root folder with {folderId=" +
						folderId + "}");
			}

			String objectId = _toFolderId(session, folderId);

			String sourceFolderId = parentCmisFolder.getId();

			String targetFolderId = _toFolderId(session, parentFolderId);

			if (!sourceFolderId.equals(targetFolderId) &&
				!targetFolderId.equals(objectId)) {

				cmisFolder =
					(org.apache.chemistry.opencmis.client.api.Folder)
						cmisFolder.move(
							new ObjectIdImpl(sourceFolderId),
							new ObjectIdImpl(targetFolderId));
			}

			return toFolder(cmisFolder);
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFolderException(
				"No CMIS folder with {folderId=" + parentFolderId + "}",
				cmisObjectNotFoundException);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public Lock refreshFileEntryLock(
		String lockUuid, long companyId, long expirationTime) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Lock refreshFolderLock(
		String lockUuid, long companyId, long expirationTime) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			Document document = _getDocument(getSession(), fileEntryId);

			Document oldVersion = null;

			List<Document> documentVersions = document.getAllVersions();

			for (Document currentVersion : documentVersions) {
				String currentVersionLabel = currentVersion.getVersionLabel();

				if (Validator.isNull(currentVersionLabel)) {
					currentVersionLabel = DLFileEntryConstants.VERSION_DEFAULT;
				}

				if (currentVersionLabel.equals(version)) {
					oldVersion = currentVersion;

					break;
				}
			}

			String mimeType = oldVersion.getContentStreamMimeType();
			String changeLog = LanguageUtil.format(
				serviceContext.getLocale(), "reverted-to-x", version, false);
			String title = oldVersion.getName();
			ContentStream contentStream = oldVersion.getContentStream();

			updateFileEntry(
				userId, fileEntryId, contentStream.getFileName(), mimeType,
				title, StringPool.BLANK, StringPool.BLANK, changeLog,
				DLVersionNumberIncrease.MAJOR, contentStream.getStream(),
				contentStream.getLength(), null, null, null, serviceContext);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public Hits search(long creatorUserId, int status, int start, int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Hits search(
		long creatorUserId, long folderId, String[] mimeTypes, int status,
		int start, int end) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Hits search(SearchContext searchContext, Query query)
		throws SearchException {

		try {
			QueryConfig queryConfig = searchContext.getQueryConfig();

			queryConfig.setScoreEnabled(false);

			return _search(searchContext, query);
		}
		catch (Exception exception) {
			throw new SearchException(exception);
		}
	}

	public FileEntry toFileEntry(Document document) throws PortalException {
		return toFileEntry(document, false);
	}

	@Override
	public FileEntry toFileEntry(String objectId) throws PortalException {
		return toFileEntry(objectId, false);
	}

	public FileVersion toFileVersion(FileEntry fileEntry, Document version)
		throws PortalException {

		RepositoryEntry repositoryEntry = getRepositoryEntry(version.getId());

		return new CMISFileVersion(
			this, fileEntry, repositoryEntry.getUuid(),
			repositoryEntry.getRepositoryEntryId(), version);
	}

	public Folder toFolder(
			org.apache.chemistry.opencmis.client.api.Folder cmisFolder)
		throws PortalException {

		RepositoryEntry repositoryEntry = getRepositoryEntry(
			cmisFolder.getId());

		return new CMISFolder(
			this, repositoryEntry.getUuid(),
			repositoryEntry.getRepositoryEntryId(), cmisFolder);
	}

	@Override
	public Folder toFolder(String objectId) throws PortalException {
		try {
			Session session = getSession();

			org.apache.chemistry.opencmis.client.api.Folder cmisFolder =
				(org.apache.chemistry.opencmis.client.api.Folder)
					session.getObject(objectId);

			return toFolder(cmisFolder);
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFolderException(
				"No CMIS folder with {objectId=" + objectId + "}",
				cmisObjectNotFoundException);
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public void unlockFolder(long folderId, String lockUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		Document document = null;

		ObjectId checkOutDocumentObjectId = null;

		try {
			Session session = getSession();

			document = _getDocument(session, fileEntryId);

			String versionSeriesCheckedOutId =
				document.getVersionSeriesCheckedOutId();

			if (Validator.isNotNull(versionSeriesCheckedOutId)) {
				document = (Document)session.getObject(
					versionSeriesCheckedOutId);

				document.refresh();
			}

			String currentTitle = document.getName();

			AllowableActions allowableActions = document.getAllowableActions();

			Set<Action> allowableActionsSet =
				allowableActions.getAllowableActions();

			if (allowableActionsSet.contains(Action.CAN_CHECK_OUT)) {
				checkOutDocumentObjectId = document.checkOut();

				document = (Document)session.getObject(
					checkOutDocumentObjectId);
			}

			Map<String, Object> properties = null;

			ContentStream contentStream = null;

			if (Validator.isNotNull(title) && !title.equals(currentTitle)) {
				properties = HashMapBuilder.<String, Object>put(
					PropertyIds.NAME, title
				).build();
			}

			if (inputStream != null) {
				contentStream = new ContentStreamImpl(
					sourceFileName, BigInteger.valueOf(size), mimeType,
					inputStream);
			}

			_checkUpdatable(allowableActionsSet, properties, contentStream);

			if (checkOutDocumentObjectId != null) {
				boolean majorVersion = false;

				if (!isSupportsMinorVersions() ||
					(dlVersionNumberIncrease ==
						DLVersionNumberIncrease.MAJOR)) {

					majorVersion = true;
				}

				document.checkIn(
					majorVersion, properties, contentStream, changeLog);

				checkOutDocumentObjectId = null;
			}
			else {
				if (properties != null) {
					document = (Document)document.updateProperties(properties);
				}

				if (contentStream != null) {
					document.setContentStream(contentStream, true, false);
				}
			}

			String versionSeriesId = toFileEntryId(fileEntryId);

			document = (Document)session.getObject(versionSeriesId);

			return toFileEntry(document);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
		finally {
			if (checkOutDocumentObjectId != null) {
				document.cancelCheckOut();
			}
		}
	}

	@Override
	public FileEntry updateFileEntry(
			String objectId, String mimeType, Map<String, Object> properties,
			InputStream inputStream, String sourceFileName, long size,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			Session session = getSession();

			Document document = (Document)session.getObject(objectId);

			AllowableActions allowableActions = document.getAllowableActions();

			Set<Action> allowableActionsSet =
				allowableActions.getAllowableActions();

			ContentStream contentStream = null;

			if (inputStream != null) {
				inputStream = new Base64.InputStream(
					inputStream, Base64.ENCODE);

				contentStream = new ContentStreamImpl(
					sourceFileName, BigInteger.valueOf(size), mimeType,
					inputStream);
			}

			_checkUpdatable(allowableActionsSet, properties, contentStream);

			if (properties != null) {
				document = (Document)document.updateProperties(properties);
			}

			if (contentStream != null) {
				document.setContentStream(contentStream, true, false);
			}

			return toFileEntry(document);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public FileShortcut updateFileShortcut(
		long userId, long fileShortcutId, long folderId, long toFileEntryId,
		ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void updateFileShortcuts(
		long oldToFileEntryId, long newToFileEntryId) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Folder updateFolder(
			long folderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			Session session = getSession();

			String objectId = _toFolderId(session, folderId);

			org.apache.chemistry.opencmis.client.api.Folder cmisFolder =
				(org.apache.chemistry.opencmis.client.api.Folder)
					session.getObject(objectId);

			String currentTitle = cmisFolder.getName();

			Map<String, Object> properties = new HashMap<>();

			if (Validator.isNotNull(name) && !name.equals(currentTitle)) {
				properties.put(PropertyIds.NAME, name);
			}

			ObjectId cmisFolderObjectId = cmisFolder.updateProperties(
				properties, true);

			String newObjectId = cmisFolderObjectId.getId();

			if (!objectId.equals(newObjectId)) {
				cmisFolder =
					(org.apache.chemistry.opencmis.client.api.Folder)
						session.getObject(newObjectId);

				_updateMappedId(folderId, newObjectId);
			}

			return toFolder(cmisFolder);
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFolderException(
				"No CMIS folder with {folderId=" + folderId + "}",
				cmisObjectNotFoundException);
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	@Override
	public boolean verifyFileEntryCheckOut(long fileEntryId, String lockUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean verifyInheritableLock(long folderId, String lockUuid) {
		throw new UnsupportedOperationException();
	}

	protected List<FileEntry> getFileEntries(long folderId) {
		_cacheFoldersAndFileEntries(folderId);

		return _cmisModelCache.getFileEntries(folderId);
	}

	protected List<FileEntry> getFileEntries(long folderId, long repositoryId) {
		return new ArrayList<>();
	}

	protected FileVersion getFileVersion(
			Session session, FileEntry fileEntry, long fileVersionId)
		throws PortalException {

		try {
			String objectId = _toFileVersionId(fileVersionId);

			return toFileVersion(
				fileEntry, (Document)session.getObject(objectId));
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFileVersionException(
				"No CMIS file version with {fileVersionId=" + fileVersionId +
					"}",
				cmisObjectNotFoundException);
		}
	}

	protected Folder getFolder(Session session, long folderId)
		throws PortalException {

		try {
			Folder folder = _cmisModelCache.getFolder(folderId);

			if (folder == null) {
				String objectId = _toFolderId(session, folderId);

				CmisObject cmisObject = session.getObject(objectId);

				Object object = _toFolderOrFileEntry(cmisObject);

				if (!(object instanceof Folder)) {
					throw new NoSuchFolderException(
						"No CMIS folder with {folderId=" + folderId + "}");
				}

				folder = (Folder)object;

				_cmisModelCache.putFolder(folder);
			}

			return folder;
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFolderException(
				"No CMIS folder with {folderId=" + folderId + "}",
				cmisObjectNotFoundException);
		}
	}

	protected List<Folder> getFolders(long parentFolderId)
		throws PortalException {

		List<Folder> folders = _cmisModelCache.getFolders(parentFolderId);

		if (folders == null) {
			List<String> folderIds = _getCmisFolderIds(
				getSession(), parentFolderId);

			folders = new ArrayList<>(folderIds.size());

			for (String folderId : folderIds) {
				folders.add(toFolder(folderId));
			}

			_cmisModelCache.putFolders(parentFolderId, folders);
		}

		return folders;
	}

	protected List<Object> getFoldersAndFileEntries(long folderId) {
		_cacheFoldersAndFileEntries(folderId);

		return _cmisModelCache.getFoldersAndFileEntries(folderId);
	}

	protected void getSubfolderIds(
			List<Long> subfolderIds, List<Folder> subfolders, boolean recurse)
		throws PortalException {

		for (Folder subfolder : subfolders) {
			long subfolderId = subfolder.getFolderId();

			subfolderIds.add(subfolderId);

			if (recurse) {
				List<Folder> subSubFolders = getFolders(
					subfolderId, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null);

				getSubfolderIds(subfolderIds, subSubFolders, recurse);
			}
		}
	}

	protected FileEntry toFileEntry(Document document, boolean strict)
		throws PortalException {

		RepositoryEntry repositoryEntry = null;

		if (isDocumentRetrievableByVersionSeriesId()) {
			repositoryEntry = getRepositoryEntry(document.getVersionSeriesId());
		}
		else {
			repositoryEntry = getRepositoryEntry(document.getId());
		}

		return new CMISFileEntry(
			this, repositoryEntry.getUuid(),
			repositoryEntry.getRepositoryEntryId(), document, _lockManager);
	}

	protected FileEntry toFileEntry(String objectId, boolean strict)
		throws PortalException {

		try {
			Session session = getSession();

			Document document = (Document)session.getObject(objectId);

			return toFileEntry(document, strict);
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFileEntryException(
				"No CMIS file entry with {objectId=" + objectId + "}",
				cmisObjectNotFoundException);
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			_processException(exception);

			throw new RepositoryException(exception);
		}
	}

	protected String toFileEntryId(long fileEntryId) throws PortalException {
		RepositoryEntry repositoryEntry =
			repositoryEntryLocalService.fetchRepositoryEntry(fileEntryId);

		if (repositoryEntry == null) {
			throw new NoSuchFileEntryException(
				"No CMIS file entry with {fileEntryId=" + fileEntryId + "}");
		}

		return repositoryEntry.getMappedId();
	}

	private void _cacheFoldersAndFileEntries(long folderId) {
		try {
			if (_cmisModelCache.getFoldersAndFileEntries(folderId) != null) {
				return;
			}

			List<Object> foldersAndFileEntries = new ArrayList<>();
			List<Folder> folders = new ArrayList<>();
			List<FileEntry> fileEntries = new ArrayList<>();

			Session session = getSession();

			org.apache.chemistry.opencmis.client.api.Folder cmisParentFolder =
				_getCmisFolder(session, folderId);

			Folder parentFolder = toFolder(cmisParentFolder);

			OperationContext operationContext =
				session.createOperationContext();

			operationContext.setFilter(
				_toSet(
					"cmis:contentStreamLength", "cmis:isPrivateWorkingCopy",
					"cmis:isVersionSeriesCheckedOut",
					"cmis:lastModificationDate", "cmis:lastModifiedBy",
					"cmis:name", "cmis:versionLabel", "cmis:versionSeriesId"));

			ItemIterable<CmisObject> cmisObjects = cmisParentFolder.getChildren(
				operationContext);

			for (CmisObject cmisObject : cmisObjects) {
				if (cmisObject instanceof
						org.apache.chemistry.opencmis.client.api.Folder) {

					CMISFolder cmisFolder = (CMISFolder)toFolder(
						(org.apache.chemistry.opencmis.client.api.Folder)
							cmisObject);

					cmisFolder.setParentFolder(parentFolder);

					foldersAndFileEntries.add(cmisFolder);
					folders.add(cmisFolder);

					_cmisModelCache.putFolder(cmisFolder);
				}
				else if (cmisObject instanceof Document) {
					Document document = (Document)cmisObject;

					CMISFileEntry cmisFileEntry = (CMISFileEntry)toFileEntry(
						document);

					cmisFileEntry.setParentFolder(parentFolder);

					Boolean privateWorkingCopy =
						document.isPrivateWorkingCopy();

					if (((privateWorkingCopy != null) && privateWorkingCopy) ||
						Objects.equals(document.getVersionLabel(), "pwc")) {

						foldersAndFileEntries.remove(cmisFileEntry);
						fileEntries.remove(cmisFileEntry);
					}

					foldersAndFileEntries.add(cmisFileEntry);
					fileEntries.add(cmisFileEntry);

					_cmisModelCache.putFileEntry(cmisFileEntry);
				}
			}

			_cmisModelCache.putFoldersAndFileEntries(
				folderId, foldersAndFileEntries);

			_cmisModelCache.putFolders(folderId, folders);

			_cmisModelCache.putFileEntries(folderId, fileEntries);
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			throw new RepositoryException(exception);
		}
	}

	private void _checkUpdatable(
			Set<Action> allowableActionsSet, Map<String, Object> properties,
			ContentStream contentStream)
		throws PrincipalException {

		if ((properties != null) &&
			!allowableActionsSet.contains(Action.CAN_UPDATE_PROPERTIES)) {

			throw new PrincipalException.MustHavePermission(
				0, Action.CAN_UPDATE_PROPERTIES.toString());
		}

		if ((contentStream != null) &&
			!allowableActionsSet.contains(Action.CAN_SET_CONTENT_STREAM)) {

			throw new PrincipalException.MustHavePermission(
				0, Action.CAN_SET_CONTENT_STREAM.toString());
		}
	}

	private void _deleteMappedFileEntry(Document document)
		throws PortalException {

		if (_cmisRepositoryConfiguration.deleteDepth() == _DELETE_NONE) {
			return;
		}

		List<Document> documentVersions = document.getAllVersions();

		List<String> mappedIds = new ArrayList<>(documentVersions.size() + 1);

		for (Document version : documentVersions) {
			mappedIds.add(version.getId());
		}

		mappedIds.add(document.getId());

		repositoryEntryLocalService.deleteRepositoryEntries(
			getRepositoryId(), mappedIds);
	}

	private void _deleteMappedFolder(
			org.apache.chemistry.opencmis.client.api.Folder cmisFolder)
		throws PortalException {

		if (_cmisRepositoryConfiguration.deleteDepth() == _DELETE_NONE) {
			return;
		}

		ItemIterable<CmisObject> cmisObjects = cmisFolder.getChildren();

		for (CmisObject cmisObject : cmisObjects) {
			if (cmisObject instanceof Document) {
				Document document = (Document)cmisObject;

				_deleteMappedFileEntry(document);
			}
			else if (cmisObject instanceof
						org.apache.chemistry.opencmis.client.api.Folder) {

				org.apache.chemistry.opencmis.client.api.Folder cmisSubfolder =
					(org.apache.chemistry.opencmis.client.api.Folder)cmisObject;

				try {
					repositoryEntryLocalService.deleteRepositoryEntry(
						getRepositoryId(), cmisObject.getId());

					if (_cmisRepositoryConfiguration.deleteDepth() ==
							_DELETE_DEEP) {

						_deleteMappedFolder(cmisSubfolder);
					}
				}
				catch (NoSuchRepositoryEntryException
							noSuchRepositoryEntryException) {

					if (_log.isWarnEnabled()) {
						_log.warn(noSuchRepositoryEntryException);
					}
				}
			}
		}
	}

	private DLFolder _fetchDLFolder(long folderId) {
		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return dlFolderLocalService.fetchFolder(folderId);
		}

		Repository repository = RepositoryLocalServiceUtil.fetchRepository(
			getRepositoryId());

		if (repository == null) {
			return null;
		}

		return dlFolderLocalService.fetchFolder(repository.getDlFolderId());
	}

	private org.apache.chemistry.opencmis.client.api.Folder _getCmisFolder(
			Session session, long folderId)
		throws PortalException {

		Folder folder = getFolder(session, folderId);

		return (org.apache.chemistry.opencmis.client.api.Folder)
			folder.getModel();
	}

	private List<String> _getCmisFolderIds(Session session, long folderId)
		throws PortalException {

		StringBundler sb = new StringBundler(4);

		sb.append("SELECT cmis:objectId FROM cmis:folder");

		if (folderId > 0) {
			sb.append(" WHERE IN_FOLDER(");

			String objectId = _toFolderId(session, folderId);

			sb.append(StringUtil.quote(objectId));

			sb.append(StringPool.CLOSE_PARENTHESIS);
		}

		String query = sb.toString();

		if (_log.isDebugEnabled()) {
			_log.debug("Calling query " + query);
		}

		ItemIterable<QueryResult> queryResults = session.query(
			query, _isAllVersionsSearchableSupported(session));

		List<String> cmsFolderIds = new ArrayList<>();

		for (QueryResult queryResult : queryResults) {
			PropertyData<String> propertyData = queryResult.getPropertyById(
				PropertyIds.OBJECT_ID);

			List<String> values = propertyData.getValues();

			String value = values.get(0);

			cmsFolderIds.add(value);
		}

		return cmsFolderIds;
	}

	private Document _getDocument(Session session, long fileEntryId)
		throws PortalException {

		try {
			String versionSeriesId = toFileEntryId(fileEntryId);

			CmisObject object = session.getObject(versionSeriesId);

			if (!(object instanceof Document)) {
				throw new NoSuchFileEntryException(
					"No CMIS file entry with {fileEntryId=" + fileEntryId +
						"}");
			}

			return (Document)object;
		}
		catch (CmisObjectNotFoundException cmisObjectNotFoundException) {
			throw new NoSuchFileEntryException(
				"No CMIS file entry with {fileEntryId=" + fileEntryId + "}",
				cmisObjectNotFoundException);
		}
	}

	private List<String> _getDocumentIds(
			Session session, long folderId, String[] mimeTypes)
		throws PortalException {

		StringBundler sb = new StringBundler();

		sb.append("SELECT cmis:objectId FROM cmis:document");

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			sb.append(" WHERE cmis:contentStreamMimeType IN (");

			for (int i = 0; i < mimeTypes.length; i++) {
				sb.append(StringUtil.quote(mimeTypes[i]));

				if ((i + 1) < mimeTypes.length) {
					sb.append(", ");
				}
			}

			sb.append(StringPool.CLOSE_PARENTHESIS);
		}

		if (folderId > 0) {
			if (ArrayUtil.isNotEmpty(mimeTypes)) {
				sb.append(" AND ");
			}
			else {
				sb.append(" WHERE ");
			}

			sb.append("IN_FOLDER(");

			String objectId = _toFolderId(session, folderId);

			sb.append(StringUtil.quote(objectId));

			sb.append(StringPool.CLOSE_PARENTHESIS);
		}

		String query = sb.toString();

		if (_log.isDebugEnabled()) {
			_log.debug("Calling query " + query);
		}

		ItemIterable<QueryResult> queryResults = session.query(query, false);

		List<String> cmisDocumentIds = new ArrayList<>();

		for (QueryResult queryResult : queryResults) {
			String objectId = queryResult.getPropertyValueByQueryName(
				PropertyIds.OBJECT_ID);

			cmisDocumentIds.add(objectId);
		}

		return cmisDocumentIds;
	}

	private String _getObjectId(
			Session session, long folderId, boolean fileEntry, String name)
		throws PortalException {

		String objectId = _toFolderId(session, folderId);

		StringBundler sb = new StringBundler(7);

		sb.append("SELECT cmis:objectId FROM ");

		if (fileEntry) {
			sb.append("cmis:document ");
		}
		else {
			sb.append("cmis:folder ");
		}

		sb.append("WHERE cmis:name = '");
		sb.append(name);
		sb.append("' AND IN_FOLDER('");
		sb.append(objectId);
		sb.append("')");

		String query = sb.toString();

		if (_log.isDebugEnabled()) {
			_log.debug("Calling query " + query);
		}

		ItemIterable<QueryResult> queryResults = session.query(query, false);

		Iterator<QueryResult> iterator = queryResults.iterator();

		if (!iterator.hasNext()) {
			return null;
		}

		QueryResult queryResult = iterator.next();

		PropertyData<String> propertyData = queryResult.getPropertyById(
			PropertyIds.OBJECT_ID);

		List<String> values = propertyData.getValues();

		return values.get(0);
	}

	private boolean _isActionAllowable(String objectId, Action action)
		throws PortalException {

		Session session = getSession();

		Document document = (Document)session.getObject(objectId);

		AllowableActions allowableActions = document.getAllowableActions();

		Set<Action> allowableActionsSet =
			allowableActions.getAllowableActions();

		return allowableActionsSet.contains(action);
	}

	private boolean _isAllVersionsSearchableSupported(Session session) {
		RepositoryInfo repositoryInfo = session.getRepositoryInfo();

		RepositoryCapabilities repositoryCapabilities =
			repositoryInfo.getCapabilities();

		return repositoryCapabilities.isAllVersionsSearchableSupported();
	}

	private void _processException(Exception exception1)
		throws PortalException {

		String message = exception1.getMessage();

		if (((exception1 instanceof CmisRuntimeException) &&
			 message.contains("authorized")) ||
			(exception1 instanceof CmisPermissionDeniedException)) {

			String login = null;

			try {
				login = _cmisRepositoryHandler.getLogin();
			}
			catch (Exception exception2) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception2);
				}
			}

			throw new PrincipalException.MustBeAuthenticated(login);
		}
	}

	private Hits _search(SearchContext searchContext, Query query)
		throws Exception {

		long startTime = System.currentTimeMillis();

		Session session = getSession();

		RepositoryInfo repositoryInfo = session.getRepositoryInfo();

		RepositoryCapabilities repositoryCapabilities =
			repositoryInfo.getCapabilities();

		QueryConfig queryConfig = searchContext.getQueryConfig();

		CapabilityQuery capabilityQuery =
			repositoryCapabilities.getQueryCapability();

		queryConfig.setAttribute("capabilityQuery", capabilityQuery.value());

		queryConfig.setAttribute(
			"repositoryProductName", repositoryInfo.getProductName());
		queryConfig.setAttribute(
			"repositoryProductVersion", repositoryInfo.getProductVersion());

		String queryString = _cmisSearchQueryBuilder.buildQuery(
			searchContext, query);

		if (_cmisRepositoryDetector.isNuxeo5_4()) {
			queryString +=
				" AND (" + PropertyIds.IS_LATEST_VERSION + " = true)";
		}

		if (_log.isDebugEnabled()) {
			_log.debug("CMIS search query: " + queryString);
		}

		boolean searchAllVersions =
			_cmisRepositoryDetector.isNuxeo5_5OrHigher();

		ItemIterable<QueryResult> queryResults = session.query(
			queryString, searchAllVersions);

		int start = searchContext.getStart();
		int end = searchContext.getEnd();

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS)) {
			start = 0;
		}

		int total = 0;

		List<com.liferay.portal.kernel.search.Document> documents =
			new ArrayList<>();
		List<String> snippets = new ArrayList<>();
		List<Float> scores = new ArrayList<>();

		for (QueryResult queryResult : queryResults) {
			total++;

			if ((total <= start) ||
				((total > end) && (end != QueryUtil.ALL_POS))) {

				continue;
			}

			String objectId = queryResult.getPropertyValueByQueryName(
				PropertyIds.OBJECT_ID);

			if (_log.isDebugEnabled()) {
				_log.debug("Search result object ID " + objectId);
			}

			FileEntry fileEntry = null;

			try {
				fileEntry = toFileEntry(objectId, true);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					Throwable throwable = exception.getCause();

					if (throwable != null) {
						throwable = throwable.getCause();
					}

					if (throwable instanceof CmisObjectNotFoundException) {
						_log.debug(
							"Search result ignored for CMIS document which " +
								"has a version with an invalid object ID " +
									throwable.getMessage());
					}
					else {
						_log.debug(
							"Search result ignored for invalid object ID",
							exception);
					}
				}

				total--;

				continue;
			}

			com.liferay.portal.kernel.search.Document document =
				new DocumentImpl();

			DocumentHelper documentHelper = new DocumentHelper(document);

			documentHelper.setEntryKey(
				fileEntry.getModelClassName(), fileEntry.getFileEntryId());

			document.addKeyword(Field.TITLE, fileEntry.getTitle());

			documents.add(document);

			if (queryConfig.isScoreEnabled()) {
				Object scoreObject = queryResult.getPropertyValueByQueryName(
					"HITS");

				if (scoreObject != null) {
					scores.add(Float.valueOf(scoreObject.toString()));
				}
				else {
					scores.add(1.0F);
				}
			}
			else {
				scores.add(1.0F);
			}

			snippets.add(StringPool.BLANK);
		}

		float searchTime =
			(float)(System.currentTimeMillis() - startTime) / Time.SECOND;

		Hits hits = new HitsImpl();

		hits.setDocs(
			documents.toArray(
				new com.liferay.portal.kernel.search.Document[0]));
		hits.setLength(total);
		hits.setQuery(query);
		hits.setQueryTerms(new String[0]);
		hits.setScores(ArrayUtil.toFloatArray(scores));
		hits.setSearchTime(searchTime);
		hits.setSnippets(snippets.toArray(new String[0]));
		hits.setStart(startTime);

		return hits;
	}

	private <E> List<E> _subList(
		List<E> list, int start, int end,
		OrderByComparator<E> orderByComparator) {

		if ((orderByComparator != null) &&
			(orderByComparator instanceof RepositoryModelCreateDateComparator ||
			 orderByComparator instanceof
				 RepositoryModelModifiedDateComparator ||
			 orderByComparator instanceof RepositoryModelSizeComparator ||
			 orderByComparator instanceof RepositoryModelTitleComparator)) {

			list = ListUtil.sort(list, orderByComparator);
		}

		return ListUtil.subList(list, start, end);
	}

	private String _toFileVersionId(long fileVersionId) throws PortalException {
		RepositoryEntry repositoryEntry =
			repositoryEntryLocalService.fetchRepositoryEntry(fileVersionId);

		if (repositoryEntry == null) {
			throw new NoSuchFileVersionException(
				"No CMIS file version with {fileVersionId=" + fileVersionId +
					"}");
		}

		return repositoryEntry.getMappedId();
	}

	private String _toFolderId(Session session, long folderId)
		throws PortalException {

		RepositoryEntry repositoryEntry =
			repositoryEntryLocalService.fetchRepositoryEntry(folderId);

		if (repositoryEntry != null) {
			return repositoryEntry.getMappedId();
		}

		DLFolder dlFolder = _fetchDLFolder(folderId);

		if (dlFolder == null) {
			throw new NoSuchFolderException(
				"No CMIS folder with {folderId=" + folderId + "}");
		}
		else if (!dlFolder.isMountPoint()) {
			throw new RepositoryException(
				"CMIS repository should not be used with {folderId=" +
					folderId + "}");
		}

		RepositoryInfo repositoryInfo = session.getRepositoryInfo();

		repositoryEntry = repositoryEntryLocalService.getRepositoryEntry(
			dlFolder.getUserId(), getGroupId(), getRepositoryId(),
			repositoryInfo.getRootFolderId());

		return repositoryEntry.getMappedId();
	}

	private Object _toFolderOrFileEntry(CmisObject cmisObject)
		throws PortalException {

		if (cmisObject instanceof Document) {
			return toFileEntry((Document)cmisObject);
		}
		else if (cmisObject instanceof
					org.apache.chemistry.opencmis.client.api.Folder) {

			org.apache.chemistry.opencmis.client.api.Folder cmisFolder =
				(org.apache.chemistry.opencmis.client.api.Folder)cmisObject;

			return toFolder(cmisFolder);
		}

		return null;
	}

	private final <T> Set<T> _toSet(T... items) {
		HashSet<T> set = new HashSet<>();

		for (T item : items) {
			set.add(item);
		}

		return set;
	}

	private void _updateMappedId(long repositoryEntryId, String mappedId)
		throws PortalException {

		RepositoryEntry repositoryEntry =
			repositoryEntryLocalService.getRepositoryEntry(repositoryEntryId);

		if (!mappedId.equals(repositoryEntry.getMappedId())) {
			RepositoryEntryLocalServiceUtil.updateRepositoryEntry(
				repositoryEntryId, mappedId);
		}
	}

	private void _validateTitle(Session session, long folderId, String title)
		throws PortalException {

		String objectId = _getObjectId(session, folderId, true, title);

		if (objectId != null) {
			throw new DuplicateFileEntryException(title);
		}

		objectId = _getObjectId(session, folderId, false, title);

		if (objectId != null) {
			throw new DuplicateFolderNameException(title);
		}
	}

	private static final int _DELETE_DEEP = -1;

	private static final int _DELETE_NONE = 0;

	private static final Log _log = LogFactoryUtil.getLog(CMISRepository.class);

	private static final CMISModelCache _cmisModelCache = new CMISModelCache();

	private final CMISRepositoryConfiguration _cmisRepositoryConfiguration;
	private CMISRepositoryDetector _cmisRepositoryDetector;
	private final CMISRepositoryHandler _cmisRepositoryHandler;
	private final CMISSearchQueryBuilder _cmisSearchQueryBuilder;
	private final LockManager _lockManager;
	private String _sessionKey;

}