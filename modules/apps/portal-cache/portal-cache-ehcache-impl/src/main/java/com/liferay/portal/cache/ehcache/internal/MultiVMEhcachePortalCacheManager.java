/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.Serializable;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tina Tian
 */
@Component(
	property = PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM,
	service = PortalCacheManager.class
)
public class MultiVMEhcachePortalCacheManager
	<K extends Serializable, V extends Serializable>
		extends BaseEhcachePortalCacheManager<K, V> {

	@Activate
	protected void activate(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		clusterEnabled = GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.CLUSTER_LINK_ENABLED));
		_defaultReplicatorPropertiesString = _getPortalPropertiesString(
			PropsKeys.EHCACHE_REPLICATOR_PROPERTIES_DEFAULT);
		_replicatorProperties = PropsUtil.getProperties(
			PropsKeys.EHCACHE_REPLICATOR_PROPERTIES + StringPool.PERIOD, true);

		setConfigFile(
			PropsUtil.get(PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION));
		setDefaultConfigFile(_DEFAULT_CONFIG_FILE_NAME);
		setPortalCacheManagerName(PortalCacheManagerNames.MULTI_VM);

		initialize();

		if (_log.isDebugEnabled()) {
			_log.debug("Activated " + PortalCacheManagerNames.MULTI_VM);
		}
	}

	@Deactivate
	protected void deactivate() {
		destroy();
	}

	@Override
	protected String getDefaultReplicatorPropertiesString() {
		if (clusterEnabled) {
			return _defaultReplicatorPropertiesString;
		}

		return null;
	}

	@Override
	protected Properties getReplicatorProperties() {
		if (clusterEnabled) {
			return _replicatorProperties;
		}

		return null;
	}

	protected boolean clusterEnabled;

	private String _getPortalPropertiesString(String portalPropertyKey) {
		String[] array = PropsUtil.getArray(portalPropertyKey);

		if (array.length == 0) {
			return null;
		}

		if (array.length == 1) {
			return array[0];
		}

		StringBundler sb = new StringBundler(array.length * 2);

		for (String value : array) {
			sb.append(value);
			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static final String _DEFAULT_CONFIG_FILE_NAME =
		"/ehcache/liferay-multi-vm.xml";

	private static final Log _log = LogFactoryUtil.getLog(
		MultiVMEhcachePortalCacheManager.class);

	private String _defaultReplicatorPropertiesString;
	private Properties _replicatorProperties;

}