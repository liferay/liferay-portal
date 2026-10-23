/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.scope.internal.liferay;

import com.liferay.oauth2.provider.scope.internal.configuration.ScopeLocatorConfiguration;
import com.liferay.oauth2.provider.scope.internal.constants.OAuth2ProviderScopeConstants;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcher;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcherFactory;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ScopedServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ScopedServiceTrackerMapFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.remote.jaxrs.whiteboard.lifecycle.JAXRSLifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.scope.internal.configuration.ScopeLocatorConfiguration",
	service = ScopeLocator.class
)
public class ScopeLocatorImpl implements ScopeLocator {

	@Override
	public LiferayOAuth2Scope getLiferayOAuth2Scope(
		long companyId, String applicationName, String scope) {

		_jaxrsLifecycle.ensureReady();

		ServiceReferenceServiceTuple<?, ScopeFinder>
			serviceReferenceServiceTuple =
				_scopeFinderByNameServiceTrackerMap.getService(applicationName);

		if (serviceReferenceServiceTuple == null) {
			return null;
		}

		Bundle bundle = getBundle(
			serviceReferenceServiceTuple.getServiceReference());

		return new LiferayOAuth2ScopeImpl(applicationName, bundle, scope);
	}

	@Override
	public Collection<LiferayOAuth2Scope> getLiferayOAuth2Scopes(
		long companyId) {

		_jaxrsLifecycle.ensureReady();

		Collection<LiferayOAuth2Scope> liferayOAuth2Scopes = new ArrayList<>();

		for (String key : _scopeFinderByNameServiceTrackerMap.keySet()) {
			ScopeFinder scopeFinder =
				_scopeFindersScopedServiceTrackerMap.getService(companyId, key);

			ServiceReferenceServiceTuple<?, ScopeFinder>
				serviceReferenceServiceTuple =
					_scopeFinderByNameServiceTrackerMap.getService(key);

			if (scopeFinder == null) {
				scopeFinder = serviceReferenceServiceTuple.getService();
			}

			Bundle bundle = getBundle(
				serviceReferenceServiceTuple.getServiceReference());

			for (String scope : scopeFinder.findScopes()) {
				liferayOAuth2Scopes.add(
					new LiferayOAuth2ScopeImpl(key, bundle, scope));
			}
		}

		return liferayOAuth2Scopes;
	}

	@Override
	public Collection<LiferayOAuth2Scope> getLiferayOAuth2Scopes(
		long companyId, String scopesAlias) {

		_jaxrsLifecycle.ensureReady();

		Set<String> names = _scopeFinderByNameServiceTrackerMap.keySet();

		Collection<LiferayOAuth2Scope> liferayOAuth2Scopes = new ArrayList<>();

		for (String name : names) {
			liferayOAuth2Scopes.addAll(
				getLiferayOAuth2Scopes(companyId, scopesAlias, name));
		}

		return liferayOAuth2Scopes;
	}

	@Override
	public Collection<LiferayOAuth2Scope> getLiferayOAuth2Scopes(
		long companyId, String scopesAlias, String applicationName) {

		_jaxrsLifecycle.ensureReady();

		ScopeFinder scopeFinder =
			_scopeFindersScopedServiceTrackerMap.getService(
				companyId, applicationName);

		Collection<String> scopes = scopeFinder.findScopes();

		if (scopes.isEmpty()) {
			return Collections.emptyList();
		}

		ServiceReferenceServiceTuple<?, ScopeFinder>
			serviceReferenceServiceTuple =
				_scopeFinderByNameServiceTrackerMap.getService(applicationName);

		PrefixHandlerFactory prefixHandlerFactory =
			_prefixHandlerFactoriesScopedServiceTrackerMap.getService(
				companyId, applicationName);

		ServiceReference<?> serviceReference =
			serviceReferenceServiceTuple.getServiceReference();

		Bundle bundle = getBundle(serviceReference);

		Set<LiferayOAuth2Scope> locatedScopes = new HashSet<>();

		Map<String, Set<String>> mappedScopeToUnmappedScopes = new HashMap<>();
		Map<String, Boolean> matchCache = new HashMap<>();
		PrefixHandler prefixHandler = prefixHandlerFactory.create(
			serviceReference::getProperty);
		Queue<String> queue = new LinkedList<>();
		ScopeMapper scopeMapper =
			_scopeMappersScopedServiceTrackerMap.getService(
				companyId, applicationName);
		ScopeMatcherFactory scopeMatcherFactory = _getScopeMatcherFactory(
			companyId);

		for (String scope : scopes) {
			for (String mappedScope : scopeMapper.map(scope)) {
				boolean matched = matchCache.computeIfAbsent(
					mappedScope,
					key -> _scopeMatchesScopesAlias(
						key, scopeMatcherFactory, prefixHandler, scopesAlias));

				if (matched) {
					queue.add(scope);
				}

				Set<String> unmappedScopes =
					mappedScopeToUnmappedScopes.computeIfAbsent(
						mappedScope, key -> new HashSet());

				unmappedScopes.add(scope);
			}
		}

		Set<String> processedScopes = new HashSet<>();

		for (String scope = queue.poll(); scope != null; scope = queue.poll()) {
			processedScopes.add(scope);

			locatedScopes.add(
				new LiferayOAuth2ScopeImpl(applicationName, bundle, scope));

			if (!_scopeLocatorConfiguration.
					includeScopesImpliedBeforeScopeMapping()) {

				continue;
			}

			ScopeMatcher scopeMatcher = scopeMatcherFactory.create(scope);

			mappedScopeToUnmappedScopes.forEach(
				(mappedScope, unmappedScopes) -> {
					boolean matched = matchCache.compute(
						mappedScope,
						(key, matches) -> {
							if (!matches) {
								return scopeMatcher.match(mappedScope);
							}

							return matches;
						});

					if (!matched) {
						return;
					}

					for (String unmappedScope : unmappedScopes) {
						if (processedScopes.contains(unmappedScope)) {
							continue;
						}

						queue.add(unmappedScope);
					}
				});
		}

		return locatedScopes;
	}

	@Override
	public Collection<String> getScopeAliases(long companyId) {
		_jaxrsLifecycle.ensureReady();

		Collection<String> scopesAliases = new HashSet<>();

		Set<String> applicationNames =
			_scopeFinderByNameServiceTrackerMap.keySet();

		for (String applicationName : applicationNames) {
			scopesAliases.addAll(getScopeAliases(companyId, applicationName));
		}

		return scopesAliases;
	}

	@Override
	public Collection<String> getScopeAliases(
		long companyId, String applicationName) {

		_jaxrsLifecycle.ensureReady();

		ServiceReferenceServiceTuple<?, ScopeFinder>
			serviceReferenceServiceTuple =
				_scopeFinderByNameServiceTrackerMap.getService(applicationName);

		if (serviceReferenceServiceTuple == null) {
			return Collections.emptyList();
		}

		PrefixHandlerFactory prefixHandlerFactory =
			_prefixHandlerFactoriesScopedServiceTrackerMap.getService(
				companyId, applicationName);
		ServiceReference<?> serviceReference =
			serviceReferenceServiceTuple.getServiceReference();

		PrefixHandler prefixHandler = prefixHandlerFactory.create(
			serviceReference::getProperty);

		ScopeFinder scopeFinder =
			_scopeFindersScopedServiceTrackerMap.getService(
				companyId, applicationName);

		ScopeMapper scopeMapper =
			_scopeMappersScopedServiceTrackerMap.getService(
				companyId, applicationName);
		Collection<String> scopes = scopeFinder.findScopes();
		Collection<String> scopesAliases = new ArrayList<>();

		for (String scope : scopes) {
			Set<String> mappedScopes = scopeMapper.map(scope);

			for (String mappedScope : mappedScopes) {
				String externalAlias = prefixHandler.addPrefix(mappedScope);

				scopesAliases.add(externalAlias);
			}
		}

		return scopesAliases;
	}

	@Override
	public void stopWaitingForScopeAliases(Runnable runnable) {
		_runnableScopeAliases.remove(runnable);
	}

	@Override
	public Set<String> waitForScopeAliases(
		long companyId, Set<String> scopeAliases, Runnable runnable) {

		Set<String> unresolvedScopeAliases = _getUnresolvedScopeAliases(
			companyId, scopeAliases);

		if (unresolvedScopeAliases.isEmpty()) {
			return new HashSet<>(scopeAliases);
		}

		Map.Entry<Long, Set<String>> entry = Map.entry(
			companyId, unresolvedScopeAliases);

		_runnableScopeAliases.put(runnable, entry);

		unresolvedScopeAliases = _getUnresolvedScopeAliases(
			companyId, unresolvedScopeAliases);

		if (unresolvedScopeAliases.isEmpty()) {
			_runnableScopeAliases.remove(runnable, entry);
		}
		else {
			_runnableScopeAliases.replace(
				runnable, entry, Map.entry(companyId, unresolvedScopeAliases));
		}

		Set<String> resolvedScopeAliases = new HashSet<>(scopeAliases);

		resolvedScopeAliases.removeAll(unresolvedScopeAliases);

		return resolvedScopeAliases;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_scopeLocatorConfiguration = ConfigurableUtil.createConfigurable(
			ScopeLocatorConfiguration.class, properties);

		setPrefixHandlerFactoriesScopedServiceTrackerMap(
			ScopedServiceTrackerMapFactory.create(
				bundleContext, PrefixHandlerFactory.class,
				OAuth2ProviderScopeConstants.OSGI_JAXRS_NAME,
				() -> {
					PrefixHandlerFactory prefixHandlerFactory =
						_defaultPrefixHandlerFactorySnapshot.get();

					if (prefixHandlerFactory != null) {
						return prefixHandlerFactory;
					}

					return propertyAccessor ->
						PrefixHandler.PASS_THROUGH_PREFIX_HANDLER;
				}));
		setScopeFindersScopedServiceTrackerMap(
			ScopedServiceTrackerMapFactory.create(
				bundleContext, ScopeFinder.class,
				OAuth2ProviderScopeConstants.OSGI_JAXRS_NAME, StringPool.BLANK,
				() -> Collections::emptySet, this::_notify));
		setScopeMappersScopedServiceTrackerMap(
			ScopedServiceTrackerMapFactory.create(
				bundleContext, ScopeMapper.class,
				OAuth2ProviderScopeConstants.OSGI_JAXRS_NAME,
				() -> {
					ScopeMapper scopeMapper = _defaultScopeMapperSnapshot.get();

					if (scopeMapper != null) {
						return scopeMapper;
					}

					return ScopeMapper.PASS_THROUGH_SCOPE_MAPPER;
				}));
		setScopeMatcherFactoriesServiceTrackerMap(
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ScopeMatcherFactory.class, "company.id"));

		ServiceReferenceMapper<String, ScopeFinder> serviceReferenceMapper =
			new PropertyServiceReferenceMapper<>(
				OAuth2ProviderScopeConstants.OSGI_JAXRS_NAME);

		setScopeFinderByNameServiceTrackerMap(
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ScopeFinder.class,
				"(" + OAuth2ProviderScopeConstants.OSGI_JAXRS_NAME + "=*)",
				(serviceReference, emitter) -> serviceReferenceMapper.map(
					serviceReference,
					key -> {
						emitter.emit(key);

						_notify(key);
					}),
				new ScopeFinderServiceTupleServiceTrackerCustomizer(
					bundleContext)));
	}

	@Deactivate
	protected void deactivate() {
		_scopeFinderByNameServiceTrackerMap.close();
		_prefixHandlerFactoriesScopedServiceTrackerMap.close();
		_scopeFindersScopedServiceTrackerMap.close();
		_scopeMappersScopedServiceTrackerMap.close();
		_scopeMatcherFactoriesServiceTrackerMap.close();
	}

	protected Bundle getBundle(ServiceReference<?> serviceReference) {
		Object property = serviceReference.getProperty(
			"original.service.bundleid");

		if (property == null) {
			return serviceReference.getBundle();
		}

		long bundleId = GetterUtil.getLong(property, -1L);

		if (bundleId == -1) {
			return serviceReference.getBundle();
		}

		Bundle bundle = _bundleContext.getBundle(bundleId);

		if (bundle == null) {
			return serviceReference.getBundle();
		}

		return bundle;
	}

	protected void setPrefixHandlerFactoriesScopedServiceTrackerMap(
		ScopedServiceTrackerMap<PrefixHandlerFactory>
			prefixHandlerFactoriesScopedServiceTrackerMap) {

		_prefixHandlerFactoriesScopedServiceTrackerMap =
			prefixHandlerFactoriesScopedServiceTrackerMap;
	}

	protected void setScopeFinderByNameServiceTrackerMap(
		ServiceTrackerMap<String, ServiceReferenceServiceTuple<?, ScopeFinder>>
			scopeFinderByNameServiceTrackerMap) {

		_scopeFinderByNameServiceTrackerMap =
			scopeFinderByNameServiceTrackerMap;
	}

	protected void setScopeFindersScopedServiceTrackerMap(
		ScopedServiceTrackerMap<ScopeFinder>
			scopeFindersScopedServiceTrackerMap) {

		_scopeFindersScopedServiceTrackerMap =
			scopeFindersScopedServiceTrackerMap;
	}

	protected void setScopeMappersScopedServiceTrackerMap(
		ScopedServiceTrackerMap<ScopeMapper>
			scopeMappersScopedServiceTrackerMap) {

		_scopeMappersScopedServiceTrackerMap =
			scopeMappersScopedServiceTrackerMap;
	}

	protected void setScopeMatcherFactoriesServiceTrackerMap(
		ServiceTrackerMap<String, ScopeMatcherFactory>
			scopeMatcherFactoriesServiceTrackerMap) {

		_scopeMatcherFactoriesServiceTrackerMap =
			scopeMatcherFactoriesServiceTrackerMap;
	}

	private ScopeMatcherFactory _getScopeMatcherFactory(long companyId) {
		ScopeMatcherFactory scopeMatcherFactory =
			_scopeMatcherFactoriesServiceTrackerMap.getService(
				String.valueOf(companyId));

		if (scopeMatcherFactory == null) {
			return _defaultScopeMatcherFactory;
		}

		return scopeMatcherFactory;
	}

	private Set<String> _getUnresolvedScopeAliases(
		long companyId, Set<String> scopeAliases) {

		Set<String> unresolvedScopeAliases = new HashSet<>();

		for (String scopeAlias : scopeAliases) {
			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
				getLiferayOAuth2Scopes(companyId, scopeAlias);

			if (!liferayOAuth2Scopes.isEmpty()) {
				continue;
			}

			unresolvedScopeAliases.add(scopeAlias);
		}

		return unresolvedScopeAliases;
	}

	private boolean _hasResolvedAny(
		long companyId, Set<String> scopeAliases, String applicationName) {

		if ((applicationName != null) &&
			!_scopeFinderByNameServiceTrackerMap.containsKey(applicationName)) {

			return false;
		}

		for (String scopeAlias : scopeAliases) {
			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes;

			if (applicationName == null) {
				liferayOAuth2Scopes = getLiferayOAuth2Scopes(
					companyId, scopeAlias);
			}
			else {
				liferayOAuth2Scopes = getLiferayOAuth2Scopes(
					companyId, scopeAlias, applicationName);
			}

			if (!liferayOAuth2Scopes.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private void _notify(String applicationName) {
		_runnableScopeAliases.forEach(
			(runnable, entry) -> {
				try {
					if (_hasResolvedAny(
							entry.getKey(), entry.getValue(),
							applicationName) &&
						_runnableScopeAliases.remove(runnable, entry)) {

						runnable.run();
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});
	}

	private boolean _scopeMatchesScopesAlias(
		String scope, ScopeMatcherFactory scopeMatcherFactory,
		PrefixHandler prefixHandler, String scopesAlias) {

		String prefixedScope = prefixHandler.addPrefix(scope);

		if (scope.length() > prefixedScope.length()) {
			return false;
		}

		String prefix = prefixedScope.substring(
			0, prefixedScope.length() - scope.length());

		if (!scopesAlias.startsWith(prefix)) {
			return false;
		}

		ScopeMatcher scopeMatcher = scopeMatcherFactory.create(
			scopesAlias.substring(prefix.length()));

		return scopeMatcher.match(scope);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ScopeLocatorImpl.class);

	private static final Snapshot<PrefixHandlerFactory>
		_defaultPrefixHandlerFactorySnapshot = new Snapshot<>(
			ScopeLocatorImpl.class, PrefixHandlerFactory.class,
			"(osgi.jaxrs.name=Default)", true);
	private static final Snapshot<ScopeMapper> _defaultScopeMapperSnapshot =
		new Snapshot<>(
			ScopeLocatorImpl.class, ScopeMapper.class,
			"(osgi.jaxrs.name=Default)", true);

	private BundleContext _bundleContext;

	@Reference(name = "default")
	private ScopeMatcherFactory _defaultScopeMatcherFactory;

	@Reference
	private JAXRSLifecycle _jaxrsLifecycle;

	private ScopedServiceTrackerMap<PrefixHandlerFactory>
		_prefixHandlerFactoriesScopedServiceTrackerMap;
	private final Map<Runnable, Map.Entry<Long, Set<String>>>
		_runnableScopeAliases = new ConcurrentHashMap<>();
	private ServiceTrackerMap
		<String, ServiceReferenceServiceTuple<?, ScopeFinder>>
			_scopeFinderByNameServiceTrackerMap;
	private ScopedServiceTrackerMap<ScopeFinder>
		_scopeFindersScopedServiceTrackerMap;
	private ScopeLocatorConfiguration _scopeLocatorConfiguration;
	private ScopedServiceTrackerMap<ScopeMapper>
		_scopeMappersScopedServiceTrackerMap;
	private ServiceTrackerMap<String, ScopeMatcherFactory>
		_scopeMatcherFactoriesServiceTrackerMap;

	private static class ScopeFinderServiceTupleServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ScopeFinder, ServiceReferenceServiceTuple<?, ScopeFinder>> {

		public ScopeFinderServiceTupleServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public ServiceReferenceServiceTuple<?, ScopeFinder> addingService(
			ServiceReference<ScopeFinder> serviceReference) {

			ScopeFinder scopeFinder = _bundleContext.getService(
				serviceReference);

			return new ServiceReferenceServiceTuple<>(
				serviceReference, scopeFinder);
		}

		@Override
		public void modifiedService(
			ServiceReference<ScopeFinder> serviceReference,
			ServiceReferenceServiceTuple<?, ScopeFinder>
				serviceReferenceServiceTuple) {
		}

		@Override
		public void removedService(
			ServiceReference<ScopeFinder> serviceReference,
			ServiceReferenceServiceTuple<?, ScopeFinder>
				serviceReferenceServiceTuple) {

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}