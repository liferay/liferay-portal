/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheManagerListener;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Tina Tian
 */
public class AggregatedPortalCacheManagerListener
	implements PortalCacheManagerListener {

	public boolean addPortalCacheListener(
		PortalCacheManagerListener portalCacheManagerListener) {

		if (portalCacheManagerListener == null) {
			return false;
		}

		return _portalCacheManagerListeners.add(portalCacheManagerListener);
	}

	public void clearAll() {
		_portalCacheManagerListeners.clear();
	}

	@Override
	public void dispose() throws PortalCacheException {
		for (PortalCacheManagerListener portalCacheManagerListener :
				_portalCacheManagerListeners) {

			portalCacheManagerListener.dispose();
		}
	}

	public Set<PortalCacheManagerListener> getPortalCacheManagerListeners() {
		return Collections.unmodifiableSet(_portalCacheManagerListeners);
	}

	@Override
	public void init() throws PortalCacheException {
		for (PortalCacheManagerListener portalCacheManagerListener :
				_portalCacheManagerListeners) {

			portalCacheManagerListener.init();
		}
	}

	@Override
	public void notifyPortalCacheAdded(String portalCacheName) {
		for (PortalCacheManagerListener portalCacheManagerListener :
				_portalCacheManagerListeners) {

			portalCacheManagerListener.notifyPortalCacheAdded(portalCacheName);
		}
	}

	@Override
	public void notifyPortalCacheRemoved(String portalCacheName) {
		for (PortalCacheManagerListener portalCacheManagerListener :
				_portalCacheManagerListeners) {

			portalCacheManagerListener.notifyPortalCacheRemoved(
				portalCacheName);
		}
	}

	public boolean removePortalCacheListener(
		PortalCacheManagerListener portalCacheManagerListener) {

		if (portalCacheManagerListener == null) {
			return false;
		}

		return _portalCacheManagerListeners.remove(portalCacheManagerListener);
	}

	private final Set<PortalCacheManagerListener> _portalCacheManagerListeners =
		new CopyOnWriteArraySet<>();

}