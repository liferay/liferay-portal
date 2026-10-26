/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.cache;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.segments.asah.connector.cache.AsahSegmentsEntryCache;
import com.liferay.segments.asah.connector.internal.configuration.SegmentsAsahCompanyConfiguration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(service = AsahSegmentsEntryCache.class)
public class AsahSegmentsEntryCacheImpl implements AsahSegmentsEntryCache {

	public long[] getSegmentsEntryIds(String userId) {
		return _portalCache.get(_generateCacheKey(userId));
	}

	public void putSegmentsEntryIds(String userId, long[] segmentsEntryIds) {
		int anonymousTimeToLiveInSeconds = PortalCache.DEFAULT_TIME_TO_LIVE;

		try {
			SegmentsAsahCompanyConfiguration segmentsAsahCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					SegmentsAsahCompanyConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			anonymousTimeToLiveInSeconds =
				segmentsAsahCompanyConfiguration.
					anonymousUserSegmentsCacheExpirationTime();
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		_portalCache.put(
			_generateCacheKey(userId), segmentsEntryIds,
			anonymousTimeToLiveInSeconds);
	}

	@Activate
	protected void activate() {
		_portalCache = (PortalCache<String, long[]>)_multiVMPool.getPortalCache(
			AsahSegmentsEntryCache.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(AsahSegmentsEntryCache.class.getName());
	}

	private String _generateCacheKey(String userId) {
		return _CACHE_PREFIX + userId;
	}

	private static final String _CACHE_PREFIX = "segments-";

	private static final Log _log = LogFactoryUtil.getLog(
		AsahSegmentsEntryCacheImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<String, long[]> _portalCache;

}