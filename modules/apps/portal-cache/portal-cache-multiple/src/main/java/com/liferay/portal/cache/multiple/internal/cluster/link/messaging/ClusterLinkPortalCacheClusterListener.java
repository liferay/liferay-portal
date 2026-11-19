/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.multiple.internal.cluster.link.messaging;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.cache.multiple.internal.PortalCacheClusterEventType;
import com.liferay.portal.cache.multiple.internal.cluster.link.ClusterLinkMessageUtil;
import com.liferay.portal.cache.multiple.internal.constants.PortalCacheDestinationNames;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.transactional.TransactionalPortalCacheUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Shuyang Zhou
 */
@Component(
	enabled = false,
	property = "destination.name=" + PortalCacheDestinationNames.CACHE_REPLICATION,
	service = MessageListener.class
)
public class ClusterLinkPortalCacheClusterListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<PortalCacheManager<? extends Serializable, ?>>)
				(Class<?>)PortalCacheManager.class,
			null,
			(serviceReference, emitter) -> {
				PortalCacheManager<? extends Serializable, ?>
					portalCacheManager = bundleContext.getService(
						serviceReference);

				emitter.emit(portalCacheManager.getPortalCacheManagerName());
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		PortalCacheManager<? extends Serializable, ?> portalCacheManager =
			_serviceTrackerMap.getService(
				ClusterLinkMessageUtil.getPortalCacheManagerName(message));

		if (portalCacheManager == null) {
			return;
		}

		PortalCache<Serializable, Serializable> portalCache =
			(PortalCache<Serializable, Serializable>)
				portalCacheManager.fetchPortalCache(
					ClusterLinkMessageUtil.getPortalCacheName(message));

		if (portalCache == null) {
			return;
		}

		if (portalCache.isSharded()) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						ClusterLinkMessageUtil.getCompanyId(message))) {

				_handlePortalCacheClusterEvent(message, portalCache);
			}

			return;
		}

		_handlePortalCacheClusterEvent(message, portalCache);
	}

	private void _handlePortalCacheClusterEvent(
		Message message, PortalCache<Serializable, Serializable> portalCache) {

		PortalCacheClusterEventType portalCacheClusterEventType =
			ClusterLinkMessageUtil.getPortalCacheClusterEventType(message);

		if (portalCacheClusterEventType.equals(
				PortalCacheClusterEventType.REMOVE_ALL)) {

			_invalidate(portalCache, true, Collections.emptyList());

			PortalCacheHelperUtil.removeAllWithoutReplicator(portalCache);
		}
		else if (portalCacheClusterEventType.equals(
					PortalCacheClusterEventType.PUT) ||
				 portalCacheClusterEventType.equals(
					 PortalCacheClusterEventType.UPDATE)) {

			Serializable key = ClusterLinkMessageUtil.getKey(message);

			_invalidate(portalCache, false, Collections.singleton(key));

			Serializable value = ClusterLinkMessageUtil.getValue(message);

			if (value == null) {
				PortalCacheHelperUtil.removeWithoutReplicator(portalCache, key);
			}
			else {
				PortalCacheHelperUtil.putWithoutReplicator(
					portalCache, key, value,
					ClusterLinkMessageUtil.getTimeToLive(message));
			}
		}
		else {
			Serializable key = ClusterLinkMessageUtil.getKey(message);

			_invalidate(portalCache, false, Collections.singleton(key));

			PortalCacheHelperUtil.removeWithoutReplicator(portalCache, key);
		}
	}

	private void _invalidate(
		PortalCache<Serializable, Serializable> portalCache, boolean removeAll,
		Collection<Serializable> keys) {

		if (!portalCache.isMVCC()) {
			TransactionalPortalCacheUtil.invalidate(
				portalCache, removeAll, keys);
		}
	}

	private ServiceTrackerMap
		<String, PortalCacheManager<? extends Serializable, ?>>
			_serviceTrackerMap;

}