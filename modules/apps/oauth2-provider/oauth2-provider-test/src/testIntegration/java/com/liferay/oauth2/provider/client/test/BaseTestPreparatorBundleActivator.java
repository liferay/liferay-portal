/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalServiceUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import jakarta.ws.rs.core.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.ListIterator;

import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carlos Sierra Andrés
 */
public abstract class BaseTestPreparatorBundleActivator
	implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		autoCloseables = new ArrayList<>();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.name", "Default"
			).put(
				"service.ranking", Integer.MAX_VALUE
			).build();

		registerPrefixHandler(
			PrefixHandler.PASS_THROUGH_PREFIX_HANDLER, properties);
		registerScopeMapper(ScopeMapper.PASS_THROUGH_SCOPE_MAPPER, properties);

		try {
			prepareTest();
		}
		catch (Exception exception) {
			_cleanUp();

			throw new RuntimeException(exception);
		}
	}

	@Override
	public void stop(BundleContext bundleContext) {
		_cleanUp();
	}

	protected User addAdminUser(Company company) throws Exception {
		User user = UserTestUtil.addCompanyAdminUser(company);

		autoCloseables.add(
			() -> UserLocalServiceUtil.deleteUser(user.getUserId()));

		return user;
	}

	protected OAuth2Authorization addOAuth2Authorization(
		long companyId, User user, OAuth2Application oAuth2Application,
		String accessTokenContent, Date accessTokenCreateDate,
		Date accessTokenExpirationDate) {

		return addOAuth2Authorization(
			companyId, user, oAuth2Application, accessTokenContent,
			accessTokenCreateDate, accessTokenExpirationDate, null, null, null);
	}

	protected OAuth2Authorization addOAuth2Authorization(
		long companyId, User user, OAuth2Application oAuth2Application,
		String accessTokenContent, Date accessTokenCreateDate,
		Date accessTokenExpirationDate, String refreshTokenContent,
		Date refreshTokenCreateDate, Date refreshTokenExpirationDate) {

		ServiceReference<OAuth2AuthorizationLocalService> serviceReference =
			bundleContext.getServiceReference(
				OAuth2AuthorizationLocalService.class);

		OAuth2AuthorizationLocalService oAuth2AuthorizationLocalService =
			bundleContext.getService(serviceReference);

		autoCloseables.add(() -> bundleContext.ungetService(serviceReference));

		OAuth2Authorization oAuth2Authorization =
			oAuth2AuthorizationLocalService.addOAuth2Authorization(
				companyId, user.getUserId(), user.getFullName(),
				oAuth2Application.getOAuth2ApplicationId(),
				oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
				accessTokenContent, accessTokenCreateDate,
				accessTokenExpirationDate, null, "localhost", "127.0.0.1",
				refreshTokenContent, refreshTokenCreateDate,
				refreshTokenExpirationDate);

		autoCloseables.add(
			() -> {
				OAuth2Authorization fetchedAuth2Authorization =
					OAuth2AuthorizationLocalServiceUtil.
						fetchOAuth2Authorization(
							oAuth2Authorization.getOAuth2AuthorizationId());

				if (fetchedAuth2Authorization != null) {
					OAuth2AuthorizationLocalServiceUtil.
						deleteOAuth2Authorization(fetchedAuth2Authorization);
				}
			});

		return oAuth2Authorization;
	}

	protected User addUser(Company company) throws Exception {
		User user = UserTestUtil.addUser(company);

		autoCloseables.add(
			() -> UserLocalServiceUtil.deleteUser(user.getUserId()));

		return user;
	}

	protected Company createCompany(String hostName) throws PortalException {
		String virtualHostname = hostName + ".xyz";

		Company company = CompanyLocalServiceUtil.addCompany(
			null, hostName, virtualHostname, virtualHostname, 0, true, true,
			null, null, null, null, null, null);

		autoCloseables.add(
			() -> CompanyLocalServiceUtil.deleteCompany(
				company.getCompanyId()));

		return company;
	}

	protected void createFactoryConfiguration(
		String factoryPid, Dictionary<String, Object> properties) {

		try {
			String pid = ConfigurationTestUtil.createFactoryConfiguration(
				factoryPid, properties);

			autoCloseables.add(
				() -> ConfigurationTestUtil.deleteConfiguration(pid));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId,
			Arrays.asList(
				GrantType.CLIENT_CREDENTIALS,
				GrantType.RESOURCE_OWNER_PASSWORD),
			Arrays.asList("everything", "everything.read"));
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId,
			List<GrantType> allowedGrantTypesList, boolean rememberDevice,
			List<String> scopeAliasesList, boolean trustedApplication)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, BaseClientTestCase.CLIENT_SECRET,
			allowedGrantTypesList,
			Collections.singletonList(
				"http://redirecturi:" + PortalUtil.getPortalServerPort(false)),
			rememberDevice, scopeAliasesList, trustedApplication);
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId,
			List<GrantType> allowedGrantTypesList,
			List<String> scopeAliasesList)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, BaseClientTestCase.CLIENT_SECRET,
			allowedGrantTypesList,
			Collections.singletonList(
				"http://redirecturi:" + PortalUtil.getPortalServerPort(false)),
			scopeAliasesList);
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId,
			List<GrantType> allowedGrantTypesList, String name,
			List<String> scopeAliasesList)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, BaseClientTestCase.CLIENT_SECRET,
			allowedGrantTypesList, name,
			Collections.singletonList(
				"http://redirecturi:" + PortalUtil.getPortalServerPort(false)),
			scopeAliasesList);
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId,
			List<String> scopeAliasesList)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId,
			Arrays.asList(
				GrantType.CLIENT_CREDENTIALS,
				GrantType.RESOURCE_OWNER_PASSWORD),
			false, scopeAliasesList, false);
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId, String clientSecret,
			List<GrantType> allowedGrantTypesList,
			List<String> redirectURIsList, boolean rememberDevice,
			List<String> scopeAliasesList, boolean trustedApplication)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, clientSecret, allowedGrantTypesList,
			OAuthConstants.TOKEN_ENDPOINT_AUTH_POST, null, redirectURIsList,
			rememberDevice, scopeAliasesList, trustedApplication);
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId, String clientSecret,
			List<GrantType> allowedGrantTypesList,
			List<String> redirectURIsList, List<String> scopeAliasesList)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, clientSecret, allowedGrantTypesList,
			redirectURIsList, false, scopeAliasesList, false);
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId, String clientSecret,
			List<GrantType> allowedGrantTypesList, String name,
			List<String> redirectURIsList, List<String> scopeAliasesList)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, clientSecret, allowedGrantTypesList,
			OAuthConstants.TOKEN_ENDPOINT_AUTH_POST, null, name,
			redirectURIsList, false, scopeAliasesList, false);
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId, String clientSecret,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, String jwks,
			List<String> redirectURIsList, boolean rememberDevice,
			List<String> scopeAliasesList, boolean trustedApplication)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, clientSecret, allowedGrantTypesList,
			clientAuthenticationMethod, jwks, RandomTestUtil.randomString(),
			redirectURIsList, rememberDevice, scopeAliasesList,
			trustedApplication);
	}

	protected OAuth2Application createOAuth2Application(
			long companyId, User user, String clientId, String clientSecret,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, String jwks, String name,
			List<String> redirectURIsList, boolean rememberDevice,
			List<String> scopeAliasesList, boolean trustedApplication)
		throws PortalException {

		ServiceReference<OAuth2ApplicationLocalService> serviceReference =
			bundleContext.getServiceReference(
				OAuth2ApplicationLocalService.class);

		OAuth2ApplicationLocalService oAuth2ApplicationLocalService =
			bundleContext.getService(serviceReference);

		autoCloseables.add(() -> bundleContext.ungetService(serviceReference));

		OAuth2Application oAuth2Application =
			oAuth2ApplicationLocalService.addOAuth2Application(
				companyId, user.getUserId(), user.getFullName(),
				allowedGrantTypesList, clientAuthenticationMethod,
				user.getUserId(), clientId, 0, clientSecret,
				RandomTestUtil.randomString(),
				Collections.singletonList("token.introspection"),
				"http://localhost:" + PortalUtil.getPortalServerPort(false), 0,
				jwks, name,
				"http://localhost:" + PortalUtil.getPortalServerPort(false),
				redirectURIsList, rememberDevice, scopeAliasesList,
				trustedApplication, new ServiceContext());

		autoCloseables.add(
			() -> {
				OAuth2Application fetchedOAuth2Application =
					oAuth2ApplicationLocalService.fetchOAuth2Application(
						oAuth2Application.getOAuth2ApplicationId());

				if (fetchedOAuth2Application != null) {
					oAuth2ApplicationLocalService.deleteOAuth2Application(
						fetchedOAuth2Application);
				}
			});

		return oAuth2Application;
	}

	protected OAuth2Application createOAuth2ApplicationWithClientSecretJWT(
			long companyId, User user, String clientId, String clientSecret,
			List<GrantType> allowedGrantTypesList,
			List<String> scopeAliasesList)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, clientSecret, allowedGrantTypesList,
			"client_secret_jwt", null, Arrays.asList(), false, scopeAliasesList,
			false);
	}

	protected OAuth2Application createOAuth2ApplicationWithClientSecretPost(
			long companyId, User user, String clientId, String clientSecret,
			List<GrantType> allowedGrantTypesList,
			List<String> scopeAliasesList)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, clientSecret, allowedGrantTypesList,
			"client_secret_post", null, Arrays.asList(), false,
			scopeAliasesList, false);
	}

	protected OAuth2Application createOAuth2ApplicationWithNone(
			long companyId, User user, String clientId,
			List<GrantType> allowedGrantTypesList,
			List<String> redirectURIsList, boolean rememberDevice,
			List<String> scopeAliasesList, boolean trustedApplication)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, null, allowedGrantTypesList,
			OAuthConstants.TOKEN_ENDPOINT_AUTH_NONE, null, redirectURIsList,
			rememberDevice, scopeAliasesList, trustedApplication);
	}

	protected OAuth2Application createOAuth2ApplicationWithPrivateKeyJWT(
			long companyId, User user, String clientId,
			List<GrantType> allowedGrantTypesList, String jwks,
			List<String> scopeAliasesList)
		throws PortalException {

		return createOAuth2Application(
			companyId, user, clientId, null, allowedGrantTypesList,
			"private_key_jwt", jwks, Arrays.asList(), false, scopeAliasesList,
			false);
	}

	protected void createServiceAccessProfile(
		long userId, String allowedServiceSignatures, boolean defaultSAPEntry,
		boolean enabled, String name) {

		ServiceReference<SAPEntryLocalService> serviceReference =
			bundleContext.getServiceReference(SAPEntryLocalService.class);

		SAPEntryLocalService sapEntryLocalService = bundleContext.getService(
			serviceReference);

		try {
			autoCloseables.add(
				() -> bundleContext.ungetService(serviceReference));

			SAPEntry sapEntry = sapEntryLocalService.addSAPEntry(
				userId, allowedServiceSignatures, defaultSAPEntry, enabled,
				name,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), name
				).build(),
				new ServiceContext());

			autoCloseables.add(
				() -> sapEntryLocalService.deleteSAPEntry(
					sapEntry.getSapEntryId()));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected abstract void prepareTest() throws Exception;

	protected ServiceRegistration<Application> registerJaxRsApplication(
		Application application, String path,
		Dictionary<String, Object> properties) {

		if ((properties == null) || properties.isEmpty()) {
			properties = new HashMapDictionary<>();
		}

		properties.put("oauth2.test.application", "true");
		properties.put("osgi.jaxrs.application.base", "/oauth2-test/" + path);
		properties.put(
			"osgi.jaxrs.extension.select", "(liferay.extension=OAuth2)");

		ServiceRegistration<Application> serviceRegistration =
			bundleContext.registerService(
				Application.class, application, properties);

		autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	protected ServiceRegistration<PrefixHandlerFactory> registerPrefixHandler(
		PrefixHandler prefixHandler, Dictionary<String, Object> properties) {

		ServiceRegistration<PrefixHandlerFactory> serviceRegistration =
			bundleContext.registerService(
				PrefixHandlerFactory.class, a -> prefixHandler, properties);

		autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	protected ServiceRegistration<ScopeDescriptor> registerScopeDescriptor(
		ScopeDescriptor scopeDescriptor,
		Dictionary<String, Object> properties) {

		ServiceRegistration<ScopeDescriptor> serviceRegistration =
			bundleContext.registerService(
				ScopeDescriptor.class, scopeDescriptor, properties);

		autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	protected ServiceRegistration<ScopeFinder> registerScopeFinder(
		ScopeFinder scopeFinder, Dictionary<String, Object> properties) {

		if ((properties == null) || properties.isEmpty()) {
			properties = new HashMapDictionary<>();
		}

		ServiceRegistration<ScopeFinder> serviceRegistration =
			bundleContext.registerService(
				ScopeFinder.class, scopeFinder, properties);

		autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	protected ServiceRegistration<ScopeMapper> registerScopeMapper(
		ScopeMapper scopeMapper, Dictionary<String, Object> properties) {

		ServiceRegistration<ScopeMapper> serviceRegistration =
			bundleContext.registerService(
				ScopeMapper.class, scopeMapper, properties);

		autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	protected void updateOAuth2ProviderConfiguration(
			Dictionary<String, Object> properties)
		throws Exception {

		autoCloseables.add(
			new ConfigurationTemporarySwapper(
				OAuth2ProviderConfiguration.class.getName(), properties));
	}

	protected ArrayList<AutoCloseable> autoCloseables;
	protected BundleContext bundleContext;

	private void _cleanUp() {
		ListIterator<AutoCloseable> listIterator = autoCloseables.listIterator(
			autoCloseables.size());

		while (listIterator.hasPrevious()) {
			AutoCloseable previousAutoCloseable = listIterator.previous();

			try {
				previousAutoCloseable.close();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseTestPreparatorBundleActivator.class);

}