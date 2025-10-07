/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.admin.rest.internal.resource.v1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.saml.admin.rest.dto.v1_0.Idp;
import com.liferay.saml.admin.rest.dto.v1_0.SamlProvider;
import com.liferay.saml.admin.rest.dto.v1_0.Sp;
import com.liferay.saml.admin.rest.resource.v1_0.SamlProviderResource;
import com.liferay.saml.constants.SamlProviderConfigurationKeys;
import com.liferay.saml.runtime.configuration.SamlConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.exception.CredentialException;
import com.liferay.saml.runtime.exception.EntityIdException;
import com.liferay.saml.runtime.metadata.LocalEntityManager;
import com.liferay.saml.util.PortletPropsKeys;

import java.io.Serializable;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.saml.runtime.configuration.SamlConfiguration",
	properties = "OSGI-INF/liferay/rest/v1_0/saml-provider.properties",
	scope = ServiceScope.PROTOTYPE, service = SamlProviderResource.class
)
public class SamlProviderResourceImpl extends BaseSamlProviderResourceImpl {

	@Override
	public SamlProvider getSamlProvider() throws Exception {
		_checkPermission();

		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		SamlProvider samlProvider = new SamlProvider() {
			{
				setEnabled(samlProviderConfiguration::enabled);
				setEntityId(samlProviderConfiguration::entityId);
				setSignMetadata(samlProviderConfiguration::signMetadata);
				setSslRequired(samlProviderConfiguration::sslRequired);
			}
		};

		String role = samlProviderConfiguration.role();

		if (SamlProviderConfigurationKeys.SAML_ROLE_IB.equals(role)) {
			samlProvider.setIdp(() -> _getIdp(samlProviderConfiguration));
			samlProvider.setRole(() -> SamlProvider.Role.IB);
			samlProvider.setSp(() -> _getSp(samlProviderConfiguration));
		}
		else if (SamlProviderConfigurationKeys.SAML_ROLE_IDP.equals(role)) {
			samlProvider.setIdp(() -> _getIdp(samlProviderConfiguration));
			samlProvider.setRole(() -> SamlProvider.Role.IDP);
		}
		else if (SamlProviderConfigurationKeys.SAML_ROLE_SP.equals(role)) {
			samlProvider.setRole(() -> SamlProvider.Role.SP);
			samlProvider.setSp(() -> _getSp(samlProviderConfiguration));
		}

		return samlProvider;
	}

	@Override
	public SamlProvider patchSamlProvider(SamlProvider samlProvider)
		throws Exception {

		_checkPermission();

		return _updateSamlProvider(
			samlProvider,
			_samlProviderConfigurationHelper.getSamlProviderConfiguration());
	}

	@Override
	public SamlProvider postSamlProvider(SamlProvider samlProvider)
		throws Exception {

		_checkPermission();

		return _updateSamlProvider(
			samlProvider, _defaultCompanySamlProviderConfiguration);
	}

	@Override
	public Page<SamlProvider> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		_checkPermission();

		return Page.of(Collections.singleton(getSamlProvider()));
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_samlConfiguration = ConfigurableUtil.createConfigurable(
			SamlConfiguration.class, properties);
		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new DefaultCompanyManagedServiceFactory(),
			HashMapDictionaryBuilder.put(
				Constants.SERVICE_PID,
				"com.liferay.saml.runtime.configuration." +
					"SamlProviderConfiguration"
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private void _authenticateLocalEntityCertificate(
			String certificateKeyPassword,
			LocalEntityManager.CertificateUsage certificateUsage,
			String entityId)
		throws Exception {

		try {
			_localEntityManager.authenticateLocalEntityCertificate(
				certificateKeyPassword, certificateUsage, entityId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new CredentialException(
				StringBundler.concat(
					"Unable to authenticate with the ", certificateUsage.name(),
					" certificate. Verify that the SAML KeyStore contains a ",
					"certificate for the entity ID and that it is protected ",
					"by the provided key credential password."));
		}
	}

	private void _checkPermission() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin(
				CompanyThreadLocal.getCompanyId())) {

			throw new PrincipalException.MustBeCompanyAdmin(
				permissionChecker.getUserId());
		}
	}

	private Idp _getIdp(SamlProviderConfiguration samlProviderConfiguration)
		throws Exception {

		return new Idp() {
			{
				setAuthnRequestSignatureRequired(
					samlProviderConfiguration::authnRequestSignatureRequired);
				setDefaultAssertionLifetime(
					samlProviderConfiguration::defaultAssertionLifetime);
				setSessionMaximumAge(
					samlProviderConfiguration::sessionMaximumAge);
				setSessionTimeout(samlProviderConfiguration::sessionTimeout);
			}
		};
	}

	private Sp _getSp(SamlProviderConfiguration samlProviderConfiguration)
		throws Exception {

		return new Sp() {
			{
				setAllowShowingTheLoginPortlet(
					samlProviderConfiguration::allowShowingTheLoginPortlet);
				setAssertionSignatureRequired(
					samlProviderConfiguration::assertionSignatureRequired);
				setClockSkew(samlProviderConfiguration::clockSkew);
				setLdapImportEnabled(
					samlProviderConfiguration::ldapImportEnabled);
				setSignAuthnRequest(
					samlProviderConfiguration::signAuthnRequest);
			}
		};
	}

	private boolean _isIdpRoleDisabled(
		boolean enabled, SamlProviderConfiguration samlProviderConfiguration) {

		if (_samlConfiguration.idpRoleConfigurationEnabled()) {
			return false;
		}

		String role = samlProviderConfiguration.role();

		if (Validator.isNull(role) ||
			!role.equals(SamlProviderConfigurationKeys.SAML_ROLE_IDP)) {

			return true;
		}

		if (!samlProviderConfiguration.enabled() && enabled) {
			return true;
		}

		return false;
	}

	private boolean _isValidRole(String role) {
		if (Validator.isBlank(role)) {
			return false;
		}

		if (role.equals(SamlProviderConfigurationKeys.SAML_ROLE_IDP) ||
			role.equals(SamlProviderConfigurationKeys.SAML_ROLE_SP)) {

			return true;
		}

		return false;
	}

	private void _setIdpProperties(
		Idp idp, SamlProviderConfiguration samlProviderConfiguration,
		UnicodeProperties unicodeProperties) {

		_setProperty(
			samlProviderConfiguration::defaultAssertionLifetime,
			PortletPropsKeys.SAML_IDP_ASSERTION_LIFETIME, unicodeProperties,
			_toNullableString(idp.getDefaultAssertionLifetime()));
		_setProperty(
			samlProviderConfiguration::authnRequestSignatureRequired,
			PortletPropsKeys.SAML_IDP_AUTHN_REQUEST_SIGNATURE_REQUIRED,
			unicodeProperties,
			_toNullableString(idp.getAuthnRequestSignatureRequired()));
		_setProperty(
			samlProviderConfiguration::sessionMaximumAge,
			PortletPropsKeys.SAML_IDP_SESSION_MAXIMUM_AGE, unicodeProperties,
			_toNullableString(idp.getSessionMaximumAge()));
		_setProperty(
			samlProviderConfiguration::sessionTimeout,
			PortletPropsKeys.SAML_IDP_SESSION_TIMEOUT, unicodeProperties,
			_toNullableString(idp.getSessionTimeout()));

		unicodeProperties.put(
			PortletPropsKeys.SAML_ROLE,
			SamlProviderConfigurationKeys.SAML_ROLE_IDP);
	}

	private void _setProperty(
		Supplier<Object> defaultSupplier, String key,
		UnicodeProperties unicodeProperties, String value) {

		if (value == null) {
			unicodeProperties.put(
				key, _toNullableString(defaultSupplier.get()));

			return;
		}

		unicodeProperties.put(key, value);
	}

	private void _setSamlProviderProperties(
		SamlProvider samlProvider,
		SamlProviderConfiguration samlProviderConfiguration,
		UnicodeProperties unicodeProperties) {

		_setProperty(
			samlProviderConfiguration::enabled, PortletPropsKeys.SAML_ENABLED,
			unicodeProperties, _toNullableString(samlProvider.getEnabled()));
		_setProperty(
			samlProviderConfiguration::entityId,
			PortletPropsKeys.SAML_ENTITY_ID, unicodeProperties,
			samlProvider.getEntityId());
		_setProperty(
			samlProviderConfiguration::keyStoreCredentialPassword,
			PortletPropsKeys.SAML_KEYSTORE_CREDENTIAL_PASSWORD,
			unicodeProperties,
			_toNullableString(samlProvider.getKeyStoreCredentialPassword()));
		_setProperty(
			samlProviderConfiguration::signMetadata,
			PortletPropsKeys.SAML_SIGN_METADATA, unicodeProperties,
			_toNullableString(samlProvider.getSignMetadata()));
		_setProperty(
			samlProviderConfiguration::sslRequired,
			PortletPropsKeys.SAML_SSL_REQUIRED, unicodeProperties,
			_toNullableString(samlProvider.getSslRequired()));
	}

	private void _setSpProperties(
			String entityId,
			SamlProviderConfiguration samlProviderConfiguration, Sp sp,
			UnicodeProperties unicodeProperties)
		throws Exception {

		if (sp.getKeyStoreEncryptionCredentialPassword() != null) {
			_authenticateLocalEntityCertificate(
				sp.getKeyStoreEncryptionCredentialPassword(),
				LocalEntityManager.CertificateUsage.ENCRYPTION, entityId);
		}

		_setProperty(
			samlProviderConfiguration::keyStoreCredentialPassword,
			PortletPropsKeys.SAML_KEYSTORE_ENCRYPTION_CREDENTIAL_PASSWORD,
			unicodeProperties,
			_toNullableString(sp.getKeyStoreEncryptionCredentialPassword()));
		_setProperty(
			samlProviderConfiguration::allowShowingTheLoginPortlet,
			PortletPropsKeys.SAML_SP_ALLOW_SHOWING_THE_LOGIN_PORTLET,
			unicodeProperties,
			_toNullableString(sp.getAllowShowingTheLoginPortlet()));
		_setProperty(
			samlProviderConfiguration::assertionSignatureRequired,
			PortletPropsKeys.SAML_SP_ASSERTION_SIGNATURE_REQUIRED,
			unicodeProperties,
			_toNullableString(sp.getAssertionSignatureRequired()));
		_setProperty(
			samlProviderConfiguration::clockSkew,
			PortletPropsKeys.SAML_SP_CLOCK_SKEW, unicodeProperties,
			_toNullableString(sp.getClockSkew()));
		_setProperty(
			samlProviderConfiguration::ldapImportEnabled,
			PortletPropsKeys.SAML_SP_LDAP_IMPORT_ENABLED, unicodeProperties,
			_toNullableString(sp.getLdapImportEnabled()));
		_setProperty(
			samlProviderConfiguration::signAuthnRequest,
			PortletPropsKeys.SAML_SP_SIGN_AUTHN_REQUEST, unicodeProperties,
			_toNullableString(sp.getSignAuthnRequest()));

		unicodeProperties.put(
			PortletPropsKeys.SAML_ROLE,
			SamlProviderConfigurationKeys.SAML_ROLE_SP);
	}

	private String _toNullableString(Object value) {
		if (value == null) {
			return null;
		}

		return String.valueOf(value);
	}

	private SamlProvider _updateSamlProvider(
			SamlProvider samlProvider,
			SamlProviderConfiguration samlProviderConfiguration)
		throws Exception {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			false
		).build();

		_setSamlProviderProperties(
			samlProvider, samlProviderConfiguration, unicodeProperties);

		String entityId = samlProvider.getEntityId();

		if (Validator.isNotNull(entityId)) {
			if (entityId.length() > 1024) {
				throw new EntityIdException(
					"Entity ID is longer than 1024 characters");
			}
		}
		else {
			entityId = samlProviderConfiguration.entityId();
		}

		if (GetterUtil.getBoolean(samlProvider.getEnabled()) ||
			!Validator.isBlank(samlProvider.getKeyStoreCredentialPassword())) {

			_authenticateLocalEntityCertificate(
				GetterUtil.getString(
					samlProvider.getKeyStoreCredentialPassword(),
					samlProviderConfiguration.keyStoreCredentialPassword()),
				LocalEntityManager.CertificateUsage.SIGNING, entityId);
		}

		if (samlProvider.getIdp() != null) {
			if (_isIdpRoleDisabled(
					samlProvider.getEnabled(), samlProviderConfiguration)) {

				throw new ConfigurationException(
					"The identity provider role is disabled");
			}

			if (samlProvider.getSp() != null) {
				throw new ConfigurationException(
					"Identity and service provider roles are mutually " +
						"exclusive");
			}

			_setIdpProperties(
				samlProvider.getIdp(), samlProviderConfiguration,
				unicodeProperties);
		}
		else if (samlProvider.getSp() != null) {
			_setSpProperties(
				entityId, samlProviderConfiguration, samlProvider.getSp(),
				unicodeProperties);
		}

		if (GetterUtil.getBoolean(samlProvider.getEnabled()) &&
			!_isValidRole(
				GetterUtil.get(
					unicodeProperties.get(PortletPropsKeys.SAML_ROLE),
					samlProviderConfiguration.role()))) {

			throw new ConfigurationException(
				"Unable to enable a provider without a role");
		}

		_samlProviderConfigurationHelper.updateProperties(unicodeProperties);

		return getSamlProvider();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SamlProviderResourceImpl.class);

	private SamlProviderConfiguration _defaultCompanySamlProviderConfiguration =
		ConfigurableUtil.createConfigurable(
			SamlProviderConfiguration.class, Collections.emptyMap());
	private String _defaultCompanySamlProviderConfigurationPid;

	@Reference
	private LocalEntityManager _localEntityManager;

	private SamlConfiguration _samlConfiguration;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	private ServiceRegistration<?> _serviceRegistration;

	private class DefaultCompanyManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			if (pid.equals(_defaultCompanySamlProviderConfigurationPid)) {
				_defaultCompanySamlProviderConfiguration =
					ConfigurableUtil.createConfigurable(
						SamlProviderConfiguration.class,
						Collections.emptyMap());
				_defaultCompanySamlProviderConfigurationPid = null;
			}
		}

		@Override
		public String getName() {
			return DefaultCompanyManagedServiceFactory.class.getName();
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> properties) {
			if (GetterUtil.getLong(properties.get("companyId")) ==
					CompanyConstants.SYSTEM) {

				_defaultCompanySamlProviderConfiguration =
					ConfigurableUtil.createConfigurable(
						SamlProviderConfiguration.class, properties);
				_defaultCompanySamlProviderConfigurationPid = pid;
			}
		}

	}

}