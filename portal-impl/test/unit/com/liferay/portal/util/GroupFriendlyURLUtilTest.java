/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.documentlibrary.constants.DLFriendlyURLConstants;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Dante Wang
 */
public class GroupFriendlyURLUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testParseGroupFriendlyURLWithDocumentPathPrefix() {
		String groupURL1 = _getGroupURL();
		String groupURL2 = _getGroupURL();

		Assert.assertEquals(
			groupURL2,
			GroupFriendlyURLUtil.parseGroupFriendlyURL(
				StringBundler.concat(
					groupURL1, DLFriendlyURLConstants.PATH_PREFIX_DOCUMENT,
					groupURL2.substring(1), StringPool.SLASH,
					RandomTestUtil.randomString())));
	}

	@Test
	public void testParseGroupFriendlyURLWithLayoutSuffix() {
		String groupURL = _getGroupURL();

		Assert.assertEquals(
			groupURL,
			GroupFriendlyURLUtil.parseGroupFriendlyURL(
				StringBundler.concat(
					groupURL, StringPool.SLASH,
					RandomTestUtil.randomString())));
	}

	@Test
	public void testParseGroupFriendlyURLWithRoot() {
		Assert.assertNull(
			GroupFriendlyURLUtil.parseGroupFriendlyURL(StringPool.SLASH));
	}

	@Test
	public void testParseGroupFriendlyURLWithoutSuffix() {
		String groupURL = _getGroupURL();

		Assert.assertEquals(
			groupURL, GroupFriendlyURLUtil.parseGroupFriendlyURL(groupURL));
	}

	private String _getGroupURL() {
		return StringPool.SLASH + RandomTestUtil.randomString();
	}

}