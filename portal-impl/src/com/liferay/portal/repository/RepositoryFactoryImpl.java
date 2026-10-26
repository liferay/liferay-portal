/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.repository.UndeployedExternalRepositoryException;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.repository.liferayrepository.LiferayRepository;
import com.liferay.portal.repository.registry.RepositoryClassDefinition;
import com.liferay.portal.repository.registry.RepositoryClassDefinitionCatalog;

/**
 * @author Adolfo Pérez
 */
public class RepositoryFactoryImpl implements RepositoryFactory {

	@Override
	public LocalRepository createLocalRepository(long repositoryId)
		throws PortalException {

		RepositoryFactory repositoryFactory = _getRepositoryFactory(
			repositoryId);

		return repositoryFactory.createLocalRepository(repositoryId);
	}

	@Override
	public Repository createRepository(long repositoryId)
		throws PortalException {

		RepositoryFactory repositoryFactory = _getRepositoryFactory(
			repositoryId);

		return repositoryFactory.createRepository(repositoryId);
	}

	protected RepositoryFactory getRepositoryFactory(
		long companyId, String className) {

		RepositoryClassDefinition repositoryDefinition =
			_repositoryClassDefinitionCatalog.getRepositoryClassDefinition(
				companyId, className);

		if (repositoryDefinition == null) {
			throw new UndeployedExternalRepositoryException(className);
		}

		return repositoryDefinition;
	}

	private RepositoryFactory _getRepositoryFactory(long repositoryId) {
		com.liferay.portal.kernel.model.Repository repository =
			_repositoryLocalService.fetchRepository(repositoryId);

		if (repository == null) {
			return getRepositoryFactory(
				CompanyConstants.SYSTEM, LiferayRepository.class.getName());
		}

		return getRepositoryFactory(
			repository.getCompanyId(), repository.getClassName());
	}

	@BeanReference(type = RepositoryClassDefinitionCatalog.class)
	private RepositoryClassDefinitionCatalog _repositoryClassDefinitionCatalog;

	@BeanReference(type = RepositoryLocalService.class)
	private RepositoryLocalService _repositoryLocalService;

}