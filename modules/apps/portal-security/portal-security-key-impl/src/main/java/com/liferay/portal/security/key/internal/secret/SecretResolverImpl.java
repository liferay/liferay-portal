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
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;

import java.util.Objects;

import java.util.Objects;

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
	public String resolve(long companyId, String value) {
		if (!KeyReferenceUtil.isKeyReference(value)) {
			return value;
		}

		try {
			KeyReference keyReference = KeyReferenceUtil.parseKeyReference(
				value);

			if (keyReference == null) {
				throw new SecretException("Unable to parse the key reference");
			}

			if (keyReference.getType() != KeyReference.Type.SECRET) {
				throw new SecretException(
					"Crypto key references are not supported by the secret " +
						"resolver");
			}

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

			SecretManager secretManager = _secretManagerSnapshot.get();

			if (secretManager == null) {
				throw new IllegalStateException(
					"Secret manager is unavailable");
			}

			try (Secret secret = secretManager.getSecret(
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
	public String store(
		long companyId, String key, String scope, String value) {

		if (!PropsValues.FIPS_ENABLED || Validator.isNull(value)) {
			return value;
		}

		try {
			if (KeyReferenceUtil.isKeyReference(value)) {
				_validateKeyReference(key, value);

				return value;
			}

			SecretManager secretManager = _secretManagerSnapshot.get();

			if (secretManager == null) {
				throw new IllegalStateException(
					"Secret manager is unavailable");
			}

			try (Secret secret = new Secret(
					new KeyReference(
						StringBundler.concat(
							_IDENTIFIER_PREFIX, scope, StringPool.SLASH, key),
						StringPool.STAR, KeyReference.Type.SECRET),
					value)) {

				return KeyReferenceUtil.toKeyReferenceString(
					secretManager.putSecret(companyId, secret));
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

	private void _validateKeyReference(String key, String value)
		throws SecretException {

		KeyReference keyReference = KeyReferenceUtil.parseKeyReference(value);

		if (keyReference == null) {
			throw new SecretException("Unable to parse the key reference");
		}

		String identifier = keyReference.getIdentifier();

		if (identifier.startsWith(_IDENTIFIER_PREFIX) &&
			Objects.equals(
				key, StringUtil.extractLast(identifier, CharPool.SLASH))) {

			return;
		}

		throw new SecretException(
			StringBundler.concat(
				"Key \"", key, "\" cannot reference a value belonging to \"",
				identifier, "\""));
	}

	private static final String _IDENTIFIER_PREFIX = "preference/";

	private static final Snapshot<SecretManager> _secretManagerSnapshot =
		new Snapshot<>(
			SecretResolverImpl.class, SecretManager.class, null, true);

	@Reference
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;

	private PortalCache<String, String> _portalCache;

}