/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.repository;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.repository.DispatchFileRepository;
import com.liferay.dispatch.repository.DispatchFileValidator;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProvider;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.repository.portletrepository.PortletRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
@Component(service = DispatchFileRepository.class)
public class DispatchFileRepositoryImpl implements DispatchFileRepository {

	@Override
	public FileEntry addFileEntry(
			long userId, long dispatchTriggerId, String fileName, long size,
			String contentType, InputStream inputStream)
		throws PortalException {

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.getDispatchTrigger(dispatchTriggerId);

		DispatchFileValidator dispatchFileValidator = _getDispatchFileValidator(
			dispatchTrigger.getDispatchTaskExecutorType());

		dispatchFileValidator.validateExtension(fileName);
		dispatchFileValidator.validateSize(size);

		Company company = _companyLocalService.getCompany(
			dispatchTrigger.getCompanyId());

		return _addFileEntry(
			company.getGroupId(), userId, dispatchTriggerId, contentType,
			inputStream);
	}

	@Override
	public FileEntry fetchFileEntry(long dispatchTriggerId) {
		try {
			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.getDispatchTrigger(
					dispatchTriggerId);

			Company company = _companyLocalService.getCompany(
				dispatchTrigger.getCompanyId());

			Folder folder = _getFolder(
				company.getGroupId(), dispatchTrigger.getUserId());

			LocalRepository localRepository =
				_repositoryProvider.getLocalRepository(
					folder.getRepositoryId());

			return localRepository.fetchFileEntry(
				folder.getFolderId(), String.valueOf(dispatchTriggerId));
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to fetch file entry", portalException);
			}

			return null;
		}
	}

	@Override
	public String fetchFileEntryName(long dispatchTriggerId) {
		FileEntry fileEntry = fetchFileEntry(dispatchTriggerId);

		if (fileEntry != null) {
			return fileEntry.getFileName();
		}

		return null;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DispatchFileValidator.class,
			"dispatch.file.validator.type");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private FileEntry _addFileEntry(
			long groupId, long userId, long dispatchTriggerId,
			String contentType, InputStream inputStream)
		throws PortalException {

		Folder folder = _getFolder(groupId, userId);

		LocalRepository localRepository =
			_repositoryProvider.getLocalRepository(folder.getRepositoryId());

		String title = String.valueOf(dispatchTriggerId);

		FileEntry fileEntry = localRepository.fetchFileEntry(
			folder.getFolderId(), title);

		if (fileEntry != null) {
			try (SafeCloseable safeCloseable =
					DLAppHelperThreadLocal.setEnabledWithSafeCloseable(false)) {

				SystemEventHierarchyEntryThreadLocal.push(FileEntry.class);

				localRepository.deleteFileEntry(fileEntry.getFileEntryId());
			}
			finally {
				SystemEventHierarchyEntryThreadLocal.pop(FileEntry.class);
			}
		}

		File file = null;

		try (SafeCloseable safeCloseable =
				DLAppHelperThreadLocal.setEnabledWithSafeCloseable(false)) {

			file = FileUtil.createTempFile(inputStream);

			return localRepository.addFileEntry(
				null, userId, folder.getFolderId(), title, contentType, title,
				title, StringPool.BLANK, StringPool.BLANK, file, null, null,
				null,
				_createServiceContext(
					DispatchTrigger.class, dispatchTriggerId));
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to write temporary file", ioException);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	private ServiceContext _createServiceContext(Class<?> clazz, long classPK) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		if (clazz != null) {
			serviceContext.setAttribute("className", clazz.getName());
			serviceContext.setAttribute("classPK", String.valueOf(classPK));
		}

		serviceContext.setIndexingEnabled(false);

		return serviceContext;
	}

	private DispatchFileValidator _getDispatchFileValidator(
		String dispatchTaskExecutorType) {

		if (_serviceTrackerMap.containsKey(dispatchTaskExecutorType)) {
			return _serviceTrackerMap.getService(dispatchTaskExecutorType);
		}

		return _serviceTrackerMap.getService("default");
	}

	private Folder _getFolder(long groupId, long userId)
		throws PortalException {

		Repository repository = _getRepository(groupId, userId);

		LocalRepository localRepository =
			_repositoryProvider.getLocalRepository(
				repository.getRepositoryId());

		try (SafeCloseable safeCloseable =
				DLAppHelperThreadLocal.setEnabledWithSafeCloseable(false)) {

			return localRepository.getFolder(
				DispatchConstants.REPOSITORY_DEFAULT_PARENT_FOLDER_ID,
				DispatchConstants.REPOSITORY_FOLDER_NAME);
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}

			Group group = _groupLocalService.getGroup(groupId);

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						group.getCtCollectionId())) {

				return localRepository.addFolder(
					null, userId,
					DispatchConstants.REPOSITORY_DEFAULT_PARENT_FOLDER_ID,
					DispatchConstants.REPOSITORY_FOLDER_NAME, StringPool.BLANK,
					_createServiceContext(null, 0L));
			}
		}
	}

	private Repository _getRepository(long groupId, long userId)
		throws PortalException {

		Repository repository = _repositoryLocalService.fetchRepository(
			groupId, DispatchPortletKeys.DISPATCH);

		if (repository != null) {
			return repository;
		}

		Group group = _groupLocalService.getGroup(groupId);

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					group.getCtCollectionId());
			SafeCloseable safeCloseable2 =
				DLAppHelperThreadLocal.setEnabledWithSafeCloseable(false)) {

			return _repositoryLocalService.addRepository(
				null, userId, groupId,
				_portal.getClassNameId(PortletRepository.class.getName()),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				DispatchPortletKeys.DISPATCH, StringPool.BLANK,
				DispatchPortletKeys.DISPATCH, new UnicodeProperties(), true,
				_createServiceContext(null, 0L));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DispatchFileRepositoryImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private RepositoryProvider _repositoryProvider;

	private ServiceTrackerMap<String, DispatchFileValidator> _serviceTrackerMap;

}