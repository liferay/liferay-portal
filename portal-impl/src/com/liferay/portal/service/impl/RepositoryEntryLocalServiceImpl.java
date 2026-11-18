/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.NoSuchRepositoryEntryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.SQLStateAcceptor;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.spring.aop.Property;
import com.liferay.portal.kernel.spring.aop.Retry;
import com.liferay.portal.service.base.RepositoryEntryLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Michael C. Han
 * @author Máté Thurzó
 */
public class RepositoryEntryLocalServiceImpl
	extends RepositoryEntryLocalServiceBaseImpl {

	@Override
	public RepositoryEntry addRepositoryEntry(
			long userId, long groupId, long repositoryId, String mappedId,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		long repositoryEntryId = counterLocalService.increment();

		RepositoryEntry repositoryEntry = repositoryEntryPersistence.create(
			repositoryEntryId);

		repositoryEntry.setUuid(serviceContext.getUuid());
		repositoryEntry.setGroupId(groupId);
		repositoryEntry.setCompanyId(user.getCompanyId());
		repositoryEntry.setUserId(userId);
		repositoryEntry.setUserName(user.getFullName());
		repositoryEntry.setRepositoryId(repositoryId);
		repositoryEntry.setMappedId(mappedId);

		return repositoryEntryPersistence.update(repositoryEntry);
	}

	@Override
	public void deleteRepositoryEntries(
			long repositoryId, Iterable<String> mappedIds)
		throws PortalException {

		for (String mappedId : mappedIds) {
			try {
				deleteRepositoryEntry(repositoryId, mappedId);
			}
			catch (NoSuchRepositoryEntryException
						noSuchRepositoryEntryException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchRepositoryEntryException);
				}
			}
		}
	}

	@Override
	public void deleteRepositoryEntry(long repositoryId, String mappedId)
		throws PortalException {

		repositoryEntryPersistence.removeByR_M(repositoryId, mappedId);
	}

	@Override
	public List<RepositoryEntry> getRepositoryEntries(long repositoryId) {
		return repositoryEntryPersistence.findByRepositoryId(repositoryId);
	}

	@Override
	@Retry(
		acceptor = SQLStateAcceptor.class,
		properties = {
			@Property(
				name = SQLStateAcceptor.SQLSTATE,
				value = SQLStateAcceptor.SQLSTATE_INTEGRITY_CONSTRAINT_VIOLATION
			)
		}
	)
	public RepositoryEntry getRepositoryEntry(
			long userId, long groupId, long repositoryId, String objectId)
		throws PortalException {

		RepositoryEntry repositoryEntry = repositoryEntryPersistence.fetchByR_M(
			repositoryId, objectId);

		if (repositoryEntry != null) {
			return repositoryEntry;
		}

		repositoryEntry = repositoryEntryPersistence.fetchByR_M(
			repositoryId, objectId, false);

		if (repositoryEntry != null) {
			return repositoryEntry;
		}

		return repositoryEntryLocalService.addRepositoryEntry(
			userId, groupId, repositoryId, objectId, new ServiceContext());
	}

	@Override
	public RepositoryEntry getRepositoryEntry(String uuid, long groupId)
		throws PortalException {

		return repositoryEntryPersistence.findByUUID_G(uuid, groupId);
	}

	@Override
	public RepositoryEntry updateRepositoryEntry(
			long repositoryEntryId, String mappedId)
		throws PortalException {

		RepositoryEntry repositoryEntry =
			repositoryEntryPersistence.findByPrimaryKey(repositoryEntryId);

		repositoryEntry.setMappedId(mappedId);

		return repositoryEntryPersistence.update(repositoryEntry);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RepositoryEntryLocalServiceImpl.class);

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}