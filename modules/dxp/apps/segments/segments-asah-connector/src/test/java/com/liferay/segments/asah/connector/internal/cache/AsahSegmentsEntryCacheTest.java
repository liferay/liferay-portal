/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.cache;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.segments.asah.connector.internal.configuration.SegmentsAsahCompanyConfiguration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author David Arques
 */
public class AsahSegmentsEntryCacheTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_asahSegmentsEntryCacheImpl, "_configurationProvider",
			_configurationProvider);
		ReflectionTestUtil.setFieldValue(
			_asahSegmentsEntryCacheImpl, "_portalCache", _portalCache);
	}

	@Test
	public void testGetSegmentsEntryIds() {
		String userId = RandomTestUtil.randomString();

		long[] segmentsEntryIds = {
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong()
		};

		String cacheKey = _generateCacheKey(userId);

		Mockito.when(
			_portalCache.get(cacheKey)
		).thenReturn(
			segmentsEntryIds
		);

		Assert.assertArrayEquals(
			segmentsEntryIds,
			_asahSegmentsEntryCacheImpl.getSegmentsEntryIds(userId));

		Mockito.verify(
			_portalCache, Mockito.times(1)
		).get(
			Mockito.eq(cacheKey)
		);
	}

	@Test
	public void testPutSegmentsEntryIds() throws Exception {
		int anonymousUserSegmentsCacheExpirationTime =
			RandomTestUtil.randomInt();

		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				Mockito.eq(SegmentsAsahCompanyConfiguration.class),
				Mockito.anyLong())
		).thenReturn(
			new SegmentsAsahCompanyConfiguration() {

				@Override
				public int anonymousUserSegmentsCacheExpirationTime() {
					return anonymousUserSegmentsCacheExpirationTime;
				}

			}
		);

		String userId = RandomTestUtil.randomString();

		long[] segmentsEntryIds = {
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong()
		};

		String cacheKey = _generateCacheKey(userId);

		_asahSegmentsEntryCacheImpl.putSegmentsEntryIds(
			userId, segmentsEntryIds);

		Mockito.verify(
			_portalCache, Mockito.times(1)
		).put(
			cacheKey, segmentsEntryIds, anonymousUserSegmentsCacheExpirationTime
		);
	}

	private String _generateCacheKey(String userId) {
		return "segments-" + userId;
	}

	private final AsahSegmentsEntryCacheImpl _asahSegmentsEntryCacheImpl =
		new AsahSegmentsEntryCacheImpl();
	private final ConfigurationProvider _configurationProvider = Mockito.mock(
		ConfigurationProvider.class);
	private final PortalCache<String, long[]> _portalCache = Mockito.mock(
		PortalCache.class);

}