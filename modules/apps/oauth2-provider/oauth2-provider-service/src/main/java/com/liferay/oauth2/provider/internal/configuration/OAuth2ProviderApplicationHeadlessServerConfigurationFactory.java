/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.configuration;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationHeadlessServerConfiguration;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationHeadlessServerConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "_portalK8sConfigMapModifier.cardinality.minimum=1", service = {}
)
public class OAuth2ProviderApplicationHeadlessServerConfigurationFactory
	extends BaseConfigurationFactory {

	@Override
	protected void doActivate(
			Map<String, Object> properties, long companyId,
			String externalReferenceCode)
		throws Exception {

		Collection<String> scopeAliases = _scopeLocator.getScopeAliases(
			companyId);

		OAuth2ProviderApplicationHeadlessServerConfiguration
			oAuth2ProviderApplicationHeadlessServerConfiguration =
				ConfigurableUtil.createConfigurable(
					OAuth2ProviderApplicationHeadlessServerConfiguration.class,
					properties);

		List<String> scopeAliasesList = TransformUtil.transformToList(
			oAuth2ProviderApplicationHeadlessServerConfiguration.scopes(),
			scopeAlias -> {
				if (!scopeAliases.contains(scopeAlias)) {
					for (String curScopeAlias : scopeAliases) {
						if (StringUtil.equalsIgnoreCase(
								curScopeAlias, scopeAlias)) {

							return curScopeAlias;
						}
					}
				}

				return scopeAlias;
			});

		oAuth2Application = _addOrUpdateOAuth2Application(
			companyId, externalReferenceCode,
			oAuth2ProviderApplicationHeadlessServerConfiguration,
			scopeAliasesList);

		if (_log.isDebugEnabled()) {
			_log.debug("OAuth 2 application " + oAuth2Application);
		}

		modifyConfigMap(
			companyLocalService.getCompanyById(companyId),
			getBaseExtensionProperties(
				externalReferenceCode, oAuth2Application
			).put(
				externalReferenceCode + ".oauth2.headless.server.audience",
				oAuth2Application.getHomePageURL()
			).put(
				externalReferenceCode + ".oauth2.headless.server.client.id",
				oAuth2Application.getClientId()
			).put(
				externalReferenceCode + ".oauth2.headless.server.client.secret",
				oAuth2Application.getClientSecret()
			).put(
				externalReferenceCode + ".oauth2.headless.server.scopes",
				StringUtil.merge(scopeAliasesList, StringPool.NEW_LINE)
			).build(),
			properties);
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private OAuth2Application _addOrUpdateOAuth2Application(
			long companyId, String externalReferenceCode,
			OAuth2ProviderApplicationHeadlessServerConfiguration
				oAuth2ProviderApplicationHeadlessServerConfiguration,
			List<String> scopeAliasesList)
		throws Exception {

		OAuth2Application oAuth2Application =
			oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					externalReferenceCode, companyId);

		User user = userLocalService.getGuestUser(companyId);

		User serviceUser = null;
		String clientId = OAuth2SecureRandomGenerator.generateClientId();
		String clientSecret =
			OAuth2SecureRandomGenerator.generateClientSecret();

		if (oAuth2Application != null) {
			serviceUser = userLocalService.getUserById(
				companyId, oAuth2Application.getClientCredentialUserId());
			clientId = oAuth2Application.getClientId();
			clientSecret = oAuth2Application.getClientSecret();
		}
		else {
			serviceUser = _getServiceUser(
				companyId,
				oAuth2ProviderApplicationHeadlessServerConfiguration);
		}

		String homePageURL = getHomePageURL(
			oAuth2ProviderApplicationHeadlessServerConfiguration.homePageURL(),
			oAuth2ProviderApplicationHeadlessServerConfiguration.baseURL());

		oAuth2Application =
			oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
				externalReferenceCode, user.getUserId(), user.getScreenName(),
				ListUtil.fromArray(
					GrantType.CLIENT_CREDENTIALS, GrantType.JWT_BEARER),
				"client_secret_post", serviceUser.getUserId(), clientId,
				ClientProfile.HEADLESS_SERVER.id(), clientSecret,
				oAuth2ProviderApplicationHeadlessServerConfiguration.
					description(),
				Arrays.asList("token.introspection"), homePageURL, 0, null,
				getName(
					oAuth2ProviderApplicationHeadlessServerConfiguration.name(),
					externalReferenceCode),
				oAuth2ProviderApplicationHeadlessServerConfiguration.
					privacyPolicyURL(),
				Collections.emptyList(), false, true, null,
				new ServiceContext());

		updateScopes(oAuth2Application, scopeAliasesList);

		logOAuth2Application(oAuth2Application);

		return oAuth2Application;
	}

	private User _getServiceUser(
			long companyId,
			OAuth2ProviderApplicationHeadlessServerConfiguration
				oAuth2ProviderApplicationHeadlessServerConfiguration)
		throws Exception {

		String userAccountEmailAddress =
			oAuth2ProviderApplicationHeadlessServerConfiguration.
				userAccountEmailAddress();
		String userAccountScreenName =
			oAuth2ProviderApplicationHeadlessServerConfiguration.
				userAccountScreenName();

		if (!Objects.equals(
				_COMPANY_DEFAULT_USER_TOKEN, userAccountEmailAddress) &&
			Objects.equals(
				_COMPANY_DEFAULT_USER_TOKEN, userAccountScreenName)) {

			if (!Validator.isEmailAddress(userAccountEmailAddress)) {
				throw new IllegalArgumentException(
					"User account email address is not an email address");
			}

			return userLocalService.getUserByEmailAddress(
				companyId, userAccountEmailAddress);
		}

		if (Validator.isNull(userAccountScreenName)) {
			throw new IllegalArgumentException(
				"User account screen name is null");
		}

		if (Objects.equals(
				_COMPANY_DEFAULT_USER_TOKEN, userAccountScreenName)) {

			return userLocalService.getUserByScreenName(
				companyId, UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT);
		}

		return userLocalService.getUserByScreenName(
			companyId, userAccountScreenName);
	}

	private static final String _COMPANY_DEFAULT_USER_TOKEN =
		"<company.default.user>";

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2ProviderApplicationHeadlessServerConfigurationFactory.class);

	@Reference
	private ScopeLocator _scopeLocator;

}