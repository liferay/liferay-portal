/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.configuration;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.redirect.OAuth2RedirectURIInterpolator;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "_portalK8sConfigMapModifier.cardinality.minimum=1", service = {}
)
public class OAuth2ProviderApplicationUserAgentConfigurationFactory
	extends BaseConfigurationFactory {

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Activate " + properties);
		}

		DependencyManagerSyncUtil.registerSyncCallable(
			new CompanyInheritableThreadLocalCallable<>(
				() -> {
					ConfigurationFactoryUtil.executeAsCompany(
						companyLocalService, properties,
						companyId -> {
							String externalReferenceCode =
								ConfigurationFactoryUtil.
									getExternalReferenceCode(properties);

							OAuth2ProviderApplicationUserAgentConfiguration
								oAuth2ProviderApplicationUserAgentConfiguration =
									ConfigurableUtil.createConfigurable(
										OAuth2ProviderApplicationUserAgentConfiguration.class,
										properties);

							Company company =
								companyLocalService.getCompanyById(companyId);

							List<String> scopeAliasesList = ListUtil.fromArray(
								oAuth2ProviderApplicationUserAgentConfiguration.
									scopes());

							oAuth2Application = _addOrUpdateOAuth2Application(
								companyId, externalReferenceCode,
								oAuth2ProviderApplicationUserAgentConfiguration,
								TransformUtil.transform(
									_virtualHostLocalService.getVirtualHosts(
										companyId),
									virtualHost -> StringBundler.concat(
										OAuth2RedirectURIInterpolator.
											TOKEN_PROTOCOL,
										Http.PROTOCOL_DELIMITER,
										virtualHost.getHostname(),
										OAuth2RedirectURIInterpolator.
											TOKEN_PORT_WITH_COLON,
										"/o/oauth2/redirect")),
								scopeAliasesList);

							if (_log.isDebugEnabled()) {
								_log.debug(
									"OAuth 2 application " + oAuth2Application);
							}

							modifyConfigMap(
								company,
								HashMapBuilder.put(
									externalReferenceCode +
										".oauth2.authorization.uri",
									"/o/oauth2/authorize"
								).put(
									externalReferenceCode +
										".oauth2.home.page.uri",
									oAuth2Application.getHomePageURL()
								).put(
									externalReferenceCode +
										".oauth2.introspection.uri",
									"/o/oauth2/introspect"
								).put(
									externalReferenceCode +
										".oauth2.redirect.uris",
									"/o/oauth2/redirect"
								).put(
									externalReferenceCode +
										".oauth2.user.agent.audience",
									oAuth2Application.getHomePageURL()
								).put(
									externalReferenceCode +
										".oauth2.user.agent.client.id",
									oAuth2Application.getClientId()
								).put(
									externalReferenceCode +
										".oauth2.user.agent.scopes",
									StringUtil.merge(
										scopeAliasesList, StringPool.NEW_LINE)
								).put(
									externalReferenceCode + ".oauth2.jwks.uri",
									"/o/oauth2/jwks"
								).put(
									externalReferenceCode + ".oauth2.token.uri",
									"/o/oauth2/token"
								).build(),
								properties);
						});

					return null;
				}));
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private OAuth2Application _addOrUpdateOAuth2Application(
			long companyId, String externalReferenceCode,
			OAuth2ProviderApplicationUserAgentConfiguration
				oAuth2ProviderApplicationUserAgentConfiguration,
			List<String> redirectURIsList, List<String> scopeAliasesList)
		throws Exception {

		User user = userLocalService.getGuestUser(companyId);

		String clientId = OAuth2SecureRandomGenerator.generateClientId();

		OAuth2Application oAuth2Application =
			oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (oAuth2Application != null) {
			clientId = oAuth2Application.getClientId();
		}

		String homePageURL = getHomePageURL(
			oAuth2ProviderApplicationUserAgentConfiguration.homePageURL(),
			oAuth2ProviderApplicationUserAgentConfiguration.baseURL());

		oAuth2Application =
			oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
				externalReferenceCode, user.getUserId(), user.getScreenName(),
				ListUtil.fromArray(
					GrantType.AUTHORIZATION_CODE_PKCE, GrantType.JWT_BEARER),
				"none", user.getUserId(), clientId,
				ClientProfile.USER_AGENT_APPLICATION.id(), null,
				oAuth2ProviderApplicationUserAgentConfiguration.description(),
				Arrays.asList("token.introspection"), homePageURL, 0, null,
				getName(
					oAuth2ProviderApplicationUserAgentConfiguration.name(),
					externalReferenceCode),
				oAuth2ProviderApplicationUserAgentConfiguration.
					privacyPolicyURL(),
				redirectURIsList, false, true, null, new ServiceContext());

		updateScopes(oAuth2Application, scopeAliasesList);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"OAuth 2 application with external reference code ",
					oAuth2Application.getExternalReferenceCode(),
					" and company ID ", oAuth2Application.getCompanyId(),
					" has client ID ", oAuth2Application.getClientId()));
		}

		return oAuth2Application;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2ProviderApplicationUserAgentConfigurationFactory.class);

	@Reference
	private VirtualHostLocalService _virtualHostLocalService;

}