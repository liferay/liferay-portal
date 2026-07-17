/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.registry;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryConfiguration;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.repository.capabilities.ConfigurationCapability;
import com.liferay.portal.kernel.repository.capabilities.PortalCapabilityLocator;
import com.liferay.portal.kernel.repository.capabilities.RepositoryEventTriggerCapability;
import com.liferay.portal.kernel.repository.event.RepositoryEventAware;
import com.liferay.portal.kernel.repository.event.RepositoryEventListener;
import com.liferay.portal.kernel.repository.event.RepositoryEventTrigger;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.repository.registry.RepositoryEventRegistry;
import com.liferay.portal.kernel.repository.registry.RepositoryFactoryRegistry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.repository.InitializedLocalRepository;
import com.liferay.portal.repository.InitializedRepository;
import com.liferay.portal.repository.capabilities.CapabilityLocalRepository;
import com.liferay.portal.repository.capabilities.CapabilityRepository;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adolfo Pérez
 */
public class RepositoryClassDefinition
	implements RepositoryFactory, RepositoryFactoryRegistry {

	public static RepositoryClassDefinition fromRepositoryDefiner(
		RepositoryDefiner repositoryDefiner) {

		DefaultRepositoryEventRegistry defaultRepositoryEventRegistry =
			new DefaultRepositoryEventRegistry(null);

		RepositoryClassDefinition repositoryClassDefinition =
			new RepositoryClassDefinition(
				repositoryDefiner, defaultRepositoryEventRegistry);

		repositoryDefiner.registerRepositoryFactory(repositoryClassDefinition);

		repositoryDefiner.registerRepositoryEventListeners(
			defaultRepositoryEventRegistry);

		return repositoryClassDefinition;
	}

	@Override
	public LocalRepository createLocalRepository(long repositoryId)
		throws PortalException {

		Map<Long, LocalRepository> localRepositories =
			_localRepositoriesMap.computeIfAbsent(
				_getCompanyId(repositoryId), key -> new ConcurrentHashMap<>());

		LocalRepository localRepository = localRepositories.get(repositoryId);

		if (localRepository != null) {
			return localRepository;
		}

		InitializedLocalRepository initializedLocalRepository =
			new InitializedLocalRepository();

		DefaultCapabilityRegistry defaultCapabilityRegistry =
			new DefaultCapabilityRegistry(initializedLocalRepository);

		_repositoryDefiner.registerCapabilities(defaultCapabilityRegistry);

		DefaultRepositoryEventRegistry defaultRepositoryEventRegistry =
			new DefaultRepositoryEventRegistry(_rootRepositoryEventTrigger);

		setUpCommonCapabilities(
			initializedLocalRepository, defaultCapabilityRegistry,
			defaultRepositoryEventRegistry);

		defaultCapabilityRegistry.registerCapabilityRepositoryEvents(
			defaultRepositoryEventRegistry);

		localRepository = _repositoryFactory.createLocalRepository(
			repositoryId);

		LocalRepository wrappedLocalRepository =
			defaultCapabilityRegistry.invokeCapabilityWrappers(localRepository);

		CapabilityLocalRepository capabilityLocalRepository =
			new CapabilityLocalRepository(
				wrappedLocalRepository, defaultCapabilityRegistry,
				defaultRepositoryEventRegistry);

		initializedLocalRepository.setDocumentRepository(
			capabilityLocalRepository);

		localRepositories.put(repositoryId, capabilityLocalRepository);

		return capabilityLocalRepository;
	}

	@Override
	public Repository createRepository(long repositoryId)
		throws PortalException {

		Map<Long, Repository> repositories = _repositoriesMap.computeIfAbsent(
			_getCompanyId(repositoryId), key -> new ConcurrentHashMap<>());

		Repository repository = repositories.get(repositoryId);

		if (repository != null) {
			return repository;
		}

		InitializedRepository initializedRepository =
			new InitializedRepository();

		DefaultCapabilityRegistry defaultCapabilityRegistry =
			new DefaultCapabilityRegistry(initializedRepository);

		_repositoryDefiner.registerCapabilities(defaultCapabilityRegistry);

		DefaultRepositoryEventRegistry defaultRepositoryEventRegistry =
			new DefaultRepositoryEventRegistry(_rootRepositoryEventTrigger);

		setUpCommonCapabilities(
			initializedRepository, defaultCapabilityRegistry,
			defaultRepositoryEventRegistry);

		repository = _repositoryFactory.createRepository(repositoryId);

		defaultCapabilityRegistry.registerCapabilityRepositoryEvents(
			defaultRepositoryEventRegistry);

		Repository wrappedRepository =
			defaultCapabilityRegistry.invokeCapabilityWrappers(repository);

		CapabilityRepository capabilityRepository = new CapabilityRepository(
			wrappedRepository, defaultCapabilityRegistry,
			defaultRepositoryEventRegistry);

		initializedRepository.setDocumentRepository(capabilityRepository);

		if (!ExportImportThreadLocal.isImportInProcess()) {
			repositories.put(repositoryId, capabilityRepository);
		}

		return capabilityRepository;
	}

	public String getClassName() {
		return _repositoryDefiner.getClassName();
	}

	public RepositoryConfiguration getRepositoryConfiguration() {
		return _repositoryDefiner.getRepositoryConfiguration();
	}

	public String getRepositoryTypeLabel(Locale locale) {
		return _repositoryDefiner.getRepositoryTypeLabel(locale);
	}

	public void invalidateCache() {
		_localRepositoriesMap.clear();
		_repositoriesMap.clear();
	}

	@Override
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		if (_repositoryFactory != null) {
			throw new IllegalStateException(
				"Repository factory already exists");
		}

		_repositoryFactory = repositoryFactory;
	}

	protected RepositoryClassDefinition(
		RepositoryDefiner repositoryDefiner,
		RepositoryEventTrigger rootRepositoryEventTrigger) {

		_repositoryDefiner = repositoryDefiner;
		_rootRepositoryEventTrigger = rootRepositoryEventTrigger;
	}

	protected void invalidateCachedRepository(long repositoryId) {
		if (CompanyThreadLocal.getCompanyId() != null) {
			Map<Long, LocalRepository> localRepositories =
				_localRepositoriesMap.get(CompanyThreadLocal.getCompanyId());

			if (localRepositories != null) {
				localRepositories.remove(repositoryId);
			}

			Map<Long, Repository> repositories = _repositoriesMap.get(
				CompanyThreadLocal.getCompanyId());

			if (repositories != null) {
				repositories.remove(repositoryId);
			}
		}
		else {
			for (Map<Long, LocalRepository> localRepositories :
					_localRepositoriesMap.values()) {

				localRepositories.remove(repositoryId);
			}

			for (Map<Long, Repository> repositories :
					_repositoriesMap.values()) {

				repositories.remove(repositoryId);
			}
		}
	}

	protected void setUpCommonCapabilities(
		DocumentRepository documentRepository,
		DefaultCapabilityRegistry capabilityRegistry,
		RepositoryEventTrigger repositoryEventTrigger) {

		if (!capabilityRegistry.isCapabilityProvided(
				ConfigurationCapability.class)) {

			PortalCapabilityLocator portalCapabilityLocator =
				_portalCapabilityLocatorSnapshot.get();

			capabilityRegistry.addExportedCapability(
				ConfigurationCapability.class,
				portalCapabilityLocator.getConfigurationCapability(
					documentRepository));
		}

		if (!capabilityRegistry.isCapabilityProvided(
				RepositoryEventTriggerCapability.class)) {

			PortalCapabilityLocator portalCapabilityLocator =
				_portalCapabilityLocatorSnapshot.get();

			capabilityRegistry.addExportedCapability(
				RepositoryEventTriggerCapability.class,
				portalCapabilityLocator.getRepositoryEventTriggerCapability(
					documentRepository, repositoryEventTrigger));
		}

		capabilityRegistry.addSupportedCapability(
			CacheCapability.class, new CacheCapability());
	}

	private long _getCompanyId(long repositoryId) throws PortalException {
		Group group = GroupLocalServiceUtil.fetchGroup(repositoryId);

		if (group != null) {
			return group.getCompanyId();
		}

		com.liferay.portal.kernel.model.Repository repository =
			RepositoryLocalServiceUtil.getRepository(repositoryId);

		return repository.getCompanyId();
	}

	private static final Snapshot<PortalCapabilityLocator>
		_portalCapabilityLocatorSnapshot = new Snapshot<>(
			RepositoryClassDefinition.class, PortalCapabilityLocator.class);

	private final Map<Long, Map<Long, LocalRepository>> _localRepositoriesMap =
		new ConcurrentHashMap<>();
	private final Map<Long, Map<Long, Repository>> _repositoriesMap =
		new ConcurrentHashMap<>();
	private final RepositoryDefiner _repositoryDefiner;
	private RepositoryFactory _repositoryFactory;
	private final RepositoryEventTrigger _rootRepositoryEventTrigger;

	private class CacheCapability implements Capability, RepositoryEventAware {

		@Override
		public void registerRepositoryEventListeners(
			RepositoryEventRegistry repositoryEventRegistry) {

			repositoryEventRegistry.registerRepositoryEventListener(
				RepositoryEventType.Delete.class, LocalRepository.class,
				new RepositoryEventListener
					<RepositoryEventType.Delete, LocalRepository>() {

					@Override
					public void execute(LocalRepository localRepository) {
						invalidateCachedRepository(
							localRepository.getRepositoryId());
					}

				});
		}

	}

}