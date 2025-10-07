/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.Serializable;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tina Tian
 */
@Component(
	property = PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.SINGLE_VM,
	service = PortalCacheManager.class
)
public class SingleVMEhcachePortalCacheManager<K extends Serializable, V>
	extends BaseEhcachePortalCacheManager<K, V> {

	@Activate
	protected void activate(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		setConfigFile(
			PropsUtil.get(PropsKeys.EHCACHE_SINGLE_VM_CONFIG_LOCATION));
		setDefaultConfigFile(_DEFAULT_CONFIG_FILE_NAME);
		setPortalCacheManagerName(PortalCacheManagerNames.SINGLE_VM);

		initialize();

		if (_log.isDebugEnabled()) {
			_log.debug("Activated " + PortalCacheManagerNames.SINGLE_VM);
		}
	}

	@Deactivate
	protected void deactivate() {
		destroy();
	}

	private static final String _DEFAULT_CONFIG_FILE_NAME =
		"/ehcache/liferay-single-vm.xml";

	private static final Log _log = LogFactoryUtil.getLog(
		SingleVMEhcachePortalCacheManager.class);

}