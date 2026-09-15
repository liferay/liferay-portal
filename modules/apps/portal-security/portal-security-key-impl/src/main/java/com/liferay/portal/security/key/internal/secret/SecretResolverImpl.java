/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.secret;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.exception.SecretException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

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

			_portalCache.put(key, resolvedValue);

			return resolvedValue;
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

	private static final Snapshot<SecretManager> _secretManagerSnapshot =
		new Snapshot<>(
			SecretResolverImpl.class, SecretManager.class, null, true);

	private PortalCache<String, String> _portalCache;

}