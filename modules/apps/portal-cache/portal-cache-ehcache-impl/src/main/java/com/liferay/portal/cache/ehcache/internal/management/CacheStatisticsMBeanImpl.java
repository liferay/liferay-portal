/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.management;

import java.util.Map;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.core.statistics.TierStatistics;

/**
 * @author Dante Wang
 */
public class CacheStatisticsMBeanImpl
	extends StandardMBean implements CacheStatisticsMBean {

	public CacheStatisticsMBeanImpl(
			String cacheName, CacheStatistics cacheStatistics)
		throws NotCompliantMBeanException {

		super(CacheStatisticsMBean.class);

		_cacheName = cacheName;
		_cacheStatistics = cacheStatistics;

		Map<String, TierStatistics> tierStatisticsMap =
			cacheStatistics.getTierStatistics();

		_tierStatistics = tierStatisticsMap.get("OnHeap");
	}

	@Override
	public void clear() {
		_cacheStatistics.clear();
	}

	@Override
	public long getCacheEvictions() {
		return _cacheStatistics.getCacheEvictions();
	}

	@Override
	public long getCacheExpirations() {
		return _cacheStatistics.getCacheExpirations();
	}

	@Override
	public long getCacheGets() {
		return _cacheStatistics.getCacheGets();
	}

	@Override
	public float getCacheHitPercentage() {
		return _cacheStatistics.getCacheHitPercentage();
	}

	@Override
	public long getCacheHits() {
		return _cacheStatistics.getCacheHits();
	}

	@Override
	public float getCacheMissPercentage() {
		return _cacheStatistics.getCacheMissPercentage();
	}

	@Override
	public long getCacheMisses() {
		return _cacheStatistics.getCacheMisses();
	}

	@Override
	public long getCachePuts() {
		return _cacheStatistics.getCachePuts();
	}

	@Override
	public long getCacheRemovals() {
		return _cacheStatistics.getCacheRemovals();
	}

	@Override
	public long getHeapEntries() {
		if (_tierStatistics == null) {
			return -1;
		}

		return _tierStatistics.getMappings();
	}

	@Override
	public String getName() {
		return _cacheName;
	}

	private final String _cacheName;
	private final CacheStatistics _cacheStatistics;
	private final TierStatistics _tierStatistics;

}