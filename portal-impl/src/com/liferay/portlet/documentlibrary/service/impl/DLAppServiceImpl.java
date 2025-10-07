/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.FileEntryLockException;
import com.liferay.document.library.kernel.exception.InvalidFolderException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.document.library.kernel.util.comparator.FolderNameComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelModifiedDateComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelTitleComparator;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.repository.InvalidRepositoryIdException;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryEntry;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.RepositoryPersistence;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.temporaryrepository.TemporaryFileEntryRepository;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.portlet.documentlibrary.service.base.DLAppServiceBaseImpl;
import com.liferay.portlet.documentlibrary.util.DLAppUtil;
import com.liferay.portlet.documentlibrary.util.DLPortletResourcePermissionUtil;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * Provides the remote service for accessing, adding, checking in/out, deleting,
 * locking/unlocking, moving, subscription handling of, trash handling of,
 * updating, and verifying document library file entries and folders. Its
 * methods include permission checks. All portlets should interact with the
 * document library through this class or through DLAppLocalService, rather than
 * through the individual document library service classes.
 *
 * <p>
 * This class provides a unified interface to all Liferay and third party
 * repositories. While the method signatures are universal for all repositories.
 * Additional implementation-specific parameters may be specified in the
 * serviceContext.
 * </p>
 *
 * <p>
 * The <code>repositoryId</code> parameter used by most of the methods is the
 * primary key of the specific repository. If the repository is a default
 * Liferay repository, the <code>repositoryId</code> is the <code>groupId</code>
 * or <code>scopeGroupId</code>. Otherwise, the <code>repositoryId</code> will
 * correspond to values obtained from {@link
 * com.liferay.portal.kernel.service.RepositoryServiceUtil}.
 * </p>
 *
 * @author Alexander Chow
 * @author Mika Koivisto
 * @author Shuyang Zhou
 * @see    DLAppLocalServiceImpl
 */
public class DLAppServiceImpl extends DLAppServiceBaseImpl {

	/**
	 * Adds a file entry and associated metadata. It is created based on a
	 * {@link File} object.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param      repositoryId the primary key of the repository
	 * @param      folderId the primary key of the file entry's parent folder
	 * @param      sourceFileName the original file's name
	 * @param      mimeType the file's MIME type
	 * @param      title the name to be assigned to the file (optionally
	 *             <code>null </code>)
	 * @param      description the file's description
	 * @param      changeLog the file's version change log
	 * @param      file the file's data (optionally <code>null</code>)
	 * @param      serviceContext the service context to be applied. Can set the
	 *             asset category IDs, asset tag names, and expando bridge
	 *             attributes for the file entry. In a Liferay repository, it
	 *             may include:  <ul> <li> fileEntryTypeId - ID for a custom
	 *             file entry type </li> <li> fieldsMap - mapping for fields
	 *             associated with a custom file entry type </li> </ul>
	 * @return     the file entry
	 * @throws     PortalException if a portal exception occurred
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addFileEntry(String, long, long, String, String, String,
	 *             String, String, String, File, Date, Date, Date,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public FileEntry addFileEntry(
			long repositoryId, long folderId, String sourceFileName,
			String mimeType, String title, String description, String changeLog,
			File file, ServiceContext serviceContext)
		throws PortalException {

		return addFileEntry(
			null, repositoryId, folderId, sourceFileName, mimeType, title,
			StringPool.BLANK, description, changeLog, file, null, null, null,
			serviceContext);
	}

	/**
	 * Adds a file entry and associated metadata. It is created based on a byte
	 * array.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param  externalReferenceCode the file entry's external reference code
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the file entry's parent folder
	 * @param  sourceFileName the original file's name
	 * @param  mimeType the file's MIME type
	 * @param  title the name to be assigned to the file (optionally <code>null
	 *         </code>)
	 * @param  urlTitle the url title
	 * @param  description the file's description
	 * @param  changeLog the file's version change log
	 * @param  bytes the file's data (optionally <code>null</code>)
	 * @param  displayDate the file's display date (optionally
	 *         <code>null</code>)
	 * @param  expirationDate the file's expiration date (optionally <code>null
	 *         </code>)
	 * @param  reviewDate the file's review Date (optionally <code>null</code>)
	 * @param  serviceContext the service context to be applied. Can set the
	 *         asset category IDs, asset tag names, and expando bridge
	 *         attributes for the file entry. In a Liferay repository, it may
	 *         include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 *         type </li> <li> fieldsMap - mapping for fields associated with a
	 *         custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long repositoryId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, byte[] bytes,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		File file = null;

		try {
			if (ArrayUtil.isNotEmpty(bytes)) {
				file = FileUtil.createTempFile(bytes);
			}

			return addFileEntry(
				externalReferenceCode, repositoryId, folderId, sourceFileName,
				mimeType, title, urlTitle, description, changeLog, file,
				displayDate, expirationDate, reviewDate, serviceContext);
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to write temporary file", ioException);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	/**
	 * Adds a file entry and associated metadata. It is created based on a
	 * {@link File} object.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param  externalReferenceCode the file entry's external reference code.
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the file entry's parent folder
	 * @param  sourceFileName the original file's name
	 * @param  mimeType the file's MIME type
	 * @param  title the name to be assigned to the file (optionally <code>null
	 *         </code>)
	 * @param  description the file's description
	 * @param  changeLog the file's version change log
	 * @param  file the file's data (optionally <code>null</code>)
	 * @param  displayDate the file's display date (optionally
	 *         <code>null</code>)
	 * @param  expirationDate the file's expiration date (optionally <code>null
	 *         </code>)
	 * @param  reviewDate the file's review Date (optionally <code>null</code>)
	 * @param  serviceContext the service context to be applied. Can set the
	 *         asset category IDs, asset tag names, and expando bridge
	 *         attributes for the file entry. In a Liferay repository, it may
	 *         include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 *         type </li> <li> fieldsMap - mapping for fields associated with a
	 *         custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long repositoryId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		if ((file == null) || !file.exists() || (file.length() == 0)) {
			return addFileEntry(
				externalReferenceCode, repositoryId, folderId, sourceFileName,
				mimeType, title, urlTitle, description, changeLog, null, 0,
				displayDate, expirationDate, reviewDate, serviceContext);
		}

		mimeType = DLAppUtil.getMimeType(sourceFileName, mimeType, title, file);

		Repository repository = getRepository(repositoryId);

		return repository.addFileEntry(
			externalReferenceCode, getUserId(), folderId, sourceFileName,
			mimeType, title, urlTitle, description, changeLog, file,
			displayDate, expirationDate, reviewDate, serviceContext);
	}

	/**
	 * Adds a file entry and associated metadata. It is created based on a
	 * {@link InputStream} object.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param  externalReferenceCode the file entry's external reference code.
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the file entry's parent folder
	 * @param  sourceFileName the original file's name
	 * @param  mimeType the file's MIME type
	 * @param  title the name to be assigned to the file (optionally <code>null
	 *         </code>)
	 * @param  description the file's description
	 * @param  changeLog the file's version change log
	 * @param  inputStream the file's data (optionally <code>null</code>)
	 * @param  size the file's size (optionally <code>0</code>)
	 * @param  displayDate the file's display date (optionally
	 *         <code>null</code>)
	 * @param  expirationDate the file's expiration date (optionally
	 *         <code>null</code>)
	 * @param  reviewDate the file's review Date (optionally <code>null</code>)
	 * @param  serviceContext the service context to be applied. Can set the
	 *         asset category IDs, asset tag names, and expando bridge
	 *         attributes for the file entry. In a Liferay repository, it may
	 *         include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 *         type </li> <li> fieldsMap - mapping for fields associated with a
	 *         custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long repositoryId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		if (inputStream == null) {
			inputStream = new UnsyncByteArrayInputStream(new byte[0]);
			size = 0;
		}

		if (Validator.isNull(mimeType) ||
			mimeType.equals(ContentTypes.APPLICATION_OCTET_STREAM)) {

			if (size == 0) {
				mimeType = MimeTypesUtil.getExtensionContentType(
					DLAppUtil.getExtension(title, sourceFileName));
			}
			else {
				File file = null;

				try {
					file = FileUtil.createTempFile(inputStream);

					return addFileEntry(
						externalReferenceCode, repositoryId, folderId,
						sourceFileName, mimeType, title, urlTitle, description,
						changeLog, file, displayDate, expirationDate,
						reviewDate, serviceContext);
				}
				catch (IOException ioException) {
					throw new SystemException(
						"Unable to write temporary file", ioException);
				}
				finally {
					FileUtil.delete(file);
				}
			}
		}

		Repository repository = getRepository(repositoryId);

		return repository.addFileEntry(
			externalReferenceCode, getUserId(), folderId, sourceFileName,
			mimeType, title, urlTitle, description, changeLog, inputStream,
			size, displayDate, expirationDate, reviewDate, serviceContext);
	}

	/**
	 * Adds a file shortcut to the existing file entry. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the file shortcut's parent folder
	 * @param  toFileEntryId the primary key of the file shortcut's file entry
	 * @param  serviceContext the service context to be applied. Can set the
	 *         asset category IDs, asset tag names, and expando bridge
	 *         attributes for the file entry.
	 * @return the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileShortcut addFileShortcut(
			String externalReferenceCode, long repositoryId, long folderId,
			long toFileEntryId, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.addFileShortcut(
			externalReferenceCode, getUserId(), folderId, toFileEntryId,
			serviceContext);
	}

	/**
	 * Adds a folder.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  name the folder's name
	 * @param  description the folder's description
	 * @param  serviceContext the service context to be applied. In a Liferay
	 *         repository, it may include boolean mountPoint specifying whether
	 *         folder is a facade for mounting a third-party repository
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Folder addFolder(
			String externalReferenceCode, long repositoryId,
			long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		Folder folder = repository.addFolder(
			externalReferenceCode, getUserId(), parentFolderId, name,
			description, serviceContext);

		_dlAppHelperLocalService.addFolder(getUserId(), folder, serviceContext);

		return folder;
	}

	/**
	 * Adds a temporary file entry.
	 *
	 * <p>
	 * This allows a client to upload a file into a temporary location and
	 * manipulate its metadata prior to making it available for public usage.
	 * This is different from checking in and checking out a file entry.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  folderId the primary key of the folder where the file entry will
	 *         eventually reside
	 * @param  folderName the temporary folder's name
	 * @param  fileName the file's original name
	 * @param  file the file's data (optionally <code>null</code>)
	 * @param  mimeType the file's MIME type
	 * @return the temporary file entry
	 * @throws PortalException if a portal exception occurred
	 * @see    TempFileEntryUtil
	 */
	@Override
	public FileEntry addTempFileEntry(
			long groupId, long folderId, String folderName, String fileName,
			File file, String mimeType)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.ADD_DOCUMENT);

		return TempFileEntryUtil.addTempFileEntry(
			groupId, getUserId(), folderName, fileName, file, mimeType);
	}

	/**
	 * Adds a temporary file entry. It is created based on the {@link
	 * InputStream} object.
	 *
	 * <p>
	 * This allows a client to upload a file into a temporary location and
	 * manipulate its metadata prior to making it available for public usage.
	 * This is different from checking in and checking out a file entry.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  folderId the primary key of the folder where the file entry will
	 *         eventually reside
	 * @param  folderName the temporary folder's name
	 * @param  fileName the file's original name
	 * @param  inputStream the file's data
	 * @param  mimeType the file's MIME type
	 * @return the temporary file entry
	 * @throws PortalException if a portal exception occurred
	 * @see    TempFileEntryUtil
	 */
	@Override
	public FileEntry addTempFileEntry(
			long groupId, long folderId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.ADD_DOCUMENT);

		return TempFileEntryUtil.addTempFileEntry(
			groupId, getUserId(), folderName, fileName, inputStream, mimeType);
	}

	/**
	 * Cancels the check out of the file entry. If a user has not checked out
	 * the specified file entry, invoking this method will result in no changes.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param  fileEntryId the primary key of the file entry to cancel the
	 *         checkout
	 * @throws PortalException if a portal exception occurred
	 * @see    #checkInFileEntry(long, String, ServiceContext)
	 * @see    #checkOutFileEntry(long, ServiceContext)
	 */
	@Override
	public void cancelCheckOut(long fileEntryId) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileEntry.getCtCollectionId())) {

			FileVersion draftFileVersion = repository.cancelCheckOut(
				fileEntryId);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			_dlAppHelperLocalService.cancelCheckOut(
				getUserId(), fileEntry, null, fileEntry.getFileVersion(),
				draftFileVersion, serviceContext);
		}
	}

	/**
	 * Checks in the file entry. If a user has not checked out the specified
	 * file entry, invoking this method will result in no changes.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param  fileEntryId the primary key of the file entry to check in
	 * @param  dlVersionNumberIncrease the kind of version number increase to
	 *         apply for these changes.
	 * @param  changeLog the file's version change log
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see    #cancelCheckOut(long)
	 * @see    #checkOutFileEntry(long, ServiceContext)
	 */
	@Override
	public void checkInFileEntry(
			long fileEntryId, DLVersionNumberIncrease dlVersionNumberIncrease,
			String changeLog, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		repository.checkInFileEntry(
			getUserId(), fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		FileVersion fileVersion = fileEntry.getLatestFileVersion();

		_dlAppHelperLocalService.updateFileEntry(
			getUserId(), fileEntry, null, fileVersion,
			fileVersion.getFileVersionId());
	}

	/**
	 * Checks in the file entry using the lock's UUID. If a user has not checked
	 * out the specified file entry, invoking this method will result in no
	 * changes. This method is primarily used by WebDAV.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param  fileEntryId the primary key of the file entry to check in
	 * @param  lockUuid the lock's UUID
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see    #cancelCheckOut(long)
	 * @see    #checkOutFileEntry(long, String, long, ServiceContext)
	 */
	@Override
	public void checkInFileEntry(
			long fileEntryId, String lockUuid, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		repository.checkInFileEntry(
			getUserId(), fileEntryId, lockUuid, serviceContext);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		FileVersion fileVersion = fileEntry.getLatestFileVersion();

		_dlAppHelperLocalService.updateFileEntry(
			getUserId(), fileEntry, null, fileVersion,
			fileVersion.getFileVersionId());
	}

	/**
	 * Check out a file entry.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param  fileEntryId the file entry to check out
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see    #cancelCheckOut(long)
	 * @see    #checkInFileEntry(long, String, ServiceContext)
	 */
	@Override
	public void checkOutFileEntry(
			long fileEntryId, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		FileEntry fileEntry = repository.checkOutFileEntry(
			fileEntryId, serviceContext);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileEntry.getCtCollectionId())) {

			FileVersion fileVersion = fileEntry.getLatestFileVersion();

			_dlAppHelperLocalService.updateFileEntry(
				getUserId(), fileEntry, null, fileVersion, fileEntryId);
		}
	}

	/**
	 * Checks out the file entry. This method is primarily used by WebDAV.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param  fileEntryId the file entry to check out
	 * @param  owner the owner string for the checkout (optionally
	 *         <code>null</code>)
	 * @param  expirationTime the time in milliseconds before the lock expires.
	 *         If the value is <code>0</code>, the default expiration time will
	 *         be used from <code>portal.properties</code>.
	 * @param  serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 * @see    #cancelCheckOut(long)
	 * @see    #checkInFileEntry(long, String, ServiceContext)
	 */
	@Override
	public FileEntry checkOutFileEntry(
			long fileEntryId, String owner, long expirationTime,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		FileEntry fileEntry = repository.checkOutFileEntry(
			fileEntryId, owner, expirationTime, serviceContext);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileEntry.getCtCollectionId())) {

			FileVersion fileVersion = fileEntry.getLatestFileVersion();

			_dlAppHelperLocalService.updateFileEntry(
				getUserId(), fileEntry, null, fileVersion, fileEntryId);
		}

		return fileEntry;
	}

	@Override
	public FileEntry copyFileEntry(
			long fileEntryId, long destinationFolderId,
			long destinationRepositoryId, long fileEntryTypeId, long[] groupIds,
			ServiceContext serviceContext)
		throws PortalException {

		Repository sourceRepository =
			RepositoryProviderUtil.getFileEntryRepository(fileEntryId);

		return copyFileEntry(
			getRepository(destinationRepositoryId),
			sourceRepository.getFileEntry(fileEntryId), destinationFolderId,
			fileEntryTypeId, groupIds, serviceContext);
	}

	@Override
	public FileShortcut copyFileShortcut(
			long fileShortcutId, long destinationFolderId,
			long destinationRepositoryId, ServiceContext serviceContext)
		throws PortalException {

		Repository destinationRepository = RepositoryProviderUtil.getRepository(
			destinationRepositoryId);

		Repository sourceRepository =
			RepositoryProviderUtil.getFileShortcutRepository(fileShortcutId);

		FileShortcut fileShortcut = sourceRepository.getFileShortcut(
			fileShortcutId);

		FileShortcut targetFileShortcut = destinationRepository.addFileShortcut(
			null, getUserId(), destinationFolderId,
			fileShortcut.getToFileEntryId(), serviceContext);

		_copyResourcePermissions(
			fileShortcut.getCompanyId(), DLFileShortcut.class.getName(),
			fileShortcut.getRepositoryId(), fileShortcut.getFileShortcutId(),
			targetFileShortcut.getRepositoryId(),
			targetFileShortcut.getFileShortcutId());

		return targetFileShortcut;
	}

	@Override
	public Folder copyFolder(
			long sourceRepositoryId, long sourceFolderId,
			long destinationRepositoryId, long destinationParentFolderId,
			Map<Long, Long> fileEntryTypeIds, long[] groupIds,
			ServiceContext serviceContext)
		throws PortalException {

		if (sourceRepositoryId == destinationRepositoryId) {
			_validateFolders(
				sourceRepositoryId, sourceFolderId, destinationParentFolderId);
		}

		return copyFolder(
			sourceFolderId, destinationParentFolderId,
			getRepository(sourceRepositoryId),
			getRepository(destinationRepositoryId), fileEntryTypeIds, groupIds,
			serviceContext);
	}

	/**
	 * Performs a deep copy of the folder.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  sourceFolderId the primary key of the folder to copy
	 * @param  parentFolderId the primary key of the new folder's parent folder
	 * @param  name the new folder's name
	 * @param  description the new folder's description
	 * @param  serviceContext the service context to be applied
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Folder copyFolder(
			long repositoryId, long sourceFolderId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		Folder sourceFolder = repository.getFolder(sourceFolderId);

		Folder targetFolder = repository.addFolder(
			null, getUserId(), parentFolderId, name, description,
			serviceContext);

		_dlAppHelperLocalService.addFolder(
			getUserId(), targetFolder, serviceContext);

		copyFolder(repository, sourceFolder, targetFolder, serviceContext);

		return targetFolder;
	}

	/**
	 * Deletes the file entry with the primary key.
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		_dlAppHelperLocalService.deleteFileEntry(
			repository.getFileEntry(fileEntryId));

		repository.deleteFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		Repository repository = getRepository(groupId);

		FileEntry fileEntry = repository.getFileEntryByExternalReferenceCode(
			externalReferenceCode);

		_dlAppHelperLocalService.deleteFileEntry(fileEntry);

		repository.deleteFileEntry(fileEntry.getFileEntryId());
	}

	/**
	 * Deletes the file entry with the title in the folder.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the file entry's parent folder
	 * @param  title the file entry's title
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteFileEntryByTitle(
			long repositoryId, long folderId, String title)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		_dlAppHelperLocalService.deleteFileEntry(
			repository.getFileEntry(folderId, title));

		repository.deleteFileEntry(folderId, title);
	}

	/**
	 * Deletes the file shortcut with the primary key. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param  fileShortcutId the primary key of the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteFileShortcut(long fileShortcutId) throws PortalException {
		Repository repository =
			RepositoryProviderUtil.getFileShortcutRepository(fileShortcutId);

		repository.deleteFileShortcut(fileShortcutId);
	}

	@Override
	public void deleteFileShortcutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		Repository repository = getRepository(groupId);

		FileShortcut fileShortcut =
			repository.getFileShortcutByExternalReferenceCode(
				externalReferenceCode);

		repository.deleteFileShortcut(fileShortcut.getFileShortcutId());
	}

	/**
	 * Deletes the file version. File versions can only be deleted if it is
	 * approved and there are other approved file versions available.
	 *
	 * @param  fileVersionId the primary key of the file version
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteFileVersion(long fileVersionId) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFileVersionRepository(
			fileVersionId);

		repository.deleteFileVersion(fileVersionId);
	}

	/**
	 * Deletes the file version. File versions can only be deleted if it is
	 * approved and there are other approved file versions available. This
	 * method is only supported by the Liferay repository.
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @param  version the version label of the file version
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteFileVersion(long fileEntryId, String version)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		repository.deleteFileVersion(fileEntryId, version);
	}

	/**
	 * Deletes the folder with the primary key and all of its subfolders and
	 * file entries.
	 *
	 * @param  folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteFolder(long folderId) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFolderRepository(
			folderId);

		Folder folder = repository.getFolder(folderId);

		List<FileEntry> fileEntries = repository.getRepositoryFileEntries(
			0, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (FileEntry fileEntry : fileEntries) {
			_dlAppHelperLocalService.deleteFileEntry(fileEntry);
		}

		repository.deleteFolder(folderId);

		_dlAppHelperLocalService.deleteFolder(folder);
	}

	/**
	 * Deletes the folder with the name in the parent folder and all of its
	 * subfolders and file entries.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  name the folder's name
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteFolder(
			long repositoryId, long parentFolderId, String name)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		repository.deleteFolder(parentFolderId, name);
	}

	/**
	 * Deletes the temporary file entry.
	 *
	 * @param  groupId the primary key of the group
	 * @param  folderId the primary key of the folder where the file entry was
	 *         eventually to reside
	 * @param  folderName the temporary folder's name
	 * @param  fileName the file's original name
	 * @throws PortalException if a portal exception occurred
	 * @see    TempFileEntryUtil
	 */
	@Override
	public void deleteTempFileEntry(
			long groupId, long folderId, String folderName, String fileName)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.ADD_DOCUMENT);

		TempFileEntryUtil.deleteTempFileEntry(
			groupId, getUserId(), folderName, fileName);
	}

	/**
	 * Returns all the file entries in the folder.
	 *
	 * @param  repositoryId the primary key of the file entry's repository
	 * @param  folderId the primary key of the file entry's folder
	 * @return the file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getFileEntries(long repositoryId, long folderId)
		throws PortalException {

		return getFileEntries(
			repositoryId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a name-ordered range of all the file entries in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the file entry's repository
	 * @param  folderId the primary key of the file entry's folder
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, int start, int end)
		throws PortalException {

		return getFileEntries(
			repositoryId, folderId, start, end,
			new RepositoryModelTitleComparator<>(true));
	}

	/**
	 * Returns an ordered range of all the file entries in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the file entry's repository
	 * @param  folderId the primary key of the file entry's folder
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the file entries
	 *         (optionally <code>null</code>)
	 * @return the range of file entries in the folder ordered by comparator
	 *         <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFileEntries(
			folderId, start, end, orderByComparator);
	}

	/**
	 * Returns the file entries with the file entry type in the folder.
	 *
	 * @param  repositoryId the primary key of the file entry's repository
	 * @param  folderId the primary key of the file entry's folder
	 * @param  fileEntryTypeId the primary key of the file entry type
	 * @return the file entries with the file entry type in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, long fileEntryTypeId)
		throws PortalException {

		return getFileEntries(
			repositoryId, folderId, fileEntryTypeId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);
	}

	/**
	 * Returns a name-ordered range of all the file entries with the file entry
	 * type in the folder.
	 *
	 * @param  repositoryId the primary key of the file entry's repository
	 * @param  folderId the primary key of the file entry's folder
	 * @param  fileEntryTypeId the primary key of the file entry type
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of the file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, long fileEntryTypeId, int start,
			int end)
		throws PortalException {

		return getFileEntries(
			repositoryId, folderId, fileEntryTypeId, start, end,
			new RepositoryModelTitleComparator<>(true));
	}

	/**
	 * Returns an ordered range of all the file entries with the file entry type
	 * in the folder.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @param  fileEntryTypeId the primary key of the file entry type
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the results by
	 *         (optionally <code>null</code>)
	 * @return the range of file entries with the file entry type in the folder
	 *         ordered by <code>null</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, long fileEntryTypeId, int start,
			int end, OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFileEntries(
			folderId, fileEntryTypeId, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, String[] mimeTypes)
		throws PortalException {

		return getFileEntries(
			repositoryId, folderId, mimeTypes, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, new RepositoryModelTitleComparator<>(true));
	}

	@Override
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, String[] mimeTypes, int start,
			int end, OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFileEntries(
			folderId, mimeTypes, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the file entries and shortcuts in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @param  status the workflow status
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public List<Object> getFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status, int start, int end)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return (List)repository.getFileEntriesAndFileShortcuts(
			folderId, status, start, end);
	}

	/**
	 * Returns the number of file entries and shortcuts in the folder.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @param  status the workflow status
	 * @return the number of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFileEntriesAndFileShortcutsCount(folderId, status);
	}

	/**
	 * Returns the number of file entries and shortcuts in the folder.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @param  status the workflow status
	 * @param  mimeTypes allowed media types
	 * @return the number of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFileEntriesAndFileShortcutsCount(
			folderId, status, mimeTypes);
	}

	/**
	 * Returns the number of file entries in the folder.
	 *
	 * @param  repositoryId the primary key of the file entry's repository
	 * @param  folderId the primary key of the file entry's folder
	 * @return the number of file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getFileEntriesCount(long repositoryId, long folderId)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFileEntriesCount(folderId);
	}

	/**
	 * Returns the number of file entries with the file entry type in the
	 * folder.
	 *
	 * @param  repositoryId the primary key of the file entry's repository
	 * @param  folderId the primary key of the file entry's folder
	 * @param  fileEntryTypeId the primary key of the file entry type
	 * @return the number of file entries with the file entry type in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getFileEntriesCount(
			long repositoryId, long folderId, long fileEntryTypeId)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFileEntriesCount(folderId, fileEntryTypeId);
	}

	@Override
	public int getFileEntriesCount(
			long repositoryId, long folderId, String[] mimeTypes)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFileEntriesCount(folderId, mimeTypes);
	}

	/**
	 * Returns the file entry with the primary key.
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @return the file entry with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry getFileEntry(long fileEntryId) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		return repository.getFileEntry(fileEntryId);
	}

	/**
	 * Returns the file entry with the title in the folder.
	 *
	 * @param  groupId the primary key of the file entry's group
	 * @param  folderId the primary key of the file entry's folder
	 * @param  title the file entry's title
	 * @return the file entry with the title in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry getFileEntry(long groupId, long folderId, String title)
		throws PortalException {

		try {
			Repository repository = getRepository(groupId);

			return repository.getFileEntry(folderId, title);
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				throw noSuchFileEntryException;
			}

			Repository repository = RepositoryProviderUtil.getFolderRepository(
				folderId);

			return repository.getFileEntry(folderId, title);
		}
	}

	/**
	 * Returns the file entry with the external reference code.
	 *
	 * @param  groupId the primary key of the file entry's group
	 * @param  externalReferenceCode the file entry's external reference code
	 * @return the file entry with the external reference code
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		Repository repository = getRepository(groupId);

		return repository.getFileEntryByExternalReferenceCode(
			externalReferenceCode);
	}

	/**
	 * Returns the file entry with the file name in the folder.
	 *
	 * @param  groupId the primary key of the file entry's group
	 * @param  folderId the primary key of the file entry's folder
	 * @param  fileName the file entry's file name
	 * @return the file entry with the file name in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry getFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws PortalException {

		try {
			Repository repository = getRepository(groupId);

			return repository.getFileEntryByFileName(folderId, fileName);
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				throw noSuchFileEntryException;
			}

			Repository repository = RepositoryProviderUtil.getFolderRepository(
				folderId);

			return repository.getFileEntryByFileName(folderId, fileName);
		}
	}

	/**
	 * Returns the file entry with the UUID and group.
	 *
	 * @param  uuid the file entry's UUID
	 * @param  groupId the primary key of the file entry's group
	 * @return the file entry with the UUID and group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		FileEntry fileEntry = fetchFileEntryByUuidAndRepositoryId(
			uuid, groupId);

		if (fileEntry != null) {
			return fileEntry;
		}

		List<com.liferay.portal.kernel.model.Repository> repositories =
			_repositoryPersistence.findByGroupId(groupId);

		for (com.liferay.portal.kernel.model.Repository repository :
				repositories) {

			if (Objects.equals(
					repository.getClassName(),
					TemporaryFileEntryRepository.class.getName())) {

				if (_log.isDebugEnabled()) {
					_log.debug("Skipping temporary file entry repository");
				}

				continue;
			}

			fileEntry = fetchFileEntryByUuidAndRepositoryId(
				uuid, repository.getRepositoryId());

			if (fileEntry != null) {
				return fileEntry;
			}
		}

		throw new NoSuchFileEntryException(
			StringBundler.concat(
				"No DLFileEntry exists with the key {uuid=", uuid, ", groupId=",
				groupId, StringPool.CLOSE_CURLY_BRACE));
	}

	/**
	 * Returns the file shortcut with the primary key. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param  fileShortcutId the primary key of the file shortcut
	 * @return the file shortcut with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException {

		Repository repository =
			RepositoryProviderUtil.getFileShortcutRepository(fileShortcutId);

		return repository.getFileShortcut(fileShortcutId);
	}

	@Override
	public FileShortcut getFileShortcutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		Repository repository = getRepository(groupId);

		return repository.getFileShortcutByExternalReferenceCode(
			externalReferenceCode);
	}

	/**
	 * Returns the file version with the primary key.
	 *
	 * @param  fileVersionId the primary key of the file version
	 * @return the file version with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getFileVersionRepository(
			fileVersionId);

		return repository.getFileVersion(fileVersionId);
	}

	/**
	 * Returns the folder with the primary key.
	 *
	 * @param  folderId the primary key of the folder
	 * @return the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Folder getFolder(long folderId) throws PortalException {
		Repository repository = RepositoryProviderUtil.getFolderRepository(
			folderId);

		return repository.getFolder(folderId);
	}

	/**
	 * Returns the folder with the name in the parent folder.
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  name the folder's name
	 * @return the folder with the name in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Folder getFolder(long repositoryId, long parentFolderId, String name)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFolder(parentFolderId, name);
	}

	@Override
	public Folder getFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		Repository repository = getRepository(groupId);

		return repository.getFolderByExternalReferenceCode(
			externalReferenceCode);
	}

	/**
	 * Returns all immediate subfolders of the parent folder.
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @return the immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getFolders(long repositoryId, long parentFolderId)
		throws PortalException {

		return getFolders(
			repositoryId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all immediate subfolders of the parent folder, optionally
	 * including mount folders for third-party repositories.
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @return the immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, boolean includeMountFolders)
		throws PortalException {

		return getFolders(
			repositoryId, parentFolderId, includeMountFolders,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder, optionally including mount folders for third-party
	 * repositories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 *         folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, boolean includeMountFolders,
			int start, int end)
		throws PortalException {

		return getFolders(
			repositoryId, parentFolderId, includeMountFolders, start, end,
			FolderNameComparator.getInstance(true));
	}

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the folders (optionally
	 *         <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 *         comparator <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, boolean includeMountFolders,
			int start, int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFolders(
			parentFolderId, includeMountFolders, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  status the workflow status
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the folders (optionally
	 *         <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 *         comparator <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, int status,
			boolean includeMountFolders, int start, int end,
			OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFolders(
			parentFolderId, status, includeMountFolders, start, end,
			orderByComparator);
	}

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 *         folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, int start, int end)
		throws PortalException {

		return getFolders(
			repositoryId, parentFolderId, start, end,
			FolderNameComparator.getInstance(true));
	}

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the folders (optionally
	 *         <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 *         comparator <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, int start, int end,
			OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFolders(
			parentFolderId, true, start, end, orderByComparator);
	}

	/**
	 * Returns a name-ordered range of all the immediate subfolders, file
	 * entries, and file shortcuts in the parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the parent folder
	 * @param  status the workflow status
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders, file entries, and
	 *         file shortcuts in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status,
			boolean includeMountFolders, int start, int end)
		throws PortalException {

		return getFoldersAndFileEntriesAndFileShortcuts(
			repositoryId, folderId, status, includeMountFolders, start, end,
			new RepositoryModelTitleComparator<>(true));
	}

	/**
	 * Returns an ordered range of all the immediate subfolders, file entries,
	 * and file shortcuts in the parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the parent folder
	 * @param  status the workflow status
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the results (optionally
	 *         <code>null</code>)
	 * @return the range of immediate subfolders, file entries, and file
	 *         shortcuts in the parent folder ordered by comparator
	 *         <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status,
			boolean includeMountFolders, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		return getFoldersAndFileEntriesAndFileShortcuts(
			repositoryId, folderId, status, null, includeMountFolders, start,
			end, orderByComparator);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, boolean includeOwner, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return (List)repository.getFoldersAndFileEntriesAndFileShortcuts(
			folderId, status, mimeTypes, includeMountFolders, includeOwner,
			start, end, orderByComparator);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		return getFoldersAndFileEntriesAndFileShortcuts(
			repositoryId, folderId, status, mimeTypes, includeMountFolders,
			true, start, end, orderByComparator);
	}

	/**
	 * Returns the number of immediate subfolders, file entries, and file
	 * shortcuts in the parent folder.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the parent folder
	 * @param  status the workflow status
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @return the number of immediate subfolders, file entries, and file
	 *         shortcuts in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	@ThreadLocalCachable
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status,
			boolean includeMountFolders)
		throws PortalException {

		return getFoldersAndFileEntriesAndFileShortcutsCount(
			repositoryId, folderId, status, null, includeMountFolders);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders)
		throws PortalException {

		return getFoldersAndFileEntriesAndFileShortcutsCount(
			repositoryId, folderId, status, mimeTypes, includeMountFolders,
			true);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, boolean includeOwner)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFoldersAndFileEntriesAndFileShortcutsCount(
			folderId, status, mimeTypes, includeMountFolders, includeOwner);
	}

	/**
	 * Returns the number of immediate subfolders of the parent folder.
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getFoldersCount(long repositoryId, long parentFolderId)
		throws PortalException {

		return getFoldersCount(repositoryId, parentFolderId, true);
	}

	/**
	 * Returns the number of immediate subfolders of the parent folder,
	 * optionally including mount folders for third-party repositories.
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getFoldersCount(
			long repositoryId, long parentFolderId, boolean includeMountFolders)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFoldersCount(parentFolderId, includeMountFolders);
	}

	/**
	 * Returns the number of immediate subfolders of the parent folder,
	 * optionally including mount folders for third-party repositories.
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  status the workflow status
	 * @param  includeMountFolders whether to include mount folders for
	 *         third-party repositories
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getFoldersCount(
			long repositoryId, long parentFolderId, int status,
			boolean includeMountFolders)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFoldersCount(
			parentFolderId, status, includeMountFolders);
	}

	/**
	 * Returns the number of immediate subfolders and file entries across the
	 * folders.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderIds the primary keys of folders from which to count
	 *         immediate subfolders and file entries
	 * @param  status the workflow status
	 * @return the number of immediate subfolders and file entries across the
	 *         folders
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getFoldersFileEntriesCount(
			long repositoryId, List<Long> folderIds, int status)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getFoldersFileEntriesCount(folderIds, status);
	}

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the repository default parent folder that are stored within the Liferay
	 * repository. This method is primarily used to search for recently modified
	 * file entries. It can be limited to the file entries modified by a given
	 * user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  userId the primary key of the user who created the file
	 *         (optionally <code>0</code>)
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching file entries ordered by date modified
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, int start, int end)
		throws PortalException {

		return getGroupFileEntries(
			groupId, userId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, start,
			end, new RepositoryModelModifiedDateComparator<>());
	}

	/**
	 * Returns an ordered range of all the file entries in the group that are
	 * stored within the Liferay repository. This method is primarily used to
	 * search for recently modified file entries. It can be limited to the file
	 * entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  userId the primary key of the user who created the file
	 *         (optionally <code>0</code>)
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the file entries
	 *         (optionally <code>null</code>)
	 * @return the range of matching file entries ordered by comparator
	 *         <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getGroupFileEntries(
			groupId, userId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the root folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  userId the primary key of the user who created the file
	 *         (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching file entries ordered by date modified
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, int start, int end)
		throws PortalException {

		return getGroupFileEntries(
			groupId, userId, rootFolderId, start, end,
			new RepositoryModelModifiedDateComparator<>());
	}

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the root folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  userId the primary key of the user who created the file
	 *         (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the file entries
	 *         (optionally <code>null</code>)
	 * @return the range of matching file entries ordered by comparator
	 *         <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(groupId);

		return repository.getRepositoryFileEntries(
			userId, rootFolderId, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(groupId);

		return repository.getRepositoryFileEntries(
			userId, rootFolderId, mimeTypes, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of file entries in a group starting at the repository
	 * default parent folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userId the primary key of the user who created the file
	 *         (optionally <code>0</code>)
	 * @return the number of matching file entries
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getGroupFileEntriesCount(long groupId, long userId)
		throws PortalException {

		return getGroupFileEntriesCount(
			groupId, userId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	/**
	 * Returns the number of file entries in a group starting at the root folder
	 * that are stored within the Liferay repository. This method is primarily
	 * used to search for recently modified file entries. It can be limited to
	 * the file entries modified by a given user.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userId the primary key of the user who created the file
	 *         (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @return the number of matching file entries
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId)
		throws PortalException {

		Repository repository = getRepository(groupId);

		return repository.getRepositoryFileEntriesCount(userId, rootFolderId);
	}

	@Override
	public int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status)
		throws PortalException {

		Repository repository = getRepository(groupId);

		return repository.getRepositoryFileEntriesCount(
			userId, rootFolderId, mimeTypes, status);
	}

	@Override
	public List<FileShortcut> getGroupFileShortcuts(long groupId)
		throws PortalException {

		Repository repository = getRepository(groupId);

		return repository.getRepositoryFileShortcuts(groupId);
	}

	/**
	 * Returns all immediate subfolders of the parent folder that are used for
	 * mounting third-party repositories. This method is only supported by the
	 * Liferay repository.
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @return the immediate subfolders of the parent folder that are used for
	 *         mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getMountFolders(long repositoryId, long parentFolderId)
		throws PortalException {

		return getMountFolders(
			repositoryId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder that are used for mounting third-party repositories. This
	 * method is only supported by the Liferay repository.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  parentFolderId the primary key of the parent folder
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 *         folder that are used for mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getMountFolders(
			long repositoryId, long parentFolderId, int start, int end)
		throws PortalException {

		return getMountFolders(
			repositoryId, parentFolderId, start, end,
			FolderNameComparator.getInstance(true));
	}

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder that are used for mounting third-party repositories. This method
	 * is only supported by the Liferay repository.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  repositoryId the primary key of the folder's repository
	 * @param  parentFolderId the primary key of the folder's parent folder
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator to order the folders (optionally
	 *         <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder that are
	 *         used for mounting third-party repositories ordered by comparator
	 *         <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Folder> getMountFolders(
			long repositoryId, long parentFolderId, int start, int end,
			OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getMountFolders(
			parentFolderId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of immediate subfolders of the parent folder that are
	 * used for mounting third-party repositories. This method is only supported
	 * by the Liferay repository.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  parentFolderId the primary key of the parent folder
	 * @return the number of folders of the parent folder that are used for
	 *         mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getMountFoldersCount(long repositoryId, long parentFolderId)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getMountFoldersCount(parentFolderId);
	}

	@Override
	public void getSubfolderIds(
			long repositoryId, List<Long> folderIds, long folderId)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		repository.getSubfolderIds(folderIds, folderId);
	}

	/**
	 * Returns all the descendant folders of the folder with the primary key.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @return the descendant folders of the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Long> getSubfolderIds(long repositoryId, long folderId)
		throws PortalException {

		return getSubfolderIds(repositoryId, folderId, true);
	}

	/**
	 * Returns descendant folders of the folder with the primary key, optionally
	 * limiting to one level deep.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @param  recurse whether to recurse through each subfolder
	 * @return the descendant folders of the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Long> getSubfolderIds(
			long repositoryId, long folderId, boolean recurse)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.getSubfolderIds(folderId, recurse);
	}

	/**
	 * Returns all the temporary file entry names.
	 *
	 * @param  groupId the primary key of the group
	 * @param  folderId the primary key of the folder where the file entry will
	 *         eventually reside
	 * @param  folderName the temporary folder's name
	 * @return the temporary file entry names
	 * @throws PortalException if a portal exception occurred
	 * @see    #addTempFileEntry(long, long, String, String, File, String)
	 * @see    TempFileEntryUtil
	 */
	@CTAware(onProduction = true)
	@Override
	public String[] getTempFileNames(
			long groupId, long folderId, String folderName)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.ADD_DOCUMENT);

		return TempFileEntryUtil.getTempFileNames(
			groupId, getUserId(), folderName);
	}

	/**
	 * Locks the folder. This method is primarily used by WebDAV.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Lock lockFolder(long repositoryId, long folderId)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.lockFolder(folderId);
	}

	/**
	 * Locks the folder. This method is primarily used by WebDAV.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @param  owner the owner string for the checkout (optionally
	 *         <code>null</code>)
	 * @param  inheritable whether the lock must propagate to descendants
	 * @param  expirationTime the time in milliseconds before the lock expires.
	 *         If the value is <code>0</code>, the default expiration time will
	 *         be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Lock lockFolder(
			long repositoryId, long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.lockFolder(
			folderId, owner, inheritable, expirationTime);
	}

	/**
	 * Moves the file entry to the new folder.
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @param  newFolderId the primary key of the new folder
	 * @param  serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry moveFileEntry(
			long fileEntryId, long newFolderId, ServiceContext serviceContext)
		throws PortalException {

		Repository fromRepository =
			RepositoryProviderUtil.getFileEntryRepository(fileEntryId);

		Repository toRepository = getFolderRepository(
			newFolderId, serviceContext.getScopeGroupId());

		if (newFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			Folder toFolder = toRepository.getFolder(newFolderId);

			if (toFolder.isMountPoint()) {
				toRepository = getRepository(toFolder.getRepositoryId());
			}
		}

		if (fromRepository.getRepositoryId() ==
				toRepository.getRepositoryId()) {

			// Move file entries within repository

			return fromRepository.moveFileEntry(
				getUserId(), fileEntryId, newFolderId, serviceContext);
		}

		// Move file entries between repositories

		return moveFileEntry(
			fileEntryId, newFolderId, fromRepository, toRepository,
			serviceContext);
	}

	/**
	 * Moves the folder to the new parent folder with the primary key.
	 *
	 * @param  folderId the primary key of the folder
	 * @param  parentFolderId the primary key of the new parent folder
	 * @param  serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Folder moveFolder(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException {

		Repository fromRepository = RepositoryProviderUtil.getFolderRepository(
			folderId);

		Repository toRepository = getFolderRepository(
			parentFolderId, serviceContext.getScopeGroupId());

		if (parentFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			Folder toFolder = toRepository.getFolder(parentFolderId);

			if (toFolder.isMountPoint()) {
				toRepository = getRepository(toFolder.getRepositoryId());
			}
		}

		if (fromRepository.getRepositoryId() ==
				toRepository.getRepositoryId()) {

			// Move file entries within repository

			return fromRepository.moveFolder(
				getUserId(), folderId, parentFolderId, serviceContext);
		}

		// Move file entries between repositories

		return moveFolder(
			folderId, parentFolderId, fromRepository, toRepository,
			serviceContext);
	}

	/**
	 * Refreshes the lock for the file entry. This method is primarily used by
	 * WebDAV.
	 *
	 * @param  lockUuid the lock's UUID
	 * @param  companyId the primary key of the file entry's company
	 * @param  expirationTime the time in milliseconds before the lock expires.
	 *         If the value is <code>0</code>, the default expiration time will
	 *         be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Lock refreshFileEntryLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException {

		Lock lock = LockManagerUtil.getLockByUuidAndCompanyId(
			lockUuid, companyId);

		long fileEntryId = GetterUtil.getLong(lock.getKey());

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		return repository.refreshFileEntryLock(
			lockUuid, companyId, expirationTime);
	}

	/**
	 * Refreshes the lock for the folder. This method is primarily used by
	 * WebDAV.
	 *
	 * @param  lockUuid the lock's UUID
	 * @param  companyId the primary key of the file entry's company
	 * @param  expirationTime the time in milliseconds before the lock expires.
	 *         If the value is <code>0</code>, the default expiration time will
	 *         be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Lock refreshFolderLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException {

		Lock lock = LockManagerUtil.getLockByUuidAndCompanyId(
			lockUuid, companyId);

		long folderId = GetterUtil.getLong(lock.getKey());

		Repository repository = RepositoryProviderUtil.getFolderRepository(
			folderId);

		return repository.refreshFolderLock(
			lockUuid, companyId, expirationTime);
	}

	/**
	 * Reverts the file entry to a previous version. A new version will be
	 * created based on the previous version and metadata.
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @param  version the version to revert back to
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void revertFileEntry(
			long fileEntryId, String version, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		repository.revertFileEntry(
			getUserId(), fileEntryId, version, serviceContext);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		_dlAppHelperLocalService.updateFileEntry(
			getUserId(), fileEntry, null, fileEntry.getFileVersion(),
			serviceContext);
	}

	@Override
	public Hits search(
			long repositoryId, long creatorUserId, int status, int start,
			int end)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.search(creatorUserId, status, start, end);
	}

	@Override
	public Hits search(
			long repositoryId, long creatorUserId, long folderId,
			String[] mimeTypes, int status, int start, int end)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.search(
			creatorUserId, folderId, mimeTypes, status, start, end);
	}

	@Override
	public Hits search(long repositoryId, SearchContext searchContext)
		throws SearchException {

		try {
			Repository repository = getRepository(repositoryId);

			PermissionChecker permissionChecker = getPermissionChecker();

			searchContext.setCompanyId(permissionChecker.getCompanyId());
			searchContext.setUserId(permissionChecker.getUserId());

			return repository.search(searchContext);
		}
		catch (Exception exception) {
			throw new SearchException(exception);
		}
	}

	@Override
	public Hits search(
			long repositoryId, SearchContext searchContext, Query query)
		throws SearchException {

		try {
			Repository repository = getRepository(repositoryId);

			PermissionChecker permissionChecker = getPermissionChecker();

			searchContext.setCompanyId(permissionChecker.getCompanyId());
			searchContext.setUserId(permissionChecker.getUserId());

			return repository.search(searchContext, query);
		}
		catch (Exception exception) {
			throw new SearchException(exception);
		}
	}

	@Override
	public void subscribeFileEntry(long groupId, long fileEntryId)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			DLPortletResourcePermissionUtil.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		dlAppLocalService.subscribeFileEntry(getUserId(), groupId, fileEntryId);
	}

	/**
	 * Subscribe the user to changes in documents of the file entry type. This
	 * method is only supported by the Liferay repository.
	 *
	 * @param  groupId the primary key of the file entry type's group
	 * @param  fileEntryTypeId the primary key of the file entry type
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void subscribeFileEntryType(long groupId, long fileEntryTypeId)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			DLPortletResourcePermissionUtil.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		dlAppLocalService.subscribeFileEntryType(
			getUserId(), groupId, fileEntryTypeId);
	}

	/**
	 * Subscribe the user to document changes in the folder. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param  groupId the primary key of the folder's group
	 * @param  folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void subscribeFolder(long groupId, long folderId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.SUBSCRIBE);

		dlAppLocalService.subscribeFolder(getUserId(), groupId, folderId);
	}

	/**
	 * Unlocks the folder. This method is primarily used by WebDAV.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  folderId the primary key of the folder
	 * @param  lockUuid the lock's UUID
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unlockFolder(long repositoryId, long folderId, String lockUuid)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		repository.unlockFolder(folderId, lockUuid);
	}

	/**
	 * Unlocks the folder. This method is primarily used by WebDAV.
	 *
	 * @param  repositoryId the primary key of the repository
	 * @param  parentFolderId the primary key of the parent folder
	 * @param  name the folder's name
	 * @param  lockUuid the lock's UUID
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unlockFolder(
			long repositoryId, long parentFolderId, String name,
			String lockUuid)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		repository.unlockFolder(parentFolderId, name, lockUuid);
	}

	@Override
	public void unsubscribeFileEntry(long groupId, long fileEntryId)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			DLPortletResourcePermissionUtil.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		dlAppLocalService.unsubscribeFileEntry(
			getUserId(), groupId, fileEntryId);
	}

	/**
	 * Unsubscribe the user from changes in documents of the file entry type.
	 * This method is only supported by the Liferay repository.
	 *
	 * @param  groupId the primary key of the file entry type's group
	 * @param  fileEntryTypeId the primary key of the file entry type
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unsubscribeFileEntryType(long groupId, long fileEntryTypeId)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			DLPortletResourcePermissionUtil.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		dlAppLocalService.unsubscribeFileEntryType(
			getUserId(), groupId, fileEntryTypeId);
	}

	/**
	 * Unsubscribe the user from document changes in the folder. This method is
	 * only supported by the Liferay repository.
	 *
	 * @param  groupId the primary key of the folder's group
	 * @param  folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unsubscribeFolder(long groupId, long folderId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.SUBSCRIBE);

		dlAppLocalService.unsubscribeFolder(getUserId(), groupId, folderId);
	}

	/**
	 * Updates a file entry and associated metadata based on a byte array
	 * object. If the file data is <code>null</code>, then only the associated
	 * metadata (i.e., <code>title</code>, <code>description</code>, and
	 * parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @param  sourceFileName the original file's name (optionally
	 *         <code>null</code>)
	 * @param  mimeType the file's MIME type (optionally <code>null</code>)
	 * @param  title the new name to be assigned to the file (optionally <code>
	 *         <code>null</code></code>)
	 * @param  description the file's new description
	 * @param  changeLog the file's version change log (optionally
	 *         <code>null</code>)
	 * @param  dlVersionNumberIncrease the kind of version number increase to
	 *         apply for these changes.
	 * @param  bytes the file's data (optionally <code>null</code>)
	 * @param  displayDate the file's display date (optionally <code>null
	 *         </code>)
	 * @param  expirationDate the file's expiration date (optionally <code>null
	 *         </code>)
	 * @param  reviewDate the file's review date (optionally <code>null</code>)
	 * @param  serviceContext the service context to be applied. Can set the
	 *         asset category IDs, asset tag names, and expando bridge
	 *         attributes for the file entry. In a Liferay repository, it may
	 *         include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 *         type </li> <li> fieldsMap - mapping for fields associated with a
	 *         custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry updateFileEntry(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease, byte[] bytes,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		File file = null;

		try {
			if (ArrayUtil.isNotEmpty(bytes)) {
				file = FileUtil.createTempFile(bytes);
			}

			return updateFileEntry(
				fileEntryId, sourceFileName, mimeType, title, urlTitle,
				description, changeLog, dlVersionNumberIncrease, file,
				displayDate, expirationDate, reviewDate, serviceContext);
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to write temporary file", ioException);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	/**
	 * Updates a file entry and associated metadata based on a {@link File}
	 * object. If the file data is <code>null</code>, then only the associated
	 * metadata (i.e., <code>title</code>, <code>description</code>, and
	 * parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @param  sourceFileName the original file's name (optionally
	 *         <code>null</code>)
	 * @param  mimeType the file's MIME type (optionally <code>null</code>)
	 * @param  title the new name to be assigned to the file (optionally <code>
	 *         <code>null</code></code>)
	 * @param  description the file's new description
	 * @param  changeLog the file's version change log (optionally
	 *         <code>null</code>)
	 * @param  dlVersionNumberIncrease the kind of version number increase to
	 *         apply for these changes.
	 * @param  file the file's data (optionally <code>null</code>)
	 * @param  displayDate the file's display date (optionally <code>null
	 *         </code>)
	 * @param  expirationDate the file's expiration date (optionally <code>null
	 *         </code>)
	 * @param  reviewDate the file's review date (optionally <code>null</code>)
	 * @param  serviceContext the service context to be applied. Can set the
	 *         asset category IDs, asset tag names, and expando bridge
	 *         attributes for the file entry. In a Liferay repository, it may
	 *         include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 *         type </li> <li> fieldsMap - mapping for fields associated with a
	 *         custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry updateFileEntry(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		if ((file == null) || !file.exists() || (file.length() == 0)) {
			return updateFileEntry(
				fileEntryId, sourceFileName, mimeType, title, urlTitle,
				description, changeLog, dlVersionNumberIncrease, null, 0,
				displayDate, expirationDate, reviewDate, serviceContext);
		}

		mimeType = DLAppUtil.getMimeType(sourceFileName, mimeType, title, file);

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		FileEntry fileEntry = repository.updateFileEntry(
			getUserId(), fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, file, displayDate,
			expirationDate, reviewDate, serviceContext);

		_dlAppHelperLocalService.updateFileEntry(
			getUserId(), fileEntry, null, fileEntry.getLatestFileVersion(),
			serviceContext);

		return fileEntry;
	}

	/**
	 * Updates a file entry and associated metadata based on an {@link
	 * InputStream} object. If the file data is <code>null</code>, then only the
	 * associated metadata (i.e., <code>title</code>, <code>description</code>,
	 * and parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @param  sourceFileName the original file's name (optionally
	 *         <code>null</code>)
	 * @param  mimeType the file's MIME type (optionally <code>null</code>)
	 * @param  title the new name to be assigned to the file (optionally <code>
	 *         <code>null</code></code>)
	 * @param  description the file's new description
	 * @param  changeLog the file's version change log (optionally
	 *         <code>null</code>)
	 * @param  dlVersionNumberIncrease the kind of version number increase to
	 *         apply for these changes.
	 * @param  inputStream the file's data (optionally <code>null</code>)
	 * @param  size the file's size (optionally <code>0</code>)
	 * @param  displayDate the file's display date (optionally <code>null
	 *         </code>)
	 * @param  expirationDate the file's expiration date (optionally <code>null
	 *         </code>)
	 * @param  reviewDate the file's review date (optionally <code>null</code>)
	 * @param  serviceContext the service context to be applied. Can set the
	 *         asset category IDs, asset tag names, and expando bridge
	 *         attributes for the file entry. In a Liferay repository, it may
	 *         include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 *         type </li> <li> fieldsMap - mapping for fields associated with a
	 *         custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileEntry updateFileEntry(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(mimeType) ||
			mimeType.equals(ContentTypes.APPLICATION_OCTET_STREAM)) {

			if (size == 0) {
				mimeType = MimeTypesUtil.getExtensionContentType(
					DLAppUtil.getExtension(title, sourceFileName));
			}
			else {
				File file = null;

				try {
					file = FileUtil.createTempFile(inputStream);

					return updateFileEntry(
						fileEntryId, sourceFileName, mimeType, title, urlTitle,
						description, changeLog, dlVersionNumberIncrease, file,
						displayDate, expirationDate, reviewDate,
						serviceContext);
				}
				catch (IOException ioException) {
					throw new SystemException(
						"Unable to write temporary file", ioException);
				}
				finally {
					FileUtil.delete(file);
				}
			}
		}

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		FileEntry fileEntry = repository.updateFileEntry(
			getUserId(), fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);

		_dlAppHelperLocalService.updateFileEntry(
			getUserId(), fileEntry, null, fileEntry.getLatestFileVersion(),
			serviceContext);

		return fileEntry;
	}

	@Override
	public FileEntry updateFileEntryAndCheckIn(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		if ((file == null) || !file.exists() || (file.length() == 0)) {
			return updateFileEntryAndCheckIn(
				fileEntryId, sourceFileName, mimeType, title, urlTitle,
				description, changeLog, dlVersionNumberIncrease, null, 0,
				displayDate, expirationDate, reviewDate, serviceContext);
		}

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		_withDLAppHelperDisabled(
			() -> repository.updateFileEntry(
				getUserId(), fileEntryId, sourceFileName, mimeType, title,
				urlTitle, description, changeLog, dlVersionNumberIncrease, file,
				displayDate, expirationDate, reviewDate, serviceContext));

		repository.checkInFileEntry(
			getUserId(), fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		_dlAppHelperLocalService.updateFileEntry(
			getUserId(), fileEntry, null, fileEntry.getFileVersion(),
			serviceContext);

		return fileEntry;
	}

	@Override
	public FileEntry updateFileEntryAndCheckIn(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		_withDLAppHelperDisabled(
			() -> repository.updateFileEntry(
				getUserId(), fileEntryId, sourceFileName, mimeType, title,
				urlTitle, description, changeLog, dlVersionNumberIncrease,
				inputStream, size, displayDate, expirationDate, reviewDate,
				serviceContext));

		repository.checkInFileEntry(
			getUserId(), fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		_dlAppHelperLocalService.updateFileEntry(
			getUserId(), fileEntry, null, fileEntry.getFileVersion(),
			serviceContext);

		return fileEntry;
	}

	/**
	 * Updates a file shortcut to the existing file entry. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param  fileShortcutId the primary key of the file shortcut
	 * @param  folderId the primary key of the file shortcut's parent folder
	 * @param  toFileEntryId the primary key of the file shortcut's file entry
	 * @param  serviceContext the service context to be applied. Can set the
	 *         asset category IDs, asset tag names, and expando bridge
	 *         attributes for the file entry.
	 * @return the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public FileShortcut updateFileShortcut(
			long fileShortcutId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository =
			RepositoryProviderUtil.getFileShortcutRepository(fileShortcutId);

		return repository.updateFileShortcut(
			getUserId(), fileShortcutId, folderId, toFileEntryId,
			serviceContext);
	}

	/**
	 * Updates the folder.
	 *
	 * @param  folderId the primary key of the folder
	 * @param  name the folder's new name
	 * @param  description the folder's new description
	 * @param  serviceContext the service context to be applied. In a Liferay
	 *         repository, it may include:  <ul> <li> defaultFileEntryTypeId -
	 *         the file entry type to default all Liferay file entries to </li>
	 *         <li> dlFileEntryTypesSearchContainerPrimaryKeys - a
	 *         comma-delimited list of file entry type primary keys allowed in
	 *         the given folder and all descendants </li> <li> restrictionType -
	 *         specifying restriction type of file entry types allowed </li>
	 *         <li> workflowDefinitionXYZ - the workflow definition name
	 *         specified per file entry type. The parameter name must be the
	 *         string <code>workflowDefinition</code> appended by the
	 *         <code>fileEntryTypeId</code> (optionally <code>0</code>).</li>
	 *         </ul>
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Folder updateFolder(
			long folderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getFolderRepository(
			folderId, serviceContext.getScopeGroupId());

		Folder folder = repository.updateFolder(
			folderId, name, description, serviceContext);

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_dlAppHelperLocalService.updateFolder(
				getUserId(), folder, serviceContext);
		}

		return folder;
	}

	/**
	 * Returns <code>true</code> if the file entry is checked out. This method
	 * is primarily used by WebDAV.
	 *
	 * @param  repositoryId the primary key for the repository
	 * @param  fileEntryId the primary key for the file entry
	 * @param  lockUuid the lock's UUID
	 * @return <code>true</code> if the file entry is checked out;
	 *         <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public boolean verifyFileEntryCheckOut(
			long repositoryId, long fileEntryId, String lockUuid)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.verifyFileEntryCheckOut(fileEntryId, lockUuid);
	}

	@Override
	public boolean verifyFileEntryLock(
			long repositoryId, long fileEntryId, String lockUuid)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.verifyFileEntryLock(fileEntryId, lockUuid);
	}

	/**
	 * Returns <code>true</code> if the inheritable lock exists. This method is
	 * primarily used by WebDAV.
	 *
	 * @param  repositoryId the primary key for the repository
	 * @param  folderId the primary key for the folder
	 * @param  lockUuid the lock's UUID
	 * @return <code>true</code> if the inheritable lock exists;
	 *         <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public boolean verifyInheritableLock(
			long repositoryId, long folderId, String lockUuid)
		throws PortalException {

		Repository repository = getRepository(repositoryId);

		return repository.verifyInheritableLock(folderId, lockUuid);
	}

	protected FileEntry copyFileEntry(
			Repository toRepository, FileEntry fileEntry, long targetFolderId,
			long fileEntryTypeId, long[] groupIds,
			ServiceContext serviceContext)
		throws PortalException {

		List<FileVersion> fileVersions = fileEntry.getFileVersions(
			WorkflowConstants.STATUS_ANY);

		FileVersion latestFileVersion = fileVersions.get(
			fileVersions.size() - 1);

		String sourceFileName = latestFileVersion.getFileName();

		DLValidatorUtil.validateFileSize(
			toRepository.getRepositoryId(), sourceFileName,
			latestFileVersion.getMimeType(), latestFileVersion.getSize());

		_populateServiceContext(
			serviceContext, DLFileEntryConstants.getClassName(),
			fileEntry.getFileEntryId(), fileEntryTypeId, groupIds,
			toRepository.getRepositoryId());

		FileEntry targetFileEntry = toRepository.addFileEntry(
			null, getUserId(), targetFolderId, sourceFileName,
			latestFileVersion.getMimeType(), latestFileVersion.getTitle(),
			latestFileVersion.getTitle(), latestFileVersion.getDescription(),
			StringPool.BLANK, latestFileVersion.getContentStream(false),
			latestFileVersion.getSize(), latestFileVersion.getDisplayDate(),
			latestFileVersion.getExpirationDate(),
			latestFileVersion.getReviewDate(), serviceContext);

		for (RatingsEntry ratingsEntry :
				_ratingsEntryLocalService.getEntries(
					DLFileEntryConstants.getClassName(),
					fileEntry.getFileEntryId())) {

			_ratingsEntryLocalService.updateEntry(
				ratingsEntry.getUserId(), DLFileEntryConstants.getClassName(),
				targetFileEntry.getFileEntryId(), ratingsEntry.getScore(),
				serviceContext);
		}

		for (int i = fileVersions.size() - 2; i >= 0; i--) {
			FileVersion fileVersion = fileVersions.get(i);

			sourceFileName = fileVersion.getFileName();

			FileVersion previousFileVersion = fileVersions.get(i + 1);

			try {
				targetFileEntry = toRepository.updateFileEntry(
					getUserId(), targetFileEntry.getFileEntryId(),
					sourceFileName, fileVersion.getMimeType(),
					fileVersion.getTitle(), fileVersion.getTitle(),
					fileVersion.getDescription(), StringPool.BLANK,
					DLVersionNumberIncrease.fromMajorVersion(
						DLAppUtil.isMajorVersion(
							previousFileVersion, fileVersion)),
					fileVersion.getContentStream(false), fileVersion.getSize(),
					fileVersion.getDisplayDate(),
					fileVersion.getExpirationDate(),
					fileVersion.getReviewDate(), serviceContext);

				FileVersion destinationFileVersion =
					targetFileEntry.getFileVersion();

				_dlAppHelperLocalService.updateFileEntry(
					getUserId(), targetFileEntry, null, destinationFileVersion,
					serviceContext);
			}
			catch (PortalException portalException) {
				toRepository.deleteFileEntry(targetFileEntry.getFileEntryId());

				throw portalException;
			}
		}

		_copyResourcePermissions(
			fileEntry.getCompanyId(), DLFileEntry.class.getName(),
			fileEntry.getRepositoryId(), fileEntry.getFileEntryId(),
			targetFileEntry.getRepositoryId(),
			targetFileEntry.getFileEntryId());

		return targetFileEntry;
	}

	protected FileEntry copyFileEntry(
			Repository toRepository, FileEntry fileEntry, long targetFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		return copyFileEntry(
			toRepository, fileEntry, targetFolderId,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			serviceContext);
	}

	protected Folder copyFolder(
			long sourceFolderId, long parentFolderId, Repository fromRepository,
			Repository toRepository, Map<Long, Long> fileEntryTypeIds,
			long[] groupIds, ServiceContext serviceContext)
		throws PortalException {

		Folder targetFolder = null;

		try {
			Folder sourceFolder = fromRepository.getFolder(sourceFolderId);

			targetFolder = toRepository.addFolder(
				null, getUserId(), parentFolderId, sourceFolder.getName(),
				sourceFolder.getDescription(), serviceContext);

			_dlAppHelperLocalService.addFolder(
				getUserId(), targetFolder, serviceContext);

			for (RatingsEntry ratingsEntry :
					_ratingsEntryLocalService.getEntries(
						DLFolderConstants.getClassName(),
						sourceFolder.getFolderId())) {

				_ratingsEntryLocalService.updateEntry(
					ratingsEntry.getUserId(), DLFolderConstants.getClassName(),
					targetFolder.getFolderId(), ratingsEntry.getScore(),
					serviceContext);
			}

			copyFolderDependencies(
				sourceFolder, targetFolder, fromRepository, toRepository,
				fileEntryTypeIds, groupIds, serviceContext);

			return targetFolder;
		}
		catch (PortalException portalException) {
			if (targetFolder != null) {
				toRepository.deleteFolder(targetFolder.getFolderId());
			}

			throw portalException;
		}
	}

	protected void copyFolder(
			Repository repository, Folder sourceFolder, Folder targetFolder,
			ServiceContext serviceContext)
		throws PortalException {

		Queue<Folder[]> folders = new LinkedList<>();
		List<FileEntry> fileEntries = new ArrayList<>();

		Folder curSourceFolder = sourceFolder;
		Folder curTargetFolder = targetFolder;

		while (true) {
			List<FileEntry> srcFileEntries = repository.getFileEntries(
				curSourceFolder.getFolderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			for (FileEntry srcFileEntry : srcFileEntries) {
				try {
					FileEntry fileEntry = repository.copyFileEntry(
						getUserId(), curTargetFolder.getGroupId(),
						srcFileEntry.getFileEntryId(),
						curTargetFolder.getFolderId(), serviceContext);

					fileEntries.add(fileEntry);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}

			List<Folder> srcSubfolders = repository.getFolders(
				curSourceFolder.getFolderId(), false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			for (Folder srcSubfolder : srcSubfolders) {
				Folder destSubfolder = repository.addFolder(
					null, getUserId(), curTargetFolder.getFolderId(),
					srcSubfolder.getName(), srcSubfolder.getDescription(),
					serviceContext);

				_dlAppHelperLocalService.addFolder(
					getUserId(), destSubfolder, serviceContext);

				folders.offer(new Folder[] {srcSubfolder, destSubfolder});
			}

			Folder[] next = folders.poll();

			if (next == null) {
				break;
			}

			curSourceFolder = next[0];
			curTargetFolder = next[1];
		}

		_copyResourcePermissions(
			sourceFolder.getCompanyId(), DLFolder.class.getName(),
			sourceFolder.getRepositoryId(), sourceFolder.getFolderId(),
			targetFolder.getRepositoryId(), targetFolder.getFolderId());

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				for (FileEntry fileEntry : fileEntries) {
					DLProcessorHelperUtil.trigger(fileEntry, null);
				}

				return null;
			});
	}

	protected void copyFolderDependencies(
			Folder sourceFolder, Folder targetFolder, Repository fromRepository,
			Repository toRepository, Map<Long, Long> fileEntryTypeIds,
			long[] groupIds, ServiceContext serviceContext)
		throws PortalException {

		List<RepositoryEntry> repositoryEntries =
			fromRepository.getFoldersAndFileEntriesAndFileShortcuts(
				sourceFolder.getFolderId(), WorkflowConstants.STATUS_ANY, true,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (RepositoryEntry repositoryEntry : repositoryEntries) {
			if (repositoryEntry instanceof FileEntry) {
				FileEntry fileEntry = (FileEntry)repositoryEntry;

				long[] assetCategoryIds = serviceContext.getAssetCategoryIds();
				String[] assetTagNames = serviceContext.getAssetTagNames();
				long fileEntryTypeId = ParamUtil.getLong(
					serviceContext, "fileEntryTypeId");

				copyFileEntry(
					toRepository, fileEntry, targetFolder.getFolderId(),
					fileEntryTypeIds.getOrDefault(
						fileEntry.getFileEntryId(),
						DLFileEntryTypeConstants.
							FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT),
					groupIds, serviceContext);

				serviceContext.setAssetCategoryIds(assetCategoryIds);
				serviceContext.setAssetTagNames(assetTagNames);
				serviceContext.setAttribute("fileEntryTypeId", fileEntryTypeId);
			}
			else if (repositoryEntry instanceof FileShortcut) {
				if (targetFolder.isSupportsShortcuts()) {
					FileShortcut fileShortcut = (FileShortcut)repositoryEntry;

					toRepository.addFileShortcut(
						null, getUserId(), targetFolder.getFolderId(),
						fileShortcut.getToFileEntryId(), serviceContext);
				}
			}
			else if (repositoryEntry instanceof Folder) {
				Folder currentFolder = (Folder)repositoryEntry;

				Folder newFolder = toRepository.addFolder(
					null, getUserId(), targetFolder.getFolderId(),
					currentFolder.getName(), currentFolder.getDescription(),
					serviceContext);

				_dlAppHelperLocalService.addFolder(
					getUserId(), newFolder, serviceContext);

				for (RatingsEntry ratingsEntry :
						_ratingsEntryLocalService.getEntries(
							DLFolderConstants.getClassName(),
							currentFolder.getFolderId())) {

					_ratingsEntryLocalService.updateEntry(
						ratingsEntry.getUserId(),
						DLFolderConstants.getClassName(),
						newFolder.getFolderId(), ratingsEntry.getScore(),
						serviceContext);
				}

				copyFolderDependencies(
					currentFolder, newFolder, fromRepository, toRepository,
					fileEntryTypeIds, groupIds, serviceContext);
			}
		}
	}

	protected void deleteFileEntry(
			long oldFileEntryId, long newFileEntryId, Repository fromRepository,
			Repository toRepository)
		throws PortalException {

		try {
			_dlAppHelperLocalService.deleteFileEntry(
				fromRepository.getFileEntry(oldFileEntryId));

			fromRepository.deleteFileEntry(oldFileEntryId);
		}
		catch (PortalException portalException) {
			FileEntry fileEntry = toRepository.getFileEntry(newFileEntryId);

			toRepository.deleteFileEntry(newFileEntryId);

			_dlAppHelperLocalService.deleteFileEntry(fileEntry);

			throw portalException;
		}
	}

	protected FileEntry fetchFileEntryByUuidAndRepositoryId(
			String uuid, long repositoryId)
		throws PortalException {

		try {
			Repository repository = getRepository(repositoryId);

			return repository.getFileEntryByUuid(uuid);
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileEntryException);
			}

			return null;
		}
		catch (RepositoryException repositoryException) {
			throw new NoSuchFileEntryException(repositoryException);
		}
	}

	protected Repository getFolderRepository(long folderId, long groupId)
		throws PortalException {

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return getRepository(groupId);
		}

		return RepositoryProviderUtil.getFolderRepository(folderId);
	}

	protected Repository getRepository(long repositoryId)
		throws PortalException {

		try {
			return RepositoryProviderUtil.getRepository(repositoryId);
		}
		catch (InvalidRepositoryIdException invalidRepositoryIdException) {
			throw new NoSuchGroupException(
				StringBundler.concat(
					"No Group exists with the key {repositoryId=", repositoryId,
					"}"),
				invalidRepositoryIdException);
		}
	}

	protected FileEntry moveFileEntry(
			long fileEntryId, long newFolderId, Repository fromRepository,
			Repository toRepository, ServiceContext serviceContext)
		throws PortalException {

		FileEntry sourceFileEntry = fromRepository.getFileEntry(fileEntryId);

		if (sourceFileEntry.isCheckedOut()) {
			throw new FileEntryLockException.MustBeUnlocked();
		}

		FileEntry destinationFileEntry = copyFileEntry(
			toRepository, sourceFileEntry, newFolderId, serviceContext);

		deleteFileEntry(
			fileEntryId, destinationFileEntry.getFileEntryId(), fromRepository,
			toRepository);

		return destinationFileEntry;
	}

	protected Folder moveFolder(
			long folderId, long parentFolderId, Repository fromRepository,
			Repository toRepository, ServiceContext serviceContext)
		throws PortalException {

		Folder newFolder = copyFolder(
			folderId, parentFolderId, fromRepository, toRepository,
			new HashMap<>(), null, serviceContext);

		fromRepository.deleteFolder(folderId);

		return newFolder;
	}

	private void _copyResourcePermissions(
			long companyId, String className, long sourceRepositoryId,
			long sourceResourcePrimKey, long targetRepositoryId,
			long targetResourcePrimKey)
		throws PortalException {

		if (RepositoryUtil.isExternalRepository(sourceRepositoryId) ||
			RepositoryUtil.isExternalRepository(targetRepositoryId)) {

			return;
		}

		for (int scope : ResourceConstants.SCOPES) {
			_resourcePermissionLocalService.deleteResourcePermissions(
				companyId, className, scope, targetResourcePrimKey);
		}

		_resourcePermissionLocalService.copyModelResourcePermissions(
			companyId, className, sourceResourcePrimKey, targetResourcePrimKey);
	}

	private long[] _getAssetCategoryIds(
		String className, long classPK, long[] groupIds, long repositoryId) {

		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			className, classPK);

		if (ArrayUtil.isEmpty(groupIds)) {
			return assetCategoryIds;
		}

		Set<Long> allowedAssetCategoryIds = new HashSet<>();

		for (long assetCategoryId : assetCategoryIds) {
			if (_isAssetCategoryIdAllowed(
					assetCategoryId, repositoryId, groupIds)) {

				allowedAssetCategoryIds.add(assetCategoryId);
			}
		}

		return ArrayUtil.toLongArray(allowedAssetCategoryIds);
	}

	private String[] _getAssetTagNames(
		String className, long classPK, long[] groupIds) {

		String[] assetTagNames = _assetTagLocalService.getTagNames(
			className, classPK);

		if (ArrayUtil.isEmpty(groupIds)) {
			return assetTagNames;
		}

		Set<String> allowedAssetTagNames = new HashSet<>();

		for (String assetTagName : assetTagNames) {
			if (_isAssetTagNameAllowed(groupIds, assetTagName)) {
				allowedAssetTagNames.add(assetTagName);
			}
		}

		return ArrayUtil.toStringArray(allowedAssetTagNames);
	}

	private boolean _isAssetCategoryIdAllowed(
		long categoryId, long groupId, long[] groupsIds) {

		AssetCategory assetCategory = _assetCategoryLocalService.fetchCategory(
			categoryId);

		if ((assetCategory == null) ||
			!ArrayUtil.contains(groupsIds, assetCategory.getGroupId())) {

			return false;
		}

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				assetCategory.getVocabularyId());

		if ((assetVocabulary.getVisibilityType() ==
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL) &&
			(assetVocabulary.getGroupId() != groupId)) {

			return false;
		}

		return true;
	}

	private boolean _isAssetTagNameAllowed(long[] groupIds, String tagName) {
		for (Long groupId : groupIds) {
			if (_assetTagLocalService.hasTag(groupId, tagName)) {
				return true;
			}
		}

		return false;
	}

	private void _populateServiceContext(
		ServiceContext serviceContext, String className, long classPK,
		long fileEntryTypeId, long[] groupIds, long repositoryId) {

		serviceContext.setAssetCategoryIds(
			_getAssetCategoryIds(className, classPK, groupIds, repositoryId));
		serviceContext.setAssetTagNames(
			_getAssetTagNames(className, classPK, groupIds));

		if (!Objects.equals(
				fileEntryTypeId,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT)) {

			serviceContext.setAttribute("fileEntryTypeId", fileEntryTypeId);
		}
	}

	private void _validateFolders(
			long repositoryId, long sourceFolderId,
			long destinationParentFolderId)
		throws PortalException {

		if (sourceFolderId == destinationParentFolderId) {
			throw new InvalidFolderException(
				InvalidFolderException.CANNOT_COPY_INTO_ITSELF,
				destinationParentFolderId);
		}

		Repository repository = getRepository(repositoryId);

		Folder folder = repository.getFolder(sourceFolderId);

		if (folder.getParentFolderId() == destinationParentFolderId) {
			throw new DuplicateFolderNameException();
		}

		List<Long> subfolderIds = repository.getSubfolderIds(
			sourceFolderId, true);

		if (subfolderIds.contains(destinationParentFolderId)) {
			throw new InvalidFolderException(
				InvalidFolderException.CANNOT_COPY_INTO_CHILD_FOLDER,
				destinationParentFolderId);
		}
	}

	private void _withDLAppHelperDisabled(
			UnsafeRunnable<PortalException> unsafeRunnable)
		throws PortalException {

		boolean enabled = DLAppHelperThreadLocal.isEnabled();

		try {
			DLAppHelperThreadLocal.setEnabled(false);

			unsafeRunnable.run();
		}
		finally {
			DLAppHelperThreadLocal.setEnabled(enabled);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLAppServiceImpl.class);

	@BeanReference(type = AssetCategoryLocalService.class)
	private AssetCategoryLocalService _assetCategoryLocalService;

	@BeanReference(type = AssetTagLocalService.class)
	private AssetTagLocalService _assetTagLocalService;

	@BeanReference(type = AssetVocabularyLocalService.class)
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@BeanReference(type = DLAppHelperLocalService.class)
	private DLAppHelperLocalService _dlAppHelperLocalService;

	@BeanReference(type = RatingsEntryLocalService.class)
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@BeanReference(type = RepositoryPersistence.class)
	private RepositoryPersistence _repositoryPersistence;

	@BeanReference(type = ResourcePermissionLocalService.class)
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}