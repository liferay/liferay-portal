/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.scope.internal.liferay;

import com.liferay.oauth2.provider.scope.internal.configuration.ScopeLocatorConfiguration;
import com.liferay.oauth2.provider.scope.internal.spi.scope.matcher.StrictScopeMatcherFactory;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcherFactory;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.ScopedServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.remote.jaxrs.whiteboard.lifecycle.JAXRSLifecycle;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.hamcrest.CoreMatchers;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stian Sigvartsen
 */
public class ScopeLocatorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("osgi.jaxrs.name", "Default");

		_prefixHandlerFactoryServiceRegistration =
			bundleContext.registerService(
				PrefixHandlerFactory.class,
				Mockito.mock(PrefixHandlerFactory.class), properties);

		_scopeMapperServiceRegistration = bundleContext.registerService(
			ScopeMapper.class, Mockito.mock(ScopeMapper.class), properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
		_prefixHandlerFactoryServiceRegistration.unregister();
		_scopeMapperServiceRegistration.unregister();
	}

	@Test
	public void testPrefixHandlerFactoryByNameAndCompany() throws Exception {
		String applicationName2 = "com.liferay.test2";

		PrefixHandler defaultPrefixHandler = target -> "default/" + target;

		ScopeFinder scopeFinder = () -> scopesSet1;

		Builder builder = new Builder();

		ScopeLocatorImpl scopeLocatorImpl = builder.withPrefixHandlerFactories(
			propertyAccessor -> defaultPrefixHandler,
			registrator -> {
			}
		).withScopeFinders(
			registrator -> {
				registrator.register(
					_COMPANY_ID, _APPLICATION_NAME, scopeFinder);
				registrator.register(
					_COMPANY_ID, applicationName2, scopeFinder);
			}
		).build();

		Collection<String> application1ScopeAliases =
			scopeLocatorImpl.getScopeAliases(_COMPANY_ID, _APPLICATION_NAME);

		Collection<String> application2ScopeAliases =
			scopeLocatorImpl.getScopeAliases(_COMPANY_ID, applicationName2);

		for (String scope : scopesSet1) {
			Assert.assertThat(
				application1ScopeAliases,
				CoreMatchers.hasItem(defaultPrefixHandler.addPrefix(scope)));

			Assert.assertThat(
				application2ScopeAliases,
				CoreMatchers.hasItem(defaultPrefixHandler.addPrefix(scope)));
		}

		PrefixHandler appPrefixHandler = target -> "app/" + target;
		PrefixHandler companyPrefixHandler = target -> "company/" + target;

		builder = new Builder();

		scopeLocatorImpl = builder.withPrefixHandlerFactories(
			propertyAccessor -> defaultPrefixHandler,
			registrator -> {
				registrator.register(
					null, _APPLICATION_NAME,
					propertyAccessor -> appPrefixHandler);
				registrator.register(
					_COMPANY_ID, null,
					propertyAccessor -> companyPrefixHandler);
			}
		).withScopeFinders(
			registrator -> {
				registrator.register(
					_COMPANY_ID, _APPLICATION_NAME, scopeFinder);
				registrator.register(
					_COMPANY_ID, applicationName2, scopeFinder);
			}
		).build();

		application1ScopeAliases = scopeLocatorImpl.getScopeAliases(
			_COMPANY_ID, _APPLICATION_NAME);

		application2ScopeAliases = scopeLocatorImpl.getScopeAliases(
			_COMPANY_ID, applicationName2);

		for (String scope : scopesSet1) {
			Assert.assertThat(
				application1ScopeAliases,
				CoreMatchers.hasItem(appPrefixHandler.addPrefix(scope)));

			Assert.assertThat(
				application2ScopeAliases,
				CoreMatchers.hasItem(companyPrefixHandler.addPrefix(scope)));
		}
	}

	@Test
	public void testScopeFinderByName() throws Exception {
		String applicationName2 = "com.liferay.test2";
		ScopeFinder application1ScopeFinder = () -> scopesSet1;
		ScopeFinder application2ScopeFinder = () -> scopedSet2;

		Builder builder = new Builder();

		ScopeLocatorImpl scopeLocatorImpl = builder.withScopeFinders(
			registrator -> {
				registrator.register(
					_COMPANY_ID, _APPLICATION_NAME, application1ScopeFinder);
				registrator.register(
					_COMPANY_ID, applicationName2, application2ScopeFinder);
			}
		).build();

		Collection<String> application1ScopeAliases =
			scopeLocatorImpl.getScopeAliases(_COMPANY_ID, _APPLICATION_NAME);

		Collection<String> application2ScopesAliasesDefault =
			scopeLocatorImpl.getScopeAliases(_COMPANY_ID, applicationName2);

		for (String scope : scopesSet1) {
			Assert.assertThat(
				application1ScopeAliases, CoreMatchers.hasItem(scope));
		}

		for (String scope : scopedSet2) {
			Assert.assertThat(
				application2ScopesAliasesDefault, CoreMatchers.hasItem(scope));
		}

		Assert.assertNotEquals(
			application1ScopeAliases, application2ScopesAliasesDefault);
	}

	@Test
	public void testScopeMapperByNameAndCompany() throws Exception {
		String applicationName2 = "com.liferay.test2";

		ScopeMapper defaultScopeMapper = ScopeMapper.PASS_THROUGH_SCOPE_MAPPER;

		ScopeFinder scopeFinder = () -> scopesSet1;

		Builder builder = new Builder();

		ScopeLocatorImpl scopeLocatorImpl = builder.withScopeMappers(
			defaultScopeMapper,
			registrator -> {
			}
		).withScopeFinders(
			registrator -> {
				registrator.register(
					_COMPANY_ID, _APPLICATION_NAME, scopeFinder);
				registrator.register(
					_COMPANY_ID, applicationName2, scopeFinder);
			}
		).build();

		Collection<String> application1ScopeAliases =
			scopeLocatorImpl.getScopeAliases(_COMPANY_ID, _APPLICATION_NAME);

		Collection<String> application2ScopeAliases =
			scopeLocatorImpl.getScopeAliases(_COMPANY_ID, applicationName2);

		for (String scope : scopesSet1) {
			Assert.assertThat(
				application1ScopeAliases, CoreMatchers.hasItem(scope));

			Assert.assertThat(
				application2ScopeAliases, CoreMatchers.hasItem(scope));
		}

		ScopeMapper appScopeMapper = scope -> Collections.singleton(
			"app/" + scope);
		ScopeMapper companyScopeMapper = scope -> Collections.singleton(
			"company/" + scope);

		builder = new Builder();

		scopeLocatorImpl = builder.withScopeMappers(
			defaultScopeMapper,
			registrator -> {
				registrator.register(null, _APPLICATION_NAME, appScopeMapper);
				registrator.register(_COMPANY_ID, null, companyScopeMapper);
			}
		).withScopeFinders(
			registrator -> {
				registrator.register(
					_COMPANY_ID, _APPLICATION_NAME, scopeFinder);
				registrator.register(
					_COMPANY_ID, applicationName2, scopeFinder);
			}
		).build();

		Collection<String> application1ScopesAliases =
			scopeLocatorImpl.getScopeAliases(_COMPANY_ID, _APPLICATION_NAME);

		Collection<String> application2ScopesAliases =
			scopeLocatorImpl.getScopeAliases(_COMPANY_ID, applicationName2);

		for (String scope : scopesSet1) {
			Assert.assertThat(
				application1ScopesAliases,
				CoreMatchers.hasItems(
					appScopeMapper.map(
						scope
					).toArray(
						new String[0]
					)));

			Assert.assertThat(
				application2ScopesAliases,
				CoreMatchers.hasItems(
					companyScopeMapper.map(
						scope
					).toArray(
						new String[0]
					)));
		}
	}

	@Test
	public void testScopeMatcherByCompany() throws Exception {
		String applicationName2 = "com.liferay.test2";

		ScopeFinder service = () -> scopesSet1;

		Set<String> matchScopes = Collections.singleton("everything.readonly");

		ScopeMatcherFactory explicitScopeMatcherFactory = scopeAlias ->
			scope -> scope.equals(scopeAlias) && matchScopes.contains(scope);

		Builder builder = new Builder();

		ScopeLocatorImpl scopeLocatorImpl = builder.withScopeFinders(
			registrator -> {
				registrator.register(_COMPANY_ID, _APPLICATION_NAME, service);
				registrator.register(_COMPANY_ID, applicationName2, service);
			}
		).withScopeMatcherFactories(
			scopeAlias -> scopeAlias::equals,
			registrator -> registrator.register(
				String.valueOf(_COMPANY_ID), explicitScopeMatcherFactory)
		).build();

		Collection<LiferayOAuth2Scope> matchedLiferayOAuth2Scopes =
			scopeLocatorImpl.getLiferayOAuth2Scopes(
				_COMPANY_ID, "everything", _APPLICATION_NAME);

		Set<String> matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);

		Assert.assertFalse(matchedScopes.contains("everything"));

		matchedLiferayOAuth2Scopes = scopeLocatorImpl.getLiferayOAuth2Scopes(
			_COMPANY_ID, "everything.readonly", _APPLICATION_NAME);

		matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);

		Assert.assertTrue(matchedScopes.contains("everything.readonly"));
	}

	@Test
	public void testScopeMatcherIsolatedFromPrefixHanderFactory()
		throws Exception {

		PrefixHandlerFactory testPrefixHandlerFactory =
			propertyAccessor -> target -> "test/" + target;

		ScopeMatcherFactory scopeMatcherFactory = Mockito.spy(
			new StrictScopeMatcherFactory());

		Builder builder = new Builder();

		ScopeLocatorImpl scopeLocatorImpl = builder.withPrefixHandlerFactories(
			propertyAccessor -> PrefixHandler.PASS_THROUGH_PREFIX_HANDLER,
			registrator -> registrator.register(
				_COMPANY_ID, _APPLICATION_NAME, testPrefixHandlerFactory)
		).withScopeMatcherFactories(
			scopeAlias -> scopeAlias::equals,
			registrator -> registrator.register(
				String.valueOf(_COMPANY_ID), scopeMatcherFactory)
		).withScopeFinders(
			registrator -> registrator.register(
				_COMPANY_ID, _APPLICATION_NAME, () -> scopesSet1)
		).build();

		Collection<LiferayOAuth2Scope> matchedLiferayOAuth2Scopes =
			scopeLocatorImpl.getLiferayOAuth2Scopes(
				_COMPANY_ID, "test/everything", _APPLICATION_NAME);

		Mockito.verify(
			scopeMatcherFactory, Mockito.atLeast(1)
		).create(
			"everything"
		);

		Set<String> matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);

		Assert.assertTrue(matchedScopes.contains("everything"));
	}

	protected final Set<String> scopedSet2 = new HashSet<>(
		Arrays.asList("GET", "POST"));
	protected final Set<String> scopesSet1 = new HashSet<>(
		Arrays.asList("everything", "everything.readonly"));

	private Set<String> _getScopes(
		Collection<LiferayOAuth2Scope> liferayOAuth2Scopes) {

		Set<String> scopes = new HashSet<>();

		for (LiferayOAuth2Scope liferayOAuth2Scope : liferayOAuth2Scopes) {
			scopes.add(liferayOAuth2Scope.getScope());
		}

		return scopes;
	}

	private static final String _APPLICATION_NAME = "com.liferay.test1";

	private static final long _COMPANY_ID = 1;

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);
	private static ServiceRegistration<PrefixHandlerFactory>
		_prefixHandlerFactoryServiceRegistration;
	private static ServiceRegistration<ScopeMapper>
		_scopeMapperServiceRegistration;

	private class Builder {

		public ScopeLocatorImpl build() throws IllegalAccessException {
			if (!_scopeMatcherFactoriesInitialized) {
				withScopeMatcherFactories(
					scopeAlias -> scopeAlias::equals,
					registrator -> {
					});
			}

			if (!_prefixHandlerFactoriesInitialized) {
				withPrefixHandlerFactories(
					propertyAccessor ->
						PrefixHandler.PASS_THROUGH_PREFIX_HANDLER,
					registrator -> {
					});
			}

			if (!_scopeMappersInitialized) {
				withScopeMappers(
					ScopeMapper.PASS_THROUGH_SCOPE_MAPPER,
					registrator -> {
					});
			}

			if (!_scopeFindersInitialized) {
				withScopeFinders(
					registrator -> {
					});
			}

			ReflectionTestUtil.setFieldValue(
				_scopeLocatorImpl, "_jaxrsLifecycle",
				new JAXRSLifecycle() {

					@Override
					public void ensureReady() {
					}

				});
			ReflectionTestUtil.setFieldValue(
				_scopeLocatorImpl, "_scopeLocatorConfiguration",
				new TestScopeLocatorConfiguration());

			return _scopeLocatorImpl;
		}

		public Builder withPrefixHandlerFactories(
				PrefixHandlerFactory defaultPrefixHandlerFactory,
				CompanyAndKeyConfigurator<PrefixHandlerFactory> configurator)
			throws IllegalAccessException {

			_scopeLocatorImpl.setPrefixHandlerFactoriesScopedServiceTrackerMap(
				_prepareScopedServiceTrackerMapMock(
					defaultPrefixHandlerFactory, configurator));

			_prefixHandlerFactoriesInitialized = true;

			return this;
		}

		public Builder withScopeFinders(
				CompanyAndKeyConfigurator<ScopeFinder> configurator)
			throws IllegalAccessException, IllegalArgumentException {

			ServiceTrackerMap
				<String, ServiceReferenceServiceTuple<?, ScopeFinder>>
					scopeFinderByNameServiceTrackerMap = Mockito.mock(
						ServiceTrackerMap.class);

			_scopeLocatorImpl.setScopeFinderByNameServiceTrackerMap(
				scopeFinderByNameServiceTrackerMap);

			ScopedServiceTrackerMap<ScopeFinder>
				scopeFindersScopedServiceTrackerMap = Mockito.mock(
					ScopedServiceTrackerMap.class);

			_scopeLocatorImpl.setScopeFindersScopedServiceTrackerMap(
				scopeFindersScopedServiceTrackerMap);

			configurator.configure(
				(companyId, applicationName, service) -> {
					ServiceReference<?> serviceReference = Mockito.mock(
						ServiceReference.class);

					Mockito.when(
						scopeFinderByNameServiceTrackerMap.getService(
							applicationName)
					).thenReturn(
						new ServiceReferenceServiceTuple(
							serviceReference, service)
					);

					Mockito.when(
						scopeFindersScopedServiceTrackerMap.getService(
							companyId, applicationName)
					).thenReturn(
						service
					);
				});

			_scopeFindersInitialized = true;

			return this;
		}

		public Builder withScopeMappers(
				ScopeMapper defaultScopeMapper,
				CompanyAndKeyConfigurator<ScopeMapper> configurator)
			throws IllegalAccessException {

			_scopeLocatorImpl.setScopeMappersScopedServiceTrackerMap(
				_prepareScopedServiceTrackerMapMock(
					defaultScopeMapper, configurator));

			_scopeMappersInitialized = true;

			return this;
		}

		public Builder withScopeMatcherFactories(
				ScopeMatcherFactory defaultScopeMatcherFactory,
				KeyConfigurator<ScopeMatcherFactory> configurator)
			throws IllegalAccessException, IllegalArgumentException {

			ServiceTrackerMap<String, ScopeMatcherFactory>
				scopeMatcherFactoriesServiceTrackerMap = Mockito.mock(
					ServiceTrackerMap.class);

			ReflectionTestUtil.setFieldValue(
				_scopeLocatorImpl, "_defaultScopeMatcherFactory",
				defaultScopeMatcherFactory);

			_scopeLocatorImpl.setScopeMatcherFactoriesServiceTrackerMap(
				scopeMatcherFactoriesServiceTrackerMap);

			configurator.configure(
				(companyId, service) -> Mockito.when(
					scopeMatcherFactoriesServiceTrackerMap.getService(companyId)
				).thenReturn(
					service
				));

			_scopeMatcherFactoriesInitialized = true;

			return this;
		}

		private <T> ScopedServiceTrackerMap<T>
			_prepareScopedServiceTrackerMapMock(
				T defaultService, CompanyAndKeyConfigurator<T> configurator) {

			ScopedServiceTrackerMap<T> scopedServiceTrackerMap = Mockito.mock(
				ScopedServiceTrackerMap.class);

			TestScopedServiceTrackerMap<T> testScopedServiceTrackerMap =
				new TestScopedServiceTrackerMap<>(defaultService);

			Answer<T> answer = invocation -> {
				long companyId = invocation.getArgument(0, Long.class);
				String key = invocation.getArgument(1, String.class);

				return testScopedServiceTrackerMap.getService(companyId, key);
			};

			Mockito.when(
				scopedServiceTrackerMap.getService(
					Mockito.anyLong(), Mockito.anyString())
			).thenAnswer(
				answer
			);

			configurator.configure(testScopedServiceTrackerMap::setService);

			return scopedServiceTrackerMap;
		}

		private boolean _prefixHandlerFactoriesInitialized;
		private boolean _scopeFindersInitialized;
		private final ScopeLocatorImpl _scopeLocatorImpl =
			new ScopeLocatorImpl();
		private boolean _scopeMappersInitialized;
		private boolean _scopeMatcherFactoriesInitialized;

	}

	private interface CompanyAndKeyConfigurator<T> {

		public void configure(CompanyAndKeyRegistrator<T> registrator);

	}

	private interface CompanyAndKeyRegistrator<T> {

		public void register(Long companyId, String key, T service);

	}

	private interface KeyConfigurator<T> {

		public void configure(KeyRegistrator<T> registrator);

	}

	private interface KeyRegistrator<T> {

		public void register(String key, T service);

	}

	private class TestScopeLocatorConfiguration
		implements ScopeLocatorConfiguration {

		@Override
		public boolean includeScopesImpliedBeforeScopeMapping() {
			return true;
		}

		@Override
		public String osgiJaxRsName() {
			return "Default";
		}

	}

}