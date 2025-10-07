/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.frontend.hashed.files;

import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFilesUtilTest {

	@Test
	public void testAddHash() {
		String hash = RandomTestUtil.randomString(8);

		Assert.assertEquals(
			"/css/main.(" + hash + ").css",
			HashedFilesUtil.addHash("/css/main.css", hash));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddHashWithIncorrectURI() {
		String hash = RandomTestUtil.randomString(8);

		HashedFilesUtil.addHash("/css/main.(" + hash + ").css", hash);
	}

	@Test
	public void testAddNameSuffix() {
		String hash = RandomTestUtil.randomString(8);

		Assert.assertEquals(
			"/css/main_a_suffix.(" + hash + ").css",
			HashedFilesUtil.addNameSuffix(
				"/css/main.(" + hash + ").css", "_a_suffix"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNameSuffixWithIncorrectURI() {
		HashedFilesUtil.addNameSuffix("/css/main.css", "_a_suffix");
	}

	@Test
	public void testContainsHash() {
		String hash = RandomTestUtil.randomString(8);

		Assert.assertTrue(
			HashedFilesUtil.containsHash("/css/main.(" + hash + ").css"));

		Assert.assertFalse(HashedFilesUtil.containsHash("/css/main.css"));
	}

	@Test
	public void testGetHash() {
		String hash = RandomTestUtil.randomString(8);

		Assert.assertEquals(
			hash, HashedFilesUtil.getHash("/css/main.(" + hash + ").css"));

		Assert.assertNull(HashedFilesUtil.getHash("/css/main.css"));
	}

	@Test
	public void testRemoveHash() {
		String hash = RandomTestUtil.randomString(8);

		Assert.assertEquals(
			"/css/main.css",
			HashedFilesUtil.removeHash("/css/main.(" + hash + ").css"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveHashWithIncorrectURI() {
		HashedFilesUtil.removeHash("/css/main.css");
	}

}