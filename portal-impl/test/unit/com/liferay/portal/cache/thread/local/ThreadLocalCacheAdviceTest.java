/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.thread.local;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.preview.PreviewableResolverUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ThreadLocalCacheAdviceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCacheKeyWithoutPreviewId() {
		Assert.assertEquals("1", _getCacheKey(1L));
		Assert.assertEquals("1#2", _getCacheKey(1L, 2L));
		Assert.assertEquals("1#2#ff", _getCacheKey(1L, 2L, 255L));
	}

	@Test
	public void testCacheKeyWithPreviewId() {
		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(10L)) {

			Assert.assertEquals("a#1", _getCacheKey(1L));
			Assert.assertEquals("a#1#2", _getCacheKey(1L, 2L));
			Assert.assertEquals("a#1#2#ff", _getCacheKey(1L, 2L, 255L));
		}
	}

	private String _getCacheKey(Object... arguments) {
		return ReflectionTestUtil.invoke(
			_threadLocalCacheAdvice, "_getCacheKey",
			new Class<?>[] {Object[].class}, (Object)arguments);
	}

	private final ThreadLocalCacheAdvice _threadLocalCacheAdvice =
		new ThreadLocalCacheAdvice();

}