/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.score;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.util.Time;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTScoreCache.class)
public class CTScoreCache {

	public int getScore(long ctCollectionId) {
		Integer score = _portalCache.get(ctCollectionId);

		if (score != null) {
			return score;
		}

		Object lock = _getLock(ctCollectionId);

		synchronized (lock) {
			score = _portalCache.get(ctCollectionId);

			if (score != null) {
				return score;
			}

			score = _ctScoreCalculator.calculateCTCollectionScore(
				ctCollectionId);

			_portalCache.put(ctCollectionId, score, _TIME_TO_LIVE);
		}

		return score;
	}

	public void remove(long ctCollectionId) {
		_portalCache.remove(ctCollectionId);

		_locks.remove(ctCollectionId);
	}

	public void removeAll() {
		_portalCache.removeAll();

		_locks.clear();
	}

	public int updateScore(long ctCollectionId, int delta) {
		Object lock = _getLock(ctCollectionId);

		synchronized (lock) {
			Integer cachedScore = _portalCache.get(ctCollectionId);

			int score;

			if (cachedScore == null) {

				// The on demand calculation already reflects the CTEntry
				// that triggered this update, so no delta is added on top
				// of it.

				score = _ctScoreCalculator.calculateCTCollectionScore(
					ctCollectionId);
			}
			else {
				score = cachedScore + delta;

				if (score < 0) {
					score = 0;
				}
			}

			_portalCache.put(ctCollectionId, score, _TIME_TO_LIVE);

			return score;
		}
	}

	@Activate
	protected void activate() {
		_portalCache = (PortalCache<Long, Integer>)_singleVMPool.getPortalCache(
			CTScoreCache.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		removeAll();
	}

	private Object _getLock(long ctCollectionId) {
		return _locks.computeIfAbsent(ctCollectionId, key -> new Object());
	}

	private static final int _TIME_TO_LIVE =
		(int)(10 * Time.HOUR / Time.SECOND);

	@Reference
	private CTScoreCalculator _ctScoreCalculator;

	private final Map<Long, Object> _locks = new ConcurrentHashMap<>();
	private PortalCache<Long, Integer> _portalCache;

	@Reference
	private SingleVMPool _singleVMPool;

}