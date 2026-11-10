/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.SingleVMPool;

import java.io.Serializable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Michael Young
 */
@Component(service = SingleVMPool.class)
public class SingleVMPoolImpl implements SingleVMPool {

	@Override
	public void clear() {
		_portalCacheManager.clearAll();
	}

	@Override
	public PortalCache<? extends Serializable, ?> getPortalCache(
		String portalCacheName) {

		return _portalCacheManager.getPortalCache(portalCacheName);
	}

	@Override
	public PortalCacheManager<? extends Serializable, ?>
		getPortalCacheManager() {

		return _portalCacheManager;
	}

	@Override
	public void removePortalCache(String portalCacheName) {
		_portalCacheManager.removePortalCache(portalCacheName);
	}

	@Activate
	protected void activate() {
		_portalCacheManager.clearAll();
	}

	@Reference(
		target = "(portal.cache.manager.name=" + PortalCacheManagerNames.SINGLE_VM + ")"
	)
	private PortalCacheManager<? extends Serializable, ?> _portalCacheManager;

}