/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.configuration.persistence.listener;

import com.liferay.configuration.admin.util.ConfigurationPidUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.fips.FIPSAuditEvent;
import com.liferay.portal.kernel.security.fips.FIPSAuditUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "model.class.name=*", service = ConfigurationModelListener.class
)
public class ConfigurationSecretConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeDelete(String pid)
		throws ConfigurationModelListenerException {

		if (_keyManagerProfileRegistry.getActiveKeyManagerProfile() == null) {
			return;
		}

		try {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					"(service.pid=" + pid + ")");

			if (configurations == null) {
				return;
			}

			for (Configuration configuration : configurations) {
				Dictionary<String, Object> properties =
					configuration.getProperties();

				if (properties == null) {
					continue;
				}

				long companyId = GetterUtil.getLong(
					properties.get(
						ExtendedObjectClassDefinition.Scope.COMPANY.
							getPropertyKey()),
					CompanyConstants.SYSTEM);

				Enumeration<String> enumeration = properties.keys();

				while (enumeration.hasMoreElements()) {
					String key = enumeration.nextElement();

					if (!(properties.get(key) instanceof String value)) {
						continue;
					}

					KeyReference keyReference =
						KeyReferenceUtil.parseKeyReference(value);

					if (keyReference == null) {
						continue;
					}

					String identifier = keyReference.getIdentifier();

					if (!identifier.startsWith(_IDENTIFIER_PREFIX)) {
						continue;
					}

					try {
						_secretManager.deleteSecret(companyId, keyReference);
					}
					catch (Exception exception) {
						_auditSecretDeletionFailure(pid, exception);
					}
				}
			}
		}
		catch (Exception exception) {
			_auditSecretDeletionFailure(pid, exception);
		}
	}

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (_keyManagerProfileRegistry.getActiveKeyManagerProfile() == null) {
			return;
		}

		ObjectClassDefinition objectClassDefinition = _getObjectClassDefinition(
			pid, properties);

		if (objectClassDefinition == null) {
			return;
		}

		long companyId = GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()),
			CompanyConstants.SYSTEM);

		for (AttributeDefinition attributeDefinition :
				objectClassDefinition.getAttributeDefinitions(
					ObjectClassDefinition.ALL)) {

			if (attributeDefinition.getType() != AttributeDefinition.PASSWORD) {
				continue;
			}

			String id = attributeDefinition.getID();

			if (!(properties.get(id) instanceof String value) ||
				Validator.isNull(value)) {

				continue;
			}

			String identifier = StringBundler.concat(
				_IDENTIFIER_PREFIX,
				StringUtil.replace(pid, CharPool.TILDE, CharPool.SLASH),
				StringPool.SLASH, companyId, StringPool.SLASH, id);

			KeyReference keyReference = KeyReferenceUtil.parseKeyReference(
				value);

			if (keyReference != null) {
				_validateKeyReference(identifier, keyReference, pid);

				continue;
			}

			try (Secret secret = new Secret(
					new KeyReference(
						identifier, StringPool.STAR, KeyReference.Type.SECRET),
					value)) {

				properties.put(
					id,
					KeyReferenceUtil.toKeyReferenceString(
						_secretManager.putSecret(companyId, secret)));
			}
			catch (Exception exception) {
				FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
					"configuration-vaulting-failure",
					FIPSAuditEvent.Severity.WARNING);

				fipsAuditEvent.put(
					"configuration-pid", GetterUtil.getString(pid));
				fipsAuditEvent.put("property-id", GetterUtil.getString(id));

				FIPSAuditUtil.write(fipsAuditEvent);

				throw new ConfigurationModelListenerException(
					exception, Object.class,
					ConfigurationSecretConfigurationModelListener.class,
					properties);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private void _auditSecretDeletionFailure(String pid, Exception exception) {
		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			"configuration-secret-deletion-failure",
			FIPSAuditEvent.Severity.WARNING);

		fipsAuditEvent.put("configuration-pid", GetterUtil.getString(pid));

		FIPSAuditUtil.write(fipsAuditEvent);

		_log.error(
			"Unable to delete the vaulted secrets of configuration " + pid,
			exception);
	}

	private ObjectClassDefinition _getObjectClassDefinition(
		String pid, Dictionary<String, Object> properties) {

		String factoryPid = GetterUtil.getString(
			properties.get(ConfigurationAdmin.SERVICE_FACTORYPID));

		if (Validator.isNotNull(factoryPid)) {
			pid = factoryPid;
		}

		String metaTypePid = ConfigurationPidUtil.getRawPid(pid);

		for (Bundle bundle : _bundleContext.getBundles()) {
			ExtendedMetaTypeInformation extendedMetaTypeInformation =
				_extendedMetaTypeService.getMetaTypeInformation(bundle);

			if ((extendedMetaTypeInformation == null) ||
				(!ArrayUtil.contains(
					extendedMetaTypeInformation.getFactoryPids(),
					metaTypePid) &&
				 !ArrayUtil.contains(
					 extendedMetaTypeInformation.getPids(), metaTypePid))) {

				continue;
			}

			if (!_isSecretResolverClassLoaded(bundle)) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Not vaulting configuration \"", pid,
							"\" because bundle \"", bundle.getSymbolicName(),
							"\" cannot resolve a key reference"));
				}

				return null;
			}

			return extendedMetaTypeInformation.getObjectClassDefinition(
				metaTypePid, null);
		}

		return null;
	}

	private boolean _isSecretResolverClassLoaded(Bundle bundle) {
		try {
			bundle.loadClass(SecretResolver.class.getName());

			return true;
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(classNotFoundException);
			}
		}

		return false;
	}

	private void _validateKeyReference(
			String identifier, KeyReference keyReference, String pid)
		throws ConfigurationModelListenerException {

		if (Objects.equals(keyReference.getIdentifier(), identifier)) {
			return;
		}

		throw new ConfigurationModelListenerException(
			StringBundler.concat(
				"Configuration ", pid,
				" cannot reference a value it does not own"),
			Object.class, ConfigurationSecretConfigurationModelListener.class,
			null);
	}

	private static final String _IDENTIFIER_PREFIX = "config/";

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationSecretConfigurationModelListener.class);

	private BundleContext _bundleContext;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ExtendedMetaTypeService _extendedMetaTypeService;

	@Reference
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;

	@Reference
	private SecretManager _secretManager;

}