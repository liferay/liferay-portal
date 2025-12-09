/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.FileEntryExpirationDateException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.InvalidFileEntryTypeException;
import com.liferay.document.library.kernel.exception.InvalidFileVersionException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.expiration.ExpireAllVersions;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeTable;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFileVersionTable;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryMetadataPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileVersionPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.kernel.util.DL;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelModifiedDateComparator;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.DDMStructure;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureLinkManagerUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureManagerUtil;
import com.liferay.dynamic.data.mapping.kernel.StorageEngineManagerUtil;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.util.ExpandoBridgeUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
import com.liferay.portal.kernel.io.ByteArrayFileInputStream;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lock.InvalidLockException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WebDAVProps;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.repository.event.RepositoryEventTrigger;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WebDAVPropsLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.persistence.RepositoryPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.service.persistence.WebDAVPropsPersistence;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.trash.helper.TrashHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.EscapableLocalizableFunction;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupSubscriptionCheckSubscriptionSender;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileVersion;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.portlet.documentlibrary.DLGroupServiceSettings;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.base.DLFileEntryLocalServiceBaseImpl;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides the local service for accessing, adding, checking in/out, deleting,
 * locking/unlocking, moving, reverting, updating, and verifying document
 * library file entries.
 *
 * <p>
 * Due to legacy code, the names of some file entry properties are not
 * intuitive. Each file entry has both a name and title. The <code>name</code>
 * is a unique identifier for a given file and is generally numeric, whereas the
 * <code>title</code> is the actual name specified by the user (such as
 * &quot;Budget.xls&quot;).
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @author Harry Mark
 * @author Alexander Chow
 * @author Manuel de la Peña
 */
public class DLFileEntryLocalServiceImpl
	extends DLFileEntryLocalServiceBaseImpl {

	@Override
	public DLFileEntry addFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, long folderId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, long fileEntryTypeId,
			Map<String, DDMFormValues> ddmFormValuesMap, File file,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new FileNameException(
				StringBundler.concat(
					"Unable to add file entry with file name ", sourceFileName,
					" because title is null"));
		}

		// File entry

		User user = _userPersistence.findByPrimaryKey(userId);

		folderId = DLFolderLocalServiceImpl.getFolderId(
			_dlFolderPersistence, user.getCompanyId(), folderId);

		String name = String.valueOf(
			counterLocalService.increment(DLFileEntry.class.getName()));

		String fileName = null;

		String extension = FileUtil.getExtension(sourceFileName);

		if (Validator.isNotNull(sourceFileName)) {
			fileName = DLUtil.getSanitizedFileName(
				FileUtil.stripExtension(sourceFileName), extension);
		}
		else {
			fileName = DLValidatorUtil.fixName(
				DLUtil.getSanitizedFileName(title, extension));
		}

		String inputStreamExtension = null;

		if (inputStream instanceof ByteArrayFileInputStream) {
			ByteArrayFileInputStream byteArrayFileInputStream =
				(ByteArrayFileInputStream)inputStream;

			File byteArrayFileInputStreamFile =
				byteArrayFileInputStream.getFile();

			inputStreamExtension = FileUtil.getExtension(
				byteArrayFileInputStreamFile.getName());
		}

		if (fileEntryTypeId == -1) {
			fileEntryTypeId =
				_dlFileEntryTypeLocalService.getDefaultFileEntryTypeId(
					folderId);
		}

		_validateFileEntryTypeId(
			PortalUtil.getCurrentAndAncestorSiteGroupIds(groupId), folderId,
			fileEntryTypeId);

		_validateFile(
			user.getCompanyId(), groupId, folderId, 0, fileEntryTypeId,
			fileName, extension, inputStream, inputStreamExtension, mimeType,
			title, displayDate, expirationDate);

		long fileEntryId = counterLocalService.increment();

		_validateExternalReferenceCode(externalReferenceCode, groupId);

		DLFileEntry dlFileEntry = dlFileEntryPersistence.create(fileEntryId);

		dlFileEntry.setUuid(serviceContext.getUuid());
		dlFileEntry.setExternalReferenceCode(externalReferenceCode);
		dlFileEntry.setGroupId(groupId);
		dlFileEntry.setCompanyId(user.getCompanyId());
		dlFileEntry.setUserId(user.getUserId());
		dlFileEntry.setUserName(user.getFullName());

		DLFolder repositoryDLFolder = null;

		if (repositoryId != groupId) {
			Repository repository = _repositoryPersistence.findByPrimaryKey(
				repositoryId);

			repositoryDLFolder = _dlFolderPersistence.findByPrimaryKey(
				repository.getDlFolderId());
		}

		long classNameId = 0;
		long classPK = 0;

		if ((repositoryDLFolder != null) && repositoryDLFolder.isHidden()) {
			classNameId = _classNameLocalService.getClassNameId(
				(String)serviceContext.getAttribute("className"));
			classPK = ParamUtil.getLong(serviceContext, "classPK");
		}

		dlFileEntry.setClassNameId(classNameId);
		dlFileEntry.setClassPK(classPK);
		dlFileEntry.setRepositoryId(repositoryId);
		dlFileEntry.setFolderId(folderId);
		dlFileEntry.setTreePath(dlFileEntry.buildTreePath());
		dlFileEntry.setName(name);
		dlFileEntry.setFileName(fileName);
		dlFileEntry.setExtension(extension);
		dlFileEntry.setMimeType(mimeType);
		dlFileEntry.setTitle(title);
		dlFileEntry.setDescription(description);
		dlFileEntry.setFileEntryTypeId(fileEntryTypeId);
		dlFileEntry.setVersion(DLFileEntryConstants.VERSION_DEFAULT);
		dlFileEntry.setSize(size);
		dlFileEntry.setDisplayDate(displayDate);
		dlFileEntry.setExpirationDate(expirationDate);
		dlFileEntry.setReviewDate(reviewDate);

		dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);

		// Resources

		_addFileEntryResources(dlFileEntry, serviceContext);

		// File version

		DLFileVersion dlFileVersion = _addFileVersion(
			user, dlFileEntry, fileName, extension, mimeType, title,
			description, changeLog, StringPool.BLANK, fileEntryTypeId,
			ddmFormValuesMap, DLFileEntryConstants.VERSION_DEFAULT, size,
			displayDate, expirationDate, reviewDate,
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		// Folder

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_dlFolderLocalService.updateLastPostDate(
				dlFileEntry.getFolderId(), dlFileEntry.getModifiedDate());
		}

		// File

		DLStoreRequest dlStoreRequest = DLStoreRequest.builder(
			user.getCompanyId(), dlFileEntry.getDataRepositoryId(), name
		).className(
			dlFileEntry.getModelClassName()
		).classPK(
			dlFileEntry.getFileEntryId()
		).size(
			dlFileEntry.getSize()
		).sourceFileName(
			dlFileEntry.getFileName()
		).versionLabel(
			dlFileVersion.getStoreFileName()
		).build();

		if (file != null) {
			DLStoreUtil.addFile(dlStoreRequest, file);
		}
		else {
			DLStoreUtil.addFile(dlStoreRequest, inputStream);
		}

		return dlFileEntry;
	}

	@Override
	public DLFileVersion cancelCheckOut(long userId, long fileEntryId)
		throws PortalException {

		if (!isFileEntryCheckedOut(fileEntryId)) {
			return null;
		}

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			fileEntryId);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlFileEntry.getCtCollectionId())) {

			DLFileVersion dlFileVersion =
				_dlFileVersionLocalService.getLatestFileVersion(
					fileEntryId, false);

			_removeFileVersion(dlFileEntry, dlFileVersion);

			return dlFileVersion;
		}
	}

	@Override
	public void checkFileEntries(long companyId, long checkInterval)
		throws PortalException {

		Date date = new Date();

		_dates.compute(
			companyId,
			(key, value) -> {
				Date checkDate = new Date(
					date.getTime() - (checkInterval * Time.MINUTE));

				if ((value != null) && value.before(checkDate)) {
					return value;
				}

				return checkDate;
			});

		long userId = _getActiveCompanyAdminUserId(companyId);

		_checkFileEntriesByDisplayDate(companyId, date, userId);

		_checkFileVersionsByExpirationDate(companyId, date, userId);

		_checkFileEntriesByReviewDate(companyId, date);

		_dates.put(companyId, date);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId,
			DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
			ServiceContext serviceContext)
		throws PortalException {

		if (!isFileEntryCheckedOut(fileEntryId)) {
			return;
		}

		User user = _userPersistence.findByPrimaryKey(userId);

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			fileEntryId);

		boolean webDAVCheckInMode = GetterUtil.getBoolean(
			serviceContext.getAttribute(DL.WEBDAV_CHECK_IN_MODE));

		boolean manualCheckInRequired = dlFileEntry.isManualCheckInRequired();

		if (!webDAVCheckInMode && manualCheckInRequired) {
			dlFileEntry.setManualCheckInRequired(false);

			dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);
		}

		DLFileVersion lastDLFileVersion =
			_dlFileVersionLocalService.getFileVersion(
				dlFileEntry.getFileEntryId(), dlFileEntry.getVersion());
		DLFileVersion latestDLFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(fileEntryId, false);

		DLVersionNumberIncrease computedDLVersionNumberIncrease =
			_computeDLVersionNumberIncrease(
				dlVersionNumberIncrease, lastDLFileVersion, latestDLFileVersion,
				serviceContext.getWorkflowAction());

		if (computedDLVersionNumberIncrease == DLVersionNumberIncrease.NONE) {
			_overwritePreviousFileVersion(
				user, dlFileEntry, latestDLFileVersion, lastDLFileVersion,
				serviceContext);

			unlockFileEntry(fileEntryId);

			return;
		}

		// File version

		String oldVersion = latestDLFileVersion.getVersion();
		String oldStoreFileName = latestDLFileVersion.getStoreFileName();

		latestDLFileVersion = _dlFileVersionPersistence.fetchByPrimaryKey(
			latestDLFileVersion.getFileVersionId());

		latestDLFileVersion.setChangeLog(changeLog);
		latestDLFileVersion.setVersion(
			_getNextVersion(dlFileEntry, computedDLVersionNumberIncrease));
		latestDLFileVersion.setStoreUUID(String.valueOf(UUID.randomUUID()));

		latestDLFileVersion = _dlFileVersionPersistence.update(
			latestDLFileVersion);

		// Folder

		if (dlFileEntry.getFolderId() !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			_dlFolderLocalService.updateLastPostDate(
				dlFileEntry.getFolderId(),
				latestDLFileVersion.getModifiedDate());
		}

		// File

		try {
			DLStoreUtil.copyFileVersion(
				user.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), oldStoreFileName,
				latestDLFileVersion.getStoreFileName());

			_registerPWCDeletionCallback(dlFileEntry, oldStoreFileName);
		}
		catch (NoSuchFileException noSuchFileException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Using version of the file as file name for file version " +
						latestDLFileVersion,
					noSuchFileException);
			}

			DLStoreUtil.copyFileVersion(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), oldVersion,
				latestDLFileVersion.getStoreFileName());

			_registerPWCDeletionCallback(dlFileEntry, oldVersion);
		}

		unlockFileEntry(fileEntryId);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNotNull(lockUuid)) {
			Lock lock = LockManagerUtil.fetchLock(
				DLFileEntry.class.getName(), fileEntryId);

			if ((lock != null) && !Objects.equals(lock.getUuid(), lockUuid)) {
				throw new InvalidLockException("UUIDs do not match");
			}
		}

		checkInFileEntry(
			userId, fileEntryId, DLVersionNumberIncrease.MINOR,
			StringPool.BLANK, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, long fileEntryTypeId,
			ServiceContext serviceContext)
		throws PortalException {

		return checkOutFileEntry(
			userId, fileEntryId, fileEntryTypeId, StringPool.BLANK,
			DLFileEntryImpl.LOCK_EXPIRATION_TIME, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, long fileEntryTypeId, String owner,
			long expirationTime, ServiceContext serviceContext)
		throws PortalException {

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(fileEntryId, false);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlFileVersion.getCtCollectionId())) {

			DLFileEntry dlFileEntry = _checkOutDLFileEntryModel(
				userId, fileEntryId, fileEntryTypeId, owner, expirationTime,
				serviceContext);

			DLFileVersion latestDLFileVersion =
				dlFileEntry.getLatestFileVersion(true);

			_copyFileVersion(dlFileEntry, dlFileVersion, latestDLFileVersion);

			return dlFileEntry;
		}
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, ServiceContext serviceContext)
		throws PortalException {

		return checkOutFileEntry(
			userId, fileEntryId, StringPool.BLANK,
			DLFileEntryImpl.LOCK_EXPIRATION_TIME, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, String owner, long expirationTime,
			ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			fileEntryId);

		return checkOutFileEntry(
			userId, fileEntryId, dlFileEntry.getFileEntryTypeId(), owner,
			expirationTime, serviceContext);
	}

	@Override
	public void convertExtraSettings(String[] keys) throws PortalException {
		int total = dlFileEntryFinder.countByExtraSettings();

		IntervalActionProcessor<Void> intervalActionProcessor =
			new IntervalActionProcessor<>(total);

		intervalActionProcessor.setPerformIntervalActionMethod(
			(start, end) -> {
				List<DLFileEntry> dlFileEntries =
					dlFileEntryFinder.findByExtraSettings(start, end);

				for (DLFileEntry dlFileEntry : dlFileEntries) {
					_convertExtraSettings(dlFileEntry, keys);
				}

				intervalActionProcessor.incrementStart(dlFileEntries.size());

				return null;
			});

		intervalActionProcessor.performIntervalActions();
	}

	@Override
	public DLFileEntry copyFileEntry(
			long userId, long groupId, long repositoryId,
			long sourceFileEntryId, long targetFolderId, String fileName,
			ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry sourceDLFileEntry = getFileEntry(sourceFileEntryId);

		String sourceFileName = "A";

		if (!Validator.isBlank(fileName)) {
			sourceFileName = fileName;
		}

		String sourceExtension = sourceDLFileEntry.getExtension();

		if (Validator.isNotNull(sourceExtension)) {
			sourceFileName = StringBundler.concat(
				sourceFileName, StringPool.PERIOD, sourceExtension);
		}

		String sourceTitle = sourceDLFileEntry.getTitle();

		if (!Validator.isBlank(fileName)) {
			sourceTitle = fileName;
		}

		DLFileVersion sourceDLFileVersion = sourceDLFileEntry.getFileVersion();

		InputStream sourceInputStream = _getInputStream(
			sourceDLFileEntry, sourceDLFileVersion);

		DLFileEntry targetDLFileEntry = addFileEntry(
			null, userId, groupId, repositoryId, targetFolderId, sourceFileName,
			sourceDLFileEntry.getMimeType(), sourceTitle, sourceTitle,
			sourceDLFileEntry.getDescription(), null,
			sourceDLFileEntry.getFileEntryTypeId(), null, null,
			sourceInputStream, sourceDLFileEntry.getSize(),
			sourceDLFileEntry.getDisplayDate(),
			sourceDLFileEntry.getExpirationDate(),
			sourceDLFileEntry.getReviewDate(), serviceContext);

		DLFileVersion targetDLFileVersion = targetDLFileEntry.getFileVersion();

		ExpandoBridgeUtil.copyExpandoBridgeAttributes(
			sourceDLFileVersion.getExpandoBridge(),
			targetDLFileVersion.getExpandoBridge());

		copyFileEntryMetadata(
			sourceDLFileVersion.getCompanyId(),
			sourceDLFileVersion.getFileEntryTypeId(), sourceFileEntryId,
			sourceDLFileVersion.getFileVersionId(),
			targetDLFileVersion.getFileVersionId(), serviceContext);

		return targetDLFileEntry;
	}

	@Override
	public void copyFileEntryMetadata(
			long companyId, long fileEntryTypeId, long fileEntryId,
			long sourceFileVersionId, long targetFileVersionId,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, DDMFormValues> ddmFormValuesMap = new HashMap<>();

		List<DDMStructure> ddmStructures;

		if (fileEntryTypeId > 0) {
			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.getFileEntryType(fileEntryTypeId);

			ddmStructures = dlFileEntryType.getDDMStructures();
		}
		else {
			ddmStructures = DDMStructureManagerUtil.getClassStructures(
				companyId,
				_classNameLocalService.getClassNameId(
					DLFileEntryMetadata.class));
		}

		_copyFileEntryMetadata(
			companyId, fileEntryId, sourceFileVersionId, targetFileVersionId,
			serviceContext, ddmFormValuesMap, ddmStructures);
	}

	@Override
	public void deleteFileEntries(long groupId, long folderId)
		throws PortalException {

		deleteFileEntries(groupId, folderId, true);
	}

	@Override
	public void deleteFileEntries(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		RepositoryEventTrigger repositoryEventTrigger =
			_getFolderRepositoryEventTrigger(groupId, folderId);

		ActionableDynamicQuery actionableDynamicQuery =
			dlFileEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property folderIdproperty = PropertyFactoryUtil.forName(
					"folderId");

				dynamicQuery.add(folderIdproperty.eq(folderId));
			});
		actionableDynamicQuery.setGroupId(groupId);
		actionableDynamicQuery.setPerformActionMethod(
			(DLFileEntry dlFileEntry) -> {
				if (includeTrashedEntries ||
					!_isInTrashExplicitly(dlFileEntry)) {

					repositoryEventTrigger.trigger(
						RepositoryEventType.Delete.class, FileEntry.class,
						new LiferayFileEntry(dlFileEntry));

					dlFileEntryLocalService.deleteFileEntry(dlFileEntry);
				}
			});

		actionableDynamicQuery.performActions();
	}

	@Override
	public void deleteFileEntries(
			long companyId, long classNameId, long classPK)
		throws PortalException {

		for (DLFileEntry dlFileEntry :
				dlFileEntryPersistence.findByC_C_C(
					companyId, classNameId, classPK)) {

			dlFileEntryLocalService.deleteFileEntry(dlFileEntry);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DLFileEntry deleteFileEntry(DLFileEntry dlFileEntry)
		throws PortalException {

		// Resources

		_resourceLocalService.deleteResource(
			dlFileEntry.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, dlFileEntry.getFileEntryId());

		// WebDAVProps

		WebDAVProps webDAVProps = _webDAVPropsPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(DLFileEntry.class),
			dlFileEntry.getFileEntryId());

		if (webDAVProps != null) {
			_webDAVPropsLocalService.deleteWebDAVProps(webDAVProps);
		}

		// File entry metadata

		_dlFileEntryMetadataLocalService.deleteFileEntryMetadata(
			dlFileEntry.getFileEntryId());

		// File versions

		List<DLFileVersion> dlFileVersions =
			_dlFileVersionPersistence.findByFileEntryId(
				dlFileEntry.getFileEntryId());

		for (DLFileVersion dlFileVersion : dlFileVersions) {
			_expandoRowLocalService.deleteRows(
				dlFileVersion.getFileVersionId());

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				dlFileEntry.getCompanyId(), dlFileEntry.getGroupId(),
				DLFileEntry.class.getName(), dlFileVersion.getFileVersionId());

			_dlFileVersionPersistence.remove(dlFileVersion);
		}

		// Expando

		_expandoRowLocalService.deleteRows(dlFileEntry.getFileEntryId());

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			DLFileEntry.class.getName(), dlFileEntry.getFileEntryId());

		// View count

		ViewCountManager viewCountManager = _viewCountManagerSnapshot.get();

		viewCountManager.deleteViewCount(
			dlFileEntry.getCompanyId(),
			_classNameLocalService.getClassNameId(DLFileEntry.class),
			dlFileEntry.getFileEntryId());

		// Lock

		unlockFileEntry(dlFileEntry.getFileEntryId());

		// File entry

		dlFileEntryPersistence.remove(dlFileEntry);

		// File

		try {
			DLStoreUtil.deleteFile(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return dlFileEntry;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public DLFileEntry deleteFileEntry(long fileEntryId)
		throws PortalException {

		DLFileEntry dlFileEntry = getFileEntry(fileEntryId);

		return dlFileEntryLocalService.deleteFileEntry(dlFileEntry);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public DLFileEntry deleteFileEntry(long userId, long fileEntryId)
		throws PortalException {

		if (!hasFileEntryLock(userId, fileEntryId)) {
			lockFileEntry(userId, fileEntryId);
		}

		try {
			return dlFileEntryLocalService.deleteFileEntry(fileEntryId);
		}
		finally {
			unlockFileEntry(fileEntryId);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public DLFileEntry deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		DLFileEntry dlFileEntry = getFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);

		return dlFileEntryLocalService.deleteFileEntry(dlFileEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DLFileEntry deleteFileVersion(
			long userId, long fileEntryId, String version)
		throws PortalException {

		if (!_isValidFileVersionNumber(version)) {
			throw new InvalidFileVersionException(
				StringBundler.concat(
					"Unable to delete version for file entry ", fileEntryId,
					" because version number ", version, " is invalid"));
		}

		if (version.equals(DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION)) {
			throw new InvalidFileVersionException(
				StringBundler.concat(
					"Unable to delete a private working copy file version ",
					version, " for file entry ", fileEntryId));
		}

		if (!hasFileEntryLock(userId, fileEntryId)) {
			lockFileEntry(userId, fileEntryId);
		}

		boolean latestVersion = false;

		DLFileEntry dlFileEntry = null;

		try {
			DLFileVersion dlFileVersion = _dlFileVersionPersistence.findByF_V(
				fileEntryId, version);

			if (!dlFileVersion.isApproved() && !dlFileVersion.isExpired() &&
				!dlFileVersion.isScheduled()) {

				throw new InvalidFileVersionException(
					StringBundler.concat(
						"Unable to delete the unapproved file version ",
						version, " for file entry ", fileEntryId));
			}

			int fileVersionsCount = _dlFileVersionPersistence.countByF_S(
				fileEntryId, WorkflowConstants.STATUS_APPROVED);

			fileVersionsCount += _dlFileVersionPersistence.countByF_S(
				fileEntryId, WorkflowConstants.STATUS_SCHEDULED);

			int fileVersionsExpiredCount = _dlFileVersionPersistence.countByF_S(
				fileEntryId, WorkflowConstants.STATUS_EXPIRED);

			if ((fileVersionsCount <= 1) &&
				!((dlFileVersion.getStatus() ==
					WorkflowConstants.STATUS_EXPIRED) &&
				  ((fileVersionsCount == 1) ||
				   (fileVersionsExpiredCount > 1)))) {

				throw new InvalidFileVersionException(
					StringBundler.concat(
						"Unable to delete the only approved file version ",
						version, " for file entry ", fileEntryId));
			}

			_dlFileVersionPersistence.remove(dlFileVersion);

			_expandoRowLocalService.deleteRows(
				dlFileVersion.getFileVersionId());

			_dlFileEntryMetadataLocalService.deleteFileVersionFileEntryMetadata(
				dlFileVersion.getFileVersionId());

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				dlFileVersion.getCompanyId(), dlFileVersion.getGroupId(),
				DLFileEntryConstants.getClassName(),
				dlFileVersion.getFileVersionId());

			dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(fileEntryId);

			latestVersion = version.equals(dlFileEntry.getVersion());

			if (latestVersion) {
				DLFileVersion dlLatestFileVersion =
					_dlFileVersionLocalService.fetchLatestFileVersion(
						dlFileEntry.getFileEntryId(), true);

				if (dlLatestFileVersion != null) {
					long fileEntryTypeId = _getValidFileEntryTypeId(
						dlLatestFileVersion.getFileEntryTypeId(), dlFileEntry);

					dlLatestFileVersion.setModifiedDate(new Date());
					dlLatestFileVersion.setFileEntryTypeId(fileEntryTypeId);
					dlLatestFileVersion.setStatusDate(new Date());

					dlLatestFileVersion = _dlFileVersionPersistence.update(
						dlLatestFileVersion);

					dlFileEntry.setModifiedDate(new Date());
					dlFileEntry.setFileName(dlLatestFileVersion.getFileName());
					dlFileEntry.setExtension(
						dlLatestFileVersion.getExtension());
					dlFileEntry.setMimeType(dlLatestFileVersion.getMimeType());
					dlFileEntry.setTitle(dlLatestFileVersion.getTitle());
					dlFileEntry.setDescription(
						dlLatestFileVersion.getDescription());
					dlFileEntry.setExtraSettings(
						dlLatestFileVersion.getExtraSettings());
					dlFileEntry.setFileEntryTypeId(fileEntryTypeId);
					dlFileEntry.setVersion(dlLatestFileVersion.getVersion());
					dlFileEntry.setSize(dlLatestFileVersion.getSize());
					dlFileEntry.setDisplayDate(
						dlLatestFileVersion.getDisplayDate());
					dlFileEntry.setExpirationDate(
						dlLatestFileVersion.getExpirationDate());
					dlFileEntry.setReviewDate(
						dlLatestFileVersion.getReviewDate());

					dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);
				}
			}

			_deleteFile(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), dlFileVersion.getStoreFileName());
		}
		finally {
			unlockFileEntry(fileEntryId);
		}

		if (latestVersion) {
			return dlFileEntry;
		}

		return null;
	}

	@Override
	public void deleteRepositoryFileEntries(long repositoryId)
		throws PortalException {

		RepositoryEventTrigger repositoryEventTrigger =
			RepositoryUtil.getRepositoryEventTrigger(repositoryId);

		int total = dlFileEntryPersistence.countByRepositoryId(repositoryId);

		IntervalActionProcessor<Void> intervalActionProcessor =
			new IntervalActionProcessor<>(total);

		intervalActionProcessor.setPerformIntervalActionMethod(
			(start, end) -> {
				List<DLFileEntry> dlFileEntries =
					dlFileEntryPersistence.findByRepositoryId(
						repositoryId, start, end);

				for (DLFileEntry dlFileEntry : dlFileEntries) {
					repositoryEventTrigger.trigger(
						RepositoryEventType.Delete.class, FileEntry.class,
						new LiferayFileEntry(dlFileEntry));

					dlFileEntryLocalService.deleteFileEntry(dlFileEntry);
				}

				return null;
			});

		intervalActionProcessor.performIntervalActions();
	}

	@Override
	public void deleteRepositoryFileEntries(long repositoryId, long folderId)
		throws PortalException {

		deleteRepositoryFileEntries(repositoryId, folderId, true);
	}

	@Override
	public void deleteRepositoryFileEntries(
			long repositoryId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		RepositoryEventTrigger repositoryEventTrigger =
			RepositoryUtil.getRepositoryEventTrigger(repositoryId);

		int total = dlFileEntryPersistence.countByR_F(repositoryId, folderId);

		IntervalActionProcessor<Void> intervalActionProcessor =
			new IntervalActionProcessor<>(total);

		intervalActionProcessor.setPerformIntervalActionMethod(
			(start, end) -> {
				List<DLFileEntry> dlFileEntries =
					dlFileEntryPersistence.findByR_F(
						repositoryId, folderId, start, end);

				for (DLFileEntry dlFileEntry : dlFileEntries) {
					if (includeTrashedEntries ||
						!_isInTrashExplicitly(dlFileEntry)) {

						repositoryEventTrigger.trigger(
							RepositoryEventType.Delete.class, FileEntry.class,
							new LiferayFileEntry(dlFileEntry));

						dlFileEntryLocalService.deleteFileEntry(dlFileEntry);
					}
					else {
						intervalActionProcessor.incrementStart();
					}
				}

				return null;
			});

		intervalActionProcessor.performIntervalActions();
	}

	@Override
	public DLFileEntry fetchFileEntry(
		long groupId, long folderId, String title) {

		return dlFileEntryPersistence.fetchByG_F_T(groupId, folderId, title);
	}

	@Override
	public DLFileEntry fetchFileEntry(String uuid, long groupId) {
		return dlFileEntryPersistence.fetchByUUID_G(uuid, groupId);
	}

	@Override
	public DLFileEntry fetchFileEntryByAnyImageId(long imageId) {
		DLFileEntry fileEntry =
			dlFileEntryPersistence.fetchBySmallImageId_First(imageId, null);

		if (fileEntry != null) {
			return fileEntry;
		}

		fileEntry = dlFileEntryPersistence.fetchByLargeImageId_First(
			imageId, null);

		if (fileEntry != null) {
			return fileEntry;
		}

		fileEntry = dlFileEntryPersistence.fetchByCustom1ImageId_First(
			imageId, null);

		if (fileEntry != null) {
			return fileEntry;
		}

		fileEntry = dlFileEntryPersistence.fetchByCustom2ImageId_First(
			imageId, null);

		if (fileEntry != null) {
			return fileEntry;
		}

		return null;
	}

	@Override
	public DLFileEntry fetchFileEntryByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return dlFileEntryPersistence.fetchByERC_G(
			externalReferenceCode, groupId);
	}

	@Override
	public DLFileEntry fetchFileEntryByFileName(
		long groupId, long folderId, String fileName) {

		return dlFileEntryPersistence.fetchByG_F_FN(
			groupId, folderId, fileName);
	}

	@Override
	public DLFileEntry fetchFileEntryByName(
		long groupId, long folderId, String name) {

		return dlFileEntryPersistence.fetchByG_F_N(groupId, folderId, name);
	}

	@Override
	public void forEachFileEntry(
			long companyId, Consumer<DLFileEntry> consumer, long maximumSize,
			String[] mimeTypes)
		throws PortalException {

		_performDynamicQueryActions(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));

				Property groupIdProperty = PropertyFactoryUtil.forName(
					"groupId");
				Property repositoryIdProperty = PropertyFactoryUtil.forName(
					"repositoryId");

				dynamicQuery.add(
					groupIdProperty.eqProperty(repositoryIdProperty));

				Property mimeTypeProperty = PropertyFactoryUtil.forName(
					"mimeType");

				dynamicQuery.add(mimeTypeProperty.in(mimeTypes));

				if (maximumSize >= 0) {
					Property sizeProperty = PropertyFactoryUtil.forName("size");

					dynamicQuery.add(sizeProperty.le(maximumSize));
				}

				DynamicQuery dlFileVersionDynamicQuery =
					_dlFileVersionLocalService.dynamicQuery();

				dlFileVersionDynamicQuery.add(companyIdProperty.eq(companyId));
				dlFileVersionDynamicQuery.add(
					groupIdProperty.eqProperty(repositoryIdProperty));
				dlFileVersionDynamicQuery.add(mimeTypeProperty.in(mimeTypes));

				Property statusProperty = PropertyFactoryUtil.forName("status");

				dlFileVersionDynamicQuery.add(
					statusProperty.eq(WorkflowConstants.STATUS_IN_TRASH));

				dlFileVersionDynamicQuery.setProjection(
					ProjectionFactoryUtil.distinct(
						ProjectionFactoryUtil.property("fileEntryId")));

				Property fileEntryIdProperty = PropertyFactoryUtil.forName(
					"fileEntryId");

				dynamicQuery.add(
					fileEntryIdProperty.notIn(dlFileVersionDynamicQuery));
			},
			consumer);
	}

	@Override
	public void forEachFileEntry(
			long companyId, long classNameId, Consumer<DLFileEntry> consumer,
			long maximumSize, String[] mimeTypes)
		throws PortalException {

		_performDynamicQueryActions(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));

				if (classNameId > 0) {
					Property classNameIdProperty = PropertyFactoryUtil.forName(
						"classNameId");

					dynamicQuery.add(classNameIdProperty.eq(classNameId));
				}

				Property mimeTypeProperty = PropertyFactoryUtil.forName(
					"mimeType");

				dynamicQuery.add(mimeTypeProperty.in(mimeTypes));

				if (maximumSize >= 0) {
					Property sizeProperty = PropertyFactoryUtil.forName("size");

					dynamicQuery.add(sizeProperty.le(maximumSize));
				}
			},
			consumer);
	}

	@Override
	public List<DLFileEntry> getDDMStructureFileEntries(
		long groupId, long[] ddmStructureIds) {

		return dlFileEntryFinder.findByDDMStructureIds(
			groupId, ddmStructureIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<DLFileEntry> getDDMStructureFileEntries(
		long[] ddmStructureIds) {

		return dlFileEntryFinder.findByDDMStructureIds(
			ddmStructureIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<DLFileEntry> getExtraSettingsFileEntries(int start, int end) {
		return dlFileEntryFinder.findByExtraSettings(start, end);
	}

	@Override
	public int getExtraSettingsFileEntriesCount() {
		return dlFileEntryFinder.countByExtraSettings();
	}

	@Override
	public InputStream getFileAsStream(long fileEntryId, String version)
		throws PortalException {

		return getFileAsStream(fileEntryId, version, true, 1);
	}

	@Override
	public InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter)
		throws PortalException {

		return getFileAsStream(fileEntryId, version, incrementCounter, 1);
	}

	@Override
	public InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter,
			int increment)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			fileEntryId);

		if (incrementCounter) {
			dlFileEntryLocalService.incrementViewCounter(
				dlFileEntry, increment);
		}

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion(version);

		return _getInputStream(dlFileEntry, dlFileVersion);
	}

	@Override
	public List<DLFileEntry> getFileEntries(int start, int end) {
		return dlFileEntryPersistence.findAll(start, end);
	}

	@Override
	public List<DLFileEntry> getFileEntries(long groupId, long folderId) {
		return dlFileEntryPersistence.findByG_F(groupId, folderId);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(folderId);

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			status, false, start, end, orderByComparator);

		return dlFileEntryFinder.findByG_F(groupId, folderIds, queryDefinition);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return dlFileEntryPersistence.findByG_F(
			groupId, folderId, start, end, orderByComparator);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return dlFileEntryFinder.findByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return dlFileEntryFinder.findByG_U_F_M(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	@Override
	public List<DLFileEntry> getFileEntries(long folderId, String name) {
		return dlFileEntryPersistence.findByF_N(folderId, name);
	}

	@Override
	public List<DLFileEntry> getFileEntriesByClassNameIdAndTreePath(
		long classNameId, String treePath) {

		return dlFileEntryFinder.findByC_T(classNameId, treePath);
	}

	@Override
	public int getFileEntriesCount() {
		return dlFileEntryPersistence.countAll();
	}

	@Override
	public int getFileEntriesCount(long groupId, long folderId) {
		return dlFileEntryPersistence.countByG_F(groupId, folderId);
	}

	@Override
	public int getFileEntriesCount(long groupId, long folderId, int status) {
		return dlFileEntryFinder.countByG_F(
			groupId, ListUtil.fromArray(folderId),
			new QueryDefinition<>(status));
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return dlFileEntryFinder.countByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return dlFileEntryFinder.countByG_U_F_M(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	@Override
	public DLFileEntry getFileEntry(long fileEntryId) throws PortalException {
		return dlFileEntryPersistence.findByPrimaryKey(fileEntryId);
	}

	@Override
	public DLFileEntry getFileEntry(long groupId, long folderId, String title)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryPersistence.fetchByG_F_T(
			groupId, folderId, title);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		List<DLFileVersion> dlFileVersions =
			_dlFileVersionPersistence.findByG_F_T_V(
				groupId, folderId, title,
				DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION);

		long userId = PrincipalThreadLocal.getUserId();

		for (DLFileVersion dlFileVersion : dlFileVersions) {
			if (hasFileEntryLock(
					userId, dlFileVersion.getFileEntryId(),
					dlFileVersion.getFolderId())) {

				return dlFileVersion.getFileEntry();
			}
		}

		throw new NoSuchFileEntryException(
			StringBundler.concat(
				"No DLFileEntry exists with the key {groupId=", groupId,
				", folderId=", folderId, ", title=", title,
				StringPool.CLOSE_CURLY_BRACE));
	}

	@Override
	public DLFileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return dlFileEntryPersistence.findByERC_G(
			externalReferenceCode, groupId);
	}

	@Override
	public DLFileEntry getFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws PortalException {

		return dlFileEntryPersistence.findByG_F_FN(groupId, folderId, fileName);
	}

	@Override
	public DLFileEntry getFileEntryByName(
			long groupId, long folderId, String name)
		throws PortalException {

		return dlFileEntryPersistence.findByG_F_N(groupId, folderId, name);
	}

	@Override
	public DLFileEntry getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return dlFileEntryPersistence.findByUUID_G(uuid, groupId);
	}

	@Override
	public Map<Long, Long> getFileEntryTypeIds(
		long companyId, long[] groupIds, String treePath) {

		Map<Long, Long> fileEntryTypeIds = new HashMap<>();

		List<Object[]> results = dslQuery(
			DSLQueryFactoryUtil.select(
				DLFileEntryTable.INSTANCE.fileEntryId,
				DLFileEntryTable.INSTANCE.fileEntryTypeId
			).from(
				DLFileEntryTable.INSTANCE
			).innerJoinON(
				DLFileEntryTypeTable.INSTANCE,
				DLFileEntryTypeTable.INSTANCE.fileEntryTypeId.eq(
					DLFileEntryTable.INSTANCE.fileEntryTypeId
				).and(
					DLFileEntryTypeTable.INSTANCE.companyId.eq(companyId)
				).and(
					DLFileEntryTypeTable.INSTANCE.groupId.in(
						ArrayUtil.toLongArray(groupIds))
				).and(
					DLFileEntryTypeTable.INSTANCE.fileEntryTypeId.neq(
						DLFileEntryTypeConstants.
							FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT)
				)
			).where(
				DLFileEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					DLFileEntryTable.INSTANCE.fileEntryTypeId.neq(
						DLFileEntryTypeConstants.
							FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT)
				).and(
					DLFileEntryTable.INSTANCE.treePath.like(
						treePath.concat(StringPool.PERCENT))
				)
			));

		for (Object[] result : results) {
			fileEntryTypeIds.put((Long)result[0], (Long)result[1]);
		}

		return fileEntryTypeIds;
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, int start, int end) {

		return getGroupFileEntries(
			groupId, start, end, new RepositoryModelModifiedDateComparator<>());
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return dlFileEntryPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, int start, int end) {

		return getGroupFileEntries(
			groupId, userId, start, end,
			new RepositoryModelModifiedDateComparator<>());
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		if (userId <= 0) {
			return dlFileEntryPersistence.findByGroupId(
				groupId, start, end, orderByComparator);
		}

		return dlFileEntryPersistence.findByG_U(
			groupId, userId, start, end, orderByComparator);
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, long rootFolderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getGroupFileEntries(
			groupId, userId, 0, rootFolderId, start, end, orderByComparator);
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, long repositoryId, long rootFolderId,
		int start, int end, OrderByComparator<DLFileEntry> orderByComparator) {

		List<Long> folderIds;

		if (repositoryId != 0) {
			folderIds = _dlFolderLocalService.getRepositoryFolderIds(
				repositoryId, rootFolderId);
		}
		else {
			folderIds = _dlFolderLocalService.getGroupFolderIds(
				groupId, rootFolderId);
		}

		if (folderIds.isEmpty()) {
			return Collections.emptyList();
		}

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY, start, end, orderByComparator);

		if (repositoryId == 0) {
			if (userId <= 0) {
				return dlFileEntryFinder.findByG_F(
					groupId, folderIds, queryDefinition);
			}

			return dlFileEntryFinder.findByG_U_F(
				groupId, userId, folderIds, queryDefinition);
		}

		List<Long> repositoryIds = new ArrayList<>();

		repositoryIds.add(repositoryId);

		if (userId <= 0) {
			return dlFileEntryFinder.findByG_R_F(
				groupId, repositoryIds, folderIds, queryDefinition);
		}

		return dlFileEntryFinder.findByG_U_R_F(
			groupId, userId, repositoryIds, folderIds, queryDefinition);
	}

	@Override
	public int getGroupFileEntriesCount(long groupId) {
		return dlFileEntryPersistence.countByGroupId(groupId);
	}

	@Override
	public int getGroupFileEntriesCount(long groupId, long userId) {
		if (userId <= 0) {
			return dlFileEntryPersistence.countByGroupId(groupId);
		}

		return dlFileEntryPersistence.countByG_U(groupId, userId);
	}

	@Override
	public List<DLFileEntry> getNoAssetFileEntries() {
		return dlFileEntryFinder.findByNoAssets();
	}

	@Override
	public List<DLFileEntry> getRepositoryFileEntries(
		long repositoryId, int start, int end) {

		return dlFileEntryPersistence.findByRepositoryId(
			repositoryId, start, end);
	}

	@Override
	public int getRepositoryFileEntriesCount(long repositoryId) {
		return dlFileEntryPersistence.countByRepositoryId(repositoryId);
	}

	@Override
	public String getUniqueTitle(
			long groupId, long folderId, long fileEntryId, String title,
			String extension)
		throws PortalException {

		String uniqueTitle = title;

		for (int i = 1;; i++) {
			String uniqueFileName = DLUtil.getSanitizedFileName(
				uniqueTitle, extension);

			try {
				validateFile(
					groupId, folderId, fileEntryId, uniqueFileName,
					uniqueTitle);

				return uniqueTitle;
			}
			catch (DuplicateFileEntryException | DuplicateFolderNameException
						exception) {

				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			uniqueTitle = FileUtil.appendParentheticalSuffix(
				title, String.valueOf(i));
		}
	}

	@Override
	public boolean hasExtraSettings() {
		if (dlFileEntryFinder.countByExtraSettings() > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasFileEntryLock(long userId, long fileEntryId)
		throws PortalException {

		DLFileEntry dlFileEntry = getFileEntry(fileEntryId);

		return hasFileEntryLock(userId, fileEntryId, dlFileEntry.getFolderId());
	}

	@Override
	public boolean hasFileEntryLock(
		long userId, long fileEntryId, long folderId) {

		boolean hasLock = LockManagerUtil.hasLock(
			userId, DLFileEntry.class.getName(), fileEntryId);

		if (hasLock ||
			(folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			return hasLock;
		}

		return _dlFolderLocalService.hasInheritableLock(folderId);
	}

	@Override
	@Transactional(enabled = false)
	public void incrementViewCounter(DLFileEntry dlFileEntry, int increment) {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		ViewCountManager viewCountManager = _viewCountManagerSnapshot.get();

		viewCountManager.incrementViewCount(
			dlFileEntry.getCompanyId(),
			_classNameLocalService.getClassNameId(DLFileEntry.class),
			dlFileEntry.getFileEntryId(), increment);
	}

	@Override
	public boolean isFileEntryCheckedOut(long fileEntryId) {
		int count = _dlFileVersionPersistence.countByF_V(
			fileEntryId, DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public Lock lockFileEntry(long userId, long fileEntryId)
		throws PortalException {

		return LockManagerUtil.lock(
			userId, DLFileEntry.class.getName(), fileEntryId, null, false,
			DLFileEntryImpl.LOCK_EXPIRATION_TIME);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DLFileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		if (!hasFileEntryLock(userId, fileEntryId)) {
			lockFileEntry(userId, fileEntryId);
		}

		try {
			DLFileEntry dlFileEntry = _moveFileEntryImpl(
				userId, fileEntryId, newFolderId, serviceContext);

			return _dlFileEntryTypeLocalService.updateFileEntryFileEntryType(
				dlFileEntry, serviceContext);
		}
		finally {
			if (!isFileEntryCheckedOut(fileEntryId)) {
				unlockFileEntry(fileEntryId);
			}
		}
	}

	@Override
	public void rebuildTree(long companyId) throws PortalException {
		_dlFolderLocalService.rebuildTree(companyId);
	}

	@Override
	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		if (!_isValidFileVersionNumber(version)) {
			throw new InvalidFileVersionException(
				StringBundler.concat(
					"Unable to revert file entry ", fileEntryId, " to version ",
					version, " because it is invalid"));
		}

		if (version.equals(DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION)) {
			throw new InvalidFileVersionException(
				"Unable to revert a private working copy file version");
		}

		DLFileVersion dlFileVersion = _dlFileVersionLocalService.getFileVersion(
			fileEntryId, version);

		if (!dlFileVersion.isApproved() && !dlFileVersion.isScheduled()) {
			throw new InvalidFileVersionException(
				"Unable to revert from an unapproved file version");
		}

		DLFileVersion latestDLFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(fileEntryId, false);

		if (version.equals(latestDLFileVersion.getVersion())) {
			throw new InvalidFileVersionException(
				"Unable to revert from the latest file version");
		}

		String sourceFileName = dlFileVersion.getFileName();
		String changeLog = LanguageUtil.format(
			serviceContext.getLocale(), "reverted-to-x", version, false);
		DLVersionNumberIncrease dlVersionNumberIncrease =
			DLVersionNumberIncrease.MAJOR;
		Map<String, DDMFormValues> ddmFormValuesMap = null;
		InputStream inputStream = getFileAsStream(fileEntryId, version, false);

		serviceContext.setCommand(Constants.REVERT);

		DLFileEntry dlFileEntry = dlFileEntryLocalService.getFileEntry(
			fileEntryId);

		long fileEntryTypeId = _getValidFileEntryTypeId(
			dlFileVersion.getFileEntryTypeId(), dlFileEntry);

		_updateFileEntry(
			userId, fileEntryId, sourceFileName, dlFileVersion.getExtension(),
			dlFileVersion.getMimeType(), dlFileVersion.getTitle(),
			dlFileVersion.getDescription(), changeLog, dlVersionNumberIncrease,
			dlFileVersion.getExtraSettings(), fileEntryTypeId, ddmFormValuesMap,
			null, inputStream, dlFileVersion.getSize(),
			dlFileVersion.getDisplayDate(), dlFileVersion.getExpirationDate(),
			dlFileVersion.getReviewDate(), serviceContext);

		DLFileVersion newDLFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(fileEntryId, false);

		copyFileEntryMetadata(
			dlFileVersion.getCompanyId(), dlFileVersion.getFileEntryTypeId(),
			fileEntryId, dlFileVersion.getFileVersionId(),
			newDLFileVersion.getFileVersionId(), serviceContext);
	}

	@Override
	public Hits search(
			long groupId, long userId, long creatorUserId, int status,
			int start, int end)
		throws PortalException {

		return search(
			groupId, userId, creatorUserId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, null, status, start,
			end);
	}

	@Override
	public Hits search(
			long groupId, long userId, long creatorUserId, long folderId,
			String[] mimeTypes, int status, int start, int end)
		throws PortalException {

		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.getIndexer(
			DLFileEntryConstants.getClassName());

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(Field.STATUS, status);

		if (creatorUserId > 0) {
			searchContext.setAttribute(
				Field.USER_ID, String.valueOf(creatorUserId));
		}

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			searchContext.setAttribute("mimeTypes", mimeTypes);
		}

		searchContext.setAttribute("paginationType", "none");

		Group group = _groupLocalService.getGroup(groupId);

		searchContext.setCompanyId(group.getCompanyId());

		searchContext.setEnd(end);
		searchContext.setFolderIds(new long[] {folderId});
		searchContext.setGroupIds(new long[] {groupId});
		searchContext.setSorts(new Sort(Field.MODIFIED_DATE, true));
		searchContext.setStart(start);
		searchContext.setUserId(userId);

		return indexer.search(searchContext);
	}

	@Override
	public void setTreePaths(long folderId, String treePath, boolean reindex)
		throws PortalException {

		if (treePath == null) {
			throw new IllegalArgumentException("Tree path is null");
		}

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
					DLFolder dlFolder = _dlFolderLocalService.fetchDLFolder(
						folderId);

					if (dlFolder != null) {
						Property groupIdProperty = PropertyFactoryUtil.forName(
							"groupId");

						dynamicQuery.add(
							groupIdProperty.eq(dlFolder.getGroupId()));
					}
				}

				Property folderIdProperty = PropertyFactoryUtil.forName(
					"folderId");

				dynamicQuery.add(folderIdProperty.eq(folderId));

				Property treePathProperty = PropertyFactoryUtil.forName(
					"treePath");

				dynamicQuery.add(
					RestrictionsFactoryUtil.or(
						treePathProperty.isNull(),
						treePathProperty.ne(treePath)));
			});

		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.getIndexer(
			DLFileEntry.class.getName());

		indexableActionableDynamicQuery.setPerformActionMethod(
			(DLFileEntry dlFileEntry) -> {
				dlFileEntry.setTreePath(treePath);

				dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);

				if (!reindex) {
					return;
				}

				indexableActionableDynamicQuery.addDocuments(
					indexer.getDocument(dlFileEntry));
			});

		indexableActionableDynamicQuery.performActions();
	}

	@Override
	public void unlockFileEntry(long fileEntryId) {
		LockManagerUtil.unlock(DLFileEntry.class.getName(), fileEntryId);
	}

	@Override
	public DLFileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			long fileEntryTypeId, Map<String, DDMFormValues> ddmFormValuesMap,
			File file, InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			fileEntryId);

		String extension = FileUtil.getExtension(sourceFileName);

		if ((file == null) && (inputStream == null)) {
			if (Validator.isNull(extension)) {
				extension = dlFileEntry.getExtension();
			}

			mimeType = dlFileEntry.getMimeType();
		}

		String extraSettings = StringPool.BLANK;

		if (fileEntryTypeId == -1) {
			fileEntryTypeId = dlFileEntry.getFileEntryTypeId();
		}

		_validateFileEntryTypeId(
			PortalUtil.getCurrentAndAncestorSiteGroupIds(
				dlFileEntry.getGroupId()),
			dlFileEntry.getFolderId(), fileEntryTypeId);

		if ((fileEntryTypeId != dlFileEntry.getFileEntryTypeId()) &&
			(dlFileEntry.getFileEntryTypeId() !=
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT)) {

			DLFileEntryMetadata dlFileEntryMetadata =
				_dlFileEntryMetadataPersistence.fetchByFileEntryId_Last(
					fileEntryId, null);

			DDMStructure ddmStructure = DDMStructureManagerUtil.fetchStructure(
				dlFileEntryMetadata.getDDMStructureId());

			if (ddmStructure != null) {
				DDMStructureLinkManagerUtil.deleteStructureLink(
					_classNameLocalService.getClassNameId(
						DLFileEntryMetadata.class),
					dlFileEntryMetadata.getFileEntryMetadataId(),
					ddmStructure.getStructureId());
			}
		}

		return _updateFileEntry(
			userId, fileEntryId, sourceFileName, extension, mimeType, title,
			description, changeLog, dlVersionNumberIncrease, extraSettings,
			fileEntryTypeId, ddmFormValuesMap, file, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);
	}

	@Override
	public DLFileEntry updateFileEntryType(
			long userId, long fileEntryId, long fileEntryTypeId,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		DLFileEntry dlFileEntry = dlFileEntryLocalService.getFileEntry(
			fileEntryId);

		dlFileEntry.setFileEntryTypeId(fileEntryTypeId);

		dlFileEntry = dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(
				fileEntryId, !dlFileEntry.isCheckedOut());

		dlFileVersion.setUserId(user.getUserId());
		dlFileVersion.setUserName(user.getFullName());
		dlFileVersion.setFileEntryTypeId(fileEntryTypeId);

		_dlFileVersionLocalService.updateDLFileVersion(dlFileVersion);

		return dlFileEntry;
	}

	@Override
	public DLFileEntry updateStatus(
			long userId, DLFileEntry dlFileEntry, DLFileVersion dlFileVersion,
			int status, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		// File version

		Date date = new Date();

		status = _getStatus(date, dlFileVersion, status);

		Date expirationDate = dlFileVersion.getExpirationDate();

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(expirationDate != null) && expirationDate.before(date)) {

			dlFileVersion.setExpirationDate(null);
		}

		int oldStatus = dlFileVersion.getStatus();

		dlFileVersion = _updateFileVersionStatus(dlFileVersion, status, userId);

		// File entry

		if (status == WorkflowConstants.STATUS_APPROVED) {
			int compare = DLUtil.compareVersions(
				dlFileEntry.getVersion(), dlFileVersion.getVersion());

			if (compare <= 0) {
				dlFileEntry.setModifiedDate(dlFileVersion.getModifiedDate());
				dlFileEntry.setFileName(dlFileVersion.getFileName());
				dlFileEntry.setExtension(dlFileVersion.getExtension());
				dlFileEntry.setMimeType(dlFileVersion.getMimeType());
				dlFileEntry.setTitle(dlFileVersion.getTitle());
				dlFileEntry.setDescription(dlFileVersion.getDescription());
				dlFileEntry.setExtraSettings(dlFileVersion.getExtraSettings());
				dlFileEntry.setFileEntryTypeId(
					dlFileVersion.getFileEntryTypeId());
				dlFileEntry.setVersion(dlFileVersion.getVersion());
				dlFileEntry.setSize(dlFileVersion.getSize());
				dlFileEntry.setDisplayDate(dlFileVersion.getDisplayDate());
				dlFileEntry.setExpirationDate(
					dlFileVersion.getExpirationDate());
				dlFileEntry.setReviewDate(dlFileVersion.getReviewDate());

				dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);
			}
		}
		else {

			// File entry

			if ((status != WorkflowConstants.STATUS_IN_TRASH) &&
				Objects.equals(
					dlFileEntry.getVersion(), dlFileVersion.getVersion())) {

				List<DLFileVersion> approvedFileVersions =
					_dlFileVersionPersistence.findByF_S(
						dlFileEntry.getFileEntryId(),
						WorkflowConstants.STATUS_APPROVED);

				if (!approvedFileVersions.isEmpty()) {
					DLFileVersion firstApprovedFileVersion =
						approvedFileVersions.get(0);

					dlFileEntry.setVersion(
						firstApprovedFileVersion.getVersion());
				}

				dlFileEntry.setDisplayDate(dlFileVersion.getDisplayDate());
				dlFileEntry.setExpirationDate(
					dlFileVersion.getExpirationDate());

				dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);
			}
			else if (!Objects.equals(
						dlFileEntry.getDisplayDate(),
						dlFileVersion.getDisplayDate()) ||
					 !Objects.equals(
						 dlFileEntry.getExpirationDate(),
						 dlFileVersion.getExpirationDate())) {

				dlFileEntry.setDisplayDate(dlFileVersion.getDisplayDate());
				dlFileEntry.setExpirationDate(
					dlFileVersion.getExpirationDate());

				dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);
			}

			// Indexer

			if (Objects.equals(
					dlFileVersion.getVersion(),
					DLFileEntryConstants.VERSION_DEFAULT)) {

				Indexer<DLFileEntry> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(DLFileEntry.class);

				indexer.delete(dlFileEntry);
			}
		}

		// App helper

		_dlAppHelperLocalService.updateStatus(
			userId, new LiferayFileEntry(dlFileEntry),
			new LiferayFileVersion(dlFileVersion), oldStatus, status,
			serviceContext, workflowContext);

		if (PropsValues.DL_FILE_ENTRY_COMMENTS_ENABLED) {
			if (status == WorkflowConstants.STATUS_IN_TRASH) {
				CommentManagerUtil.moveDiscussionToTrash(
					DLFileEntry.class.getName(), dlFileEntry.getFileEntryId());
			}
			else if (oldStatus == WorkflowConstants.STATUS_IN_TRASH) {
				CommentManagerUtil.restoreDiscussionFromTrash(
					DLFileEntry.class.getName(), dlFileEntry.getFileEntryId());
			}
		}

		// Indexer

		if (((status == WorkflowConstants.STATUS_APPROVED) ||
			 (status == WorkflowConstants.STATUS_EXPIRED) ||
			 (status == WorkflowConstants.STATUS_IN_TRASH) ||
			 (status == WorkflowConstants.STATUS_SCHEDULED) ||
			 (oldStatus == WorkflowConstants.STATUS_IN_TRASH)) &&
			((serviceContext == null) || serviceContext.isIndexingEnabled())) {

			_reindex(dlFileEntry);
		}

		return dlFileEntry;
	}

	@Override
	public DLFileEntry updateStatus(
			long userId, long fileVersionId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		DLFileVersion dlFileVersion =
			_dlFileVersionPersistence.findByPrimaryKey(fileVersionId);

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			dlFileVersion.getFileEntryId());

		return updateStatus(
			userId, dlFileEntry, dlFileVersion, status, serviceContext,
			workflowContext);
	}

	@Override
	public void validateFile(
			long groupId, long folderId, long fileEntryId, String fileName,
			String title)
		throws PortalException {

		_validateFolder(groupId, folderId, title);

		DLFileEntry dlFileEntry = dlFileEntryPersistence.fetchByG_F_T(
			groupId, folderId, title);

		if ((dlFileEntry != null) &&
			(dlFileEntry.getFileEntryId() != fileEntryId)) {

			throw new DuplicateFileEntryException(
				"A file entry already exists with title " + title);
		}

		dlFileEntry = dlFileEntryPersistence.fetchByG_F_FN(
			groupId, folderId, fileName);

		if ((dlFileEntry != null) &&
			(dlFileEntry.getFileEntryId() != fileEntryId)) {

			throw new DuplicateFileEntryException(
				"A file entry already exists with file name " + fileName);
		}
	}

	@Override
	public boolean verifyFileEntryCheckOut(long fileEntryId, String lockUuid)
		throws PortalException {

		if (verifyFileEntryLock(fileEntryId, lockUuid) &&
			isFileEntryCheckedOut(fileEntryId)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean verifyFileEntryLock(long fileEntryId, String lockUuid)
		throws PortalException {

		Lock lock = LockManagerUtil.fetchLock(
			DLFileEntry.class.getName(), fileEntryId);

		if (lock != null) {
			return Objects.equals(lock.getUuid(), lockUuid);
		}

		DLFileEntry dlFileEntry = dlFileEntryLocalService.getFileEntry(
			fileEntryId);

		return _dlFolderLocalService.verifyInheritableLock(
			dlFileEntry.getFolderId(), lockUuid);
	}

	private void _addFileEntryResources(
			DLFileEntry dlFileEntry, ServiceContext serviceContext)
		throws PortalException {

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			_resourceLocalService.addResources(
				dlFileEntry.getCompanyId(), dlFileEntry.getGroupId(),
				dlFileEntry.getUserId(), DLFileEntry.class.getName(),
				dlFileEntry.getFileEntryId(), false, serviceContext);
		}
		else {
			if (serviceContext.isDeriveDefaultPermissions()) {
				serviceContext.deriveDefaultPermissions(
					dlFileEntry.getRepositoryId(),
					DLFileEntryConstants.getClassName());
			}

			_resourceLocalService.addModelResources(
				dlFileEntry.getCompanyId(), dlFileEntry.getGroupId(),
				dlFileEntry.getUserId(), DLFileEntry.class.getName(),
				dlFileEntry.getFileEntryId(),
				serviceContext.getModelPermissions());
		}
	}

	private DLFileVersion _addFileVersion(
			User user, DLFileEntry dlFileEntry, String fileName,
			String extension, String mimeType, String title, String description,
			String changeLog, String extraSettings, long fileEntryTypeId,
			Map<String, DDMFormValues> ddmFormValuesMap, String version,
			long size, Date displayDate, Date expirationDate, Date reviewDate,
			int status, ServiceContext serviceContext)
		throws PortalException {

		long fileVersionId = counterLocalService.increment();

		DLFileVersion dlFileVersion = _dlFileVersionPersistence.create(
			fileVersionId);

		dlFileVersion.setUuid(
			ParamUtil.getString(
				serviceContext, "fileVersionUuid", serviceContext.getUuid()));
		dlFileVersion.setGroupId(dlFileEntry.getGroupId());
		dlFileVersion.setCompanyId(dlFileEntry.getCompanyId());
		dlFileVersion.setUserId(user.getUserId());
		dlFileVersion.setUserName(user.getFullName());
		dlFileVersion.setRepositoryId(dlFileEntry.getRepositoryId());
		dlFileVersion.setFolderId(dlFileEntry.getFolderId());
		dlFileVersion.setFileEntryId(dlFileEntry.getFileEntryId());
		dlFileVersion.setTreePath(dlFileVersion.buildTreePath());
		dlFileVersion.setFileName(fileName);
		dlFileVersion.setExtension(extension);
		dlFileVersion.setMimeType(mimeType);
		dlFileVersion.setTitle(title);
		dlFileVersion.setDescription(description);
		dlFileVersion.setChangeLog(changeLog);
		dlFileVersion.setExtraSettings(extraSettings);
		dlFileVersion.setFileEntryTypeId(fileEntryTypeId);
		dlFileVersion.setVersion(version);
		dlFileVersion.setSize(size);
		dlFileVersion.setStoreUUID(String.valueOf(UUID.randomUUID()));
		dlFileVersion.setDisplayDate(displayDate);
		dlFileVersion.setExpirationDate(expirationDate);
		dlFileVersion.setReviewDate(reviewDate);
		dlFileVersion.setStatus(status);
		dlFileVersion.setStatusByUserId(user.getUserId());
		dlFileVersion.setStatusByUserName(user.getFullName());
		dlFileVersion.setStatusDate(dlFileEntry.getModifiedDate());

		ExpandoBridge oldExpandoBridge = dlFileVersion.getExpandoBridge();

		DLFileVersion latestFileVersion =
			_dlFileVersionLocalService.fetchLatestFileVersion(
				dlFileEntry.getFileEntryId(), false);

		if (latestFileVersion != null) {
			oldExpandoBridge = latestFileVersion.getExpandoBridge();
		}

		ExpandoBridgeUtil.setExpandoBridgeAttributes(
			oldExpandoBridge, dlFileVersion.getExpandoBridge(), serviceContext);

		dlFileVersion = _dlFileVersionPersistence.update(dlFileVersion);

		if ((fileEntryTypeId > 0) && (ddmFormValuesMap != null)) {
			_dlFileEntryMetadataLocalService.updateFileEntryMetadata(
				null, fileEntryTypeId, dlFileEntry.getFileEntryId(),
				fileVersionId, ddmFormValuesMap, serviceContext);
		}

		return dlFileVersion;
	}

	private String _buildEntryURL(DLFileVersion fileVersion)
		throws PortalException {

		String portletId = PortletProviderUtil.getPortletId(
			FileEntry.class.getName(), PortletProvider.Action.MANAGE);

		String entryURL = PortalUtil.getControlPanelFullURL(
			fileVersion.getGroupId(), portletId, null);

		String namespace = PortalUtil.getPortletNamespace(portletId);

		entryURL = HttpComponentsUtil.addParameter(
			entryURL, namespace + "mvcRenderCommandName",
			"/document_library/edit_file_entry");
		entryURL = HttpComponentsUtil.addParameter(
			entryURL, namespace + "redirect",
			HttpComponentsUtil.addParameter(
				PortalUtil.getControlPanelFullURL(
					fileVersion.getGroupId(), portletId, null),
				namespace + "folderId", fileVersion.getFolderId()));
		entryURL = HttpComponentsUtil.addParameter(
			entryURL, namespace + "groupId", fileVersion.getGroupId());
		entryURL = HttpComponentsUtil.addParameter(
			entryURL, namespace + "folderId", fileVersion.getFolderId());
		entryURL = HttpComponentsUtil.addParameter(
			entryURL, namespace + "fileEntryId",
			String.valueOf(fileVersion.getFileEntryId()));

		return entryURL;
	}

	private void _checkFileEntriesByDisplayDate(
			long companyId, Date displayDate, long userId)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Publishing file entries with display date between ",
					_dates.get(companyId), " and ", displayDate,
					" for company ", companyId));
		}

		_publishFileEntriesByCompanyId(
			companyId, displayDate, userId, Collections.emptyMap(),
			new ServiceContext());
	}

	private void _checkFileEntriesByReviewDate(long companyId, Date reviewDate)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Sending review notification for file entries with review ",
					"date between ", _dates.get(companyId), " and ",
					reviewDate));
		}

		List<DLFileEntry> fileEntries = _getFileEntriesByReviewDate(
			companyId, reviewDate, _dates.get(companyId));

		for (DLFileEntry fileEntry : fileEntries) {
			if (fileEntry.isInTrash()) {
				continue;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Sending review notification for file entry " +
						fileEntry.getFileEntryId());
			}

			DLFileVersion latestFileVersion =
				_dlFileVersionLocalService.fetchLatestFileVersion(
					fileEntry.getFileEntryId(), false);

			_notify(
				fileEntry.getUserId(), _EMAIL_TYPE_REVIEW, latestFileVersion);
		}
	}

	private void _checkFileVersionsByExpirationDate(
			long companyId, Date expirationDate, long userId)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Expiring file versions with expiration date prior to ",
					expirationDate, " for company ", companyId));
		}

		_expireFileVersionsByCompanyId(
			companyId, expirationDate, userId, Collections.emptyMap(),
			new ServiceContext());
	}

	private DLFileEntry _checkOutDLFileEntryModel(
			long userId, long fileEntryId, long fileEntryTypeId,
			ServiceContext serviceContext)
		throws PortalException {

		return _checkOutDLFileEntryModel(
			userId, fileEntryId, fileEntryTypeId, StringPool.BLANK,
			DLFileEntryImpl.LOCK_EXPIRATION_TIME, serviceContext);
	}

	private DLFileEntry _checkOutDLFileEntryModel(
			long userId, long fileEntryId, long fileEntryTypeId, String owner,
			long expirationTime, ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			fileEntryId);

		boolean hasLock = hasFileEntryLock(
			userId, fileEntryId, dlFileEntry.getFolderId());

		if (!hasLock) {
			if ((expirationTime <= 0) ||
				(expirationTime > DLFileEntryImpl.LOCK_EXPIRATION_TIME)) {

				expirationTime = DLFileEntryImpl.LOCK_EXPIRATION_TIME;
			}

			LockManagerUtil.lock(
				userId, DLFileEntry.class.getName(), fileEntryId, owner, false,
				expirationTime);
		}

		User user = _userPersistence.findByPrimaryKey(userId);

		serviceContext.setCompanyId(user.getCompanyId());

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(fileEntryId, false);

		serviceContext.setUserId(userId);

		boolean manualCheckinRequired = GetterUtil.getBoolean(
			serviceContext.getAttribute(DL.MANUAL_CHECK_IN_REQUIRED));

		if (dlFileEntry.isManualCheckInRequired() ^ manualCheckinRequired) {
			dlFileEntry.setManualCheckInRequired(manualCheckinRequired);

			dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);
		}

		String version = dlFileVersion.getVersion();

		if (!version.equals(
				DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION)) {

			DLFileVersion oldDLFileVersion = dlFileVersion;

			long oldDLFileVersionId = dlFileVersion.getFileVersionId();

			long existingDLFileVersionId = ParamUtil.getLong(
				serviceContext, "existingDLFileVersionId");

			if (existingDLFileVersionId > 0) {
				DLFileVersion existingDLFileVersion =
					_dlFileVersionPersistence.findByPrimaryKey(
						existingDLFileVersionId);

				dlFileVersion = _updateFileVersion(
					user, existingDLFileVersion, null,
					existingDLFileVersion.getFileName(),
					existingDLFileVersion.getExtension(),
					existingDLFileVersion.getMimeType(),
					existingDLFileVersion.getTitle(),
					existingDLFileVersion.getDescription(),
					existingDLFileVersion.getChangeLog(),
					existingDLFileVersion.getExtraSettings(),
					existingDLFileVersion.getFileEntryTypeId(), null,
					DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION, null,
					null, existingDLFileVersion.getSize(),
					existingDLFileVersion.getDisplayDate(),
					existingDLFileVersion.getExpirationDate(),
					existingDLFileVersion.getReviewDate(),
					WorkflowConstants.STATUS_DRAFT,
					serviceContext.getModifiedDate(null), serviceContext);
			}
			else {
				dlFileVersion = _addFileVersion(
					user, dlFileEntry, oldDLFileVersion.getFileName(),
					oldDLFileVersion.getExtension(),
					oldDLFileVersion.getMimeType(), oldDLFileVersion.getTitle(),
					oldDLFileVersion.getDescription(),
					oldDLFileVersion.getChangeLog(),
					oldDLFileVersion.getExtraSettings(),
					oldDLFileVersion.getFileEntryTypeId(), null,
					DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION,
					oldDLFileVersion.getSize(),
					oldDLFileVersion.getDisplayDate(),
					oldDLFileVersion.getExpirationDate(),
					oldDLFileVersion.getReviewDate(),
					WorkflowConstants.STATUS_DRAFT, serviceContext);

				_copyExpandoRowModifiedDate(
					dlFileEntry.getCompanyId(), oldDLFileVersionId,
					dlFileVersion.getFileVersionId());
			}

			Serializable validateDDMFormValues = serviceContext.getAttribute(
				"validateDDMFormValues");

			serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);

			if (fileEntryTypeId == oldDLFileVersion.getFileEntryTypeId()) {
				copyFileEntryMetadata(
					dlFileEntry.getCompanyId(), fileEntryTypeId, fileEntryId,
					oldDLFileVersionId, dlFileVersion.getFileVersionId(),
					serviceContext);
			}

			serviceContext.setAttribute(
				"validateDDMFormValues", validateDDMFormValues);
		}

		return dlFileEntry;
	}

	private DLVersionNumberIncrease _computeDLVersionNumberIncrease(
		DLVersionNumberIncrease dlVersionNumberIncrease,
		DLFileVersion previousDLFileVersion, DLFileVersion nextDLFileVersion,
		int workflowAction) {

		if (workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT) {
			return DLVersionNumberIncrease.MINOR;
		}

		VersioningStrategy versioningStrategy =
			_versioningStrategySnapshot.get();

		if (versioningStrategy == null) {
			if ((dlVersionNumberIncrease == null) ||
				(dlVersionNumberIncrease ==
					DLVersionNumberIncrease.AUTOMATIC)) {

				return DLVersionNumberIncrease.NONE;
			}

			return dlVersionNumberIncrease;
		}

		if (versioningStrategy.isOverridable() &&
			(dlVersionNumberIncrease != null) &&
			(dlVersionNumberIncrease != DLVersionNumberIncrease.AUTOMATIC)) {

			return dlVersionNumberIncrease;
		}

		return versioningStrategy.computeDLVersionNumberIncrease(
			previousDLFileVersion, nextDLFileVersion);
	}

	private void _convertExtraSettings(
			DLFileEntry dlFileEntry, DLFileVersion dlFileVersion, String[] keys)
		throws PortalException {

		UnicodeProperties extraSettingsUnicodeProperties =
			dlFileVersion.getExtraSettingsProperties();

		_convertExtraSettings(
			extraSettingsUnicodeProperties, dlFileVersion.getExpandoBridge(),
			keys);

		dlFileVersion.setExtraSettingsProperties(
			extraSettingsUnicodeProperties);

		dlFileVersion = _dlFileVersionPersistence.update(dlFileVersion);

		int status = dlFileVersion.getStatus();

		if (status == WorkflowConstants.STATUS_APPROVED) {
			int compare = DLUtil.compareVersions(
				dlFileEntry.getVersion(), dlFileVersion.getVersion());

			if (compare <= 0) {
				_reindex(dlFileEntry);
			}
		}
	}

	private void _convertExtraSettings(DLFileEntry dlFileEntry, String[] keys)
		throws PortalException {

		UnicodeProperties extraSettingsUnicodeProperties =
			dlFileEntry.getExtraSettingsProperties();

		_convertExtraSettings(
			extraSettingsUnicodeProperties, dlFileEntry.getExpandoBridge(),
			keys);

		dlFileEntry.setExtraSettingsProperties(extraSettingsUnicodeProperties);

		dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);

		List<DLFileVersion> dlFileVersions =
			_dlFileVersionLocalService.getFileVersions(
				dlFileEntry.getFileEntryId(), WorkflowConstants.STATUS_ANY);

		for (DLFileVersion dlFileVersion : dlFileVersions) {
			_convertExtraSettings(dlFileEntry, dlFileVersion, keys);
		}
	}

	private void _convertExtraSettings(
		UnicodeProperties extraSettingsUnicodeProperties,
		ExpandoBridge expandoBridge, String[] keys) {

		for (String key : keys) {
			String value = extraSettingsUnicodeProperties.remove(key);

			if (Validator.isNull(value)) {
				continue;
			}

			int type = expandoBridge.getAttributeType(key);

			expandoBridge.setAttribute(
				key, ExpandoColumnConstants.getSerializable(type, value));
		}
	}

	private void _copyExpandoRowModifiedDate(
		long companyId, long sourceFileVersionId, long targetFileVersionId) {

		ExpandoTable expandoTable = _expandoTableLocalService.fetchDefaultTable(
			companyId, DLFileEntry.class.getName());

		if (expandoTable == null) {
			return;
		}

		ExpandoRow sourceExpandoRow = _expandoRowLocalService.fetchRow(
			expandoTable.getTableId(), sourceFileVersionId);

		if (sourceExpandoRow == null) {
			return;
		}

		ExpandoRow targetExpandoRow = _expandoRowLocalService.fetchRow(
			expandoTable.getTableId(), targetFileVersionId);

		if (targetExpandoRow == null) {
			return;
		}

		targetExpandoRow.setModifiedDate(sourceExpandoRow.getModifiedDate());

		_expandoRowLocalService.updateExpandoRow(targetExpandoRow);
	}

	private void _copyFileEntryMetadata(
			long companyId, long fileEntryId, long sourceFileVersionId,
			long targetFileVersionId, ServiceContext serviceContext,
			Map<String, DDMFormValues> ddmFormValuesMap,
			List<DDMStructure> ddmStructures)
		throws PortalException {

		for (DDMStructure ddmStructure : ddmStructures) {
			DLFileEntryMetadata dlFileEntryMetadata =
				_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
					ddmStructure.getStructureId(), sourceFileVersionId);

			if (dlFileEntryMetadata == null) {
				continue;
			}

			ddmFormValuesMap.put(
				ddmStructure.getStructureKey(),
				StorageEngineManagerUtil.getDDMFormValues(
					dlFileEntryMetadata.getDDMStorageId()));
		}

		if (!ddmFormValuesMap.isEmpty()) {
			_dlFileEntryMetadataLocalService.updateFileEntryMetadata(
				null, companyId, ddmStructures, fileEntryId,
				targetFileVersionId, ddmFormValuesMap, serviceContext);
		}
	}

	private void _copyFileVersion(
			DLFileEntry dlFileEntry, DLFileVersion fromDLFileVersion,
			DLFileVersion toDLFileVersion)
		throws PortalException {

		try {
			DLStoreUtil.copyFileVersion(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), fromDLFileVersion.getStoreFileName(),
				toDLFileVersion.getStoreFileName());
		}
		catch (NoSuchFileException noSuchFileException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Using version of the file as file name for file version " +
						fromDLFileVersion,
					noSuchFileException);
			}

			DLStoreUtil.copyFileVersion(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), fromDLFileVersion.getVersion(),
				toDLFileVersion.getStoreFileName());
		}
	}

	private void _deleteFile(
			long companyId, long repositoryId, String name,
			String storeFileName)
		throws PortalException {

		DLStoreUtil.deleteFile(companyId, repositoryId, name, storeFileName);
		DLStoreUtil.deleteFile(
			companyId, repositoryId, name, storeFileName + ".index");
	}

	private void _expireFileVersion(
			long userId, DLFileEntry fileEntry, DLFileVersion fileVersion,
			boolean latestFileVersion,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		if (fileVersion.isExpired()) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Expiring file entry ", fileEntry.getFileEntryId(),
					" on version ", fileVersion.getVersion(),
					" with expiration date ", fileVersion.getExpirationDate()));
		}

		if (latestFileVersion) {
			updateStatus(
				userId, fileEntry, fileVersion,
				WorkflowConstants.STATUS_EXPIRED, serviceContext,
				workflowContext);

			_notify(userId, _EMAIL_TYPE_EXPIRED, fileVersion);
		}
		else {
			_updateFileVersionStatus(
				fileVersion, WorkflowConstants.STATUS_EXPIRED, userId);
		}
	}

	private void _expireFileVersionsByCompanyId(
			long companyId, Date expirationDate, long userId,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		List<DLFileVersion> dlFileVersions =
			_getFileVersionByCompanyIdAndExpirationDate(
				companyId, expirationDate);

		for (DLFileVersion dlFileVersion : dlFileVersions) {
			DLFileEntry fileEntry = dlFileEntryPersistence.findByPrimaryKey(
				dlFileVersion.getFileEntryId());

			if (fileEntry.isInTrash()) {
				continue;
			}

			ExpireAllVersions expireAllVersions =
				_expireAllVersionsSnapshot.get();

			DLFileVersion latestFileVersion =
				_dlFileVersionLocalService.fetchLatestFileVersion(
					fileEntry.getFileEntryId(), false);

			if (expireAllVersions.isEnabled()) {
				List<DLFileVersion> fileVersions = fileEntry.getFileVersions(
					WorkflowConstants.STATUS_ANY);

				for (DLFileVersion fileVersion : fileVersions) {
					_expireFileVersion(
						userId, fileEntry, fileVersion,
						_isLatestFileVersion(fileVersion, latestFileVersion),
						workflowContext, serviceContext);
				}
			}
			else {
				_expireFileVersion(
					userId, fileEntry, dlFileVersion,
					_isLatestFileVersion(dlFileVersion, latestFileVersion),
					workflowContext, serviceContext);
			}
		}
	}

	private long _getActiveCompanyAdminUserId(long companyId)
		throws PortalException {

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		Long userId = _getActiveUser(
			_userLocalService.getRoleUserIds(role.getRoleId()));

		if (userId != null) {
			return userId;
		}

		List<Group> groups = _groupLocalService.getRoleGroups(role.getRoleId());

		for (Group group : groups) {
			if (group.isDepot() || group.isRegularSite()) {
				userId = _getActiveUser(
					_groupLocalService.getUserPrimaryKeys(group.getGroupId()));

				if (userId != null) {
					return userId;
				}
			}
			else if (group.isOrganization()) {
				userId = _getActiveUser(
					_organizationLocalService.getUserPrimaryKeys(
						group.getClassPK()));

				if (userId != null) {
					return userId;
				}
			}
			else if (group.isUserGroup()) {
				userId = _getActiveUser(
					_userGroupLocalService.getUserPrimaryKeys(
						group.getClassPK()));

				if (userId != null) {
					return userId;
				}
			}
		}

		throw new PortalException(
			"Unable to find an administrator user in company " + companyId);
	}

	private Long _getActiveUser(long[] userIds) {
		if (ArrayUtil.isNotEmpty(userIds)) {
			for (long userId : userIds) {
				User user = _userLocalService.fetchUser(userId);

				if ((user != null) && user.isActive()) {
					return userId;
				}
			}
		}

		return null;
	}

	private List<DLFileEntry> _getFileEntriesByReviewDate(
		long companyId, Date reviewDateLT, Date reviewDateGT) {

		return dlFileEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				DLFileEntryTable.INSTANCE
			).from(
				DLFileEntryTable.INSTANCE
			).where(
				DLFileEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					DLFileEntryTable.INSTANCE.reviewDate.gte(reviewDateGT)
				).and(
					DLFileEntryTable.INSTANCE.reviewDate.lte(reviewDateLT)
				)
			));
	}

	private List<DLFileVersion> _getFileVersionByCompanyIdAndExpirationDate(
		long companyId, Date expirationDate) {

		return _dlFileVersionPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				DLFileVersionTable.INSTANCE
			).from(
				DLFileVersionTable.INSTANCE
			).where(
				DLFileVersionTable.INSTANCE.companyId.eq(
					companyId
				).and(
					DLFileVersionTable.INSTANCE.expirationDate.gte(
						_dates.get(companyId))
				).and(
					DLFileVersionTable.INSTANCE.expirationDate.lte(
						expirationDate)
				).and(
					DLFileVersionTable.INSTANCE.status.in(
						new Integer[] {
							WorkflowConstants.STATUS_APPROVED,
							WorkflowConstants.STATUS_DRAFT
						})
				)
			));
	}

	private RepositoryEventTrigger _getFolderRepositoryEventTrigger(
			long groupId, long folderId)
		throws PortalException {

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return RepositoryUtil.getFolderRepositoryEventTrigger(folderId);
		}

		return RepositoryUtil.getRepositoryEventTrigger(groupId);
	}

	private InputStream _getInputStream(
			DLFileEntry dlFileEntry, DLFileVersion dlFileVersion)
		throws PortalException {

		try {
			return DLStoreUtil.getFileAsStream(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), dlFileVersion.getStoreFileName());
		}
		catch (NoSuchFileException noSuchFileException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Using version of the file as file name for file version " +
						dlFileVersion,
					noSuchFileException);
			}

			return DLStoreUtil.getFileAsStream(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), dlFileVersion.getVersion());
		}
	}

	private String _getNextVersion(
			DLFileEntry dlFileEntry,
			DLVersionNumberIncrease dlVersionNumberIncrease)
		throws InvalidFileVersionException {

		String version = dlFileEntry.getVersion();

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.fetchLatestFileVersion(
				dlFileEntry.getFileEntryId(), true);

		if (dlFileVersion != null) {
			version = dlFileVersion.getVersion();
		}

		if (!_isValidFileVersionNumber(version)) {
			throw new InvalidFileVersionException(
				StringBundler.concat(
					"Unable to increase version number for file entry ",
					dlFileEntry.getFileEntryId(),
					" because original version number ", version,
					" is invalid"));
		}

		int[] versionParts = StringUtil.split(version, StringPool.PERIOD, 0);

		if (dlVersionNumberIncrease == DLVersionNumberIncrease.MAJOR) {
			versionParts[0]++;
			versionParts[1] = 0;
		}
		else {
			versionParts[1]++;
		}

		return versionParts[0] + StringPool.PERIOD + versionParts[1];
	}

	private int _getStatus(Date date, DLFileVersion dlFileVersion, int status) {
		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(dlFileVersion.getDisplayDate() != null) &&
			date.before(dlFileVersion.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		return status;
	}

	private long _getValidFileEntryTypeId(
			long fileEntryTypeId, DLFileEntry dlFileEntry)
		throws PortalException {

		try {
			_validateFileEntryTypeId(
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					dlFileEntry.getGroupId()),
				dlFileEntry.getFolderId(), fileEntryTypeId);

			return fileEntryTypeId;
		}
		catch (InvalidFileEntryTypeException invalidFileEntryTypeException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(invalidFileEntryTypeException);
			}

			return _dlFileEntryTypeLocalService.getDefaultFileEntryTypeId(
				dlFileEntry.getFolderId());
		}
	}

	private boolean _isInTrashExplicitly(TrashedModel trashedModel) {
		TrashHelper trashHelper = _trashHelperSnapshot.get();

		if (trashHelper == null) {
			return GetterUtil.DEFAULT_BOOLEAN;
		}

		return trashHelper.isInTrashExplicitly(trashedModel);
	}

	private boolean _isLatestFileVersion(
		DLFileVersion fileVersion, DLFileVersion latestFileVersion) {

		if (fileVersion.getFileVersionId() ==
				latestFileVersion.getFileVersionId()) {

			return true;
		}

		return false;
	}

	private boolean _isValidFileVersionNumber(String version) {
		if (Validator.isNull(version)) {
			return false;
		}

		if (version.equals(DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION)) {
			return true;
		}

		Matcher matcher = _fileVersionPattern.matcher(version);

		return matcher.matches();
	}

	private DLFileEntry _moveFileEntryImpl(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		// File entry

		User user = _userPersistence.findByPrimaryKey(userId);

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			fileEntryId);

		long oldDataRepositoryId = dlFileEntry.getDataRepositoryId();

		validateFile(
			dlFileEntry.getGroupId(), newFolderId, dlFileEntry.getFileEntryId(),
			dlFileEntry.getFileName(), dlFileEntry.getTitle());

		dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(fileEntryId);

		dlFileEntry.setFolderId(newFolderId);
		dlFileEntry.setTreePath(dlFileEntry.buildTreePath());

		dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);

		// File version

		List<DLFileVersion> dlFileVersions =
			_dlFileVersionPersistence.findByFileEntryId(fileEntryId);

		for (DLFileVersion dlFileVersion : dlFileVersions) {
			dlFileVersion.setFolderId(newFolderId);
			dlFileVersion.setTreePath(dlFileVersion.buildTreePath());
			dlFileVersion.setStatusByUserId(userId);
			dlFileVersion.setStatusByUserName(user.getFullName());

			_dlFileVersionPersistence.update(dlFileVersion);
		}

		// Folder

		_dlFolderLocalService.updateLastPostDate(
			newFolderId, serviceContext.getModifiedDate(null));

		// File

		DLStoreUtil.updateFile(
			user.getCompanyId(), oldDataRepositoryId,
			dlFileEntry.getDataRepositoryId(), dlFileEntry.getName());

		return dlFileEntry;
	}

	private void _notify(long userId, int emailType, DLFileVersion fileVersion)
		throws PortalException {

		_notifySubscribers(
			userId, emailType, _buildEntryURL(fileVersion), fileVersion,
			new ServiceContext());

		_notifyOwner(
			userId, emailType, _buildEntryURL(fileVersion), fileVersion,
			new ServiceContext());
	}

	private void _notifyOwner(
			long userId, int emailType, String entryURL,
			DLFileVersion fileVersion, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(entryURL)) {
			return;
		}

		User user = _userLocalService.fetchUser(fileVersion.getUserId());

		if ((user == null) || !user.isActive()) {
			user = _userLocalService.fetchUser(userId);
		}

		DLGroupServiceSettings dlGroupServiceSettings =
			DLGroupServiceSettings.getInstance(fileVersion.getGroupId());

		if ((emailType == _EMAIL_TYPE_REVIEW) &&
			!dlGroupServiceSettings.isEmailFileEntryReviewEnabled()) {

			return;
		}

		if ((emailType == _EMAIL_TYPE_EXPIRED) &&
			!dlGroupServiceSettings.isEmailFileEntryExpiredEnabled()) {

			return;
		}

		DLFileEntry fileEntry = fileVersion.getFileEntry();

		DLFolder folder = null;

		long folderId = fileEntry.getFolderId();

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folder = fileEntry.getFolder();
		}

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		String fromName = dlGroupServiceSettings.getEmailFromName();
		String fromAddress = dlGroupServiceSettings.getEmailFromAddress();

		String toName = user.getFullName();
		String toAddress = user.getEmailAddress();

		subscriptionSender.setClassName(DLFileEntryConstants.getClassName());
		subscriptionSender.setClassPK(fileVersion.getFileEntryId());

		if (folder != null) {
			subscriptionSender.setContextAttribute(
				"[$FOLDER_NAME$]", folder.getName(), true);
		}
		else {
			subscriptionSender.setLocalizedContextAttribute(
				"[$FOLDER_NAME$]",
				new EscapableLocalizableFunction(
					locale -> LanguageUtil.get(locale, "home")));
		}

		String entryTitle = fileVersion.getTitle();

		subscriptionSender.setContextAttributes(
			"[$DOCUMENT_STATUS_BY_USER_NAME$]",
			fileVersion.getStatusByUserName(), "[$DOCUMENT_TITLE$]", entryTitle,
			"[$DOCUMENT_URL$]", entryURL);

		subscriptionSender.setContextCreatorUserPrefix("DOCUMENT");
		subscriptionSender.setCreatorUserId(fileVersion.getUserId());
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(entryTitle);
		subscriptionSender.setEntryURL(entryURL);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);

		LocalizedValuesMap subjectLocalizedValuesMap = null;
		LocalizedValuesMap bodyLocalizedValuesMap = null;

		int notificationType =
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY;

		if (emailType == _EMAIL_TYPE_REVIEW) {
			subjectLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryReviewSubject();
			bodyLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryReviewBody();

			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY;
		}
		else if (emailType == _EMAIL_TYPE_EXPIRED) {
			subjectLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryExpiredSubject();
			bodyLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryExpiredBody();

			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_EXPIRED_ENTRY;
		}
		else if (serviceContext.isCommandUpdate()) {
			subjectLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryUpdatedSubject();
			bodyLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryUpdatedBody();

			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY;
		}
		else {
			subjectLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryAddedSubject();
			bodyLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryAddedBody();
		}

		subscriptionSender.setLocalizedBodyMap(
			LocalizationUtil.getMap(bodyLocalizedValuesMap));

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getDLFileEntryType(
				fileEntry.getFileEntryTypeId());

		subscriptionSender.setLocalizedContextAttribute(
			"[$DOCUMENT_TYPE$]",
			new EscapableLocalizableFunction(dlFileEntryType::getName));

		subscriptionSender.setLocalizedSubjectMap(
			LocalizationUtil.getMap(subjectLocalizedValuesMap));
		subscriptionSender.setMailId(
			"file_entry", fileVersion.getFileEntryId());
		subscriptionSender.setNotificationType(notificationType);
		subscriptionSender.setPortletId(
			PortletProviderUtil.getPortletId(
				FileEntry.class.getName(), PortletProvider.Action.EDIT));
		subscriptionSender.setReplyToAddress(fromAddress);
		subscriptionSender.setScopeGroupId(fileVersion.getGroupId());
		subscriptionSender.setSendToCurrentUser(true);
		subscriptionSender.setServiceContext(serviceContext);

		subscriptionSender.addRuntimeSubscribers(toAddress, toName);

		subscriptionSender.flushNotificationsAsync();
	}

	private void _notifySubscribers(
			long userId, int emailType, String entryURL,
			DLFileVersion fileVersion, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(entryURL)) {
			return;
		}

		User user = _userLocalService.fetchUser(fileVersion.getUserId());

		if (user == null) {
			return;
		}

		DLGroupServiceSettings dlGroupServiceSettings =
			DLGroupServiceSettings.getInstance(fileVersion.getGroupId());

		if (Objects.equals(emailType, _EMAIL_TYPE_REVIEW) &&
			!dlGroupServiceSettings.isEmailFileEntryReviewEnabled()) {

			return;
		}

		if (Objects.equals(emailType, _EMAIL_TYPE_EXPIRED) &&
			!dlGroupServiceSettings.isEmailFileEntryExpiredEnabled()) {

			return;
		}

		String entryTitle = fileVersion.getTitle();

		String fromName = dlGroupServiceSettings.getEmailFromName();
		String fromAddress = dlGroupServiceSettings.getEmailFromAddress();

		LocalizedValuesMap subjectLocalizedValuesMap = null;
		LocalizedValuesMap bodyLocalizedValuesMap = null;

		int notificationType =
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY;

		if (Objects.equals(emailType, _EMAIL_TYPE_REVIEW)) {
			subjectLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryReviewSubject();
			bodyLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryReviewBody();

			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY;
		}
		else if (Objects.equals(emailType, _EMAIL_TYPE_EXPIRED)) {
			subjectLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryExpiredSubject();
			bodyLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryExpiredBody();

			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_EXPIRED_ENTRY;
		}
		else if (serviceContext.isCommandUpdate()) {
			subjectLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryUpdatedSubject();
			bodyLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryUpdatedBody();

			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY;
		}
		else {
			subjectLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryAddedSubject();
			bodyLocalizedValuesMap =
				dlGroupServiceSettings.getEmailFileEntryAddedBody();
		}

		DLFileEntry fileEntry = fileVersion.getFileEntry();

		DLFolder folder = null;

		long folderId = fileEntry.getFolderId();

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folder = fileEntry.getFolder();
		}

		SubscriptionSender subscriptionSender =
			new GroupSubscriptionCheckSubscriptionSender(
				DLConstants.RESOURCE_NAME);

		subscriptionSender.setClassName(DLFileEntryConstants.getClassName());
		subscriptionSender.setClassPK(fileVersion.getFileEntryId());

		if (folder != null) {
			subscriptionSender.setContextAttribute(
				"[$FOLDER_NAME$]", folder.getName(), true);
		}
		else {
			subscriptionSender.setLocalizedContextAttribute(
				"[$FOLDER_NAME$]",
				new EscapableLocalizableFunction(
					locale -> LanguageUtil.get(locale, "home")));
		}

		subscriptionSender.setContextAttributes(
			"[$DOCUMENT_STATUS_BY_USER_NAME$]",
			fileVersion.getStatusByUserName(), "[$DOCUMENT_TITLE$]", entryTitle,
			"[$DOCUMENT_URL$]", entryURL);
		subscriptionSender.setContextCreatorUserPrefix("DOCUMENT");
		subscriptionSender.setCreatorUserId(fileVersion.getUserId());
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(entryTitle);
		subscriptionSender.setEntryURL(entryURL);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setLocalizedBodyMap(
			LocalizationUtil.getMap(bodyLocalizedValuesMap));

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getDLFileEntryType(
				fileEntry.getFileEntryTypeId());

		subscriptionSender.setLocalizedContextAttribute(
			"[$DOCUMENT_TYPE$]",
			new EscapableLocalizableFunction(dlFileEntryType::getName));

		subscriptionSender.setLocalizedSubjectMap(
			LocalizationUtil.getMap(subjectLocalizedValuesMap));
		subscriptionSender.setMailId(
			"file_entry", fileVersion.getFileEntryId());
		subscriptionSender.setNotificationType(notificationType);
		subscriptionSender.setPortletId(
			PortletProviderUtil.getPortletId(
				FileEntry.class.getName(), PortletProvider.Action.EDIT));
		subscriptionSender.setReplyToAddress(fromAddress);
		subscriptionSender.setScopeGroupId(fileVersion.getGroupId());
		subscriptionSender.setServiceContext(serviceContext);

		subscriptionSender.addPersistedSubscribers(
			DLFolder.class.getName(), fileVersion.getGroupId());

		if (folder != null) {
			subscriptionSender.addPersistedSubscribers(
				DLFolder.class.getName(), folder.getFolderId());

			for (Long ancestorFolderId : folder.getAncestorFolderIds()) {
				subscriptionSender.addPersistedSubscribers(
					DLFolder.class.getName(), ancestorFolderId);
			}
		}

		if (dlFileEntryType.getFileEntryTypeId() ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			subscriptionSender.addPersistedSubscribers(
				DLFileEntryType.class.getName(), fileVersion.getGroupId());
		}
		else {
			subscriptionSender.addPersistedSubscribers(
				DLFileEntryType.class.getName(),
				dlFileEntryType.getFileEntryTypeId());
		}

		subscriptionSender.addPersistedSubscribers(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		subscriptionSender.setScopeGroupId(fileVersion.getGroupId());
		subscriptionSender.setServiceContext(serviceContext);

		subscriptionSender.flushNotificationsAsync();
	}

	private void _overwritePreviousFileVersion(
			User user, DLFileEntry dlFileEntry,
			DLFileVersion latestDLFileVersion, DLFileVersion lastDLFileVersion,
			ServiceContext serviceContext)
		throws PortalException {

		// File entry

		dlFileEntry.setModifiedDate(latestDLFileVersion.getModifiedDate());
		dlFileEntry.setFileName(latestDLFileVersion.getFileName());
		dlFileEntry.setExtension(latestDLFileVersion.getExtension());
		dlFileEntry.setMimeType(latestDLFileVersion.getMimeType());
		dlFileEntry.setTitle(latestDLFileVersion.getTitle());
		dlFileEntry.setDescription(latestDLFileVersion.getDescription());
		dlFileEntry.setExtraSettings(latestDLFileVersion.getExtraSettings());
		dlFileEntry.setFileEntryTypeId(
			latestDLFileVersion.getFileEntryTypeId());
		dlFileEntry.setSize(latestDLFileVersion.getSize());
		dlFileEntry.setDisplayDate(latestDLFileVersion.getDisplayDate());
		dlFileEntry.setExpirationDate(lastDLFileVersion.getExpirationDate());
		dlFileEntry.setReviewDate(lastDLFileVersion.getReviewDate());

		dlFileEntry = dlFileEntryPersistence.update(dlFileEntry);

		// File version

		lastDLFileVersion.setUserId(latestDLFileVersion.getUserId());
		lastDLFileVersion.setUserName(latestDLFileVersion.getUserName());
		lastDLFileVersion.setModifiedDate(
			latestDLFileVersion.getModifiedDate());
		lastDLFileVersion.setFileName(latestDLFileVersion.getFileName());
		lastDLFileVersion.setExtension(latestDLFileVersion.getExtension());
		lastDLFileVersion.setMimeType(latestDLFileVersion.getMimeType());
		lastDLFileVersion.setTitle(latestDLFileVersion.getTitle());
		lastDLFileVersion.setDescription(latestDLFileVersion.getDescription());
		lastDLFileVersion.setChangeLog(latestDLFileVersion.getChangeLog());
		lastDLFileVersion.setExtraSettings(
			latestDLFileVersion.getExtraSettings());
		lastDLFileVersion.setFileEntryTypeId(
			latestDLFileVersion.getFileEntryTypeId());
		lastDLFileVersion.setSize(latestDLFileVersion.getSize());
		lastDLFileVersion.setStoreUUID(String.valueOf(UUID.randomUUID()));
		lastDLFileVersion.setDisplayDate(latestDLFileVersion.getDisplayDate());
		lastDLFileVersion.setExpirationDate(
			latestDLFileVersion.getExpirationDate());
		lastDLFileVersion.setReviewDate(latestDLFileVersion.getReviewDate());

		ExpandoBridgeUtil.copyExpandoBridgeAttributes(
			lastDLFileVersion.getExpandoBridge(),
			latestDLFileVersion.getExpandoBridge());

		lastDLFileVersion = _dlFileVersionPersistence.update(lastDLFileVersion);

		// Metadata

		Serializable validateDDMFormValues = serviceContext.getAttribute(
			"validateDDMFormValues");

		try {
			serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);

			copyFileEntryMetadata(
				dlFileEntry.getCompanyId(), dlFileEntry.getFileEntryTypeId(),
				dlFileEntry.getFileEntryId(),
				latestDLFileVersion.getFileVersionId(),
				lastDLFileVersion.getFileVersionId(), serviceContext);
		}
		finally {
			serviceContext.setAttribute(
				"validateDDMFormValues", validateDDMFormValues);
		}

		// Asset

		AssetEntry latestDLFileVersionAssetEntry =
			_assetEntryLocalService.fetchEntry(
				DLFileEntryConstants.getClassName(),
				latestDLFileVersion.getPrimaryKey());

		if (latestDLFileVersionAssetEntry != null) {
			_assetEntryLocalService.updateEntry(
				lastDLFileVersion.getUserId(), lastDLFileVersion.getGroupId(),
				DLFileEntryConstants.getClassName(),
				lastDLFileVersion.getPrimaryKey(),
				latestDLFileVersionAssetEntry.getCategoryIds(),
				latestDLFileVersionAssetEntry.getTagNames());
		}

		// File

		DLFileVersion previousDLFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(
				dlFileEntry.getFileEntryId(), true);

		_deleteFile(
			user.getCompanyId(), dlFileEntry.getDataRepositoryId(),
			dlFileEntry.getName(), previousDLFileVersion.getStoreFileName());

		_copyFileVersion(dlFileEntry, latestDLFileVersion, lastDLFileVersion);

		// Latest file version

		_removeFileVersion(dlFileEntry, latestDLFileVersion);
	}

	private void _performDynamicQueryActions(
			Consumer<DynamicQuery> addCriteriaMethodConsumer,
			Consumer<DLFileEntry> performActionMethodConsumer)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			addCriteriaMethodConsumer::accept);
		actionableDynamicQuery.setInterval(10);
		actionableDynamicQuery.setPerformActionMethod(
			(DLFileEntry dlFileEntry) -> performActionMethodConsumer.accept(
				dlFileEntry));
		actionableDynamicQuery.setTransactionConfig(
			DefaultActionableDynamicQuery.REQUIRES_NEW_TRANSACTION_CONFIG);

		actionableDynamicQuery.performActions();
	}

	private void _publishFileEntriesByCompanyId(
			long companyId, Date displayDate, long userId,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		List<DLFileEntry> dlFileEntries = dlFileEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				DLFileEntryTable.INSTANCE
			).from(
				DLFileEntryTable.INSTANCE
			).where(
				DLFileEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					DLFileEntryTable.INSTANCE.displayDate.isNotNull()
				).and(
					DLFileEntryTable.INSTANCE.displayDate.gte(
						_dates.get(companyId))
				).and(
					DLFileEntryTable.INSTANCE.displayDate.lte(displayDate)
				)
			));

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			if (dlFileEntry.isInTrash()) {
				continue;
			}

			DLFileVersion latestFileVersion =
				_dlFileVersionLocalService.fetchLatestFileVersion(
					dlFileEntry.getFileEntryId(), false);

			if (WorkflowConstants.STATUS_SCHEDULED ==
					latestFileVersion.getStatus()) {

				updateStatus(
					userId, dlFileEntry, latestFileVersion,
					WorkflowConstants.STATUS_APPROVED, serviceContext,
					workflowContext);
			}
		}
	}

	private void _registerPWCDeletionCallback(
		DLFileEntry dlFileEntry, String storeFileName) {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				_deleteFile(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					storeFileName);

				return null;
			});
	}

	private void _reindex(DLFileEntry dlFileEntry) throws SearchException {
		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			DLFileEntry.class);

		indexer.reindex(dlFileEntry);
	}

	private void _removeFileVersion(
			DLFileEntry dlFileEntry, DLFileVersion dlFileVersion)
		throws PortalException {

		_dlFileVersionPersistence.remove(dlFileVersion);

		_expandoRowLocalService.deleteRows(dlFileVersion.getFileVersionId());

		_dlFileEntryMetadataLocalService.deleteFileVersionFileEntryMetadata(
			dlFileVersion.getFileVersionId());

		_assetEntryLocalService.deleteEntry(
			DLFileEntryConstants.getClassName(), dlFileVersion.getPrimaryKey());

		_deleteFile(
			dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
			dlFileEntry.getName(),
			DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION);

		unlockFileEntry(dlFileEntry.getFileEntryId());
	}

	private DLFileEntry _updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String extension, String mimeType, String title, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			String extraSettings, long fileEntryTypeId,
			Map<String, DDMFormValues> ddmFormValuesMap, File file,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByPrimaryKey(
			fileEntryId);

		boolean checkedOut = dlFileEntry.isCheckedOut();

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(
				fileEntryId, !checkedOut);

		boolean autoCheckIn = false;

		if (!checkedOut &&
			(dlFileVersion.isApproved() || dlFileVersion.isScheduled()) &&
			!Objects.equals(
				dlFileVersion.getUuid(),
				serviceContext.getUuidWithoutReset())) {

			autoCheckIn = true;
		}

		if (autoCheckIn) {
			if ((file != null) || (inputStream != null)) {
				dlFileEntry = _checkOutDLFileEntryModel(
					userId, fileEntryId, fileEntryTypeId, serviceContext);
			}
			else if (!CTCollectionThreadLocal.isProductionMode()) {
				DLFileVersion oldDLFileVersion =
					_dlFileVersionLocalService.getLatestFileVersion(
						fileEntryId, false);

				dlFileEntry = _checkOutDLFileEntryModel(
					userId, fileEntryId, fileEntryTypeId, serviceContext);

				_copyFileVersion(
					dlFileEntry, oldDLFileVersion,
					dlFileEntry.getLatestFileVersion(true));
			}
			else {
				dlFileEntry = checkOutFileEntry(
					userId, fileEntryId, fileEntryTypeId, serviceContext);
			}
		}

		if (!hasFileEntryLock(userId, fileEntryId, dlFileEntry.getFolderId())) {
			lockFileEntry(userId, fileEntryId);
		}

		if (checkedOut || autoCheckIn) {
			dlFileVersion = _dlFileVersionLocalService.getLatestFileVersion(
				fileEntryId, false);
		}

		try {
			if (Validator.isNull(extension)) {
				extension = dlFileEntry.getExtension();
			}

			String inputStreamExtension = null;

			if (inputStream instanceof ByteArrayFileInputStream) {
				ByteArrayFileInputStream byteArrayFileInputStream =
					(ByteArrayFileInputStream)inputStream;

				File byteArrayFileInputStreamFile =
					byteArrayFileInputStream.getFile();

				inputStreamExtension = FileUtil.getExtension(
					byteArrayFileInputStreamFile.getName());
			}

			if (Validator.isNull(mimeType)) {
				mimeType = dlFileEntry.getMimeType();
			}

			if (Validator.isNull(title)) {
				title = sourceFileName;

				if (Validator.isNull(title)) {
					title = dlFileEntry.getTitle();
				}
			}

			String fileName = DLValidatorUtil.fixName(
				DLUtil.getSanitizedFileName(title, extension));

			if (Validator.isNotNull(sourceFileName)) {
				fileName = DLUtil.getSanitizedFileName(
					FileUtil.stripExtension(sourceFileName), extension);
			}

			Date date = new Date();

			_validateFile(
				user.getCompanyId(), dlFileEntry.getGroupId(),
				dlFileEntry.getFolderId(), dlFileEntry.getFileEntryId(),
				fileEntryTypeId, fileName, extension, inputStream,
				inputStreamExtension, mimeType, title, displayDate,
				expirationDate);

			// File version

			String oldStoreFileName = dlFileVersion.getStoreFileName();
			String version = dlFileVersion.getVersion();

			if (size == 0) {
				size = dlFileVersion.getSize();
			}

			DLFileVersion updatedFileVersion = _updateFileVersion(
				user, dlFileVersion, sourceFileName, fileName, extension,
				mimeType, title, description, changeLog, extraSettings,
				fileEntryTypeId, ddmFormValuesMap, version, file, inputStream,
				size, displayDate, expirationDate, reviewDate,
				dlFileVersion.getStatus(), serviceContext.getModifiedDate(date),
				serviceContext);

			// Folder

			if (!checkedOut &&
				(dlFileEntry.getFolderId() !=
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

				_dlFolderLocalService.updateLastPostDate(
					dlFileEntry.getFolderId(),
					serviceContext.getModifiedDate(date));
			}

			// File

			if ((file != null) || (inputStream != null)) {
				_deleteFile(
					user.getCompanyId(), dlFileEntry.getDataRepositoryId(),
					dlFileEntry.getName(), oldStoreFileName);

				DLStoreRequest dlStoreRequest = DLStoreRequest.builder(
					user.getCompanyId(), dlFileEntry.getDataRepositoryId(),
					dlFileEntry.getName()
				).fileExtension(
					dlFileEntry.getExtension()
				).sourceFileName(
					sourceFileName
				).validateFileExtension(
					false
				).versionLabel(
					updatedFileVersion.getStoreFileName()
				).build();

				if (file != null) {
					DLStoreUtil.updateFile(dlStoreRequest, file);
				}
				else {
					DLStoreUtil.updateFile(dlStoreRequest, inputStream);
				}
			}

			if (autoCheckIn) {
				checkInFileEntry(
					userId, fileEntryId, dlVersionNumberIncrease, changeLog,
					serviceContext);
			}
		}
		catch (PortalException | SystemException exception1) {
			if (autoCheckIn) {
				try {
					cancelCheckOut(userId, fileEntryId);
				}
				catch (Exception exception2) {
					_log.error(exception2);
				}
			}

			throw exception1;
		}
		finally {
			if (!autoCheckIn && !checkedOut) {
				unlockFileEntry(fileEntryId);
			}
		}

		return dlFileEntryPersistence.findByPrimaryKey(fileEntryId);
	}

	private DLFileVersion _updateFileVersion(
			User user, DLFileVersion dlFileVersion, String sourceFileName,
			String fileName, String extension, String mimeType, String title,
			String description, String changeLog, String extraSettings,
			long fileEntryTypeId, Map<String, DDMFormValues> ddmFormValuesMap,
			String version, File file, InputStream inputStream, long size,
			Date displayDate, Date expirationDate, Date reviewDate, int status,
			Date statusDate, ServiceContext serviceContext)
		throws PortalException {

		dlFileVersion.setUserId(user.getUserId());
		dlFileVersion.setUserName(user.getFullName());
		dlFileVersion.setModifiedDate(statusDate);
		dlFileVersion.setFileName(fileName);

		if (Validator.isNotNull(sourceFileName)) {
			dlFileVersion.setExtension(extension);
			dlFileVersion.setMimeType(mimeType);
		}

		dlFileVersion.setTitle(title);
		dlFileVersion.setDescription(description);
		dlFileVersion.setChangeLog(changeLog);
		dlFileVersion.setExtraSettings(extraSettings);
		dlFileVersion.setFileEntryTypeId(fileEntryTypeId);
		dlFileVersion.setVersion(version);
		dlFileVersion.setSize(size);

		if ((file != null) || (inputStream != null)) {
			dlFileVersion.setStoreUUID(String.valueOf(UUID.randomUUID()));
		}

		dlFileVersion.setDisplayDate(displayDate);
		dlFileVersion.setExpirationDate(expirationDate);
		dlFileVersion.setReviewDate(reviewDate);
		dlFileVersion.setStatus(status);
		dlFileVersion.setStatusByUserId(user.getUserId());
		dlFileVersion.setStatusByUserName(user.getFullName());
		dlFileVersion.setStatusDate(statusDate);

		ExpandoBridgeUtil.setExpandoBridgeAttributes(
			dlFileVersion.getExpandoBridge(), dlFileVersion.getExpandoBridge(),
			serviceContext);

		dlFileVersion = _dlFileVersionPersistence.update(dlFileVersion);

		if ((fileEntryTypeId > 0) && (ddmFormValuesMap != null)) {
			_dlFileEntryMetadataLocalService.updateFileEntryMetadata(
				null, fileEntryTypeId, dlFileVersion.getFileEntryId(),
				dlFileVersion.getFileVersionId(), ddmFormValuesMap,
				serviceContext);
		}

		return dlFileVersion;
	}

	private DLFileVersion _updateFileVersionStatus(
			DLFileVersion dlFileVersion, int status, long userId)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		dlFileVersion.setStatus(status);

		dlFileVersion.setStatusByUserId(user.getUserId());
		dlFileVersion.setStatusByUserName(user.getFullName());
		dlFileVersion.setStatusDate(new Date());

		return _dlFileVersionPersistence.update(dlFileVersion);
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		DLFileEntry dlFileEntry = dlFileEntryPersistence.fetchByERC_G(
			externalReferenceCode, groupId);

		if (dlFileEntry != null) {
			throw new DuplicateFileEntryExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate file entry external reference code ",
					externalReferenceCode, " in group ", groupId));
		}
	}

	private void _validateFile(
			long companyId, long groupId, long folderId, long fileEntryId,
			long fileEntryTypeId, String fileName, String extension,
			InputStream inputStream, String inputStreamExtension,
			String mimeType, String title, Date displayDate,
			Date expirationDate)
		throws PortalException {

		DLValidatorUtil.validateFileName(fileName);

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getDLFileEntryType(fileEntryTypeId);

		if ((dlFileEntryType.getScope() !=
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_SYSTEM) ||
			Validator.isNotNull(extension)) {

			_validateFileExtension(fileName, extension, inputStreamExtension);
		}

		if ((dlFileEntryType.getScope() !=
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_SYSTEM) &&
			(inputStream != null) && Validator.isNotNull(mimeType)) {

			_validateFileMimeType(companyId, mimeType);
		}

		validateFile(groupId, folderId, fileEntryId, fileName, title);

		if (!ExportImportThreadLocal.isImportInProcess() &&
			(expirationDate != null) && expirationDate.before(new Date())) {

			throw new FileEntryExpirationDateException(
				"Expiration date " + expirationDate + " is in the past");
		}

		if ((displayDate != null) && (expirationDate != null) &&
			displayDate.after(expirationDate)) {

			throw new FileEntryExpirationDateException(
				StringBundler.concat(
					"Expiration date ", expirationDate,
					" is prior to display date ", displayDate));
		}
	}

	private void _validateFileEntryTypeId(
			long[] groupIds, long folderId, long fileEntryTypeId)
		throws PortalException {

		List<DLFileEntryType> dlFileEntryTypes =
			_dlFileEntryTypeLocalService.getFolderFileEntryTypes(
				groupIds, folderId, true);

		for (DLFileEntryType dlFileEntryType : dlFileEntryTypes) {
			if (dlFileEntryType.getFileEntryTypeId() == fileEntryTypeId) {
				return;
			}
		}

		throw new InvalidFileEntryTypeException(
			StringBundler.concat(
				"Invalid file entry type ", fileEntryTypeId, " for folder ",
				folderId));
	}

	private void _validateFileExtension(
			String fileName, String extension, String inputStreamExtension)
		throws PortalException {

		if (!DLAppHelperThreadLocal.isEnabled()) {
			return;
		}

		DLValidatorUtil.validateFileExtension(fileName);

		if (Validator.isNull(extension)) {
			return;
		}

		if (Validator.isNotNull(inputStreamExtension) &&
			!extension.equals(inputStreamExtension)) {

			throw new FileExtensionException.MismatchExtension();
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			DLFileEntry.class.getName(), "extension");

		if (extension.length() > maxLength) {
			throw new FileExtensionException.InvalidExtension(
				StringBundler.concat(
					extension, " of file ", fileName, " exceeds max length of ",
					maxLength));
		}
	}

	private void _validateFileMimeType(long companyId, String mimeType)
		throws PortalException {

		if (!DLAppHelperThreadLocal.isEnabled()) {
			return;
		}

		DLValidatorUtil.validateFileMimeType(companyId, mimeType);
	}

	private void _validateFolder(long groupId, long folderId, String title)
		throws PortalException {

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder parentDLFolder = _dlFolderPersistence.findByPrimaryKey(
				folderId);

			if ((groupId != parentDLFolder.getGroupId()) ||
				parentDLFolder.isInTrash()) {

				throw new NoSuchFolderException();
			}
		}

		DLFolder dlFolder = _dlFolderPersistence.fetchByG_P_N(
			groupId, folderId, title);

		if (dlFolder != null) {
			throw new DuplicateFolderNameException(
				"A folder already exists with name " + title);
		}
	}

	private static final int _EMAIL_TYPE_EXPIRED = 0;

	private static final int _EMAIL_TYPE_REVIEW = 1;

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryLocalServiceImpl.class);

	private static final Snapshot<ExpireAllVersions>
		_expireAllVersionsSnapshot = new Snapshot<>(
			DLFileEntryLocalServiceImpl.class, ExpireAllVersions.class);
	private static final Pattern _fileVersionPattern = Pattern.compile(
		"\\d+\\.\\d+");
	private static final Snapshot<TrashHelper> _trashHelperSnapshot =
		new Snapshot<>(DLFileEntryLocalServiceImpl.class, TrashHelper.class);
	private static final Snapshot<VersioningStrategy>
		_versioningStrategySnapshot = new Snapshot<>(
			DLFileEntryLocalServiceImpl.class, VersioningStrategy.class);
	private static final Snapshot<ViewCountManager> _viewCountManagerSnapshot =
		new Snapshot<>(
			DLFileEntryLocalServiceImpl.class, ViewCountManager.class);

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	private final Map<Long, Date> _dates = new ConcurrentHashMap<>();

	@BeanReference(type = DLAppHelperLocalService.class)
	private DLAppHelperLocalService _dlAppHelperLocalService;

	@BeanReference(type = DLFileEntryMetadataLocalService.class)
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@BeanReference(type = DLFileEntryMetadataPersistence.class)
	private DLFileEntryMetadataPersistence _dlFileEntryMetadataPersistence;

	@BeanReference(type = DLFileEntryTypeLocalService.class)
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@BeanReference(type = DLFileVersionLocalService.class)
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@BeanReference(type = DLFileVersionPersistence.class)
	private DLFileVersionPersistence _dlFileVersionPersistence;

	@BeanReference(type = DLFolderLocalService.class)
	private DLFolderLocalService _dlFolderLocalService;

	@BeanReference(type = DLFolderPersistence.class)
	private DLFolderPersistence _dlFolderPersistence;

	@BeanReference(type = ExpandoRowLocalService.class)
	private ExpandoRowLocalService _expandoRowLocalService;

	@BeanReference(type = ExpandoTableLocalService.class)
	private ExpandoTableLocalService _expandoTableLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = OrganizationLocalService.class)
	private OrganizationLocalService _organizationLocalService;

	@BeanReference(type = RatingsStatsLocalService.class)
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@BeanReference(type = RepositoryPersistence.class)
	private RepositoryPersistence _repositoryPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = RoleLocalService.class)
	private RoleLocalService _roleLocalService;

	@BeanReference(type = UserGroupLocalService.class)
	private UserGroupLocalService _userGroupLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

	@BeanReference(type = WebDAVPropsLocalService.class)
	private WebDAVPropsLocalService _webDAVPropsLocalService;

	@BeanReference(type = WebDAVPropsPersistence.class)
	private WebDAVPropsPersistence _webDAVPropsPersistence;

	@BeanReference(type = WorkflowInstanceLinkLocalService.class)
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}