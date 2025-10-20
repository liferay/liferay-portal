/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.registry;

import com.liferay.portal.kernel.cache.CacheRegistryItem;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Adolfo Pérez
 */
public class RepositoryClassDefinitionCatalogImpl
	implements CacheRegistryItem, RepositoryClassDefinitionCatalog {

	public void afterPropertiesSet() {
		_serviceTracker = new ServiceTracker<>(
			_bundleContext, RepositoryDefiner.class,
			new RepositoryDefinerServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	public void destroy() {
		_serviceTracker.close();
	}

	@Override
	public Iterable<RepositoryClassDefinition>
		getExternalRepositoryClassDefinitions(long companyId) {

		Collection<RepositoryClassDefinition>
			externalRepositoryClassDefinitions =
				_getSystemExternalRepositoryData(Map::values);

		Map<String, RepositoryClassDefinition>
			companyRepositoryClassDefinitions =
				_externalRepositoryClassDefinitions.get(companyId);

		if (companyRepositoryClassDefinitions != null) {
			externalRepositoryClassDefinitions.addAll(
				companyRepositoryClassDefinitions.values());
		}

		return externalRepositoryClassDefinitions;
	}

	@Override
	public Collection<String> getExternalRepositoryClassNames(long companyId) {
		Collection<String> externalRepositoryClassNames =
			_getSystemExternalRepositoryData(Map::keySet);

		Map<String, RepositoryClassDefinition>
			companyRepositoryClassDefinitions =
				_externalRepositoryClassDefinitions.get(companyId);

		if (companyRepositoryClassDefinitions != null) {
			externalRepositoryClassNames.addAll(
				companyRepositoryClassDefinitions.keySet());
		}

		return externalRepositoryClassNames;
	}

	@Override
	public String getRegistryName() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	@Override
	public RepositoryClassDefinition getRepositoryClassDefinition(
		long companyId, String className) {

		Map<String, RepositoryClassDefinition>
			companyRepositoryClassDefinitions = _repositoryClassDefinitions.get(
				companyId);

		if (companyRepositoryClassDefinitions == null) {
			return _getSystemRepositoryClassDefinition(className);
		}

		RepositoryClassDefinition repositoryClassDefinition =
			companyRepositoryClassDefinitions.get(className);

		if (repositoryClassDefinition == null) {
			return _getSystemRepositoryClassDefinition(className);
		}

		return repositoryClassDefinition;
	}

	@Override
	public void invalidate() {
		Collection<Map<String, RepositoryClassDefinition>>
			repositoryClassDefinitions = null;

		if (PropsValues.DATABASE_PARTITION_ENABLED &&
			(CompanyThreadLocal.getCompanyId() != CompanyConstants.SYSTEM)) {

			Map<String, RepositoryClassDefinition>
				companyRepositoryClassDefinitions =
					_repositoryClassDefinitions.get(
						CompanyThreadLocal.getCompanyId());

			if (companyRepositoryClassDefinitions == null) {
				return;
			}

			repositoryClassDefinitions = Collections.singletonList(
				companyRepositoryClassDefinitions);
		}
		else {
			repositoryClassDefinitions = _repositoryClassDefinitions.values();
		}

		for (Map<String, RepositoryClassDefinition>
				companyRepositoryClassDefinitions :
					repositoryClassDefinitions) {

			for (RepositoryClassDefinition repositoryClassDefinition :
					companyRepositoryClassDefinitions.values()) {

				repositoryClassDefinition.invalidateCache();
			}
		}
	}

	private <T> Collection<T> _getSystemExternalRepositoryData(
		Function<Map<String, RepositoryClassDefinition>, Collection<T>>
			function) {

		Map<String, RepositoryClassDefinition>
			systemRepositoryClassDefinitions =
				_externalRepositoryClassDefinitions.get(
					CompanyConstants.SYSTEM);

		if (systemRepositoryClassDefinitions == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(
			function.apply(systemRepositoryClassDefinitions));
	}

	private RepositoryClassDefinition _getSystemRepositoryClassDefinition(
		String className) {

		Map<String, RepositoryClassDefinition>
			systemRepositoryClassDefinitions = _repositoryClassDefinitions.get(
				CompanyConstants.SYSTEM);

		if (systemRepositoryClassDefinitions == null) {
			return null;
		}

		return systemRepositoryClassDefinitions.get(className);
	}

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private final Map<Long, Map<String, RepositoryClassDefinition>>
		_externalRepositoryClassDefinitions = new ConcurrentHashMap<>();
	private final Map<Long, Map<String, RepositoryClassDefinition>>
		_repositoryClassDefinitions = new ConcurrentHashMap<>();
	private ServiceTracker
		<RepositoryDefiner, ServiceRegistration<RepositoryFactory>>
			_serviceTracker;

	private class RepositoryDefinerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<RepositoryDefiner, ServiceRegistration<RepositoryFactory>> {

		@Override
		public ServiceRegistration<RepositoryFactory> addingService(
			ServiceReference<RepositoryDefiner> serviceReference) {

			long companyId = GetterUtil.getLong(
				serviceReference.getProperty("companyId"));

			RepositoryDefiner repositoryDefiner = _bundleContext.getService(
				serviceReference);

			String className = repositoryDefiner.getClassName();
			RepositoryClassDefinition repositoryClassDefinition =
				RepositoryClassDefinition.fromRepositoryDefiner(
					repositoryDefiner);

			if (repositoryDefiner.isExternalRepository()) {
				Map<String, RepositoryClassDefinition>
					companyRepositoryClassDefinitions =
						_externalRepositoryClassDefinitions.computeIfAbsent(
							companyId, key -> new ConcurrentHashMap<>());

				companyRepositoryClassDefinitions.put(
					className, repositoryClassDefinition);
			}

			Map<String, RepositoryClassDefinition>
				companyRepositoryClassDefinitions =
					_repositoryClassDefinitions.computeIfAbsent(
						companyId, key -> new ConcurrentHashMap<>());

			companyRepositoryClassDefinitions.put(
				className, repositoryClassDefinition);

			return _bundleContext.registerService(
				RepositoryFactory.class, repositoryClassDefinition,
				MapUtil.singletonDictionary("class.name", className));
		}

		@Override
		public void modifiedService(
			ServiceReference<RepositoryDefiner> serviceReference,
			ServiceRegistration<RepositoryFactory> serviceRegistration) {
		}

		@Override
		public void removedService(
			ServiceReference<RepositoryDefiner> serviceReference,
			ServiceRegistration<RepositoryFactory> serviceRegistration) {

			_bundleContext.ungetService(serviceReference);

			ServiceReference<RepositoryFactory>
				repositoryFactoryServiceReference =
					serviceRegistration.getReference();

			long companyId = GetterUtil.getLong(
				repositoryFactoryServiceReference.getProperty("companyId"));
			String className =
				(String)repositoryFactoryServiceReference.getProperty(
					"class.name");

			Map<String, RepositoryClassDefinition>
				companyExternalRepositoryClassDefinitions =
					_externalRepositoryClassDefinitions.get(companyId);

			companyExternalRepositoryClassDefinitions.remove(className);

			Map<String, RepositoryClassDefinition>
				companyRepositoryClassDefinitions =
					_repositoryClassDefinitions.get(companyId);

			companyRepositoryClassDefinitions.remove(className);

			serviceRegistration.unregister();
		}

	}

}