/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.url.builder.facet.CacheAwareAbsolutePortalURLBuilder.CachePolicy;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * @author Iván Zaera Avellón
 */
public class CacheHelper {

	public CacheHelper(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_bundleListener = bundleEvent -> {
			if (bundleEvent.getType() == BundleEvent.STOPPED) {
				Bundle bundle = bundleEvent.getBundle();

				_digests.remove(bundle.getBundleId());
			}
		};

		bundleContext.addBundleListener(_bundleListener);

		_lastRestartTime = System.currentTimeMillis();
	}

	public void appendCacheParam(
		StringBundler sb, Bundle bundle, CachePolicy cachePolicy,
		String resourcePath) {

		if (cachePolicy == CachePolicy.NEVER) {
			appendNeverCacheParam(sb);
		}
		else if (cachePolicy == CachePolicy.UNTIL_CHANGED) {
			String mac = _digest(bundle, "META-INF/resources" + resourcePath);

			if (mac != null) {
				URLUtil.appendParam(sb, "mac", mac);
			}
		}
	}

	/**
	 * This is a compromise technique that may be used when a resource cannot be
	 * checked for updates.
	 *
	 * It appends a "t" parameter to the URL with the last time the server was
	 * started, which invalidates browser caches after every server restart.
	 *
	 * This is suboptimal in the sense that a server restart does not mean that
	 * resources have been changed, but at least, it is efficient if servers
	 * are not restarted very often, and guarantees that any upgrade of the
	 * Portal invalidates browser caches (because you cannot upgrade without
	 * restarting).
	 */
	public void appendLastRestartCacheParam(StringBundler sb) {
		URLUtil.appendParam(sb, "t", String.valueOf(_lastRestartTime));
	}

	public void appendNeverCacheParam(StringBundler sb) {
		URLUtil.appendParam(
			sb, "t", String.valueOf(System.currentTimeMillis()));
	}

	public void destroy() {
		_bundleContext.removeBundleListener(_bundleListener);
	}

	private String _digest(Bundle bundle, String path) {
		Map<String, String> digests = _digests.computeIfAbsent(
			bundle.getBundleId(), key -> new ConcurrentHashMap<>());

		String cacheKey = StringBundler.concat(
			bundle.getBundleId(), StringPool.COLON, path);

		String digest = digests.get(cacheKey);

		if (digest != null) {
			if (digest == _NULL_HOLDER) {
				return null;
			}

			return digest;
		}

		URL url = bundle.getResource(path);

		if (url == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to find resource ", path, " inside bundle ",
						bundle.getSymbolicName()));
			}

			digests.put(cacheKey, _NULL_HOLDER);

			return null;
		}

		try (InputStream inputStream = url.openStream()) {
			digest = DigesterUtil.digestBase64("SHA-1", inputStream);

			digests.put(cacheKey, digest);

			return digest;
		}
		catch (IOException | SystemException exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to digest resource ", path, " inside bundle ",
						bundle.getSymbolicName()),
					exception);
			}

			digests.put(cacheKey, _NULL_HOLDER);

			return null;
		}
	}

	private static final String _NULL_HOLDER = "NULL_HOLDER";

	private static final Log _log = LogFactoryUtil.getLog(CacheHelper.class);

	private final BundleContext _bundleContext;
	private final BundleListener _bundleListener;
	private final Map<Long, Map<String, String>> _digests =
		new ConcurrentHashMap<>();
	private final long _lastRestartTime;

}