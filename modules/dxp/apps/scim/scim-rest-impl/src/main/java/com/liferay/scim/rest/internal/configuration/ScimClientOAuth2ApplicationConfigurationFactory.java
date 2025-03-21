/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.configuration;

import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.scim.rest.internal.provider.ScimClientBearerTokenProvider;
import com.liferay.scim.rest.util.ScimClientUtil;

import jakarta.ws.rs.core.Application;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Olivér Kecskeméty
 */
@Component(
	configurationPid = "com.liferay.scim.rest.internal.configuration.ScimClientOAuth2ApplicationConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class ScimClientOAuth2ApplicationConfigurationFactory {

	@Activate
	protected void activate(
			BundleContext bundleContext, Map<String, Object> properties)
		throws Exception {

		ConfigurationFactoryUtil.executeAsCompany(
			_companyLocalService, properties,
			companyId -> {
				ScimClientOAuth2ApplicationConfiguration
					scimClientOAuth2ApplicationConfiguration =
						ConfigurableUtil.createConfigurable(
							ScimClientOAuth2ApplicationConfiguration.class,
							properties);

				_oAuth2Application = _getOrAddOAuth2Application(
					companyId, scimClientOAuth2ApplicationConfiguration,
					GetterUtil.getLong(properties.get("userId")));

				_serviceRegistration = bundleContext.registerService(
					BearerTokenProvider.class,
					new ScimClientBearerTokenProvider(),
					HashMapDictionaryBuilder.<String, Object>put(
						"clientId", _oAuth2Application.getClientId()
					).put(
						"companyId", companyId.toString()
					).build());
			});
	}

	@Deactivate
	protected void deactivate(Integer reason) throws PortalException {
		if (reason !=
				ComponentConstants.DEACTIVATION_REASON_CONFIGURATION_DELETED) {

			return;
		}

		_oAuth2ApplicationLocalService.deleteOAuth2Application(
			_oAuth2Application);

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Removed OAuth2 application: " + _oAuth2Application.getName());
		}
	}

	private OAuth2Application _getOrAddOAuth2Application(
			long companyId,
			ScimClientOAuth2ApplicationConfiguration
				scimClientOAuth2ApplicationConfiguration,
			long userId)
		throws Exception {

		String clientId = ScimClientUtil.generateScimClientId(
			scimClientOAuth2ApplicationConfiguration.oAuth2ApplicationName());

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				companyId, clientId);

		if (oAuth2Application == null) {
			User user = _userLocalService.getUser(userId);

			oAuth2Application =
				_oAuth2ApplicationLocalService.addOAuth2Application(
					companyId, user.getUserId(), user.getScreenName(),
					ListUtil.fromArray(GrantType.CLIENT_CREDENTIALS),
					"client_secret_post", user.getUserId(), clientId,
					ClientProfile.HEADLESS_SERVER.id(),
					OAuth2SecureRandomGenerator.generateClientSecret(), null,
					Collections.emptyList(), null, 0, null,
					scimClientOAuth2ApplicationConfiguration.
						oAuth2ApplicationName(),
					null, Collections.emptyList(), false, false, null,
					new ServiceContext());

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Created OAuth2 application: " +
						oAuth2Application.getName());
			}
		}

		return _oAuth2ApplicationLocalService.updateScopeAliases(
			oAuth2Application.getUserId(), oAuth2Application.getUserName(),
			oAuth2Application.getOAuth2ApplicationId(),
			ListUtil.fromArray("Liferay.Scim.REST.everything"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ScimClientOAuth2ApplicationConfigurationFactory.class);

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private Collection<Application> _applications;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	private volatile OAuth2Application _oAuth2Application;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	private volatile ServiceRegistration<BearerTokenProvider>
		_serviceRegistration;

	@Reference
	private UserLocalService _userLocalService;

}