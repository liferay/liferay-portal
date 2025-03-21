/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.webdav;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLTrashService;
import com.liferay.document.library.kernel.util.DL;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.lock.InvalidLockException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.NoSuchLockException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webdav.BaseResourceImpl;
import com.liferay.portal.kernel.webdav.Resource;
import com.liferay.portal.kernel.webdav.Status;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.webdav.BaseWebDAVStorageImpl;
import com.liferay.portal.webdav.LockException;
import com.liferay.portlet.documentlibrary.webdav.DLFileEntryResourceImpl;
import com.liferay.portlet.documentlibrary.webdav.DLWebDAVUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"webdav.storage.token=document_library"
	},
	service = WebDAVStorage.class
)
public class DLWebDAVStorageImpl extends BaseWebDAVStorageImpl {

	public static final String MS_OFFICE_2010_TEXT_XML_UTF8 =
		"text/xml; charset=\"utf-8\"";

	@Override
	public int copyCollectionResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite, long depth)
		throws WebDAVException {

		try {
			String[] destinationArray = WebDAVUtil.getPathArray(
				destination, true);

			long parentFolderId = _getParentFolderId(
				webDAVRequest.getCompanyId(), destinationArray);

			Folder folder = (Folder)resource.getModel();

			long groupId = WebDAVUtil.getGroupId(
				webDAVRequest.getCompanyId(), destination);
			String name = WebDAVUtil.getResourceName(destinationArray);
			String description = folder.getDescription();

			ServiceContext serviceContext = _getServiceContext(
				DLFolder.class.getName(), webDAVRequest);

			int status = HttpServletResponse.SC_CREATED;

			if (overwrite &&
				deleteResource(
					groupId, parentFolderId, name,
					webDAVRequest.getLockUuid())) {

				status = HttpServletResponse.SC_NO_CONTENT;
			}

			if (depth == 0) {
				_dlAppService.addFolder(
					null, groupId, parentFolderId, name, description,
					serviceContext);
			}
			else {
				_dlAppService.copyFolder(
					groupId, folder.getFolderId(), parentFolderId, name,
					description, serviceContext);
			}

			return status;
		}
		catch (DuplicateFolderNameException duplicateFolderNameException) {
			if (_log.isDebugEnabled()) {
				_log.debug(duplicateFolderNameException);
			}

			return HttpServletResponse.SC_PRECONDITION_FAILED;
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}

			return HttpServletResponse.SC_CONFLICT;
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public int copySimpleResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite)
		throws WebDAVException {

		File file = null;

		try {
			String[] destinationArray = WebDAVUtil.getPathArray(
				destination, true);

			long parentFolderId = _getParentFolderId(
				webDAVRequest.getCompanyId(), destinationArray);

			long groupId = WebDAVUtil.getGroupId(
				webDAVRequest.getCompanyId(), destination);
			String fileName = _getFileName(destinationArray);

			FileEntry fileEntry = (FileEntry)resource.getModel();

			InputStream inputStream = fileEntry.getContentStream();

			file = FileUtil.createTempFile(inputStream);

			ServiceContext serviceContext = _getServiceContext(
				DLFileEntry.class.getName(), webDAVRequest);

			int status = HttpServletResponse.SC_CREATED;

			if (overwrite &&
				deleteResource(
					groupId, parentFolderId, fileName,
					webDAVRequest.getLockUuid())) {

				status = HttpServletResponse.SC_NO_CONTENT;
			}

			_dlAppService.addFileEntry(
				null, groupId, parentFolderId, fileName,
				fileEntry.getMimeType(), FileUtil.stripExtension(fileName),
				StringPool.BLANK, fileEntry.getDescription(), StringPool.BLANK,
				file, null, null, null, serviceContext);

			return status;
		}
		catch (DuplicateFileEntryException | DuplicateFolderNameException
					exception) {

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return HttpServletResponse.SC_PRECONDITION_FAILED;
		}
		catch (LockException lockException) {
			if (_log.isDebugEnabled()) {
				_log.debug(lockException);
			}

			return WebDAVUtil.SC_LOCKED;
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}

			return HttpServletResponse.SC_CONFLICT;
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public int deleteResource(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		try {
			Resource resource = getResource(webDAVRequest);

			if (resource == null) {
				if (webDAVRequest.isAppleDoubleRequest()) {
					return HttpServletResponse.SC_NO_CONTENT;
				}

				return HttpServletResponse.SC_NOT_FOUND;
			}

			Object model = resource.getModel();

			if (model instanceof Folder) {
				Folder folder = (Folder)model;

				if (folder.isRepositoryCapabilityProvided(
						TrashCapability.class) &&
					_dlTrashHelper.isTrashEnabled(
						folder.getGroupId(), folder.getRepositoryId())) {

					_dlTrashService.moveFolderToTrash(folder.getFolderId());
				}
				else {
					_dlAppService.deleteFolder(folder.getFolderId());
				}
			}
			else {
				FileEntry fileEntry = (FileEntry)model;

				if (!_hasLock(fileEntry, webDAVRequest.getLockUuid()) &&
					(fileEntry.getLock() != null)) {

					return WebDAVUtil.SC_LOCKED;
				}

				if (fileEntry.isRepositoryCapabilityProvided(
						TrashCapability.class) &&
					_dlTrashHelper.isTrashEnabled(
						fileEntry.getGroupId(), fileEntry.getRepositoryId())) {

					_dlTrashService.moveFileEntryToTrash(
						fileEntry.getFileEntryId());
				}
				else {
					_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());
				}
			}

			return HttpServletResponse.SC_NO_CONTENT;
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public Resource getResource(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		try {
			String[] pathArray = webDAVRequest.getPathArray();

			long parentFolderId = _getParentFolderId(
				webDAVRequest.getCompanyId(), pathArray);

			String name = WebDAVUtil.getResourceName(pathArray);

			if (Validator.isNull(name)) {
				String path = getRootPath() + webDAVRequest.getPath();

				return new BaseResourceImpl(path, StringPool.BLANK, getToken());
			}

			try {
				Folder folder = _dlAppService.getFolder(
					webDAVRequest.getGroupId(), parentFolderId, name);

				if ((folder.getParentFolderId() != parentFolderId) ||
					(webDAVRequest.getGroupId() != folder.getRepositoryId())) {

					throw new NoSuchFolderException(
						StringBundler.concat(
							"No DLFolder exists with the key ",
							"{parendFolderId=", parentFolderId,
							", repositoryId=", webDAVRequest.getGroupId(),
							StringPool.CLOSE_CURLY_BRACE));
				}

				return _toResource(webDAVRequest, folder, false);
			}
			catch (NoSuchFolderException noSuchFolderException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFolderException);
				}

				try {
					FileEntry fileEntry = _getFileEntryByFileName(
						webDAVRequest.getGroupId(), parentFolderId,
						_getFileName(pathArray));

					return _toResource(webDAVRequest, fileEntry, false);
				}
				catch (NoSuchFileEntryException noSuchFileEntryException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchFileEntryException);
					}

					return null;
				}
			}
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public List<Resource> getResources(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		try {
			long folderId = _getFolderId(
				webDAVRequest.getCompanyId(), webDAVRequest.getPathArray());

			List<Resource> folders = _getFolders(webDAVRequest, folderId);
			List<Resource> fileEntries = _getFileEntries(
				webDAVRequest, folderId);

			List<Resource> resources = new ArrayList<>(
				folders.size() + fileEntries.size());

			resources.addAll(folders);
			resources.addAll(fileEntries);

			return resources;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public boolean isSupportsClassTwo() {
		return true;
	}

	@Override
	public Status lockResource(
			WebDAVRequest webDAVRequest, String owner, long timeout)
		throws WebDAVException {

		Resource resource = getResource(webDAVRequest);

		Lock lock = null;
		int status = HttpServletResponse.SC_OK;

		try {
			if (resource == null) {
				status = HttpServletResponse.SC_CREATED;

				String[] pathArray = webDAVRequest.getPathArray();

				long parentFolderId = _getParentFolderId(
					webDAVRequest.getCompanyId(), pathArray);

				String fileName = _getFileName(pathArray);

				String contentType = _getContentType(
					webDAVRequest.getHttpServletRequest(), null, fileName);

				File file = FileUtil.createTempFile(
					FileUtil.getExtension(fileName));

				file.createNewFile();

				ServiceContext serviceContext = _getServiceContext(
					DLFileEntry.class.getName(), webDAVRequest);

				String title = FileUtil.stripExtension(fileName);

				FileEntry fileEntry = _dlAppService.addFileEntry(
					null, webDAVRequest.getGroupId(), parentFolderId, fileName,
					contentType, title, StringPool.BLANK, StringPool.BLANK,
					StringPool.BLANK, file, null, null, null, serviceContext);

				resource = _toResource(webDAVRequest, fileEntry, false);
			}

			if (!resource.isCollection()) {
				FileEntry fileEntry = (FileEntry)resource.getModel();

				ServiceContext serviceContext = _getServiceContext(
					DLFileEntry.class.getName(), webDAVRequest);

				serviceContext.setAttribute(
					DL.MANUAL_CHECK_IN_REQUIRED,
					webDAVRequest.isManualCheckInRequired());

				_populateServiceContext(serviceContext, fileEntry);

				_dlAppService.checkOutFileEntry(
					fileEntry.getFileEntryId(), owner, timeout, serviceContext);

				lock = fileEntry.getLock();
			}
			else {
				boolean inheritable = false;

				long depth = WebDAVUtil.getDepth(
					webDAVRequest.getHttpServletRequest());

				if (depth != 0) {
					inheritable = true;
				}

				Folder folder = (Folder)resource.getModel();

				lock = _dlAppService.lockFolder(
					folder.getRepositoryId(), folder.getFolderId(), owner,
					inheritable, timeout);
			}
		}
		catch (Exception exception) {

			// DuplicateLock is 423 not 501

			if (!(exception instanceof DuplicateLockException)) {
				throw new WebDAVException(exception);
			}

			status = WebDAVUtil.SC_LOCKED;
		}

		return new Status(lock, status);
	}

	@Override
	public Status makeCollection(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		try {
			HttpServletRequest httpServletRequest =
				webDAVRequest.getHttpServletRequest();

			if (httpServletRequest.getContentLength() > 0) {
				return new Status(
					HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			}

			String[] pathArray = webDAVRequest.getPathArray();

			long parentFolderId = _getParentFolderId(
				webDAVRequest.getCompanyId(), pathArray);
			String name = WebDAVUtil.getResourceName(pathArray);

			ServiceContext serviceContext = _getServiceContext(
				DLFolder.class.getName(), webDAVRequest);

			_dlAppService.addFolder(
				null, webDAVRequest.getGroupId(), parentFolderId, name,
				StringPool.BLANK, serviceContext);

			String location = StringUtil.merge(pathArray, StringPool.SLASH);

			return new Status(location, HttpServletResponse.SC_CREATED);
		}
		catch (DuplicateFileEntryException | DuplicateFolderNameException
					exception) {

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return new Status(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}

			return new Status(HttpServletResponse.SC_CONFLICT);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			return new Status(HttpServletResponse.SC_FORBIDDEN);
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public int moveCollectionResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite)
		throws WebDAVException {

		try {
			String[] destinationArray = WebDAVUtil.getPathArray(
				destination, true);

			Folder folder = (Folder)resource.getModel();

			long groupId = WebDAVUtil.getGroupId(
				webDAVRequest.getCompanyId(), destinationArray);

			long parentFolderId = _getParentFolderId(
				webDAVRequest.getCompanyId(), destinationArray);
			String name = WebDAVUtil.getResourceName(destinationArray);

			ServiceContext serviceContext = _getServiceContext(
				DLFolder.class.getName(), webDAVRequest);

			serviceContext.setUserId(webDAVRequest.getUserId());

			int status = HttpServletResponse.SC_CREATED;

			if (overwrite &&
				deleteResource(
					groupId, parentFolderId, name,
					webDAVRequest.getLockUuid())) {

				status = HttpServletResponse.SC_NO_CONTENT;
			}

			if (parentFolderId != folder.getParentFolderId()) {
				_dlAppService.moveFolder(
					folder.getFolderId(), parentFolderId, serviceContext);
			}

			if (!name.equals(folder.getName())) {
				_dlAppService.updateFolder(
					folder.getFolderId(), name, folder.getDescription(),
					serviceContext);
			}

			return status;
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (DuplicateFolderNameException duplicateFolderNameException) {
			if (_log.isDebugEnabled()) {
				_log.debug(duplicateFolderNameException);
			}

			return HttpServletResponse.SC_PRECONDITION_FAILED;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public int moveSimpleResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite)
		throws WebDAVException {

		File file = null;

		try {
			FileEntry fileEntry = (FileEntry)resource.getModel();

			if (!_hasLock(fileEntry, webDAVRequest.getLockUuid()) &&
				(fileEntry.getLock() != null)) {

				return WebDAVUtil.SC_LOCKED;
			}

			String[] destinationArray = WebDAVUtil.getPathArray(
				destination, true);

			long groupId = WebDAVUtil.getGroupId(
				webDAVRequest.getCompanyId(), destinationArray);
			long newParentFolderId = _getParentFolderId(
				webDAVRequest.getCompanyId(), destinationArray);
			String fileName = _getFileName(destinationArray);

			ServiceContext serviceContext = _getServiceContext(
				DLFileEntry.class.getName(), webDAVRequest);

			_populateServiceContext(serviceContext, fileEntry);

			int status = HttpServletResponse.SC_CREATED;

			if (overwrite &&
				deleteResource(
					groupId, newParentFolderId, fileName,
					webDAVRequest.getLockUuid())) {

				status = HttpServletResponse.SC_NO_CONTENT;
			}

			// LPS-5415

			if (webDAVRequest.isMac()) {
				try {
					FileEntry destFileEntry = _getFileEntryByFileName(
						groupId, newParentFolderId, fileName);

					InputStream inputStream = fileEntry.getContentStream();

					file = FileUtil.createTempFile(inputStream);

					_populateServiceContext(serviceContext, destFileEntry);

					_dlAppService.updateFileEntry(
						destFileEntry.getFileEntryId(),
						destFileEntry.getFileName(),
						destFileEntry.getMimeType(), destFileEntry.getTitle(),
						destFileEntry.getTitle(),
						destFileEntry.getDescription(), StringPool.BLANK,
						DLVersionNumberIncrease.MINOR, file,
						destFileEntry.getDisplayDate(),
						destFileEntry.getExpirationDate(),
						destFileEntry.getReviewDate(), serviceContext);

					_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());

					return status;
				}
				catch (NoSuchFileEntryException noSuchFileEntryException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchFileEntryException);
					}
				}
			}

			_populateServiceContext(serviceContext, fileEntry);

			_dlAppService.updateFileEntry(
				fileEntry.getFileEntryId(), fileName, fileEntry.getMimeType(),
				fileEntry.getTitle(), fileEntry.getTitle(),
				fileEntry.getDescription(), StringPool.BLANK,
				DLVersionNumberIncrease.MINOR, file, fileEntry.getDisplayDate(),
				fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
				serviceContext);

			if (fileEntry.getFolderId() != newParentFolderId) {
				_dlAppService.moveFileEntry(
					fileEntry.getFileEntryId(), newParentFolderId,
					serviceContext);
			}

			return status;
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (DuplicateFileEntryException | DuplicateFolderNameException
					exception) {

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return HttpServletResponse.SC_PRECONDITION_FAILED;
		}
		catch (LockException lockException) {
			if (_log.isDebugEnabled()) {
				_log.debug(lockException);
			}

			return WebDAVUtil.SC_LOCKED;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public int putResource(WebDAVRequest webDAVRequest) throws WebDAVException {
		File file = null;

		try {
			HttpServletRequest httpServletRequest =
				webDAVRequest.getHttpServletRequest();

			String[] pathArray = webDAVRequest.getPathArray();

			long parentFolderId = _getParentFolderId(
				webDAVRequest.getCompanyId(), pathArray);
			String fileName = _getFileName(pathArray);

			ServiceContext serviceContext = _getServiceContext(
				DLFileEntry.class.getName(), webDAVRequest);

			file = FileUtil.createTempFile(FileUtil.getExtension(fileName));

			FileUtil.write(file, httpServletRequest.getInputStream());

			String contentType = _getContentType(
				httpServletRequest, file, fileName);

			try {
				FileEntry fileEntry = _getFileEntryByFileName(
					webDAVRequest.getGroupId(), parentFolderId, fileName);

				if (!_hasLock(fileEntry, webDAVRequest.getLockUuid()) &&
					(fileEntry.getLock() != null)) {

					return WebDAVUtil.SC_LOCKED;
				}

				_populateServiceContext(serviceContext, fileEntry);

				serviceContext.setCommand(Constants.UPDATE_WEBDAV);

				_dlAppService.updateFileEntry(
					fileEntry.getFileEntryId(), fileName, contentType,
					fileEntry.getTitle(), fileEntry.getTitle(),
					fileEntry.getDescription(), StringPool.BLANK,
					DLVersionNumberIncrease.MINOR, file,
					fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
					fileEntry.getReviewDate(), serviceContext);
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileEntryException);
				}

				serviceContext.setCommand(Constants.ADD_WEBDAV);

				String title = FileUtil.stripExtension(fileName);

				_dlAppService.addFileEntry(
					null, webDAVRequest.getGroupId(), parentFolderId, fileName,
					contentType, title, StringPool.BLANK, StringPool.BLANK,
					StringPool.BLANK, file, null, null, null, serviceContext);
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Added " + StringUtil.merge(pathArray, StringPool.SLASH));
			}

			return HttpServletResponse.SC_CREATED;
		}
		catch (FileSizeException fileSizeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(fileSizeException);
			}

			return HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}

			return HttpServletResponse.SC_CONFLICT;
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return HttpServletResponse.SC_CONFLICT;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public Lock refreshResourceLock(
			WebDAVRequest webDAVRequest, String uuid, long timeout)
		throws WebDAVException {

		try {
			Resource resource = getResource(webDAVRequest);

			if (!resource.isCollection()) {
				return _dlAppService.refreshFileEntryLock(
					uuid, webDAVRequest.getCompanyId(), timeout);
			}

			return _dlAppService.refreshFolderLock(
				uuid, webDAVRequest.getCompanyId(), timeout);
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public boolean unlockResource(WebDAVRequest webDAVRequest, String token)
		throws WebDAVException {

		Resource resource = getResource(webDAVRequest);

		try {
			if (!resource.isCollection()) {
				FileEntry fileEntry = (FileEntry)resource.getModel();

				// Do not allow WebDAV to check in a file entry if it requires a
				// manual check in

				if (fileEntry.isManualCheckInRequired()) {
					return false;
				}

				ServiceContext serviceContext = _getServiceContext(
					DLFileEntry.class.getName(), webDAVRequest);

				serviceContext.setAttribute(
					DL.WEBDAV_CHECK_IN_MODE, Boolean.TRUE);

				_dlAppService.checkInFileEntry(
					fileEntry.getFileEntryId(), token, serviceContext);

				if (webDAVRequest.isAppleDoubleRequest()) {
					_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());
				}
			}
			else {
				Folder folder = (Folder)resource.getModel();

				_dlAppService.unlockFolder(
					folder.getRepositoryId(), folder.getParentFolderId(),
					folder.getName(), token);
			}

			return true;
		}
		catch (InvalidLockException invalidLockException) {
			if (_log.isWarnEnabled()) {
				_log.warn(invalidLockException);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to unlock file entry", exception);
			}
		}

		return false;
	}

	protected boolean deleteResource(
			long groupId, long parentFolderId, String name, String lockUuid)
		throws Exception {

		try {
			Folder folder = _dlAppService.getFolder(
				groupId, parentFolderId, name);

			_dlAppService.deleteFolder(folder.getFolderId());

			return true;
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}

			try {
				FileEntry fileEntry = _getFileEntryByFileName(
					groupId, parentFolderId, name);

				if (!_hasLock(fileEntry, lockUuid) &&
					(fileEntry.getLock() != null)) {

					throw new LockException(
						StringBundler.concat(
							"Inconsistent file lock state for file entry ",
							fileEntry.getPrimaryKey(), " and lock UUID ",
							lockUuid));
				}

				_dlAppService.deleteFileEntryByTitle(
					groupId, parentFolderId, fileEntry.getTitle());

				return true;
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileEntryException);
				}
			}
		}

		return false;
	}

	private String _getContentType(
		HttpServletRequest httpServletRequest, File file, String title) {

		String contentType = GetterUtil.getString(
			httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE),
			ContentTypes.APPLICATION_OCTET_STREAM);

		if (contentType.equals(ContentTypes.APPLICATION_OCTET_STREAM) ||
			contentType.equals(MS_OFFICE_2010_TEXT_XML_UTF8)) {

			contentType = MimeTypesUtil.getContentType(file, title);
		}

		return contentType;
	}

	private List<Resource> _getFileEntries(
			WebDAVRequest webDAVRequest, long parentFolderId)
		throws Exception {

		return TransformUtil.transform(
			_dlAppService.getFileEntries(
				webDAVRequest.getGroupId(), parentFolderId),
			fileEntry -> {
				if (DLWebDAVUtil.isRepresentableTitle(
						fileEntry.getFileName())) {

					return _toResource(webDAVRequest, fileEntry, true);
				}

				_log.error(
					"Unrepresentable WebDAV title for file name " +
						fileEntry.getFileName());

				return null;
			});
	}

	private FileEntry _getFileEntryByFileName(
			long groupId, long parentFolderId, String fileName)
		throws PortalException {

		try {
			return _dlAppService.getFileEntryByFileName(
				groupId, parentFolderId, fileName);
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileEntryException);
			}

			return _dlAppService.getFileEntry(
				groupId, parentFolderId, fileName);
		}
	}

	private String _getFileName(String[] pathArray) {
		return DLWebDAVUtil.unescapeRawTitle(
			WebDAVUtil.getResourceName(pathArray));
	}

	private long _getFolderId(long companyId, String[] pathArray)
		throws Exception {

		return _getFolderId(companyId, pathArray, false);
	}

	private long _getFolderId(
			long companyId, String[] pathArray, boolean parent)
		throws Exception {

		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (pathArray.length <= 1) {
			return folderId;
		}

		long groupId = WebDAVUtil.getGroupId(companyId, pathArray);

		int x = pathArray.length;

		if (parent) {
			x--;
		}

		for (int i = 2; i < x; i++) {
			String name = pathArray[i];

			Folder folder = _dlAppService.getFolder(groupId, folderId, name);

			if (groupId == folder.getRepositoryId()) {
				folderId = folder.getFolderId();
			}
		}

		return folderId;
	}

	private List<Resource> _getFolders(
			WebDAVRequest webDAVRequest, long parentFolderId)
		throws Exception {

		return TransformUtil.transform(
			_dlAppService.getFolders(
				webDAVRequest.getGroupId(), parentFolderId, false),
			folder -> _toResource(webDAVRequest, folder, true));
	}

	private long _getParentFolderId(long companyId, String[] pathArray)
		throws Exception {

		return _getFolderId(companyId, pathArray, true);
	}

	private ServiceContext _getServiceContext(
			String className, WebDAVRequest webDAVRequest)
		throws PortalException {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			className, webDAVRequest.getHttpServletRequest());

		serviceContext.setModelPermissions(null);

		serviceContext.deriveDefaultPermissions(
			webDAVRequest.getGroupId(), className);

		return serviceContext;
	}

	private boolean _hasLock(FileEntry fileEntry, String lockUuid)
		throws Exception {

		if (Validator.isNull(lockUuid)) {

			// Client does not claim to know of a lock

			return fileEntry.hasLock();
		}

		// Client claims to know of a lock. Verify the lock UUID.

		try {
			return _dlAppService.verifyFileEntryLock(
				fileEntry.getRepositoryId(), fileEntry.getFileEntryId(),
				lockUuid);
		}
		catch (NoSuchLockException noSuchLockException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLockException);
			}

			return false;
		}
	}

	private void _populateServiceContext(
			ServiceContext serviceContext, FileEntry fileEntry)
		throws PortalException {

		serviceContext.setScopeGroupId(fileEntry.getGroupId());

		String className = DLFileEntryConstants.getClassName();

		serviceContext.setAssetCategoryIds(
			_assetCategoryLocalService.getCategoryIds(
				className, fileEntry.getFileEntryId()));

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, fileEntry.getFileEntryId());

		List<AssetLink> assetLinks = _assetLinkLocalService.getLinks(
			assetEntry.getEntryId());

		serviceContext.setAssetLinkEntryIds(
			ListUtil.toLongArray(assetLinks, AssetLink.ENTRY_ID2_ACCESSOR));

		serviceContext.setAssetTagNames(
			_assetTagLocalService.getTagNames(
				className, fileEntry.getFileEntryId()));

		ExpandoBridge expandoBridge = fileEntry.getExpandoBridge();

		serviceContext.setExpandoBridgeAttributes(
			expandoBridge.getAttributes(false));

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		long fileEntryTypeId = dlFileEntry.getFileEntryTypeId();

		if (fileEntryTypeId <= 0) {
			return;
		}

		serviceContext.setAttribute(
			"fileEntryTypeId", dlFileEntry.getFileEntryTypeId());

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(
				fileEntry.getFileEntryId(), !dlFileEntry.isCheckedOut());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getFileEntryType(fileEntryTypeId);

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		for (DDMStructure ddmStructure : ddmStructures) {
			DLFileEntryMetadata dlFileEntryMetadata =
				_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
					ddmStructure.getStructureId(),
					dlFileVersion.getFileVersionId());

			if (dlFileEntryMetadata == null) {
				continue;
			}

			serviceContext.setAttribute(
				DDMFormValues.class.getName() + StringPool.POUND +
					ddmStructure.getStructureId(),
				_ddmBeanTranslator.translate(
					_ddmStorageEngineManager.getDDMFormValues(
						dlFileEntryMetadata.getDDMStorageId())));
		}
	}

	private Resource _toResource(
		WebDAVRequest webDAVRequest, FileEntry fileEntry, boolean appendPath) {

		return new DLFileEntryResourceImpl(
			webDAVRequest, fileEntry, appendPath);
	}

	private Resource _toResource(
		WebDAVRequest webDAVRequest, Folder folder, boolean appendPath) {

		String parentPath = getRootPath() + webDAVRequest.getPath();

		String name = StringPool.BLANK;

		if (appendPath) {
			name = folder.getName();
		}

		Resource resource = new BaseResourceImpl(
			parentPath, name, folder.getName(), folder.getCreateDate(),
			folder.getModifiedDate());

		resource.setModel(folder);
		resource.setClassName(Folder.class.getName());
		resource.setPrimaryKey(folder.getPrimaryKey());

		return resource;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLWebDAVStorageImpl.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DDMBeanTranslator _ddmBeanTranslator;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLTrashHelper _dlTrashHelper;

	@Reference
	private DLTrashService _dlTrashService;

}