/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.frontend.hashed.files;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Iván Zaera Avellón
 */
public class CachingStrategyTest {

	@Test
	public void testFromValue() {
		for (CachingStrategy cachingStrategy : CachingStrategy.values()) {
			Assert.assertEquals(
				cachingStrategy,
				CachingStrategy.fromValue(cachingStrategy.getValue()));
			Assert.assertEquals(
				cachingStrategy,
				CachingStrategy.fromValue(
					cachingStrategy.getValue(),
					CachingStrategy.USE_ONE_HASH_PER_FILE));
		}
	}

	@Test
	public void testFromValueWhenValueIsInvalid() {
		for (String value :
				new String[] {
					null, StringPool.BLANK, RandomTestUtil.randomString()
				}) {

			Assert.assertEquals(
				CachingStrategy.DO_NOT_USE_HASHES,
				CachingStrategy.fromValue(
					value, CachingStrategy.DO_NOT_USE_HASHES));
		}
	}

}