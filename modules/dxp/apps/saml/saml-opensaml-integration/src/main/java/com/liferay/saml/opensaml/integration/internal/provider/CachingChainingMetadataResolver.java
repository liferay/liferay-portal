/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Mika Koivisto
 */
public class CachingChainingMetadataResolver extends AbstractMetadataResolver {

	public CachingChainingMetadataResolver(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, MetadataResolver.class,
			new ServiceTrackerCustomizer<MetadataResolver, MetadataResolver>() {

				@Override
				public MetadataResolver addingService(
					ServiceReference<MetadataResolver> serviceReference) {

					MetadataResolver metadataResolver =
						bundleContext.getService(serviceReference);

					addMetadataResolver(metadataResolver);

					return metadataResolver;
				}

				@Override
				public void modifiedService(
					ServiceReference<MetadataResolver> serviceReference,
					MetadataResolver metadataResolver) {
				}

				@Override
				public void removedService(
					ServiceReference<MetadataResolver> serviceReference,
					MetadataResolver metadataResolver) {

					removeMetadataResolver(metadataResolver);

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();
	}

	public void addMetadataResolver(MetadataResolver metadataResolver) {
		Lock lock = _readWriteLock.writeLock();

		lock.lock();

		try {
			if (metadataResolver != null) {
				metadataResolver.setRequireValidMetadata(
					isRequireValidMetadata());

				_metadataResolvers.add(metadataResolver);
			}
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public void doDestroy() {
		Lock lock = _readWriteLock.writeLock();

		lock.lock();

		try {
			_serviceTracker.close();

			_metadataResolversMap.clear();

			for (MetadataResolver metadataProvider : _metadataResolvers) {
				if (!(metadataProvider instanceof AbstractMetadataResolver)) {
					continue;
				}

				AbstractMetadataResolver baseMetadataProvider =
					(AbstractMetadataResolver)metadataProvider;

				baseMetadataProvider.destroy();
			}

			_metadataResolvers.clear();
		}
		finally {
			lock.unlock();
		}
	}

	public void removeMetadataResolver(MetadataResolver metadataResolver) {
		Lock lock = _readWriteLock.writeLock();

		lock.lock();

		try {
			_metadataResolvers.remove(metadataResolver);

			_metadataResolversMap.clear();
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public Iterable<EntityDescriptor> resolve(CriteriaSet criteria)
		throws ResolverException {

		ArrayList<EntityDescriptor> entityDescriptors = new ArrayList<>();

		Lock lock = _readWriteLock.writeLock();

		lock.lock();

		try {
			for (MetadataResolver metadataResolver : _metadataResolvers) {
				Iterable<EntityDescriptor> iterable = metadataResolver.resolve(
					criteria);

				iterable.forEach(entityDescriptors::add);
			}

			return entityDescriptors;
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public void setRequireValidMetadata(boolean requireValidMetadata) {
		super.setRequireValidMetadata(requireValidMetadata);

		Lock lock = _readWriteLock.writeLock();

		lock.lock();

		try {
			for (MetadataResolver metadataProvider : _metadataResolvers) {
				metadataProvider.setRequireValidMetadata(requireValidMetadata);
			}
		}
		finally {
			lock.unlock();
		}
	}

	private final List<MetadataResolver> _metadataResolvers =
		new CopyOnWriteArrayList<>();
	private final Map<String, MetadataResolver> _metadataResolversMap =
		new ConcurrentHashMap<>();
	private final ReadWriteLock _readWriteLock = new ReentrantReadWriteLock(
		true);
	private final ServiceTracker<MetadataResolver, MetadataResolver>
		_serviceTracker;

}