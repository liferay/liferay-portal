/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.score;

import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTScoreSizeClassificationCache.class)
public class CTScoreSizeClassificationCache {

	public String getScoreSizeClassification(long ctCollectionId) {
		String scoreSizeClassification = _portalCache.get(ctCollectionId);

		if (scoreSizeClassification != null) {
			return scoreSizeClassification;
		}

		scoreSizeClassification = _ctScoreCalculator.getScoreSizeClassification(
			_ctScoreCache.getScore(ctCollectionId));

		PortalCacheHelperUtil.putWithoutReplicator(
			_portalCache, ctCollectionId, scoreSizeClassification);

		return scoreSizeClassification;
	}

	public void put(long ctCollectionId, String scoreSizeClassification) {
		_portalCache.put(ctCollectionId, scoreSizeClassification);

		_ctScoreCache.remove(ctCollectionId);
	}

	public void remove(long ctCollectionId) {
		_portalCache.remove(ctCollectionId);

		_ctScoreCache.remove(ctCollectionId);
	}

	@Activate
	protected void activate() {
		_portalCache = (PortalCache<Long, String>)_multiVMPool.getPortalCache(
			CTScoreSizeClassificationCache.class.getName());

		_portalCache.registerPortalCacheListener(
			_portalCacheListener, PortalCacheListenerScope.REMOTE);
	}

	@Deactivate
	protected void deactivate() {
		_portalCache.unregisterPortalCacheListener(_portalCacheListener);
	}

	@Reference
	private CTScoreCache _ctScoreCache;

	@Reference
	private CTScoreCalculator _ctScoreCalculator;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<Long, String> _portalCache;
	private final PortalCacheListener<Long, String> _portalCacheListener =
		new CTScoreSizeClassificationPortalCacheListener();

	private class CTScoreSizeClassificationPortalCacheListener
		implements PortalCacheListener<Long, String> {

		@Override
		public void dispose() {
		}

		@Override
		public void notifyEntryEvicted(
				PortalCache<Long, String> portalCache, Long ctCollectionId,
				String scoreSizeClassification, int timeToLive)
			throws PortalCacheException {
		}

		@Override
		public void notifyEntryExpired(
				PortalCache<Long, String> portalCache, Long ctCollectionId,
				String scoreSizeClassification, int timeToLive)
			throws PortalCacheException {
		}

		@Override
		public void notifyEntryPut(
				PortalCache<Long, String> portalCache, Long ctCollectionId,
				String scoreSizeClassification, int timeToLive)
			throws PortalCacheException {
		}

		@Override
		public void notifyEntryRemoved(
				PortalCache<Long, String> portalCache, Long ctCollectionId,
				String scoreSizeClassification, int timeToLive)
			throws PortalCacheException {

			_ctScoreCache.remove(ctCollectionId);
		}

		@Override
		public void notifyEntryUpdated(
				PortalCache<Long, String> portalCache, Long ctCollectionId,
				String scoreSizeClassification, int timeToLive)
			throws PortalCacheException {
		}

		@Override
		public void notifyRemoveAll(PortalCache<Long, String> portalCache)
			throws PortalCacheException {

			_ctScoreCache.removeAll();
		}

	}

}