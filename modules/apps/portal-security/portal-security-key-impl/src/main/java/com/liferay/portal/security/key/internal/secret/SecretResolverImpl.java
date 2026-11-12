/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.secret;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.fips.FIPSAuditEvent;
import com.liferay.portal.kernel.security.fips.FIPSAuditUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretContext;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;

import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(service = SecretResolver.class)
public class SecretResolverImpl implements SecretResolver {

	public static final String PORTAL_CACHE_NAME =
		SecretResolverImpl.class.getName();

	public static String getKey(long companyId, String keyReferenceString) {
		return StringBundler.concat(
			companyId, StringPool.POUND, keyReferenceString);
	}

	@Override
	public String resolve(
		long companyId, SecretContext secretContext, String value) {

		if (!KeyReferenceUtil.isKeyReference(value)) {
			return value;
		}

		try {
			KeyReference keyReference = _getKeyReference(value);

			long scopeCompanyId = _getScopeCompanyId(
				companyId, keyReference.getIdentifier(), "resolve",
				_getSecretIdentifierMap(secretContext));

			String key = getKey(scopeCompanyId, value);

			String resolvedValue = _portalCache.get(key);

			if (resolvedValue != null) {
				return resolvedValue;
			}

			try (Secret secret = _secretManager.getSecret(
					scopeCompanyId, keyReference)) {

				resolvedValue = new String(secret.getChars());
			}

			_portalCache.put(key, resolvedValue, 600);

			return resolvedValue;
		}
		catch (SecretException secretException) {
			return ReflectionUtil.throwException(secretException);
		}
	}

	@Override
	public String resolve(long companyId, String value) {
		if (!KeyReferenceUtil.isKeyReference(value)) {
			return value;
		}

		try {
			KeyReference keyReference = _getKeyReference(value);

			if (companyId != CompanyConstants.SYSTEM) {
				KeyManagerProfile keyManagerProfile =
					_keyManagerProfileRegistry.getActiveKeyManagerProfile();

				if ((keyManagerProfile != null) &&
					Objects.equals(
						keyReference.getProviderId(),
						keyManagerProfile.getSystemSecretProviderId())) {

					companyId = CompanyConstants.SYSTEM;
				}
			}

			String key = getKey(companyId, value);

			String resolvedValue = _portalCache.get(key);

			if (resolvedValue != null) {
				return resolvedValue;
			}

			try (Secret secret = _secretManager.getSecret(
					companyId, keyReference)) {

				resolvedValue = new String(secret.getChars());
			}

			_portalCache.put(key, resolvedValue, 600);

			return resolvedValue;
		}
		catch (SecretException secretException) {
			return ReflectionUtil.throwException(secretException);
		}
	}

	@Override
	public String resolve(long companyId, String identifier, String value) {
		KeyReference keyReference = KeyReferenceUtil.parseKeyReference(value);

		if ((keyReference == null) ||
			!Objects.equals(identifier, keyReference.getIdentifier())) {

			return value;
		}

		return resolve(companyId, value);
	}

	@Override
	public String store(
		long companyId, SecretContext secretContext, String value) {

		if (!PropsValues.FIPS_ENABLED || Validator.isNull(value)) {
			return value;
		}

		try {
			SequencedMap<String, Long> secretIdentifierMap =
				_getSecretIdentifierMap(secretContext);

			if (KeyReferenceUtil.isKeyReference(value)) {
				KeyReference keyReference = _getKeyReference(value);

				_getScopeCompanyId(
					companyId, keyReference.getIdentifier(), "store",
					secretIdentifierMap);

				return value;
			}

			Map.Entry<String, Long> entry = secretIdentifierMap.firstEntry();

			String identifier = entry.getKey();

			long scopeCompanyId = _getScopeCompanyId(
				companyId, identifier, "store", secretIdentifierMap);

			try (Secret secret = new Secret(
					new KeyReference(
						identifier, StringPool.STAR, KeyReference.Type.SECRET),
					value)) {

				return _putSecret(scopeCompanyId, secret);
			}
		}
		catch (SecretException secretException) {
			return ReflectionUtil.throwException(secretException);
		}
	}

	@Override
	public String store(long companyId, String identifier, String value) {
		if (!PropsValues.FIPS_ENABLED || Validator.isNull(value)) {
			return value;
		}

		if (!identifier.startsWith("config/") &&
			!identifier.startsWith(_IDENTIFIER_PREFIX_PREFERENCE)) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"Unable to store \"", identifier,
					"\" because its namespace is not supported"));
		}

		try {
			if (KeyReferenceUtil.isKeyReference(value)) {
				KeyReference keyReference = KeyReferenceUtil.parseKeyReference(
					value);

				if (keyReference == null) {
					throw new SecretException(
						"Unable to parse the key reference");
				}

				String keyReferenceIdentifier = keyReference.getIdentifier();

				if (Objects.equals(identifier, keyReferenceIdentifier) ||
					(identifier.startsWith(_IDENTIFIER_PREFIX_PREFERENCE) &&
					 keyReferenceIdentifier.startsWith(
						 _IDENTIFIER_PREFIX_PREFERENCE) &&
					 Objects.equals(
						 StringUtil.extractLast(identifier, CharPool.SLASH),
						 StringUtil.extractLast(
							 keyReferenceIdentifier, CharPool.SLASH)))) {

					return value;
				}

				throw new SecretException(
					StringBundler.concat(
						"Unable to store \"", identifier,
						"\" because it references \"", keyReferenceIdentifier,
						"\""));
			}

			try (Secret secret = new Secret(
					new KeyReference(
						identifier, StringPool.STAR, KeyReference.Type.SECRET),
					value)) {

				return _putSecret(companyId, secret);
			}
		}
		catch (SecretException secretException) {
			return ReflectionUtil.throwException(secretException);
		}
	}

	@Activate
	protected void activate() {
		_portalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM, PORTAL_CACHE_NAME);
	}

	@Deactivate
	protected void deactivate() {
		PortalCacheHelperUtil.removePortalCache(
			PortalCacheManagerNames.SINGLE_VM, PORTAL_CACHE_NAME);
	}

	private KeyReference _getKeyReference(String keyReferenceString)
		throws SecretException {

		KeyReference keyReference = KeyReferenceUtil.parseKeyReference(
			keyReferenceString);

		if (keyReference == null) {
			throw new SecretException("Unable to parse the key reference");
		}

		if (keyReference.getType() != KeyReference.Type.SECRET) {
			throw new SecretException("Only secret references are supported");
		}

		return keyReference;
	}

	private long _getScopeCompanyId(
			long companyId, String identifier, String operation,
			Map<String, Long> secretIdentifierMap)
		throws SecretException {

		Long scopeCompanyId = secretIdentifierMap.get(identifier);

		if ((scopeCompanyId != null) &&
			((scopeCompanyId == CompanyConstants.SYSTEM) ||
			 (scopeCompanyId == companyId))) {

			return scopeCompanyId;
		}

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			"secret-reference-refused", FIPSAuditEvent.Severity.WARNING);

		fipsAuditEvent.put("company-id", companyId);
		fipsAuditEvent.put("identifier", identifier);
		fipsAuditEvent.put("operation", operation);

		FIPSAuditUtil.write(fipsAuditEvent);

		throw new SecretException(
			StringBundler.concat(
				"Unable to ", operation, " the key reference \"", identifier,
				"\" in this secret context"));
	}

	private SequencedMap<String, Long> _getSecretIdentifierMap(
		SecretContext secretContext) {

		SequencedMap<String, Long> secretIdentifierMap = new LinkedHashMap<>();

		SecretContext.Type type = secretContext.getType();

		if (type == SecretContext.Type.CONFIGURATION) {
			Dictionary<String, Object> properties = new HashMapDictionary<>(
				secretContext.getProperties());

			String pid = GetterUtil.getString(
				properties.get(Constants.SERVICE_PID));

			secretIdentifierMap.put(
				SecretIdentifierUtil.getSecretIdentifier(
					secretContext.getKey(), pid, properties),
				SecretIdentifierUtil.getConfigurationCompanyId(properties));
		}

		return secretIdentifierMap;
	}

	private String _putSecret(long companyId, Secret secret)
		throws SecretException {

		KeyReference keyReference = _secretManager.putSecret(
			companyId, secret);

		return KeyReferenceUtil.toKeyReferenceString(keyReference);
	}

	private static final String _IDENTIFIER_PREFIX_PREFERENCE = "preference/";

	@Reference
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;

	private PortalCache<String, String> _portalCache;

	@Reference
	private SecretManager _secretManager;

}