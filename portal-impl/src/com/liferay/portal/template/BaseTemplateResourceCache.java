/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceCache;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyFactory;

/**
 * @author Tina Tian
 */
public abstract class BaseTemplateResourceCache
	implements TemplateResourceCache {

	@Override
	public void clear() {
		if (!isEnabled()) {
			return;
		}

		_multiVMPortalCache.removeAll();
	}

	public <T> PortalCache<TemplateResource, T> getSecondLevelPortalCache() {
		return (PortalCache<TemplateResource, T>)_secondLevelPortalCache;
	}

	@Override
	public TemplateResource getTemplateResource(String templateId) {
		if (!isEnabled()) {
			return null;
		}

		TemplateResource templateResource = _multiVMPortalCache.get(templateId);

		if ((templateResource != null) &&
			(templateResource != DUMMY_TEMPLATE_RESOURCE) &&
			(_modificationCheckInterval > 0)) {

			long expireTime =
				templateResource.getLastModified() + _modificationCheckInterval;

			if (System.currentTimeMillis() > expireTime) {
				remove(templateId);

				templateResource = null;

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Remove expired template resource " + templateId);
				}
			}
		}

		return templateResource;
	}

	@Override
	public boolean isEnabled() {
		if (!PropsValues.TEMPLATE_ENGINE_CACHE_ENABLED ||
			(_modificationCheckInterval == 0)) {

			return false;
		}

		return true;
	}

	@Override
	public void put(String templateId, TemplateResource templateResource) {
		if (!isEnabled()) {
			return;
		}

		if (templateResource == null) {
			templateResource = DUMMY_TEMPLATE_RESOURCE;
		}
		else if (!(templateResource instanceof CacheTemplateResource) &&
				 !(templateResource instanceof StringTemplateResource)) {

			templateResource = new CacheTemplateResource(templateResource);
		}

		PortalCacheHelperUtil.putWithoutReplicator(
			_multiVMPortalCache, templateId, templateResource);
	}

	@Override
	public void remove(String templateId) {
		if (!isEnabled()) {
			return;
		}

		_multiVMPortalCache.remove(templateId);
	}

	protected void destroy() {
		PortalCacheHelperUtil.removePortalCache(
			PortalCacheManagerNames.MULTI_VM,
			_multiVMPortalCache.getPortalCacheName());

		PortalCacheHelperUtil.removePortalCache(
			PortalCacheManagerNames.SINGLE_VM,
			_secondLevelPortalCache.getPortalCacheName());
	}

	protected void init(
		long modificationCheckInterval, String portalCacheName,
		String secondLevelPortalCacheName) {

		_modificationCheckInterval = modificationCheckInterval;

		_multiVMPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, portalCacheName);

		_secondLevelPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM, secondLevelPortalCacheName);

		_setSecondLevelPortalCache(_secondLevelPortalCache);
	}

	protected void setModificationCheckInterval(
		long modificationCheckInterval) {

		_modificationCheckInterval = modificationCheckInterval;
	}

	protected static final TemplateResource DUMMY_TEMPLATE_RESOURCE =
		ProxyFactory.newDummyInstance(TemplateResource.class);

	private void _setSecondLevelPortalCache(
		PortalCache<TemplateResource, ?> portalCache) {

		TemplateResourcePortalCacheListener
			templateResourcePortalCacheListener =
				new TemplateResourcePortalCacheListener(portalCache);

		_multiVMPortalCache.registerPortalCacheListener(
			templateResourcePortalCacheListener);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseTemplateResourceCache.class);

	private volatile long _modificationCheckInterval;
	private PortalCache<String, TemplateResource> _multiVMPortalCache;
	private PortalCache<TemplateResource, ?> _secondLevelPortalCache;

	private class TemplateResourcePortalCacheListener
		implements PortalCacheListener<String, TemplateResource> {

		@Override
		public void dispose() {
		}

		@Override
		public void notifyEntryEvicted(
				PortalCache<String, TemplateResource> portalCache, String key,
				TemplateResource templateResource, int timeToLive)
			throws PortalCacheException {

			if (templateResource != null) {
				_portalCache.remove(templateResource);
			}
		}

		@Override
		public void notifyEntryExpired(
				PortalCache<String, TemplateResource> portalCache, String key,
				TemplateResource templateResource, int timeToLive)
			throws PortalCacheException {

			if (templateResource != null) {
				_portalCache.remove(templateResource);
			}
		}

		@Override
		public void notifyEntryPut(
				PortalCache<String, TemplateResource> portalCache, String key,
				TemplateResource templateResource, int timeToLive)
			throws PortalCacheException {
		}

		@Override
		public void notifyEntryRemoved(
				PortalCache<String, TemplateResource> portalCache, String key,
				TemplateResource templateResource, int timeToLive)
			throws PortalCacheException {

			if (templateResource != null) {
				_portalCache.remove(templateResource);
			}
		}

		@Override
		public void notifyEntryUpdated(
				PortalCache<String, TemplateResource> portalCache, String key,
				TemplateResource templateResource, int timeToLive)
			throws PortalCacheException {

			if (templateResource != null) {
				_portalCache.remove(templateResource);
			}
		}

		@Override
		public void notifyRemoveAll(
				PortalCache<String, TemplateResource> portalCache)
			throws PortalCacheException {

			_portalCache.removeAll();
		}

		private TemplateResourcePortalCacheListener(
			PortalCache<TemplateResource, ?> portalCache) {

			_portalCache = portalCache;
		}

		private final PortalCache<TemplateResource, ?> _portalCache;

	}

}