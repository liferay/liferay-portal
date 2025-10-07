/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.cache.thread.local.Lifecycle;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCache;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author István András Dézsi
 */
public class StartupHelperUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSetUpgrading() throws Exception {
		try {
			ThreadLocalCache<Object> threadLocalCache =
				ThreadLocalCacheManager.getThreadLocalCache(
					Lifecycle.REQUEST, Object.class.getName());

			Class<?> clazz = threadLocalCache.getClass();

			Assert.assertEquals("ThreadLocalCache", clazz.getSimpleName());

			StartupHelperUtil.setUpgrading(true);

			threadLocalCache = ThreadLocalCacheManager.getThreadLocalCache(
				Lifecycle.REQUEST, Object.class.getName());

			clazz = threadLocalCache.getClass();

			Assert.assertEquals("EmptyThreadLocalCache", clazz.getSimpleName());

			StartupHelperUtil.setUpgrading(false);

			threadLocalCache = ThreadLocalCacheManager.getThreadLocalCache(
				Lifecycle.REQUEST, Object.class.getName());

			clazz = threadLocalCache.getClass();

			Assert.assertEquals("ThreadLocalCache", clazz.getSimpleName());
		}
		finally {
			StartupHelperUtil.setUpgrading(false);
		}
	}

}