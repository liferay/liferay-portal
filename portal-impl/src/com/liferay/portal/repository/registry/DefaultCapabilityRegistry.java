/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.registry;

import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.capabilities.BaseCapabilityProvider;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.repository.capabilities.CapabilityProvider;
import com.liferay.portal.kernel.repository.event.RepositoryEventAware;
import com.liferay.portal.kernel.repository.registry.CapabilityRegistry;
import com.liferay.portal.kernel.repository.registry.RepositoryEventRegistry;
import com.liferay.portal.repository.util.RepositoryWrapperAware;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class DefaultCapabilityRegistry
	extends BaseCapabilityProvider
	implements CapabilityProvider, CapabilityRegistry<DocumentRepository> {

	public DefaultCapabilityRegistry(DocumentRepository documentRepository) {
		_documentRepository = documentRepository;
	}

	@Override
	public <S extends Capability> void addExportedCapability(
		Class<S> capabilityClass, S capability) {

		super.addExportedCapability(capabilityClass, capability);
	}

	@Override
	public <S extends Capability> void addSupportedCapability(
		Class<S> capabilityClass, S capability) {

		super.addSupportedCapability(capabilityClass, capability);
	}

	@Override
	public DocumentRepository getTarget() {
		return _documentRepository;
	}

	public LocalRepository invokeCapabilityWrappers(
		LocalRepository localRepository) {

		Map<Class<? extends Capability>, Capability> capabilities =
			getCapabilities();

		for (Capability capability : capabilities.values()) {
			if (!(capability instanceof RepositoryWrapperAware)) {
				continue;
			}

			RepositoryWrapperAware repositoryWrapperAware =
				(RepositoryWrapperAware)capability;

			localRepository = repositoryWrapperAware.wrapLocalRepository(
				localRepository);
		}

		return localRepository;
	}

	public Repository invokeCapabilityWrappers(Repository repository) {
		Map<Class<? extends Capability>, Capability> capabilities =
			getCapabilities();

		for (Capability capability : capabilities.values()) {
			if (!(capability instanceof RepositoryWrapperAware)) {
				continue;
			}

			RepositoryWrapperAware repositoryWrapperAware =
				(RepositoryWrapperAware)capability;

			repository = repositoryWrapperAware.wrapRepository(repository);
		}

		return repository;
	}

	public void registerCapabilityRepositoryEvents(
		RepositoryEventRegistry repositoryEventRegistry) {

		Map<Class<? extends Capability>, Capability> capabilities =
			getCapabilities();

		for (Capability capability : capabilities.values()) {
			if (!(capability instanceof RepositoryEventAware)) {
				continue;
			}

			RepositoryEventAware repositoryEventAware =
				(RepositoryEventAware)capability;

			repositoryEventAware.registerRepositoryEventListeners(
				repositoryEventRegistry);
		}
	}

	@Override
	protected String getProviderKey() {
		return String.valueOf(_documentRepository.getRepositoryId());
	}

	private final DocumentRepository _documentRepository;

}