/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.management;

/**
 * @author Dante Wang
 */
public interface CacheStatisticsMBean {

	public void clear();

	public long getCacheEvictions();

	public long getCacheExpirations();

	public long getCacheGets();

	public float getCacheHitPercentage();

	public long getCacheHits();

	public float getCacheMissPercentage();

	public long getCacheMisses();

	public long getCachePuts();

	public long getCacheRemovals();

	public long getHeapEntries();

	public String getName();

}