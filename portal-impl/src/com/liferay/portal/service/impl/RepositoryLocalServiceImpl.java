/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.document.library.kernel.exception.RepositoryNameException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.InvalidRepositoryException;
import com.liferay.portal.kernel.exception.NoSuchRepositoryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.InvalidRepositoryIdException;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.repository.RepositoryFactoryUtil;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.RepositoryEventTriggerCapability;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.RepositoryEntryPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.base.RepositoryLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Alexander Chow
 */
public class RepositoryLocalServiceImpl extends RepositoryLocalServiceBaseImpl {

	@Override
	public Repository addRepository(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long parentFolderId, String name,
			String description, String portletId,
			UnicodeProperties typeSettingsUnicodeProperties, boolean hidden,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		long repositoryId = counterLocalService.increment();

		Repository repository = repositoryPersistence.create(repositoryId);

		repository.setUuid(serviceContext.getUuid());
		repository.setExternalReferenceCode(externalReferenceCode);
		repository.setGroupId(groupId);
		repository.setCompanyId(user.getCompanyId());
		repository.setUserId(user.getUserId());
		repository.setUserName(user.getFullName());
		repository.setClassNameId(classNameId);
		repository.setName(name);
		repository.setDescription(description);
		repository.setPortletId(portletId);
		repository.setTypeSettingsProperties(typeSettingsUnicodeProperties);
		repository.setDlFolderId(
			getDLFolderId(
				user, groupId, repositoryId, parentFolderId, name, description,
				hidden, serviceContext));

		repository = repositoryPersistence.update(repository);

		try {
			RepositoryFactoryUtil.createRepository(repositoryId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new InvalidRepositoryException(exception);
		}

		return repository;
	}

	@Override
	public void checkRepository(long repositoryId) {
		Group group = _groupPersistence.fetchByPrimaryKey(repositoryId);

		if (group != null) {
			return;
		}

		try {
			repositoryPersistence.findByPrimaryKey(repositoryId);
		}
		catch (NoSuchRepositoryException noSuchRepositoryException) {
			throw new InvalidRepositoryIdException(
				noSuchRepositoryException.getMessage());
		}
	}

	@Override
	public void deleteRepositories(long groupId) throws PortalException {
		List<Repository> repositories = repositoryPersistence.findByGroupId(
			groupId);

		for (Repository repository : repositories) {
			deleteRepository(repository.getRepositoryId());
		}
	}

	@Override
	public Repository deleteRepository(long repositoryId)
		throws PortalException {

		Repository repository = repositoryPersistence.fetchByPrimaryKey(
			repositoryId);

		if (repository == null) {
			return null;
		}

		try {
			LocalRepository localRepository =
				RepositoryProviderUtil.getLocalRepository(repositoryId);

			if (localRepository.isCapabilityProvided(
					RepositoryEventTriggerCapability.class)) {

				RepositoryEventTriggerCapability
					repositoryEventTriggerCapability =
						localRepository.getCapability(
							RepositoryEventTriggerCapability.class);

				repositoryEventTriggerCapability.trigger(
					RepositoryEventType.Delete.class, LocalRepository.class,
					localRepository);
			}
		}
		catch (RepositoryException repositoryException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Repository deletion events for this repository will not " +
						"be triggered",
					repositoryException);
			}
		}

		return repositoryLocalService.deleteRepository(repository);
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public Repository deleteRepository(Repository repository)
		throws PortalException {

		_expandoValueLocalService.deleteValues(
			Repository.class.getName(), repository.getRepositoryId());

		DLFolder dlFolder = _dlFolderLocalService.fetchDLFolder(
			repository.getDlFolderId());

		if (dlFolder != null) {
			_dlFolderLocalService.deleteFolder(dlFolder);
		}

		repositoryPersistence.remove(repository);

		_repositoryEntryPersistence.removeByRepositoryId(
			repository.getRepositoryId());

		return repository;
	}

	@Override
	public Repository fetchRepository(long groupId, String portletId) {
		return fetchRepository(groupId, portletId, portletId);
	}

	@Override
	public Repository fetchRepository(
		long groupId, String name, String portletId) {

		return repositoryPersistence.fetchByG_N_P(groupId, name, portletId);
	}

	@Override
	public List<Repository> getGroupRepositories(long groupId) {
		return repositoryPersistence.findByGroupId(groupId);
	}

	@Override
	public List<Repository> getRepositories(String portletId) {
		return repositoryPersistence.findByPortletId(portletId);
	}

	@Override
	public Repository getRepository(long groupId, String portletId)
		throws PortalException {

		return getRepository(groupId, portletId, portletId);
	}

	@Override
	public Repository getRepository(long groupId, String name, String portletId)
		throws PortalException {

		return repositoryPersistence.findByG_N_P(groupId, name, portletId);
	}

	@Override
	public UnicodeProperties getTypeSettingsProperties(long repositoryId)
		throws PortalException {

		Repository repository = repositoryPersistence.findByPrimaryKey(
			repositoryId);

		return repository.getTypeSettingsProperties();
	}

	@Override
	public void updateRepository(
			long repositoryId, String name, String description)
		throws PortalException {

		Repository repository = repositoryPersistence.findByPrimaryKey(
			repositoryId);

		repository.setName(name);
		repository.setDescription(description);

		repository = repositoryPersistence.update(repository);

		DLFolder dlFolder = _dlFolderPersistence.findByPrimaryKey(
			repository.getDlFolderId());

		dlFolder.setName(name);
		dlFolder.setDescription(description);

		_dlFolderPersistence.update(dlFolder);
	}

	@Override
	public void updateRepository(
			long repositoryId, UnicodeProperties typeSettingsUnicodeProperties)
		throws PortalException {

		Repository repository = repositoryPersistence.findByPrimaryKey(
			repositoryId);

		repository.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		repositoryPersistence.update(repository);
	}

	protected long getDLFolderId(
			User user, long groupId, long repositoryId, long parentFolderId,
			String name, String description, boolean hidden,
			ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new RepositoryNameException();
		}

		DLFolder dlFolder = _dlFolderLocalService.addFolder(
			null, user.getUserId(), groupId, repositoryId, true, parentFolderId,
			name, description, hidden, serviceContext);

		return dlFolder.getFolderId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RepositoryLocalServiceImpl.class);

	@BeanReference(type = DLFolderLocalService.class)
	private DLFolderLocalService _dlFolderLocalService;

	@BeanReference(type = DLFolderPersistence.class)
	private DLFolderPersistence _dlFolderPersistence;

	@BeanReference(type = ExpandoValueLocalService.class)
	private ExpandoValueLocalService _expandoValueLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = RepositoryEntryPersistence.class)
	private RepositoryEntryPersistence _repositoryEntryPersistence;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}