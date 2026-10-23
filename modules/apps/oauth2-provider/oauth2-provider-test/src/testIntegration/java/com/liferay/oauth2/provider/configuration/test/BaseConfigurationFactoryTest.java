/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationHeadlessServerConfiguration;
import com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import jodd.util.StringUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class BaseConfigurationFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testActivate() throws Exception {
		_testActivateWithUnregisteredCompany();
		_testActivateWithUnresolvedScopeAliases();
	}

	@Test
	public void testExcessiveActivations() throws Exception {
		Configuration configuration = _createFactoryConfiguration(
			OAuth2ProviderApplicationUserAgentConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"_portalK8sConfigMapModifier.cardinality.minimum", 0
			).put(
				"baseURL", "http://foo.me"
			).put(
				"companyId",
				() -> {
					Company company = null;

					try {
						company = CompanyTestUtil.addCompany();
					}
					catch (Exception exception) {
						throw new RuntimeException(exception);
					}

					return company.getCompanyId();
				}
			).build());

		List<String> errorLogEntries = new ArrayList<>();

		LogListener logListener = logEntry -> {
			if (Objects.equals(logEntry.getLogLevel(), LogLevel.ERROR)) {
				errorLogEntries.add(logEntry.getMessage());
			}
		};

		_logReaderService.addLogListener(logListener);

		ServiceRegistration<?> serviceRegistration = null;

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				TestPropsValues.getCompanyId())) {

			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			serviceRegistration = bundleContext.registerService(
				Application.class,
				new Application() {
				},
				null);

			Assert.assertTrue(
				StringUtil.join(errorLogEntries, "\n"),
				errorLogEntries.isEmpty());
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(configuration);

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}

			_logReaderService.removeLogListener(logListener);
		}
	}

	@Test
	public void testGetFactoryConfiguration() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"_portalK8sConfigMapModifier.cardinality.minimum", 0
			).put(
				"baseURL", "http://foo.me"
			).put(
				"companyId", companyId
			).build();

		_testGetFactoryConfiguration(
			OAuth2ProviderApplicationHeadlessServerConfiguration.class.
				getName(),
			companyId, properties,
			_userLocalService.getUserByScreenName(
				companyId, UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT));

		properties.put("userAccountScreenName", "test");

		_testGetFactoryConfiguration(
			OAuth2ProviderApplicationHeadlessServerConfiguration.class.
				getName(),
			companyId, properties, TestPropsValues.getUser());

		_testGetFactoryConfiguration(
			OAuth2ProviderApplicationUserAgentConfiguration.class.getName(),
			companyId, properties, _userLocalService.getGuestUser(companyId));

		_user = UserTestUtil.addUser();

		properties.put("userAccountScreenName", _user.getScreenName());

		_testGetFactoryConfiguration(
			OAuth2ProviderApplicationHeadlessServerConfiguration.class.
				getName(),
			companyId, properties, _user);
	}

	@Path("/get")
	public static class TestGetResource {

		@GET
		public String get() {
			return RandomTestUtil.randomString();
		}

	}

	private Configuration _createFactoryConfiguration(
			String className, Dictionary<String, Object> properties)
		throws Exception {

		Configuration configuration =
			_configurationAdmin.getFactoryConfiguration(
				className, _EXTERNAL_REFERENCE_CODE, StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(configuration, properties);

		return configuration;
	}

	private OAuth2Application _fetchOAuthApplication(long companyId)
		throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(50);

		OAuth2Application oAuth2Application = null;

		while ((countDownLatch.getCount() > 0) && (oAuth2Application == null)) {
			try {
				oAuth2Application =
					_oAuth2ApplicationLocalService.
						getOAuth2ApplicationByExternalReferenceCode(
							_EXTERNAL_REFERENCE_CODE, companyId);
			}
			catch (Exception exception) {

				// Ignore

			}

			countDownLatch.countDown();

			countDownLatch.await(10, TimeUnit.MILLISECONDS);
		}

		return oAuth2Application;
	}

	private List<String> _getScopeAliasesList(long companyId) throws Exception {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.
				getOAuth2ApplicationByExternalReferenceCode(
					_EXTERNAL_REFERENCE_CODE, companyId);

		return _oAuth2ApplicationScopeAliasesLocalService.getScopeAliasesList(
			oAuth2Application.getOAuth2ApplicationScopeAliasesId());
	}

	private void _testActivateWithUnregisteredCompany() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Semaphore semaphore = new Semaphore(0);

		ServiceRegistration<PortalK8sConfigMapModifier> serviceRegistration =
			bundleContext.registerService(
				PortalK8sConfigMapModifier.class,
				(configMapModelConsumer, configMapName) -> {
					semaphore.release();

					return PortalK8sConfigMapModifier.Result.UNCHANGED;
				},
				MapUtil.singletonDictionary(
					"service.ranking", Integer.MAX_VALUE));

		try {
			String webId = RandomTestUtil.randomString() + ".com";

			Configuration configuration = _createFactoryConfiguration(
				OAuth2ProviderApplicationUserAgentConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"_portalK8sConfigMapModifier.cardinality.minimum", 0
				).put(
					"baseURL", "http://foo.me"
				).put(
					"dxp.lxc.liferay.com.virtualInstanceId", webId
				).put(
					"projectName", RandomTestUtil.randomString()
				).build());

			try {
				_company = CompanyTestUtil.addCompanyWithWebId(webId);

				semaphore.acquire();

				Assert.assertNotNull(
					_oAuth2ApplicationLocalService.
						fetchOAuth2ApplicationByExternalReferenceCode(
							_EXTERNAL_REFERENCE_CODE, _company.getCompanyId()));
			}
			finally {
				ConfigurationTestUtil.deleteConfiguration(configuration);
			}
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testActivateWithUnresolvedScopeAliases() throws Exception {
		JAXRSWhiteboardTestUtil.ensureReady();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		String name = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.application.base", "/" + name
			).put(
				"osgi.jaxrs.extension.select", "(liferay.extension=OAuth2)"
			).put(
				"osgi.jaxrs.name", name
			).build();

		ServiceRegistration<Application> applicationServiceRegistration =
			bundleContext.registerService(
				Application.class, new TestApplication(), properties);

		long companyId = TestPropsValues.getCompanyId();

		Collection<String> scopeAliases = _scopeLocator.getScopeAliases(
			companyId, name);

		Assert.assertFalse(scopeAliases.toString(), scopeAliases.isEmpty());

		applicationServiceRegistration.unregister();

		Semaphore semaphore = new Semaphore(0);

		ServiceRegistration<PortalK8sConfigMapModifier>
			portalK8sConfigMapModifierServiceRegistration =
				bundleContext.registerService(
					PortalK8sConfigMapModifier.class,
					(configMapModelConsumer, configMapName) -> {
						semaphore.release();

						return PortalK8sConfigMapModifier.Result.UNCHANGED;
					},
					MapUtil.singletonDictionary(
						"service.ranking", Integer.MAX_VALUE));

		try {
			Configuration configuration = _createFactoryConfiguration(
				OAuth2ProviderApplicationUserAgentConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"_portalK8sConfigMapModifier.cardinality.minimum", 0
				).put(
					"baseURL", "http://foo.me"
				).put(
					"companyId", companyId
				).put(
					"projectName", RandomTestUtil.randomString()
				).put(
					"scopes", scopeAliases.toArray(new String[0])
				).build());

			semaphore.acquire();

			List<String> errorLogEntries = new ArrayList<>();

			LogListener logListener = logEntry -> {
				if (Objects.equals(logEntry.getLogLevel(), LogLevel.ERROR)) {
					errorLogEntries.add(logEntry.getMessage());
				}
			};

			_logReaderService.addLogListener(logListener);

			try {
				List<String> scopeAliasesList = _getScopeAliasesList(companyId);

				Assert.assertTrue(
					scopeAliasesList.toString(), scopeAliasesList.isEmpty());

				try {
					TransactionInvokerUtil.invoke(
						TransactionConfig.Factory.create(
							Propagation.REQUIRED,
							new Class<?>[] {Exception.class}),
						() -> {
							ServiceRegistration<Application>
								serviceRegistration =
									bundleContext.registerService(
										Application.class,
										new TestApplication(), properties);

							serviceRegistration.unregister();

							throw new PortalException("Roll back");
						});

					Assert.fail();
				}
				catch (Throwable throwable) {
					Assert.assertEquals("Roll back", throwable.getMessage());
				}

				Assert.assertEquals(
					new HashSet<>(scopeAliases),
					new HashSet<>(_getScopeAliasesList(companyId)));
				Assert.assertTrue(
					errorLogEntries.toString(), errorLogEntries.isEmpty());
			}
			finally {
				_logReaderService.removeLogListener(logListener);

				semaphore.drainPermits();

				ConfigurationTestUtil.deleteConfiguration(configuration);

				semaphore.acquire();
			}
		}
		finally {
			portalK8sConfigMapModifierServiceRegistration.unregister();
		}
	}

	private void _testGetFactoryConfiguration(
			String className, long companyId,
			Dictionary<String, Object> properties, User user)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				className, LoggerTestUtil.INFO)) {

			Configuration configuration = _createFactoryConfiguration(
				className, properties);

			Assert.assertTrue(ListUtil.isEmpty(logCapture.getLogEntries()));

			try {
				OAuth2Application oAuth2Application = _fetchOAuthApplication(
					companyId);

				Assert.assertNotNull(oAuth2Application);
				Assert.assertEquals(
					user.getUserId(),
					oAuth2Application.getClientCredentialUserId());
				Assert.assertEquals(
					_EXTERNAL_REFERENCE_CODE, oAuth2Application.getName());
			}
			finally {
				ConfigurationTestUtil.deleteConfiguration(configuration);
			}
		}

		Thread.sleep(200);

		OAuth2Application oAuth2Application = _fetchOAuthApplication(companyId);

		Assert.assertNull(oAuth2Application);
	}

	private static final String _EXTERNAL_REFERENCE_CODE = "foo";

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private LogReaderService _logReaderService;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Inject
	private ScopeLocator _scopeLocator;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	private static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(new TestGetResource());
		}

	}

}