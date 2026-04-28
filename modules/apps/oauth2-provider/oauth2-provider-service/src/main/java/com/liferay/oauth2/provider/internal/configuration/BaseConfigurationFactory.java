/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.configuration;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 */
public abstract class BaseConfigurationFactory {

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		Log log = getLog();

		if (log.isDebugEnabled()) {
			log.debug("Activate " + properties);
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

							doActivate(
								properties, companyId, externalReferenceCode);
						});

					return null;
				}));
	}

	@Deactivate
	protected void deactivate(Integer reason) throws PortalException {
		if ((oAuth2Application == null) ||
			(reason !=
				ComponentConstants.DEACTIVATION_REASON_CONFIGURATION_DELETED)) {

			return;
		}

		Log log = getLog();

		if (log.isDebugEnabled()) {
			log.debug("Deactivating " + oAuth2Application);
		}

		ConfigurationFactoryUtil.executeAsCompany(
			companyLocalService,
			HashMapBuilder.<String, Object>put(
				"companyId", oAuth2Application.getCompanyId()
			).build(),
			companyId -> {
				oAuth2ApplicationLocalService.deleteOAuth2Application(
					oAuth2Application);

				if (Validator.isNull(_configMapName)) {
					return;
				}

				PortalK8sConfigMapModifier portalK8sConfigMapModifier =
					_portalK8sConfigMapModifierSnapshot.get();

				portalK8sConfigMapModifier.modifyConfigMap(
					configMapModel -> {
						_extensionProperties.forEach(
							configMapModel.data()::remove);

						Map<String, String> labels = configMapModel.labels();

						labels.put(
							"dxp.lxc.liferay.com/virtualInstanceId",
							_virtualInstanceId);
						labels.put(
							"ext.lxc.liferay.com/projectName", _projectName);
					},
					_configMapName);
			});
	}

	protected abstract void doActivate(
			Map<String, Object> properties, long companyId,
			String externalReferenceCode)
		throws Exception;

	protected HashMapBuilder.HashMapWrapper<String, String>
		getExtensionProperties(
			String externalReferenceCode, OAuth2Application oAuth2Application) {

		return HashMapBuilder.put(
			externalReferenceCode + ".oauth2.authorization.uri",
			"/o/oauth2/authorize"
		).put(
			externalReferenceCode + ".oauth2.home.page.uri",
			oAuth2Application.getHomePageURL()
		).put(
			externalReferenceCode + ".oauth2.introspection.uri",
			"/o/oauth2/introspect"
		).put(
			externalReferenceCode + ".oauth2.jwks.uri", "/o/oauth2/jwks"
		).put(
			externalReferenceCode + ".oauth2.redirect.uris",
			"/o/oauth2/redirect"
		).put(
			externalReferenceCode + ".oauth2.token.uri", "/o/oauth2/token"
		);
	}

	protected String getHomePageURL(String homePageURL, String baseURL) {
		if (Validator.isNull(homePageURL)) {
			return baseURL;
		}

		return homePageURL;
	}

	protected abstract Log getLog();

	protected String getName(String name, String defaultValue) {
		if (Validator.isNotNull(name)) {
			return name;
		}

		return defaultValue;
	}

	protected String getServiceAddress(Company company) {
		return Http.HTTPS_WITH_SLASH.concat(company.getVirtualHostname());
	}

	protected void logOAuth2Application(OAuth2Application oAuth2Application) {
		Log log = getLog();

		if (log.isDebugEnabled()) {
			log.debug(
				StringBundler.concat(
					"OAuth 2 application with external reference code ",
					oAuth2Application.getExternalReferenceCode(),
					" and company ID ", oAuth2Application.getCompanyId(),
					" has client ID ", oAuth2Application.getClientId()));
		}
	}

	protected void modifyConfigMap(
		Company company, Map<String, String> extensionProperties,
		Map<String, Object> properties) {

		_extensionProperties = extensionProperties;

		PortalK8sConfigMapModifier portalK8sConfigMapModifier =
			_portalK8sConfigMapModifierSnapshot.get();

		String projectName = GetterUtil.getString(
			properties.get("ext.lxc.liferay.com.projectName"),
			(String)properties.get("projectName"));

		String serviceIdOrProjectName = GetterUtil.getString(
			properties.get("ext.lxc.liferay.com.serviceId"), projectName);

		if ((portalK8sConfigMapModifier == null) ||
			Validator.isNull(serviceIdOrProjectName)) {

			return;
		}

		_configMapName = StringBundler.concat(
			serviceIdOrProjectName, StringPool.DASH, company.getWebId(),
			"-lxc-ext-init-metadata");
		_projectName = projectName;
		_virtualInstanceId = company.getWebId();

		portalK8sConfigMapModifier.modifyConfigMap(
			configMapModel -> {
				Map<String, String> data = configMapModel.data();

				extensionProperties.forEach(data::put);

				Map<String, String> labels = configMapModel.labels();

				labels.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					_virtualInstanceId);
				labels.put(
					"ext.lxc.liferay.com/projectId",
					GetterUtil.getString(
						properties.get("ext.lxc.liferay.com.projectId"),
						GetterUtil.getString(
							properties.get("ext.lxc.liferay.com/projectId"))));
				labels.put("ext.lxc.liferay.com/projectName", _projectName);
				labels.put(
					"ext.lxc.liferay.com/projectUid",
					GetterUtil.getString(
						properties.get("ext.lxc.liferay.com.projectUid"),
						GetterUtil.getString(
							properties.get("ext.lxc.liferay.com/projectUid"))));
				labels.put(
					"ext.lxc.liferay.com/serviceId",
					GetterUtil.getString(
						properties.get("ext.lxc.liferay.com.serviceId"),
						GetterUtil.getString(
							properties.get("ext.lxc.liferay.com/serviceId"))));
				labels.put(
					"ext.lxc.liferay.com/serviceUid",
					GetterUtil.getString(
						properties.get("ext.lxc.liferay.com.serviceUid"),
						GetterUtil.getString(
							properties.get("ext.lxc.liferay.com/serviceUid"))));
				labels.put("lxc.liferay.com/metadataType", "ext-init");
			},
			_configMapName);
	}

	protected void updateScopes(
			OAuth2Application oAuth2Application, List<String> scopeAliasesList)
		throws Exception {

		boolean update = true;

		OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
			oAuth2ApplicationScopeAliasesLocalService.
				fetchOAuth2ApplicationScopeAliases(
					oAuth2Application.getOAuth2ApplicationId(),
					scopeAliasesList);

		if (oAuth2ApplicationScopeAliases != null) {
			List<String> currentScopeAliasesList =
				oAuth2ApplicationScopeAliasesLocalService.getScopeAliasesList(
					oAuth2ApplicationScopeAliases.
						getOAuth2ApplicationScopeAliasesId());

			if (currentScopeAliasesList.containsAll(scopeAliasesList) &&
				(currentScopeAliasesList.size() == scopeAliasesList.size())) {

				update = false;
			}
		}

		if (update) {

			// Make sure all scopes are registered

			scopeLocator.getLiferayOAuth2Scopes(
				oAuth2Application.getCompanyId());

			oAuth2ApplicationLocalService.updateScopeAliases(
				oAuth2Application.getUserId(), oAuth2Application.getUserName(),
				oAuth2Application.getOAuth2ApplicationId(), scopeAliasesList);
		}
	}

	@Reference
	protected CompanyLocalService companyLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	protected ModuleServiceLifecycle moduleServiceLifecycle;

	protected volatile OAuth2Application oAuth2Application;

	@Reference
	protected OAuth2ApplicationLocalService oAuth2ApplicationLocalService;

	@Reference
	protected OAuth2ApplicationScopeAliasesLocalService
		oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	protected ScopeLocator scopeLocator;

	@Reference
	protected UserLocalService userLocalService;

	private static final Snapshot<PortalK8sConfigMapModifier>
		_portalK8sConfigMapModifierSnapshot = new Snapshot<>(
			BaseConfigurationFactory.class, PortalK8sConfigMapModifier.class,
			null, true);

	private volatile String _configMapName;
	private volatile Map<String, String> _extensionProperties;
	private volatile String _projectName;
	private volatile String _virtualInstanceId;

}